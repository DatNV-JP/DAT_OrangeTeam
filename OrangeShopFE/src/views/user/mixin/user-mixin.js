import { mapGetters } from 'vuex'
import { addCart } from '@/api/cart'
import loadingScreenCommon from '@/utils/loading-screen-common'
export default {
  mixins: [loadingScreenCommon],
  data() {
    return {
      price1: '232312',
      price2: '203233'
    }
  },
  computed: {
    ...mapGetters([
      'token',
      'cart'
    ]),
    subTotal() {
      return this.cart
        // eslint-disable-next-line no-return-assign
        .map(item => item.quantity * item.price).reduce((total, qty) => total += qty, 0)
    }
  },
  methods: {
    addToCart(product, size, color, quantity) {
      if (!this.token) {
        // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
        if (size && color) {
          this.showLoading()
          console.log(2, product)
          console.log(quantity)
          const index = this.cart ? this.cart.findIndex(item => item.productDetailId === product.productDetailId) : -1
          console.log(1, index)
          if (index !== -1) {
            if (quantity > 1) {
              this.cart[index].quantity = parseInt(this.cart[index].quantity) + parseInt(quantity)
            } else {
              this.cart[index].quantity++
            }
          } else {
            const newItem = product
            if (quantity > 1) {
              newItem.quantity = parseInt(newItem.quantity) + parseInt(quantity)
            } else {
              newItem.quantity = 1
            }
            this.cart.push(product)
          }
          this.saveToLocalStorage()
          this.$store.dispatch('cart/getCart').then(() => this.messSuccess('Thêm sản phẩm vào giỏ hàng thành công!'))
          this.hideLoading()
        } else {
          this.notifyWarning('Cảnh báo', 'Hãy chọn Size và Color trước khi thêm vào giỏ hàng!')
        }
      } else {
        if (size && color) {
          this.showLoading()
          if (!quantity) {
            quantity = 1
          } else {
            product.quantity = quantity
          }
          addCart(product).then(() => {
            console.log(product)
            this.$store.dispatch('cart/getCart').then(() => this.messSuccess('Thêm sản phẩm vào giỏ hàng thành công!'))
            this.hideLoading()
          })
        } else {
          this.notifyWarning('Cảnh báo', 'Hãy chọn Size và Color trước khi thêm vào giỏ hàng!')
        }
      }
    },
    saveToLocalStorage() {
      const json = JSON.stringify(this.cart)
      localStorage.setItem('cart', json)
    },
    registerUpdate() {
      console.log('dang ky thanh cong')
    },
    viewDetail(data) {
      this.$router.push(`/product`)
    }
  }
}
