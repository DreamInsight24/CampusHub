package com.campushub.entity.demand;

import java.time.LocalDateTime;
import java.util.UUID;

import com.campushub.common.enums.DemandStat;
import com.campushub.common.enums.DemandType;
public class Demand {
    private UUID uuid;
    private UUID publisher_uuid;
    private UUID taker_uuid;
    private String title;
    private String location;
    private LocalDateTime deadline;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private DemandType type;
    private DemandStat stat;

    public UUID getPublisher_uuid() {
        return publisher_uuid;
    }

    public void setPublisher_uuid(UUID publisher_uuid) {
        this.publisher_uuid = publisher_uuid;
    }

    public UUID getTaker_uuid() {
        return taker_uuid;
    }

    public void setTaker_uuid(UUID taker_uuid) {
        this.taker_uuid = taker_uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
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

    public DemandType getType() {
        return type;
    }

    public void setType(DemandType type) {
        this.type = type;
    }

    public DemandStat getStat() {
        return stat;
    }

    public void setStat(DemandStat stat) {
        this.stat = stat;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
