import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import api from '../../api/axios'
import toast from 'react-hot-toast'

export default function GymPage() {
  const qc = useQueryClient()
  const [modal, setModal] = useState(null) // null | 'create' | gym객체
  const [form, setForm] = useState({ name: '', address: '', description: '' })

  const { data: gyms = [] } = useQuery({
    queryKey: ['gyms'],
    queryFn: () => api.get('/gyms').then((r) => r.data),
  })

  const openCreate = () => {
    setForm({ name: '', address: '', description: '' })
    setModal('create')
  }

  const openEdit = (gym) => {
    setForm({ name: gym.name, address: gym.address || '', description: gym.description || '' })
    setModal(gym)
  }

  const saveMutation = useMutation({
    mutationFn: () =>
      modal === 'create'
        ? api.post('/gyms', form)
        : api.put(`/gyms/${modal.id}`, form),
    onSuccess: () => {
      qc.invalidateQueries(['gyms'])
      toast.success(modal === 'create' ? '지점이 등록됐습니다.' : '지점이 수정됐습니다.')
      setModal(null)
    },
    onError: (err) => toast.error(err.response?.data?.message || '오류가 발생했습니다.'),
  })

  const deleteMutation = useMutation({
    mutationFn: (id) => api.delete(`/gyms/${id}`),
    onSuccess: () => {
      qc.invalidateQueries(['gyms'])
      toast.success('지점이 삭제됐습니다.')
    },
    onError: () => toast.error('삭제에 실패했습니다.'),
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-bold text-gray-900">지점 관리</h2>
        <button onClick={openCreate} className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium">
          <Plus size={16} /> 지점 추가
        </button>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
            <tr>
              <th className="px-4 py-3 text-left">지점명</th>
              <th className="px-4 py-3 text-left">주소</th>
              <th className="px-4 py-3 text-left">소개</th>
              <th className="px-4 py-3 text-right">관리</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {gyms.length === 0 ? (
              <tr><td colSpan={4} className="px-4 py-8 text-center text-gray-400">등록된 지점이 없습니다.</td></tr>
            ) : gyms.map((gym) => (
              <tr key={gym.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-medium">{gym.name}</td>
                <td className="px-4 py-3 text-gray-500">{gym.address || '-'}</td>
                <td className="px-4 py-3 text-gray-500 max-w-xs truncate">{gym.description || '-'}</td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button onClick={() => openEdit(gym)} className="text-gray-400 hover:text-blue-600"><Pencil size={15} /></button>
                  <button onClick={() => { if (confirm('삭제할까요?')) deleteMutation.mutate(gym.id) }} className="text-gray-400 hover:text-red-600"><Trash2 size={15} /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl">
            <h3 className="font-bold text-lg mb-4">{modal === 'create' ? '지점 추가' : '지점 수정'}</h3>
            <div className="space-y-3">
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                placeholder="지점명 *" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <input value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })}
                placeholder="주소" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}
                placeholder="지점 소개" rows={3} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
            </div>
            <div className="flex gap-2 mt-4 justify-end">
              <button onClick={() => setModal(null)} className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-lg">취소</button>
              <button onClick={() => saveMutation.mutate()} disabled={!form.name}
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
