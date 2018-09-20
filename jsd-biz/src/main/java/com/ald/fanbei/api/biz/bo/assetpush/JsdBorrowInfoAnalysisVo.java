package com.ald.fanbei.api.biz.bo.assetpush;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 退货变更的债权信息vo
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月15日下午4:42:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class JsdBorrowInfoAnalysisVo implements Serializable {
	
	private static final long serialVersionUID = 4204652534348461359L;
	private BigDecimal totalLoanAmount;//总放款额
	private BigDecimal returnedRate;//回款率
	private Integer borrowMans;//放款人数
	private BigDecimal repeatBorrowRate;//复借率
	private BigDecimal overdueRate;//逾期率
	private BigDecimal badRate;//不良率
	private BigDecimal riskPassRate;//认证通过率
	private BigDecimal borrowPassRate;//借款通过率
}
