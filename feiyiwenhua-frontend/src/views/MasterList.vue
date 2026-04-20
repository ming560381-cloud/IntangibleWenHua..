<template>
  <section class="page-section">
    <div class="container">
      <div class="section-heading">
        <div>
          <span class="section-eyebrow">Masters</span>
          <h2>传承人档案</h2>
          <p>聚焦代表性传承人的经历、擅长领域与相关非遗项目，从人物入口理解技艺传承。</p>
        </div>
      </div>

      <article class="detail-panel" style="margin-bottom: 24px;">
        <div class="filter-bar" style="grid-template-columns: minmax(0, 1fr) auto;">
          <input v-model.trim="keyword" class="field" type="text" placeholder="搜索传承人姓名、简介或成果" />
          <button class="ghost-button" @click="keyword = ''">清空搜索</button>
        </div>
      </article>

      <div v-if="loading" class="loading-state section-card">正在加载传承人数据...</div>
      <div v-else-if="filteredMasters.length === 0" class="empty-state section-card">没有找到符合条件的传承人。</div>
      <div v-else class="grid-3">
        <article v-for="master in filteredMasters" :key="master.id" class="resource-card">
          <img class="resource-cover" :src="resolveImage(master.image, master.name, '#366b5b', '#deefe9')" :alt="master.name" />
          <div class="resource-body">
            <div class="meta-row">
              <span class="badge-chip">传承人</span>
              <span class="meta-chip">关联项目 {{ relatedHeritages(master.id).length }}</span>
            </div>
            <h3>{{ master.name }}</h3>
            <p>{{ excerpt(master.introduction, 108) }}</p>
            <RouterLink class="text-button" :to="`/master/${master.id}`">查看详情</RouterLink>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { excerpt, heritageApi, masterApi, resolveImage } from '../services/api';

const loading = ref(true);
const keyword = ref('');
const masters = ref([]);
const heritages = ref([]);

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

const relatedHeritages = (masterId) => heritages.value.filter((item) => item.masterId === masterId);

onMounted(fetchData);
</script>