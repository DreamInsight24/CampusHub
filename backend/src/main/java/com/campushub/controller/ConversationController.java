package com.campushub.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campushub.common.Result;
import com.campushub.dto.message.CreateConversationDTO;
import com.campushub.dto.message.CreateMessageDTO;
import com.campushub.dto.message.MessageQueryDTO;
import com.campushub.dto.message.ReadConversationDTO;
import com.campushub.service.message.ConversationService;
import com.campushub.vo.message.ConversationQueryVO;
import com.campushub.vo.message.CreateConversationVO;
import com.campushub.vo.message.CreateMessageVO;
import com.campushub.vo.message.MessageQueryVO;
import com.campushub.vo.message.ReadConversationVO;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/query")
    public Result<ConversationQueryVO> listConversations(
            @RequestHeader(value = "token", required = false) String token) {
        return conversationService.listConversations(token);
    }

    @PostMapping("/demands/{demandId}")
    public Result<CreateConversationVO> createConversation(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID demandId,
            @RequestBody CreateConversationDTO dto) {
        if (dto == null) {
            dto = new CreateConversationDTO();
        }

        dto.setDemandUuid(demandId);
        if (token == null || token.isBlank()) {
            return conversationService.createConversation(dto);
        }
        return conversationService.createConversation(token, dto);
    }

    @GetMapping("/{conversationId}/messages")
    public Result<MessageQueryVO> listMessages(@PathVariable UUID conversationId) {
        MessageQueryDTO dto = new MessageQueryDTO();
        dto.setConversationUuid(conversationId);
        return conversationService.listMessages(dto);
    }

    @PostMapping("/{conversationId}/messages")
    public Result<CreateMessageVO> createMessage(
            @PathVariable UUID conversationId,
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody CreateMessageDTO dto) {
        if (dto == null) {
            dto = new CreateMessageDTO();
        }

        dto.setConversationUuid(conversationId);
        if (token == null || token.isBlank()) {
            return conversationService.createMessage(dto);
        }
        return conversationService.createMessage(token, dto);
    }

    @PutMapping("/{conversationId}/read")
    public Result<ReadConversationVO> markAsRead(
            @PathVariable UUID conversationId,
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody ReadConversationDTO dto) {
        if (dto == null) {
            dto = new ReadConversationDTO();
        }

        dto.setConversationUuid(conversationId);
        if (token == null || token.isBlank()) {
            return conversationService.markAsRead(dto);
        }
        return conversationService.markAsRead(token, dto);
    }
}
