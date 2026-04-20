const STORAGE_KEY = 'feiyi-user';
const AUTH_KEY = 'feiyi-authenticated';
const LEGACY_STORAGE_KEY = 'user';
const LEGACY_AUTH_KEY = 'isAuthenticated';
const SESSION_EVENT = 'feiyi-session-changed';

const parseUser = (value) => {
  if (!value) return null;
  try {
    return JSON.parse(value);
  } catch (error) {
    console.error('无法解析本地用户信息', error);
    return null;
  }
};

export function readSessionUser() {
  const current = parseUser(localStorage.getItem(STORAGE_KEY));
  console.log('从本地存储读取用户信息:', current);
  if (current) {
    return current;
  }
  const legacy = parseUser(localStorage.getItem(LEGACY_STORAGE_KEY));
  console.log('从旧本地存储读取用户信息:', legacy);
  return legacy;
}

export function isAuthenticated() {
  const current = localStorage.getItem(AUTH_KEY);
  if (current === 'true') {
    return true;
  }
  return localStorage.getItem(LEGACY_AUTH_KEY) === 'true' && !!readSessionUser();
}

export function saveSessionUser(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  localStorage.setItem(LEGACY_STORAGE_KEY, JSON.stringify(user));
  localStorage.setItem(AUTH_KEY, 'true');
  localStorage.setItem(LEGACY_AUTH_KEY, 'true');
  window.dispatchEvent(new CustomEvent(SESSION_EVENT));
}

export function clearSessionUser() {
  localStorage.removeItem(STORAGE_KEY);
  localStorage.removeItem(LEGACY_STORAGE_KEY);
  localStorage.removeItem(AUTH_KEY);
  localStorage.removeItem(LEGACY_AUTH_KEY);
  window.dispatchEvent(new CustomEvent(SESSION_EVENT));
}

export function sessionEventName() {
  return SESSION_EVENT;
}

export function currentRole() {
  return readSessionUser()?.role || 'user';
}

export function isAdminUser(user = readSessionUser()) {
  return user?.role === 'admin';
}
