<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import { getAllCountStatus } from '@/api/procedure'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      completed: 0,
      cancel: 0,
      WaitForPay: 0,
      DeliveryInProgress: 0,
      WaitForConfirmation: 0,
      Confirmed: 0
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.callGetAllCountStatus()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    async callGetAllCountStatus() {
      var fromDate = new Date()
      var toDate = new Date()
      toDate.setDate(fromDate.getDate() + 1)
      var options = { year: 'numeric', month: '2-digit', day: '2-digit' }
      fromDate = fromDate.toLocaleDateString('en-US', options)
      toDate = toDate.toLocaleDateString('en-US', options)
      await getAllCountStatus().then(res => {
        this.completed = res.data.completed
        this.cancel = res.data.cancel
        this.WaitForPay = res.data.waitForPay
        this.DeliveryInProgress = res.data.deliveryInProgress
        this.WaitForConfirmation = res.data.waitForConfirmation
        this.Confirmed = res.data.confirmed
      })
      this.initChart()
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['Completed', 'Cancel', 'Wait for pay', 'Delivery in progress', 'Wait for confirmation', 'Confirmed']
        },
        series: [
          {
            name: 'WEEKLY WRITE ARTICLES',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: [
              { value: this.completed, name: 'Completed' },
              { value: this.cancel, name: 'Cancel' },
              { value: this.WaitForPay, name: 'Wait for pay' },
              { value: this.DeliveryInProgress, name: 'Delivery in progress' },
              { value: this.WaitForConfirmation, name: 'Wait for confirmation' },
              { value: this.Confirmed, name: 'Confirmed' }
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
