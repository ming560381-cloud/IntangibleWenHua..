<template>
  <section class="page-section">
    <div class="container" style="max-width: 560px;">
      <article class="detail-panel">
        <h1>登录</h1>
        <p class="muted">支持密码登录，也保留验证码登录入口。</p>
        <div class="inline-actions" style="margin-bottom: 16px;">
          <button :class="loginMode === 'password' ? 'solid-button' : 'ghost-button'" @click="loginMode = 'password'">密码登录</button>
          <button :class="loginMode === 'code' ? 'solid-button' : 'ghost-button'" @click="loginMode = 'code'">验证码登录</button>
        </div>

        <template v-if="loginMode === 'password'">
          <input v-model.trim="passwordForm.phone" class="field" type="text" placeholder="手机号" />
          <input v-model.trim="passwordForm.password" class="field" type="password" placeholder="密码" style="margin-top: 12px;" />
          <button class="solid-button" style="margin-top: 16px;" @click="submitPasswordLogin">登录</button>
        </template>

        <template v-else>
          <input v-model.trim="codeForm.phone" class="field" type="text" placeholder="手机号" />
          <div class="inline-actions" style="margin-top: 12px;">
            <input v-model.trim="codeForm.code" class="field" type="text" placeholder="验证码" style="flex: 1;" />
            <button class="ghost-button" @click="sendCode">发送验证码</button>
          </div>
          <button class="solid-button" style="margin-top: 16px;" @click="submitCodeLogin">登录</button>
        </template>

        <p v-if="message" class="muted" style="margin-top: 14px;">{{ message }}</p>
        <div class="inline-actions" style="margin-top: 18px;">
          <RouterLink class="text-button" to="/register">没有账号？去注册</RouterLink>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { userApi } from '../services/api';
import { saveSessionUser } from '../utils/session';

const router = useRouter();
const route = useRoute();
const loginMode = ref('password');
const message = ref('');
const passwordForm = ref({ phone: '', password: '' });
const codeForm = ref({ phone: '', code: '' });

const finishLogin = (user) => {
  saveSessionUser(user);
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/';
  router.push(redirect);
};

const submitPasswordLogin = async () => {
  try {
    const response = await userApi.loginWithPassword(passwordForm.value);
    if (response.data?.success) {
      finishLogin(response.data.data);
    } else {
      message.value = response.data?.message || '登录失败';
    }
  } catch (error) {
    message.value = error.response?.data?.message || '登录失败，请检查手机号和密码。';
  }
};

const sendCode = async () => {
  try {
    const response = await userApi.sendCode({ phone: codeForm.value.phone });
    message.value = response.data?.message || '验证码已发送';
  } catch (error) {
    message.value = error.response?.data?.message || '验证码发送失败';
  }
};

const submitCodeLogin = async () => {
  try {
    const response = await userApi.loginWithCode(codeForm.value);
    if (response.data?.success) {
      finishLogin(response.data.data);
    } else {
      message.value = response.data?.message || '登录失败';
    }
  } catch (error) {
    message.value = error.response?.data?.message || '验证码登录失败';
  }
};
</script>
