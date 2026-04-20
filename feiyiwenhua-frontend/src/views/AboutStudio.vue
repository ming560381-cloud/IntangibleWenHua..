<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner about-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Project Manifesto</span>
        <h1>这不是一个只做展示的网站，而是一条把浏览、理解和参与串起来的路径。</h1>
        <p>
          网站围绕非遗资源展示、互动交流、活动推广和资源检索四个方向搭建，
          既适合毕业设计展示，也适合作为地方文化数字化传播的基础站点。
        </p>
      </section>

      <div class="manifesto-grid">
        <article class="manifesto-panel">
          <h3>核心目标</h3>
          <div class="manifesto-list">
            <div class="manifesto-item">
              <strong>资源展示</strong>
              <p>整合项目简介、技艺流程、传承人、图文与视频信息，让内容更容易被首次访问者理解。</p>
            </div>
            <div class="manifesto-item">
              <strong>互动交流</strong>
              <p>通过社区帖子、评论回复和在线答疑，让用户不仅是浏览内容，也能主动发问和分享。</p>
            </div>
            <div class="manifesto-item">
              <strong>活动推广</strong>
              <p>统一发布展览、培训、导赏和体验课程，并把活动详情与在线报名放在同一流程里。</p>
            </div>
            <div class="manifesto-item">
              <strong>资源检索</strong>
              <p>通过关键词搜索和分类筛选，帮助用户快速定位项目、人物和活动信息。</p>
            </div>
          </div>
        </article>

        <div class="list-stack">
          <article class="manifesto-panel">
            <h3>适用场景</h3>
            <p>适合作为高校毕业设计、地方非遗宣传站、文化馆数字展示页，或教学演示项目的基础版本。</p>
            <div class="inline-actions" style="margin-top: 14px;">
              <RouterLink class="solid-button" to="/heritage">浏览资源</RouterLink>
              <RouterLink class="ghost-button" to="/activities">查看活动</RouterLink>
            </div>
          </article>

          <article class="manifesto-panel">
            <h3>站点特色</h3>
            <ul>
              <li>首页负责导览，不把所有信息一次性堆给用户。</li>
              <li>资源页、社区页、活动页各自承担不同任务和节奏。</li>
              <li>内置 AI 答疑可辅助用户快速理解站内内容和参与方式。</li>
            </ul>
          </article>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { RouterLink } from 'vue-router';
</script>
