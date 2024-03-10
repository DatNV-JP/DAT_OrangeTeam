import { getListCart } from '@/api/cart'
import { getToken } from '@/utils/auth'

const state = {
  cart: []
}

const mutations = {
  // SET_CART: (state, item) => {
  //   const cartTemp = state.cart.find(it => it.productDetailId === item.productDetailId)
  //   console.log(1, state.cart)
  //   console.log(2, item)
  //   if (cartTemp) {
  //     state.cart.push(item)
  //   } else {
  //     state.cart.forEach(it => {
  //       if (it.productDetailId === item.productDetailId) {
  //         it = item
  //       }
  //     })
  //   }
  // },
  SET_ALL_CART: (state, cart) => {
    state.cart = cart
  }
}

const actions = {
  // add cart
  // addCart({ commit }, item) {
  //   return new Promise((resolve, reject) => {
  //     addCart(item).then(response => {
  //       const { data } = response
  //       commit('SET_CART', data)
  //       resolve()
  //     }).catch(error => {
  //       reject(error)
  //     })
  //   })
  // },

  // get cart
  getCart({ commit, state }) {
    return new Promise((resolve, reject) => {
      const hasToken = getToken()
      if (hasToken) {
        getListCart().then(response => {
          const { data } = response
          commit('SET_ALL_CART', data)
          resolve(data)
        }).catch(error => {
          reject(error)
        })
      } else {
        const data = JSON.parse(localStorage.getItem('cart')) ? JSON.parse(localStorage.getItem('cart')) : []
        commit('SET_ALL_CART', data)
        resolve(data)
      }
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
