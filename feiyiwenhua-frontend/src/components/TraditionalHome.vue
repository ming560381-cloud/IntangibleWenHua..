<template>
  <div class="traditional-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="ink-splash"></div>
      <div class="paper-texture"></div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 头部 -->
      <header class="header">
        <div class="logo">
          <h1 class="logo-text">飞翼文化</h1>
          <div class="logo-decoration">
            <span class="decoration-line"></span>
            <span class="decoration-dot"></span>
          </div>
        </div>
      </header>

      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <div class="welcome-card">
          <div class="ink-brush"></div>
          <h2 class="welcome-title">传承千年文化</h2>
          <p class="welcome-subtitle">领略东方美学，感受传统韵味</p>
          <div class="scroll-indicator">
            <span class="scroll-text">向下探索</span>
            <div class="scroll-arrow"></div>
          </div>
        </div>
      </section>

      <!-- 内容区域 -->
      <section class="content-section">
        <div class="content-grid">
          <div class="content-card" v-for="(item, index) in contentItems" :key="index">
            <div class="card-header">
              <div class="card-icon" :style="{ backgroundColor: item.color }">
                <span class="icon-text">{{ item.icon }}</span>
              </div>
              <h3 class="card-title">{{ item.title }}</h3>
            </div>
            <p class="card-description">{{ item.description }}</p>
          </div>
        </div>
      </section>

      <!-- 特色展示 -->
      <section class="features-section">
        <h2 class="section-title">文化特色</h2>
        <div class="features-grid">
          <div class="feature-item" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon" :style="{ backgroundColor: feature.color }">
              <span class="feature-icon-text">{{ feature.icon }}</span>
            </div>
            <h4 class="feature-title">{{ feature.title }}</h4>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </section>

      <!-- 底部 -->
      <footer class="footer">
        <div class="footer-content">
          <p class="footer-text">© 2026 飞翼文化 | 传承经典，创新未来</p>
          <div class="footer-decoration">
            <span class="decoration-line"></span>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const contentItems = ref([
  {
    icon: '文',
    title: '传统文化',
    description: '探索中华五千年文明，感受诗词歌赋的韵味',
    color: '#8B4513'
  },
  {
    icon: '艺',
    title: '艺术瑰宝',
    description: '欣赏书法绘画，品味陶瓷玉器之美',
    color: '#CD853F'
  },
  {
    icon: '史',
    title: '历史长河',
    description: '追溯历史足迹，了解朝代更迭与文明演进',
    color: '#A0522D'
  },
  {
    icon: '哲',
    title: '智慧结晶',
    description: '感悟儒家道家思想，领略东方哲学智慧',
    color: '#D2691E'
  }
])

const features = ref([
  {
    icon: '🏮',
    title: '节日庆典',
    description: '体验传统节日的独特魅力与深厚内涵',
    color: '#FF6347'
  },
  {
    icon: '🎋',
    title: '民俗风情',
    description: '了解各地民俗习惯，感受民间艺术魅力',
    color: '#FF8C00'
  },
  {
    icon: '📜',
    title: '古籍文献',
    description: '研读经典著作，传承文化精髓',
    color: '#FFA500'
  },
  {
    icon: '🎨',
    title: '工艺技术',
    description: '欣赏传统工艺，了解匠心独运的精湛技艺',
    color: '#FFD700'
  }
])
</script>

<style scoped>
.traditional-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #F5F5DC 0%, #FAEBD7 100%);
  position: relative;
  overflow-x: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.ink-splash {
  position: absolute;
  top: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(139, 69, 19, 0.1) 0%, rgba(139, 69, 19, 0) 70%);
  border-radius: 50%;
  filter: blur(20px);
}

.paper-texture {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0, 0, 0, 0.02) 2px, rgba(0, 0, 0, 0.02) 4px),
    repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(0, 0, 0, 0.02) 2px, rgba(0, 0, 0, 0.02) 4px);
  opacity: 0.3;
}

.main-content {
  position: relative;
  z-index: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  padding: 2rem 0;
  margin-bottom: 3rem;
}

.logo {
  display: inline-block;
  position: relative;
}

