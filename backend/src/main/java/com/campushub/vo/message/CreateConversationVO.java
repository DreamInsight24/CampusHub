package com.campushub.vo.message;

import java.util.UUID;

public class CreateConversationVO {

    private UUID conversationUuid;
    private UUID demandUuid;
    private UUID ownerId;
    private UUID participantId;
    private String status;

    public UUID getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(UUID conversationUuid) {
        this.conversationUuid = conversationUuid;
    }

    public UUID getDemandUuid() {
        return demandUuid;
    }

    public void setDemandUuid(UUID demandUuid) {
        this.demandUuid = demandUuid;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
