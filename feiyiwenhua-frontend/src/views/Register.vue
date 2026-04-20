<template>
  <section class="page-section">
    <div class="container" style="max-width: 560px;">
      <article class="detail-panel">
        <h1>注册账号</h1>
        <p class="muted">注册后可在社区发帖、留言评论并便捷报名活动。</p>
        <input v-model.trim="form.username" class="field" type="text" placeholder="用户名" />
        <input v-model.trim="form.phone" class="field" type="text" placeholder="手机号" style="margin-top: 12px;" />
        <input v-model.trim="form.password" class="field" type="password" placeholder="密码（至少 6 位）" style="margin-top: 12px;" />
        <input v-model.trim="confirmPassword" class="field" type="password" placeholder="确认密码" style="margin-top: 12px;" />
        <button class="solid-button" style="margin-top: 16px;" @click="submitRegister">注册</button>
        <p v-if="message" class="muted" style="margin-top: 14px;">{{ message }}</p>
        <div class="inline-actions" style="margin-top: 18px;">
          <RouterLink class="text-button" to="/login">已有账号？去登录</RouterLink>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { userApi } from '../services/api';

const router = useRouter();
const confirmPassword = ref('');
const message = ref('');
const form = ref({
  username: '',
  phone: '',
  password: '',
  role: 'user'
});

const submitRegister = async () => {
  if (!form.value.username || !form.value.phone || !form.value.password) {
    message.value = '请完整填写注册信息。';
    return;
  }
  if (form.value.password.length < 6) {
    message.value = '密码至少需要 6 位。';
    return;
  }
  if (form.value.password !== confirmPassword.value) {
    message.value = '两次输入的密码不一致。';
    return;
  }
  try {
    const response = await userApi.register(form.value);
    if (response.data?.success) {
      message.value = '注册成功，正在跳转到登录页。';
      setTimeout(() => router.push('/login'), 800);
    } else {
      message.value = response.data?.message || '注册失败';
    }
  } catch (error) {
    message.value = error.response?.data?.message || '注册失败，请稍后再试。';
  }
};
</script>
