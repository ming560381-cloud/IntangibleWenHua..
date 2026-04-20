<template>
  <section class="page-section">
    <div class="container detail-layout">
      <div class="list-stack">
        <div v-if="loading" class="loading-state section-card">正在加载帖子详情...</div>
        <div v-else-if="!post" class="empty-state section-card">帖子不存在或已被删除。</div>
        <template v-else>
          <article class="detail-panel">
            <div class="meta-row">
              <span v-for="tag in splitTags(post.tags)" :key="tag" class="meta-chip">{{ tag }}</span>
            </div>
            <h1>{{ post.title }}</h1>
            <div class="post-author-row">
              <div class="comment-avatar">
                <img v-if="post.authorAvatar" :src="post.authorAvatar" :alt="post.authorName" />
                <span v-else class="avatar-initial">{{ post.authorInitial || (post.authorName || '?').charAt(0).toUpperCase() }}</span>
              </div>
              <div class="post-meta" style="margin-bottom: 0;">
                <span>{{ post.authorName || '匿名用户' }}</span>
                <span>{{ formatDateTime(post.createTime) }}</span>
                <span>浏览 {{ post.viewCount || 0 }}</span>
                <span>点赞 {{ post.likeCount || 0 }}</span>
              </div>
            </div>
            <div v-if="detailImages.length > 0" class="detail-images-wrap">
              <div class="detail-images-head">图片 {{ detailImages.length }} 张</div>
              <div class="detail-images-grid">
                <img
                  v-for="(img, index) in detailImages"
                  :key="img + index"
                  :src="img"
                  :alt="`${post.title || '帖子'}的图片 ${index + 1}`"
                  class="detail-image-item"
                />
              </div>
            </div>
            <p>{{ post.content }}</p>
            <div class="inline-actions" style="margin-top: 18px;">
              <button class="action-button" @click="toggleLike" :class="{ 'liked': post.likedByCurrentUser }">
                <span class="action-icon like-icon">♥</span>
                <span class="action-count">{{ post.likeCount || 0 }}</span>
              </button>
              <RouterLink class="text-button" to="/community">返回社区</RouterLink>
            </div>
          </article>

          <article class="detail-panel" id="comments">
            <h3>留言评论</h3>
            <div v-if="currentUser" class="form-panel" style="padding: 18px; box-shadow: none; margin-bottom: 18px;">
              <textarea v-model.trim="commentContent" class="textarea" placeholder="输入你的评论或补充问题"></textarea>
              <div class="inline-actions" style="margin-top: 12px;">
                <button class="solid-button" @click="submitComment">发表评论</button>
              </div>
            </div>
            <div v-else class="section-card" style="padding: 18px; box-shadow: none; margin-bottom: 18px;">
              <p class="muted">登录后可发表评论和回复。</p>
              <RouterLink class="solid-button" to="/login">登录后评论</RouterLink>
            </div>

            <div v-if="commentsLoading" class="loading-state">正在加载评论...</div>
            <div v-else-if="comments.length === 0" class="empty-state">还没有评论，欢迎成为第一个发言的人。</div>
            <div v-else class="comment-list">
              <article v-for="comment in comments" :key="comment.id" class="comment-card">
                <div class="comment-header">
                  <div class="comment-avatar">
                    <img v-if="comment.userAvatar" :src="comment.userAvatar" :alt="comment.userName" />
                    <span v-else class="avatar-initial">{{ comment.userInitial || (comment.userName || '?').charAt(0).toUpperCase() }}</span>
                  </div>
                  <div class="comment-info">
                    <span class="comment-user-name">{{ comment.userName || '匿名用户' }}</span>
                    <span class="comment-time">{{ formatDateTime(comment.createTime) }}</span>
                  </div>
                </div>
                <p class="comment-body">{{ comment.content }}</p>
                <div class="inline-actions">
                  <button class="action-button" @click="toggleLikeComment(comment)" :class="{ 'liked': comment.isLiked }">
                    <span class="action-icon like-icon">♥</span>
                    <span class="action-count">{{ comment.likeCount || 0 }}</span>
                  </button>
                  <button v-if="currentUser" class="text-button" @click="replyTo(comment.id, null)">回复</button>
                </div>

                <div v-if="comment.replies?.length" class="reply-list">
                  <article v-for="reply in comment.replies" :key="reply.id" class="comment-card" style="box-shadow: none;">
                    <div class="comment-header">
                      <div class="comment-avatar small">
                        <img v-if="reply.userAvatar" :src="reply.userAvatar" :alt="reply.userName" />
                        <span v-else class="avatar-initial">{{ reply.userInitial || (reply.userName || '?').charAt(0).toUpperCase() }}</span>
                      </div>
                      <div class="comment-info">
                        <span class="comment-user-name">{{ reply.userName || '匿名用户' }}</span>
                        <span v-if="reply.replyUserName" class="reply-arrow">&#9654;</span>
                        <span v-if="reply.replyUserName" class="reply-at">@{{ reply.replyUserName }}</span>
                        <span class="comment-time">{{ formatDateTime(reply.createTime) }}</span>
                      </div>
                    </div>
                    <p class="comment-body">{{ reply.content }}</p>
                    <div class="inline-actions">
                      <button class="action-button" @click="toggleLikeComment(reply)" :class="{ 'liked': reply.isLiked }">
                        <span class="action-icon like-icon">♥</span>
                        <span class="action-count">{{ reply.likeCount || 0 }}</span>
                      </button>
                      <button v-if="currentUser" class="text-button" @click="replyTo(comment.id, reply)">回复</button>
                    </div>
                  </article>
                </div>

                <div v-if="replyingTo === comment.id" class="form-panel" style="padding: 16px; box-shadow: none; margin-top: 12px;">
                  <div v-if="replyTarget" class="reply-hint">回复 @{{ replyTarget.userName || '匿名用户' }}</div>
                  <textarea v-model.trim="replyContent" class="textarea" :placeholder="replyTarget ? `回复 @${replyTarget.userName || '匿名用户'}` : '输入回复内容'"></textarea>
                  <div class="inline-actions" style="margin-top: 12px;">
                    <button class="solid-button" @click="submitReply(comment.id)">提交回复</button>
                    <button class="ghost-button" @click="cancelReply">取消</button>
                  </div>
                </div>
              </article>
            </div>
          </article>
        </template>
      </div>

      <aside class="sticky-side list-stack">
        <article class="detail-panel">
          <h3>答疑提示</h3>
          <p>如果这是一个“在线答疑”帖子，建议在评论里描述想了解的技艺细节、学习阶段和活动诉求，方便获得更具体的回复。</p>
        </article>
      </aside>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { commentApi, formatDateTime, postApi } from '../services/api';
