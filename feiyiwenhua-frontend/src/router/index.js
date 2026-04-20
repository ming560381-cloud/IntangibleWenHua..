import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/HomePage.vue';
import HeritageList from '../views/HeritageArchive.vue';
import HeritageDetail from '../views/HeritageDetailPage.vue';
import MasterList from '../views/MasterGallery.vue';
import MasterDetail from '../views/MasterDetail.vue';
import Community from '../views/CommunityHub.vue';
import CommunityDetail from '../views/CommunityDetail.vue';
import Activities from '../views/ActivitySchedule.vue';
import ActivityDetail from '../views/ActivityDetail.vue';
import About from '../views/AboutStudio.vue';
import Contact from '../views/ContactDesk.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import MyPage from '../views/My.vue';
import { userApi } from '../services/api';
import { clearSessionUser, isAuthenticated, saveSessionUser } from '../utils/session';

const routes = [
  { path: '/', name: 'home', component: Home, meta: { theme: 'home', transition: 'page-soft' } },
  { path: '/heritage', name: 'heritage', component: HeritageList, meta: { theme: 'heritage', transition: 'page-slide' } },
  { path: '/heritage/:id', name: 'heritage-detail', component: HeritageDetail, props: true, meta: { theme: 'heritage', transition: 'page-slide' } },
  { path: '/master', name: 'master', component: MasterList, meta: { theme: 'master', transition: 'page-slide' } },
  { path: '/master/:id', name: 'master-detail', component: MasterDetail, props: true, meta: { theme: 'master', transition: 'page-slide' } },
  { path: '/community', name: 'community', component: Community, meta: { theme: 'community', transition: 'page-flow' } },
  { path: '/community/:id', name: 'community-detail', component: CommunityDetail, props: true, meta: { theme: 'community', transition: 'page-flow' } },
  { path: '/activities', name: 'activities', component: Activities, meta: { theme: 'activities', transition: 'page-rise' } },
  { path: '/activity-detail', name: 'activity-detail', component: ActivityDetail, meta: { theme: 'activities', transition: 'page-rise' } },
  { path: '/about', name: 'about', component: About, meta: { theme: 'studio', transition: 'page-soft' } },
  { path: '/contact', name: 'contact', component: Contact, meta: { theme: 'contact', transition: 'page-soft' } },
  { path: '/login', name: 'login', component: Login, meta: { guestOnly: true, theme: 'account', transition: 'page-veil' } },
  { path: '/register', name: 'register', component: Register, meta: { guestOnly: true, theme: 'account', transition: 'page-veil' } },
  { path: '/my', name: 'my', component: MyPage, meta: { theme: 'account', transition: 'page-veil' } }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else if (to.hash) {
      return {
        selector: to.hash,
        behavior: 'smooth'
      };
    } else {
      return { top: 0 };
    }
  }
});

async function syncAuthState() {
  try {
    const response = await userApi.check();
    if (response.data?.authenticated && response.data?.user) {
      saveSessionUser(response.data.user);
      return true;
    }
  } catch (error) {
    console.error('Failed to sync auth state.', error);
  }
  clearSessionUser();
  return false;
}

router.beforeEach(async (to) => {
  if (!to.meta?.guestOnly) {
    return true;
  }

  if (!isAuthenticated()) {
    return true;
  }

  const authenticated = await syncAuthState();
  if (authenticated) {
    return '/';
  }

  return true;
});

export default router;
