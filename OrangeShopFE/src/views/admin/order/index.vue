<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <div class="tab-container">
      <el-tabs v-model="activeName" type="border-card">
        <el-tab-pane v-for="item in tabMapOptions" :key="item.id" :label="item.label" :name="item.key">
          <keep-alive>
            <tab-pane v-if="activeName===item.key" :type="item.key" />
          </keep-alive>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-main>
</template>

<script>
import horizontalScroll from 'el-table-horizontal-scroll'
import PageHeader from '@/views/user/component/PageHeader'
import TabPane from '@/views/tab/components/TabPane-3'

export default {
  name: 'Order',
  directives: {
    horizontalScroll
  },
  components: { PageHeader, TabPane },
  data() {
    return {
      listOrder: [],
      tabMapOptions: [
        { label: 'Tất cả', key: '0' },
        { label: 'Chờ thanh toán', key: '3' },
        { label: 'Chờ xác nhận', key: '5' },
        { label: 'Xác nhận', key: '6' },
        { label: 'Đang giao hàng', key: '4' },
        { label: 'Đã nhận hàng', key: '1' },
        { label: 'Chờ hủy', key: '7' },
        { label: 'Đã hủy', key: '2' },
        { label: 'Chờ xác nhận hoàn trả', key: '9' },
        { label: 'Hoàn trả', key: '8' }
      ],
      activeName: '0'
    }
  },
  watch: {
    activeName(val) {
      this.$router.push(`${this.$route.path}?tab=${val}`)
    }
  },
  created() {
    // init the default selected tab
    const tab = this.$route.query.tab
    if (tab) {
      this.activeName = tab
    }
  },
  methods: {
  }
}
</script>

<style>
.el-page-header__title {
  margin: auto !important;
}
.breadcrumb-container {
  padding-top: .5rem;
}
.el-carousel, .el-carousel__container {
  width: 100% !important;
  height: 100% !important;
}
.font-weight-medium {
  font-weight: 500;
}
.font-weight-semi-bold {
  font-weight: 600;
}
.text-primary {
  color: #D19C97 !important;
}
.btn-primary {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:hover {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
}

.btn-primary:focus, .btn-primary.focus {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}

.btn-primary.disabled, .btn-primary:disabled {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:not(:disabled):not(.disabled):active, .btn-primary:not(:disabled):not(.disabled).active,
.show > .btn-primary.dropdown-toggle {
  color: #fff;
  background-color: #c17a74;
  border-color: #bd726b;
}

.btn-primary:not(:disabled):not(.disabled):active:focus, .btn-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}
.bg-secondary {
  background-color: #EDF1FF !important;
}

a.bg-secondary:hover, a.bg-secondary:focus,
button.bg-secondary:hover,
button.bg-secondary:focus {
  background-color: #bac9ff !important;
}

@media (prefers-reduced-motion: reduce) {
  .form-control {
    transition: none;
  }
}

.form-control::-ms-expand {
  background-color: transparent;
  border: 0;
}

.form-control:-moz-focusring {
  color: transparent;
  text-shadow: 0 0 0 #495057;
}

.form-control:focus {
  color: #495057;
  background-color: #fff;
  border-color: #f8f0ef;
  outline: 0;
  box-shadow: none;
}

.form-control::placeholder {
  color: #999999;
  opacity: 1;
}

.form-control:disabled, .form-control[readonly] {
  background-color: #e9ecef;
  opacity: 1;
}

input[type="date"].form-control,
input[type="time"].form-control,
input[type="datetime-local"].form-control,
input[type="month"].form-control {
  appearance: none;
}
.el-radio__input.is-checked + .el-radio__label, .el-tabs__item.is-active {
  color: #bd726b;
}
.el-radio__input.is-checked .el-radio__inner {
  border-color: #bd726b;
  background: #bd726b;
}
.el-checkbox__label {
  font-size: 1.5em;
}
label {
  margin-bottom: 0;
}
.el-form--label-top .el-form-item__label {
  padding-bottom:0;
}
</style>
