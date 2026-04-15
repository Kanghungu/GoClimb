import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { UserPlus, Trash2 } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

export default function ManagerAssignPage() {
  const qc = useQueryClient()
  const [selectedGymId, setSelectedGymId] = useState('')
  const [modal, setModal] = useState(false)
  const [selectedUserId, setSelectedUserId] = useState('')

  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then(r => r.data),
  })

  const { data: users = [] } = useQuery({
    queryKey: ['admin-users'],
    queryFn: () => api.get('/admin/users').then(r => r.data),
  })

  // 선택된 지점의 매니저 목록
  const { data: gymManagers = [] } = useQuery({
    queryKey: ['gym-managers', selectedGymId],
    queryFn: () => api.get(`/admin/gyms/${selectedGymId}/managers`).then(r => r.data),
    enabled: !!selectedGymId,
  })

  const assignMutation = useMutation({
    mutationFn: () => api.post(`/admin/gyms/${selectedGymId}/managers/${selectedUserId}`),
    onSuccess: () => {
      qc.invalidateQueries(['gym-managers'])
      toast.success('매니저가 배정됐습니다.')
      setModal(false)
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류'),
  })

  const removeMutation = useMutation({
    mutationFn: (userId) => api.delete(`/admin/gyms/${selectedGymId}/managers/${userId}`),
    onSuccess: () => { qc.invalidateQueries(['gym-managers']); toast.success('매니저가 해제됐습니다.') },
  })

  // MANAGER 역할 사용자만 필터
  const managerUsers = users.filter(u => u.role === 'MANAGER')

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-gray-900">매니저 배정 관리</h2>
          <p className="text-sm text-gray-400 mt-1">각 지점에 매니저 계정을 배정합니다.</p>
        </div>
        <button onClick={() => setModal(true)} disabled={!selectedGymId}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-40">
          <UserPlus size={16} /> 매니저 배정
        </button>
      </div>

      <div className="mb-5">
        <select value={selectedGymId} onChange={(e) => setSelectedGymId(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
          <option value="">지점 선택</option>
          {gyms.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
        </select>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">닉네임</th>
              <th className="px-4 py-3 text-left">이메일</th>
              <th className="px-4 py-3 text-right">해제</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {!selectedGymId ? (
              <tr><td colSpan={3} className="px-4 py-8 text-center text-gray-400">지점을 선택해주세요.</td></tr>
            ) : gymManagers.length === 0 ? (
              <tr><td colSpan={3} className="px-4 py-8 text-center text-gray-400">배정된 매니저가 없습니다.</td></tr>
            ) : gymManagers.map((m) => (
              <tr key={m.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{m.nickname}</td>
                <td className="px-4 py-3 text-gray-500">{m.email}</td>
                <td className="px-4 py-3 text-right">
                  <button onClick={() => { if (confirm('매니저를 해제할까요?')) removeMutation.mutate(m.id) }}
                    className="text-gray-400 hover:text-red-600"><Trash2 size={15} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-lg mb-1">매니저 배정</h3>
            <p className="text-sm text-gray-400 mb-4">MANAGER 역할 계정만 표시됩니다.</p>
            <select value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-4">
              <option value="">사용자 선택</option>
              {managerUsers.map(u => (
                <option key={u.id} value={u.id}>{u.nickname} ({u.email})</option>
              ))}
            </select>
            {managerUsers.length === 0 && (
              <p className="text-xs text-amber-600 bg-amber-50 p-2 rounded-lg mb-3">
                MANAGER 역할 계정이 없습니다. 먼저 회원가입 후 DB에서 role을 MANAGER로 변경해주세요.
              </p>
            )}
            <div className="flex gap-2 justify-end">
              <button onClick={() => setModal(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => assignMutation.mutate()} disabled={!selectedUserId}
                className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50">
                {assignMutation.isPending ? '배정 중...' : '배정'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
