import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/axios'
import toast from 'react-hot-toast'
import { CheckCircle } from 'lucide-react'

export default function ApplyPage() {
  const navigate = useNavigate()
  const [step, setStep] = useState('form') // 'form' | 'done'
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({
    nickname: '',
    email: '',
    password: '',
    passwordConfirm: '',
    gymName: '',
    gymAddress: '',
    gymDescription: '',
  })

  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.password !== form.passwordConfirm) {
      toast.error('비밀번호가 일치하지 않습니다.')
      return
    }
    setLoading(true)
    try {
      await api.post('/auth/apply', {
        nickname: form.nickname,
        email: form.email,
        password: form.password,
        gymName: form.gymName,
        gymAddress: form.gymAddress,
        gymDescription: form.gymDescription,
      })
      setStep('done')
    } catch (err) {
      toast.error(err.response?.data?.message || '신청 중 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  if (step === 'done') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 w-full max-w-sm text-center">
          <div className="flex justify-center mb-4">
            <div className="w-14 h-14 bg-green-100 rounded-full flex items-center justify-center">
              <CheckCircle className="text-green-600" size={30} />
            </div>
          </div>
          <h2 className="text-xl font-bold text-gray-900 mb-2">신청 완료!</h2>
          <p className="text-sm text-gray-500 mb-1">지점 가입 신청이 접수됐습니다.</p>
          <p className="text-sm text-gray-500 mb-6">
            웹 관리자 승인 후 이메일로 로그인하실 수 있습니다.
          </p>
          <Link
            to="/login"
            className="block w-full bg-green-600 hover:bg-green-700 text-white font-medium py-2.5 rounded-lg text-sm transition-colors"
          >
            로그인 페이지로 돌아가기
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-10">
      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-8 w-full max-w-md">
        <div className="text-center mb-7">
          <div className="text-4xl mb-2">🧗</div>
          <h1 className="text-2xl font-bold text-gray-900">지점 가입 신청</h1>
          <p className="text-sm text-gray-500 mt-1">계정과 지점 정보를 입력해주세요.</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          {/* 계정 정보 섹션 */}
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">계정 정보</p>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">이름 (닉네임)</label>
                <input
                  type="text"
                  value={form.nickname}
                  onChange={set('nickname')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="홍길동"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">이메일</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={set('email')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="manager@example.com"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
                <input
                  type="password"
                  value={form.password}
                  onChange={set('password')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="6자 이상"
                  required
                  minLength={6}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호 확인</label>
                <input
                  type="password"
                  value={form.passwordConfirm}
                  onChange={set('passwordConfirm')}
                  className={`w-full px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500 ${
                    form.passwordConfirm && form.password !== form.passwordConfirm
                      ? 'border-red-400 bg-red-50'
                      : 'border-gray-300'
                  }`}
                  placeholder="비밀번호 재입력"
                  required
                />
                {form.passwordConfirm && form.password !== form.passwordConfirm && (
                  <p className="text-xs text-red-500 mt-1">비밀번호가 일치하지 않습니다.</p>
                )}
              </div>
            </div>
          </div>

          {/* 지점 정보 섹션 */}
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider mb-3">지점 정보</p>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">지점명</label>
                <input
                  type="text"
                  value={form.gymName}
                  onChange={set('gymName')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="클라임 강남점"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">주소 <span className="text-gray-400 font-normal">(선택)</span></label>
                <input
                  type="text"
                  value={form.gymAddress}
                  onChange={set('gymAddress')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                  placeholder="서울시 강남구 ..."
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">소개 <span className="text-gray-400 font-normal">(선택)</span></label>
                <textarea
                  value={form.gymDescription}
                  onChange={set('gymDescription')}
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-green-500 resize-none"
                  placeholder="지점 소개를 간단히 적어주세요."
                />
              </div>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading || (form.passwordConfirm && form.password !== form.passwordConfirm)}
            className="w-full bg-green-600 hover:bg-green-700 text-white font-medium py-2.5 rounded-lg text-sm transition-colors disabled:opacity-50"
          >
            {loading ? '신청 중...' : '가입 신청하기'}
          </button>
        </form>

        <p className="text-center text-xs text-gray-400 mt-5">
          이미 계정이 있으신가요?{' '}
          <Link to="/login" className="text-green-600 hover:underline font-medium">
            로그인
          </Link>
        </p>
      </div>
    </div>
  )
}
