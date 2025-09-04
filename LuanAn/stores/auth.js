// stores/auth.js
import { defineStore } from "pinia";
import { ref, onMounted } from "vue";
import { useCookie } from "#app"; 

export const useAuthStore = defineStore("auth", () => {
  const token = useCookie("token");
  const email = useCookie("email");
  const userId = useCookie("userId");
  const userName = useCookie("userName");
  const role = useCookie("role");
  const isAuthenticated = ref(false);

  const login = (loginData) => {
    token.value = loginData.token;
    email.value = loginData.email;
    userId.value = loginData.userId;
    userName.value = loginData.userName;
    role.value = loginData.role;
    isAuthenticated.value = true;
  };

  const logout = () => {
    useCookie("token", { maxAge: 0 }).value = null;
    useCookie("email", { maxAge: 0 }).value = null;
    useCookie("userId", { maxAge: 0 }).value = null;
    useCookie("userName", { maxAge: 0 }).value = null;
    useCookie("role", { maxAge: 0 }).value = null;

    isAuthenticated.value = false;
  };

  const checkAuth = () => {
    isAuthenticated.value = !!token.value;
  };

  onMounted(() => {
    checkAuth();
  });

  return {
    isAuthenticated,
    token,
    email,
    userId,
    userName,
    role,
    login,
    logout,
    checkAuth,
  };
});
