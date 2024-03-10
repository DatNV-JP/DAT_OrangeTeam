<template>
  <el-card style="margin-bottom:20px;">
    <div slot="header" class="clearfix">
      <span>About me</span>
    </div>

    <div class="user-profile">
      <div class="box-center">
        <pan-thumb :image="urlViewImg+'/avatar/'+user.avatar" :height="'100px'" :width="'100px'" :hoverable="false">
          <div>Hello</div>
          {{ user.lastName }}
        </pan-thumb>
      </div>
      <div class="box-center">
        <div class="user-name text-center">{{ user.firstName + ' '+ user.lastName }}</div>
        <div class="user-role text-center text-muted">{{ user.role }}</div>
      </div>
    </div>

    <div class="user-bio">
      <div class="user-education user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="education" /><span>Thôn tin</span></div>
        <div class="user-bio-section-body">
          <div class="text-muted">
            Đã mua : {{ totalCompleted }} Đơn
          </div>
          <div class="text-muted">
            Đã hủy : {{ totalCancel }} Đơn
          </div>
          <div class="text-muted">
            Tổng tiền đã giao dịch : {{ totalPaid | currency('VND', 0, 'đ', '.', ',') }}
          </div>
        </div>
      </div>

      <!--      <div class="user-skills user-bio-section">-->
      <!--        <div class="user-bio-section-header"><svg-icon icon-class="skill" /><span>Bảng chỉ số</span></div>-->
      <!--        <div class="user-bio-section-body">-->
      <!--          <div class="progress-item">-->
      <!--            <span>Vue</span>-->
      <!--            <el-progress :percentage="totalCompleted" />-->
      <!--          </div>-->
      <!--          <div class="progress-item">-->
      <!--            <span>JavaScript</span>-->
      <!--            <el-progress :percentage="totalCancel" />-->
      <!--          </div>-->
      <!--          <div class="progress-item">-->
      <!--            <span>ESLint</span>-->
      <!--            <el-progress :percentage="100" status="success" />-->
      <!--          </div>-->
      <!--        </div>-->
      <!--      </div>-->
    </div>
  </el-card>
</template>

<script>
import PanThumb from '@/components/PanThumb'
import { getOrderByStatusAndUserId } from '@/api/procedure'
import { currency, numberFormatter } from '@/filters'

export default {
  filters: { currency, numberFormatter },
  components: { PanThumb },
  props: {
    user: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL,
      totalCompleted: 0,
      totalCancel: 0,
      totalPaid: 0
    }
  },
  watch: {
    user(val) {
      if (val.username !== undefined) {
        this.CallGetOrderByStatusAndUser()
      }
    }
  },
  methods: {
    CallGetOrderByStatusAndUser() {
      getOrderByStatusAndUserId().then(res => {
        this.totalCompleted = res.data.totalCompleted
        this.totalCancel = res.data.totalCancel
        this.totalPaid = res.data.totalPaid
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
}

.user-profile {
  .user-name {
    font-weight: bold;
  }

  .box-center {
    padding-top: 10px;
  }

  .user-role {
    padding-top: 10px;
    font-weight: 400;
    font-size: 14px;
  }

  .box-social {
    padding-top: 30px;

    .el-table {
      border-top: 1px solid #dfe6ec;
    }
  }

  .user-follow {
    padding-top: 20px;
  }
}

.user-bio {
  margin-top: 20px;
  color: #606266;

  span {
    padding-left: 4px;
  }

  .user-bio-section {
    font-size: 14px;
    padding: 15px 0;

    .user-bio-section-header {
      border-bottom: 1px solid #dfe6ec;
      padding-bottom: 10px;
      margin-bottom: 10px;
      font-weight: bold;
    }
  }
}
</style>
