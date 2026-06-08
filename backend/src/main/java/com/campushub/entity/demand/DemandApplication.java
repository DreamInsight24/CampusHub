package com.campushub.entity.demand;

import java.time.LocalDateTime;
import java.util.UUID;

import com.campushub.common.enums.DemandApplicationStatus;

public class DemandApplication {
    private UUID uuid;
    private UUID demandUuid;
    private UUID applicantUuid;
    private String statement;
    private DemandApplicationStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getDemandUuid() {
        return demandUuid;
    }

    public void setDemandUuid(UUID demandUuid) {
        this.demandUuid = demandUuid;
    }

    public UUID getApplicantUuid() {
        return applicantUuid;
    }

    public void setApplicantUuid(UUID applicantUuid) {
        this.applicantUuid = applicantUuid;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public DemandApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(DemandApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
