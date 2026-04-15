import { create } from 'zustand'

const useAuthStore = create((set) => ({
  token: localStorage.getItem('token') || null,
  user: JSON.parse(localStorage.getItem('user') || 'null'),
  myGym: JSON.parse(localStorage.getItem('myGym') || 'null'), // MANAGER 전용 지점 정보

  login: (token, user) => {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
    set({ token, user })
  },

  setMyGym: (gym) => {
    localStorage.setItem('myGym', JSON.stringify(gym))
    set({ myGym: gym })
  },

  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('myGym')
    set({ token: null, user: null, myGym: null })
  },
}))

export default useAuthStore
