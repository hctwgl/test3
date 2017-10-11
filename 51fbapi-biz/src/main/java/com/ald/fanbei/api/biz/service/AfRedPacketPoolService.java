package com.ald.fanbei.api.biz.service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;


public interface AfRedPacketPoolService {
	
	
	/**
	 * 注入红包
	 * @param 
	 */
	public void inject(BlockingQueue<Redpacket> packets);
	
	/**
	 * 申请一个红包
	 */
	public Redpacket apply();
	
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
		private String coupon_name;
		private Long coupon_id;
		private Integer redRainRoundId;

		public Redpacket(String type, String couponName, Long couponId, Integer redRainRoundId){
			this.type = type;
			this.coupon_name = couponName;
			this.coupon_id = couponId;
			this.redRainRoundId = redRainRoundId;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getCoupon_name() {
			return coupon_name;
		}

		public void setCoupon_name(String coupon_name) {
			this.coupon_name = coupon_name;
		}

		public Long getCoupon_id() {
			return coupon_id;
		}

		public void setCoupon_id(Long coupon_id) {
			this.coupon_id = coupon_id;
		}

		public Integer getRedRainRoundId() {
			return redRainRoundId;
		}

		public void setRedRainRoundId(Integer redRainRoundId) {
			this.redRainRoundId = redRainRoundId;
		}
	}
	
}
