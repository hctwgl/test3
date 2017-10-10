package com.ald.fanbei.api.biz.service.redpacket;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;


public interface RedPacketPoolService {
	
	// TODO 池，可选方案A：BlockQueue，B：Redis List
	// private ArrayBlockingQueue<Redpacket> cashCoupons;		//现金券
	// private ArrayBlockingQueue<Redpacket> moneyOffCoupons;	//满减券
	// ...
	
	
	// TODO 红包池状态信息字段
	// ...
	
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
	public void EmptyPacket();
	// TODO 
	
	/**
	 * 红包池状态信息
	 */
	public Map<String,Integer> InformationPacket();
	// TODO 
	
	static class Redpacket{
		private String type;
		private String coupon_name;
		private Long coupon_id;

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
	}
	
}
