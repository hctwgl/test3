package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;

/**
 * '虚拟商品额度记录Service
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-07-08 14:16:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserVirtualAccountService extends ParentService<AfUserVirtualAccountDo, Long>{

	/**
	 * 获取用户当前月份已经使用额度
	 * @param userId
	 * @param virtualCode
	 * @return
	 */
	BigDecimal getCurrentMonthUsedAmount(Long userId, String virtualCode, Integer virtualRecentDay);
	
	/**
	 *  获取用户当前月份剩余可使用额度
	 * @param userId
	 * @param virtualCode
	 * @param virtualTotalAmount 当月总额度
	 * @return
	 */
	BigDecimal getCurrentMonthLeftAmount(Long userId, String virtualCode, BigDecimal virtualTotalAmount, Integer virtualRecentDay);
	
	/**
	 *  获取场景当日已使用额度
	 * @param virtualCode
	 * @return
	 */
	BigDecimal getCurrentDayUsedAmount(String virtualCode);
	
	
}
