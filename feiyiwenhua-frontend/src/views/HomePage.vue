<template>
  <section ref="setPhaseRef" class="home-phase phase-hero">
    <div class="home-phase-bg" :style="{ backgroundImage: heroStageBackground, transform: `translateY(${phaseShifts[0]}px)` }"></div>
    <div class="home-phase-overlay"></div>
    <section class="hero hero-poster home-stage hero-stage-fullscreen">
      <div class="hero-stage hero-stage-fullscreen-inner">
        <div class="hero-carousel">
          <img :src="heroSlides[currentHeroIndex]" :alt="`首页轮播图 ${currentHeroIndex + 1}`" class="hero-carousel-image" />
          <div class="hero-carousel-mask">
            <h1>让技艺、人物与在场体验，回到当代生活。</h1>
            <p>像你参考站那样的分阶段沉浸浏览，从这里开始。</p>
          </div>
          <div class="hero-carousel-dots">
            <button
              v-for="(_, idx) in heroSlides"
              :key="`hero-dot-${idx}`"
              type="button"
              class="hero-carousel-dot"
              :class="{ 'is-active': idx === currentHeroIndex }"
              @click="currentHeroIndex = idx"
            ></button>
          </div>
        </div>
      </div>
    </section>
  </section>

  <section ref="setPhaseRef" class="home-phase phase-1">
    <div class="home-phase-bg" :style="{ backgroundImage: phaseBackground(0), transform: `translateY(${phaseShifts[1]}px)` }"></div>
    <div class="home-phase-overlay"></div>
    <section class="hero hero-poster home-stage">
      <div class="container hero-stage">
        <div class="home-quick-entry">
        <div class="quick-card-grid">
          <RouterLink v-for="card in quickEntryCards" :key="card.key" :to="card.to" class="quick-entry-card">
            <div class="quick-card-inner">
              <article class="quick-card-face quick-card-front">
                <img :src="card.image" :alt="card.title" class="quick-card-image" />
                <div class="quick-card-mask">
                  <h3>{{ card.title }}</h3>
                </div>
              </article>
              <article class="quick-card-face quick-card-back">
                <h3>{{ card.title }}</h3>
                <p>{{ card.desc }}</p>
                <span class="text-button">点击进入</span>
              </article>
            </div>
          </RouterLink>
        </div>

        <div class="hero-search-label">快速检索</div>
        <div class="search-bar">
          <input
            v-model.trim="keyword"
            class="field"
            type="text"
            placeholder="搜索项目、传承人、活动或社区帖子"
            @keyup.enter="runSearch"
          />
          <select v-model="searchType" class="select">
            <option value="">全部类型</option>
            <option value="heritage">非遗项目</option>
            <option value="master">传承人</option>
            <option value="activity">活动</option>
            <option value="post">社区帖子</option>
          </select>
          <button class="solid-button" @click="runSearch">立即搜索</button>
        </div>

        <div class="stats-grid">
          <article class="stat-card">
            <strong>{{ heritages.length }}</strong>
            <span>非遗项目</span>
          </article>
          <article class="stat-card">
            <strong>{{ activities.length }}</strong>
            <span>活动发布</span>
          </article>
          <article class="stat-card">
            <strong>{{ masters.length }}</strong>
            <span>传承人档案</span>
          </article>
          <article class="stat-card">
            <strong>{{ posts.length }}</strong>
            <span>社区交流</span>
          </article>
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
      </div>
    </section>
  </section>

  <section ref="setPhaseRef" class="home-phase phase-2">
    <div class="home-phase-bg" :style="{ backgroundImage: phaseBackground(1), transform: `translateY(${phaseShifts[2]}px)` }"></div>
    <div class="home-phase-overlay"></div>
    <section class="page-section">
      <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Archive Highlights</span>
          <h2>先看项目，再看它背后的人与工艺。</h2>
          <p>资源库以项目为主轴，串联类别、级别、技艺流程与代表性传承人。</p>
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
  </section>

  <section ref="setPhaseRef" class="home-phase phase-3">
    <div class="home-phase-bg" :style="{ backgroundImage: phaseBackground(2), transform: `translateY(${phaseShifts[3]}px)` }"></div>
    <div class="home-phase-overlay"></div>
    <section class="page-section">
      <div class="container">
      <div class="story-grid">
        <article class="story-column">
          <div class="section-eyebrow" style="margin-bottom: 16px;">代表传承人</div>
          <div class="story-list">
            <article v-for="master in featuredMasters" :key="master.id" class="story-entry">
              <h3 style="font-size: 24px; margin-bottom: 10px;">{{ master.name }}</h3>
              <p>{{ excerpt(master.introduction, 92) }}</p>
              <p class="muted">关联项目 {{ heritageCount(master.id) }} 个</p>
              <RouterLink class="text-button" :to="`/master/${master.id}`">查看人物档案</RouterLink>
            </article>
          </div>
        </article>

        <article class="story-column">
          <div class="section-eyebrow" style="margin-bottom: 16px;">近期讨论</div>
          <div class="story-list">
            <article v-for="post in featuredPosts" :key="post.id" class="story-entry">
              <div class="meta-row">
                <span v-for="tag in splitTags(post.tags).slice(0, 3)" :key="tag" class="meta-chip">{{ tag }}</span>
              </div>
              <h3 style="font-size: 24px; margin-bottom: 10px;">{{ post.title }}</h3>
              <p>{{ excerpt(post.content, 94) }}</p>
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
            <span class="section-eyebrow" style="color: rgba(255, 247, 239, 0.82);">From Browse To Participate</span>
            <h2 style="margin-top: 12px;">浏览之后，就把问题和兴趣带到现场。</h2>
            <p>活动板块会集中展示展览、培训、体验与导赏信息，并支持在线报名。</p>
          </div>
          <div class="inline-actions">
            <RouterLink class="ghost-button" to="/community">进入社区</RouterLink>
            <RouterLink class="ghost-button" to="/activities">查看活动</RouterLink>
          </div>
        </div>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import {
  activityApi,
  categoryApi,
  excerpt,
  heritageApi,
  masterApi,
  postApi,
  resolveImage,
  searchApi
} from '../services/api';
import t1Image from '../assets/images/c1.png';
import t2Image from '../assets/images/c2.png';
import t3Image from '../assets/images/c3.png';

