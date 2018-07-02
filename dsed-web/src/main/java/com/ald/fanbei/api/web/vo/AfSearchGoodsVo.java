package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午11:21:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSearchGoodsVo extends AbstractSerial implements Comparable<AfSearchGoodsVo>{

	private static final long serialVersionUID = 7350723172877682162L;
	
	private String numId;//淘宝商品id
	private BigDecimal saleAmount;//商品价格
	private String realAmount;//到手价
	private String rebateAmount;//返利金额
	private String goodsName;//商品名称
	private String goodsIcon;//商品图片
	private String thumbnailIcon;//商品缩略图
	private String goodsUrl;//商品链接
	private Long volume;//销量
	private Map<String, Object> nperMap;//月供
	private String source;
	
	
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public AfSearchGoodsVo() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String toString() {
		return "AfSearchGoodsVo [numId=" + numId + ", saleAmount=" + saleAmount + ", realAmount=" + realAmount
				+ ", rebateAmount=" + rebateAmount + ", goodsName=" + goodsName + ", goodsIcon=" + goodsIcon
				+ ", thumbnailIcon=" + thumbnailIcon + ", goodsUrl=" + goodsUrl + ", volume=" + volume + ", nperMap="
				+ nperMap + ", source=" + source + "]";
	}
	public AfSearchGoodsVo(String numId, BigDecimal saleAmount, String realAmount, String rebateAmount,
			String goodsName, String goodsIcon, String thumbnailIcon, String goodsUrl, Long volume,
			Map<String, Object> nperMap, String source) {
		super();
		this.numId = numId;
		this.saleAmount = saleAmount;
		this.realAmount = realAmount;
		this.rebateAmount = rebateAmount;
		this.goodsName = goodsName;
		this.goodsIcon = goodsIcon;
		this.thumbnailIcon = thumbnailIcon;
		this.goodsUrl = goodsUrl;
		this.volume = volume;
		this.nperMap = nperMap;
		this.source = source;
	}
	public Long getVolume() {
		return volume;
	}
	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public String getNumId() {
		return numId;
	}
	public void setNumId(String numId) {
		this.numId = numId;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public String getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(String realAmount) {
		this.realAmount = realAmount;
	}
	public String getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsIcon() {
		return goodsIcon;
	}
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	public String getThumbnailIcon() {
		return thumbnailIcon;
	}
	public void setThumbnailIcon(String thumbnailIcon) {
		this.thumbnailIcon = thumbnailIcon;
	}
	public String getGoodsUrl() {
		return goodsUrl;
	}
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
    public Map<String, Object> getNperMap() {
        return nperMap;
    }
	public void setNperMap(Map<String, Object> nperMap) {
		this.nperMap = nperMap;
	}
	
	@Override
	public int compareTo(AfSearchGoodsVo o) {
		BigDecimal price1 = new BigDecimal(this.getRealAmount());
		BigDecimal price2 = new BigDecimal(o.getRealAmount());
		
		return price1.compareTo(price2);
	}

}
