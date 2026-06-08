package com.campushub.service.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.campushub.common.Result;
import com.campushub.dto.message.CreateConversationDTO;
import com.campushub.dto.message.CreateMessageDTO;
import com.campushub.dto.message.MessageQueryDTO;
import com.campushub.dto.message.ReadConversationDTO;
import com.campushub.entity.User;
import com.campushub.entity.demand.Demand;
import com.campushub.entity.message.ChatMessage;
import com.campushub.entity.message.Conversation;
import com.campushub.mapper.ConversationMapper;
import com.campushub.mapper.DemandMapper;
import com.campushub.mapper.UsersMapper;
import com.campushub.util.TokenUtil;
import com.campushub.vo.message.ConversationQueryVO;
import com.campushub.vo.message.CreateConversationVO;
import com.campushub.vo.message.CreateMessageVO;
import com.campushub.vo.message.MessageQueryVO;
import com.campushub.vo.message.ReadConversationVO;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private DemandMapper demandMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ConversationService conversationService;

    private UUID userA;
    private UUID userB;
    private UUID demandUuid;
    private UUID conversationUuid;
    private String validToken;

    @BeforeEach
    void setUp() {
        userA = UUID.randomUUID();
        userB = UUID.randomUUID();
        demandUuid = UUID.randomUUID();
        conversationUuid = UUID.randomUUID();
        validToken = TokenUtil.generateToken(userA);
    }

    // ==================== listConversations ====================

    @Test
    void listConversationsShouldReturnConversationsForValidToken() {
        List<Conversation> conversations = new ArrayList<>();
        Conversation conv = buildConversation(conversationUuid, demandUuid, userA, userB);
        conversations.add(conv);

        when(conversationMapper.selectConversationsByUserId(userA)).thenReturn(conversations);

        Result<ConversationQueryVO> result = conversationService.listConversations(validToken);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getConversations().size());
        verify(conversationMapper).selectConversationsByUserId(userA);
    }

    @Test
    void listConversationsShouldReturn401WhenTokenIsNull() {
        Result<ConversationQueryVO> result = conversationService.listConversations(null);
        assertEquals(401, result.getCode());
        assertEquals("未登录，请先登录", result.getMessage());
    }

    @Test
    void listConversationsShouldReturn401WhenTokenIsBlank() {
        Result<ConversationQueryVO> result = conversationService.listConversations("   ");
        assertEquals(401, result.getCode());
    }

    @Test
    void listConversationsShouldReturn401WhenTokenIsInvalid() {
        Result<ConversationQueryVO> result = conversationService.listConversations("garbage-token");
        assertEquals(401, result.getCode());
        assertEquals("登录已过期，请重新登录", result.getMessage());
    }

    @Test
    void listConversationsShouldReturnEmptyListWhenNoConversations() {
        when(conversationMapper.selectConversationsByUserId(userA)).thenReturn(new ArrayList<>());

        Result<ConversationQueryVO> result = conversationService.listConversations(validToken);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().getConversations().size());
    }

    // ==================== createConversation ====================

    @Test
    void createConversationShouldSucceed() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userB);

        when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(new Demand());
        when(usersMapper.selectUserByUuid(userA)).thenReturn(new User());
        when(usersMapper.selectUserByUuid(userB)).thenReturn(new User());
        when(conversationMapper.selectConversationByDemandAndUsers(demandUuid, userA, userB))
                .thenReturn(null);

        Result<CreateConversationVO> result = conversationService.createConversation(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getConversationUuid());
        assertEquals(demandUuid, result.getData().getDemandUuid());
        assertEquals(userA, result.getData().getOwnerId());
        assertEquals(userB, result.getData().getParticipantId());
        assertEquals("ACTIVE", result.getData().getStatus());

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationMapper).insertConversation(captor.capture());
        assertEquals(demandUuid, captor.getValue().getDemandUuid());
        assertEquals(userA, captor.getValue().getOwnerId());
        assertEquals(userB, captor.getValue().getParticipantId());
    }

    @Test
    void createConversationShouldReturn400WhenDTOIsNull() {
        Result<CreateConversationVO> result = conversationService.createConversation(null);
        assertEquals(400, result.getCode());
    }

    @Test
    void createConversationShouldReturn400WhenDemandUuidIsNull() {
        CreateConversationDTO dto = buildCreateConvDTO(null, userA, userB);
        Result<CreateConversationVO> result = conversationService.createConversation(dto);
        assertEquals(400, result.getCode());
        assertEquals("需求ID不能为空", result.getMessage());
    }

    @Test
    void createConversationShouldReturn400WhenOwnerIdIsNull() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, null, userB);
        Result<CreateConversationVO> result = conversationService.createConversation(dto);
        assertEquals(400, result.getCode());
        assertEquals("发布者ID不能为空", result.getMessage());
    }

    @Test
    void createConversationShouldReturn400WhenParticipantIdIsNull() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, null);
        Result<CreateConversationVO> result = conversationService.createConversation(dto);
        assertEquals(400, result.getCode());
        assertEquals("参与者ID不能为空", result.getMessage());
    }

    @Test
    void createConversationShouldReturn400WhenOwnerEqualsParticipant() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userA);
        Result<CreateConversationVO> result = conversationService.createConversation(dto);
        assertEquals(400, result.getCode());
        assertEquals("不能与自己创建会话", result.getMessage());
    }

    @Test
    void createConversationShouldReturn404WhenDemandNotFound() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userB);
        when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(null);

        Result<CreateConversationVO> result = conversationService.createConversation(dto);

        assertEquals(404, result.getCode());
        assertEquals("需求不存在", result.getMessage());
        verify(conversationMapper, never()).insertConversation(any());
    }

    @Test
    void createConversationShouldReturn404WhenOwnerNotFound() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userB);
        when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(new Demand());
        when(usersMapper.selectUserByUuid(userA)).thenReturn(null);

        Result<CreateConversationVO> result = conversationService.createConversation(dto);

        assertEquals(404, result.getCode());
        assertEquals("发布者不存在", result.getMessage());
        verify(conversationMapper, never()).insertConversation(any());
    }

    @Test
    void createConversationShouldReturn404WhenParticipantNotFound() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userB);
        when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(new Demand());
        when(usersMapper.selectUserByUuid(userA)).thenReturn(new User());
        when(usersMapper.selectUserByUuid(userB)).thenReturn(null);

        Result<CreateConversationVO> result = conversationService.createConversation(dto);

        assertEquals(404, result.getCode());
        assertEquals("参与者不存在", result.getMessage());
        verify(conversationMapper, never()).insertConversation(any());
    }

    @Test
    void createConversationShouldReturn409WhenDuplicate() {
        CreateConversationDTO dto = buildCreateConvDTO(demandUuid, userA, userB);
        when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(new Demand());
        when(usersMapper.selectUserByUuid(userA)).thenReturn(new User());
        when(usersMapper.selectUserByUuid(userB)).thenReturn(new User());
        when(conversationMapper.selectConversationByDemandAndUsers(demandUuid, userA, userB))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        Result<CreateConversationVO> result = conversationService.createConversation(dto);

        assertEquals(409, result.getCode());
        assertEquals("会话已存在，请勿重复创建", result.getMessage());
        verify(conversationMapper, never()).insertConversation(any());
    }

    // ==================== listMessages ====================

    @Test
    void listMessagesShouldReturnMessages() {
        MessageQueryDTO dto = new MessageQueryDTO();
        dto.setConversationUuid(conversationUuid);

        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage msg = new ChatMessage();
        msg.setConversation_uuid(conversationUuid);
        msg.setUser_uuid(userA);
        msg.setMessage("Hello");
        messages.add(msg);

        when(conversationMapper.selectMessagesByConversationUuid(conversationUuid)).thenReturn(messages);

        Result<MessageQueryVO> result = conversationService.listMessages(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(conversationUuid, result.getData().getConversationUuid());
        assertEquals(1, result.getData().getMessages().size());
    }

    @Test
    void listMessagesShouldReturn400WhenConversationUuidIsNull() {
        MessageQueryDTO dto = new MessageQueryDTO();
        Result<MessageQueryVO> result = conversationService.listMessages(dto);
        assertEquals(400, result.getCode());
        assertEquals("会话ID不能为空", result.getMessage());
    }

    @Test
    void listMessagesShouldReturn400WhenDTOIsNull() {
        Result<MessageQueryVO> result = conversationService.listMessages(null);
        assertEquals(400, result.getCode());
    }

    @Test
    void listMessagesShouldReturn404WhenConversationNotFound() {
        MessageQueryDTO dto = new MessageQueryDTO();
        dto.setConversationUuid(conversationUuid);
        when(conversationMapper.selectConversationByUuid(conversationUuid)).thenReturn(null);

        Result<MessageQueryVO> result = conversationService.listMessages(dto);

        assertEquals(404, result.getCode());
        assertEquals("会话不存在", result.getMessage());
    }

    // ==================== createMessage ====================

    @Test
    void createMessageShouldSucceed() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, "Hello!");

        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        Result<CreateMessageVO> result = conversationService.createMessage(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(conversationUuid, result.getData().getConversationUuid());
        assertEquals(userA, result.getData().getSenderUuid());
        assertEquals("Hello!", result.getData().getContent());
        assertNotNull(result.getData().getSendTime());
        assertEquals(false, result.getData().getRead());

        verify(conversationMapper).insertChatMessage(any(ChatMessage.class));
        verify(conversationMapper).touchConversation(conversationUuid);
        // WebSocket 推送验证
        verify(messagingTemplate).convertAndSend(
                eq("/topic/conversation/" + conversationUuid),
                any(CreateMessageVO.class));
    }

    @Test
    void createMessageShouldNotPushWhenValidationFails() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, null);
        conversationService.createMessage(dto);
        verify(messagingTemplate, never()).convertAndSend(any(), any(Object.class));
    }

    @Test
    void createMessageShouldReturn400WhenConversationUuidIsNull() {
        CreateMessageDTO dto = buildCreateMsgDTO(null, userA, "Hello");
        Result<CreateMessageVO> result = conversationService.createMessage(dto);
        assertEquals(400, result.getCode());
        assertEquals("会话ID不能为空", result.getMessage());
    }

    @Test
    void createMessageShouldReturn400WhenSenderUuidIsNull() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, null, "Hello");
        Result<CreateMessageVO> result = conversationService.createMessage(dto);
        assertEquals(400, result.getCode());
        assertEquals("发送者ID不能为空", result.getMessage());
    }

    @Test
    void createMessageShouldReturn400WhenMessageIsNull() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, null);
        Result<CreateMessageVO> result = conversationService.createMessage(dto);
        assertEquals(400, result.getCode());
        assertEquals("消息内容不能为空", result.getMessage());
    }

    @Test
    void createMessageShouldReturn400WhenMessageIsEmpty() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, "   ");
        Result<CreateMessageVO> result = conversationService.createMessage(dto);
        assertEquals(400, result.getCode());
        assertEquals("消息内容不能为空", result.getMessage());
    }

    @Test
    void createMessageShouldReturn400WhenDTOIsNull() {
        Result<CreateMessageVO> result = conversationService.createMessage(null);
        assertEquals(400, result.getCode());
    }

    @Test
    void createMessageShouldReturn404WhenConversationNotFound() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, "Hello");
        when(conversationMapper.selectConversationByUuid(conversationUuid)).thenReturn(null);

        Result<CreateMessageVO> result = conversationService.createMessage(dto);

        assertEquals(404, result.getCode());
        assertEquals("会话不存在", result.getMessage());
        verify(conversationMapper, never()).insertChatMessage(any());
    }

    @Test
    void createMessageShouldReturn400WhenConversationClosed() {
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, userA, "Hello");
        Conversation closedConv = buildConversation(conversationUuid, demandUuid, userA, userB);
        closedConv.setStatus("CLOSED");
        when(conversationMapper.selectConversationByUuid(conversationUuid)).thenReturn(closedConv);

        Result<CreateMessageVO> result = conversationService.createMessage(dto);

        assertEquals(400, result.getCode());
        assertEquals("会话已关闭，无法发送消息", result.getMessage());
        verify(conversationMapper, never()).insertChatMessage(any());
    }

    @Test
    void createMessageShouldReturn403WhenSenderNotMember() {
        UUID outsider = UUID.randomUUID();
        CreateMessageDTO dto = buildCreateMsgDTO(conversationUuid, outsider, "Hello");
        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        Result<CreateMessageVO> result = conversationService.createMessage(dto);

        assertEquals(403, result.getCode());
        assertEquals("无权在此会话中发送消息", result.getMessage());
        verify(conversationMapper, never()).insertChatMessage(any());
    }

    // ==================== markAsRead ====================

    @Test
    void markAsReadShouldSucceedWithUnreadMessages() {
        ReadConversationDTO dto = buildReadConvDTO(conversationUuid, userA);

        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        List<ChatMessage> unread = new ArrayList<>();
        unread.add(new ChatMessage());
        when(conversationMapper.selectUnreadMessagesByConversationUuid(conversationUuid, userA))
                .thenReturn(unread);
        when(conversationMapper.updateMessagesReadByConversationUuid(conversationUuid, userA))
                .thenReturn(1);

        Result<ReadConversationVO> result = conversationService.markAsRead(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(conversationUuid, result.getData().getConversationUuid());
        assertEquals(userA, result.getData().getUserUuid());
        assertEquals(1, result.getData().getReadCount());

        verify(conversationMapper).updateMessagesReadByConversationUuid(conversationUuid, userA);
    }

    @Test
    void markAsReadShouldReturnZeroWhenNoUnreadMessages() {
        ReadConversationDTO dto = buildReadConvDTO(conversationUuid, userA);

        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));
        when(conversationMapper.selectUnreadMessagesByConversationUuid(conversationUuid, userA))
                .thenReturn(new ArrayList<>());
        when(conversationMapper.updateMessagesReadByConversationUuid(conversationUuid, userA))
                .thenReturn(0);

        Result<ReadConversationVO> result = conversationService.markAsRead(dto);

        assertEquals(200, result.getCode());
        assertEquals(0, result.getData().getReadCount());
    }

    @Test
    void markAsReadShouldReturn400WhenConversationUuidIsNull() {
        ReadConversationDTO dto = buildReadConvDTO(null, userA);
        Result<ReadConversationVO> result = conversationService.markAsRead(dto);
        assertEquals(400, result.getCode());
        assertEquals("会话ID不能为空", result.getMessage());
    }

    @Test
    void markAsReadShouldReturn400WhenUserUuidIsNull() {
        ReadConversationDTO dto = buildReadConvDTO(conversationUuid, null);
        Result<ReadConversationVO> result = conversationService.markAsRead(dto);
        assertEquals(400, result.getCode());
        assertEquals("用户ID不能为空", result.getMessage());
    }

    @Test
    void markAsReadShouldReturn400WhenDTOIsNull() {
        Result<ReadConversationVO> result = conversationService.markAsRead(null);
        assertEquals(400, result.getCode());
    }

    @Test
    void markAsReadShouldReturn404WhenConversationNotFound() {
        ReadConversationDTO dto = buildReadConvDTO(conversationUuid, userA);
        when(conversationMapper.selectConversationByUuid(conversationUuid)).thenReturn(null);

        Result<ReadConversationVO> result = conversationService.markAsRead(dto);

        assertEquals(404, result.getCode());
        assertEquals("会话不存在", result.getMessage());
        verify(conversationMapper, never()).updateMessagesReadByConversationUuid(any(), any());
    }

    @Test
    void markAsReadShouldReturn403WhenUserNotMember() {
        UUID outsider = UUID.randomUUID();
        ReadConversationDTO dto = buildReadConvDTO(conversationUuid, outsider);
        when(conversationMapper.selectConversationByUuid(conversationUuid))
                .thenReturn(buildConversation(conversationUuid, demandUuid, userA, userB));

        Result<ReadConversationVO> result = conversationService.markAsRead(dto);

        assertEquals(403, result.getCode());
        assertEquals("无权操作此会话", result.getMessage());
        verify(conversationMapper, never()).updateMessagesReadByConversationUuid(any(), any());
    }

    // ==================== Helper methods ====================

    private Conversation buildConversation(UUID uuid, UUID demandUuid, UUID owner, UUID participant) {
        Conversation c = new Conversation();
        c.setUuid(uuid);
        c.setDemandUuid(demandUuid);
        c.setOwnerId(owner);
        c.setParticipantId(participant);
        c.setStatus("ACTIVE");
        return c;
    }

    private CreateConversationDTO buildCreateConvDTO(UUID demand, UUID owner, UUID participant) {
        CreateConversationDTO dto = new CreateConversationDTO();
        dto.setDemandUuid(demand);
        dto.setOwnerId(owner);
        dto.setParticipantId(participant);
        return dto;
    }

    private CreateMessageDTO buildCreateMsgDTO(UUID convUuid, UUID sender, String message) {
        CreateMessageDTO dto = new CreateMessageDTO();
        dto.setConversationUuid(convUuid);
        dto.setSenderUuid(sender);
        dto.setMessage(message);
        dto.setImg(false);
        return dto;
    }

    private ReadConversationDTO buildReadConvDTO(UUID convUuid, UUID userUuid) {
        ReadConversationDTO dto = new ReadConversationDTO();
        dto.setConversationUuid(convUuid);
        dto.setUserUuid(userUuid);
        return dto;
    }
}
