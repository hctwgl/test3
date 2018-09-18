package com.ald.fanbei.api.dal.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.ReviewLoanDto;

import java.util.List;

public class ReviewLoanQuery extends Page<ReviewLoanDto> {
    /**
     * 【TRANSED:已经打款,OVERDUE:逾期,FINSH:已结清】
     */
    private String status;
    /**
     * 模糊匹配字段
     */
    private String searchContent;

    private List<ReviewLoanDto> list;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public List<ReviewLoanDto> getList() {
        return list;
    }

    public void setList(List<ReviewLoanDto> list) {
        this.list = list;
    }
}
