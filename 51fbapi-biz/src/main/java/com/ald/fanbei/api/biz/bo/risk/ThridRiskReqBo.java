package com.ald.fanbei.api.biz.bo.risk;

/**
 *@类现描述：风控调用我方接口入参类
 *@author ZJF
 *@version 4.1.2
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ThridRiskReqBo{
	public String orderNo;//对应触发的回调订单号
	public String customerNo;//
	public String code;
	public String msg;
	public String signInfo;
}