.logo-text {
  font-family: 'Ma Shan Zhong', cursive;
  font-size: 3rem;
  color: #8B0000;
  letter-spacing: 0.2rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  margin: 0;
}

.logo-decoration {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.decoration-line {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #8B0000, transparent);
}

.decoration-dot {
  width: 8px;
  height: 8px;
  background-color: #8B0000;
  border-radius: 50%;
}

.welcome-section {
  text-align: center;
  padding: 4rem 0;
  position: relative;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(139, 69, 19, 0.2);
  border-radius: 15px;
  padding: 3rem;
  max-width: 600px;
  margin: 0 auto;
  position: relative;
  box-shadow: 0 10px 30px rgba(139, 69, 19, 0.1);
}

.ink-brush {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 30px;
  background: linear-gradient(90deg, transparent, #8B4513, transparent);
  border-radius: 50%;
  filter: blur(5px);
  opacity: 0.6;
}

.welcome-title {
  font-family: 'Ma Shan Zhong', cursive;
  font-size: 2.5rem;
  color: #8B0000;
  margin-bottom: 1rem;
  letter-spacing: 0.1rem;
}

.welcome-subtitle {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.2rem;
  color: #8B4513;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.scroll-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  margin-top: 2rem;
}

.scroll-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 0.9rem;
  color: #8B4513;
  animation: pulse 2s infinite;
}

.scroll-arrow {
  width: 20px;
  height: 20px;
  border-left: 2px solid #8B4513;
  border-bottom: 2px solid #8B4513;
  transform: rotate(-45deg);
  animation: bounce 2s infinite;
}

.content-section {
  padding: 4rem 0;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.content-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(139, 69, 19, 0.15);
  border-radius: 12px;
  padding: 2rem;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(139, 69, 19, 0.08);
}

.content-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(139, 69, 19, 0.15);
  border-color: rgba(139, 69, 19, 0.3);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.icon-text {
  font-family: 'Ma Shan Zhong', cursive;
}

.card-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.3rem;
  color: #8B0000;
  margin: 0;
}

.card-description {
  font-family: 'Noto Serif SC', serif;
  font-size: 1rem;
  color: #5D4037;
  line-height: 1.6;
  margin: 0;
}

.features-section {
  padding: 4rem 0;
  background: linear-gradient(180deg, rgba(255, 248, 220, 0.3) 0%, transparent 100%);
}

.section-title {
  font-family: 'Ma Shan Zhong', cursive;
  font-size: 2rem;
  color: #8B0000;
  text-align: center;
  margin-bottom: 3rem;
  letter-spacing: 0.1rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.feature-item {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(139, 69, 19, 0.15);
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(139, 69, 19, 0.08);
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(139, 69, 19, 0.15);
  border-color: rgba(139, 69, 19, 0.3);
}

.feature-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 2rem;
  margin: 0 auto 1.5rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.feature-icon-text {
  font-family: 'Ma Shan Zhong', cursive;
}

.feature-title {
  font-family: 'Noto Serif SC', serif;
  font-size: 1.2rem;
  color: #8B0000;
  margin: 0 0 1rem;
}

.feature-description {
  font-family: 'Noto Serif SC', serif;
  font-size: 0.95rem;
  color: #5D4037;
  line-height: 1.6;
  margin: 0;
}

.footer {
  margin-top: 4rem;
  padding: 3rem 0;
  text-align: center;
  border-top: 1px solid rgba(139, 69, 19, 0.2);
}

.footer-content {
  position: relative;
}

.footer-text {
  font-family: 'Noto Serif SC', serif;
  font-size: 1rem;
  color: #5D4037;
  margin: 0;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

@keyframes bounce {
  0%, 100% { transform: rotate(-45deg) translateY(0); }
  50% { transform: rotate(-45deg) translateY(5px); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .logo-text {
    font-size: 2.5rem;
  }

  .welcome-title {
    font-size: 2rem;
  }

  .content-grid, .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<!-- 引入中文字体 -->
<style>
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zhong&family=Noto+Serif+SC:wght@400;700&display=swap');
</style>