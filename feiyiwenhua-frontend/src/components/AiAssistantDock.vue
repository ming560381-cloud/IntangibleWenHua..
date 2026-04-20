<template>
  <div class="ai-dock" :class="{ 'is-open': open }">
    <button class="ai-fab" type="button" @click="toggleOpen">
      <span class="ai-fab-mark">问</span>
      <span class="ai-fab-text">AI答疑</span>
    </button>

    <transition name="ai-panel">
      <section v-if="open" class="ai-panel">
        <header class="ai-panel-header">
          <div>
            <p class="ai-panel-kicker">HERITAGE ASSISTANT</p>
            <h3>非遗 AI 答疑</h3>
          </div>
          <button class="ai-close" type="button" @click="open = false">关闭</button>
        </header>

        <div class="ai-quick-list">
          <button
            v-for="prompt in quickPrompts"
            :key="prompt"
            type="button"
            class="ai-quick-chip"
            @click="applyPrompt(prompt)"
          >
            {{ prompt }}
          </button>
        </div>

        <div class="ai-messages">
          <article
            v-for="item in messages"
            :key="item.id"
            class="ai-message"
            :class="item.role === 'user' ? 'is-user' : 'is-assistant'"
          >
            <span class="ai-role">{{ item.role === 'user' ? '我' : 'AI' }}</span>
            <p>{{ item.content }}</p>
          </article>

          <article v-if="loading" class="ai-message is-assistant">
            <span class="ai-role">AI</span>
            <p>正在整理回答，请稍候...</p>
          </article>
        </div>

        <form class="ai-form" @submit.prevent="submit">
          <textarea
            v-model.trim="draft"
            class="ai-textarea"
            placeholder="输入想问的内容，例如：这个项目适合如何入门？"
          />
          <div class="ai-form-row">
            <span class="ai-tip">可快速了解项目背景、活动参与方式和站内内容。</span>
            <button class="solid-button" type="submit" :disabled="loading || !draft">发送</button>
          </div>
        </form>
      </section>
    </transition>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import { aiApi } from '../services/api';

let idCounter = 1;

const open = ref(false);
const loading = ref(false);
const draft = ref('');
const quickPrompts = [
  '推荐几个适合入门了解的非遗项目',
  '这个网站可以怎么使用',
  '我想报名活动应该先看哪里'
];
const messages = ref([
  {
    id: idCounter++,
    role: 'assistant',
    content: '你好，我是站内 AI 答疑助手。你可以问我非遗项目、传承人、活动参与方式，或者站内功能怎么使用。'
  }
]);

const toggleOpen = () => {
  open.value = !open.value;
};

const applyPrompt = (prompt) => {
  draft.value = prompt;
  open.value = true;
};

const submit = async () => {
  if (!draft.value || loading.value) {
    return;
  }

  const question = draft.value;
  messages.value.push({
    id: idCounter++,
    role: 'user',
    content: question
  });
  draft.value = '';
  open.value = true;
  loading.value = true;

  try {
    const response = await aiApi.chat(question);
    messages.value.push({
      id: idCounter++,
      role: 'assistant',
      content: typeof response.data === 'string' ? response.data : '当前没有拿到有效回复，请稍后再试。'
    });
  } catch (error) {
    messages.value.push({
      id: idCounter++,
      role: 'assistant',
      content: error.response?.data || 'AI 答疑暂时不可用，请稍后再试。'
    });
  } finally {
    loading.value = false;
  }
};

const handleOpenEvent = () => {
  open.value = true;
};

onMounted(() => {
  window.addEventListener('feiyi-open-ai', handleOpenEvent);
});

onUnmounted(() => {
  window.removeEventListener('feiyi-open-ai', handleOpenEvent);
});
</script>
