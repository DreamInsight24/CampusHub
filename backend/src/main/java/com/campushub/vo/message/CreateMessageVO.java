package com.campushub.vo.message;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateMessageVO {

    private Long id;
    private UUID conversationUuid;
    private UUID senderUuid;
    private String content;
    private LocalDateTime sendTime;
    private Boolean img;
    private Boolean read;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(UUID conversationUuid) {
        this.conversationUuid = conversationUuid;
    }

    public UUID getSenderUuid() {
        return senderUuid;
    }

    public void setSenderUuid(UUID senderUuid) {
        this.senderUuid = senderUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getImg() {
        return img;
    }

    public void setImg(Boolean img) {
        this.img = img;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
