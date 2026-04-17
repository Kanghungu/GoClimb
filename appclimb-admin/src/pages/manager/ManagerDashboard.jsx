import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { Calendar, Zap, Grid, Tag, Users, ArrowRight } from 'lucide-react'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'

export default function ManagerDashboard() {
  const { user, myGym } = useAuthStore()
  const gymId = myGym?.gymId
  const navigate = useNavigate()

  const { data: schedules = [] } = useQuery({
    queryKey: ['schedules', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/schedules?month=${new Date().toISOString().slice(0, 7)}`).then(r => r.data),
    enabled: !!gymId,
  })
  const { data: events = [] } = useQuery({
    queryKey: ['events', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/events`).then(r => r.data),
    enabled: !!gymId,
  })
  const { data: sectors = [] } = useQuery({
    queryKey: ['sectors', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/sectors`).then(r => r.data),
    enabled: !!gymId,
  })
  const { data: colors = [] } = useQuery({
    queryKey: ['colors', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/colors`).then(r => r.data),
    enabled: !!gymId,
  })
  const { data: staff = [] } = useQuery({
    queryKey: ['staff', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/staff`).then(r => r.data),
    enabled: !!gymId,
  })

  // 직원 역할별 카운트
  const staffCounts = {
    SETTER: staff.filter(s => s.role === 'SETTER').length,
    INSTRUCTOR: staff.filter(s => s.role === 'INSTRUCTOR').length,
    FRONT_DESK: staff.filter(s => s.role === 'FRONT_DESK').length,
  }

  // 이번달 이벤트만 필터링 (최대 3개)
  const currentMonth = new Date().toISOString().slice(0, 7)
  const thisMonthEvents = events.filter(e => e.eventDate?.startsWith(currentMonth)).slice(0, 3)

  const cards = [
    { label: '이번달 세팅 일정', value: schedules.length, icon: Calendar, color: 'text-blue-600 bg-blue-50', to: '/schedules' },
    { label: '진행중인 이벤트', value: events.length, icon: Zap, color: 'text-yellow-600 bg-yellow-50', to: '/events' },
    { label: '섹터 수', value: sectors.length, icon: Grid, color: 'text-purple-600 bg-purple-50', to: '/sectors' },
    { label: '난이도 색깔', value: colors.length, icon: Tag, color: 'text-green-600 bg-green-50', to: '/colors' },
  ]

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">안녕하세요, {user?.nickname}님 👋</h2>
        <p className="text-gray-500 mt-1">{myGym?.gymName} 관리 페이지입니다.</p>
        {myGym?.gymAddress && <p className="text-sm text-gray-400 mt-0.5">{myGym.gymAddress}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4 mb-8">
        {cards.map(({ label, value, icon: Icon, color, to }) => (
          <button
            key={label}
            onClick={() => navigate(to)}
            className="bg-white rounded-xl border border-gray-200 p-5 text-left hover:border-green-300 hover:shadow-md transition-all cursor-pointer group"
          >
            <div className={`inline-flex p-2 rounded-lg ${color} mb-3 group-hover:scale-110 transition-transform`}>
              <Icon size={20} />
            </div>
            <div className="text-2xl font-bold text-gray-900">{value}</div>
            <div className="text-sm text-gray-500 mt-1 flex items-center gap-1">
              {label}
              <ArrowRight size={14} className="opacity-0 group-hover:opacity-100 transition-opacity" />
            </div>
          </button>
        ))}
      </div>

      {/* 직원 현황 섹션 */}
      <div className="bg-white rounded-xl border border-gray-200 p-5 mb-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-gray-900 flex items-center gap-2">
            <Users size={18} className="text-green-600" />
            직원 현황
          </h3>
          <button
            onClick={() => navigate('/staff')}
            className="text-xs text-green-600 hover:text-green-700 font-medium"
          >
            전체보기 →
          </button>
        </div>
        <div className="grid grid-cols-3 gap-3">
          <div className="bg-blue-50 rounded-lg p-4 text-center">
            <div className="text-2xl font-bold text-blue-600">{staffCounts.SETTER}</div>
            <div className="text-xs text-gray-600 mt-1">세터</div>
          </div>
          <div className="bg-purple-50 rounded-lg p-4 text-center">
            <div className="text-2xl font-bold text-purple-600">{staffCounts.INSTRUCTOR}</div>
            <div className="text-xs text-gray-600 mt-1">강사</div>
          </div>
          <div className="bg-orange-50 rounded-lg p-4 text-center">
            <div className="text-2xl font-bold text-orange-600">{staffCounts.FRONT_DESK}</div>
            <div className="text-xs text-gray-600 mt-1">프론트</div>
          </div>
        </div>
      </div>

      {/* 이번달 이벤트 섹션 */}
      <div className="bg-white rounded-xl border border-gray-200 p-5 mb-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-gray-900 flex items-center gap-2">
            <Zap size={18} className="text-yellow-600" />
            이번달 이벤트
          </h3>
          <button
            onClick={() => navigate('/events')}
            className="text-xs text-green-600 hover:text-green-700 font-medium"
          >
            전체보기 →
          </button>
        </div>
        {thisMonthEvents.length === 0 ? (
          <p className="text-sm text-gray-400">이번달 이벤트가 없습니다.</p>
        ) : (
          <div className="space-y-2">
            {thisMonthEvents.map((e) => (
              <div key={e.id} className="flex items-start justify-between py-2 px-3 bg-yellow-50 rounded-lg border border-yellow-100">
                <div className="flex-1">
                  <div className="text-sm font-medium text-gray-900">{e.eventName}</div>
                  <div className="text-xs text-gray-500 mt-0.5">📅 {e.eventDate}</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 이번달 세팅 일정 미리보기 */}
      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-gray-900 flex items-center gap-2">
            <Calendar size={18} className="text-blue-600" />
            이번달 세팅 일정
          </h3>
          <button
            onClick={() => navigate('/schedules')}
            className="text-xs text-green-600 hover:text-green-700 font-medium"
          >
            전체보기 →
          </button>
        </div>
        {schedules.length === 0 ? (
          <p className="text-sm text-gray-400">이번달 세팅 일정이 없습니다.</p>
        ) : (
          <div className="space-y-2">
            {schedules.slice(0, 5).map((s) => (
              <div key={s.id} className="flex items-center justify-between py-2 px-3 bg-blue-50 rounded-lg border border-blue-100">
                <div className="flex-1">
                  <div className="text-sm font-medium text-gray-900">{s.settingDate}</div>
                  <div className="text-xs text-gray-500 mt-0.5">📍 {s.sectorName || '전체 섹터'}</div>
                  {s.description && <div className="text-xs text-gray-600 mt-0.5">{s.description}</div>}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
