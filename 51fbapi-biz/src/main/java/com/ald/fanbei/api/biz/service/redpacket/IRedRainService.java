package com.ald.fanbei.api.biz.service.redpacket;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;

public interface IRedRainService {
	
	/**
	 * 申请获取一个红包
	 * @param userName
	 * @return
	 */
	AfCouponDo apply(String userName);
	
	/**
	 * 获取当日的红包雨场次信息
	 * @return
	 */
	List<AfRedRainRoundDo> fetchTodayRounds();
	
	/**
	 * 5分钟间隔扫描数据库，提前10分钟将开始的红包雨场次注入红包池
	 */
	void scanAndInjected();
	
	/**
	 * 清理红包池，并统计红包池状态信息
	 */
	void clearAndStatisic(Integer roundId);
	
}
