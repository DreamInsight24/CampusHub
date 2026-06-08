package com.campushub.dto.message;

import java.util.UUID;

public class CreateConversationDTO {

    private UUID demandUuid;
    private UUID ownerId;
    private UUID participantId;

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
}
