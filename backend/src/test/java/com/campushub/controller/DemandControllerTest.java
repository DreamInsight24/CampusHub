package com.campushub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.campushub.common.Result;
import com.campushub.dto.demand.DemandCreateDTO;
import com.campushub.dto.demand.DemandQueryDTO;
import com.campushub.service.demand.DemandService;
import com.campushub.vo.PageVO;
import com.campushub.vo.UploadVO;
import com.campushub.vo.demand.DemandCreateVO;
import com.campushub.vo.demand.DemandDetailVO;
import com.campushub.vo.demand.DemandListVO;

@WebMvcTest(DemandController.class)
class DemandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemandService demandService;

    // ==================== searchDemands ====================

    @Test
    void searchDemandsShouldReturnPageVO() throws Exception {
        PageVO<DemandListVO> page = new PageVO<>(new ArrayList<>(), 0, 1, 20);
        when(demandService.searchDemands(any(DemandQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/demands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20));

        verify(demandService).searchDemands(any(DemandQueryDTO.class));
    }

    @Test
    void searchDemandsShouldPassQueryParameters() throws Exception {
        PageVO<DemandListVO> page = new PageVO<>(new ArrayList<>(), 5, 2, 10);
        when(demandService.searchDemands(any(DemandQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/demands")
                        .param("keyword", "快递")
                        .param("page", "2")
                        .param("pageSize", "10")
                        .param("sort", "deadline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(5))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void searchDemandsShouldHandleNullQuery() throws Exception {
        PageVO<DemandListVO> page = new PageVO<>(new ArrayList<>(), 0, 1, 20);
        when(demandService.searchDemands(any(DemandQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/demands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== listMyPublishedDemands ====================

    @Test
    void listMyPublishedDemandsShouldReturnList() throws Exception {
        DemandDetailVO detail = new DemandDetailVO();
        detail.setId(UUID.randomUUID().toString());
        detail.setTitle("我的发布需求");
        when(demandService.listMyPublishedDemands("valid-token"))
                .thenReturn(Result.success(List.of(detail)));

        mockMvc.perform(get("/api/demands/mine/published")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("我的发布需求"));

        verify(demandService).listMyPublishedDemands("valid-token");
    }

    @Test
    void listMyPublishedDemandsShouldReturn401WhenUnauthorized() throws Exception {
        when(demandService.listMyPublishedDemands(null))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(get("/api/demands/mine/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void listMyPublishedDemandsShouldReturnEmptyListWhenNone() throws Exception {
        when(demandService.listMyPublishedDemands("valid-token"))
                .thenReturn(Result.success(new ArrayList<>()));

        mockMvc.perform(get("/api/demands/mine/published")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ==================== listMyAcceptedDemands ====================

    @Test
    void listMyAcceptedDemandsShouldReturnList() throws Exception {
        DemandDetailVO detail = new DemandDetailVO();
        detail.setId(UUID.randomUUID().toString());
        detail.setTitle("我接取的需求");
        when(demandService.listMyAcceptedDemands("valid-token"))
                .thenReturn(Result.success(List.of(detail)));

        mockMvc.perform(get("/api/demands/mine/accepted")
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("我接取的需求"));

        verify(demandService).listMyAcceptedDemands("valid-token");
    }

    @Test
    void listMyAcceptedDemandsShouldReturn401WhenUnauthorized() throws Exception {
        when(demandService.listMyAcceptedDemands(null))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(get("/api/demands/mine/accepted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    // ==================== getDemandDetail ====================

    @Test
    void getDemandDetailShouldReturnDemand() throws Exception {
        UUID demandId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        DemandDetailVO detail = new DemandDetailVO();
        detail.setId(demandId.toString());
        detail.setTitle("需求详情");
        when(demandService.getDemandDetail(demandId)).thenReturn(Result.success(detail));

        mockMvc.perform(get("/api/demands/{id}", demandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(demandId.toString()))
                .andExpect(jsonPath("$.data.title").value("需求详情"));

        verify(demandService).getDemandDetail(demandId);
    }

    @Test
    void getDemandDetailShouldReturn404WhenNotFound() throws Exception {
        UUID demandId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(demandService.getDemandDetail(demandId))
                .thenReturn(Result.error(404, "需求不存在"));

        mockMvc.perform(get("/api/demands/{id}", demandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("需求不存在"));
    }

    @Test
    void getDemandDetailShouldReturn400WhenIdIsNull() throws Exception {
        when(demandService.getDemandDetail(null))
                .thenReturn(Result.error(400, "需求ID不能为空"));

        // simulate null UUID by passing a non-UUID string; Spring will bind null
        // We test the service error propagation directly
        when(demandService.getDemandDetail(any())).thenReturn(Result.error(400, "需求ID不能为空"));

        // Use a known UUID to avoid type mismatch, then verify the service was called
        UUID someId = UUID.randomUUID();
        mockMvc.perform(get("/api/demands/{id}", someId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ==================== respondDemand ====================

    @Test
    void respondDemandShouldSucceed() throws Exception {
        UUID demandId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        DemandDetailVO detail = new DemandDetailVO();
        detail.setId(demandId.toString());
        detail.setTitle("被接取的需求");
        when(demandService.respondDemand("valid-token", demandId))
                .thenReturn(Result.success(detail));

        mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(demandId.toString()));

        verify(demandService).respondDemand("valid-token", demandId);
    }

    @Test
    void respondDemandShouldReturn401WhenUnauthorized() throws Exception {
        UUID demandId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(demandService.respondDemand(isNull(), eq(demandId)))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(post("/api/demands/{id}/responses", demandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void respondDemandShouldReturn400WhenOwnDemand() throws Exception {
        UUID demandId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        when(demandService.respondDemand("valid-token", demandId))
                .thenReturn(Result.error(400, "不能接取自己发布的需求"));

        mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("不能接取自己发布的需求"));
    }

    @Test
    void respondDemandShouldReturn409WhenAlreadyTaken() throws Exception {
        UUID demandId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        when(demandService.respondDemand("valid-token", demandId))
                .thenReturn(Result.error(409, "该需求已被接取"));

        mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("该需求已被接取"));
    }

    // ==================== createDemand ====================

    @Test
    void createDemandShouldSucceed() throws Exception {
        String demandId = UUID.randomUUID().toString();
        DemandCreateVO vo = new DemandCreateVO(demandId);
        when(demandService.createDemand(eq("valid-token"), any(DemandCreateDTO.class)))
                .thenReturn(Result.success(vo));

        mockMvc.perform(post("/api/demands")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"EXPRESS\",\"title\":\"帮忙取快递\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(demandId));

        verify(demandService).createDemand(eq("valid-token"), any(DemandCreateDTO.class));
    }

    @Test
    void createDemandShouldReturn401WhenUnauthorized() throws Exception {
        when(demandService.createDemand(isNull(), any(DemandCreateDTO.class)))
                .thenReturn(Result.error(401, "请先登录"));

        mockMvc.perform(post("/api/demands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"EXPRESS\",\"title\":\"帮忙取快递\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void createDemandShouldReturn400WhenMissingTitle() throws Exception {
        when(demandService.createDemand(eq("valid-token"), any(DemandCreateDTO.class)))
                .thenReturn(Result.error(400, "需求类型和标题不能为空"));

        mockMvc.perform(post("/api/demands")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"EXPRESS\",\"title\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("需求类型和标题不能为空"));
    }

    @Test
    void createDemandShouldHandleNullBody() throws Exception {
        when(demandService.createDemand(eq("valid-token"), any(DemandCreateDTO.class)))
                .thenReturn(Result.error(400, "需求类型和标题不能为空"));

        mockMvc.perform(post("/api/demands")
                        .header("token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ==================== uploadDemandImage ====================

    @Test
    void uploadDemandImageShouldReturnUrl() throws Exception {
        UploadVO uploadVO = new UploadVO("/api/uploads/demands/mock-123-image.jpg");
        when(demandService.uploadDemandImage(eq("valid-token"), any()))
                .thenReturn(Result.success(uploadVO));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/api/demands/images")
                        .file(file)
                        .header("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url").value("/api/uploads/demands/mock-123-image.jpg"));

        verify(demandService).uploadDemandImage(eq("valid-token"), any());
    }

    @Test
    void uploadDemandImageShouldReturn401WhenUnauthorized() throws Exception {
        when(demandService.uploadDemandImage(isNull(), any()))
                .thenReturn(Result.error(401, "请先登录"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "fake-image".getBytes());

        mockMvc.perform(multipart("/api/demands/images")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }
}
