<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner master-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Masters</span>
        <h1>从人物档案进入，看见技艺如何被一代代守住。</h1>
        <p>这里聚焦代表性传承人的经历、成果和关联项目，让人物成为理解非遗的入口。</p>
      </section>

      <article class="detail-panel" style="margin-bottom: 24px;">
        <div class="filter-bar" style="grid-template-columns: minmax(0, 1fr) auto;">
          <input v-model.trim="keyword" class="field" type="text" placeholder="搜索传承人姓名、简介或成果" />
          <button class="ghost-button" @click="keyword = ''">清空搜索</button>
        </div>
        <div v-if="isAdmin" class="inline-actions" style="margin-top: 12px;">
          <button class="solid-button" @click="openCreateMasterModal">添加传承人</button>
        </div>
      </article>

      <div v-if="loading" class="loading-state section-card">正在加载传承人档案...</div>
      <div v-else-if="filteredMasters.length === 0" class="empty-state section-card">没有找到符合条件的传承人。</div>
      <div v-else class="roster-shell">
        <article v-if="featuredMaster" class="roster-feature roster-feature-spotlight">
          <img
            class="roster-portrait"
            :src="resolveImage(featuredMaster.image, featuredMaster.name, '#366b5b', '#deefe9')"
            :alt="featuredMaster.name"
          />
          <div class="roster-feature-copy">
            <div class="meta-row">
              <span class="badge-chip">每日推荐</span>
              <span class="meta-chip">关联项目 {{ relatedHeritages(featuredMaster.id).length }} 个</span>
              <span class="meta-chip">{{ todayLabel }}</span>
              <button class="ghost-button" type="button" style="min-height: 32px; padding: 4px 12px;" @click="shuffleFeaturedMaster">
                换一位
              </button>
            </div>
            <h2>{{ featuredMaster.name }}</h2>
            <p>{{ featuredMaster.introduction || '暂无人物简介。' }}</p>
            <p class="muted">{{ excerpt(featuredMaster.achievements, 128) || '成果信息正在整理中。' }}</p>
            <div v-if="isAdmin" class="inline-actions">
              <button class="ghost-button" @click="openEditMasterModal(featuredMaster)">编辑</button>
              <button class="ghost-button" @click="removeMaster(featuredMaster)">删除</button>
            </div>
            <RouterLink class="solid-button" :to="`/master/${featuredMaster.id}`">查看完整档案</RouterLink>
          </div>
        </article>

        <div class="roster-grid">
          <article v-for="master in listMasters" :key="master.id" class="resource-card master-card">
            <img class="resource-cover" :src="resolveImage(master.image, master.name, '#366b5b', '#deefe9')" :alt="master.name" />
            <div class="resource-body">
              <div class="meta-row">
                <span class="badge-chip">传承人</span>
                <span class="meta-chip">关联项目 {{ relatedHeritages(master.id).length }}</span>
              </div>
              <h3>{{ master.name }}</h3>
              <p>{{ excerpt(master.introduction, 98) }}</p>
              <div v-if="isAdmin" class="inline-actions" style="margin-bottom: 6px;">
                <button class="ghost-button" @click="openEditMasterModal(master)">编辑</button>
                <button class="ghost-button" @click="removeMaster(master)">删除</button>
              </div>
              <RouterLink class="text-button" :to="`/master/${master.id}`">查看详情</RouterLink>
            </div>
          </article>
        </div>
      </div>

      <div v-if="showMasterModal" class="admin-modal-mask" @click="closeMasterModal">
        <div class="admin-modal-card" @click.stop>
          <div class="admin-modal-head">
            <h3>{{ editingMasterId ? '编辑传承人' : '新建传承人' }}</h3>
            <button class="ghost-button" @click="closeMasterModal">关闭</button>
          </div>
          <div class="admin-modal-form-grid">
            <input v-model.trim="masterForm.name" class="field" type="text" placeholder="姓名" />
            <input v-model.trim="masterForm.gender" class="field" type="text" placeholder="性别" />
            <input v-model="masterForm.birthDate" class="field" type="datetime-local" />
            <input v-model.trim="masterForm.image" class="field" type="text" placeholder="头像图片URL" />
          </div>
          <textarea v-model.trim="masterForm.introduction" class="textarea" placeholder="人物简介"></textarea>
          <textarea v-model.trim="masterForm.achievements" class="textarea" placeholder="成果与经历"></textarea>
          <div class="inline-actions" style="margin-top: 12px;">
            <button class="solid-button" :disabled="savingMaster" @click="submitMasterForm">
              {{ savingMaster ? '提交中...' : (editingMasterId ? '保存修改' : '创建传承人') }}
            </button>
            <button class="ghost-button" :disabled="savingMaster" @click="closeMasterModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { excerpt, heritageApi, masterApi, resolveImage, userApi } from '../services/api';
