<template>
  <section class="my-profile">
    <!-- 顶部个人信息 -->
    <div class="profile-header">
      <div class="profile-info">
        <div class="avatar-container">
          <img :src="userInfo.avatar || 'https://via.placeholder.com/100'" alt="用户头像" class="avatar" />
          <button class="avatar-edit" @click="showAvatarUpload = true">
            <span class="edit-icon">📷</span>
          </button>
        </div>
        <div class="user-details">
          <h2 class="username">{{ userInfo.username || userInfo.phone || '未登录' }}</h2>
          <p class="bio">{{ userInfo.bio || '暂无个人简介' }}</p>
          <button class="edit-profile-btn" @click="showEditProfile = true">编辑资料</button>
        </div>
      </div>
      <div class="profile-stats">
        <div class="stat-item">
          <span class="stat-number">{{ userStats.posts || 0 }}</span>
          <span class="stat-label">作品</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ userStats.followers || 0 }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ userStats.following || 0 }}</span>
          <span class="stat-label">关注</span>
        </div>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="profile-nav">
      <button 
        v-for="tab in navTabs" 
        :key="tab.id"
        class="nav-tab"
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        <span class="tab-icon">{{ tab.icon }}</span>
        <span class="tab-label">{{ tab.label }}</span>
      </button>
    </div>

    <!-- 内容区域 -->
    <div class="profile-content">
      <!-- 我的作品 -->
      <div v-if="activeTab === 'posts'" class="content-section">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="userPosts.length === 0" class="empty-state">暂无发布内容</div>
        <div v-else class="post-grid">
          <div v-for="post in userPosts" :key="post.id" class="post-item">
            <img :src="post.coverImage || (post.images ? post.images.split(',')[0] : 'https://via.placeholder.com/300')" alt="帖子图片" class="post-image" @click="goToPostDetail(post.id)" />
            <div class="post-overlay">
              <span class="post-stats">
                <span class="stat-item">
                  <span class="stat-icon">♥</span>
                  <span class="stat-count">{{ post.likeCount || 0 }}</span>
                </span>
                <span class="stat-item" @click.stop="goToCommentSection(post.id)">
                  <span class="stat-icon">💬</span>
                  <span class="stat-count">{{ post.commentCount || 0 }}</span>
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 点赞的内容 -->
      <div v-if="activeTab === 'likes'" class="content-section">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="likedPosts.length === 0" class="empty-state">暂无点赞内容</div>
        <div v-else class="post-grid">
          <div v-for="post in likedPosts" :key="post.id" class="post-item">
            <img :src="post.coverImage || (post.images ? post.images.split(',')[0] : 'https://via.placeholder.com/300')" alt="帖子图片" class="post-image" @click="goToPostDetail(post.id)" />
            <div class="post-overlay">
              <span class="post-stats">
                <span class="stat-item">
                  <span class="stat-icon">♥</span>
                  <span class="stat-count">{{ post.likeCount || 0 }}</span>
                </span>
                <span class="stat-item" @click.stop="goToCommentSection(post.id)">
                  <span class="stat-icon">💬</span>
                  <span class="stat-count">{{ post.commentCount || 0 }}</span>
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 收藏的内容 -->
      <div v-if="activeTab === 'collections'" class="content-section">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="collectedPosts.length === 0" class="empty-state">暂无收藏内容</div>
        <div v-else class="post-grid">
          <div v-for="post in collectedPosts" :key="post.id" class="post-item">
            <img :src="post.coverImage || (post.images ? post.images.split(',')[0] : 'https://via.placeholder.com/300')" alt="帖子图片" class="post-image" @click="goToPostDetail(post.id)" />
            <div class="post-overlay">
              <span class="post-stats">
                <span class="stat-item">
                  <span class="stat-icon">♥</span>
                  <span class="stat-count">{{ post.likeCount || 0 }}</span>
                </span>
                <span class="stat-item" @click.stop="goToCommentSection(post.id)">
                  <span class="stat-icon">💬</span>
                  <span class="stat-count">{{ post.commentCount || 0 }}</span>
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 已报名活动 -->
      <div v-if="activeTab === 'registrations'" class="content-section">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else-if="userRegistrations.length === 0" class="empty-state">暂无报名记录</div>
        <div v-else class="registration-list">
          <div v-for="item in userRegistrations" :key="item.id" class="registration-item">
            <div class="registration-head">
              <h3 class="registration-title">{{ item.activityTitle }}</h3>
              <span class="registration-status">{{ item.statusText }}</span>
            </div>
            <p class="registration-meta">报名时间：{{ formatDateTime(item.createTime) }}</p>
            <p class="registration-meta">手机号：{{ item.phone }}</p>
            <p v-if="item.message" class="registration-meta">备注：{{ item.message }}</p>
            <div class="registration-actions">
              <button class="view-activity-btn" @click="goToActivity(item.activityId)">查看活动</button>
              <button
                v-if="item.status === 1"
                class="cancel-registration-btn"
                @click="cancelRegistration(item)"
              >
                退订报名
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 个人信息修改 -->
      <div v-if="activeTab === 'settings'" class="content-section">
        <div class="settings-form">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="editForm.username" type="text" class="form-input" placeholder="输入用户名" />
          </div>
          <div class="form-group">
            <label>个人简介</label>
            <textarea v-model="editForm.bio" class="form-textarea" placeholder="输入个人简介" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="editForm.phone" type="tel" class="form-input" placeholder="输入手机号" />
          </div>
          <div class="form-actions">
            <button class="cancel-btn" @click="cancelEdit">取消</button>
            <button class="save-btn" @click="saveProfile">保存</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <div v-if="showEditProfile" class="modal-overlay" @click="showEditProfile = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑个人资料</h3>
          <button class="close-btn" @click="showEditProfile = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="editForm.username" type="text" class="form-input" placeholder="输入用户名" />
          </div>
          <div class="form-group">
            <label>个人简介</label>
            <textarea v-model="editForm.bio" class="form-textarea" placeholder="输入个人简介" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="editForm.phone" type="tel" class="form-input" placeholder="输入手机号" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showEditProfile = false">取消</button>
          <button class="save-btn" @click="saveProfile">保存</button>
        </div>
      </div>
    </div>

    <!-- 头像上传弹窗 -->
    <div v-if="showAvatarUpload" class="modal-overlay" @click="showAvatarUpload = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>更换头像</h3>
          <button class="close-btn" @click="showAvatarUpload = false">×</button>
        </div>
        <div class="modal-body">
          <div class="avatar-upload-area">
            <input type="file" ref="avatarInput" accept="image/*" @change="handleAvatarUpload" style="display: none;" />
            <div class="upload-button" @click="$refs.avatarInput.click()">
              <span class="upload-icon">📷</span>
              <span class="upload-text">选择图片</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { userApi, postApi, activityApi, formatDateTime } from '../services/api';
