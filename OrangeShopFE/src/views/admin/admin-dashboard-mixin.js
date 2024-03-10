import CountTo from 'vue-count-to'
import { getAmountToDay, getAllCountStatus } from '@/api/procedure'

export default {
  components: {
    CountTo
  },
  data() {
    return {
      amountToday: 0,
      quantityToday: 0,
      completed: 0,
      cancel: 0
    }
  },
  created() {
    this.callGetAmountToDay()
    this.callGetCountOfAllStatus()
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    },
    async callGetAmountToDay() {
      const today = new Date()
      const fromDate = today.getFullYear() + '-' + (today.getMonth() + 1).toString().padStart(2, '0') + '-' + today.getDate().toString().padStart(2, '0')
      const tomorrow = new Date()
      tomorrow.setDate(today.getDate() + 1)
      const toDate = tomorrow.getFullYear() + '-' + (tomorrow.getMonth() + 1).toString().padStart(2, '0') + '-' + tomorrow.getDate().toString().padStart(2, '0')
      await getAmountToDay({ fromDate, toDate, status: 1 }).then(res => {
        this.amountToday = res.data.totalSold
      })
      await getAmountToDay({ fromDate, toDate, status: -1 }).then(res => {
        this.quantityToday = res.data.quantity
      })
    },
    callGetCountOfAllStatus() {
      const today = new Date()
      const tomorrow = new Date(today)
      tomorrow.setDate(today.getDate() + 1)
      const [fromDate, toDate] = [today, tomorrow].map(d => d.toISOString().slice(0, 10))
      getAllCountStatus({ fromDate, toDate }).then(({ data }) => {
        const { cancel, completed } = data
        this.cancel = cancel
        this.completed = completed
      })
    }
  }
}
