import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { CheckCircle, XCircle, Clock } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

const STATUS_LABEL = {
  PENDING: { label: '대기중', color: 'bg-yellow-100 text-yellow-700', icon: Clock },
  APPROVED: { label: '승인됨', color: 'bg-green-100 text-green-700', icon: CheckCircle },
  REJECTED: { label: '거절됨', color: 'bg-red-100 text-red-700', icon: XCircle },
}

export default function GymJoinRequestPage() {
  const qc = useQueryClient()
  const [filter, setFilter] = useState('ALL')

  const { data: requests = [], isLoading } = useQuery({
    queryKey: ['gym-join-requests'],
    queryFn: () => api.get('/admin/gym-join-requests').then(r => r.data),
  })

  const approveMutation = useMutation({
    mutationFn: (id) => api.post(`/admin/gym-join-requests/${id}/approve`),
    onSuccess: () => {
      qc.invalidateQueries(['gym-join-requests'])
      toast.success('지점 신청이 승인됐습니다. 지점이 생성되고 매니저 권한이 부여됩니다.')
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const rejectMutation = useMutation({
    mutationFn: (id) => api.post(`/admin/gym-join-requests/${id}/reject`),
    onSuccess: () => {
      qc.invalidateQueries(['gym-join-requests'])
      toast.success('지점 신청이 거절됐습니다.')
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const filtered = filter === 'ALL' ? requests : requests.filter(r => r.status === filter)

  const counts = {
    ALL: requests.length,
    PENDING: requests.filter(r => r.status === 'PENDING').length,
    APPROVED: requests.filter(r => r.status === 'APPROVED').length,
    REJECTED: requests.filter(r => r.status === 'REJECTED').length,
  }

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-xl font-bold text-gray-900">지점 요청 승인</h2>
        <p className="text-sm text-gray-400 mt-1">지점 등록 신청을 검토하고 승인 또는 거절합니다.</p>
      </div>

      {/* 필터 탭 */}
      <div className="flex gap-2 mb-5">
        {[
          { key: 'ALL', label: '전체' },
          { key: 'PENDING', label: '대기중' },
          { key: 'APPROVED', label: '승인됨' },
          { key: 'REJECTED', label: '거절됨' },
        ].map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setFilter(key)}
            className={`px-4 py-1.5 rounded-full text-sm font-medium transition-colors ${
              filter === key
                ? 'bg-green-600 text-white'
                : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'
            }`}
          >
            {label} <span className="ml-1 text-xs opacity-70">({counts[key]})</span>
          </button>
        ))}
      </div>

      {/* 신청 목록 */}
      {isLoading ? (
        <div className="text-center py-16 text-gray-400">불러오는 중...</div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16 text-gray-400 bg-white rounded-xl border border-gray-200">
          신청 내역이 없습니다.
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((req) => {
            const statusInfo = STATUS_LABEL[req.status]
            const StatusIcon = statusInfo.icon
            const isPending = req.status === 'PENDING'

            return (
              <div key={req.id} className="bg-white rounded-xl border border-gray-200 p-5">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    {/* 지점명 + 상태 */}
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="text-base font-semibold text-gray-900">{req.gymName}</h3>
                      <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${statusInfo.color}`}>
                        <StatusIcon size={11} />
                        {statusInfo.label}
                      </span>
                    </div>

                    {/* 주소 */}
                    {req.gymAddress && (
                      <p className="text-sm text-gray-500 mb-1">📍 {req.gymAddress}</p>
                    )}

                    {/* 설명 */}
                    {req.gymDescription && (
                      <p className="text-sm text-gray-400 mb-2 line-clamp-2">{req.gymDescription}</p>
                    )}

                    {/* 신청자 정보 */}
                    <div className="flex items-center gap-3 text-xs text-gray-400">
                      <span>신청자: <span className="font-medium text-gray-600">{req.requesterNickname}</span> ({req.requesterEmail})</span>
                      <span>·</span>
                      <span>{new Date(req.createdAt).toLocaleDateString('ko-KR')}</span>
                    </div>
                  </div>

                  {/* 승인/거절 버튼 */}
                  {isPending && (
                    <div className="flex gap-2 shrink-0">
                      <button
                        onClick={() => {
                          if (confirm(`"${req.gymName}" 신청을 승인할까요?\n승인 시 지점이 생성되고 신청자에게 지점관리자 권한이 부여됩니다.`)) {
                            approveMutation.mutate(req.id)
                          }
                        }}
                        disabled={approveMutation.isPending || rejectMutation.isPending}
                        className="flex items-center gap-1.5 px-3 py-1.5 bg-green-600 hover:bg-green-700 text-white text-sm rounded-lg disabled:opacity-50 transition-colors"
                      >
                        <CheckCircle size={14} />
                        승인
                      </button>
                      <button
                        onClick={() => {
                          if (confirm(`"${req.gymName}" 신청을 거절할까요?`)) {
                            rejectMutation.mutate(req.id)
                          }
                        }}
                        disabled={approveMutation.isPending || rejectMutation.isPending}
                        className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-gray-200 hover:bg-red-50 hover:text-red-600 hover:border-red-200 text-gray-600 text-sm rounded-lg disabled:opacity-50 transition-colors"
                      >
                        <XCircle size={14} />
                        거절
                      </button>
                    </div>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
