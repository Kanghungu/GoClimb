import { Routes, Route, Navigate } from 'react-router-dom'
import useAuthStore from './store/authStore'

// 레이아웃
import Layout from './components/Layout'
import ManagerLayout from './components/ManagerLayout'

// 공통
import LoginPage from './pages/auth/LoginPage'
import ApplyPage from './pages/auth/ApplyPage'

// ADMIN 페이지
import GymJoinRequestPage from './pages/admin/GymJoinRequestPage'

// MANAGER 페이지
import ManagerDashboard from './pages/manager/ManagerDashboard'
import ManagerSchedulePage from './pages/manager/ManagerSchedulePage'
import ManagerEventPage from './pages/manager/ManagerEventPage'
import ManagerColorPage from './pages/manager/ManagerColorPage'
import ManagerSectorPage from './pages/manager/ManagerSectorPage'
import ManagerStaffPage from './pages/manager/ManagerStaffPage'

function PrivateRoute({ children }) {
  const token = useAuthStore((s) => s.token)
  return token ? children : <Navigate to="/login" replace />
}

function RoleRouter() {
  const role = useAuthStore((s) => s.user?.role)

  if (role === 'ADMIN') {
    return (
      <Layout>
        <Routes>
          <Route index element={<Navigate to="/requests" replace />} />
          <Route path="requests" element={<GymJoinRequestPage />} />
          <Route path="*" element={<Navigate to="/requests" replace />} />
        </Routes>
      </Layout>
    )
  }

  if (role === 'MANAGER') {
    return (
      <ManagerLayout>
        <Routes>
          <Route index element={<ManagerDashboard />} />
          <Route path="schedules" element={<ManagerSchedulePage />} />
          <Route path="events" element={<ManagerEventPage />} />
          <Route path="colors" element={<ManagerColorPage />} />
          <Route path="sectors" element={<ManagerSectorPage />} />
          <Route path="staff" element={<ManagerStaffPage />} />
        </Routes>
      </ManagerLayout>
    )
  }

  return <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/apply" element={<ApplyPage />} />
      <Route path="/*" element={<PrivateRoute><RoleRouter /></PrivateRoute>} />
    </Routes>
  )
}
