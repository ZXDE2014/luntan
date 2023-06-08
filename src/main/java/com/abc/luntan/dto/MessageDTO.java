package com.abc.luntan.dto;

import com.abc.luntan.entity.Message;

import java.util.HashMap;

public class MessageDTO extends Message {
    private HashMap<String, String> contentMap;

    public HashMap<String, String> getContentMap() {
        return contentMap;
    }

    public void setContentMap(HashMap<String, String> contentMap) {
        this.contentMap = contentMap;
    }

    public MessageDTO() {
    }

    public MessageDTO(Message message) {
        super(message.getId(), message.getFromId(), message.getToId(), message.getConversionId(), message.getContent(), message.getStatus(), message.getCreatedTime());
    }
}
