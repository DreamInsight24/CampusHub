package com.campushub.vo.message;

import java.util.List;
import java.util.UUID;

public class MessageQueryVO {

    private UUID conversationUuid;
    private List<Object> messages;

    public UUID getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(UUID conversationUuid) {
        this.conversationUuid = conversationUuid;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
        this.messages = messages;
    }
}