import { readSessionUser, sessionEventName } from '../utils/session';

const route = useRoute();
const router = useRouter();
const post = ref(null);
const loading = ref(true);
const commentsLoading = ref(true);
const comments = ref([]);
const currentUser = ref(readSessionUser());
const commentContent = ref('');
const replyContent = ref('');
const replyingTo = ref(null);
const replyTarget = ref(null); // 被回复的子评论对象（null 表示直接回复顶级评论）

const splitTags = (tags) => (tags || '').split(',').map((item) => item.trim()).filter(Boolean);

const splitImages = (images) => (images || '').split(',').map((item) => item.trim()).filter(Boolean);

const detailImages = computed(() => {
  if (!post.value) return [];
  const images = splitImages(post.value.images);
  if (post.value.coverImage && !images.includes(post.value.coverImage)) {
    return [post.value.coverImage, ...images].slice(0, 9);
  }
  if (images.length > 0) {
    return images.slice(0, 9);
  }
  return post.value.coverImage ? [post.value.coverImage] : [];
});

const fetchPost = async () => {
  loading.value = true;
  try {
    const response = await postApi.getDetail(route.params.id);
    post.value = response.data;
    // 确保帖子有likedByCurrentUser字段
    if (post.value && post.value.likedByCurrentUser === undefined) {
      post.value.likedByCurrentUser = false;
    }
  } catch (error) {
    console.error('帖子详情加载失败', error);
  } finally {
    loading.value = false;
  }
};