import { readSessionUser } from '../utils/session';

const router = useRouter();
const currentUser = ref(readSessionUser());
console.log('currentUser:', currentUser.value);
const userInfo = ref({});
const userStats = ref({});
const userPosts = ref([]);
const likedPosts = ref([]);
const collectedPosts = ref([]);
const userRegistrations = ref([]);
const loading = ref(false);
const activeTab = ref('posts');
const showEditProfile = ref(false);
const showAvatarUpload = ref(false);
const editForm = ref({});

const navTabs = [
  { id: 'posts', icon: '📝', label: '作品' },
  { id: 'registrations', icon: '📅', label: '已报名' },
  { id: 'likes', icon: '♥', label: '点赞' },
  { id: 'collections', icon: '⭐', label: '收藏' },
  { id: 'settings', icon: '⚙️', label: '设置' }
];

const fetchUserProfile = async () => {
  if (!currentUser.value) return;
  
  loading.value = true;
  try {
    const response = await userApi.getProfile();
    if (response.data.success) {
      userInfo.value = response.data.data || {};
      editForm.value = { ...userInfo.value };
    } else {
      // 后端返回失败，使用localStorage中的用户信息
      userInfo.value = currentUser.value || {};
      editForm.value = { ...userInfo.value };
    }
  } catch (error) {
    console.error('获取用户信息失败', error);
    // 网络错误，使用localStorage中的用户信息
    userInfo.value = currentUser.value || {};
    editForm.value = { ...userInfo.value };
  } finally {
    loading.value = false;
  }
};

const fetchUserPosts = async () => {
  if (!currentUser.value) return;
  
  loading.value = true;
  try {
    const response = await postApi.getList();
    userPosts.value = response.data.filter(post => post.authorId === currentUser.value.id) || [];
  } catch (error) {
    console.error('获取用户帖子失败', error);
  } finally {
    loading.value = false;
  }
};

