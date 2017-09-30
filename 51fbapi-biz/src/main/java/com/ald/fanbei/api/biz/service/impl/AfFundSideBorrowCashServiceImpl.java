package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFundSideBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountDo;
import com.ald.fanbei.api.dal.domain.AfFundSideBorrowCashDo;
import com.ald.fanbei.api.biz.service.AfFundSideAccountService;
import com.ald.fanbei.api.biz.service.AfFundSideBorrowCashService;



/**
 * 资金方放款与用户借款记录关联表ServiceImpl
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:55:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFundSideBorrowCashService")
public class AfFundSideBorrowCashServiceImpl extends ParentServiceImpl<AfFundSideBorrowCashDo, Long> implements AfFundSideBorrowCashService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFundSideBorrowCashServiceImpl.class);
   
    @Resource
    private AfFundSideBorrowCashDao afFundSideBorrowCashDao;
    @Resource
    private AfBorrowCashDao afBorrowCashDao;
    @Resource
    private AfFundSideAccountDao afFundSideAccountDao;
    
	@Override
	public BaseDao<AfFundSideBorrowCashDo, Long> getDao() {
		return afFundSideBorrowCashDao;
	}
	
	@Override
	public boolean matchFundAndBorrowCash(Long rid){
		AfBorrowCashDo borrowCashDao = afBorrowCashDao.getBorrowCashByrid(rid);
		if(borrowCashDao==null || borrowCashDao.getAmount().compareTo(BigDecimal.ZERO)<=0){
			logger.info("matchFundAndBorrowCash return false,borrowCashDao is null or amount is zero,borrowCashId:"+rid);
			return false;
		}
		
		//找出所有账户可用余额大于借款金额的用户，随机取一个并锁定
		List<AfFundSideAccountDo> accounts = afFundSideAccountDao.getAccountsByMinUsableMoney(borrowCashDao.getAmount());
		
		//如果没有则不匹配直接返回
		
		return true;
	}
}