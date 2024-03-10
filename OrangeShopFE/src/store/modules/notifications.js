import { getNotification } from '@/api/notification'

const state = {
  notifications: [],
  notificationsPrivate: [],
  notificationsUser: []
}

const mutations = {
  SET_ALL_NOTIFICATION_USER: (state, notifications) => {
    state.notificationsUser = notifications
  },
  SET_ALL_NOTIFICATION_PRIVATE: (state, notifications) => {
    state.notificationsPrivate = notifications
  },
  SET_ALL_NOTIFICATION: (state, notifications) => {
    state.notifications = notifications
  },
  REMOVE_ALL_NOTIFICATION_PRIVATE: (state, notifications) => {
    state.notificationsPrivate = notifications
  }
}

const actions = {
  // get cate
  getAllNotification({ commit, state }, hashKey) {
    return new Promise((resolve, reject) => {
      getNotification(hashKey).then(response => {
        const { data } = response
        if (hashKey === 'private') {
          commit('SET_ALL_NOTIFICATION_PRIVATE', data)
        } else {
          commit('SET_ALL_NOTIFICATION_USER', data)
        }
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getAllNotify({ commit, state }, listHashKey) {
    return new Promise((resolve, reject) => {
      getNotification(listHashKey.hashKey1).then(response => {
        const { data } = response
        commit('SET_ALL_NOTIFICATION_USER', data)
        resolve(data)
      }).finally(() => {
        if (listHashKey.hashKey2) {
          getNotification('private').then(response => {
            const { data } = response
            commit('SET_ALL_NOTIFICATION_PRIVATE', data)
            resolve(data)
          }).finally(() => {
            commit('SET_ALL_NOTIFICATION', [...state.notificationsUser, ...state.notificationsPrivate])
          })
        } else {
          commit('SET_ALL_NOTIFICATION', [...state.notificationsUser])
        }
        // if (state.notificationsPrivate) {
        //   console.log('private', state.notificationsUser, state.notificationsPrivate)
        //   commit('SET_ALL_NOTIFICATION', [...state.notificationsUser, ...state.notificationsPrivate])
        // } else {
        //   console.log('user', state.notificationsUser)
        //   commit('SET_ALL_NOTIFICATION', [...state.notificationsUser])
        // }
      })
    })
  },
  removeAllNotificationPrivate({ commit, state }) {
    return new Promise((resolve, reject) => {
      commit('REMOVE_ALL_NOTIFICATION_PRIVATE', [])
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
