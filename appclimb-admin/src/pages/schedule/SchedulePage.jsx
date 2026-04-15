import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Trash2 } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

export default function SchedulePage() {
  const qc = useQueryClient()
  const [selectedGymId, setSelectedGymId] = useState('')
  const [month, setMonth] = useState(new Date().toISOString().slice(0, 7))
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState({ sectorId: '', settingDate: '', description: '' })

  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then((r) => r.data),
  })

  const { data: sectors = [] } = useQuery({
    queryKey: ['sectors', selectedGymId],
    queryFn: () => api.get(`/gyms/${selectedGymId}/sectors`).then((r) => r.data),
    enabled: !!selectedGymId,
  })

  const { data: schedules = [] } = useQuery({
    queryKey: ['schedules', selectedGymId, month],
    queryFn: () => api.get(`/gyms/${selectedGymId}/schedules?month=${month}`).then((r) => r.data),
    enabled: !!selectedGymId,
  })

  const createMutation = useMutation({
    mutationFn: () => api.post(`/gyms/${selectedGymId}/schedules`, {
      ...form,
      sectorId: form.sectorId || null,
    }),
    onSuccess: () => {
      qc.invalidateQueries(['schedules'])
      toast.success('세팅 일정이 등록됐습니다.')
      setModal(false)
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/gyms/${selectedGymId}/schedules/${id}`),
    onSuccess: () => {
      qc.invalidateQueries(['schedules'])
      toast.success('삭제됐습니다.')
    },
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">세팅 일정 관리</h2>
        <button onClick={() => setModal(true)} disabled={!selectedGymId}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-40">
          <Plus size={16} /> 일정 추가
        </button>
      </div>

      <div className="flex gap-3 mb-5">
        <select value={selectedGymId} onChange={(e) => setSelectedGymId(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
          <option value="">지점 선택</option>
          {gyms.map((g) => <option key={g.id} value={g.id}>{g.name}</option>)}
        </select>
        <input type="month" value={month} onChange={(e) => setMonth(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm" />
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">날짜</th>
              <th className="px-4 py-3 text-left">섹터</th>
              <th className="px-4 py-3 text-left">메모</th>
              <th className="px-4 py-3 text-right">삭제</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {!selectedGymId ? (
              <tr><td colSpan={4} className="px-4 py-8 text-center text-gray-400">지점을 먼저 선택해주세요.</td></tr>
            ) : schedules.length === 0 ? (
              <tr><td colSpan={4} className="px-4 py-8 text-center text-gray-400">등록된 세팅 일정이 없습니다.</td></tr>
            ) : schedules.map((s) => (
              <tr key={s.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{s.settingDate}</td>
                <td className="px-4 py-3 text-gray-500">{s.sectorName || '전체'}</td>
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
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h3 className="font-bold text-lg mb-4">세팅 일정 추가</h3>
            <div className="space-y-3">
              <div>
                <label className="block text-xs text-gray-500 mb-1">세팅 날짜 *</label>
                <input type="date" value={form.settingDate} onChange={(e) => setForm({ ...form, settingDate: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">섹터 (선택)</label>
                <select value={form.sectorId} onChange={(e) => setForm({ ...form, sectorId: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
                  <option value="">전체</option>
                  {sectors.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
                </select>
              </div>
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="메모" rows={2} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
            </div>
            <div className="flex gap-2 mt-4 justify-end">
              <button onClick={() => setModal(false)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => createMutation.mutate()} disabled={!form.settingDate}
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
