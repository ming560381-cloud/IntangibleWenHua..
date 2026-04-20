<template>
  <section class="page-section">
    <div class="container">
      <div v-if="loading" class="loading-state section-card">正在加载项目详情...</div>
      <div v-else-if="!heritage" class="empty-state section-card">未找到该非遗项目。</div>
      <div v-else class="detail-layout">
        <article class="detail-panel">
          <img class="detail-cover" :src="resolveImage(heritage.images, heritage.name, '#9c4c32', '#f7e4d2')" :alt="heritage.name" />
          <div class="meta-row">
            <span class="badge-chip">{{ categoryName }}</span>
            <span class="meta-chip">{{ levelName(heritage.level) }}</span>
            <span v-if="master" class="meta-chip">传承人：{{ master.name }}</span>
          </div>
          <h1>{{ heritage.name }}</h1>
          <p>{{ heritage.description }}</p>
          <div v-if="isAdmin" class="inline-actions" style="margin-bottom: 8px;">
            <button class="ghost-button" @click="openEditHeritageModal">编辑项目</button>
            <button class="ghost-button" @click="deleteHeritage">删除项目</button>
          </div>

          <div class="detail-panel" style="margin-top: 20px; box-shadow: none;">
            <h3>历史背景</h3>
            <p>{{ heritage.history || '暂无相关介绍。' }}</p>
          </div>

          <div class="detail-panel" style="margin-top: 20px; box-shadow: none;">
            <h3>技艺流程</h3>
            <p>{{ heritage.process || '暂无流程说明。' }}</p>
          </div>

          <div class="detail-panel" style="margin-top: 20px; box-shadow: none;">
            <h3>主要材料</h3>
            <p>{{ heritage.materials || '暂无材料信息。' }}</p>
          </div>

          <div class="detail-panel video-panel" style="margin-top: 20px; box-shadow: none;">
            <div class="video-panel-head">
              <div>
                <h3>相关视频</h3>
                <p class="muted" style="margin: 8px 0 0;">支持在线播放，管理员可在此上传或替换项目主视频。</p>
              </div>
              <a v-if="heritage.videoUrl" class="ghost-button" :href="heritage.videoUrl" target="_blank" rel="noreferrer">打开视频</a>
            </div>

            <div v-if="heritage.videoUrl" class="video-player-shell">
              <video class="heritage-video-player" :src="heritage.videoUrl" controls preload="metadata">
                当前浏览器不支持视频播放，请使用“打开视频”按钮查看。
              </video>
            </div>
            <p v-else class="muted">当前项目暂未上传相关视频。</p>

            <div v-if="isAdmin" class="video-upload-panel">
              <div class="video-upload-copy">
                <strong>管理员视频上传</strong>
                <p class="muted">仅管理员可上传视频，上传成功后会自动写入当前项目并立即生效。</p>
              </div>
              <input
                ref="fileInputRef"
                class="field"
                type="file"
                accept="video/*"
                @change="handleFileChange"
              />
              <div class="inline-actions">
                <button class="solid-button" type="button" :disabled="uploading || !selectedVideo" @click="uploadVideo">
                  {{ uploading ? '上传中...' : '上传并替换视频' }}
                </button>
              </div>
              <p v-if="uploadMessage" :class="uploadError ? 'video-message is-error' : 'video-message'">{{ uploadMessage }}</p>
            </div>
          </div>
        </article>

        <aside class="sticky-side list-stack">
          <article v-if="master" class="detail-panel">
            <h3>代表性传承人</h3>
            <img class="detail-cover" :src="resolveImage(master.image, master.name, '#366b5b', '#deefe9')" :alt="master.name" style="aspect-ratio: 16/11;" />
            <p>{{ master.introduction }}</p>
            <RouterLink class="text-button" :to="`/master/${master.id}`">查看传承人详情</RouterLink>
          </article>

          <article class="detail-panel">
            <h3>相关活动</h3>
            <div v-if="relatedActivities.length" class="list-stack">
              <div v-for="activity in relatedActivities" :key="activity.id" class="info-card">
                <div class="info-body">
                  <div class="meta-row">
                    <span class="status-chip">{{ activityStatus(activity) }}</span>
                  </div>
                  <h3>{{ activity.title }}</h3>
                  <p>{{ excerpt(activity.summary, 88) }}</p>
                  <p class="muted">{{ formatDateTime(activity.startTime) }}</p>
                  <RouterLink class="text-button" :to="`/activities?focus=${activity.id}`">查看并报名</RouterLink>
                </div>
              </div>
            </div>
            <p v-else class="muted">当前项目暂无关联活动，可先在社区留言提问。</p>
          </article>
        </aside>
      </div>

      <div v-if="showHeritageModal" class="admin-modal-mask" @click="closeHeritageModal">
        <div class="admin-modal-card" @click.stop>
          <div class="admin-modal-head">
            <h3>编辑非遗项目</h3>
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
              {{ savingHeritage ? '提交中...' : (editingHeritageId ? '保存修改' : '创建项目') }}
            </button>
            <button class="ghost-button" :disabled="savingHeritage" @click="closeHeritageModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import {
  activityApi,
  activityStatus,
  categoryApi,
  excerpt,
  formatDateTime,
  heritageApi,
  masterApi,
  resolveImage,
  userApi
} from '../services/api';
import { isAdminUser, readSessionUser, sessionEventName } from '../utils/session';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const heritage = ref(null);
const categories = ref([]);
const masters = ref([]);
const activities = ref([]);
const currentUser = ref(readSessionUser());
const selectedVideo = ref(null);
const uploadMessage = ref('');
const uploadError = ref(false);
const uploading = ref(false);
const fileInputRef = ref(null);
const showHeritageModal = ref(false);
const savingHeritage = ref(false);
const editingHeritageId = ref(null);
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
    const heritageId = route.params.id;
    const [heritageRes, categoryRes, masterRes, activityRes] = await Promise.all([
      heritageApi.getDetail(heritageId),
      categoryApi.getList(),
      masterApi.getList(),
      activityApi.getList()
    ]);
    heritage.value = heritageRes.data;
    categories.value = categoryRes.data || [];
    masters.value = masterRes.data || [];
    activities.value = activityRes.data || [];
  } catch (error) {
    console.error('加载非遗详情失败', error);
    heritage.value = null;
  } finally {
    loading.value = false;
  }
};

