package com.ald.fanbei.api.biz.bo.risk;

/**
 *@类现描述：风控调用我方接口入参基类
 *@author ZJF
 *@version 4.1.2
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ReqFromRiskBo{
	/**
	 * 通信流水号
	 */
	public String orderNo;
	
	/**
	 * 用户唯一标识号 对应我方库userId
	 */
	public Long consumerNo;
	
}
