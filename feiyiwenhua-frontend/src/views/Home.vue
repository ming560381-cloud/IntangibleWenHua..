<template>
  <section class="hero hero-poster">
    <div class="container hero-stage">
      <div class="hero-copy">
        <span class="hero-kicker">非遗资源展示 · 互动交流 · 活动推广 · 检索服务</span>
        <div class="hero-caption">
          <span class="hero-seal">非遗</span>
          <span>Intangible Heritage Exchange</span>
        </div>
        <h1>让技艺、人物与在场体验，在一册当代中式画卷里重新相遇。</h1>
        <p>
          这里汇集非遗项目、传承人、技艺流程、社区答疑与活动报名，
          让用户从安静浏览走向深入理解，也真正走进线下体验与文化交流。
        </p>
        <div class="hero-actions">
          <RouterLink class="solid-button" to="/heritage">进入资源库</RouterLink>
          <RouterLink class="ghost-button" to="/activities">查看活动日程</RouterLink>
        </div>

        <div class="stats-grid">
          <article class="stat-card">
            <strong>{{ heritages.length }}</strong>
            <span>非遗项目</span>
          </article>
          <article class="stat-card">
            <strong>{{ masters.length }}</strong>
            <span>传承人档案</span>
          </article>
          <article class="stat-card">
            <strong>{{ activities.length }}</strong>
            <span>活动与培训</span>
          </article>
          <article class="stat-card">
            <strong>{{ posts.length }}</strong>
            <span>社区讨论</span>
          </article>
        </div>

        <div class="search-bar">
          <input
            v-model.trim="keyword"
            class="field"
            type="text"
            placeholder="搜索非遗项目、传承人、活动或社区帖子"
            @keyup.enter="runSearch"
          />
          <select v-model="searchType" class="select">
            <option value="">全部类型</option>
            <option value="heritage">非遗项目</option>
            <option value="master">传承人</option>
            <option value="activity">活动</option>
            <option value="post">社区</option>
          </select>
          <button class="solid-button" @click="runSearch">立即搜索</button>
        </div>

        <div v-if="searchResults.length" class="hero-results list-stack" style="margin-top: 18px;">
          <article v-for="item in searchResults" :key="`${item.type}-${item.id}`">
            <div class="meta-row" style="margin-bottom: 8px;">
              <span class="meta-chip">{{ item.extra }}</span>
            </div>
            <h3 style="margin: 0 0 8px;">{{ item.title }}</h3>
            <p style="margin: 0 0 10px;">{{ excerpt(item.summary, 118) }}</p>
            <RouterLink class="text-button" :to="targetLink(item)">查看详情</RouterLink>
          </article>
        </div>
      </div>

      <aside class="hero-aside">
        <span class="section-eyebrow">非遗资源轮番</span>
        <div class="heritage-carousel">
          <div class="carousel-container" ref="carouselContainer">
            <article 
              v-for="(heritage, index) in featuredHeritages" 
              :key="heritage.id" 
              class="carousel-item"
              :style="{ transform: `translateX(${(index - currentSlide) * 100}%)` }"
            >
              <img
                class="carousel-image"
                :src="resolveImage(heritage.images, heritage.name, '#9c4c32', '#f7e4d2')"
                :alt="heritage.name"
              />
              <div class="carousel-content">
                <div class="meta-row">
                  <span class="badge-chip">{{ categoryName(heritage.categoryId) }}</span>
                  <span class="meta-chip">{{ levelName(heritage.level) }}</span>
                </div>
                <h3>{{ heritage.name }}</h3>
                <p>{{ excerpt(heritage.description, 80) }}</p>
                <RouterLink class="text-button" :to="`/heritage/${heritage.id}`">查看详情</RouterLink>
              </div>
            </article>
          </div>
          <div class="carousel-indicators">
            <button 
              v-for="(_, index) in featuredHeritages.length" 
              :key="index"
              class="indicator"
              :class="{ active: currentSlide === index }"
              @click="currentSlide = index"
            ></button>
          </div>
        </div>
      </aside>
    </div>
  </section>

  <section class="page-section">
    <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Resource Archive</span>
          <h2>先从项目入门，看见技艺的来路与层次</h2>
          <p>以图文和流程信息快速了解代表性非遗项目，并串联相关类别与传承人。</p>
        </div>
        <RouterLink class="text-button" to="/heritage">查看全部项目</RouterLink>
      </div>

      <div v-if="loading" class="loading-state section-card">正在加载首页内容...</div>
      <div v-else class="editorial-list">
        <article v-for="heritage in featuredHeritages" :key="heritage.id" class="editorial-item">
          <img
            class="editorial-media"
            :src="resolveImage(heritage.images, heritage.name, '#9c4c32', '#f7e4d2')"
            :alt="heritage.name"
          />
          <div class="editorial-content">
            <div class="meta-row">
              <span class="badge-chip">{{ categoryName(heritage.categoryId) }}</span>
              <span class="meta-chip">{{ levelName(heritage.level) }}</span>
              <span v-if="masterName(heritage.masterId)" class="meta-chip">传承人：{{ masterName(heritage.masterId) }}</span>
            </div>
            <h3>{{ heritage.name }}</h3>
            <p>{{ excerpt(heritage.description, 120) }}</p>
            <RouterLink class="text-button" :to="`/heritage/${heritage.id}`">阅读项目详情</RouterLink>
          </div>
        </article>
      </div>
    </div>
  </section>

  <section class="page-section">
    <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Masters</span>
          <h2>从人物进入，理解非遗如何被一代代延续</h2>
          <p>聚焦代表性传承人的经历、专长与关联项目，建立更有温度的人物印象。</p>
        </div>
        <RouterLink class="text-button" to="/master">查看全部传承人</RouterLink>
      </div>

      <div class="portrait-grid">
        <article v-for="master in featuredMasters" :key="master.id" class="portrait-item">
          <img
            class="portrait-media"
            :src="resolveImage(master.image, master.name, '#36594f', '#e7efe8')"
            :alt="master.name"
          />
          <div class="meta-row">
            <span class="badge-chip">传承人</span>
            <span class="meta-chip">关联项目 {{ heritageCount(master.id) }}</span>
          </div>
          <h3>{{ master.name }}</h3>
          <p>{{ excerpt(master.introduction, 98) }}</p>
          <RouterLink class="text-button" :to="`/master/${master.id}`">查看人物档案</RouterLink>
        </article>
      </div>
    </div>
  </section>

  <section class="page-section">
    <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Activities & Community</span>
          <h2>线上交流与线下体验，让浏览变成真正参与</h2>
          <p>活动推广与社区问答并行，让用户既能提问讨论，也能报名进入真实场景。</p>
        </div>
      </div>

      <div class="story-grid">
        <article class="story-column">
          <div class="section-eyebrow" style="margin-bottom: 16px;">近期活动</div>
          <div class="story-list">
            <article v-for="activity in featuredActivities" :key="activity.id" class="story-entry">
              <div class="meta-row">
                <span class="status-chip">{{ activityStatus(activity) }}</span>
                <span class="meta-chip">{{ activity.type || '活动' }}</span>
              </div>
              <h3 style="font-size: 24px; margin-bottom: 10px;">{{ activity.title }}</h3>
              <p>{{ excerpt(activity.summary, 88) }}</p>
              <p class="muted">{{ formatDateTime(activity.startTime) }} · {{ activity.location || '地点待定' }}</p>
              <RouterLink class="text-button" :to="`/activities?focus=${activity.id}`">查看并报名</RouterLink>
            </article>
          </div>
        </article>

        <article class="story-column">
          <div class="section-eyebrow" style="margin-bottom: 16px;">社区新帖</div>
          <div class="story-list">
            <article v-for="post in featuredPosts" :key="post.id" class="story-entry">
              <div class="meta-row">
                <span v-for="tag in splitTags(post.tags).slice(0, 3)" :key="tag" class="meta-chip">{{ tag }}</span>
              </div>
              <h3 style="font-size: 24px; margin-bottom: 10px;">{{ post.title }}</h3>
              <p>{{ excerpt(post.content, 96) }}</p>
              <p class="muted">作者：{{ post.authorName || '匿名用户' }} · 评论 {{ post.commentCount || 0 }}</p>
              <RouterLink class="text-button" :to="`/community/${post.id}`">进入讨论</RouterLink>
            </article>
          </div>
        </article>
      </div>
    </div>
  </section>

  <section class="page-section">
    <div class="container">
      <div class="cta-band">
        <div>
          <span class="section-eyebrow" style="color: rgba(255, 247, 239, 0.82);">Join The Archive</span>
          <h2 style="margin-top: 12px;">浏览资源、提出问题、报名活动，把非遗从“知道”走到“参与”。</h2>
          <p>如果你想继续深入，可以从社区发帖开始，也可以直接查看近期活动安排。</p>
        </div>
        <div class="inline-actions">
          <RouterLink class="ghost-button" to="/community">进入社区</RouterLink>
          <RouterLink class="ghost-button" to="/activities">查看活动</RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import {
  activityApi,
  activityStatus,
  categoryApi,
  excerpt,
  formatDateTime,
  heritageApi,
  masterApi,
  postApi,
  resolveImage,
  searchApi
} from '../services/api';

