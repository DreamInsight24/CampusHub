package com.campushub.dto.demand;

import com.campushub.common.enums.DemandStat;
import com.campushub.common.enums.DemandType;

public class DemandQueryDTO {
    private String keyword;
    private DemandType type;
    private DemandStat status;
    private String sort;
    private Integer page;
    private Integer pageSize;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public DemandType getType() {
        return type;
    }

    public void setType(DemandType type) {
        this.type = type;
    }

    public DemandStat getStatus() {
        return status;
    }

    public void setStatus(DemandStat status) {
        this.status = status;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
