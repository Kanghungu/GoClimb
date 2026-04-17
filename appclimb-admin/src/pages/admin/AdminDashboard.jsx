import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { Building2, Clock, CheckCircle, ArrowRight } from 'lucide-react'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'

export default function AdminDashboard() {
  const { user } = useAuthStore()
  const navigate = useNavigate()

  // 전체 지점 수
  const { data: allGyms = [] } = useQuery({
    queryKey: ['all-gyms'],
    queryFn: () => api.get('/gyms').then(r => r.data),
  })

  // 모든 신청 (대기, 승인, 거절)
  const { data: allRequests = [] } = useQuery({
    queryKey: ['gym-join-requests-all'],
    queryFn: () => api.get('/admin/gym-join-requests').then(r => r.data),
  })

  // 대기중인 신청만
  const { data: pendingRequests = [] } = useQuery({
    queryKey: ['gym-join-requests-pending'],
    queryFn: () => api.get('/admin/gym-join-requests/pending').then(r => r.data).catch(() => []),
  })

  // 통계 계산
  const totalGyms = allGyms.length
  const pendingCount = allRequests.filter(r => r.status === 'PENDING').length
  const approvedCount = allRequests.filter(r => r.status === 'APPROVED').length

  const stats = [
    { label: '전체 지점 수', value: totalGyms, icon: Building2, color: 'text-blue-600 bg-blue-50' },
    { label: '대기중인 신청', value: pendingCount, icon: Clock, color: 'text-yellow-600 bg-yellow-50' },
    { label: '승인된 지점', value: approvedCount, icon: CheckCircle, color: 'text-green-600 bg-green-50' },
  ]

  return (
    <div>
      <div className="mb-8">
        <h2 className="text-2xl font-bold text-gray-900">안녕하세요, {user?.nickname}님 👋</h2>
        <p className="text-gray-500 mt-1">AppClimb 관리자 대시보드입니다.</p>
      </div>

      {/* 통계 카드 */}
      <div className="grid grid-cols-3 gap-4 mb-8">
        {stats.map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="bg-white rounded-xl border border-gray-200 p-5">
            <div className={`inline-flex p-2 rounded-lg ${color} mb-3`}>
              <Icon size={20} />
            </div>
            <div className="text-3xl font-bold text-gray-900">{value}</div>
            <div className="text-sm text-gray-500 mt-1">{label}</div>
          </div>
        ))}
      </div>

      {/* 대기중인 신청 미리보기 */}
      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-gray-900 flex items-center gap-2">
            <Clock size={18} className="text-yellow-600" />
            대기중인 신청 ({pendingCount})
          </h3>
          <button
            onClick={() => navigate('/requests')}
            className="text-xs text-green-600 hover:text-green-700 font-medium flex items-center gap-1"
          >
            전체보기
            <ArrowRight size={14} />
          </button>
        </div>

        {pendingRequests.length === 0 ? (
          <div className="text-center py-8 text-gray-400">
            <p className="text-sm">대기 중인 신청이 없습니다.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {pendingRequests.slice(0, 3).map((req) => (
              <div
                key={req.id}
                className="flex items-start justify-between py-3 px-4 bg-yellow-50 rounded-lg border border-yellow-100 hover:border-yellow-300 transition-colors"
              >
                <div className="flex-1 min-w-0">
                  <h4 className="text-sm font-semibold text-gray-900">{req.gymName}</h4>
                  <p className="text-xs text-gray-600 mt-1">📍 {req.gymAddress}</p>
                  {req.gymDescription && (
                    <p className="text-xs text-gray-500 mt-1 line-clamp-1">{req.gymDescription}</p>
                  )}
                  <p className="text-xs text-gray-400 mt-2">
                    신청자: <span className="font-medium">{req.requesterNickname}</span> ({req.requesterEmail})
                  </p>
                </div>
                <button
                  onClick={() => navigate('/requests')}
                  className="ml-4 px-3 py-1.5 text-xs font-medium bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors shrink-0"
                >
                  검토
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
