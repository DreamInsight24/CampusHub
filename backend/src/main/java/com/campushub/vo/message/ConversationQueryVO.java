package com.campushub.vo.message;

import java.util.List;

public class ConversationQueryVO {

    private List<ConversationListVO> conversations;

    public List<ConversationListVO> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationListVO> conversations) {
        this.conversations = conversations;
    }
}
