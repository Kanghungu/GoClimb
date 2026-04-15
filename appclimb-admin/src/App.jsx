import { Routes, Route, Navigate } from 'react-router-dom'
import useAuthStore from './store/authStore'

// 레이아웃
import Layout from './components/Layout'
import ManagerLayout from './components/ManagerLayout'

// 공통
import LoginPage from './pages/auth/LoginPage'

// ADMIN 페이지
import DashboardPage from './pages/DashboardPage'
import GymPage from './pages/gym/GymPage'
import SchedulePage from './pages/schedule/SchedulePage'
import EventPage from './pages/event/EventPage'
import ColorPage from './pages/color/ColorPage'
import SectorPage from './pages/sector/SectorPage'
import ManagerAssignPage from './pages/admin/ManagerAssignPage'

// MANAGER 페이지
import ManagerDashboard from './pages/manager/ManagerDashboard'
import ManagerSchedulePage from './pages/manager/ManagerSchedulePage'
import ManagerEventPage from './pages/manager/ManagerEventPage'
import ManagerColorPage from './pages/manager/ManagerColorPage'
import ManagerSectorPage from './pages/manager/ManagerSectorPage'

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
          <Route index element={<DashboardPage />} />
          <Route path="gyms" element={<GymPage />} />
          <Route path="schedules" element={<SchedulePage />} />
          <Route path="events" element={<EventPage />} />
          <Route path="colors" element={<ColorPage />} />
          <Route path="sectors" element={<SectorPage />} />
          <Route path="managers" element={<ManagerAssignPage />} />
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
      <Route path="/*" element={<PrivateRoute><RoleRouter /></PrivateRoute>} />
    </Routes>
  )
}
