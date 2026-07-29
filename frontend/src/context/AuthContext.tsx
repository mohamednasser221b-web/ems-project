import { createContext, useContext, useState, ReactNode } from "react";
import { apiClient, setAccessToken } from "../api/client";

type Role = "HR_ADMIN" | "MANAGER" | "EMPLOYEE";

interface AuthState {
  role: Role | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<Role | null>(null);

  async function login(email: string, password: string) {
    const { data } = await apiClient.post("/auth/login", { email, password });
    setAccessToken(data.accessToken);
    setRole(data.role);
    // Refresh token: in a hardened build this is set as an HttpOnly cookie by
    // the backend directly, never handed to JS at all. Storing it in JS-
    // reachable state (even briefly) widens the XSS blast radius - flagged
    // as a hardening task, not shipped as-is.
  }

  function logout() {
    setAccessToken(null);
    setRole(null);
  }

  return (
    <AuthContext.Provider value={{ role, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
