package com.ald.fanbei.api.dal.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.jsd.mgr.dal.domain.dto.UserAuthDto;

import java.util.List;

public class UserAuthQuery extends Page<JsdUserAuthDo>{

    /**
     * 认证状态，直接存入西关递交的值
     */
    private String riskStatus;
    /**
     * 搜索查询的内容
     */
    private String searchContent;

    private List<UserAuthDto> list;

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

    public List<UserAuthDto> getList() {
        return list;
    }

    public void setList(List<UserAuthDto> list) {
        this.list = list;
    }
}
