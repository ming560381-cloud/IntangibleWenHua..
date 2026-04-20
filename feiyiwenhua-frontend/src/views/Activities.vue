<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner activity-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Events & Signup</span>
        <h1>把展览、培训与体验活动，整理成一张真正可参与的日程表。</h1>
        <p>
          这里集中发布非遗展演、体验课、导赏和工作坊。你可以按类型筛选，也可以直接聚焦某一场活动并完成报名。
        </p>
      </section>

      <div class="activity-layout">
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
          <article v-else v-for="activity in filteredActivities" :key="activity.id" class="resource-card">
            <img class="resource-cover" :src="resolveImage(activity.coverImage || (activity.images ? activity.images.split(',')[0].trim() : ''), activity.title, '#c1732e', '#f6ead0')" :alt="activity.title" />
            <div class="resource-body">
              <div class="meta-row">
                <span class="status-chip">{{ activityStatus(activity) }}</span>
                <span class="meta-chip">{{ activity.type || '活动' }}</span>
                <span class="meta-chip">已报名 {{ activity.signupCount || 0 }} / {{ activity.capacity || '不限' }}</span>
              </div>
              <h3>{{ activity.title }}</h3>
              <p>{{ activity.summary }}</p>
              <div class="post-meta">
                <span>时间：{{ formatDateTime(activity.startTime) }}</span>
                <span>地点：{{ activity.location || '待定' }}</span>
                <span>主办：{{ activity.organizer || '待定' }}</span>
              </div>
              <div class="inline-actions" style="margin-top: 14px;">
                <button class="solid-button" @click="focusActivity(activity)">查看并报名</button>
              </div>
            </div>
          </article>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  activityApi,
  activityStatus,
  formatDateTime,
  resolveImage
} from '../services/api';
import { readSessionUser, sessionEventName } from '../utils/session';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const activities = ref([]);
const keyword = ref('');
const typeFilter = ref('');
const currentUser = ref(readSessionUser());

const fetchActivities = async () => {
  loading.value = true;
  try {
    const response = await activityApi.getList();
    activities.value = response.data || [];
  } catch (error) {
    console.error('活动数据加载失败', error);
  } finally {
    loading.value = false;
  }
};

const types = computed(() => [...new Set(activities.value.map((item) => item.type).filter(Boolean))]);
const filteredActivities = computed(() => activities.value.filter((activity) => {
  const matchesKeyword = !keyword.value || [activity.title, activity.summary, activity.location].filter(Boolean).some((text) => text.includes(keyword.value));
  const matchesType = !typeFilter.value || activity.type === typeFilter.value;
  return matchesKeyword && matchesType;
}));

const focusActivity = (activity) => {
  // 跳转到活动详情和报名页面
  router.push({
    path: '/activity-detail',
    query: {
      id: String(activity.id)
    }
  });
};

const resetFilters = () => {
  keyword.value = '';
  typeFilter.value = '';
};

onMounted(() => {
  fetchActivities();
});
</script>