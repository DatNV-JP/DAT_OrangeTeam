<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>

import WebSocketService from '@/websocketjs'
import { mapGetters } from 'vuex'

export default {
  name: 'App',
  data() {
    return {
    }
  },
  computed: {
    ...mapGetters(['username', 'roles', 'token'])
  },
  watch: {
    username: {
      async handler(newVal, oldVal) {
        // if (newVal) {
        await WebSocketService.connect(newVal, this.roles)
        this.chooseRole(this.username, this.roles)
        // }
      },
      deep: true
    },
    roles: {
      async handler(newVal, oldVal) {
        if (newVal) {
          const role = newVal.find(r => r === 'admin' || r === 'editor')
          if (!role) {
            await this.$store.dispatch('notifications/removeAllNotificationPrivate')
          }
        } else {
          await this.$store.dispatch('notifications/removeAllNotificationPrivate')
        }
        this.chooseRole(this.username, this.roles)
      },
      deep: true
    }
  },
  created() {
    this.$store.dispatch('categories/getAllCategory')
    this.$store.dispatch('cart/getCart')
  },
  mounted() {
    WebSocketService.connect(this.username, this.roles)
    WebSocketService.stompClient.onWebSocketClose = () => {
      console.log('Connection closed, attempting to reconnect')
      // WebSocketService.connect(this.username, this.roles)
    }
    WebSocketService.stompClient.onStompError = () => {
      console.error('Error occurred in StompJS')
      // WebSocketService.connect(this.username, this.roles)
    }
    WebSocketService.stompClient.onWebSocketError = () => {
      console.error('Error occurred in WebSocket')
      // WebSocketService.connect(this.username, this.roles)
    }
    WebSocketService.stompClient.onWebSocketOpen = () => {
      console.log('WebSocket opened')
    }
    WebSocketService.stompClient.onWebSocketClose = () => {
      console.log('WebSocket closed')
    }
  },
  beforeDestroy() {
    WebSocketService.disconnect()
  },
  methods: {
    chooseRole(username, roles) {
      this.$store.dispatch('notifications/getAllNotify', { hashKey1: username, hashKey2: null })
      if (roles) {
        const role = roles.find(r => r === 'admin' || r === 'editor')
        if (role) {
          this.$store.dispatch('notifications/getAllNotify', { hashKey1: username, hashKey2: 'private' })
        }
      }
    }
  }
}
</script>
