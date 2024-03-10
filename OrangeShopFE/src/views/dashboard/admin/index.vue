<template>
  <div class="dashboard-editor-container">

    <panel-group @handleSetLineChartData="handleSetLineChartData" />

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <el-row :gutter="32">
      <!--      <el-col :xs="24" :sm="24" :lg="8">-->
      <!--        <div class="chart-wrapper">-->
      <!--          <raddar-chart />-->
      <!--        </div>-->
      <!--      </el-col>-->
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <pie-chart />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <bar-chart />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 24}" :xl="{span: 24}" style="padding-right:8px;margin-bottom:30px;">
        <transaction-table />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import PanelGroup from './components/PanelGroup'
import LineChart from './components/LineChart'
import PieChart from './components/PieChart'
import BarChart from './components/BarChart'
import TransactionTable from './components/TransactionTable'
import { getCountOrdersByWeek } from '@/api/procedure'

const lineChartData = {
  newVisitis: {
    expectedData: [],
    actualData: []
  },
  messages: {
    expectedData: [],
    actualData: []
  },
  purchases: {
    expectedData: [],
    actualData: []
  },
  shoppings: {
    expectedData: [],
    actualData: []
  }
}

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup,
    LineChart,
    PieChart,
    BarChart,
    TransactionTable
  },
  data() {
    return {
      lineChartData: lineChartData.newVisitis
    }
  },
  created() {
    this.addData()
  },
  methods: {
    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type]
    },
    addData() {
      getCountOrdersByWeek().then(res => {
        for (let i = 0; i < res.data.length; i++) {
          lineChartData.newVisitis.expectedData.push(res.data[i].countComplatedLastWeek)
          lineChartData.newVisitis.actualData.push(res.data[i].countComplatedThisWeek)
          lineChartData.messages.expectedData.push(res.data[i].countCancelLastWeek)
          lineChartData.messages.expectedData.push(res.data[i].countCancelThisWeek)
          lineChartData.shoppings.expectedData.push(res.data[i].orderCountLastWeek)
          lineChartData.shoppings.actualData.push(res.data[i].orderCountThisWeek)
          lineChartData.purchases.expectedData.push(res.data[i].totalSoldLastWeek)
          lineChartData.purchases.actualData.push(res.data[i].totalSoldThisWeek)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
