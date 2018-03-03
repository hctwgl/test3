package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.util.NumberWordFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfFundSideBorrowCashService;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfFundSideAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfFundSideBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfFundSideInfoDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountDo;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfFundSideBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfFundSideInfoDo;



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
    private AfFundSideAccountLogDao afFundSideAccountLogDao;
    @Resource
    private AfFundSideInfoDao afFundSideInfoDao;
    
    @Resource
    private TransactionTemplate transactionTemplate;
	@Resource
	NumberWordFormat numberWordFormat;
	@Override
	public BaseDao<AfFundSideBorrowCashDo, Long> getDao() {
		return afFundSideBorrowCashDao;
	}
	
	@Override
	public boolean matchFundAndBorrowCash(Long rid){
		final Long borrowCashId = rid;
		return transactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus transactionStatus) {
				AfBorrowCashDo borrowCashDo = afBorrowCashDao.getBorrowCashByrid(borrowCashId);
				if(borrowCashDo==null || borrowCashDo.getArrivalAmount().compareTo(BigDecimal.ZERO)<=0){
					logger.error("matchFundAndBorrowCash return false,borrowCashDao is null or amount is zero,borrowCashId:"+borrowCashId);
					return false;
				}
				
				//找出所有账户可用余额大于借款金额的用户，随机取一个并锁定
				AfFundSideAccountDo accounts = afFundSideAccountDao.getRandomOneAccountsByMinUsableMoney(borrowCashDo.getArrivalAmount());
				
				if(accounts==null ||  accounts.getUsableAmount().compareTo(borrowCashDo.getArrivalAmount()) <0){
					logger.error("matchFundAndBorrowCash return false,accounts is null or usableAmount is not enough,borrowCashId:"+borrowCashId+",accountsId:"+(accounts!=null?accounts.getRid():0));
					return false;
				}
				
				AfFundSideInfoDo fundSideInfoDo = afFundSideInfoDao.getById(accounts.getFundSideInfoId());
				if(fundSideInfoDo==null){
					logger.error("matchFundAndBorrowCash return false,fundSideInfoDo is null ,borrowCashId:"+borrowCashId+",accountsId:"+accounts.getRid()+",fundSideInfoId"+accounts.getFundSideInfoId());
					return false;
				}
				
				//当前日期和资金方可用余额
				Date currDay = new Date();
				BigDecimal usableMoney = accounts.getUsableAmount();
				
				//af_fund_side_borrow_cash关联记录插入
//				Integer borrowDays = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(borrowCashDo.getType()).getCode(), 0);
				Integer borrowDays = numberWordFormat.borrowTime(borrowCashDo.getType());
				BigDecimal planCollectInterest = NumberUtil.getSumInterestsByAmountAndRate(borrowCashDo.getArrivalAmount(), fundSideInfoDo.getAnnualRate(), borrowDays);
				
				//af_fund_side_account资金更新
				AfFundSideAccountDo afFundSideAccountDo = new AfFundSideAccountDo();
				afFundSideAccountDo.setRid(accounts.getRid());
				afFundSideAccountDo.setUsableAmount(borrowCashDo.getArrivalAmount().negate());
				afFundSideAccountDo.setCollectCapital(borrowCashDo.getArrivalAmount());
				afFundSideAccountDo.setCollectInterest(planCollectInterest);
				afFundSideAccountDo.setBorrowTotalAmount(borrowCashDo.getArrivalAmount());
				afFundSideAccountDo.setGmtModified(accounts.getGmtModified());
				int affectRows = afFundSideAccountDao.updateRecordInfo(afFundSideAccountDo);
				if(affectRows != 1){
					//影响行数不等于1,关联失败并返回
					logger.error("matchFundAndBorrowCash return false, updateAfFundSideAccount fail,borrowCashId:"+borrowCashId+",accountsId:"+accounts.getRid()+",fundSideInfoId"+accounts.getFundSideInfoId());
					return false;
				}
				//关联更新成功,插入关联记录
				AfFundSideBorrowCashDo fundSideBorrowCashDo = new AfFundSideBorrowCashDo(borrowCashId, borrowCashDo.getBorrowNo(), planCollectInterest, accounts.getFundSideInfoId(), borrowCashDo.getGmtPlanRepayment(), 
						borrowCashDo.getArrivalAmount().add(planCollectInterest), null, YesNoStatus.NO.getCode(), currDay, currDay, fundSideInfoDo.getAnnualRate());
				afFundSideBorrowCashDao.saveRecord(fundSideBorrowCashDo);
				//af_fund_side_account_log资金记录插入
				AfFundSideAccountLogDo afFundSideAccountLogDo = new AfFundSideAccountLogDo(accounts.getFundSideInfoId(), usableMoney, borrowCashDo.getArrivalAmount().negate(), AfFundSideAccountLogType.LOAN.getCode(), fundSideBorrowCashDo.getRid(), currDay,borrowCashDo.getBorrowNo(), "放款成功");
				afFundSideAccountLogDao.saveRecord(afFundSideAccountLogDo);
				
				return true;
			}
		});
	}

	@Override
	public AfFundSideInfoDo getLenderInfoByBorrowCashId(Long borrowId){
		AfFundSideBorrowCashDo fsCashDo = afFundSideBorrowCashDao.getRecordByBorrowCashId(borrowId);
		AfFundSideInfoDo afFundSideInfoDo = null;
		if(fsCashDo!=null){
			//取资金方信息
			afFundSideInfoDo =afFundSideInfoDao.getById(fsCashDo.getFundSideInfoId());
		}
		return afFundSideInfoDo;
	}


}