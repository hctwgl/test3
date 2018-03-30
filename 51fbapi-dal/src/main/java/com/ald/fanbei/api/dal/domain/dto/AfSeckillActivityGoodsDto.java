package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.zip.DataFormatException;

@Getter
@Setter
public class AfSeckillActivityGoodsDto extends AfSeckillActivityGoodsDo {
    private Date gmtStart;
    private Date gmtEnd;
    private Integer closeTime;
    private Integer goodsLimitCount;
    private Integer type;
}
