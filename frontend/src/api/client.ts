import axios from "axios";

// Tokens are kept in memory (a module-level variable), NOT localStorage.
// localStorage is readable by any JS on the page, which makes an XSS bug an
// instant account-takeover; keeping tokens in memory limits the blast radius
// to the current tab/session. Trade-off: a hard page refresh clears them,
// so the app also needs a refresh-token flow on boot (see AuthContext).
let accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1",
});

apiClient.interceptors.request.use((config) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    // A 401 here means the access token expired mid-session; a real refresh
    // flow would retry once via /auth/refresh before giving up and forcing
    // a re-login. Left as a follow-up task once refresh-token rotation is
    // wired up server-side.
    return Promise.reject(error);
  }
);
