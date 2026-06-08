package com.campushub.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.campushub.common.enums.EngagementStatus;

public class Engagement {

    private UUID uuid;
    private UUID demandUuid;
    private UUID publisherUuid;
    private UUID receiverUuid;
    private UUID conversationUuid;
    private EngagementStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime completedTime;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getDemandUuid() {
        return demandUuid;
    }

    public void setDemandUuid(UUID demandUuid) {
        this.demandUuid = demandUuid;
    }

    public UUID getPublisherUuid() {
        return publisherUuid;
    }

    public void setPublisherUuid(UUID publisherUuid) {
        this.publisherUuid = publisherUuid;
    }

    public UUID getReceiverUuid() {
        return receiverUuid;
    }

    public void setReceiverUuid(UUID receiverUuid) {
        this.receiverUuid = receiverUuid;
    }

    public UUID getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(UUID conversationUuid) {
        this.conversationUuid = conversationUuid;
    }

    public EngagementStatus getStatus() {
        return status;
    }

    public void setStatus(EngagementStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
}
