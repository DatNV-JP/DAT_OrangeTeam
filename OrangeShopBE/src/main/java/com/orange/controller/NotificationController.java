package com.orange.controller;

import com.orange.common.payload.Result;
import com.orange.domain.dto.NotificationDTO;
import com.orange.payload.request.DeleteNotificationRequest;
import com.orange.services.impl.WebsocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("notification")
public class NotificationController {
    private final WebsocketNotificationService wsn;

    @GetMapping("websocket")
    public Result<?> getAllNotification(@RequestParam String hashKey) {
        return Result.result(200, "Lấy dữ liệu thông báo thành công!", wsn.getAllNotification(hashKey));
    }

    @PostMapping("websocket")
    public Result<?> deleteNotification(@RequestBody DeleteNotificationRequest deleteNotificationRequest) {
        return Result.result(200, wsn.deleteNotificationItemByIds(deleteNotificationRequest), null);
    }

    @GetMapping("websocket/read")
    public Result<?> readAllNotification(@RequestParam String hashKey) {
        return Result.result(200, wsn.readAllNotification(hashKey), null);
    }

    @PostMapping("websocket/read")
    public Result<?> readNotification(@RequestBody NotificationDTO notificationDTO) {
        return Result.result(200, wsn.readNotification(notificationDTO), null);
    }
}
