package com.campushub.service.demand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import com.campushub.common.Result;
import com.campushub.common.enums.DemandApplicationStatus;
import com.campushub.common.enums.DemandStat;
import com.campushub.common.enums.DemandType;
import com.campushub.dto.demand.DemandCreateDTO;
import com.campushub.dto.demand.DemandQueryDTO;
import com.campushub.entity.demand.Demand;
import com.campushub.entity.demand.DemandApplication;
import com.campushub.entity.demand.TeamupDemandDetail;
import com.campushub.mapper.DemandMapper;
import com.campushub.service.auth.AuthService;
import com.campushub.vo.PageVO;
import com.campushub.vo.UploadVO;
import com.campushub.vo.demand.DemandApplicationVO;
import com.campushub.vo.demand.DemandCreateVO;
import com.campushub.vo.demand.DemandDetailVO;
import com.campushub.vo.demand.DemandListVO;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DemandService Unit Tests")
class DemandServiceTest {

    @Mock
    private DemandMapper demandMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private DemandService demandService;

    private UUID publisherUuid;
    private UUID takerUuid;
    private UUID demandUuid;
    private String validToken;

    @BeforeEach
    void setUp() {
        publisherUuid = UUID.randomUUID();
        takerUuid = UUID.randomUUID();
        demandUuid = UUID.randomUUID();
        validToken = "valid-token";

        when(authService.getUserUuidByToken(validToken)).thenReturn(publisherUuid);
    }

    // ==================== searchDemands ====================

    @Nested
    @DisplayName("searchDemands Tests")
    class SearchDemandsTests {

        @Test
        @DisplayName("should return page with default params when query is null")
        void searchDemandsWithNullQuery() {
            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            PageVO<DemandListVO> result = demandService.searchDemands(null);

            assertNotNull(result);
            assertEquals(0, result.getTotal());
            assertEquals(1, result.getPage());
            assertEquals(20, result.getPageSize());

            verify(demandMapper).searchDemands(any(DemandQueryDTO.class), eq(0));
            verify(demandMapper).countDemands(any(DemandQueryDTO.class));
        }

        @Test
        @DisplayName("should normalize page to default when less than 1")
        void searchDemandsNormalizesInvalidPage() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setPage(0);
            query.setPageSize(5);

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            PageVO<DemandListVO> result = demandService.searchDemands(query);

            assertEquals(1, result.getPage()); // normalized to 1
            assertEquals(5, result.getPageSize());
        }

