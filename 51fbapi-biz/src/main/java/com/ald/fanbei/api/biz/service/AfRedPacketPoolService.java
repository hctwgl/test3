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
		private String couponName;
		private Long couponId;
		private Integer redRainRoundId;

		public Redpacket(String type, String couponName, Long couponId, Integer redRainRoundId){
			this.type = type;
			this.couponName = couponName;
			this.couponId = couponId;
			this.redRainRoundId = redRainRoundId;
		}
		
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
	}
	
}
