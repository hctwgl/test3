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
	UPS_ERROR_1001("1001","系统正忙，您可联系客服或稍后重试", ""),
	UPS_ERROR_2001("2001","银行维护中", ""),
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
	UPS_ERROR_5001("5001","卡未开通银联无卡支付，您可换卡重试", ""),
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
        return null;
    }
    public static UpsErrorType findRoleTypeByName(String name) {
        for (UpsErrorType roleType : UpsErrorType.values()) {
            if (StringUtils.equals(name, roleType.getName())) {
                return roleType;
            }
        }
        return null;
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
