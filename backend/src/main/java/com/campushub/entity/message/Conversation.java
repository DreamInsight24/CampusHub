package com.campushub.entity.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Conversation {
    private UUID uuid;
    private UUID demand_uuid;
    private UUID engagement_uuid;
    private UUID ownerId;        // 需求发布者
    private UUID participantId;  // 响应者 / 接单者
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ArrayList<ChatMessage> messages;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ChatMessage> messages) {
        this.messages = messages;
    }

    public UUID getDemandUuid() {
        return demand_uuid;
    }

    public void setDemandUuid(UUID demand_uuid) {
        this.demand_uuid = demand_uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getEngagement_uuid() {
        return engagement_uuid;
    }

    public void setEngagement_uuid(UUID engagement_uuid) {
        this.engagement_uuid = engagement_uuid;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getParticipantId() {
        return participantId;
    }

    public void setParticipantId(UUID participantId) {
        this.participantId = participantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
