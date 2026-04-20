<template>
  <section class="page-section">
    <div class="container">
      <div v-if="loading" class="loading-state section-card">正在加载传承人详情...</div>
      <div v-else-if="!master" class="empty-state section-card">未找到该传承人档案。</div>
      <div v-else class="detail-layout">
        <article class="detail-panel">
          <img class="detail-cover" :src="resolveImage(master.image, master.name, '#366b5b', '#deefe9')" :alt="master.name" />
          <div class="meta-row">
            <span class="badge-chip">传承人</span>
            <span class="meta-chip">{{ master.gender || '性别未标注' }}</span>
            <span class="meta-chip">出生日期：{{ formatDate(master.birthDate) }}</span>
          </div>
          <h1>{{ master.name }}</h1>
          <p>{{ master.introduction || '暂无人物简介。' }}</p>

          <div class="detail-panel" style="margin-top: 20px; box-shadow: none;">
            <h3>代表成果</h3>
            <p>{{ master.achievements || '暂无成果信息。' }}</p>
          </div>
        </article>

        <aside class="sticky-side list-stack">
          <article class="detail-panel">
            <h3>相关非遗项目</h3>
            <div v-if="relatedHeritages.length" class="list-stack">
              <div v-for="item in relatedHeritages" :key="item.id" class="info-card">
                <div class="info-body">
                  <h3>{{ item.name }}</h3>
                  <p>{{ excerpt(item.description, 88) }}</p>
                  <RouterLink class="text-button" :to="`/heritage/${item.id}`">查看项目</RouterLink>
                </div>
              </div>
            </div>
            <p v-else class="muted">暂未关联具体项目。</p>
          </article>

          <article class="detail-panel">
            <h3>参与互动</h3>
            <p>如果想进一步了解学习路径、观演方法或技艺要点，可以进入社区发起提问，或直接查看近期导赏活动。</p>
            <div class="inline-actions">
              <RouterLink class="solid-button" to="/community?tag=在线答疑">去提问</RouterLink>
              <RouterLink class="ghost-button" to="/activities">看活动</RouterLink>
            </div>
          </article>
        </aside>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import { excerpt, formatDate, heritageApi, masterApi, resolveImage } from '../services/api';

const route = useRoute();
const loading = ref(true);
const master = ref(null);
const heritages = ref([]);

const fetchData = async () => {
  loading.value = true;
  try {
    const [masterRes, heritageRes] = await Promise.all([
      masterApi.getDetail(route.params.id),
      heritageApi.getList()
    ]);
    master.value = masterRes.data;
    heritages.value = heritageRes.data || [];
  } catch (error) {
    console.error('传承人详情加载失败', error);
    master.value = null;
  } finally {
    loading.value = false;
  }
};

const relatedHeritages = computed(() => heritages.value.filter((item) => item.masterId === master.value?.id));

onMounted(fetchData);
</script>
