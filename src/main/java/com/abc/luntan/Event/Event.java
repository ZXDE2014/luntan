package com.abc.luntan.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String routingKey;
    // 发消息的人
    private String userId;
    private String entityType;
    private String entityId;
    // 收消息的人
    private String entityUserId;

}
