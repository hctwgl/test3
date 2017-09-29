package com.ald.fanbei.api.biz.service.redpacket;

import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
class RedpacketPool {
	
	// TODO 池，可选方案A：BlockQueue，B：Redis List
	private ArrayBlockingQueue<Redpacket> cashCoupons;		//现金券
	private ArrayBlockingQueue<Redpacket> moneyOffCoupons;	//满减券
	// ...
	
	
	public int tmpSum; //临时的数据总数
	
	@PostConstruct
	public void init() {
		// TODO 启动定时扫描仪
	}
	
	/**
	 * 注入红包
	 */
	public void inject() {
		// TODO 扫描到合法场次则数据入池，并且添加定时器到点清理红包池
		
		// TODO 
	}
	
	/**
	 * 申请一个红包
	 */
	public Redpacket apply() {
		
		return null;
	}
	
	/**
	 * 清空红包池
	 */
	private void clear() {
		
	}
	
	static class Redpacket{
		
	}
	
}
