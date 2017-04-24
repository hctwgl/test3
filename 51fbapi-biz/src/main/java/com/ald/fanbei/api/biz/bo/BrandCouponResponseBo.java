package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月29日上午12:44:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BrandCouponResponseBo extends AbstractSerial{
	
	private static final long serialVersionUID = 6619444158028886799L;
	
	private String id;   //平台活动id
	private String title;//平台活动title
	private String subtitle;//平台活动subtitle
	private Float threshold;   //优惠券使用阈值, 期含义跟type 类型有关 type = 1,2是表示满足的金额, type = 3.4表示满足的数量
	private Float value;   //优惠券抵用的新近价值或折扣时可抵用的最大现金价值
	private Float discount ;   //折扣
	private String[] channels;//优惠券适用的服务
	private Integer mutex; //互斥
	private Integer type;//类型
	private Float sts;//开始时间
	private Float ets;//结束时间
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the subtitle
	 */
	public String getSubtitle() {
		return subtitle;
	}
	/**
	 * @param subtitle the subtitle to set
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
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
	 * @return the mutex
	 */
	public Integer getMutex() {
		return mutex;
	}
	/**
	 * @param mutex the mutex to set
	 */
	public void setMutex(Integer mutex) {
		this.mutex = mutex;
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
	
	
}
