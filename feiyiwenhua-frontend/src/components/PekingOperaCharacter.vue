<template>
  <div class="classical-container" @click="toggleChat">
    <div class="square-seal">
      <svg viewBox="0 0 100 100" class="seal-svg">
        <rect x="2" y="2" width="96" height="96" fill="none" stroke="#8b4513" stroke-width="2"/>
        <rect x="5" y="5" width="90" height="90" fill="none" stroke="#8b4513" stroke-width="1" stroke-dasharray="2,2"/>
        <text x="50" y="28" class="seal-char" text-anchor="middle">满船明月从此去</text>
        <text x="50" y="55" class="seal-char" text-anchor="middle">本是江湖寂寞人</text>
      </svg>
    </div>
    
    <div class="chat-bubble" v-if="showBubble">
      <span>你好呀！我是浏星~</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PekingOperaCharacter',
  data() {
    return {
      showBubble: false
    }
  },
  mounted() {
    setTimeout(() => {
      this.showBubble = true
      setTimeout(() => {
        this.showBubble = false
      }, 3000)
    }, 1000)
  },
  methods: {
    toggleChat() {
      this.showBubble = false
      this.$emit('toggle-chat')
    }
  }
}
</script>

<style scoped>
.classical-container {
  width: 70px;
  height: 70px;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.classical-container:hover {
  transform: scale(1.1);
}

.square-seal {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #f5e6d3 0%, #e8d5c4 50%, #d4c4b0 100%);
  border: 3px solid #8b4513;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    inset 0 0 10px rgba(139, 69, 19, 0.2),
    3px 3px 6px rgba(0, 0, 0, 0.25);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.classical-container:hover .square-seal {
  animation: bounce 0.5s ease;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.seal-svg {
  width: 60px;
  height: 60px;
}

.seal-char {
  font-family: "FangSong", "STFangSong", "仿宋", serif;
  font-size: 9px;
  fill: #8b0000;
  font-weight: bold;
}

.chat-bubble {
  position: absolute;
  top: -45px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f5e6d3 0%, #e8d5c4 100%);
  padding: 8px 12px;
  border-radius: 6px;
  border: 2px solid #8b4513;
  font-size: 12px;
  color: #5d4037;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  animation: bubblePop 0.3s ease;
  z-index: 10;
}

.chat-bubble::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid #8b4513;
}

.chat-bubble::before {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid #f5e6d3;
  z-index: 1;
}

@keyframes bubblePop {
  0% { opacity: 0; transform: translateX(-50%) scale(0.5); }
  100% { opacity: 1; transform: translateX(-50%) scale(1); }
}
</style>
