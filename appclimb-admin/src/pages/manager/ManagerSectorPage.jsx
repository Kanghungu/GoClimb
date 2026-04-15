import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Trash2 } from 'lucide-react'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'
import toast from 'react-hot-toast'

export default function ManagerSectorPage() {
  const qc = useQueryClient()
  const gymId = useAuthStore((s) => s.myGym?.gymId)
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })

  const { data: sectors = [] } = useQuery({
    queryKey: ['sectors', gymId],
    queryFn: () => api.get(`/gyms/${gymId}/sectors`).then(r => r.data),
    enabled: !!gymId,
  })

  const createMutation = useMutation({
    mutationFn: () => api.post(`/gyms/${gymId}/sectors`, form),
    onSuccess: () => { qc.invalidateQueries(['sectors']); toast.success('섹터가 추가됐습니다.'); setModal(false) },
    onError: (err) => toast.error(err.response?.data?.message || '오류'),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/gyms/${gymId}/sectors/${id}`),
    onSuccess: () => { qc.invalidateQueries(['sectors']); toast.success('삭제됐습니다.') },
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">섹터 관리</h2>
        <button onClick={() => { setForm({ name: '', description: '' }); setModal(true) }}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium">
          <Plus size={16} /> 섹터 추가
        </button>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">섹터명</th>
              <th className="px-4 py-3 text-left">설명</th>
              <th className="px-4 py-3 text-right">삭제</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {sectors.length === 0 ? (
              <tr><td colSpan={3} className="px-4 py-8 text-center text-gray-400">등록된 섹터가 없습니다.</td></tr>
            ) : sectors.map((s) => (
              <tr key={s.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{s.name}</td>
                <td className="px-4 py-3 text-gray-500">{s.description || '-'}</td>
                <td className="px-4 py-3 text-right">
                  <button onClick={() => { if (confirm('삭제할까요?')) deleteMutation.mutate(s.id) }}
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
            <h3 className="font-bold text-lg mb-4">섹터 추가</h3>
            <div className="space-y-3">
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="섹터명 (예: A존) *" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="설명" rows={2} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
            </div>
            <div className="flex gap-2 mt-4 justify-end">
              <button onClick={() => setModal(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => createMutation.mutate()} disabled={!form.name}
                className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50">
                {createMutation.isPending ? '저장 중...' : '저장'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
