<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner archive-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Resource Archive</span>
        <h1>像查阅馆藏一样，检索每一项非遗资源。</h1>
        <p>按照关键词、类别和级别筛选项目，并进一步查看历史背景、技艺流程与相关传承人。</p>
      </section>

      <div v-if="isAdmin" class="admin-toolbar" style="margin-bottom: 12px;">
        <button class="solid-button" @click="openCreateHeritageModal">+ 新建非遗项目</button>
      </div>

      <div class="archive-shell">
        <aside class="archive-sidebar sticky-side">
          <article class="detail-panel">
            <h3>资源筛选</h3>
            <div class="list-stack" style="margin-top: 14px;">
              <input v-model.trim="keyword" class="field" type="text" placeholder="输入项目名、简介或工艺关键词" />
              <select v-model="selectedCategory" class="select">
                <option value="">全部类别</option>
                <option v-for="category in categories" :key="category.id" :value="String(category.id)">{{ category.name }}</option>
              </select>
              <select v-model="selectedLevel" class="select">
                <option value="">全部级别</option>
                <option value="1">国家级</option>
                <option value="2">省级</option>
                <option value="3">市级</option>
              </select>
              <button class="ghost-button" @click="resetFilters">重置筛选</button>
            </div>
          </article>

          <article class="detail-panel">
            <h3>浏览提示</h3>
            <ul>
              <li>先按类别缩小范围，再根据关键词快速定位具体项目。</li>
              <li>进入详情页后可以继续查看技艺流程、历史背景和相关活动。</li>
              <li>如果想进一步提问，可以从详情页跳到社区或活动板块。</li>
            </ul>
          </article>
        </aside>

        <div class="archive-stream">
          <div v-if="loading" class="loading-state section-card">正在加载非遗资源...</div>
          <div v-else-if="filteredItems.length === 0" class="empty-state section-card">没有找到符合条件的非遗项目。</div>
          <div v-else class="archive-list">
            <article v-for="item in filteredItems" :key="item.id" class="archive-entry">
              <img class="archive-cover" :src="resolveImage(item.images, item.name, '#9c4c32', '#f7e4d2')" :alt="item.name" />
              <div class="archive-content">
                <div class="meta-row">
                  <span class="badge-chip">{{ categoryName(item.categoryId) }}</span>
                  <span class="meta-chip">{{ levelName(item.level) }}</span>
                  <span v-if="masterName(item.masterId)" class="meta-chip">传承人：{{ masterName(item.masterId) }}</span>
                </div>
                <h3>{{ item.name }}</h3>
                <p>{{ excerpt(item.description, 120) }}</p>
                <div class="inline-meta">
                  <span>历史背景 {{ item.history ? '已整理' : '待补充' }}</span>
                  <span>技艺流程 {{ item.process ? '可查看' : '待补充' }}</span>
                </div>
                <RouterLink class="text-button" :to="`/heritage/${item.id}`">查看项目详情</RouterLink>
              </div>
            </article>
          </div>
        </div>
      </div>

      <div v-if="showHeritageModal" class="admin-modal-mask" @click="closeHeritageModal">
        <div class="admin-modal-card" @click.stop>
          <div class="admin-modal-head">
            <h3>新建非遗项目</h3>
            <button class="ghost-button" @click="closeHeritageModal">关闭</button>
          </div>
          <div class="admin-modal-form-grid">
            <input v-model.trim="heritageForm.name" class="field" type="text" placeholder="项目名称" />
            <select v-model.number="heritageForm.level" class="select">
              <option :value="1">国家级</option>
              <option :value="2">省级</option>
              <option :value="3">市级</option>
            </select>
            <select v-model="heritageForm.categoryId" class="select">
              <option :value="null">分类（可空）</option>
              <option v-for="item in categories" :key="item.id" :value="item.id">{{ item.name }}</option>
            </select>
            <select v-model="heritageForm.masterId" class="select">
              <option :value="null">传承人（可空）</option>
              <option v-for="item in masters" :key="item.id" :value="item.id">{{ item.name }}</option>
            </select>
          </div>
          <textarea v-model.trim="heritageForm.description" class="textarea" placeholder="项目简介"></textarea>
          <textarea v-model.trim="heritageForm.history" class="textarea" placeholder="历史背景"></textarea>
          <textarea v-model.trim="heritageForm.process" class="textarea" placeholder="技艺流程"></textarea>
          <textarea v-model.trim="heritageForm.materials" class="textarea" placeholder="主要材料"></textarea>
          <textarea v-model.trim="heritageForm.images" class="textarea" placeholder="图片URL（多张逗号分隔）"></textarea>
          <div class="inline-actions" style="margin-top: 12px;">
            <button class="solid-button" :disabled="savingHeritage" @click="submitHeritageForm">
              {{ savingHeritage ? '提交中...' : '创建项目' }}
            </button>
            <button class="ghost-button" :disabled="savingHeritage" @click="closeHeritageModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { categoryApi, excerpt, heritageApi, masterApi, resolveImage, userApi } from '../services/api';
