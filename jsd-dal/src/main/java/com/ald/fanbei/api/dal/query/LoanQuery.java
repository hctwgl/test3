package com.ald.fanbei.api.dal.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;

import java.util.List;

public class LoanQuery extends Page<LoanDto> {
    /**
     * 审核列表【ALL：所有，WAIT：待审核，PASS：通过，REFUSE：拒绝】
     * 借款列表【ALL：全部，WAITTRANSED:待打款, TRANSED:已经打款,CLOSED:关闭,FINSH:已结清】
     * 还款计划列表【TRANSED:已经打款,OVERDUE:逾期,FINSH:已结清】
     */
    private String status;
    /**
     * 模糊匹配字段
     */
    private String searchContent;

    private List<LoanDto> list;

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

    public List<LoanDto> getList() {
        return list;
    }

    public void setList(List<LoanDto> list) {
        this.list = list;
    }
}