package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AfActGoodsDto extends AfGoodsDo{
    private BigDecimal specialPrice;
    private Long activityId;
    private Long goodsId;
    private Integer goodsCount;
}
