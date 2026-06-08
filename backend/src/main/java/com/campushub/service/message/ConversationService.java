package com.campushub.service.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.campushub.common.Result;
import com.campushub.dto.message.CreateConversationDTO;
import com.campushub.dto.message.CreateMessageDTO;
import com.campushub.dto.message.MessageQueryDTO;
import com.campushub.dto.message.ReadConversationDTO;
import com.campushub.entity.message.ChatMessage;
import com.campushub.entity.message.Conversation;
import com.campushub.entity.demand.Demand;
import com.campushub.mapper.ConversationMapper;
import com.campushub.mapper.DemandMapper;
import com.campushub.mapper.UsersMapper;
import com.campushub.service.auth.AuthService;
import com.campushub.util.TokenUtil;
import com.campushub.vo.message.ConversationQueryVO;
import com.campushub.vo.message.ConversationListVO;
import com.campushub.vo.message.CreateConversationVO;
import com.campushub.vo.message.CreateMessageVO;
import com.campushub.vo.message.MessageQueryVO;
import com.campushub.vo.message.ReadConversationVO;

@Service
public class ConversationService {

    private final ConversationMapper conversationMapper;
    private final UsersMapper usersMapper;
    private final DemandMapper demandMapper;
    private final AuthService authService;
    private final SimpMessagingTemplate messagingTemplate;

    public ConversationService(ConversationMapper conversationMapper,
                               UsersMapper usersMapper,
                               DemandMapper demandMapper,
                               AuthService authService,
                               SimpMessagingTemplate messagingTemplate) {
        this.conversationMapper = conversationMapper;
        this.usersMapper = usersMapper;
        this.demandMapper = demandMapper;
        this.authService = authService;
        this.messagingTemplate = messagingTemplate;
    }

    public Result<ConversationQueryVO> listConversations(String token) {
        if (token == null || token.isBlank()) {
            return Result.error(401, "\u672a\u767b\u5f55\uff0c\u8bf7\u5148\u767b\u5f55");
        }

        UUID userUuid = getCurrentUserUuid(token);
        if (userUuid == null) {
            return Result.error(401, "\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55");
        }

        List<ConversationListVO> conversations = conversationMapper.selectConversationListByUserId(userUuid);
        if (conversations == null || conversations.isEmpty()) {
            conversations = new ArrayList<>();
            for (Conversation conversation : conversationMapper.selectConversationsByUserId(userUuid)) {
                conversations.add(toConversationListVO(conversation));
            }
        }

        ConversationQueryVO vo = new ConversationQueryVO();
        vo.setConversations(conversations);
        return Result.success(vo);
    }

