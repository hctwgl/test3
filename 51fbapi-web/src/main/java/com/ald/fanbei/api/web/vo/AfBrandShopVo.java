package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年3月1日下午16:33:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBrandShopVo extends AbstractSerial{

	private static final long serialVersionUID = -568646704067519875L;

	private String brandName;
	private String brandIcon;
	private String brandRate;
	private String brandUrl;
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getBrandIcon() {
		return brandIcon;
	}
	public void setBrandIcon(String brandIcon) {
		this.brandIcon = brandIcon;
	}
	public String getBrandRate() {
		return brandRate;
	}
	public void setBrandRate(String brandRate) {
		this.brandRate = brandRate;
	}
	public String getBrandUrl() {
		return brandUrl;
	}
	public void setBrandUrl(String brandUrl) {
		this.brandUrl = brandUrl;
	}
	
}
