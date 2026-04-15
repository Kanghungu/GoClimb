import { useQuery } from '@tanstack/react-query'
import { Calendar, Zap, Grid, Tag } from 'lucide-react'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'

export default function ManagerDashboard() {
  const { user, myGym } = useAuthStore()
  const gymId = myGym?.gymId

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

  const cards = [
    { label: '이번달 세팅 일정', value: schedules.length, icon: Calendar, color: 'text-blue-600 bg-blue-50' },
    { label: '진행중인 이벤트', value: events.length, icon: Zap, color: 'text-yellow-600 bg-yellow-50' },
    { label: '섹터 수', value: sectors.length, icon: Grid, color: 'text-purple-600 bg-purple-50' },
    { label: '난이도 색깔', value: colors.length, icon: Tag, color: 'text-green-600 bg-green-50' },
  ]

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">안녕하세요, {user?.nickname}님 👋</h2>
        <p className="text-gray-500 mt-1">{myGym?.gymName} 관리 페이지입니다.</p>
        {myGym?.gymAddress && <p className="text-sm text-gray-400 mt-0.5">{myGym.gymAddress}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4 mb-8">
        {cards.map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="bg-white rounded-xl border border-gray-200 p-5">
            <div className={`inline-flex p-2 rounded-lg ${color} mb-3`}>
              <Icon size={20} />
            </div>
            <div className="text-2xl font-bold text-gray-900">{value}</div>
            <div className="text-sm text-gray-500 mt-1">{label}</div>
          </div>
        ))}
      </div>

      {/* 이번달 세팅 일정 미리보기 */}
      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <h3 className="font-semibold text-gray-900 mb-3">이번달 세팅 일정</h3>
        {schedules.length === 0 ? (
          <p className="text-sm text-gray-400">이번달 세팅 일정이 없습니다.</p>
        ) : (
          <div className="space-y-2">
            {schedules.slice(0, 5).map((s) => (
              <div key={s.id} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
                <span className="text-sm font-medium">{s.settingDate}</span>
                <span className="text-xs text-gray-400">{s.sectorName || '전체'}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
