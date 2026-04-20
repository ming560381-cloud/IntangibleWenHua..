<template>
  <header class="site-header">
    <div class="container nav-row" ref="headerRef">
      <RouterLink class="brand" to="/">
        <span class="brand-mark">&#x5370;</span>
        <span class="brand-text-wrap">
          <span class="brand-text">&#x975E;&#x9057;&#x6587;&#x5316;&#x4EA4;&#x6D41;&#x7F51;&#x7AD9;</span>
          <span class="brand-sub">Intangible Heritage Exchange</span>
        </span>
      </RouterLink>

      <nav class="nav-links" aria-label="&#x4E3B;&#x5BFC;&#x822A;">
        <RouterLink
          v-for="item in primaryLinks"
          :key="item.to"
          :to="item.to"
        >
          {{ item.label }}
        </RouterLink>

        <div class="nav-more">
          <button
            type="button"
            class="text-button"
            @click="toggleMore"
          >
            {{ moreLabel }}
          </button>
          <div v-if="moreOpen" class="nav-more-panel">
            <RouterLink
              v-for="item in secondaryLinks"
              :key="item.to"
              :to="item.to"
              class="nav-more-link"
              @click="closeMore"
            >
              {{ item.label }}
            </RouterLink>
          </div>
        </div>
      </nav>

      <div class="nav-actions">
        <button class="ghost-button" type="button" @click="openAiAssistant">AI答疑</button>
        <template v-if="user">
          <RouterLink class="ghost-button" to="/my">我的</RouterLink>
          <span class="user-pill">{{ user.username || user.phone || defaultUserLabel }}</span>
          <button class="ghost-button" @click="handleLogout">{{ logoutLabel }}</button>
        </template>
        <template v-else>
          <RouterLink class="ghost-button" to="/login">{{ loginLabel }}</RouterLink>
          <RouterLink class="solid-button" to="/register">{{ registerLabel }}</RouterLink>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { userApi } from '../services/api';
import { clearSessionUser, readSessionUser, saveSessionUser, sessionEventName } from '../utils/session';

const headerRef = ref(null);
const router = useRouter();
const route = useRoute();
const user = ref(readSessionUser());
const moreOpen = ref(false);

const primaryLinks = [
  { to: '/', label: '\u9996\u9875' },
  { to: '/heritage', label: '\u975E\u9057\u8D44\u6E90' },
  { to: '/community', label: '\u4EA4\u6D41\u793E\u533A' },
  { to: '/activities', label: '\u6D3B\u52A8\u63A8\u5E7F' }
];

const secondaryLinks = [
  { to: '/master', label: '\u4F20\u627F\u4EBA' },
  { to: '/about', label: '\u9879\u76EE\u4ECB\u7ECD' },
  { to: '/contact', label: '\u8054\u7CFB\u6211\u4EEC' }
];

const moreLabel = '\u66F4\u591A';
const loginLabel = '\u767B\u5F55';
const registerLabel = '\u6CE8\u518C';
const logoutLabel = '\u9000\u51FA';
const defaultUserLabel = '\u5DF2\u767B\u5F55\u7528\u6237';

const isMoreActive = computed(() => secondaryLinks.some((item) => route.path.startsWith(item.to)));

const syncUser = () => {
  user.value = readSessionUser();
};

const syncUserFromServer = async () => {
  try {
    const response = await userApi.check();
    if (response.data?.authenticated && response.data?.user) {
      saveSessionUser(response.data.user);
      user.value = response.data.user;
      return;
    }
  } catch (error) {
    console.error('Failed to read auth state.', error);
  }
  clearSessionUser();
  user.value = null;
};

const handleLogout = async () => {
  try {
    await userApi.logout();
  } catch (error) {
    console.error('Failed to logout.', error);
  } finally {
    clearSessionUser();
    user.value = null;
    router.push('/');
  }
};

const openAiAssistant = () => {
  window.dispatchEvent(new CustomEvent('feiyi-open-ai'));
};

const closeMore = () => {
  moreOpen.value = false;
};

const toggleMore = () => {
  moreOpen.value = !moreOpen.value;
};

const handleDocumentClick = (event) => {
  if (!headerRef.value) {
    return;
  }
  if (!headerRef.value.contains(event.target)) {
    closeMore();
  }
};

watch(() => route.fullPath, () => {
  closeMore();
});

onMounted(() => {
  window.addEventListener(sessionEventName(), syncUser);
  document.addEventListener('click', handleDocumentClick);
  syncUserFromServer();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncUser);
  document.removeEventListener('click', handleDocumentClick);
});
</script>
