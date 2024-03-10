<template>
  <div>
    <span class="my-4 d-flex justify-content-center fs-3"> Quản lý người dùng </span>
    <table-user />
  </div>
</template>

<script>
import TableUser from '@/views/admin/user/component/table.vue'
export default {
  name: 'IndexUser',
  components: { TableUser }
}
</script>

<style scoped>

</style>
