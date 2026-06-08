package com.campushub.service.demand;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.campushub.common.Result;
import com.campushub.common.enums.DemandApplicationStatus;
import com.campushub.common.enums.DemandStat;
import com.campushub.common.enums.DemandType;
import com.campushub.dto.demand.DemandApplicationCreateDTO;
import com.campushub.dto.demand.DemandCreateDTO;
import com.campushub.dto.demand.DemandQueryDTO;
import com.campushub.entity.demand.Demand;
import com.campushub.entity.demand.DemandApplication;
import com.campushub.entity.demand.ExpressDemandDetail;
import com.campushub.entity.demand.SecondhandDemandDetail;
import com.campushub.entity.demand.TeamupDemandDetail;
import com.campushub.entity.demand.TutoringDemandDetail;
import com.campushub.mapper.DemandMapper;
import com.campushub.service.auth.AuthService;
import com.campushub.vo.PageVO;
import com.campushub.vo.UploadVO;
import com.campushub.vo.demand.DemandApplicationVO;
import com.campushub.vo.demand.DemandCreateVO;
import com.campushub.vo.demand.DemandDetailVO;
import com.campushub.vo.demand.DemandListVO;

@Service
public class DemandService {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final String SORT_DEADLINE = "deadline";
    private static final String SORT_LATEST = "latest";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DemandMapper demandMapper;
    private final AuthService authService;

    public DemandService(DemandMapper demandMapper, AuthService authService) {
        this.demandMapper = demandMapper;
        this.authService = authService;
    }

    public PageVO<DemandListVO> searchDemands(DemandQueryDTO queryDTO) {
        DemandQueryDTO query = queryDTO == null ? new DemandQueryDTO() : queryDTO;
        normalize(query);

        int offset = (query.getPage() - 1) * query.getPageSize();
        List<DemandListVO> items = demandMapper.searchDemands(query, offset);
        long total = demandMapper.countDemands(query);

        return new PageVO<>(items, total, query.getPage(), query.getPageSize());
    }

    public Result<DemandDetailVO> getDemandDetail(UUID demandUuid) {
        if (demandUuid == null) {
            return Result.error(400, "需求ID不能为空");
        }

        DemandDetailVO detail = demandMapper.selectDemandDetailByUuid(demandUuid);
        if (detail == null) {
            return Result.error(404, "需求不存在");
        }

        return Result.success(detail);
    }

