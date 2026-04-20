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

          <div v-if="heritage.videoUrl" class="inline-actions" style="margin-top: 16px;">
            <a class="solid-button" :href="heritage.videoUrl" target="_blank" rel="noreferrer">观看相关视频</a>
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
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import {
  activityApi,
  activityStatus,
  categoryApi,
  excerpt,
  formatDateTime,
  heritageApi,
  masterApi,
  resolveImage
} from '../services/api';

const route = useRoute();
const loading = ref(true);
const heritage = ref(null);
const categories = ref([]);
const masters = ref([]);
const activities = ref([]);

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

const master = computed(() => masters.value.find((item) => item.id === heritage.value?.masterId) || null);
const categoryName = computed(() => categories.value.find((item) => item.id === heritage.value?.categoryId)?.name || '未分类');
const relatedActivities = computed(() => activities.value.filter((item) => item.heritageId === heritage.value?.id));
const levelName = (level) => ({ 1: '国家级', 2: '省级', 3: '市级' }[level] || '特色项目');

onMounted(fetchData);
</script>
