package com.orange.payload.request;

import lombok.Data;

@Data
public class DeleteNotificationRequest {
    private String hashKey;
    private String[] ids;
    private Boolean isDeleteAll;
}