    public Result<List<DemandDetailVO>> listMyPublishedDemands(String token) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(demandMapper.selectPublishedDemandDetails(userUuid));
    }

    public Result<List<DemandDetailVO>> listMyAcceptedDemands(String token) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(demandMapper.selectAcceptedDemandDetails(userUuid));
    }

    @Transactional
    public Result<DemandDetailVO> respondDemand(String token, UUID demandUuid) {
        return respondDemand(token, demandUuid, null);
    }

    @Transactional
    public Result<DemandDetailVO> respondDemand(String token, UUID demandUuid, DemandApplicationCreateDTO dto) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        Demand demand = demandMapper.selectDemandByUuid(demandUuid);
        if (demand == null) {
            return Result.error(404, "需求不存在");
        }
        if (userUuid.equals(demand.getPublisher_uuid())) {
            return Result.error(400, "不能接取自己发布的需求");
        }
        if (demand.getStat() != DemandStat.OPEN) {
            return Result.error(400, "该需求当前不可接取");
        }

        if (isApplicationDemand(demand.getType())) {
            DemandApplicationVO existing = demandMapper.selectDemandApplicationByDemandAndApplicant(demandUuid, userUuid);
            if (existing != null) {
                return Result.error(409, "已申请，请等待发布者确认");
            }

            DemandApplication application = new DemandApplication();
            LocalDateTime now = LocalDateTime.now();
            application.setUuid(UUID.randomUUID());
            application.setDemandUuid(demandUuid);
            application.setApplicantUuid(userUuid);
            application.setStatement(dto == null ? null : trimToNull(dto.getStatement()));
            application.setStatus(DemandApplicationStatus.PENDING);
            application.setCreateTime(now);
            application.setUpdateTime(now);
            demandMapper.insertDemandApplication(application);
            return getDemandDetail(demandUuid);
        }

        if (demand.getTaker_uuid() != null) {
            return Result.error(409, "该需求已被接取");
        }

        int updated = demandMapper.updateDemandTaker(demandUuid, userUuid);
        if (updated == 0) {
            return Result.error(409, "该需求已被其他用户接取");
        }

        return getDemandDetail(demandUuid);
    }

    public Result<DemandApplicationVO> getMyApplication(String token, UUID demandUuid) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }
        if (demandUuid == null) {
            return Result.error(400, "需求ID不能为空");
        }

        return Result.success(demandMapper.selectDemandApplicationByDemandAndApplicant(demandUuid, userUuid));
    }

    public Result<List<DemandApplicationVO>> listDemandApplications(String token, UUID demandUuid) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        Demand demand = demandMapper.selectDemandByUuid(demandUuid);
        if (demand == null) {
            return Result.error(404, "需求不存在");
        }
        if (!userUuid.equals(demand.getPublisher_uuid())) {
            return Result.error(403, "无权查看该需求的申请列表");
        }

        return Result.success(demandMapper.selectDemandApplications(demandUuid));
    }

    @Transactional
    public Result<DemandDetailVO> acceptApplication(String token, UUID demandUuid, UUID applicationUuid) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        Demand demand = demandMapper.selectDemandByUuid(demandUuid);
        if (demand == null) {
            return Result.error(404, "需求不存在");
        }
        if (!userUuid.equals(demand.getPublisher_uuid())) {
            return Result.error(403, "无权审核该需求的申请");
        }
        if (!isApplicationDemand(demand.getType())) {
            return Result.error(400, "该需求不支持申请审核");
        }
        if (demand.getStat() != DemandStat.OPEN) {
            return Result.error(400, "该需求当前不能接受申请");
        }

        DemandApplication application = demandMapper.selectDemandApplicationByUuid(applicationUuid);
        if (application == null || !demandUuid.equals(application.getDemandUuid())) {
            return Result.error(404, "申请记录不存在");
        }
        if (application.getStatus() != DemandApplicationStatus.PENDING) {
            return Result.error(409, "该申请已处理");
        }

        if (normalizeType(demand.getType()) == DemandType.EXPRESS) {
            int updated = demandMapper.updateDemandTaker(demandUuid, application.getApplicantUuid());
            if (updated == 0) {
                return Result.error(409, "该跑腿需求已无法接受申请");
            }
            demandMapper.updateDemandApplicationStatus(applicationUuid, DemandApplicationStatus.ACCEPTED);
            demandMapper.expireOtherPendingApplications(demandUuid, applicationUuid);
            return getDemandDetail(demandUuid);
        }

        demandMapper.updateDemandApplicationStatus(applicationUuid, DemandApplicationStatus.ACCEPTED);
        demandMapper.updateTeamupCurrentMembers(demandUuid);
        return getDemandDetail(demandUuid);
    }

    @Transactional
    public Result<DemandApplicationVO> rejectApplication(String token, UUID demandUuid, UUID applicationUuid) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        Demand demand = demandMapper.selectDemandByUuid(demandUuid);
        if (demand == null) {
            return Result.error(404, "需求不存在");
        }
        if (!userUuid.equals(demand.getPublisher_uuid())) {
            return Result.error(403, "无权审核该需求的申请");
        }
        if (demand.getStat() != DemandStat.OPEN) {
            return Result.error(400, "该需求当前不能拒绝申请");
        }

        DemandApplication application = demandMapper.selectDemandApplicationByUuid(applicationUuid);
        if (application == null || !demandUuid.equals(application.getDemandUuid())) {
            return Result.error(404, "申请记录不存在");
        }
        if (application.getStatus() != DemandApplicationStatus.PENDING) {
            return Result.error(409, "该申请已处理");
        }

        demandMapper.updateDemandApplicationStatus(applicationUuid, DemandApplicationStatus.REJECTED);
        return Result.success(demandMapper.selectDemandApplicationVOByUuid(applicationUuid));
    }

    @Transactional
    public Result<DemandDetailVO> endDemand(String token, UUID demandUuid) {
        return closeDemand(token, demandUuid, DemandStat.CLOSED);
    }

    @Transactional
    public Result<DemandDetailVO> cancelDemand(String token, UUID demandUuid) {
        return closeDemand(token, demandUuid, DemandStat.CANCELLED);
    }

    public Result<DemandCreateVO> createDemand(String token, DemandCreateDTO dto) {
        UUID publisherUuid = authService.getUserUuidByToken(token);
        if (publisherUuid == null) {
            return Result.error(401, "请先登录");
        }

        if (dto == null || dto.getType() == null || !StringUtils.hasText(dto.getTitle())) {
            return Result.error(400, "需求类型和标题不能为空");
        }

        UUID demandUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Demand demand = new Demand();
        demand.setUuid(demandUuid);
        demand.setPublisher_uuid(publisherUuid);
        demand.setTitle(dto.getTitle().trim());
        demand.setLocation(dto.getLocation());
        demand.setDeadline(parseDateTime(dto.getDeadline()));
        demand.setCreateTime(now);
        demand.setUpdateTime(now);
        demand.setType(normalizeType(dto.getType()));
        demand.setStat(DemandStat.OPEN);

        demandMapper.insertDemand(demand);
        insertDetail(demandUuid, dto);

        return Result.success(new DemandCreateVO(demandUuid.toString()));
    }

    public Result<UploadVO> uploadDemandImage(String token, MultipartFile file) {
        if (authService.getUserUuidByToken(token) == null) {
            return Result.error(401, "请先登录");
        }

        String filename = file == null || file.getOriginalFilename() == null
                ? "image"
                : file.getOriginalFilename().replace("\\", "_").replace("/", "_");
        return Result.success(new UploadVO("/api/uploads/demands/mock-" + UUID.randomUUID() + "-" + filename));
    }

    private void normalize(DemandQueryDTO query) {
        if (StringUtils.hasText(query.getKeyword())) {
            query.setKeyword(query.getKeyword().trim());
        } else {
            query.setKeyword(null);
        }

        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(DEFAULT_PAGE);
        }

        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(DEFAULT_PAGE_SIZE);
        } else if (query.getPageSize() > MAX_PAGE_SIZE) {
            query.setPageSize(MAX_PAGE_SIZE);
        }

        if (!SORT_DEADLINE.equalsIgnoreCase(query.getSort())) {
            query.setSort(SORT_LATEST);
        } else {
            query.setSort(SORT_DEADLINE);
        }
    }

    private void insertDetail(UUID demandUuid, DemandCreateDTO dto) {
        DemandType type = normalizeType(dto.getType());
        ArrayList<String> imageUrls = new ArrayList<>();
        if (dto.getImageUrls() != null) {
            imageUrls.addAll(dto.getImageUrls());
        }

        if (type == DemandType.EXPRESS) {
            ExpressDemandDetail detail = new ExpressDemandDetail();
            detail.setDemand_uuid(demandUuid);
            detail.setPickup_location(dto.getPickupLocation());
            detail.setDelivery_location(dto.getDeliveryLocation());
            detail.setPickup_code(dto.getPickupCode());
            detail.setExpected_delivery_time(parseDateTime(dto.getExpectedDeliveryTime()));
            detail.setImage_urls(imageUrls);
            detail.setDescription(dto.getDescription());
            demandMapper.insertExpressDemandDetail(detail);
            return;
        }

        if (type == DemandType.SECONDHAND) {
            SecondhandDemandDetail detail = new SecondhandDemandDetail();
            detail.setDemand_uuid(demandUuid);
            detail.setItem_name(dto.getItemName());
            detail.setCategory(dto.getCategory());
            detail.setPrice(dto.getPrice());
            detail.setOriginal_price(dto.getOriginalPrice());
            detail.setCondition_level(dto.getConditionLevel());
            detail.setTrade_location(dto.getTradeLocation());
            detail.setImage_urls(imageUrls);
            detail.setDescription(dto.getDescription());
            demandMapper.insertSecondhandDemandDetail(detail);
            return;
        }

        if (type == DemandType.TUTORING) {
            TutoringDemandDetail detail = new TutoringDemandDetail();
            detail.setDemand_uuid(demandUuid);
            detail.setSubject(dto.getSubject());
            detail.setTutoring_mode(dto.getTutoringMode());
            detail.setExpected_time(parseDateTime(dto.getExpectedTime()));
            detail.setDuration(dto.getDuration());
            detail.setLevel_requirement(dto.getLevelRequirement());
            detail.setImage_urls(imageUrls);
            detail.setDescription(dto.getDescription());
            demandMapper.insertTutoringDemandDetail(detail);
            return;
        }

        TeamupDemandDetail detail = new TeamupDemandDetail();
        detail.setDemand_uuid(demandUuid);
        detail.setTeam_goal(dto.getTeamGoal());
        detail.setCurrent_members(dto.getCurrentMembers());
        detail.setExpected_members(dto.getExpectedMembers());
        detail.setRequired_skills(new ArrayList<>(dto.getRequiredSkills() == null ? List.of() : dto.getRequiredSkills()));
        detail.setDeadline(parseDateTime(dto.getDeadline()));
        detail.setContact_method(dto.getContactMethod());
        detail.setImage_urls(imageUrls);
        detail.setDescription(dto.getDescription());
        demandMapper.insertTeamupDemandDetail(detail);
    }

    private Result<DemandDetailVO> closeDemand(String token, UUID demandUuid, DemandStat targetStat) {
        UUID userUuid = authService.getUserUuidByToken(token);
        if (userUuid == null) {
            return Result.error(401, "请先登录");
        }

        Demand demand = demandMapper.selectDemandByUuid(demandUuid);
        if (demand == null) {
            return Result.error(404, "需求不存在");
        }
        if (!userUuid.equals(demand.getPublisher_uuid())) {
            return Result.error(403, "无权操作该需求");
        }
        if (demand.getStat() == DemandStat.CLOSED || demand.getStat() == DemandStat.CANCELLED) {
            return getDemandDetail(demandUuid);
        }

        int updated = demandMapper.updateDemandStatByPublisher(demandUuid, userUuid, targetStat);
        if (updated == 0) {
            return Result.error(409, "需求状态已变化，请刷新后重试");
        }
        demandMapper.expirePendingApplicationsByDemand(demandUuid);
        return getDemandDetail(demandUuid);
    }

    private DemandType normalizeType(DemandType type) {
        return type == DemandType.TEAM ? DemandType.TEAMUP : type;
    }

    private boolean isApplicationDemand(DemandType type) {
        DemandType normalized = normalizeType(type);
        return normalized == DemandType.EXPRESS || normalized == DemandType.TEAMUP;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private LocalDateTime parseDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim();
        try {
            if (normalized.contains("T")) {
                return LocalDateTime.parse(normalized);
            }
            return LocalDateTime.parse(normalized, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
