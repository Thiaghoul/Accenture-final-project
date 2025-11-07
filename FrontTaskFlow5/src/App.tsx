import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import { Toaster } from "sonner"
import { AuthProvider } from "./context/AuthContext"
import { ProtectedRoute } from "../src/components/auth/ProtectedRoute"
import LoginPage from "./pages/LoginPage"
import RegisterPage from "./pages/RegisterPage"
import ForgotPasswordPage from "./pages/ForgotPasswordPage"
import DashboardPage from "./pages/DashboardPage"
import ProjectBoardPage from "./pages/ProjectBoardPage"

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" richColors />
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/project/:id"
            element={
              <ProtectedRoute>
                <ProjectBoardPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