import { isAdminUser, readSessionUser, sessionEventName } from '../utils/session';

const loading = ref(true);
const keyword = ref('');
const masters = ref([]);
const heritages = ref([]);
const currentUser = ref(readSessionUser());
const isAdmin = computed(() => isAdminUser(currentUser.value));
const showMasterModal = ref(false);
const editingMasterId = ref(null);
const savingMaster = ref(false);
const featuredOverrideIndex = ref(null);
const masterForm = ref({
  name: '',
  gender: '',
  birthDate: '',
  introduction: '',
  achievements: '',
  image: ''
});

const fetchData = async () => {
  loading.value = true;
  try {
    const [masterRes, heritageRes] = await Promise.all([
      masterApi.getList(),
      heritageApi.getList()
    ]);
    masters.value = masterRes.data || [];
    heritages.value = heritageRes.data || [];
  } catch (error) {
    console.error('传承人数据加载失败', error);
  } finally {
    loading.value = false;
  }
};

const filteredMasters = computed(() => masters.value.filter((item) => {
  if (!keyword.value) return true;
  return [item.name, item.introduction, item.achievements]
    .filter(Boolean)
    .some((text) => text.includes(keyword.value));
}));

const daySeed = () => {
  const now = new Date();
  return Number(`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`);
};

const pickDailyMasterIndex = (length) => {
  if (!length) return -1;
  return daySeed() % length;
};

const featuredMaster = computed(() => {
  const list = filteredMasters.value;
  if (!list.length) return null;
  if (featuredOverrideIndex.value !== null && list[featuredOverrideIndex.value]) {
    return list[featuredOverrideIndex.value];
  }
  return list[pickDailyMasterIndex(list.length)];
});

const listMasters = computed(() => {
  if (!featuredMaster.value) return filteredMasters.value;
  return filteredMasters.value.filter((item) => item.id !== featuredMaster.value.id);
});

const todayLabel = computed(() => {
  const now = new Date();
  return `${now.getMonth() + 1}月${now.getDate()}日`;
});

const shuffleFeaturedMaster = () => {
  const list = filteredMasters.value;
  if (list.length <= 1) return;
  const current = featuredMaster.value;
  const currentIndex = current ? list.findIndex((item) => item.id === current.id) : -1;
  const nextIndex = currentIndex >= 0 ? (currentIndex + 1) % list.length : 0;
  featuredOverrideIndex.value = nextIndex;
};
const relatedHeritages = (masterId) => heritages.value.filter((item) => item.masterId === masterId);

const toDateTimeInput = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  const pad = (num) => String(num).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

const normalizeDateTime = (value) => (value ? `${value}:00` : null);

const openCreateMasterModal = () => {
  editingMasterId.value = null;
  masterForm.value = {
    name: '',
    gender: '',
    birthDate: '',
    introduction: '',
    achievements: '',
    image: ''
  };
  showMasterModal.value = true;
};

const openEditMasterModal = (master) => {
  editingMasterId.value = master.id;
  masterForm.value = {
    name: master.name || '',
    gender: master.gender || '',
    birthDate: toDateTimeInput(master.birthDate),
    introduction: master.introduction || '',
    achievements: master.achievements || '',
    image: master.image || ''
  };
  showMasterModal.value = true;
};

const closeMasterModal = () => {
  if (savingMaster.value) return;
  showMasterModal.value = false;
};

const submitMasterForm = async () => {
  if (!masterForm.value.name) {
    window.alert('请填写传承人姓名');
    return;
  }
  savingMaster.value = true;
  const payload = {
    ...masterForm.value,
    birthDate: normalizeDateTime(masterForm.value.birthDate)
  };
  try {
    if (editingMasterId.value) {
      await masterApi.update(editingMasterId.value, payload);
    } else {
      await masterApi.create(payload);
    }
    showMasterModal.value = false;
    await fetchData();
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '提交失败，请检查管理员权限');
  } finally {
    savingMaster.value = false;
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

const removeMaster = async (master) => {
  const confirmed = window.confirm(`确认删除传承人「${master.name}」吗？`);
  if (!confirmed) return;
  try {
    await masterApi.delete(master.id);
    await fetchData();
    window.alert('传承人已删除');
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '删除失败，请检查管理员权限');
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
