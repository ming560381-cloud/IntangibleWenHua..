<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner activity-banner">
        <span class="section-eyebrow">Event Details</span>
        <h1>{{ activity?.title || '活动详情' }}</h1>
        <p>
          查看活动详情并完成报名
        </p>
      </section>

      <div v-if="loading" class="loading-state section-card">正在加载活动信息...</div>
      <div v-else-if="!activity" class="empty-state section-card">活动不存在或已被删除。</div>
      <div v-else class="activity-detail">
        <div class="activity-info">
          <div class="activity-cover-gallery">
            <img class="activity-cover" :src="activityCoverImage()" :alt="activity.title" />
            <template v-if="activity.images">
              <div class="activity-gallery">
                <img v-for="(image, index) in splitImages(activity.images)" :key="index" :src="image" :alt="`${activity.title}的图片 ${index + 1}`" class="gallery-image" />
              </div>
            </template>
          </div>
          <div class="activity-content">
            <div class="meta-row">
              <span class="status-chip">{{ activityStatus(activity) }}</span>
              <span class="meta-chip">{{ activity.type || '活动' }}</span>
              <span class="meta-chip">已报名 {{ activity.signupCount || 0 }} / {{ activity.capacity || '不限' }}</span>
            </div>
            <h2>{{ activity.title }}</h2>
            <p class="activity-description">{{ activity.description || activity.summary }}</p>
            <div class="activity-meta">
              <div class="meta-item">
                <span class="meta-label">时间：</span>
                <span class="meta-value">{{ formatDateTime(activity.startTime) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">地点：</span>
                <span class="meta-value">{{ activity.location || '待定' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">主办：</span>
                <span class="meta-value">{{ activity.organizer || '待定' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">费用：</span>
                <span class="meta-value">{{ activity.fee || '免费' }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="activity-registration">
          <h3>在线报名</h3>
          <div class="form-panel">
            <input v-model.trim="form.participantName" class="field" type="text" placeholder="报名人姓名" />
            <input v-model.trim="form.phone" class="field" type="text" placeholder="联系电话" />
            <textarea v-model.trim="form.message" class="textarea" placeholder="可填写参与诉求、问题或备注"></textarea>
            <div class="inline-actions">
              <button class="solid-button" :disabled="registrationClosed" @click="submitRegistration">
                {{ registrationClosed ? '报名已截止' : '提交报名' }}
              </button>
            </div>
            <p v-if="message" class="muted">{{ message }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  activityApi,
  activityStatus,
  formatDateTime,
  normalizeImageUrl
} from '../services/api';
import { readSessionUser, sessionEventName } from '../utils/session';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const activity = ref(null);
const currentUser = ref(readSessionUser());
const message = ref('');
const form = ref({ participantName: '', phone: '', message: '' });

const splitImages = (images) => (images || '').split(',').map((item) => item.trim()).filter(Boolean);
const activityCoverImage = () => {
  const firstImage = activity.value?.images ? String(activity.value.images).split(',')[0].trim() : '';
  return normalizeImageUrl(firstImage);
};
const registrationClosed = computed(() => {
  if (!activity.value?.startTime) return false;
  const start = new Date(activity.value.startTime);
  if (Number.isNaN(start.getTime())) return false;
  const now = new Date();
  const startDate = new Date(start.getFullYear(), start.getMonth(), start.getDate());
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  return startDate <= today;
});

const fetchActivityDetail = async () => {
  const activityId = Number(route.query.id);
  if (!Number.isNaN(activityId) && activityId) {
    loading.value = true;
    try {
      const response = await activityApi.getDetail(activityId);
      activity.value = response.data;
      fillForm();
    } catch (error) {
      console.error('活动详情加载失败', error);
      message.value = '活动详情加载失败，请稍后再试。';
    } finally {
      loading.value = false;
    }
  } else {
    loading.value = false;
  }
};

const fillForm = () => {
  form.value.participantName = currentUser.value?.username || '';
  form.value.phone = currentUser.value?.phone || '';
  form.value.message = '';
};

const submitRegistration = async () => {
  if (registrationClosed.value) {
    message.value = '该活动报名已截止，仅可查看详情。';
    return;
  }
  if (!currentUser.value) {
    message.value = '请先登录后再报名。';
    return;
  }
  if (!activity.value || !form.value.participantName || !form.value.phone) {
    message.value = '请填写姓名和联系电话。';
    return;
  }
  const confirmed = window.confirm('确认提交报名吗？提交后可在“我的-已报名”中退订。');
  if (!confirmed) {
    return;
  }
  try {
    await activityApi.register(activity.value.id, {
      userId: currentUser.value.id,
      participantName: form.value.participantName,
      phone: form.value.phone,
      message: form.value.message
    });
    activity.value.signupCount = (activity.value.signupCount || 0) + 1;
    message.value = '报名成功，工作人员会根据联系方式与您确认。';
    form.value.message = '';
  } catch (error) {
    console.error('报名失败', error);
    message.value = typeof error?.response?.data === 'string' ? error.response.data : '报名失败，请稍后再试。';
  }
};

const syncCurrentUser = () => {
  currentUser.value = readSessionUser();
  fillForm();
};

onMounted(() => {
  window.addEventListener(sessionEventName(), syncCurrentUser);
  fetchActivityDetail();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>

<style scoped>
.activity-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-banner {
  margin: 14px 0 26px;
}

.activity-info {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.activity-cover-gallery {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-cover {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.activity-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 6px;
}

.gallery-image {
  width: 100%;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.gallery-image:hover {
  transform: scale(1.02);
}

.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-content h2 {
  margin: 0;
  font-size: 30px;
  line-height: 1.3;
  color: #2f241d;
}

.activity-description {
  margin: 0;
  line-height: 1.8;
  font-size: 16px;
  color: var(--text);
}

.activity-meta {
  margin-top: 4px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 18px;
}

.meta-item {
  display: flex;
  align-items: baseline;
  gap: 10px;
  min-height: 28px;
}

.meta-label {
  font-weight: 600;
  color: var(--text-muted);
  min-width: 52px;
  text-align: right;
  line-height: 1.6;
}

.meta-value {
  color: var(--text);
  line-height: 1.6;
}

.activity-registration {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

.activity-registration h3 {
  margin-top: 0;
  margin-bottom: 16px;
  color: var(--text);
  font-size: 18px;
  font-weight: 600;
}

.form-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.inline-actions {
  margin-top: 8px;
}

@media (max-width: 768px) {
  .activity-info {
    flex-direction: column;
  }
  
  .activity-cover {
    width: 100%;
    height: 200px;
  }

  .activity-content h2 {
    font-size: 24px;
  }

  .activity-meta {
    grid-template-columns: 1fr;
  }
}
</style>