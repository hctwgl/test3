package com.ald.fanbei.api.biz.bo.risk;

import java.math.BigDecimal;

/**
 *@类现描述：强风控 回调 请求参数类
 *@author ZJF
 *@version 4.1.2
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ReqFromStrongRiskBo extends ReqFromRiskBo{
	public String result;
	public String scene;
	public BigDecimal amount; //目标场景对应额度
	public BigDecimal totalAmount; //总额度
}
