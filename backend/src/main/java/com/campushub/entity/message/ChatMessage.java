package com.campushub.entity.message;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage {
    private Long id;
    private UUID conversation_uuid;
    private UUID user_uuid;             //sender
    private String message;
    private LocalDateTime time;
    private Boolean is_img;
    private Boolean read;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getConversation_uuid() {
        return conversation_uuid;
    }

    public void setConversation_uuid(UUID conversation_uuid) {
        this.conversation_uuid = conversation_uuid;
    }

    public UUID getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(UUID user_uuid) {
        this.user_uuid = user_uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Boolean isImg() {
        return is_img;
    }

    public void setImg(Boolean is_img) {
        this.is_img = is_img;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
