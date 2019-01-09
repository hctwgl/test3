/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @类描述：
 * @author cfp 2017年3月25日上午10:30:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UpsErrorType {
	UPS_ERROR_0000("0000","请求成功", ""),
	UPS_ERROR_0001("0001","交易处理中，您可稍后查询", ""),
	UPS_ERROR_1001("1001","参数不能为空", ""),
	UPS_ERROR_1002("1002","参数不规范", ""),
	UPS_ERROR_1003("1003","验签未通过", ""),
	UPS_ERROR_1004("1004","当前订单不存在", ""),
	UPS_ERROR_1005("1005","无此服务", ""),
	UPS_ERROR_1006("1006","无效的访问IP试", ""),
	UPS_ERROR_1007("1007","无权限访问", ""),
	UPS_ERROR_1008("1008","暂无可用第三方，请稍后再试", ""),
	UPS_ERROR_1009("1009","商户合约结束时间设置有误", ""),
	UPS_ERROR_1010("1010","用户未签约或签约状态有误", ""),
	UPS_ERROR_2001("2001","第三方响应超时", ""),
	UPS_ERROR_2002("2002","请求第三方失败", ""),
	UPS_ERROR_2003("2003","请求第三方时出现异常", ""),
	UPS_ERROR_2004("2004","第三方响应验签失败", ""),
	UPS_ERROR_2005("2005","第三方响应参数转换异常", ""),
	UPS_ERROR_3000("3000","请求第三方成功，但无参数返回", ""),
	UPS_ERROR_3001("3001","请求第三方状态不为200", ""),
	UPS_ERROR_3002("3002","请求第三方超时", ""),
	UPS_ERROR_3003("3003","请求第三方出现其他异常", ""),
	UPS_ERROR_3004("3004","第三方保证金不足", ""),
	UPS_ERROR_3005("3005","第三方渠道或银行正在维护不可用", ""),
	UPS_ERROR_3006("3006","第三方校验失败", ""),
	UPS_ERROR_3007("3007","证书错误", ""),
	UPS_ERROR_3008("3008","商户未开通该业务或状态异常", ""),
	UPS_ERROR_3009("3009","商户号不存在或状态异常", ""),
	UPS_ERROR_3010("3010","第三方交易异常", ""),
	UPS_ERROR_3011("3011","用户未签约或者未绑卡", ""),
	UPS_ERROR_3012("3012","支付渠道不支持该银行", ""),
	UPS_ERROR_3013("3013","第三方用户签约或绑卡失败", ""),
	UPS_ERROR_4001("4001","卡余额不足，您可换卡重试", ""),
	UPS_ERROR_4002("4002","余额不足次数超限，您可换卡或次日重试", ""),
	UPS_ERROR_4003("4003","银行卡单笔金额超限，您可换卡重试", ""),
	UPS_ERROR_4004("4004","银行卡单日金额超限，您可换卡重试", ""),
	UPS_ERROR_4005("4005","金额小于银行最低单笔限额，您可换卡重试", ""),
	UPS_ERROR_4006("4006","金额超过银行月累计限额，您可换卡重试", ""),
	UPS_ERROR_4007("4007","银行卡失败次数超限，您可换卡或次日重试", ""),
	UPS_ERROR_4008("4008","支付次数超过发卡行限制，您可换卡重试", ""),
	UPS_ERROR_4009("4009","当前银行卡不支持该业务，您可换卡重试", ""),
	UPS_ERROR_4010("4010","密码输入失败次数超限，您可换卡重试", ""),
	UPS_ERROR_4011("4011","持卡人信息有误，您可核对身份信息是否有误", ""),
	UPS_ERROR_4012("4012","银行卡已过有效期，您可换卡重试", ""),
	UPS_ERROR_4013("4013","银行卡状态异常，您可换卡重试", ""),
	UPS_ERROR_4014("4014","交易失败，您可联系发卡行", ""),
	UPS_ERROR_4015("4015","交易次数频繁，请稍后重试", ""),
	UPS_ERROR_4016("4016","该卡当日交易笔数超过限制，请次日再试", ""),
	UPS_ERROR_4017("4017","输入密码有误", ""),
	UPS_ERROR_4018("4018","输入密码超限", ""),
	UPS_ERROR_4019("4019","订单已过有效期", ""),
	UPS_ERROR_4020("4020","处理中，请稍后查询支付结果", ""),
	UPS_ERROR_4021("4021","当前交易不支持信用卡", ""),
	UPS_ERROR_4022("4022","暂不支持该银行卡绑定", ""),
	UPS_ERROR_4025("4025","银行卡号无效或输入有误", ""),
	UPS_ERROR_4026("4026","该银行卡已绑定", ""),
	UPS_ERROR_4027("4027","交易金额不正确", ""),
	UPS_ERROR_5001("5001","卡未开通银联无卡支付，您可换卡重试", ""),
	UPS_ERROR_5002("5002","代付账户余额不足", ""),
	UPS_ERROR_5003("5003","短信验证码超时或过期失效", ""),
	UPS_ERROR_5004("5004","短信验证码不符，请重新确认", ""),
	UPS_ERROR_5005("5005","交易关闭，请重新发起", ""),
	UPS_ERROR_9998("9998","匹配不到第三方响应码，默认返回码", ""),
	UPS_ERROR_9999("9999","系统错误", ""),
	UPS_ERROR_10012("10012","退款次数超限", ""),
	UPS_ERROR_10013("10013","累计退款金额超限", ""),
	UPS_ERROR_default("default","银行卡交易失败，您可换卡或稍后重试", "");


	private String code;
    private String name;
    private String dec;


    UpsErrorType(String code, String name, String dec) {
        this.code = code;
        this.name = name;
        this.setDec(dec);

    }
    public static UpsErrorType findRoleTypeByCode(String code) {
        for (UpsErrorType roleType : UpsErrorType.values()) {
            if (StringUtils.equals(code, roleType.getCode())) {
                return roleType;
            }
        }
        return UPS_ERROR_default;
    }
    public static UpsErrorType findRoleTypeByName(String name) {
        for (UpsErrorType roleType : UpsErrorType.values()) {
            if (StringUtils.equals(name, roleType.getName())) {
                return roleType;
            }
        }
        return UPS_ERROR_default;
    }
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Ups error," + code + "," + name;
	}

	/**
	 * @return the dec
	 */
	public String getDec() {
		return dec;
	}

	/**
	 * @param dec the dec to set
	 */
	public void setDec(String dec) {
		this.dec = dec;
	}

}
