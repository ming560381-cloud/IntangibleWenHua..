<template>
  <section class="weibo-community-page">
    <div class="container community-shell">
      <header class="weibo-top-card">
        <div class="weibo-search-row">
          <input
            v-model.trim="keyword"
            class="weibo-search-input"
            type="text"
            placeholder="搜索标题、内容或标签"
          />
          <select v-model="postType" class="weibo-type-select">
            <option value="">全部话题</option>
            <option v-for="tag in hotTags" :key="tag" :value="tag">{{ tag }}</option>
          </select>
          <button class="weibo-ghost-btn" @click="resetFilters">重置筛选</button>
        </div>
        <div class="weibo-tag-row">
          <button
            v-for="tag in hotTags"
            :key="tag"
            class="weibo-chip"
            :class="{ active: postType === tag }"
            @click="applyTag(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </header>

      <div class="weibo-layout">
        <main class="weibo-feed">
          <article class="feed-card composer-card">
            <div class="composer-head">
              <div class="avatar">
                <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="用户头像" />
                <span v-else>{{ (currentUser?.username || currentUser?.phone || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="composer-meta">
                <strong>{{ currentUser?.username || currentUser?.phone || '游客' }}</strong>
                <small>说说你最近的非遗体验、学习心得或提问</small>
              </div>
            </div>
            <textarea
              v-model.trim="newPost.content"
              class="composer-textarea"
              placeholder="发个帖子，和大家交流一下..."
            />

            <div v-if="newPost.images.length" class="composer-image-grid">
              <div v-for="(image, index) in newPost.images" :key="image + index" class="composer-image-item">
                <img :src="image" :alt="`上传图片${index + 1}`" />
                <button class="remove-image-btn" @click="removeImage(index)">×</button>
                <button class="cover-btn" :class="{ active: newPost.coverImage === image }" @click="setCoverImage(image)">
                  封面
                </button>
              </div>
            </div>

            <div class="composer-tools">
              <input
                ref="imageUploadInput"
                type="file"
                accept="image/*"
                multiple
                class="hidden-input"
                @change="handleImageUpload"
              />
              <button class="tool-btn" :disabled="uploadingImages || newPost.images.length >= maxImages" @click="triggerImageUpload">
                {{ uploadingImages ? '上传中...' : `图片 ${newPost.images.length}/${maxImages}` }}
              </button>
              <button class="tool-btn" @click="applyTag('在线答疑')">在线答疑</button>
              <button class="tool-btn" @click="applyTag('交流分享')">交流分享</button>
              <button class="publish-btn" :disabled="submitting || uploadingImages" @click="submitPost">
                {{ submitting ? '发布中...' : '发布帖子' }}
              </button>
            </div>
          </article>

          <div v-if="loading" class="feed-card state-card">正在加载社区内容...</div>
          <div v-else-if="filteredPosts.length === 0" class="feed-card state-card">暂无匹配内容，试试换个关键词。</div>

          <article v-for="post in filteredPosts" v-else :key="post.id" class="feed-card post-card">
            <div class="post-head">
              <div class="avatar">
                <img v-if="post.authorAvatar" :src="post.authorAvatar" :alt="post.authorName" />
                <span v-else>{{ post.authorInitial || (post.authorName || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="post-author">
                <strong>{{ post.authorName || '匿名用户' }}</strong>
                <small>{{ formatDateTime(post.createTime) }}</small>
              </div>
              <RouterLink :to="`/community/${post.id}`" class="detail-link">查看详情</RouterLink>
            </div>

            <p class="post-content">{{ post.content }}</p>

            <div v-if="postImages(post).length" class="post-image-grid">
              <img
                v-for="(image, index) in postImages(post).slice(0, 9)"
                :key="image + index"
                :src="image"
                :alt="`${post.title || '帖子'}图片${index + 1}`"
              />
            </div>

            <footer class="post-actions">
              <button class="action-btn">
                💬 {{ post.commentCount || 0 }}
              </button>
              <button class="action-btn" @click="toggleLike(post)" :class="{ liked: post.likedByCurrentUser }">
                ♥ {{ post.likeCount || 0 }}
              </button>
              <RouterLink :to="`/community/${post.id}`" class="action-btn link-btn">进入详情</RouterLink>
            </footer>
          </article>
        </main>

        <aside class="weibo-side">
          <article class="side-card">
            <h3>发布新帖</h3>
            <p>把问题描述得更具体一些，通常更容易得到有帮助的回复。</p>
            <input v-model.trim="newPost.title" class="side-input" placeholder="帖子标题（可选）" />
            <select v-model="newPost.type" class="side-input">
              <option v-for="tag in hotTags" :key="tag" :value="tag">{{ tag }}</option>
            </select>
            <textarea
              v-model.trim="newPost.extraTags"
              class="side-textarea"
              placeholder="补充标签，多个标签用英文逗号分隔"
            />
            <div class="side-actions">
              <button class="publish-btn" :disabled="submitting || uploadingImages" @click="submitPost">发布帖子</button>
              <RouterLink to="/community?tag=在线答疑" class="outline-btn">查看在线答疑</RouterLink>
            </div>
          </article>

          <article class="side-card">
            <h3>社区建议</h3>
            <ul class="tips">
              <li>提问时尽量补充学习阶段、地域背景</li>
              <li>分享体验时可附多图展示过程与成品</li>
              <li>引用资料时建议备注来源便于讨论</li>
            </ul>
          </article>
        </aside>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { formatDateTime, postApi } from '../services/api';
import { readSessionUser, sessionEventName } from '../utils/session';

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const submitting = ref(false);
const uploadingImages = ref(false);
const posts = ref([]);
const keyword = ref('');
const postType = ref('');
const maxImages = 9;
const currentUser = ref(readSessionUser());
const hotTags = ['在线答疑', '交流分享', '体验活动', '数字化展示', '观展心得'];
const newPost = ref({
  title: '',
  content: '',
  type: '交流分享',
  extraTags: '',
  images: [],
  coverImage: ''
});

const imageUploadInput = ref(null);

const splitTags = (tags) => (tags || '').split(',').map((item) => item.trim()).filter(Boolean);

const splitImages = (images) => (images || '').split(',').map((item) => item.trim()).filter(Boolean);

const postImages = (post) => {
  const images = splitImages(post.images);
  if (post.coverImage && !images.includes(post.coverImage)) {
    return [post.coverImage, ...images];
  }
  return images.length ? images : (post.coverImage ? [post.coverImage] : []);
};

const filteredPosts = computed(() => posts.value.filter((post) => {
  const tags = splitTags(post.tags);
  const searchPool = [post.title, post.content, post.tags].filter(Boolean);
  const matchesKeyword = !keyword.value || searchPool.some((text) => text.includes(keyword.value));
  const matchesType = !postType.value || tags.includes(postType.value);
  return matchesKeyword && matchesType;
}));

const syncCurrentUser = () => {
  currentUser.value = readSessionUser();
};

const fetchPosts = async () => {
  loading.value = true;
  try {
    const response = await postApi.getList();
    posts.value = response.data || [];
    posts.value.forEach((post) => {
      if (post.likedByCurrentUser === undefined) {
        post.likedByCurrentUser = false;
      }
    });
  } catch (error) {
    console.error('社区帖子加载失败', error);
  } finally {
    loading.value = false;
  }
};

const applyTag = (tag) => {
  postType.value = tag;
  router.replace({
    path: route.path,
    query: { ...route.query, tag }
  });
};

const resetFilters = () => {
  keyword.value = '';
  postType.value = '';
  const nextQuery = { ...route.query };
  delete nextQuery.tag;
  router.replace({
    path: route.path,
    query: nextQuery
  });
};

const triggerImageUpload = () => {
  if (!currentUser.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }
  imageUploadInput.value?.click();
};

const handleImageUpload = async (event) => {
  const files = Array.from(event.target.files || []);
  if (!files.length) return;

  if (newPost.value.images.length >= maxImages) {
    window.alert(`最多只能上传 ${maxImages} 张图片`);
    return;
  }

  const remain = maxImages - newPost.value.images.length;
  const uploadFiles = files.slice(0, remain);
  uploadingImages.value = true;

  try {
    const uploadResults = await Promise.all(uploadFiles.map(async (file) => {
      const response = await postApi.uploadImage(file);
      return response.data?.url;
    }));
    const successUrls = uploadResults.filter(Boolean);
    if (!successUrls.length) {
      window.alert('图片上传失败，请稍后再试');
      return;
    }
    newPost.value.images.push(...successUrls);
    if (!newPost.value.coverImage) {
      newPost.value.coverImage = successUrls[0];
    }
  } catch (error) {
    console.error('图片上传失败', error);
    window.alert('图片上传失败，请确认 MinIO 服务是否可用');
  } finally {
    uploadingImages.value = false;
    event.target.value = '';
  }
};

const removeImage = (index) => {
  const [removed] = newPost.value.images.splice(index, 1);
  if (removed && newPost.value.coverImage === removed) {
    newPost.value.coverImage = newPost.value.images[0] || '';
  }
};

const setCoverImage = (image) => {
  newPost.value.coverImage = image;
};

const toggleLike = async (post) => {
  if (!currentUser.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }
  try {
    const response = await postApi.like(post.id);
    post.likeCount = response.data.likeCount;
    post.likedByCurrentUser = response.data.liked;
  } catch (error) {
    console.error('帖子点赞操作失败', error);
  }
};

const submitPost = async () => {
  if (!currentUser.value) {
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }
  if (!newPost.value.content) {
    window.alert('请输入帖子内容');
    return;
  }

  const tags = [newPost.value.type, newPost.value.extraTags]
    .filter(Boolean)
    .join(',')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
    .join(',');

  submitting.value = true;
  try {
    await postApi.create({
      title: newPost.value.title || newPost.value.content.substring(0, 50),
      content: newPost.value.content,
      tags,
      authorId: currentUser.value.id,
      authorName: currentUser.value.username || currentUser.value.phone,
      images: newPost.value.images.join(','),
      coverImage: newPost.value.coverImage || newPost.value.images[0] || ''
    });
    newPost.value = {
      title: '',
      content: '',
      type: newPost.value.type,
      extraTags: '',
      images: [],
      coverImage: ''
    };
    await fetchPosts();
  } catch (error) {
    console.error('发布帖子失败', error);
    window.alert('发布失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

watch(() => route.query.tag, (value) => {
  if (typeof value === 'string') {
    postType.value = value;
  }
}, { immediate: true });

onMounted(() => {
  window.addEventListener(sessionEventName(), syncCurrentUser);
  fetchPosts();
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>

<style scoped>
.weibo-community-page {
  background: #f5f7fb;
  min-height: calc(100vh - 120px);
  padding: 20px 0 36px;
}

.community-shell {
  max-width: 1240px;
}

.weibo-top-card,
.feed-card,
.side-card {
  background: #fff;
  border: 1px solid #e6eaf1;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(15, 32, 66, 0.05);
}

.weibo-top-card {
  padding: 16px;
  margin-bottom: 16px;
}

.weibo-search-row {
  display: grid;
  grid-template-columns: 1fr 160px 116px;
  gap: 10px;
}

.weibo-search-input,
.weibo-type-select,
.side-input,
.side-textarea,
.composer-textarea {
  border: 1px solid #dce2ec;
  border-radius: 10px;
  background: #fff;
  color: #1a2433;
}

.weibo-search-input,
.weibo-type-select,
.side-input {
  height: 40px;
  padding: 0 12px;
}

.weibo-ghost-btn,
.tool-btn,
.action-btn,
.outline-btn {
  border: 1px solid #dce2ec;
  background: #f6f8fc;
  color: #4a586b;
  border-radius: 10px;
  padding: 0 14px;
  height: 40px;
  cursor: pointer;
}

.weibo-tag-row {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.weibo-chip {
  border: 1px solid #dde4f0;
  background: #fbfcff;
  color: #3f4f63;
  border-radius: 999px;
  padding: 7px 12px;
  cursor: pointer;
}

.weibo-chip.active {
  border-color: #ff8345;
  background: #fff4ee;
  color: #dd5a15;
}

.weibo-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 16px;
  align-items: start;
}

.weibo-feed {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.composer-card,
.post-card,
.side-card {
  padding: 16px;
}

.composer-head,
.post-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  background: #ff884d;
  color: #fff;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  font-weight: 700;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.composer-meta,
.post-author {
  display: flex;
  flex-direction: column;
}

.composer-meta small,
.post-author small {
  color: #778395;
}

.composer-textarea {
  width: 100%;
  margin-top: 12px;
  min-height: 100px;
  resize: vertical;
  padding: 12px;
}

.composer-image-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.composer-image-item {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e2e8f2;
  background: #f8fbff;
}

.composer-image-item img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  display: block;
}

.remove-image-btn,
.cover-btn {
  position: absolute;
  border: none;
  border-radius: 999px;
  font-size: 12px;
  cursor: pointer;
}

.remove-image-btn {
  top: 8px;
  right: 8px;
  width: 22px;
  height: 22px;
  background: rgba(0, 0, 0, 0.58);
  color: #fff;
}

.cover-btn {
  left: 8px;
  bottom: 8px;
  padding: 2px 8px;
  background: rgba(0, 0, 0, 0.52);
  color: #fff;
}

.cover-btn.active {
  background: #ff6f2e;
}

.composer-tools {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.hidden-input {
  display: none;
}

.publish-btn {
  margin-left: auto;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #ff8547, #e45c1b);
  color: #fff;
  height: 40px;
  padding: 0 20px;
  cursor: pointer;
}

.publish-btn:disabled,
.tool-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.post-head {
  margin-bottom: 10px;
}

.detail-link {
  margin-left: auto;
  color: #66768a;
  text-decoration: none;
}

.post-content {
  white-space: pre-wrap;
  color: #213043;
  line-height: 1.7;
}

.post-image-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}

.post-image-grid img {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 8px;
  object-fit: cover;
}

.post-actions {
  margin-top: 12px;
  border-top: 1px solid #edf1f7;
  padding-top: 10px;
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  text-align: center;
  text-decoration: none;
  line-height: 40px;
}

.action-btn.liked {
  color: #da5b2a;
  background: #fff3ed;
  border-color: #ffccba;
}

.link-btn {
  display: inline-block;
}

.state-card {
  text-align: center;
  color: #79879a;
  padding: 20px;
}

.weibo-side {
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: sticky;
  top: 92px;
}

.side-card h3 {
  margin: 0 0 10px;
}

.side-card p {
  margin: 0 0 12px;
  color: #6f7d91;
  line-height: 1.6;
}

.side-textarea {
  width: 100%;
  min-height: 74px;
  padding: 10px;
  resize: vertical;
  margin-top: 10px;
}

.side-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.outline-btn {
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.tips {
  margin: 0;
  padding-left: 18px;
  color: #465569;
  line-height: 1.7;
}

@media (max-width: 1024px) {
  .weibo-layout {
    grid-template-columns: 1fr;
  }

  .weibo-side {
    position: static;
  }
}

@media (max-width: 768px) {
  .weibo-search-row {
    grid-template-columns: 1fr;
  }

  .composer-tools {
    flex-wrap: wrap;
  }

  .publish-btn {
    margin-left: 0;
  }
}
</style>