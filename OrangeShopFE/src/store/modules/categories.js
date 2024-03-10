import { getAllCategory } from '@/api/category'

const state = {
  categories: []
}

const mutations = {
  SET_ALL_CATEGORY: (state, categories) => {
    state.categories = categories
  }
}

const actions = {

  // get cate
  getAllCategory({ commit, state }) {
    return new Promise((resolve, reject) => {
      getAllCategory().then(response => {
        const { data } = response
        commit('SET_ALL_CATEGORY', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