const fetchComments = async () => {
  commentsLoading.value = true;
  try {
    const response = await commentApi.getByPost(route.params.id);
    const topLevel = response.data || [];
    const withReplies = await Promise.all(topLevel.map(async (item) => {
      const repliesRes = await commentApi.getReplies(item.id);
      return { ...item, replies: repliesRes.data || [] };
    }));
    comments.value = withReplies;
  } catch (error) {
    console.error('评论加载失败', error);
    comments.value = [];
  } finally {
    commentsLoading.value = false;
  }
};

const toggleLike = async () => {
  if (!currentUser.value) {
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  if (!post.value) return;
  try {
    console.log('开始点赞操作，帖子ID:', post.value.id);
    const response = await postApi.like(post.value.id);
    console.log('点赞操作响应:', response);
    post.value.likeCount = response.data.likeCount;
    post.value.likedByCurrentUser = response.data.liked;
    console.log('点赞操作后，帖子点赞数:', post.value.likeCount, '点赞状态:', post.value.likedByCurrentUser);
  } catch (error) {
    console.error('帖子点赞操作失败', error);
  }
};

const toggleLikeComment = async (target) => {
  if (!currentUser.value) {
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  try {
    console.log('开始评论点赞操作，评论ID:', target.id);
    const response = await commentApi.like(target.id);
    console.log('评论点赞操作响应:', response);
    target.likeCount = response.data.likeCount;
    target.isLiked = response.data.liked;
    console.log('评论点赞操作后，评论点赞数:', target.likeCount, '点赞状态:', target.isLiked);
  } catch (error) {
    console.error('评论点赞操作失败', error);
  }
};

const submitComment = async () => {
  if (!currentUser.value) {
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  if (!commentContent.value) return;
  try {
    await commentApi.create({
      postId: Number(route.params.id),
      userId: currentUser.value.id,
      userName: currentUser.value.username || currentUser.value.phone,
      content: commentContent.value,
      parentId: 0
    });
    commentContent.value = '';
    await fetchComments();
  } catch (error) {
    console.error('发表评论失败', error);
  }
};

const replyTo = (topCommentId, targetReply) => {
  replyingTo.value = topCommentId;
  replyTarget.value = targetReply; // null = 直接回复顶级评论
  replyContent.value = '';
};

const cancelReply = () => {
  replyingTo.value = null;
  replyTarget.value = null;
  replyContent.value = '';
};

const submitReply = async (topCommentId) => {
  if (!currentUser.value) {
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  if (!replyContent.value) return;
  try {
    const payload = {
      postId: Number(route.params.id),
      userId: currentUser.value.id,
      userName: currentUser.value.username || currentUser.value.phone,
      content: replyContent.value,
      parentId: topCommentId
    };
    // 如果是回复子评论，携带被回复人信息
    if (replyTarget.value) {
      payload.replyUserId = replyTarget.value.userId;
      payload.replyUserName = replyTarget.value.userName || '匿名用户';
    }
    await commentApi.create(payload);
    cancelReply();
    await fetchComments();
  } catch (error) {
    console.error('回复评论失败', error);
  }
};

const syncCurrentUser = () => {
  currentUser.value = readSessionUser();
};

onMounted(async () => {
  window.addEventListener(sessionEventName(), syncCurrentUser);
  await Promise.all([fetchPost(), fetchComments()]);
  
  // 检查URL是否包含comments锚点，如果有则滚动到评论区
  if (window.location.hash === '#comments') {
    setTimeout(() => {
      const commentsElement = document.getElementById('comments');
      if (commentsElement) {
        commentsElement.scrollIntoView({ behavior: 'smooth' });
      }
    }, 100);
  }
});

onUnmounted(() => {
  window.removeEventListener(sessionEventName(), syncCurrentUser);
});
</script>

<style scoped>
.detail-images-wrap {
  margin: 14px 0 18px;
}

.detail-images-head {
  margin-bottom: 10px;
  color: #6f7f93;
  font-size: 14px;
}

.detail-images-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  max-width: 660px;
}

.detail-image-item {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e6ecf4;
  background: #f6f8fc;
  box-shadow: 0 4px 12px rgba(22, 37, 64, 0.08);
}

@media (max-width: 768px) {
  .detail-images-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }
}
</style>
