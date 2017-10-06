package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfFundSideBorrowCashService;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountDao;
import com.ald.fanbei.api.dal.dao.AfFundSideBorrowCashDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountDo;
import com.ald.fanbei.api.dal.domain.AfFundSideBorrowCashDo;



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
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
	@Override
	public BaseDao<AfFundSideBorrowCashDo, Long> getDao() {
		return afFundSideBorrowCashDao;
	}
	
	@Override
	public boolean matchFundAndBorrowCash(Long rid){
		final Long borrowCashId = rid;
		int resultValue = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus transactionStatus) {
				AfBorrowCashDo borrowCashDao = afBorrowCashDao.getBorrowCashByrid(borrowCashId);
				if(borrowCashDao==null || borrowCashDao.getAmount().compareTo(BigDecimal.ZERO)<=0){
					logger.error("matchFundAndBorrowCash return false,borrowCashDao is null or amount is zero,borrowCashId:"+borrowCashId);
					return 0;
				}
				
				//找出所有账户可用余额大于借款金额的用户，随机取一个并锁定
				AfFundSideAccountDo accounts = afFundSideAccountDao.getRandomOneAccountsByMinUsableMoney(borrowCashDao.getAmount());
				if(accounts==null && accounts.getUsableAmount().compareTo(borrowCashDao.getAmount()) <0){
					logger.error("matchFundAndBorrowCash return false,accounts is null or usableAmount is not enough,borrowCashId:"+borrowCashId+",accountsId:"+(accounts!=null?accounts.getRid():0));
					return 0;
				}
				
				//af_fund_side_borrow_cash关联记录插入
		
				
				//af_fund_side_account资金更新
				
				
				//af_fund_side_account_log资金记录插入
				
				
				return 1;
			}
		});
		
		if(resultValue ==1){
			return true;
		}else{
			return false;
		}
	}
}