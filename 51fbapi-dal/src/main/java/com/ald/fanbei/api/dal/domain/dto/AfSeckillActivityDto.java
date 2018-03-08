package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;

public class AfSeckillActivityDto extends AfSeckillActivityDo {
    private Integer limitCount;

    public Integer getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }
}
