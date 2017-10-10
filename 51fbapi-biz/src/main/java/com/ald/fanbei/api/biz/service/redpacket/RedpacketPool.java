package com.ald.fanbei.api.biz.service.redpacket;

import java.util.concurrent.BlockingQueue;

import org.springframework.stereotype.Component;

@Component
interface RedpacketPool {
	
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
	public void InformationPacket();
	// TODO 
	
	static class Redpacket{
		
	}
	
}
