<template>
  <el-skeleton :rows="15" animated />
</template>

<script>
import { getPaymentInfo } from '@/api/payment'
import { mapGetters } from 'vuex'

export default {
  name: 'AfterPayment',
  computed: {
    ...mapGetters(['token'])
  },
  created() {
    this.getParams()
  },
  methods: {
    getParams() {
      console.log(this.$route.query)
      getPaymentInfo(this.$route.query).then(res => {
        if (this.token) {
          // this.$router.push('/user/tracking-page')
          if (res.code === 400) {
            this.$router.push(`/user/tracking-page/${res.data}`)
          } else {
            this.$router.push(`/user/tracking-page/${res.data.id}`)
          }
        } else {
          this.$router.push('/home')
        }
      }).catch(() => {
        // this.$router.push(`/user/order`)
      })
    }
  }
}
</script>

<style scoped>

</style>
