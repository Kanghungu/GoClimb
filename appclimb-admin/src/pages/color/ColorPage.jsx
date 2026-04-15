import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Trash2 } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

const empty = { colorName: '', colorHex: '#000000', levelOrder: 1 }

export default function ColorPage() {
  const qc = useQueryClient()
  const [selectedGymId, setSelectedGymId] = useState('')
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState(empty)

  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then((r) => r.data),
  })

  const { data: colors = [] } = useQuery({
    queryKey: ['colors', selectedGymId],
    queryFn: () => api.get(`/gyms/${selectedGymId}/colors`).then((r) => r.data),
    enabled: !!selectedGymId,
  })

  const createMutation = useMutation({
    mutationFn: () => api.post(`/gyms/${selectedGymId}/colors`, form),
    onSuccess: () => { qc.invalidateQueries(['colors']); toast.success('난이도가 추가됐습니다.'); setModal(false) },
    onError: (err) => toast.error(err.response?.data?.message || '오류'),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/gyms/${selectedGymId}/colors/${id}`),
    onSuccess: () => { qc.invalidateQueries(['colors']); toast.success('삭제됐습니다.') },
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">난이도 색깔 관리</h2>
        <button onClick={() => { setForm(empty); setModal(true) }} disabled={!selectedGymId}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-40">
          <Plus size={16} /> 색깔 추가
        </button>
      </div>

      <div className="mb-5">
        <select value={selectedGymId} onChange={(e) => setSelectedGymId(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
          <option value="">지점 선택</option>
          {gyms.map((g) => <option key={g.id} value={g.id}>{g.name}</option>)}
        </select>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">순서</th>
              <th className="px-4 py-3 text-left">색깔</th>
              <th className="px-4 py-3 text-left">이름</th>
              <th className="px-4 py-3 text-left">HEX</th>
              <th className="px-4 py-3 text-right">삭제</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {!selectedGymId ? (
              <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-400">지점을 선택해주세요.</td></tr>
            ) : colors.length === 0 ? (
              <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-400">등록된 난이도가 없습니다.</td></tr>
            ) : colors.map((c) => (
              <tr key={c.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-gray-500">Lv.{c.levelOrder}</td>
                <td className="px-4 py-3">
                  <div className="w-6 h-6 rounded-full border border-gray-200" style={{ backgroundColor: c.colorHex }} />
                </td>
                <td className="px-4 py-3 font-medium">{c.colorName}</td>
                <td className="px-4 py-3 text-gray-400 font-mono text-xs">{c.colorHex}</td>
                <td className="px-4 py-3 text-right">
                  <button onClick={() => { if (confirm('삭제할까요?')) deleteMutation.mutate(c.id) }}
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
            <h3 className="font-bold text-lg mb-4">난이도 색깔 추가</h3>
            <div className="space-y-3">
              <input value={form.colorName} onChange={(e) => setForm({ ...form, colorName: e.target.value })}
                placeholder="색깔 이름 (예: 노랑) *" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <div>
                <label className="block text-xs text-gray-500 mb-1">색깔 선택</label>
                <div className="flex items-center gap-3">
                  <input type="color" value={form.colorHex} onChange={(e) => setForm({ ...form, colorHex: e.target.value })}
                    className="w-12 h-10 rounded border border-gray-300 cursor-pointer" />
                  <span className="text-sm font-mono text-gray-500">{form.colorHex}</span>
                </div>
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">난이도 순서 (1=쉬움)</label>
                <input type="number" min={1} value={form.levelOrder} onChange={(e) => setForm({ ...form, levelOrder: parseInt(e.target.value) })}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              </div>
            </div>
            <div className="flex gap-2 mt-4 justify-end">
              <button onClick={() => setModal(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => createMutation.mutate()} disabled={!form.colorName}
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
