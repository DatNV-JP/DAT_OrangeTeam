export default {
  data() {
    return {
      duration: 3000
    }
  },
  methods: {
    notifySuccess(title, message) {
      this.$notify({
        title: title,
        message: message,
        type: 'success',
        duration: this.duration,
        customClass: 'loading-backgroup-success'
      })
    },
    messSuccess(message) {
      this.$message({
        message: message,
        type: 'success',
        duration: this.duration
      })
    },
    notifyWarning(title, message) {
      this.$notify({
        title: title,
        message: message,
        type: 'warning',
        duration: this.duration,
        customClass: 'loading-backgroup-warning'
      })
    },
    notifyInfo(title, message) {
      this.$notify({
        title: title,
        message: message,
        type: 'info',
        duration: this.duration,
        customClass: 'loading-backgroup-info'
      })
    },
    notifyError(title, message) {
      this.$notify({
        title: title,
        message: message,
        type: 'error',
        duration: this.duration,
        customClass: 'loading-backgroup-error'
      })
    },
    notifyNormally(title, message) {
      this.$notify({
        title: title,
        message: message,
        duration: this.duration,
        position: 'bottom-right'
      })
    }
  }
}

