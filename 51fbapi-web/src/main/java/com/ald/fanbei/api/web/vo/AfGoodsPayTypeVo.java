package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：商品支付方式
 * @author chengkang 2017年6月16日 下午21:52:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsPayTypeVo extends AbstractSerial {

	private static final long serialVersionUID = 990327740589938087L;

	private String code;// 支付类型码
	private String name;// 支付类型描述
	private String isDefault;// 是否默认卡

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

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

}