const fetchLikedPosts = async () => {
  if (!currentUser.value) return;
  
  loading.value = true;
  try {
    console.log('开始获取点赞帖子');
    const response = await postApi.getList();
    console.log('获取帖子列表成功:', response.data);
    likedPosts.value = response.data.filter(post => post.likedByCurrentUser) || [];
    console.log('过滤后的点赞帖子:', likedPosts.value);
  } catch (error) {
    console.error('获取点赞帖子失败', error);
  } finally {
    loading.value = false;
  }
};

const fetchUserRegistrations = async () => {
  if (!currentUser.value) return;

  loading.value = true;
  try {
    const response = await activityApi.getUserRegistrations();
    userRegistrations.value = response.data || [];
  } catch (error) {
    console.error('获取报名记录失败', error);
  } finally {
    loading.value = false;
  }
};

const saveProfile = async () => {
  if (!currentUser.value) return;
  
  try {
    await userApi.updateProfile(editForm.value);
    userInfo.value = { ...editForm.value };
    showEditProfile.value = false;
  } catch (error) {
    console.error('更新用户信息失败', error);
  }
};

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await userApi.uploadAvatar(formData);
    userInfo.value.avatar = response.data.avatar;
    showAvatarUpload.value = false;
  } catch (error) {
    console.error('上传头像失败', error);
  }
};

const cancelEdit = () => {
  editForm.value = { ...userInfo.value };
  showEditProfile.value = false;
};

const goToActivity = (activityId) => {
  router.push({ path: '/activity-detail', query: { id: String(activityId) } });
};

const goToPostDetail = (postId) => {
  router.push({ path: `/community/${postId}` });
};

const goToCommentSection = (postId) => {
  router.push({ path: `/community/${postId}#comments` });
};

const cancelRegistration = async (item) => {
  const confirmed = window.confirm(`确认退订“${item.activityTitle}”吗？`);
  if (!confirmed) return;
  try {
    await activityApi.cancelRegistration(item.id);
    await fetchUserRegistrations();
  } catch (error) {
    console.error('取消报名失败', error);
    window.alert(typeof error?.response?.data === 'string' ? error.response.data : '取消报名失败，请稍后再试');
  }
};

onMounted(() => {
  fetchUserProfile();
  fetchUserPosts();
  fetchLikedPosts();
  fetchUserRegistrations();
});
</script>

<style scoped>
.my-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
}

.avatar-container {
  position: relative;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #ff4757;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #ff4757;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.bio {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.edit-profile-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-profile-btn:hover {
  background: #f5f5f5;
}

.profile-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.profile-nav {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.nav-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
}

.nav-tab.active {
  color: #ff4757;
  border-bottom: 2px solid #ff4757;
}

.tab-icon {
  font-size: 18px;
}

.content-section {
  min-height: 400px;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.registration-list {
  display: grid;
  gap: 12px;
}

.registration-item {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 14px;
}

.registration-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.registration-title {
  margin: 0;
  font-size: 16px;
}

.registration-status {
  background: rgba(184, 138, 66, 0.15);
  color: #7b571d;
  border-radius: 999px;
  padding: 2px 10px;
  font-size: 12px;
}

.registration-meta {
  margin: 4px 0;
  color: #666;
  font-size: 13px;
}

.view-activity-btn {
  margin-top: 10px;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  background: #ff4757;
  color: #fff;
  cursor: pointer;
}

.registration-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.cancel-registration-btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
  color: #555;
  cursor: pointer;
}

.post-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f0f0;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.post-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  padding: 10px;
  color: white;
}

.post-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.stat-icon {
  font-size: 14px;
}

.loading-state,
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  font-size: 16px;
  color: #999;
  background: #f9f9f9;
  border-radius: 8px;
}

.settings-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #ff4757;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.cancel-btn,
.save-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: white;
  color: #666;
}

.save-btn {
  background: #ff4757;
  color: white;
  border-color: #ff4757;
}

.save-btn:hover {
  background: #ff3742;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  border: none;
  background: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.avatar-upload-area {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.upload-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 30px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-button:hover {
  border-color: #ff4757;
  background: #f9f0f0;
}

.upload-icon {
  font-size: 40px;
}

.upload-text {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-stats {
    gap: 20px;
  }
  
  .profile-nav {
    gap: 20px;
  }
  
  .post-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>