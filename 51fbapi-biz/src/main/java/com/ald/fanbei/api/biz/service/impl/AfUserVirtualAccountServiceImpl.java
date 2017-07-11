package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfUserVirtualAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;



/**
 * '虚拟商品额度记录ServiceImpl
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-07-08 14:16:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserVirtualAccountService")
public class AfUserVirtualAccountServiceImpl extends ParentServiceImpl<AfUserVirtualAccountDo, Long> implements AfUserVirtualAccountService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserVirtualAccountServiceImpl.class);
   
    @Resource
    private AfUserVirtualAccountDao afUserVirtualAccountDao;

	@Override
	public BaseDao<AfUserVirtualAccountDo, Long> getDao() {
		return afUserVirtualAccountDao;
	}

	@Override
	public BigDecimal getCurrentMonthUsedAmount(Long userId, String virtualCode) {
		return afUserVirtualAccountDao.getCurrentMonthUsedAmount(DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), userId, virtualCode);
	}

	@Override
	public BigDecimal getCurrentMonthLeftAmount(Long userId, String virtualCode, BigDecimal virtualTotalAmount) {
		//已经使用额度
		BigDecimal usedAmount = getCurrentMonthUsedAmount(userId, virtualCode);
		//剩余额度
		BigDecimal leftAmount = BigDecimalUtil.subtract(virtualTotalAmount, usedAmount);
		return usedAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : leftAmount;
	}
}