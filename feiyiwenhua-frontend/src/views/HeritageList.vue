<template>
  <section class="page-section">
    <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Resource Archive</span>
          <h2>非遗资源库</h2>
          <p>支持按关键词、类别和级别筛选项目，快速定位所需资源。</p>
        </div>
      </div>

      <article class="detail-panel" style="margin-bottom: 24px;">
        <div class="filter-bar">
          <input v-model.trim="keyword" class="field" type="text" placeholder="输入项目名称、简介或工艺流程关键词" />
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

      <div v-if="loading" class="loading-state section-card">正在加载非遗资源...</div>
      <div v-else-if="filteredItems.length === 0" class="empty-state section-card">没有找到符合条件的非遗项目。</div>
      <div v-else class="grid-3">
        <article v-for="item in filteredItems" :key="item.id" class="resource-card">
          <img class="resource-cover" :src="resolveImage(item.images, item.name, '#9c4c32', '#f7e4d2')" :alt="item.name" />
          <div class="resource-body">
            <div class="meta-row">
              <span class="badge-chip">{{ categoryName(item.categoryId) }}</span>
              <span class="meta-chip">{{ levelName(item.level) }}</span>
              <span v-if="masterName(item.masterId)" class="meta-chip">传承人：{{ masterName(item.masterId) }}</span>
            </div>
            <h3>{{ item.name }}</h3>
            <p>{{ excerpt(item.description, 108) }}</p>
            <RouterLink class="text-button" :to="`/heritage/${item.id}`">阅读详情</RouterLink>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { categoryApi, excerpt, heritageApi, masterApi, resolveImage } from '../services/api';

const loading = ref(true);
const heritages = ref([]);
const categories = ref([]);
const masters = ref([]);
const keyword = ref('');
const selectedCategory = ref('');
const selectedLevel = ref('');

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

const filteredItems = computed(() => {
  return heritages.value.filter((item) => {
    const matchesKeyword = !keyword.value || [item.name, item.description, item.history, item.process]
      .filter(Boolean)
      .some((text) => text.includes(keyword.value));
    const matchesCategory = !selectedCategory.value || String(item.categoryId) === selectedCategory.value;
    const matchesLevel = !selectedLevel.value || String(item.level) === selectedLevel.value;
    return matchesKeyword && matchesCategory && matchesLevel;
  });
});

const categoryName = (categoryId) => categories.value.find((item) => item.id === categoryId)?.name || '未分类';
const masterName = (masterId) => masters.value.find((item) => item.id === masterId)?.name || '';
const levelName = (level) => ({ 1: '国家级', 2: '省级', 3: '市级' }[level] || '特色项目');
const resetFilters = () => {
  keyword.value = '';
  selectedCategory.value = '';
  selectedLevel.value = '';
};

onMounted(fetchData);
</script>