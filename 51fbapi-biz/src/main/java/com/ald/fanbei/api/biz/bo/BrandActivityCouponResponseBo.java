package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：根据活动取优惠券相关信息
 * @author xiaotianjian 2017年8月30日下午11:24:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BrandActivityCouponResponseBo extends AbstractSerial{
	
	private static final long serialVersionUID = 1128605059994563358L;
	
	private String campaign_name;   //红包活动名称
	private Long activity_coupon_id;//优惠券类别唯一标识
	private String[] channels;//优惠券可使用的服务品类
	private Long total;//优惠券总数
	private Long distributed;//优惠券已领取数
	private String name;//优惠券名称
	private Integer term_type;//1-有效时间 2-有效日期
	private Integer type;//类型1-减免 2-折扣
	private Float threshold;   //优惠券使用阈值, 期含义跟type 类型有关 type = 1,2是表示满足的金额, type = 3.4表示满足的数量
	private Float value;   //优惠券抵用的新近价值或折扣时可抵用的最大现金价值 type = 1 时为减多少元, type = 2 时为上限
	private Integer effective_day;//有效时间，单位（天）
	private Float sts;//开始时间
	private Float ets;//结束时间
	private Float discount ;//折扣
	
	/**
	 * @return the campaign_name
	 */
	public String getCampaign_name() {
		return campaign_name;
	}
	/**
	 * @param campaign_name the campaign_name to set
	 */
	public void setCampaign_name(String campaign_name) {
		this.campaign_name = campaign_name;
	}
	/**
	 * @return the activity_coupon_id
	 */
	public Long getActivity_coupon_id() {
		return activity_coupon_id;
	}
	/**
	 * @param activity_coupon_id the activity_coupon_id to set
	 */
	public void setActivity_coupon_id(Long activity_coupon_id) {
		this.activity_coupon_id = activity_coupon_id;
	}
	/**
	 * @return the channels
	 */
	public String[] getChannels() {
		return channels;
	}
	/**
	 * @param channels the channels to set
	 */
	public void setChannels(String[] channels) {
		this.channels = channels;
	}
	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}
	/**
	 * @return the distributed
	 */
	public Long getDistributed() {
		return distributed;
	}
	/**
	 * @param distributed the distributed to set
	 */
	public void setDistributed(Long distributed) {
		this.distributed = distributed;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the term_type
	 */
	public Integer getTerm_type() {
		return term_type;
	}
	/**
	 * @param term_type the term_type to set
	 */
	public void setTerm_type(Integer term_type) {
		this.term_type = term_type;
	}
	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * @return the threshold
	 */
	public Float getThreshold() {
		return threshold;
	}
	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Float threshold) {
		this.threshold = threshold;
	}
	/**
	 * @return the value
	 */
	public Float getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Float value) {
		this.value = value;
	}
	/**
	 * @return the effective_day
	 */
	public Integer getEffective_day() {
		return effective_day;
	}
	/**
	 * @param effective_day the effective_day to set
	 */
	public void setEffective_day(Integer effective_day) {
		this.effective_day = effective_day;
	}
	/**
	 * @return the sts
	 */
	public Float getSts() {
		return sts;
	}
	/**
	 * @param sts the sts to set
	 */
	public void setSts(Float sts) {
		this.sts = sts;
	}
	/**
	 * @return the ets
	 */
	public Float getEts() {
		return ets;
	}
	/**
	 * @param ets the ets to set
	 */
	public void setEts(Float ets) {
		this.ets = ets;
	}
	/**
	 * @return the discount
	 */
	public Float getDiscount() {
		return discount;
	}
	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(Float discount) {
		this.discount = discount;
	}
	
	
	
}
