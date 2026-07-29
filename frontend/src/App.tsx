import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { LoginPage } from "./pages/LoginPage";
import { EmployeeListPage } from "./pages/EmployeeListPage";

function PrivateRoute({ children }: { children: JSX.Element }) {
  const { role } = useAuth();
  return role ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/employees"
            element={
              <PrivateRoute>
                <EmployeeListPage />
              </PrivateRoute>
            }
          />
          <Route path="*" element={<Navigate to="/employees" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
