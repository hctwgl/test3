package com.ald.fanbei.api.dal.domain.query;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AfSeckillActivityQuery {
    private Long activityId;
    private String name;
    private Integer type;
    private Date gmtStart;
    private Date gmtEnd;
    private Date gmtPStart;
    private List<Long> goodsIdList;
}
