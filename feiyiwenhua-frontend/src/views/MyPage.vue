<template>
  <section class="page-section">
    <div class="container">
      <section class="page-banner about-banner" style="margin-bottom: 26px;">
        <span class="section-eyebrow">Personal Center</span>
        <h1>我的个人中心</h1>
        <p>
          在这里管理您的个人信息，查看您的活动报名记录，以及参与社区互动。
        </p>
      </section>

      <div class="grid-2">
        <!-- 个人信息修改 -->
        <article class="detail-panel">
          <h2>个人信息</h2>
          <div class="form-grid">
            <div class="avatar-upload-section">
              <div class="avatar-preview">
                <img 
                  v-if="user.avatar" 
                  :src="user.avatar" 
                  alt="头像"
                  class="avatar-image"
                />
                <div v-else class="avatar-placeholder">
                  {{ user.getInitial() }}
                </div>
                <input 
                  type="file" 
                  id="avatar-upload" 
                  class="avatar-input"
                  @change="handleAvatarUpload"
                  accept="image/*"
                />
                <label for="avatar-upload" class="avatar-upload-btn">
                  更换头像
                </label>
              </div>
              <p v-if="avatarMessage" class="muted" style="margin-top: 10px;">{{ avatarMessage }}</p>
            </div>
            
            <div class="form-field">
              <label class="field-label">用户名</label>
              <input 
                v-model="form.username" 
                class="field" 
                type="text" 
                placeholder="请输入用户名"
              />
            </div>
            
            <div class="form-field">
              <label class="field-label">手机号</label>
              <input 
                v-model="form.phone" 
                class="field" 
                type="text" 
                placeholder="请输入手机号"
                disabled
              />
              <p class="muted" style="margin-top: 6px; font-size: 12px;">手机号不可修改</p>
            </div>
          </div>
          <div class="inline-actions" style="margin-top: 20px;">
            <button class="solid-button" @click="updateProfile">保存修改</button>
          </div>
          <p v-if="message" class="muted" style="margin-top: 10px;">{{ message }}</p>
        </article>

        <!-- 报名活动记录 -->
        <article class="detail-panel">
          <h2>我的报名</h2>
          <div v-if="loading" class="loading-state">正在加载报名记录...</div>
          <div v-else-if="registrations.length === 0" class="empty-state">暂无报名记录</div>
          <div v-else class="registration-list">
            <div v-for="registration in registrations" :key="registration.id" class="registration-item">
              <div class="registration-header">
                <h3>{{ registration.activityTitle }}</h3>
                <span class="status-chip">{{ registration.statusText }}</span>
              </div>
              <div class="registration-info">
                <p>报名时间：{{ formatDateTime(registration.createTime) }}</p>
                <p>报名人：{{ registration.participantName }}</p>
                <p>联系电话：{{ registration.phone }}</p>
                <p v-if="registration.message" class="muted">备注：{{ registration.message }}</p>
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { userApi, activityApi, formatDateTime } from '../services/api';
import { readSessionUser, sessionEventName } from '../utils/session';

const router = useRouter();
const user = ref(readSessionUser());
const loading = ref(false);
const message = ref('');
const avatarMessage = ref('');
const form = ref({
  username: '',
  phone: ''
});
const registrations = ref([]);

const fetchUserProfile = async () => {
  try {
    const response = await userApi.getProfile();
    if (response.data.success) {
      user.value = response.data.data;
      form.value.username = user.value.username || '';
      form.value.phone = user.value.phone || '';
    }
  } catch (error) {
    console.error('获取用户信息失败', error);
    message.value = '获取用户信息失败，请重新登录';
  }
};

const fetchUserRegistrations = async () => {
  if (!user.value) return;
  
  loading.value = true;
  try {
    const response = await activityApi.getUserRegistrations();
    registrations.value = response.data || [];
  } catch (error) {
    console.error('获取报名记录失败', error);
  } finally {
    loading.value = false;
  }
};

const updateProfile = async () => {
  if (!form.value.username) {
    message.value = '请输入用户名';
    return;
  }
  
  try {
    const response = await userApi.updateProfile({
      username: form.value.username
    });
    if (response.data.success) {
      user.value = response.data.data;
      message.value = '更新成功';
    } else {
      message.value = response.data.message || '更新失败';
    }
  } catch (error) {
    console.error('更新失败', error);
    message.value = '更新失败，请稍后再试';
  }
};

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  if (file.size > 2 * 1024 * 1024) {
    avatarMessage.value = '头像文件大小不能超过2MB';
    return;
  }
  
  const formData = new FormData();
  formData.append('avatar', file);
  
  try {
    const response = await userApi.uploadAvatar(formData);
    if (response.data.success) {
      user.value.avatar = response.data.data.avatar;
      avatarMessage.value = '头像上传成功';
    } else {
      avatarMessage.value = response.data.message || '头像上传失败';
    }
  } catch (error) {
    console.error('头像上传失败', error);
    avatarMessage.value = '头像上传失败，请稍后再试';
  }
};

onMounted(() => {
  if (!user.value) {
    router.push('/login');
    return;
  }
  fetchUserProfile();
  fetchUserRegistrations();
});
</script>

<style scoped>
.form-field {
  margin-bottom: 14px;
}

.field-label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: var(--text);
  font-size: 13px;
}

.avatar-upload-section {
  margin-bottom: 20px;
  text-align: center;
}

.avatar-preview {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 10px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--line-strong);
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), #b14c31);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  border: 1px solid var(--line-strong);
}

.avatar-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 11px;
  cursor: pointer;
  z-index: 1;
  transition: all 0.2s ease;
}

.avatar-upload-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

.registration-list {
  display: grid;
  gap: 14px;
}

.registration-item {
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: 8px;
  background: var(--paper);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.registration-item:hover {
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.registration-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 10px;
}

.registration-header h3 {
  margin: 0;
  font-size: 14px;
  flex: 1;
  min-width: 0;
  line-height: 1.4;
}

.registration-info {
  display: grid;
  gap: 6px;
}

.registration-info p {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
}

.status-chip {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(184, 138, 66, 0.13);
  color: #7b571d;
  white-space: nowrap;
}
</style>