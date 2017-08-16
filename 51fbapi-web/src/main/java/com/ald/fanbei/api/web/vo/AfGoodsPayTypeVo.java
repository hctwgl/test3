package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：商品支付方式
 * @author chengkang 2017年6月16日 下午21:52:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsPayTypeVo extends AbstractSerial {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3801829761993026000L;
	
	private String code;// 支付类型码
	private String name;// 支付类型描述
	private String isDefault;// 是否默认卡

	public AfGoodsPayTypeVo() {
		super();
	}
	
	public AfGoodsPayTypeVo(String code, String name, String isDefault) {
		super();
		this.code = code;
		this.name = name;
		this.isDefault = isDefault;
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

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	 @Override
	public String toString() {
		return "AfGoodsPayTypeVo [code=" + code + ", name=" + name
				+ ", isDefault=" + isDefault + "]";
	}

}
