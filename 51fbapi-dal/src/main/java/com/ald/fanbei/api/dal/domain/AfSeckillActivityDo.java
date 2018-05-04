package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 秒杀活动管理实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfSeckillActivityDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型，1表示特惠类型，2表示秒杀类型
     */
    private Integer type;

    /**
     * 活动图片URL地址
     */
    private String iconUrl;

    /**
     * 活动开始时间
     */
    private Date gmtStart;

    /**
     * 活动结束时间
     */
    private Date gmtEnd;

    /**
     * 背景颜色
     */
    private String bgColor;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 活动状态 1：启用，0：禁用
     */
    private Integer status;

    /**
     * 是否删除，0表示启用，1表示禁用
     */
    private Integer isDisable;

    /**
     * 折扣类型，1表示折扣，2表示立减
     */
    private Integer discountType;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 立减价格
     */
    private BigDecimal subtractPrice;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 活动开始时间
     */
    private Date gmtPStart;
    private Integer goodsLimitCount;//没人限购数量
    private String payType;//支付方式
    private Date nowDate;
    private Integer saleCount;
}