const loading = ref(true);
const heritages = ref([]);
const categories = ref([]);
const masters = ref([]);
const activities = ref([]);
const posts = ref([]);
const keyword = ref('');
const searchType = ref('');
const searchResults = ref([]);
const currentSlide = ref(0);
const carouselContainer = ref(null);

// 自动轮播
let carouselInterval = null;

const startCarousel = () => {
  if (carouselInterval) clearInterval(carouselInterval);
  carouselInterval = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % featuredHeritages.value.length;
  }, 3000);
};

const stopCarousel = () => {
  if (carouselInterval) {
    clearInterval(carouselInterval);
    carouselInterval = null;
  }
};

const featuredHeritages = computed(() => heritages.value.slice(0, 3));
const featuredMasters = computed(() => masters.value.slice(0, 3));
const featuredActivities = computed(() => activities.value.slice(0, 3));
const featuredPosts = computed(() => posts.value.slice(0, 3));

const fetchHomeData = async () => {
  loading.value = true;
  try {
    const [heritageRes, categoryRes, masterRes, activityRes, postRes] = await Promise.all([
      heritageApi.getList(),
      categoryApi.getList(),
      masterApi.getList(),
      activityApi.getList(),
      postApi.getList()
    ]);

    heritages.value = heritageRes.data || [];
    categories.value = categoryRes.data || [];
    masters.value = masterRes.data || [];
    activities.value = activityRes.data || [];
    posts.value = postRes.data || [];
  } catch (error) {
    console.error('首页数据加载失败', error);
  } finally {
    loading.value = false;
  }
};

