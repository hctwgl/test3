package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：商品详情图片相关
 * @author chengkang 2017年6月20日下午14:49:41
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GoodsDetailPicInfoVo extends AbstractSerial{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2440846766943695737L;
	private String picUrl;
	private String width;
	private String height;
	
	public GoodsDetailPicInfoVo() {
		super();
	}
	
	
	public GoodsDetailPicInfoVo(String picUrl, String width, String height) {
		super();
		this.picUrl = picUrl;
		this.width = width;
		this.height = height;
	}


	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
}
