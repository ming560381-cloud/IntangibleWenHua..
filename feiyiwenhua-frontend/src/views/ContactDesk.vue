<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner contact-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Contact & Feedback</span>
        <h1>如果你想补充资料、合作办展，或者反馈网站问题，这里是最直接的入口。</h1>
        <p>
          我们欢迎项目资料补充、活动合作洽谈、功能建议反馈，
          也欢迎提出新的传承人档案和技艺流程补录需求。
        </p>
      </section>

      <div class="contact-layout">
        <article class="contact-card">
          <h3>联系方式</h3>
          <div class="contact-list">
            <div class="contact-row">
              <strong>邮箱</strong>
              <span>feiyi@example.com</span>
            </div>
            <div class="contact-row">
              <strong>电话</strong>
              <span>0571-88886666</span>
            </div>
            <div class="contact-row">
              <strong>地址</strong>
              <span>非遗文化数字传播中心</span>
            </div>
          </div>
        </article>

        <article class="contact-card">
          <h3>反馈建议</h3>
          <p>
            如果你希望补充新的分类、技艺流程图、传承人资料或活动报名字段，
            也可以先在社区发帖，我们会优先整理高频需求。
          </p>
          <div class="inline-actions" style="margin-top: 14px;">
            <RouterLink class="solid-button" to="/community">去社区反馈</RouterLink>
            <button class="ghost-button" type="button" @click="openAi">先问 AI</button>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { RouterLink } from 'vue-router';

const openAi = () => {
  window.dispatchEvent(new CustomEvent('feiyi-open-ai'));
};
</script>
