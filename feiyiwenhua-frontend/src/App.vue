<template>
  <div
    class="app-shell"
    :data-theme="themeName"
    :data-header-scrolled="headerScrolled ? 'true' : 'false'"
    :style="headerStyleVars"
  >
    <SiteHeader />
    <main class="page-main" :class="`theme-${themeName}`">
      <RouterView />
    </main>
    <SiteFooter />
    <AiAssistantDock />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { RouterView, useRoute } from 'vue-router';
import AiAssistantDock from './components/AiAssistantDock.vue';
import SiteFooter from './components/SiteFooter.vue';
import SiteHeader from './components/SiteHeader.vue';

const route = useRoute();
const themeName = computed(() => route.meta?.theme || 'home');
const headerScrolled = ref(false);
const headerProgress = ref(0);

const clamp = (value, min = 0, max = 1) => Math.min(max, Math.max(min, value));
const headerStyleVars = computed(() => ({
  '--home-header-progress': String(headerProgress.value),
  '--home-header-bg-alpha': String(0.03 + headerProgress.value * 0.9),
  '--home-header-border-alpha': String(headerProgress.value * 0.24),
  '--home-header-shadow-alpha': String(headerProgress.value * 0.16),
  '--home-header-blur': `${Math.round(headerProgress.value * 14)}px`
}));

const updateHeaderState = () => {
  if (themeName.value !== 'home') {
    headerScrolled.value = true;
    headerProgress.value = 1;
    return;
  }
  const scrollY = window.scrollY || window.pageYOffset || 0;
  headerProgress.value = clamp(scrollY / 220);
  headerScrolled.value = headerProgress.value > 0.1;
};

watch(() => route.fullPath, () => {
  updateHeaderState();
});

onMounted(() => {
  updateHeaderState();
  window.addEventListener('scroll', updateHeaderState, { passive: true });
});

onUnmounted(() => {
  window.removeEventListener('scroll', updateHeaderState);
});
</script>
