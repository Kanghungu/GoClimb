import { Routes, Route, Navigate } from 'react-router-dom'
import useAuthStore from './store/authStore'
import Layout from './components/Layout'
import LoginPage from './pages/auth/LoginPage'
import DashboardPage from './pages/DashboardPage'
import SchedulePage from './pages/schedule/SchedulePage'
import EventPage from './pages/event/EventPage'
import ColorPage from './pages/color/ColorPage'
import SectorPage from './pages/sector/SectorPage'
import GymPage from './pages/gym/GymPage'

function PrivateRoute({ children }) {
  const token = useAuthStore((s) => s.token)
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
        <Route index element={<DashboardPage />} />
        <Route path="gyms" element={<GymPage />} />
        <Route path="schedules" element={<SchedulePage />} />
        <Route path="events" element={<EventPage />} />
        <Route path="colors" element={<ColorPage />} />
        <Route path="sectors" element={<SectorPage />} />
      </Route>
    </Routes>
  )
}
