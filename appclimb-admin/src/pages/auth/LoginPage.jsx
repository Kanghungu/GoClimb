import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import api from '../../api/axios'
import useAuthStore from '../../store/authStore'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const login = useAuthStore((s) => s.login)
  const navigate = useNavigate()

  const getErrorMessage = (err) => {
    const payload = err.response?.data
    if (!payload) return 'Login failed.'
    if (typeof payload.message === 'string') return payload.message
    if (typeof payload.email === 'string') return payload.email
    if (typeof payload.password === 'string') return payload.password
    return 'Login failed.'
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      const { data } = await api.post('/auth/login', form)

      if (data.role !== 'MANAGER' && data.role !== 'ADMIN') {
        toast.error('Only manager or admin accounts can sign in.')
        return
      }

      login(data.accessToken, {
        nickname: data.nickname,
        role: data.role,
        userId: data.userId,
      })

      toast.success(`Welcome, ${data.nickname}`)
      navigate('/')
    } catch (err) {
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="w-full max-w-sm rounded-2xl border border-gray-200 bg-white p-8 shadow-sm">
        <div className="mb-8 text-center">
          <div className="mb-2 text-4xl">Climb</div>
          <h1 className="text-2xl font-bold text-gray-900">AppClimb</h1>
          <p className="mt-1 text-sm text-gray-500">Admin sign in</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Email</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="admin@appclimb.local"
              required
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Password</label>
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              placeholder="Enter your password"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-green-600 py-2.5 text-sm font-medium text-white transition-colors hover:bg-green-700 disabled:opacity-50"
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  )
}
