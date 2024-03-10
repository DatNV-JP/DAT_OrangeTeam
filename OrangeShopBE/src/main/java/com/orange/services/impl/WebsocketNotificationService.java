package com.orange.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.domain.dto.NotificationDTO;
import com.orange.exception.EntityType;
import com.orange.exception.ExceptionType;
import com.orange.exception.GlobalException;
import com.orange.payload.request.DeleteNotificationRequest;
import com.orange.redis.CacheService;
import com.orange.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsocketNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketNotificationService.class);
    private final static String HASH_KEY = "Notification_";
    private final static Long TIMEOUT = 30L;
    private final static TimeUnit timeUnit = TimeUnit.DAYS;
    private final CacheService cacheService;
    private final SimpMessagingTemplate messagingTemplate;

    public void pushNotification(NotificationDTO notification) {
        try {
            messagingTemplate.convertAndSend(notification.getTopicUrl(), notification);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

    public void pushAndSaveNotification(NotificationDTO notification) {
        try {
            saveNotification(notification);
            messagingTemplate.convertAndSend(notification.getTopicUrl(), notification);
            System.out.println(notification);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

    public void saveNotification(NotificationDTO notification) {
        if (notification.getId() == null) {
            String name = String.valueOf(System.currentTimeMillis()) + String.valueOf((Math.random() * 9999999));
            String key = Integer.toHexString(name.hashCode());
            notification.setId(key);
        }
        String value = JsonUtils.toJson(notification);
        cacheService.hmSetWithExpire(HASH_KEY + notification.getToTopic(), notification.getId(), value, TIMEOUT, TimeUnit.DAYS);
    }

    public List<NotificationDTO> getAllNotification(String hashKey) {
        return cacheService.hmGetValues(HASH_KEY + hashKey).stream()
                .map(c -> {
                    try {
                        return JsonUtils.fromJson(c.toString(), NotificationDTO.class);
                    } catch (JsonProcessingException e) {
                        throw GlobalException.throwException(EntityType.product,
                                ExceptionType.ENTITY_NOT_FOUND,
                                "Có lỗi xảy ra khi lấy thông báo!");
                    }
                }).collect(Collectors.toList());
    }

    public String deleteNotificationItemByIds(DeleteNotificationRequest deleteNotificationRequest) {
        if (deleteNotificationRequest.getIsDeleteAll()) {
            List<NotificationDTO> notificationDTOS = this.getAllNotification(deleteNotificationRequest.getHashKey());
            String[] ids = notificationDTOS.stream()
                    .map(NotificationDTO::getId)
                    .toArray(String[]::new);
            cacheService.hmDelete(HASH_KEY + deleteNotificationRequest.getHashKey(), ids);
        } else {
            cacheService.hmDelete(HASH_KEY + deleteNotificationRequest.getHashKey(), deleteNotificationRequest.getIds());
        }
        return "Đã xóa thông báo!!";
    }

    public String readAllNotification(String hashKey) {
        List<NotificationDTO> notificationDTOS = this.getAllNotification(hashKey);
        for (NotificationDTO notification : notificationDTOS) {
            notification.setIsRead(true);
            this.saveNotification(notification);
        }
        return "Đã đọc tất cả thông báo!!";
    }

    public String readNotification(NotificationDTO notificationDTO) {
        this.saveNotification(notificationDTO);
        return "Đã đọc thông báo!!";
    }
}
