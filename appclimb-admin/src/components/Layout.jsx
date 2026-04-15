import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { Calendar, Zap, Tag, Grid, Home, LogOut, Mountain } from 'lucide-react'
import useAuthStore from '../store/authStore'
import toast from 'react-hot-toast'

const navItems = [
  { to: '/', label: '대시보드', icon: Home, end: true },
  { to: '/gyms', label: '지점 관리', icon: Mountain },
  { to: '/schedules', label: '세팅 일정', icon: Calendar },
  { to: '/events', label: '이벤트', icon: Zap },
  { to: '/colors', label: '난이도 색깔', icon: Tag },
  { to: '/sectors', label: '섹터 관리', icon: Grid },
]

export default function Layout() {
  const logout = useAuthStore((s) => s.logout)
  const user = useAuthStore((s) => s.user)
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    toast.success('로그아웃되었습니다.')
    navigate('/login')
  }

  return (
    <div className="flex h-screen bg-gray-50">
      {/* 사이드바 */}
      <aside className="w-60 bg-white border-r border-gray-200 flex flex-col">
        <div className="p-5 border-b border-gray-200">
          <h1 className="text-xl font-bold text-green-600">🧗 AppClimb</h1>
          <p className="text-xs text-gray-500 mt-1">관리자 페이지</p>
        </div>

        <nav className="flex-1 p-3 space-y-1">
          {navItems.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-green-50 text-green-700'
                    : 'text-gray-600 hover:bg-gray-100'
                }`
              }
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="p-3 border-t border-gray-200">
          <div className="px-3 py-2 text-xs text-gray-500 mb-1">{user?.nickname}</div>
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-gray-600 hover:bg-red-50 hover:text-red-600 w-full transition-colors"
          >
            <LogOut size={18} />
            로그아웃
          </button>
        </div>
      </aside>

      {/* 메인 콘텐츠 */}
      <main className="flex-1 overflow-y-auto">
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
