package com.ald.fanbei.api.biz.service.wxpay;

/**
 * 
 *@类描述：微信支付配置
 *
 *@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxpayConfig {
	
	public static final String KEY_SIGN                    = "sign";
	public static final String WX_REFUND_API               = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
//	public static final String WX_APP_ID                   = "wx195a899c2da34c8b";
//	public static final String WX_MCH_ID                   = "1373909902";
	
	public static final String WX_REFUND_QUERY_API               = "https://api.mch.weixin.qq.com/pay/refundquery";

	
//	public static final String WX_ORDERQUERY_API           = "https://api.mch.weixin.qq.com/pay/orderquery";

}
