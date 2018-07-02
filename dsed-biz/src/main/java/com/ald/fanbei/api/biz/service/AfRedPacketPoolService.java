package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;


public interface AfRedPacketPoolService {
	
	
	/**
	 * 注入红包
	 * @param 
	 */
	public void inject(Collection<String> packets);
	
	/**
	 * 申请一个红包
	 */
	public String apply();
	
	/**
	 * 清空红包池
	 */
	public void emptyPacket();
	
	/**
	 * 红包池状态信息
	 */
	public Map<String,Integer> informationPacket();
	
	static class Redpacket{
		private String type;
		private String couponName;
		private Long couponId;
		private Integer redRainRoundId;
		private Integer amount;
		private String couponEffectiveTime;
		private BigDecimal couponLimitAmount;

		public Redpacket(String type, String couponName, Long couponId, Integer redRainRoundId, Integer amount, String couponEffectiveTime, BigDecimal couponLimitAmount){
			this.type = type;
			this.couponName = couponName;
			this.couponId = couponId;
			this.redRainRoundId = redRainRoundId;
			this.amount = amount;
			this.couponEffectiveTime = couponEffectiveTime;
			this.couponLimitAmount = couponLimitAmount;
		}
		
		public Redpacket() {}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getCouponName() {
			return couponName;
		}

		public void setCouponName(String couponName) {
			this.couponName = couponName;
		}

		public Long getCouponId() {
			return couponId;
		}

		public void setCouponId(Long couponId) {
			this.couponId = couponId;
		}

		public Integer getRedRainRoundId() {
			return redRainRoundId;
		}

		public void setRedRainRoundId(Integer redRainRoundId) {
			this.redRainRoundId = redRainRoundId;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

		public String getCouponEffectiveTime() {
			return couponEffectiveTime;
		}

		public void setCouponEffectiveTime(String couponEffectiveTime) {
			this.couponEffectiveTime = couponEffectiveTime;
		}

		public BigDecimal getCouponLimitAmount() {
			return couponLimitAmount;
		}

		public void setCouponLimitAmount(BigDecimal couponLimitAmount) {
			this.couponLimitAmount = couponLimitAmount;
		}
	}
	
}
