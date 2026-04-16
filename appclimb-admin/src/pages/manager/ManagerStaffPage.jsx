import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { UserPlus, Trash2, Pencil } from 'lucide-react'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'
import toast from 'react-hot-toast'

const ROLE_OPTIONS = [
  { value: 'SETTER', label: '세팅직원' },
  { value: 'TEACHER', label: '티칭직원' },
  { value: 'FRONT', label: '프론트직원' },
  { value: 'MANAGER_STAFF', label: '매니지먼트' },
]

const ROLE_COLORS = {
  SETTER: 'bg-blue-100 text-blue-700',
  TEACHER: 'bg-purple-100 text-purple-700',
  FRONT: 'bg-orange-100 text-orange-700',
  MANAGER_STAFF: 'bg-gray-100 text-gray-700',
}

const emptyForm = { name: '', staffRole: 'SETTER', note: '' }

export default function ManagerStaffPage() {
  const qc = useQueryClient()
  const { myGym } = useAuthStore()
  const gymId = myGym?.gymId

  const [modal, setModal] = useState(false)
  const [editTarget, setEditTarget] = useState(null) // null = add, object = edit
  const [form, setForm] = useState(emptyForm)

  const { data: staff = [], isLoading } = useQuery({
    queryKey: ['gym-staff', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/staff`).then(r => r.data),
    enabled: !!gymId,
  })

  const addMutation = useMutation({
    mutationFn: (data) => api.post(`/gyms/${gymId}/staff`, data),
    onSuccess: () => {
      qc.invalidateQueries(['gym-staff', gymId])
      toast.success('직원이 등록됐습니다.')
      closeModal()
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const updateMutation = useMutation({
    mutationFn: ({ staffId, data }) => api.put(`/gyms/${gymId}/staff/${staffId}`, data),
    onSuccess: () => {
      qc.invalidateQueries(['gym-staff', gymId])
      toast.success('직원 정보가 수정됐습니다.')
      closeModal()
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const deleteMutation = useMutation({
    mutationFn: (staffId) => api.delete(`/gyms/${gymId}/staff/${staffId}`),
    onSuccess: () => {
      qc.invalidateQueries(['gym-staff', gymId])
      toast.success('직원이 삭제됐습니다.')
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const openAdd = () => {
    setEditTarget(null)
    setForm(emptyForm)
    setModal(true)
  }

  const openEdit = (s) => {
    setEditTarget(s)
    setForm({ name: s.name, staffRole: s.staffRole, note: s.note || '' })
    setModal(true)
  }

  const closeModal = () => {
    setModal(false)
    setEditTarget(null)
    setForm(emptyForm)
  }

  const handleSubmit = () => {
    if (!form.name.trim()) { toast.error('직원 이름을 입력해주세요.'); return }
    if (editTarget) {
      updateMutation.mutate({ staffId: editTarget.id, data: form })
    } else {
      addMutation.mutate(form)
    }
  }

  // 역할별 그룹핑
  const grouped = ROLE_OPTIONS.map(({ value, label }) => ({
    role: value,
    label,
    members: staff.filter(s => s.staffRole === value),
  })).filter(g => g.members.length > 0)

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-gray-900">직원 관리</h2>
          <p className="text-sm text-gray-400 mt-1">지점 직원의 역할을 등록하고 관리합니다.</p>
        </div>
        <button
          onClick={openAdd}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium"
        >
          <UserPlus size={16} />
          직원 등록
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-16 text-gray-400">불러오는 중...</div>
      ) : staff.length === 0 ? (
        <div className="text-center py-16 text-gray-400 bg-white rounded-xl border border-gray-200">
          <p className="font-medium mb-1">등록된 직원이 없습니다.</p>
          <p className="text-sm">직원 등록 버튼을 눌러 직원을 추가해보세요.</p>
        </div>
      ) : (
        <div className="space-y-5">
          {grouped.map(({ role, label, members }) => (
            <div key={role} className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="px-4 py-3 bg-gray-50 border-b border-gray-100 flex items-center gap-2">
                <span className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${ROLE_COLORS[role]}`}>
                  {label}
                </span>
                <span className="text-xs text-gray-400">{members.length}명</span>
              </div>
              <table className="w-full text-sm">
                <tbody className="divide-y divide-gray-100">
                  {members.map((s) => (
                    <tr key={s.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 font-medium text-gray-900">{s.name}</td>
                      <td className="px-4 py-3 text-gray-400 text-xs">{s.note || '-'}</td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button
                            onClick={() => openEdit(s)}
                            className="text-gray-400 hover:text-blue-600 p-1 rounded"
                          >
                            <Pencil size={14} />
                          </button>
                          <button
                            onClick={() => { if (confirm(`"${s.name}"를 삭제할까요?`)) deleteMutation.mutate(s.id) }}
                            className="text-gray-400 hover:text-red-600 p-1 rounded"
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ))}
        </div>
      )}

      {/* 등록/수정 모달 */}
      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
            <h3 className="font-bold text-lg mb-4">{editTarget ? '직원 정보 수정' : '직원 등록'}</h3>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">이름</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm(f => ({ ...f, name: e.target.value }))}
                  placeholder="직원 이름"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">역할</label>
                <select
                  value={form.staffRole}
                  onChange={(e) => setForm(f => ({ ...f, staffRole: e.target.value }))}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {ROLE_OPTIONS.map(({ value, label }) => (
                    <option key={value} value={value}>{label}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">메모 (선택)</label>
                <input
                  type="text"
                  value={form.note}
                  onChange={(e) => setForm(f => ({ ...f, note: e.target.value }))}
                  placeholder="예: 월수금 근무"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            </div>

            <div className="flex gap-2 justify-end mt-5">
              <button
                onClick={closeModal}
                className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg"
              >
                취소
              </button>
              <button
                onClick={handleSubmit}
                disabled={addMutation.isPending || updateMutation.isPending}
                className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50"
              >
                {editTarget ? '수정' : '등록'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
