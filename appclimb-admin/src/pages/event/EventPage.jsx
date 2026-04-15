import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Trash2, Pencil } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

const empty = { title: '', description: '', startDate: '', endDate: '' }

export default function EventPage() {
  const qc = useQueryClient()
  const [selectedGymId, setSelectedGymId] = useState('')
  const [modal, setModal] = useState(null)
  const [form, setForm] = useState(empty)

  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then((r) => r.data),
  })

  const { data: events = [] } = useQuery({
    queryKey: ['events', selectedGymId],
    queryFn: () => api.get(`/gyms/${selectedGymId}/events`).then((r) => r.data),
    enabled: !!selectedGymId,
  })

  const saveMutation = useMutation({
    mutationFn: () =>
      modal === 'create'
        ? api.post(`/gyms/${selectedGymId}/events`, form)
        : api.put(`/gyms/${selectedGymId}/events/${modal.id}`, form),
    onSuccess: () => {
      qc.invalidateQueries(['events'])
      toast.success(modal === 'create' ? '이벤트가 등록됐습니다.' : '이벤트가 수정됐습니다.')
      setModal(null)
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/gyms/${selectedGymId}/events/${id}`),
    onSuccess: () => { qc.invalidateQueries(['events']); toast.success('삭제됐습니다.') },
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">이벤트 관리</h2>
        <button onClick={() => { setForm(empty); setModal('create') }} disabled={!selectedGymId}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-40">
          <Plus size={16} /> 이벤트 추가
        </button>
      </div>

      <div className="mb-5">
        <select value={selectedGymId} onChange={(e) => setSelectedGymId(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
          <option value="">지점 선택</option>
          {gyms.map((g) => <option key={g.id} value={g.id}>{g.name}</option>)}
        </select>
      </div>

      <div className="space-y-3">
        {!selectedGymId ? (
          <p className="text-sm text-gray-400 text-center py-8">지점을 먼저 선택해주세요.</p>
        ) : events.length === 0 ? (
          <p className="text-sm text-gray-400 text-center py-8">등록된 이벤트가 없습니다.</p>
        ) : events.map((ev) => (
          <div key={ev.id} className="bg-white rounded-xl border border-gray-200 p-4 flex items-start justify-between">
            <div>
              <div className="font-medium text-gray-900">{ev.title}</div>
              <div className="text-xs text-gray-400 mt-1">{ev.startDate} {ev.endDate ? `~ ${ev.endDate}` : ''}</div>
              {ev.description && <div className="text-sm text-gray-500 mt-2">{ev.description}</div>}
            </div>
            <div className="flex gap-2 ml-4">
              <button onClick={() => { setForm({ title: ev.title, description: ev.description || '', startDate: ev.startDate, endDate: ev.endDate || '' }); setModal(ev) }}
                className="text-gray-400 hover:text-blue-600"><Pencil size={15} /></button>
              <button onClick={() => { if (confirm('삭제할까요?')) deleteMutation.mutate(ev.id) }}
                className="text-gray-400 hover:text-red-600"><Trash2 size={15} /></button>
            </div>
          </div>
        ))}
      </div>

      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h3 className="font-bold text-lg mb-4">{modal === 'create' ? '이벤트 추가' : '이벤트 수정'}</h3>
            <div className="space-y-3">
              <input value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })}
                placeholder="이벤트 제목 *" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="내용" rows={3} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
              <div className="flex gap-2">
                <div className="flex-1">
                  <label className="block text-xs text-gray-500 mb-1">시작일 *</label>
                  <input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                </div>
                <div className="flex-1">
                  <label className="block text-xs text-gray-500 mb-1">종료일</label>
                  <input type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                </div>
              </div>
            </div>
            <div className="flex gap-2 mt-4 justify-end">
              <button onClick={() => setModal(null)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => saveMutation.mutate()} disabled={!form.title || !form.startDate}
                className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50">
                {saveMutation.isPending ? '저장 중...' : '저장'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
