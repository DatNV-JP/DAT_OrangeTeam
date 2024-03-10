package com.orange.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private String url;
    private String content;
    private String title;
    private String toTopic;
    private String topicUrl;
    private Boolean isRead;
}

