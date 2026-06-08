package com.campushub.dto.message;

import java.util.UUID;

public class MessageQueryDTO {

    private UUID conversationUuid;

    public UUID getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(UUID conversationUuid) {
        this.conversationUuid = conversationUuid;
    }
}
