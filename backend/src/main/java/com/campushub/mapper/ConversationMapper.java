package com.campushub.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;

import com.campushub.entity.message.ChatMessage;
import com.campushub.entity.message.Conversation;
import com.campushub.vo.message.ConversationListVO;

public interface ConversationMapper {
    //需要的接口自行补充
    int insertConversation(Conversation conversation);

    int updateConversation(Conversation conversation);

    int updateConversationStatus(@Param("uuid") UUID uuid, @Param("status") String status);

    int deleteConversationByUuid(@Param("uuid") UUID uuid);

    Conversation selectConversationByUuid(@Param("uuid") UUID uuid);

    List<Conversation> selectAllConversations();

    Conversation selectConversationByEngagementUuid(@Param("engagement_uuid") UUID engagement_uuid);

    List<Conversation> selectConversationsByDemandUuid(@Param("demand_uuid") UUID demand_uuid);

    List<Conversation> selectConversationsByOwnerId(@Param("ownerId") UUID ownerId);

    List<Conversation> selectConversationsByParticipantId(@Param("participantId") UUID participantId);

    List<Conversation> selectConversationsByUserId(@Param("userId") UUID userId);

    List<ConversationListVO> selectConversationListByUserId(@Param("userId") UUID userId);

    List<Conversation> selectConversationsByStatus(@Param("status") String status);

    int insertChatMessage(ChatMessage chatMessage);

    int updateChatMessage(ChatMessage chatMessage);

    int updateMessagesReadByConversationUuid(
            @Param("conversation_uuid") UUID conversation_uuid,
            @Param("user_uuid") UUID user_uuid);

    int deleteMessagesByConversationUuid(@Param("conversation_uuid") UUID conversation_uuid);

    List<ChatMessage> selectMessagesByConversationUuid(@Param("conversation_uuid") UUID conversation_uuid);

    List<ChatMessage> selectUnreadMessagesByConversationUuid(
            @Param("conversation_uuid") UUID conversation_uuid,
            @Param("user_uuid") UUID user_uuid);

    Conversation selectConversationByDemandAndUsers(
            @Param("demandUuid") UUID demandUuid,
            @Param("userA") UUID userA,
            @Param("userB") UUID userB);

    int touchConversation(@Param("uuid") UUID uuid);
}
