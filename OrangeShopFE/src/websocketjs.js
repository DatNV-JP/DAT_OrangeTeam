import Stomp from 'stompjs'
import { Notification } from 'element-ui'
import store from '@/store'
const urlSocket = process.env.VUE_APP_SOCKET_URL
const WebSocketService = {
  stompClient: null,
  isAlive: false,

  async connect(username, roles) {
    const socket = new WebSocket(urlSocket)
    await new Promise(resolve => {
      socket.onopen = resolve
      this.stompClient = Stomp.over(socket)
      this.stompClient.debug = null
      this.stompClient.reconnect_delay = 5000
      this.stompClient.connect({}, async() => {
        if (username) {
          this.stompClient.subscribe(`/topic/${username}`, async(message) => {
            await this.showNotification(message.body, username, roles)
          })
        }
        if (roles) {
          const role = roles.find(r => r === 'admin' || r === 'editor')
          if (role) {
            this.stompClient.subscribe('/topic/private/admin', async(message) => {
              await this.showNotification(message.body, username, roles)
            })
          }
        }
        await this.getNotification(username, roles)
        console.log('connected websocket')
        this.isAlive = true
      }, (error) => {
        console.log('error websocket', error)
        this.connect(username, roles)
      })
    })
  },

  disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect()
    }
  },

  async showNotification(message, username, roles) {
    const notification = JSON.parse(message)
    await this.getNotification(username, roles)
    Notification({
      title: notification.title,
      message: notification.content,
      position: 'bottom-right',
      duration: 3000
    })
  },
  async getNotification(username, roles) {
    if (roles) {
      const role = roles.find(r => r === 'admin' || r === 'editor')
      await store.dispatch('notifications/getAllNotify', { hashKey1: username, hashKey2: role ? 'private' : null })
    }
  }
}

export default WebSocketService
