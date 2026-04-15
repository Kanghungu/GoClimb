import { useQuery } from '@tanstack/react-query'
import { Calendar, Zap, Mountain, Grid } from 'lucide-react'
import api from '../api/axios'
import useAuthStore from '../store/authStore'

export default function DashboardPage() {
  const user = useAuthStore((s) => s.user)
  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then((r) => r.data),
  })

  const cards = [
    { label: '등록된 지점', value: gyms.length, icon: Mountain, color: 'text-green-600 bg-green-50' },
    { label: '오늘 날짜', value: new Date().toLocaleDateString('ko-KR'), icon: Calendar, color: 'text-blue-600 bg-blue-50' },
  ]

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">안녕하세요, {user?.nickname}님 👋</h2>
        <p className="text-gray-500 mt-1">AppClimb 관리자 페이지입니다.</p>
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

      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <h3 className="font-semibold text-gray-900 mb-3">등록된 지점</h3>
        {gyms.length === 0 ? (
          <p className="text-sm text-gray-400">등록된 지점이 없습니다. 지점 관리에서 추가해주세요.</p>
        ) : (
          <div className="space-y-2">
            {gyms.map((gym) => (
              <div key={gym.id} className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
                <span className="font-medium text-sm">{gym.name}</span>
                <span className="text-xs text-gray-400">{gym.address || '주소 없음'}</span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
