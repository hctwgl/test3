package com.ald.fanbei.api.biz.bo;

import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;
public class BoluomeCouponResponseBo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5784104070750066975L;
	private String campaign_name;
	private int activity_coupon_id;
	/*	private int id;*/
	private String name;
	private int term_type;
	private int type;
	private int threshold;
	private float value;
	private int effective_day;
	private Long sts;
	private Long ets;
	private float discount;
	private List<String> channels;
	private String sceneId ;
	private String isHas;//用户是否拥有 Y, 代表拥有 N 代表没有拥有
	
	public String getSceneId() {
		return sceneId;
	}
	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}
	public String getCampaign_name() {
		return campaign_name;
	}
	public void setCampaign_name(String campaign_name) {
		this.campaign_name = campaign_name;
	}
	public int getActivity_coupon_id() {
		return activity_coupon_id;
	}
	public List<String> getChannels() {
		return channels;
	}
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
	public void setActivity_coupon_id(int activity_coupon_id) {
		this.activity_coupon_id = activity_coupon_id;
	}
	/*public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTerm_type() {
		return term_type;
	}
	public void setTerm_type(int term_type) {
		this.term_type = term_type;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	/**
	 * @return
	 */
	public int getEffective_day() {
		return effective_day;
	}
	public Long getSts() {
	    return sts;
	}
	public void setSts(Long sts) {
	    this.sts = sts;
	}
	public Long getEts() {
	    return ets;
	}
	public void setEts(Long ets) {
	    this.ets = ets;
	}
	public void setEffective_day(int effective_day) {
		this.effective_day = effective_day;
	}
	
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	/**
	 * @return the isHas
	 */
	public String getIsHas() {
		return isHas;
	}
	/**
	 * @param isHas the isHas to set
	 */
	public void setIsHas(String isHas) {
		this.isHas = isHas;
	}
	

}
