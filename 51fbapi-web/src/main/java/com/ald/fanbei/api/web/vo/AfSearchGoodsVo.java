package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月9日上午11:51:57
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSearchGoodsVo extends AbstractSerial {

	private static final long serialVersionUID = 7350723172877682162L;
	
	private Long numIid;//淘宝商品id
	private String title;//商品标题
	private String pictUrl;//商品主图
	private String reservePrice;//商品原价
	private Long volume;//30天销量
	private String rebateStart;//返利下限
	private String rebateEnd;//返利上限
	
	public Long getNumIid() {
		return numIid;
	}
	public void setNumIid(Long numIid) {
		this.numIid = numIid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPictUrl() {
		return pictUrl;
	}
	public void setPictUrl(String pictUrl) {
		this.pictUrl = pictUrl;
	}
	public String getReservePrice() {
		return reservePrice;
	}
	public void setReservePrice(String reservePrice) {
		this.reservePrice = reservePrice;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}
	public String getRebateStart() {
		return rebateStart;
	}
	public void setRebateStart(String rebateStart) {
		this.rebateStart = rebateStart;
	}
	public String getRebateEnd() {
		return rebateEnd;
	}
	public void setRebateEnd(String rebateEnd) {
		this.rebateEnd = rebateEnd;
	}
	
	

}