    public Result<CreateConversationVO> createConversation(CreateConversationDTO dto) {
        if (dto == null || dto.getDemandUuid() == null) {
            return Result.error(400, "\u9700\u6c42ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (dto.getOwnerId() == null) {
            return Result.error(400, "\u53d1\u5e03\u8005ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (dto.getParticipantId() == null) {
            return Result.error(400, "\u53c2\u4e0e\u8005ID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (dto.getOwnerId().equals(dto.getParticipantId())) {
            return Result.error(400, "\u4e0d\u80fd\u4e0e\u81ea\u5df1\u521b\u5efa\u4f1a\u8bdd");
        }

        if (demandMapper.selectDemandByUuid(dto.getDemandUuid()) == null) {
            return Result.error(404, "\u9700\u6c42\u4e0d\u5b58\u5728");
        }
        if (usersMapper.selectUserByUuid(dto.getOwnerId()) == null) {
            return Result.error(404, "\u53d1\u5e03\u8005\u4e0d\u5b58\u5728");
        }
        if (usersMapper.selectUserByUuid(dto.getParticipantId()) == null) {
            return Result.error(404, "\u53c2\u4e0e\u8005\u4e0d\u5b58\u5728");
        }

        Conversation existing = conversationMapper.selectConversationByDemandAndUsers(
                dto.getDemandUuid(), dto.getOwnerId(), dto.getParticipantId());
        if (existing != null) {
            return Result.error(409, "\u4f1a\u8bdd\u5df2\u5b58\u5728\uff0c\u8bf7\u52ff\u91cd\u590d\u521b\u5efa");
        }

        return insertConversation(dto);
    }

    public Result<CreateConversationVO> createConversation(String token, CreateConversationDTO dto) {
        if (dto == null || dto.getDemandUuid() == null) {
            return Result.error(400, "\u9700\u6c42ID\u4e0d\u80fd\u4e3a\u7a7a");
        }

        Demand demand = demandMapper.selectDemandByUuid(dto.getDemandUuid());
        if (demand == null) {
            return Result.error(404, "\u9700\u6c42\u4e0d\u5b58\u5728");
        }

        if (dto.getOwnerId() == null) {
            dto.setOwnerId(demand.getPublisher_uuid());
        }
        if (dto.getParticipantId() == null) {
            UUID currentUserUuid = getCurrentUserUuid(token);
            if (currentUserUuid != null) {
                dto.setParticipantId(currentUserUuid);
            }
        }
        if (dto.getParticipantId() == null) {
            return Result.error(401, "\u8bf7\u5148\u767b\u5f55");
        }
        if (dto.getOwnerId().equals(dto.getParticipantId())) {
            return Result.error(400, "\u4e0d\u80fd\u4e0e\u81ea\u5df1\u521b\u5efa\u4f1a\u8bdd");
        }

        if (usersMapper.selectUserByUuid(dto.getOwnerId()) == null) {
            return Result.error(404, "\u53d1\u5e03\u8005\u4e0d\u5b58\u5728");
        }
        if (usersMapper.selectUserByUuid(dto.getParticipantId()) == null) {
            return Result.error(404, "\u53c2\u4e0e\u8005\u4e0d\u5b58\u5728");
        }

        Conversation existing = conversationMapper.selectConversationByDemandAndUsers(
                dto.getDemandUuid(), dto.getOwnerId(), dto.getParticipantId());
        if (existing != null) {
            return Result.success(toCreateConversationVO(existing));
        }

        return insertConversation(dto);
    }

    private Result<CreateConversationVO> insertConversation(CreateConversationDTO dto) {

        Conversation conversation = new Conversation();
        conversation.setUuid(UUID.randomUUID());
        conversation.setDemandUuid(dto.getDemandUuid());
        conversation.setOwnerId(dto.getOwnerId());
        conversation.setParticipantId(dto.getParticipantId());
        conversation.setStatus("ACTIVE");
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        conversationMapper.insertConversation(conversation);

        return Result.success(toCreateConversationVO(conversation));
    }

    public Result<MessageQueryVO> listMessages(MessageQueryDTO dto) {
        if (dto == null || dto.getConversationUuid() == null) {
            return Result.error(400, "\u4f1a\u8bddID\u4e0d\u80fd\u4e3a\u7a7a");
        }

        Conversation conversation = conversationMapper.selectConversationByUuid(dto.getConversationUuid());
        if (conversation == null) {
            return Result.error(404, "\u4f1a\u8bdd\u4e0d\u5b58\u5728");
        }

        List<ChatMessage> messages = conversationMapper.selectMessagesByConversationUuid(dto.getConversationUuid());
        MessageQueryVO vo = new MessageQueryVO();
        vo.setConversationUuid(dto.getConversationUuid());
        vo.setMessages(new ArrayList<>(messages));
        return Result.success(vo);
    }

    public Result<CreateMessageVO> createMessage(String token, CreateMessageDTO dto) {
        UUID senderUuid = getCurrentUserUuid(token);
        if (senderUuid == null) {
            return Result.error(401, "\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55");
        }

        return createMessageForUser(senderUuid, dto);
    }

    public Result<CreateMessageVO> createMessage(CreateMessageDTO dto) {
        if (dto == null || dto.getSenderUuid() == null) {
            return Result.error(400, "\u53d1\u9001\u8005ID\u4e0d\u80fd\u4e3a\u7a7a");
        }

        return createMessageForUser(dto.getSenderUuid(), dto);
    }

    public Result<ReadConversationVO> markAsRead(String token, ReadConversationDTO dto) {
        UUID userUuid = getCurrentUserUuid(token);
        if (userUuid == null) {
            return Result.error(401, "\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55");
        }

        return markAsReadForUser(userUuid, dto);
    }

    public Result<ReadConversationVO> markAsRead(ReadConversationDTO dto) {
        if (dto == null || dto.getUserUuid() == null) {
            return Result.error(400, "\u7528\u6237ID\u4e0d\u80fd\u4e3a\u7a7a");
        }

        return markAsReadForUser(dto.getUserUuid(), dto);
    }

    private UUID getCurrentUserUuid(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        if (authService != null) {
            return authService.getUserUuidByToken(token);
        }
        return TokenUtil.parseToken(token);
    }

    private Result<CreateMessageVO> createMessageForUser(UUID senderUuid, CreateMessageDTO dto) {
        if (dto == null || dto.getConversationUuid() == null) {
            return Result.error(400, "\u4f1a\u8bddID\u4e0d\u80fd\u4e3a\u7a7a");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            return Result.error(400, "\u6d88\u606f\u5185\u5bb9\u4e0d\u80fd\u4e3a\u7a7a");
        }

        Conversation conversation = conversationMapper.selectConversationByUuid(dto.getConversationUuid());
        if (conversation == null) {
            return Result.error(404, "\u4f1a\u8bdd\u4e0d\u5b58\u5728");
        }
        if (!"ACTIVE".equals(conversation.getStatus())) {
            return Result.error(400, "\u4f1a\u8bdd\u5df2\u5173\u95ed\uff0c\u65e0\u6cd5\u53d1\u9001\u6d88\u606f");
        }
        if (!senderUuid.equals(conversation.getOwnerId()) && !senderUuid.equals(conversation.getParticipantId())) {
            return Result.error(403, "\u65e0\u6743\u5728\u6b64\u4f1a\u8bdd\u4e2d\u53d1\u9001\u6d88\u606f");
        }

        ChatMessage msg = new ChatMessage();
        msg.setConversation_uuid(dto.getConversationUuid());
        msg.setUser_uuid(senderUuid);
        msg.setMessage(dto.getMessage().trim());
        msg.setTime(LocalDateTime.now());
        msg.setImg(Boolean.TRUE.equals(dto.getImg()));
        msg.setRead(false);

        conversationMapper.insertChatMessage(msg);
        conversationMapper.touchConversation(dto.getConversationUuid());

        CreateMessageVO vo = new CreateMessageVO();
        vo.setId(msg.getId());
        vo.setConversationUuid(dto.getConversationUuid());
        vo.setSenderUuid(senderUuid);
        vo.setContent(msg.getMessage());
        vo.setSendTime(msg.getTime());
        vo.setImg(msg.isImg());
        vo.setRead(false);

        messagingTemplate.convertAndSend("/topic/conversation/" + dto.getConversationUuid(), vo);
        return Result.success(vo);
    }

    private CreateConversationVO toCreateConversationVO(Conversation conversation) {
        CreateConversationVO vo = new CreateConversationVO();
        vo.setConversationUuid(conversation.getUuid());
        vo.setDemandUuid(conversation.getDemandUuid());
        vo.setOwnerId(conversation.getOwnerId());
        vo.setParticipantId(conversation.getParticipantId());
        vo.setStatus(conversation.getStatus());
        return vo;
    }

    private ConversationListVO toConversationListVO(Conversation conversation) {
        ConversationListVO vo = new ConversationListVO();
        vo.setUuid(conversation.getUuid());
        vo.setDemandUuid(conversation.getDemandUuid());
        vo.setOwnerId(conversation.getOwnerId());
        vo.setParticipantId(conversation.getParticipantId());
        vo.setStatus(conversation.getStatus());
        vo.setCreatedAt(conversation.getCreatedAt());
        vo.setUpdatedAt(conversation.getUpdatedAt());
        return vo;
    }

    private Result<ReadConversationVO> markAsReadForUser(UUID userUuid, ReadConversationDTO dto) {
        if (dto == null || dto.getConversationUuid() == null) {
            return Result.error(400, "\u4f1a\u8bddID\u4e0d\u80fd\u4e3a\u7a7a");
        }

        Conversation conversation = conversationMapper.selectConversationByUuid(dto.getConversationUuid());
        if (conversation == null) {
            return Result.error(404, "\u4f1a\u8bdd\u4e0d\u5b58\u5728");
        }
        if (!userUuid.equals(conversation.getOwnerId()) && !userUuid.equals(conversation.getParticipantId())) {
            return Result.error(403, "\u65e0\u6743\u64cd\u4f5c\u6b64\u4f1a\u8bdd");
        }

        conversationMapper.selectUnreadMessagesByConversationUuid(dto.getConversationUuid(), userUuid);
        int updatedCount = conversationMapper.updateMessagesReadByConversationUuid(
                dto.getConversationUuid(), userUuid);

        ReadConversationVO vo = new ReadConversationVO();
        vo.setConversationUuid(dto.getConversationUuid());
        vo.setUserUuid(userUuid);
        vo.setReadCount(updatedCount);
        return Result.success(vo);
    }
}
