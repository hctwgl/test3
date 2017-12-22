package com.ald.fanbei.api.web.vo;

import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfDouble12GoodsTimeTypeVo extends AbstractSerial{

	private static final long serialVersionUID = -7150400787252733902L;
	
	private String type;//秒杀状态，O：进行中；E：已结束；N：未开始
	private List<AfDouble12GoodsVo> goodsList;//商品map
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<AfDouble12GoodsVo> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<AfDouble12GoodsVo> goodsList) {
		this.goodsList = goodsList;
	}
	
	
}
