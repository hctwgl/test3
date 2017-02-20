package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月20日上午10:02:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfConsumeConfirmVo extends AbstractSerial{

	private static final long serialVersionUID = 8656064187748575028L;
	private List<Map<String,Object>> timeLimitList;
	private Long goodsId;
	private String	openId;
	private String 	goodsName;
	private String 	goodsIcon;
	private BigDecimal 	saleAmount;
	private String 	bankName;
	private String 	cardNo;
	private Long 	cardId;
	
	public List<Map<String, Object>> getTimeLimitList() {
		return timeLimitList;
	}
	public void setTimeLimitList(List<Map<String, Object>> timeLimitList) {
		this.timeLimitList = timeLimitList;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
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
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

}
