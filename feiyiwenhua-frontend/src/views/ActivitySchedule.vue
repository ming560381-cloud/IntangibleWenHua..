<template>
  <section class="page-section">
    <div class="container">
      <!-- 顶部图片横幅 -->
      <div class="page-hero-banner">
        <div class="hero-image-wrapper">
          <div class="hero-overlay"></div>
          <div class="hero-content">
            <div class="hero-text">
              <h1 class="hero-title">非遗展览、培训与体验活动</h1>
              <p class="hero-subtitle">发现并参与最新的非遗文化活动，点击活动查看详情并报名。</p>
            </div>
            <div class="hero-stats">
              <div class="stat-item">
                <span class="stat-number">30+</span>
                <span class="stat-label">活动项目</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">500+</span>
                <span class="stat-label">参与人次</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">20+</span>
                <span class="stat-label">非遗传承人</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="activity-layout">
        <div v-if="isAdmin" class="admin-toolbar">
          <button class="solid-button" @click="openCreateActivityModal">+ 新建活动</button>
        </div>
        <div class="activity-list">
          <article class="detail-panel">
            <div class="filter-bar" style="grid-template-columns: minmax(0, 1fr) 180px auto;">
              <input v-model.trim="keyword" class="field" type="text" placeholder="搜索活动主题、地点或简介" />
              <select v-model="typeFilter" class="select">
                <option value="">全部类型</option>
                <option v-for="type in types" :key="type" :value="type">{{ type }}</option>
              </select>
              <button class="ghost-button" @click="resetFilters">重置筛选</button>
            </div>
          </article>

          <div v-if="loading" class="loading-state section-card">正在加载活动信息...</div>
          <div v-else-if="filteredActivities.length === 0" class="empty-state section-card">当前没有符合条件的活动。</div>
          <div v-else class="activity-timeline">
            <article v-for="activity in filteredActivities" :key="activity.id" class="activity-entry">
              <div class="activity-image-section">
                <img v-if="activityCover(activity)" :src="activityCover(activity)" :alt="activity.title" class="activity-main-image" />
                <div v-else class="activity-image-placeholder">
                  <div class="placeholder-icons">
                    <span class="placeholder-icon">🏮</span>
                    <span class="placeholder-icon">🎭</span>
                    <span class="placeholder-icon">🖼️</span>
                  </div>
                  <span class="placeholder-text">非遗文化活动</span>
                </div>
                <div class="activity-date">
                  <span class="date-month">{{ monthDay(activity.startTime).month }}</span>
                  <strong class="date-day">{{ monthDay(activity.startTime).day }}</strong>
                </div>
              </div>
              <div class="activity-content-section">
                <div class="activity-meta">
                  <span class="activity-status">{{ activityStatus(activity) }}</span>
                  <span class="activity-type">{{ activity.type || '活动' }}</span>
                  <span class="activity-capacity">已报名 {{ activity.signupCount || 0 }} / {{ activity.capacity || '不限' }}</span>
                </div>
                <h3 class="activity-title">{{ activity.title }}</h3>
                <p class="activity-summary">{{ activity.summary }}</p>
                <div class="activity-details">
                  <div class="detail-item">
                    <span class="detail-label">时间：</span>
                    <span class="detail-value">{{ formatDateTime(activity.startTime) }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">地点：</span>
                    <span class="detail-value">{{ activity.location || '待定' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">主办：</span>
                    <span class="detail-value">{{ activity.organizer || '待定' }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="detail-label">费用：</span>
                    <span class="detail-value">{{ activity.fee || '免费' }}</span>
                  </div>
                </div>
                <div class="activity-actions">
                  <button
                    class="activity-button"
                    :class="{ 'is-view-only': isRegistrationClosed(activity) }"
                    @click="focusActivity(activity)"
                  >
                    {{ isRegistrationClosed(activity) ? '查看详情' : '查看详情并报名' }}
                  </button>
                  <button v-if="isAdmin" class="ghost-button" style="min-height: 40px;" @click="openEditActivityModal(activity)">编辑</button>
                  <button v-if="isAdmin" class="ghost-button" style="min-height: 40px;" @click="removeActivity(activity)">删除</button>
                </div>
              </div>
            </article>
          </div>
        </div>
      </div>

      <div v-if="showActivityModal" class="admin-modal-mask" @click="closeActivityModal">
        <div class="admin-modal-card" @click.stop>
          <div class="admin-modal-head">
            <h3>{{ editingActivityId ? '编辑活动' : '新建活动' }}</h3>
            <button class="ghost-button" @click="closeActivityModal">关闭</button>
          </div>
          <div class="admin-modal-form-grid">
            <input v-model.trim="activityForm.title" class="field" type="text" placeholder="活动标题" />
            <input v-model.trim="activityForm.type" class="field" type="text" placeholder="活动类型" />
            <input v-model="activityForm.startTime" class="field" type="datetime-local" />
            <input v-model="activityForm.endTime" class="field" type="datetime-local" />
            <input v-model.number="activityForm.capacity" class="field" type="number" placeholder="人数上限（可空）" />
            <select v-model="activityForm.heritageId" class="select">
              <option :value="null">关联非遗（可空）</option>
              <option v-for="item in heritages" :key="item.id" :value="item.id">{{ item.name }}</option>
            </select>
            <input v-model.trim="activityForm.location" class="field" type="text" placeholder="活动地点" />
            <input v-model.trim="activityForm.organizer" class="field" type="text" placeholder="主办方" />
            <input v-model.trim="activityForm.contact" class="field" type="text" placeholder="联系方式" />
            <input v-model.number="activityForm.status" class="field" type="number" placeholder="状态（1发布）" />
          </div>
          <textarea v-model.trim="activityForm.summary" class="textarea" placeholder="活动摘要"></textarea>
          <textarea v-model.trim="activityForm.description" class="textarea" placeholder="活动详情"></textarea>
          <textarea v-model.trim="activityForm.images" class="textarea" placeholder="活动图片URL（多张逗号分隔）"></textarea>
          <div class="inline-actions" style="margin-top: 12px;">
            <button class="solid-button" :disabled="savingActivity" @click="submitActivityForm">
              {{ savingActivity ? '提交中...' : (editingActivityId ? '保存修改' : '创建活动') }}
            </button>
            <button class="ghost-button" :disabled="savingActivity" @click="closeActivityModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  activityApi,
  activityStatus,
  formatDateTime,
  normalizeImageUrl,
  heritageApi,
  userApi
} from '../services/api';
import { isAdminUser, readSessionUser, sessionEventName } from '../utils/session';
const router = useRouter();
const loading = ref(true);
const activities = ref([]);
const keyword = ref('');
const typeFilter = ref('');
const currentUser = ref(readSessionUser());
const isAdmin = computed(() => isAdminUser(currentUser.value));
const heritages = ref([]);
const showActivityModal = ref(false);
const savingActivity = ref(false);
const editingActivityId = ref(null);
const activityForm = ref({
  title: '',
  summary: '',
  description: '',
  images: '',
  location: '',
  organizer: '',
  contact: '',
  type: '',
  startTime: '',
  endTime: '',
  capacity: null,
  status: 1,
  heritageId: null
});

const fetchActivities = async () => {
  loading.value = true;
  try {
    const [activityRes, heritageRes] = await Promise.all([
      activityApi.getList(),
      heritageApi.getList()
    ]);
    activities.value = activityRes.data || [];
    heritages.value = heritageRes.data || [];
  } catch (error) {
    console.error('活动数据加载失败', error);
  } finally {
    loading.value = false;
  }
};

const types = computed(() => [...new Set(activities.value.map((item) => item.type).filter(Boolean))]);
const filteredActivities = computed(() => activities.value.filter((activity) => {
  const matchesKeyword = !keyword.value || [activity.title, activity.summary, activity.location]
    .filter(Boolean)
    .some((text) => text.includes(keyword.value));
  const matchesType = !typeFilter.value || activity.type === typeFilter.value;
  return matchesKeyword && matchesType;
}));

const activityCover = (activity) => {
  const firstImage = activity?.images ? String(activity.images).split(',')[0].trim() : '';
  return normalizeImageUrl(firstImage);
};

const heroStyle = computed(() => {
  return {
    backgroundImage: 'linear-gradient(rgba(38, 24, 16, 0.36), rgba(38, 24, 16, 0.62))'
  };
});

const focusActivity = (activity) => {
  // 跳转到活动详情和报名页面
  router.push({
    path: '/activity-detail',
    query: {
      id: String(activity.id)
    }
  });
};

const toDateTimeInput = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  const pad = (num) => String(num).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

const normalizeDateTime = (value) => (value ? `${value}:00` : null);

const openCreateActivityModal = () => {
  editingActivityId.value = null;
  activityForm.value = {
    title: '',
    summary: '',
    description: '',
    images: '',
    location: '',
    organizer: '',
    contact: '',
    type: '体验活动',
    startTime: '',
    endTime: '',
    capacity: null,
    status: 1,
    heritageId: null
  };
  showActivityModal.value = true;
};

const openEditActivityModal = (activity) => {
  editingActivityId.value = activity.id;
  activityForm.value = {
    title: activity.title || '',
    summary: activity.summary || '',
    description: activity.description || '',
    images: activity.images || '',
    location: activity.location || '',
    organizer: activity.organizer || '',
    contact: activity.contact || '',
    type: activity.type || '',
    startTime: toDateTimeInput(activity.startTime),
    endTime: toDateTimeInput(activity.endTime),
    capacity: activity.capacity ?? null,
    status: activity.status ?? 1,
    heritageId: activity.heritageId ?? null
  };
  showActivityModal.value = true;
};

const closeActivityModal = () => {
  if (savingActivity.value) return;
  showActivityModal.value = false;
};

const submitActivityForm = async () => {
  if (!activityForm.value.title) {
    window.alert('请填写活动标题');
    return;
  }
  savingActivity.value = true;
  try {
    const payload = {
      ...activityForm.value,
      startTime: normalizeDateTime(activityForm.value.startTime),
      endTime: normalizeDateTime(activityForm.value.endTime),
      capacity: activityForm.value.capacity || null,
      heritageId: activityForm.value.heritageId || null
    };
    if (editingActivityId.value) {
      await activityApi.update(editingActivityId.value, payload);
    } else {
      await activityApi.create(payload);
    }
    showActivityModal.value = false;
    await fetchActivities();
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '提交失败，请检查管理员权限');
  } finally {
    savingActivity.value = false;
  }
};

const removeActivity = async (activity) => {
  const confirmed = window.confirm(`确认删除活动「${activity.title}」吗？`);
  if (!confirmed) return;
  try {
    await activityApi.delete(activity.id);
    await fetchActivities();
    window.alert('活动已删除');
  } catch (error) {
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '删除失败，请检查管理员权限');
  }
};

const resetFilters = () => {
  keyword.value = '';
  typeFilter.value = '';
};

const monthDay = (value) => {
  if (!value) return { month: '--', day: '--' };
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return { month: '--', day: '--' };
  return {
    month: `${date.getMonth() + 1}月`,
    day: `${date.getDate()}`
  };
};

const isRegistrationClosed = (activity) => {
  if (!activity?.startTime) return false;
  const start = new Date(activity.startTime);
  if (Number.isNaN(start.getTime())) return false;
  const now = new Date();
  const startDate = new Date(start.getFullYear(), start.getMonth(), start.getDate());
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  return startDate <= today;
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
  fetchActivities();
  window.addEventListener(sessionEventName(), syncCurrentUser);
  syncCurrentUser();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>
