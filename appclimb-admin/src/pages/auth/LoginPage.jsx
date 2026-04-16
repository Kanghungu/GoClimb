import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'
import toast from 'react-hot-toast'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const { login, setMyGym } = useAuthStore()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', form)
      if (data.role !== 'MANAGER' && data.role !== 'ADMIN') {
        toast.error('관리자 계정으로만 로그인할 수 있습니다.')
        return
      }
      login(data.accessToken, { nickname: data.nickname, role: data.role, userId: data.userId })

      // MANAGER면 내 지점 정보 가져와서 저장
      if (data.role === 'MANAGER') {
        try {
          const gymRes = await api.get('/me/gym', {
            headers: { Authorization: `Bearer ${data.accessToken}` }
          })
          setMyGym(gymRes.data)
        } catch {
          toast.error('배정된 지점이 없습니다. 관리자에게 문의하세요.')
          return
        }
      }

      toast.success(`환영합니다, ${data.nickname}님!`)
      navigate('/')
    } catch (err) {
      toast.error(err.response?.data?.message || '로그인에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 w-full max-w-sm">
        <div className="text-center mb-8">
          <div className="text-4xl mb-2">🧗</div>
          <h1 className="text-2xl font-bold text-gray-900">AppClimb</h1>
          <p className="text-sm text-gray-500 mt-1">관리자 로그인</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="admin@example.com"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="••••••••"
              required
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 hover:bg-green-700 text-white font-medium py-2.5 rounded-lg text-sm transition-colors disabled:opacity-50"
          >
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
        <div className="mt-5 pt-5 border-t border-gray-100 text-center">
          <p className="text-xs text-gray-400 mb-2">지점 등록을 원하시나요?</p>
          <Link
            to="/apply"
            className="inline-block w-full border border-green-600 text-green-600 hover:bg-green-50 font-medium py-2.5 rounded-lg text-sm transition-colors"
          >
            지점 가입 신청하기
          </Link>
        </div>
      </div>
    </div>
  )
}