import { isAdminUser, readSessionUser, sessionEventName } from '../utils/session';

const loading = ref(true);
const heritages = ref([]);
const categories = ref([]);
const masters = ref([]);
const keyword = ref('');
const selectedCategory = ref('');
const selectedLevel = ref('');
const currentUser = ref(readSessionUser());
const isAdmin = computed(() => isAdminUser(currentUser.value));
const showHeritageModal = ref(false);
const savingHeritage = ref(false);
const heritageForm = ref({
  name: '',
  description: '',
  history: '',
  process: '',
  materials: '',
  images: '',
  level: 1,
  categoryId: null,
  masterId: null
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [heritageRes, categoryRes, masterRes] = await Promise.all([
      heritageApi.getList(),
      categoryApi.getList(),
      masterApi.getList()
    ]);
    heritages.value = heritageRes.data || [];
    categories.value = categoryRes.data || [];
    masters.value = masterRes.data || [];
  } catch (error) {
    console.error('加载非遗资源失败', error);
  } finally {
    loading.value = false;
  }
};

const filteredItems = computed(() => heritages.value.filter((item) => {
  const matchesKeyword = !keyword.value || [item.name, item.description, item.history, item.process]
    .filter(Boolean)
    .some((text) => text.includes(keyword.value));
  const matchesCategory = !selectedCategory.value || String(item.categoryId) === selectedCategory.value;
  const matchesLevel = !selectedLevel.value || String(item.level) === selectedLevel.value;
  return matchesKeyword && matchesCategory && matchesLevel;
}));

const categoryName = (categoryId) => categories.value.find((item) => item.id === categoryId)?.name || '未分类';
const masterName = (masterId) => masters.value.find((item) => item.id === masterId)?.name || '';
const levelName = (level) => ({ 1: '国家级', 2: '省级', 3: '市级' }[level] || '特色项目');

const resetFilters = () => {
  keyword.value = '';
  selectedCategory.value = '';
  selectedLevel.value = '';
};

const openCreateHeritageModal = () => {
  heritageForm.value = {
    name: '',
    description: '',
    history: '',
    process: '',
    materials: '',
    images: '',
    level: 1,
    categoryId: null,
    masterId: null
  };
  showHeritageModal.value = true;
};

const closeHeritageModal = () => {
  if (savingHeritage.value) return;
  showHeritageModal.value = false;
};

const submitHeritageForm = async () => {
  if (!heritageForm.value.name) {
    window.alert('请填写项目名称');
    return;
  }
  savingHeritage.value = true;
  try {
    const response = await heritageApi.create({
      ...heritageForm.value,
      level: heritageForm.value.level ? Number(heritageForm.value.level) : null,
      categoryId: heritageForm.value.categoryId ? Number(heritageForm.value.categoryId) : null,
      masterId: heritageForm.value.masterId ? Number(heritageForm.value.masterId) : null
    });
    showHeritageModal.value = false;
    await fetchData();
    if (response.data?.id) {
      window.alert('项目创建成功，已可在列表查看');
    }
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '创建失败，请检查管理员权限');
  } finally {
    savingHeritage.value = false;
  }
};

const syncCurrentUser = async () => {
  currentUser.value = readSessionUser();
  try {
    const response = await userApi.check();
    if (response.data?.authenticated && response.data?.user) {
      currentUser.value = response.data.user;
    }
  } catch {
    // ignore
  }
};

onMounted(() => {
  fetchData();
  window.addEventListener(sessionEventName(), syncCurrentUser);
  syncCurrentUser();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>
