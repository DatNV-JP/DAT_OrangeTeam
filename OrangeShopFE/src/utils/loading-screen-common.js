export default {
  data() {
    return {
      loading1: null
    }
  },
  methods: {
    showLoading() {
      this.loading1 = this.$loading({
        lock: true,
        text: 'Đang tải'
      })
      setTimeout(() => {
        this.hideLoading()
      }, 50000)
    },
    hideLoading() {
      this.loading1.close()
    }
  }
}