const loading = ref(true);
const heritages = ref([]);
const categories = ref([]);
const masters = ref([]);
const activities = ref([]);
const posts = ref([]);
const keyword = ref('');
const searchType = ref('');
const searchResults = ref([]);
const phaseShifts = ref([0, 0, 0, 0]);
const phaseRefs = ref([]);
const currentHeroIndex = ref(0);
const heroSlides = [t1Image, t2Image, t3Image];
let heroTimer = null;

const featuredHeritages = computed(() => heritages.value.slice(0, 3));
const featuredMasters = computed(() => masters.value.slice(0, 3));
const featuredPosts = computed(() => posts.value.slice(0, 3));
const quickEntryCards = computed(() => [
  {
    key: 'heritage',
    title: '非遗项目',
    desc: '按类别、级别和关键词浏览非遗内容。',
    to: '/heritage',
    image: resolveImage('https://tse1-mm.cn.bing.net/th/id/OIP-C.SBa5DtvQ0eiSAGzLboou7QHaEf?w=283&h=180&c=7&r=0&o=5&dpr=1.7&pid=1.7', '非遗项目', '#9c4c32', '#f7e4d2')
  },
  {
    key: 'activity',
    title: '活动',
    desc: '查看展演、培训与体验活动并在线报名。',
    to: '/activities',
    image: resolveImage('https://tse4-mm.cn.bing.net/th/id/OIP-C.vxOBS9jzWpX-tR4HOLxRJgHaE8?w=260&h=180&c=7&r=0&o=5&dpr=1.7&pid=1.7', '活动', '#7b5b2f', '#f6e8c8')
  },
  {
    key: 'master',
    title: '传承人',
    desc: '阅读代表性传承人的经历与成果档案。',
    to: '/master',
    image: resolveImage('https://tse1-mm.cn.bing.net/th/id/OIP-C.gr2W3FHTdOCjEdwt8n0bsgHaE3?w=232&h=180&c=7&r=0&o=5&dpr=1.7&pid=1.7', '传承人', '#366b5b', '#deefe9')
  },
  {
    key: 'community',
    title: '交流社区',
    desc: '提问、讨论、分享观展与学习心得。',
    to: '/community',
    image: resolveImage('https://tse3-mm.cn.bing.net/th/id/OIP-C.40wUfFY835QBXpAM0zXR2gHaFD?w=250&h=180&c=7&r=0&o=5&dpr=1.7&pid=1.7','交流社区', '#6f4b3b', '#eee3d8')
  }
]);

const setPhaseRef = (element) => {
  if (!element || phaseRefs.value.includes(element)) return;
  phaseRefs.value.push(element);
};

const heroStageBackground = computed(() => `linear-gradient(rgba(245, 236, 224, 0.38), rgba(245, 236, 224, 0.38)), url("${heroSlides[currentHeroIndex.value]}")`);

const phaseBackground = (index) => {
  const fallback = [
    resolveImage(heritages.value[0]?.images, '首页阶段一', '#8b3d25', '#f5e5d5'),
    resolveImage(activities.value[0]?.images, '首页阶段二', '#7a5a2a', '#f4e8d1'),
    resolveImage(masters.value[0]?.image || posts.value[0]?.coverImage, '首页阶段三', '#5e4638', '#eee2d7')
  ];
  const image = fallback[index] || fallback[0];
  return `linear-gradient(rgba(248, 241, 232, 0.86), rgba(248, 241, 232, 0.86)), url("${image}")`;
};

const updatePhaseShift = () => {
  const viewportHeight = window.innerHeight || 1;
  phaseRefs.value.forEach((element, index) => {
    const rect = element.getBoundingClientRect();
    const progress = (viewportHeight - rect.top) / (viewportHeight + rect.height);
    const clamped = Math.min(1, Math.max(0, progress));
    phaseShifts.value[index] = -Math.round(clamped * 120);
  });
};

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
  updatePhaseShift();
  window.addEventListener('scroll', updatePhaseShift, { passive: true });
  window.addEventListener('resize', updatePhaseShift);
  heroTimer = window.setInterval(() => {
    currentHeroIndex.value = (currentHeroIndex.value + 1) % heroSlides.length;
  }, 3500);
});

onUnmounted(() => {
  window.removeEventListener('scroll', updatePhaseShift);
  window.removeEventListener('resize', updatePhaseShift);
  if (heroTimer) {
    window.clearInterval(heroTimer);
    heroTimer = null;
  }
});
</script>
