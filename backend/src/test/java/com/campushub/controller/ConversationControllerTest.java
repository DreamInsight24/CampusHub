package com.campushub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @Test
    void queryConversationsShouldReadTokenHeaderAndReturnVO() throws Exception {
        ConversationQueryVO vo = new ConversationQueryVO();
        vo.setConversations(new ArrayList<>());

        when(conversationService.listConversations("mock-token")).thenReturn(Result.success(vo));

        mockMvc.perform(get("/api/conversations/query").header("token", "mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversations").isArray());

        verify(conversationService).listConversations("mock-token");
    }

    @Test
    void createConversationShouldPutDemandPathVariableIntoDTO() throws Exception {
        UUID demandId = UUID.fromString("88888888-8888-8888-8888-888888888888");
        UUID ownerId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        UUID participantId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID conversationId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        CreateConversationVO vo = new CreateConversationVO();
        vo.setConversationUuid(conversationId);
        vo.setDemandUuid(demandId);
        vo.setOwnerId(ownerId);
        vo.setParticipantId(participantId);
        vo.setStatus("ACTIVE");

        when(conversationService.createConversation(any(CreateConversationDTO.class))).thenReturn(Result.success(vo));

        mockMvc.perform(post("/api/conversations/demands/{demandId}", demandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerId\":\"" + ownerId + "\",\"participantId\":\"" + participantId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationUuid").value(conversationId.toString()))
                .andExpect(jsonPath("$.data.demandUuid").value(demandId.toString()))
                .andExpect(jsonPath("$.data.ownerId").value(ownerId.toString()))
                .andExpect(jsonPath("$.data.participantId").value(participantId.toString()))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        ArgumentCaptor<CreateConversationDTO> captor = ArgumentCaptor.forClass(CreateConversationDTO.class);
        verify(conversationService).createConversation(captor.capture());
        assertEquals(demandId, captor.getValue().getDemandUuid());
        assertEquals(ownerId, captor.getValue().getOwnerId());
        assertEquals(participantId, captor.getValue().getParticipantId());
    }

    @Test
    void listMessagesShouldPutPathVariableIntoDTO() throws Exception {
        UUID conversationId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        MessageQueryVO vo = new MessageQueryVO();
        vo.setConversationUuid(conversationId);
        vo.setMessages(new ArrayList<>());

        when(conversationService.listMessages(any(MessageQueryDTO.class))).thenReturn(Result.success(vo));

        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationUuid").value(conversationId.toString()))
                .andExpect(jsonPath("$.data.messages").isArray());

        ArgumentCaptor<MessageQueryDTO> captor = ArgumentCaptor.forClass(MessageQueryDTO.class);
        verify(conversationService).listMessages(captor.capture());
        assertEquals(conversationId, captor.getValue().getConversationUuid());
    }

    @Test
    void createMessageShouldPutPathVariableIntoDTO() throws Exception {
        UUID conversationId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID senderUuid = UUID.fromString("55555555-5555-5555-5555-555555555555");

        CreateMessageVO vo = new CreateMessageVO();
        vo.setConversationUuid(conversationId);
        vo.setSenderUuid(senderUuid);
        vo.setContent("hello");
        vo.setImg(false);
        vo.setRead(false);

        when(conversationService.createMessage(any(CreateMessageDTO.class))).thenReturn(Result.success(vo));

        mockMvc.perform(post("/api/conversations/{conversationId}/messages", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderUuid\":\"" + senderUuid + "\",\"message\":\"hello\",\"img\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationUuid").value(conversationId.toString()))
                .andExpect(jsonPath("$.data.senderUuid").value(senderUuid.toString()))
                .andExpect(jsonPath("$.data.content").value("hello"));

        ArgumentCaptor<CreateMessageDTO> captor = ArgumentCaptor.forClass(CreateMessageDTO.class);
        verify(conversationService).createMessage(captor.capture());
        assertEquals(conversationId, captor.getValue().getConversationUuid());
        assertEquals(senderUuid, captor.getValue().getSenderUuid());
        assertEquals("hello", captor.getValue().getMessage());
    }

    @Test
    void markAsReadShouldPutPathVariableIntoDTO() throws Exception {
        UUID conversationId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID userUuid = UUID.fromString("77777777-7777-7777-7777-777777777777");

        ReadConversationVO vo = new ReadConversationVO();
        vo.setConversationUuid(conversationId);
        vo.setUserUuid(userUuid);
        vo.setReadCount(3);

        when(conversationService.markAsRead(any(ReadConversationDTO.class))).thenReturn(Result.success(vo));

        mockMvc.perform(put("/api/conversations/{conversationId}/read", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userUuid\":\"" + userUuid + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationUuid").value(conversationId.toString()))
                .andExpect(jsonPath("$.data.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.data.readCount").value(3));

        ArgumentCaptor<ReadConversationDTO> captor = ArgumentCaptor.forClass(ReadConversationDTO.class);
        verify(conversationService).markAsRead(captor.capture());
        assertEquals(conversationId, captor.getValue().getConversationUuid());
        assertEquals(userUuid, captor.getValue().getUserUuid());
    }

    // ==================== error propagation ====================

    @Test
    void queryConversationsShouldPropagateServiceError() throws Exception {
        when(conversationService.listConversations("bad-token"))
                .thenReturn(Result.error(401, "登录已过期，请重新登录"));

        mockMvc.perform(get("/api/conversations/query").header("token", "bad-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("登录已过期，请重新登录"));
    }

    @Test
    void createConversationShouldPropagateService404() throws Exception {
        UUID demandId = UUID.randomUUID();
        when(conversationService.createConversation(any(CreateConversationDTO.class)))
                .thenReturn(Result.error(404, "需求不存在"));

        mockMvc.perform(post("/api/conversations/demands/{demandId}", demandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ownerId\":\"" + UUID.randomUUID() + "\",\"participantId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("需求不存在"));
    }

    @Test
    void createMessageShouldPropagateService403() throws Exception {
        UUID conversationId = UUID.randomUUID();
        when(conversationService.createMessage(any(CreateMessageDTO.class)))
                .thenReturn(Result.error(403, "无权在此会话中发送消息"));

        mockMvc.perform(post("/api/conversations/{conversationId}/messages", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderUuid\":\"" + UUID.randomUUID() + "\",\"message\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("无权在此会话中发送消息"));
    }

    @Test
    void markAsReadShouldPropagateService404() throws Exception {
        UUID conversationId = UUID.randomUUID();
        when(conversationService.markAsRead(any(ReadConversationDTO.class)))
                .thenReturn(Result.error(404, "会话不存在"));

        mockMvc.perform(put("/api/conversations/{conversationId}/read", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userUuid\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("会话不存在"));
    }

    // ==================== null body guards ====================

    @Test
    void createConversationShouldNotFailWithNullBody() throws Exception {
        UUID demandId = UUID.randomUUID();
        when(conversationService.createConversation(any(CreateConversationDTO.class)))
                .thenReturn(Result.error(400, "发布者ID不能为空"));

        mockMvc.perform(post("/api/conversations/demands/{demandId}", demandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        // Verify DTO was created with demandId set, even with empty body
        ArgumentCaptor<CreateConversationDTO> captor = ArgumentCaptor.forClass(CreateConversationDTO.class);
        verify(conversationService).createConversation(captor.capture());
        assertEquals(demandId, captor.getValue().getDemandUuid());
    }

    @Test
    void createMessageShouldNotFailWithNullBody() throws Exception {
        UUID conversationId = UUID.randomUUID();
        when(conversationService.createMessage(any(CreateMessageDTO.class)))
                .thenReturn(Result.error(400, "发送者ID不能为空"));

        mockMvc.perform(post("/api/conversations/{conversationId}/messages", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        ArgumentCaptor<CreateMessageDTO> captor = ArgumentCaptor.forClass(CreateMessageDTO.class);
        verify(conversationService).createMessage(captor.capture());
        assertEquals(conversationId, captor.getValue().getConversationUuid());
    }

    @Test
    void markAsReadShouldNotFailWithNullBody() throws Exception {
        UUID conversationId = UUID.randomUUID();
        when(conversationService.markAsRead(any(ReadConversationDTO.class)))
                .thenReturn(Result.error(400, "用户ID不能为空"));

        mockMvc.perform(put("/api/conversations/{conversationId}/read", conversationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        ArgumentCaptor<ReadConversationDTO> captor = ArgumentCaptor.forClass(ReadConversationDTO.class);
        verify(conversationService).markAsRead(captor.capture());
        assertEquals(conversationId, captor.getValue().getConversationUuid());
    }
}