        @Test
        @DisplayName("should cap pageSize to max when too large")
        void searchDemandsCapsPageSize() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setPage(1);
            query.setPageSize(500);

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertEquals(100, captor.getValue().getPageSize()); // MAX_PAGE_SIZE = 100
        }

        @Test
        @DisplayName("should normalize pageSize to default when less than 1")
        void searchDemandsNormalizesInvalidPageSize() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setPageSize(-1);

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertEquals(20, captor.getValue().getPageSize());
        }

        @Test
        @DisplayName("should trim keyword whitespace")
        void searchDemandsTrimsKeyword() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setKeyword("  快递  ");

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertEquals("快递", captor.getValue().getKeyword());
        }

        @Test
        @DisplayName("should set keyword to null when blank")
        void searchDemandsNullifiesBlankKeyword() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setKeyword("   ");

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertNull(captor.getValue().getKeyword());
        }

        @Test
        @DisplayName("should keep sort as 'deadline' when specified")
        void searchDemandsKeepsDeadlineSort() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setSort("deadline");

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertEquals("deadline", captor.getValue().getSort());
        }

        @Test
        @DisplayName("should default sort to 'latest' for unknown values")
        void searchDemandsDefaultsSortToLatest() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setSort("random");

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(0))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            ArgumentCaptor<DemandQueryDTO> captor = ArgumentCaptor.forClass(DemandQueryDTO.class);
            verify(demandMapper).searchDemands(captor.capture(), anyInt());
            assertEquals("latest", captor.getValue().getSort());
        }

        @Test
        @DisplayName("should calculate correct offset for pagination")
        void searchDemandsCalculatesOffset() {
            DemandQueryDTO query = new DemandQueryDTO();
            query.setPage(3);
            query.setPageSize(10);

            when(demandMapper.searchDemands(any(DemandQueryDTO.class), eq(20))).thenReturn(new ArrayList<>());
            when(demandMapper.countDemands(any(DemandQueryDTO.class))).thenReturn(0L);

            demandService.searchDemands(query);

            verify(demandMapper).searchDemands(any(DemandQueryDTO.class), eq(20));
        }
    }

    // ==================== getDemandDetail ====================

    @Nested
    @DisplayName("getDemandDetail Tests")
    class GetDemandDetailTests {

        @Test
        @DisplayName("should return demand detail by uuid")
        void getDemandDetailSuccess() {
            DemandDetailVO detail = new DemandDetailVO();
            detail.setId(demandUuid.toString());
            detail.setTitle("测试需求");
            when(demandMapper.selectDemandDetailByUuid(demandUuid)).thenReturn(detail);

            Result<DemandDetailVO> result = demandService.getDemandDetail(demandUuid);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(demandUuid.toString(), result.getData().getId());
            assertEquals("测试需求", result.getData().getTitle());
        }

        @Test
        @DisplayName("should return 400 when uuid is null")
        void getDemandDetailNullId() {
            Result<DemandDetailVO> result = demandService.getDemandDetail(null);

            assertEquals(400, result.getCode());
            assertEquals("需求ID不能为空", result.getMessage());
            assertNull(result.getData());
            verify(demandMapper, never()).selectDemandDetailByUuid(any());
        }

        @Test
        @DisplayName("should return 404 when demand not found")
        void getDemandDetailNotFound() {
            when(demandMapper.selectDemandDetailByUuid(demandUuid)).thenReturn(null);

            Result<DemandDetailVO> result = demandService.getDemandDetail(demandUuid);

            assertEquals(404, result.getCode());
            assertEquals("需求不存在", result.getMessage());
            assertNull(result.getData());
        }
    }

    // ==================== listMyPublishedDemands ====================

    @Nested
    @DisplayName("listMyPublishedDemands Tests")
    class ListMyPublishedDemandsTests {

        @Test
        @DisplayName("should return published demands for valid token")
        void listMyPublishedDemandsSuccess() {
            DemandDetailVO detail = new DemandDetailVO();
            detail.setId(demandUuid.toString());
            when(demandMapper.selectPublishedDemandDetails(publisherUuid)).thenReturn(List.of(detail));

            Result<List<DemandDetailVO>> result = demandService.listMyPublishedDemands(validToken);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            verify(demandMapper).selectPublishedDemandDetails(publisherUuid);
        }

        @Test
        @DisplayName("should return 401 when token is null")
        void listMyPublishedDemandsNullToken() {
            when(authService.getUserUuidByToken(null)).thenReturn(null);

            Result<List<DemandDetailVO>> result = demandService.listMyPublishedDemands(null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(demandMapper, never()).selectPublishedDemandDetails(any());
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void listMyPublishedDemandsInvalidToken() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<List<DemandDetailVO>> result = demandService.listMyPublishedDemands("bad-token");

            assertEquals(401, result.getCode());
            verify(demandMapper, never()).selectPublishedDemandDetails(any());
        }

        @Test
        @DisplayName("should return empty list when no published demands")
        void listMyPublishedDemandsEmptyList() {
            when(demandMapper.selectPublishedDemandDetails(publisherUuid)).thenReturn(new ArrayList<>());

            Result<List<DemandDetailVO>> result = demandService.listMyPublishedDemands(validToken);

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    // ==================== listMyAcceptedDemands ====================

    @Nested
    @DisplayName("listMyAcceptedDemands Tests")
    class ListMyAcceptedDemandsTests {

        @Test
        @DisplayName("should return accepted demands for valid token")
        void listMyAcceptedDemandsSuccess() {
            DemandDetailVO detail = new DemandDetailVO();
            detail.setId(demandUuid.toString());
            when(demandMapper.selectAcceptedDemandDetails(publisherUuid)).thenReturn(List.of(detail));

            Result<List<DemandDetailVO>> result = demandService.listMyAcceptedDemands(validToken);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            verify(demandMapper).selectAcceptedDemandDetails(publisherUuid);
        }

        @Test
        @DisplayName("should return 401 when token is null")
        void listMyAcceptedDemandsNullToken() {
            when(authService.getUserUuidByToken(null)).thenReturn(null);

            Result<List<DemandDetailVO>> result = demandService.listMyAcceptedDemands(null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(demandMapper, never()).selectAcceptedDemandDetails(any());
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void listMyAcceptedDemandsInvalidToken() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<List<DemandDetailVO>> result = demandService.listMyAcceptedDemands("bad-token");

            assertEquals(401, result.getCode());
            verify(demandMapper, never()).selectAcceptedDemandDetails(any());
        }

        @Test
        @DisplayName("should return empty list when no accepted demands")
        void listMyAcceptedDemandsEmptyList() {
            when(demandMapper.selectAcceptedDemandDetails(publisherUuid)).thenReturn(new ArrayList<>());

            Result<List<DemandDetailVO>> result = demandService.listMyAcceptedDemands(validToken);

            assertEquals(200, result.getCode());
            assertTrue(result.getData().isEmpty());
        }
    }

    // ==================== respondDemand ====================

    @Nested
    @DisplayName("respondDemand Tests")
    class RespondDemandTests {

        private Demand openDemand;

        @BeforeEach
        void setUp() {
            openDemand = new Demand();
            openDemand.setUuid(demandUuid);
            openDemand.setPublisher_uuid(publisherUuid);
            openDemand.setStat(DemandStat.OPEN);
        }

        @Test
        @DisplayName("should apply to demand successfully")
        void respondDemandSuccess() {
            Demand demandWithDifferentPublisher = new Demand();
            demandWithDifferentPublisher.setUuid(demandUuid);
            demandWithDifferentPublisher.setPublisher_uuid(takerUuid); // different from responder
            demandWithDifferentPublisher.setStat(DemandStat.OPEN);
            demandWithDifferentPublisher.setType(DemandType.EXPRESS);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(demandWithDifferentPublisher);

            DemandDetailVO detail = new DemandDetailVO();
            detail.setId(demandUuid.toString());
            when(demandMapper.selectDemandDetailByUuid(demandUuid)).thenReturn(detail);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            verify(demandMapper).insertDemandApplication(any(DemandApplication.class));
            verify(demandMapper, never()).updateDemandTaker(any(), any());
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void respondDemandUnauthorized() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<DemandDetailVO> result = demandService.respondDemand("bad-token", demandUuid);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }

        @Test
        @DisplayName("should return 404 when demand not found")
        void respondDemandNotFound() {
            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(null);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(404, result.getCode());
            assertEquals("需求不存在", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }

        @Test
        @DisplayName("should return 400 when responding to own demand")
        void respondDemandOwnDemand() {
            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(openDemand);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(400, result.getCode());
            assertEquals("不能申请自己发布的需求", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }

        @Test
        @DisplayName("should return 409 when already applied")
        void respondDemandAlreadyApplied() {
            openDemand.setPublisher_uuid(takerUuid); // different publisher
            openDemand.setType(DemandType.EXPRESS);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(openDemand);
            DemandApplicationVO existing = new DemandApplicationVO();
            existing.setId(UUID.randomUUID().toString());
            when(demandMapper.selectDemandApplicationByDemandAndApplicant(demandUuid, publisherUuid))
                    .thenReturn(existing);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(409, result.getCode());
            assertEquals("已申请，请等待发布者确认", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }

        @Test
        @DisplayName("should return 400 when demand status is not OPEN")
        void respondDemandNotOpen() {
            Demand closedDemand = new Demand();
            closedDemand.setUuid(demandUuid);
            closedDemand.setPublisher_uuid(takerUuid);
            closedDemand.setStat(DemandStat.CLOSED);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(closedDemand);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(400, result.getCode());
            assertEquals("该需求当前不可申请", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }

        @Test
        @DisplayName("should return 400 when secondhand demand is applied")
        void respondSecondhandDemand() {
            Demand demandWithDifferentPublisher = new Demand();
            demandWithDifferentPublisher.setUuid(demandUuid);
            demandWithDifferentPublisher.setPublisher_uuid(takerUuid);
            demandWithDifferentPublisher.setStat(DemandStat.OPEN);
            demandWithDifferentPublisher.setType(DemandType.SECONDHAND);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(demandWithDifferentPublisher);

            Result<DemandDetailVO> result = demandService.respondDemand(validToken, demandUuid);

            assertEquals(400, result.getCode());
            assertEquals("二手交易不支持申请或接取，请先联系发布者沟通", result.getMessage());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
            verify(demandMapper, never()).insertDemandApplication(any());
        }
    }

    // ==================== acceptApplication ====================

    @Nested
    @DisplayName("acceptApplication Tests")
    class AcceptApplicationTests {

        private UUID applicationUuid;
        private Demand teamupDemand;
        private DemandApplication pendingApplication;

        @BeforeEach
        void setUp() {
            applicationUuid = UUID.randomUUID();

            teamupDemand = new Demand();
            teamupDemand.setUuid(demandUuid);
            teamupDemand.setPublisher_uuid(publisherUuid);
            teamupDemand.setStat(DemandStat.OPEN);
            teamupDemand.setType(DemandType.TEAMUP);

            pendingApplication = new DemandApplication();
            pendingApplication.setUuid(applicationUuid);
            pendingApplication.setDemandUuid(demandUuid);
            pendingApplication.setApplicantUuid(takerUuid);
            pendingApplication.setStatus(DemandApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("should keep other pending applications when team still has seats")
        void acceptTeamupApplicationWithRemainingSeats() {
            TeamupDemandDetail detail = new TeamupDemandDetail();
            detail.setDemand_uuid(demandUuid);
            detail.setCurrent_members(2);
            detail.setExpected_members(4);

            DemandDetailVO resultDetail = new DemandDetailVO();
            resultDetail.setId(demandUuid.toString());
            resultDetail.setType(DemandType.TEAMUP);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(teamupDemand);
            when(demandMapper.selectDemandApplicationByUuid(applicationUuid)).thenReturn(pendingApplication);
            when(demandMapper.selectTeamupDemandDetailByDemandUuid(demandUuid)).thenReturn(detail);
            when(demandMapper.selectDemandDetailByUuid(demandUuid)).thenReturn(resultDetail);

            Result<DemandDetailVO> result = demandService.acceptApplication(validToken, demandUuid, applicationUuid);

            assertEquals(200, result.getCode());
            verify(demandMapper).updateDemandApplicationStatus(applicationUuid, DemandApplicationStatus.ACCEPTED);
            verify(demandMapper).updateTeamupCurrentMembers(demandUuid);
            verify(demandMapper, never()).expireOtherPendingApplications(any(), any());
            verify(demandMapper, never()).updateDemandStat(any(), any());
            verify(demandMapper, never()).updateDemandTaker(any(), any());
        }

        @Test
        @DisplayName("should expire other pending applications when team becomes full")
        void acceptTeamupApplicationWhenTeamBecomesFull() {
            TeamupDemandDetail detail = new TeamupDemandDetail();
            detail.setDemand_uuid(demandUuid);
            detail.setCurrent_members(3);
            detail.setExpected_members(4);

            DemandDetailVO resultDetail = new DemandDetailVO();
            resultDetail.setId(demandUuid.toString());
            resultDetail.setType(DemandType.TEAMUP);

            when(demandMapper.selectDemandByUuid(demandUuid)).thenReturn(teamupDemand);
            when(demandMapper.selectDemandApplicationByUuid(applicationUuid)).thenReturn(pendingApplication);
            when(demandMapper.selectTeamupDemandDetailByDemandUuid(demandUuid)).thenReturn(detail);
            when(demandMapper.selectDemandDetailByUuid(demandUuid)).thenReturn(resultDetail);

            Result<DemandDetailVO> result = demandService.acceptApplication(validToken, demandUuid, applicationUuid);

            assertEquals(200, result.getCode());
            verify(demandMapper).updateDemandApplicationStatus(applicationUuid, DemandApplicationStatus.ACCEPTED);
            verify(demandMapper).updateTeamupCurrentMembers(demandUuid);
            verify(demandMapper).updateDemandStat(demandUuid, DemandStat.IN_PROGRESS);
            verify(demandMapper).expireOtherPendingApplications(demandUuid, applicationUuid);
            verify(demandMapper, never()).updateDemandTaker(any(), any());
        }
    }

    // ==================== createDemand ====================

    @Nested
    @DisplayName("createDemand Tests")
    class CreateDemandTests {

        @Test
        @DisplayName("should create EXPRESS demand successfully")
        void createExpressDemand() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("帮忙取快递");
            dto.setPickupLocation("菜鸟驿站");
            dto.setDeliveryLocation("宿舍楼A");
            dto.setDeadline("2026-06-10 18:00");

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getId());

            verify(demandMapper).insertDemand(any(Demand.class));
            verify(demandMapper).insertExpressDemandDetail(any());
        }

        @Test
        @DisplayName("should create SECONDHAND demand successfully")
        void createSecondhandDemand() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.SECONDHAND);
            dto.setTitle("出售二手书");
            dto.setItemName("数据结构教材");
            dto.setPrice(new java.math.BigDecimal("25.00"));
            dto.setOriginalPrice(new java.math.BigDecimal("59.00"));

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());

            verify(demandMapper).insertDemand(any(Demand.class));
            verify(demandMapper).insertSecondhandDemandDetail(any());
        }

        @Test
        @DisplayName("should create TUTORING demand successfully")
        void createTutoringDemand() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.TUTORING);
            dto.setTitle("高数辅导");
            dto.setSubject("高等数学");
            dto.setTutoringMode("线下");
            dto.setDuration(60);

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());

            verify(demandMapper).insertDemand(any(Demand.class));
            verify(demandMapper).insertTutoringDemandDetail(any());
        }

        @Test
        @DisplayName("should create TEAMUP demand successfully (TEAM type normalized to TEAMUP)")
        void createTeamupDemand() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.TEAM);
            dto.setTitle("组队参加竞赛");
            dto.setTeamGoal("ACM竞赛");
            dto.setExpectedMembers(3);
            dto.setRequiredSkills(List.of("Java", "算法"));

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());

            verify(demandMapper).insertDemand(any(Demand.class));
            verify(demandMapper).insertTeamupDemandDetail(any());
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void createDemandUnauthorized() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("测试");

            Result<DemandCreateVO> result = demandService.createDemand("bad-token", dto);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
            verify(demandMapper, never()).insertDemand(any());
        }

        @Test
        @DisplayName("should return 400 when DTO is null")
        void createDemandNullDto() {
            Result<DemandCreateVO> result = demandService.createDemand(validToken, null);

            assertEquals(400, result.getCode());
            assertEquals("需求类型和标题不能为空", result.getMessage());
            verify(demandMapper, never()).insertDemand(any());
        }

        @Test
        @DisplayName("should return 400 when type is null")
        void createDemandNullType() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setTitle("测试");

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(400, result.getCode());
            assertEquals("需求类型和标题不能为空", result.getMessage());
            verify(demandMapper, never()).insertDemand(any());
        }

        @Test
        @DisplayName("should return 400 when title is empty")
        void createDemandEmptyTitle() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("");

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(400, result.getCode());
            assertEquals("需求类型和标题不能为空", result.getMessage());
            verify(demandMapper, never()).insertDemand(any());
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void createDemandBlankTitle() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("   ");

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(400, result.getCode());
            assertEquals("需求类型和标题不能为空", result.getMessage());
            verify(demandMapper, never()).insertDemand(any());
        }

        @Test
        @DisplayName("should trim title whitespace")
        void createDemandTrimsTitle() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("  帮忙取快递  ");

            Result<DemandCreateVO> result = demandService.createDemand(validToken, dto);

            assertEquals(200, result.getCode());
            ArgumentCaptor<Demand> captor = ArgumentCaptor.forClass(Demand.class);
            verify(demandMapper).insertDemand(captor.capture());
            assertEquals("帮忙取快递", captor.getValue().getTitle());
        }

        @Test
        @DisplayName("should set demand status to OPEN on creation")
        void createDemandSetsOpenStatus() {
            DemandCreateDTO dto = new DemandCreateDTO();
            dto.setType(DemandType.EXPRESS);
            dto.setTitle("测试需求");

            demandService.createDemand(validToken, dto);

            ArgumentCaptor<Demand> captor = ArgumentCaptor.forClass(Demand.class);
            verify(demandMapper).insertDemand(captor.capture());
            assertEquals(DemandStat.OPEN, captor.getValue().getStat());
        }
    }

    // ==================== uploadDemandImage ====================

    @Nested
    @DisplayName("uploadDemandImage Tests")
    class UploadDemandImageTests {

        @Test
        @DisplayName("should upload image successfully")
        void uploadDemandImageSuccess() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.jpg", "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = demandService.uploadDemandImage(validToken, file);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getUrl().startsWith("/api/uploads/demands/mock-"));
            assertTrue(result.getData().getUrl().contains("test.jpg"));
        }

        @Test
        @DisplayName("should return 401 when token is invalid")
        void uploadDemandImageUnauthorized() {
            when(authService.getUserUuidByToken("bad-token")).thenReturn(null);

            Result<UploadVO> result = demandService.uploadDemandImage("bad-token", null);

            assertEquals(401, result.getCode());
            assertEquals("请先登录", result.getMessage());
        }

        @Test
        @DisplayName("should handle null file gracefully")
        void uploadDemandImageNullFile() {
            Result<UploadVO> result = demandService.uploadDemandImage(validToken, null);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().getUrl().contains("image"));
        }

        @Test
        @DisplayName("should handle file with null original filename")
        void uploadDemandImageNullFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", null, "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = demandService.uploadDemandImage(validToken, file);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getUrl());
            assertTrue(result.getData().getUrl().startsWith("/api/uploads/demands/mock-"));
        }

        @Test
        @DisplayName("should sanitize filename with path separators")
        void uploadDemandImageSanitizesFilename() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "../malicious.jpg", "image/jpeg", "fake-image".getBytes());

            Result<UploadVO> result = demandService.uploadDemandImage(validToken, file);

            assertEquals(200, result.getCode());
            // backslashes and forward slashes should be replaced
            assertTrue(result.getData().getUrl().contains("_malicious.jpg")
                    || result.getData().getUrl().contains("_.."));
        }
    }
}
