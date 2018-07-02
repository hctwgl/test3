package com.ald.fanbei.api.biz.bo.risk;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo.Result;

/**
 *@类现描述：补充认证回调 请求参数类
 *@author ZJF
 *@version 4.1.2
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ReqFromSecondaryRiskBo extends ReqFromRiskBo{
	public Result[] results;
	public BigDecimal amount;
	public Result[] bldResults;
	public BigDecimal bldAmount;
	public BigDecimal totalAmount;
	public Result[] fqResults;
	public BigDecimal fqAmount;
}
