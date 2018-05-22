/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain;


import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @类描述：自营商城 订单结算
 * @author weiqingeng 2017年12月12日下午10:22:55
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
public class AfSupplierOrderSettlementDo extends AbstractSerial {

	private static final long serialVersionUID = -8091350096823799534L;

	private Long rid;
    private Date gmtCreate;
    private Date ruleGmtCreate;//结算规则配置时间
    private Date gmtModified;
    private String creator;
    private String modifier;
    private String settlementNo; //结算单号
	private Date shouldSettlementDate;//应结算时间
    private Long supId;//商户id
    private String supName;//商户名称
	private Integer supStatus;//商户状态 1：启用  0：禁用
	private String contactPhone;//联系人电话
	private String openBankName;//开户行名称
    private String bankNo;//银行卡号
	private String bankIdNumber;//对私时用户的身份证号
	private String bankUserPhone;//开户行绑定的手机号
	private String bankUserName;//银行户名
	private String bankCode;//银行代号
	private String unionBankNo;//对公的银行账号
	private Integer accountType;//账户类型
	private BigDecimal amount;//结算金额（单笔结算订单）
	private BigDecimal totalAmount;//结算总金额（单笔结算单）
	private Integer status;//结算状态
	private Integer verify;//结算单是否人工审核通过 0：否 1：是
	private Date settlementDate;//应结算日期
	private Date finishDate;//结算完成时间
	private Integer settlementPeriod;//结算周期 结算周期【1:按日，2：按周，3:按月】,每天或每周或每月生成一次结算单
	private Integer transferDays;//结算划款天数，生成结算单后，延迟n天执行打款'
	private Long brandId;
	private String brandName;
	private String upsResponse;//ups异步回调
}
