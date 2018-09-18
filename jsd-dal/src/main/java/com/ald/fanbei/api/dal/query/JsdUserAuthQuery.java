package com.ald.fanbei.api.dal.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import lombok.Getter;
import lombok.Setter;

public class JsdUserAuthQuery extends Page<JsdUserAuthDo>{

    /**
     * 认证状态，直接存入西关递交的值
     */
    private String riskStatus;
    /**
     * 搜索查询的内容
     */
    private String searchContent;

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }
}
