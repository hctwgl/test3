package com.ald.fanbei.api.biz.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退货变更的债权信息vo
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月15日下午4:42:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class MgrDashboardInfoVo implements Serializable {
	
	private static final long serialVersionUID = 4204652534348461359L;
	private BigDecimal totalLoanAmt;//总放款额
	private BigDecimal totalLoanAmtRateByWeek;//放款总额周同比
	private Integer borrowMans;//放款人数
	private BigDecimal totalLoanAmtRateByDay;//放款总额日环比
	private BigDecimal avgAmountPer;//人平均放款额
	private BigDecimal loanRate;//放款率
	private Integer pvLastDay;//昨日访问人数
	private BigDecimal riskPassRate;//认证通过率
	private BigDecimal riskPassRateByWeek;//认证通过率周同比
	private BigDecimal riskPassRateByDay;//认证通过率日环比
	
	private BigDecimal totalRepayAmt;	//今日还款额
	private BigDecimal totalRepayAmtRateByWeek;	//今日还款额周同比
	private BigDecimal totalRepayAmtRateByDay;	//今日还款额日环比
	private BigDecimal avgRepayAmtPer;	//人均还款额
}
