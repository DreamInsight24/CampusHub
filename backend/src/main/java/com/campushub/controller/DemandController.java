package com.campushub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.campushub.common.Result;
import com.campushub.dto.demand.DemandApplicationCreateDTO;
import com.campushub.dto.demand.DemandCreateDTO;
import com.campushub.dto.demand.DemandQueryDTO;
import com.campushub.service.demand.DemandService;
import com.campushub.vo.PageVO;
import com.campushub.vo.UploadVO;
import com.campushub.vo.demand.DemandApplicationVO;
import com.campushub.vo.demand.DemandCreateVO;
import com.campushub.vo.demand.DemandDetailVO;
import com.campushub.vo.demand.DemandListVO;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/demands")
public class DemandController {
    private final DemandService demandService;

    public DemandController(DemandService demandService) {
        this.demandService = demandService;
    }

    @GetMapping
    public Result<PageVO<DemandListVO>> searchDemands(DemandQueryDTO queryDTO) {
        return Result.success(demandService.searchDemands(queryDTO));
    }

    @GetMapping("/mine/published")
    public Result<List<DemandDetailVO>> listMyPublishedDemands(
            @RequestHeader(value = "token", required = false) String token) {
        return demandService.listMyPublishedDemands(token);
    }

    @GetMapping("/mine/accepted")
    public Result<List<DemandDetailVO>> listMyAcceptedDemands(
            @RequestHeader(value = "token", required = false) String token) {
        return demandService.listMyAcceptedDemands(token);
    }

    @GetMapping("/favorites")
    public Result<List<DemandDetailVO>> listMyFavoriteDemands(
            @RequestHeader(value = "token", required = false) String token) {
        return demandService.listMyFavoriteDemands(token);
    }

    @GetMapping("/{id}")
    public Result<DemandDetailVO> getDemandDetail(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        if (token == null || token.isBlank()) {
            return demandService.getDemandDetail(id);
        }
        return demandService.getDemandDetail(token, id);
    }

    @PostMapping("/{id}/responses")
    public Result<DemandDetailVO> respondDemand(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id,
            @RequestBody(required = false) DemandApplicationCreateDTO dto) {
        if (dto == null) {
            return demandService.respondDemand(token, id);
        }
        return demandService.respondDemand(token, id, dto);
    }

    @GetMapping("/{id}/applications/mine")
    public Result<DemandApplicationVO> getMyApplication(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.getMyApplication(token, id);
    }

    @GetMapping("/{id}/applications")
    public Result<List<DemandApplicationVO>> listDemandApplications(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.listDemandApplications(token, id);
    }

    @PostMapping("/{id}/applications/{applicationId}/accept")
    public Result<DemandDetailVO> acceptApplication(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id,
            @PathVariable UUID applicationId) {
        return demandService.acceptApplication(token, id, applicationId);
    }

    @PostMapping("/{id}/applications/{applicationId}/reject")
    public Result<DemandApplicationVO> rejectApplication(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id,
            @PathVariable UUID applicationId) {
        return demandService.rejectApplication(token, id, applicationId);
    }

    @PostMapping("/{id}/end")
    public Result<DemandDetailVO> endDemand(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.endDemand(token, id);
    }

    @PostMapping("/{id}/cancel")
    public Result<DemandDetailVO> cancelDemand(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.cancelDemand(token, id);
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> favoriteDemand(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.favoriteDemand(token, id);
    }

    @DeleteMapping("/{id}/favorite")
    public Result<Void> unfavoriteDemand(
            @RequestHeader(value = "token", required = false) String token,
            @PathVariable UUID id) {
        return demandService.unfavoriteDemand(token, id);
    }

    @PostMapping
    public Result<DemandCreateVO> createDemand(
            @RequestHeader(value = "token", required = false) String token,
            @RequestBody DemandCreateDTO dto) {
        return demandService.createDemand(token, dto);
    }

    @PostMapping("/images")
    public Result<UploadVO> uploadDemandImage(
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam("file") MultipartFile file) {
        return demandService.uploadDemandImage(token, file);
    }
}