const syncCurrentUser = async () => {
  currentUser.value = readSessionUser();
  try {
    const response = await userApi.check();
    if (response.data?.authenticated && response.data?.user) {
      currentUser.value = response.data.user;
    }
  } catch (error) {
    console.error('同步登录状态失败', error);
  }
};

const handleFileChange = (event) => {
  const [file] = event.target.files || [];
  selectedVideo.value = file || null;
  uploadError.value = false;
  uploadMessage.value = selectedVideo.value ? `已选择：${selectedVideo.value.name}` : '';
};

const uploadVideo = async () => {
  if (!heritage.value?.id || !selectedVideo.value) {
    uploadError.value = true;
    uploadMessage.value = '请先选择要上传的视频文件。';
    return;
  }

  uploading.value = true;
  uploadError.value = false;
  uploadMessage.value = '';
  try {
    const response = await heritageApi.uploadVideo(heritage.value.id, selectedVideo.value);
    if (response.data?.videoUrl) {
      heritage.value.videoUrl = response.data.videoUrl;
    }
    uploadMessage.value = response.data?.message || '视频上传成功。';
    selectedVideo.value = null;
    if (fileInputRef.value) {
      fileInputRef.value.value = '';
    }
  } catch (error) {
    uploadError.value = true;
    uploadMessage.value = typeof error?.response?.data === 'string'
      ? error.response.data
      : error?.response?.data?.message || '视频上传失败，请稍后再试。';
  } finally {
    uploading.value = false;
  }
};

const openEditHeritageModal = () => {
  if (!heritage.value?.id) return;
  editingHeritageId.value = heritage.value.id;
  heritageForm.value = {
    name: heritage.value.name || '',
    description: heritage.value.description || '',
    history: heritage.value.history || '',
    process: heritage.value.process || '',
    materials: heritage.value.materials || '',
    images: heritage.value.images || '',
    level: heritage.value.level ?? 1,
    categoryId: heritage.value.categoryId ?? null,
    masterId: heritage.value.masterId ?? null
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
  if (!editingHeritageId.value) {
    window.alert('当前仅支持编辑已有项目，请返回非遗资源页创建新项目。');
    return;
  }
  savingHeritage.value = true;
  const payload = {
    ...heritageForm.value,
    level: heritageForm.value.level ? Number(heritageForm.value.level) : null,
    categoryId: heritageForm.value.categoryId ? Number(heritageForm.value.categoryId) : null,
    masterId: heritageForm.value.masterId ? Number(heritageForm.value.masterId) : null
  };
  try {
    await heritageApi.update(editingHeritageId.value, { ...heritage.value, ...payload });
    showHeritageModal.value = false;
    await fetchData();
    window.alert('项目更新成功');
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '更新失败，请检查管理员权限');
  } finally {
    savingHeritage.value = false;
  }
};

const deleteHeritage = async () => {
  if (!heritage.value?.id) return;
  const confirmed = window.confirm(`确认删除非遗项目「${heritage.value.name}」吗？`);
  if (!confirmed) return;
  try {
    await heritageApi.delete(heritage.value.id);
    window.alert('项目已删除');
    router.push('/heritage');
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '删除失败，请检查管理员权限');
  }
};

const master = computed(() => masters.value.find((item) => item.id === heritage.value?.masterId) || null);
const categoryName = computed(() => categories.value.find((item) => item.id === heritage.value?.categoryId)?.name || '未分类');
const relatedActivities = computed(() => activities.value.filter((item) => item.heritageId === heritage.value?.id));
const isAdmin = computed(() => isAdminUser(currentUser.value));
const levelName = (level) => ({ 1: '国家级', 2: '省级', 3: '市级' }[level] || '特色项目');

watch(() => route.params.id, fetchData, { immediate: true });

onMounted(() => {
  window.addEventListener(sessionEventName(), syncCurrentUser);
  syncCurrentUser();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>
