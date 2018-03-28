package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 秒杀活动管理(商品)实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-08 21:35:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfSeckillActivityOrderDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 特惠价格
     */
    private BigDecimal specialPrice;

    /**
     * 商品数量
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动开始时间
     */
    private Date gmtStart;

    /**
     * 活动结束时间
     */
    private Date gmtEnd;

}