const runSearch = async () => {
  if (!keyword.value) {
    searchResults.value = [];
    return;
  }
  try {
    const response = await searchApi.query(keyword.value, searchType.value);
    searchResults.value = response.data || [];
  } catch (error) {
    console.error('搜索失败', error);
  }
};

const targetLink = (item) => {
  if (item.type === 'master') return `/master/${item.id}`;
  if (item.type === 'activity') return `/activities?focus=${item.id}`;
  if (item.type === 'post') return `/community/${item.id}`;
  return `/heritage/${item.id}`;
};

const categoryName = (categoryId) => categories.value.find((item) => item.id === categoryId)?.name || '未分类';
const masterName = (masterId) => masters.value.find((item) => item.id === masterId)?.name || '';
const heritageCount = (masterId) => heritages.value.filter((item) => item.masterId === masterId).length;
const splitTags = (tags) => (tags || '').split(',').map((item) => item.trim()).filter(Boolean);
const levelName = (level) => ({ 1: '国家级', 2: '省级', 3: '市级' }[level] || '特色项目');

onMounted(async () => {
  await fetchHomeData();
  // 数据加载完成后启动轮播
  if (featuredHeritages.value.length > 0) {
    startCarousel();
  }
});

// 组件卸载时清除轮播定时器
onUnmounted(() => {
  stopCarousel();
});
</script>