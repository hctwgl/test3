package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetPlatUserInfoRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfViewAssetBorrowCashService;
import com.ald.fanbei.api.biz.service.AfViewAssetBorrowService;
import com.ald.fanbei.api.biz.service.AfViewAssetLoanService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfAssetOperaLogChangeType;
import com.ald.fanbei.api.common.enums.AfAssetPackageBusiType;
import com.ald.fanbei.api.common.enums.AfAssetPackageDetailStatus;
import com.ald.fanbei.api.common.enums.AfAssetPackageRepaymentType;
import com.ald.fanbei.api.common.enums.AfAssetPackageSendMode;
import com.ald.fanbei.api.common.enums.AfAssetPackageStatus;
import com.ald.fanbei.api.common.enums.AfAssetPackageType;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDetailDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideOperaLogDo;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowDo;
import com.ald.fanbei.api.dal.domain.AfViewAssetLoanDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowCashQuery;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowQuery;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetLoanQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;



/**
 * 资产包与债权记录关系ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
/**
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月14日下午5:57:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */


@Service("afAssetPackageDetailService")
public class AfAssetPackageDetailServiceImpl extends ParentServiceImpl<AfAssetPackageDetailDo, Long> implements AfAssetPackageDetailService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAssetPackageDetailServiceImpl.class);
   
    @Resource
    private AfAssetPackageDetailDao afAssetPackageDetailDao;
    @Resource
    private AfAssetSideOperaLogDao afAssetSideOperaLogDao;
    @Resource
    private BizCacheUtil  bizCacheUtil;
    @Resource
    private AfAssetPackageDao  afAssetPackageDao;
    @Resource
    private AfBorrowCashDao  afBorrowCashDao;
	@Resource
	private AfViewAssetBorrowCashService  afViewAssetBorrowCashService;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	private AfViewAssetBorrowService afViewAssetBorrowService;
	@Resource
	private AfBorrowBillService afBorrowBillService;
	@Resource
    private AfBorrowDao  afBorrowDao;
	@Resource
	private AfResourceService  afResourceService;
	@Resource
	private AfViewAssetLoanService  afViewAssetLoanService;
	@Resource
	private AfLoanPeriodsService  afLoanPeriodsService;
	@Resource
	private AfLoanDao  afLoanDao;
	
	@Override
	public BaseDao<AfAssetPackageDetailDo, Long> getDao() {
		return afAssetPackageDetailDao;
	}
	
	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 */
	@Override
	public int batchGiveBackCreditInfo(final AfAssetSideInfoDo afAssetSideInfoDo,final List<String> orderNos,final Integer debtType){
		//更新成功的信息
		List<String> successPackageIds = new ArrayList<String>();
		List<String> successPackageDetailIds = new ArrayList<String>();
		//更新失败的信息
		List<String> failPackageIds = new ArrayList<String>();
		List<String> failPackageDetailIds = new ArrayList<String>();
		//不存在的记录
		List<String> invalidBorrowNos = new ArrayList<String>();
		//成功的总金额
		final List<BigDecimal> totalMoneyList = new ArrayList<BigDecimal>();
		//校验
		final Date currDate = new Date();
		for (String tempBorrowNo : orderNos) {
			final String borrowNo = tempBorrowNo;
			final AfAssetPackageDetailDo afAssetPackageDetail = afAssetPackageDetailDao.getByBorrowNo(borrowNo);
    		if(afAssetPackageDetail==null){
    			logger.error("batchGiveBackCreditInfo error ,orderNo not exists,borrowNo="+borrowNo);
    			invalidBorrowNos.add(borrowNo);
    			continue;
    		}
    		final AfAssetPackageDo packageDo = afAssetPackageDao.getById(afAssetPackageDetail.getAssetPackageId());
    		if(packageDo==null){
    			logger.error("batchGiveBackCreditInfo error ,packageDo not exists,id="+afAssetPackageDetail.getAssetPackageId());
    			invalidBorrowNos.add(borrowNo);
    			continue;
    		}
			//begin事务,记录变更
			int result = transactionTemplate.execute(new TransactionCallback<Integer>() {
		        @Override
	            public Integer doInTransaction(TransactionStatus status) {
		        	try {
		        		if(!AfAssetPackageDetailStatus.VALID.getCode().equals(afAssetPackageDetail.getStatus())){
		        			logger.error("batchGiveBackCreditInfo error ,afAssetPackageDetail is invalid,borrowNo="+borrowNo);
		        			return 0;
		        		}
		        		BigDecimal cancelMoney = BigDecimal.ZERO;
		        		//区分现金贷和消费分期
		        		if (debtType == 1) {
							//消费分期
		        			AfBorrowDo BorrowDo = afBorrowDao.getBorrowById(afAssetPackageDetail.getBorrowCashId());
		        			if(BorrowDo==null){
		        				logger.error("batchGiveBackCreditInfo error ,borrow not exists,id="+afAssetPackageDetail.getBorrowCashId());
		        				return 0;
		        			}
		        			cancelMoney=BorrowDo.getAmount();
						}else if(debtType == 0){
							//现金贷
							AfBorrowCashDo borrowCashDo = afBorrowCashDao.getBorrowCashByrid(afAssetPackageDetail.getBorrowCashId());
							if(borrowCashDo==null){
								logger.error("batchGiveBackCreditInfo error ,borrowCash not exists,id="+afAssetPackageDetail.getBorrowCashId());
								return 0;
							}
							cancelMoney=borrowCashDo.getAmount();
						}else{
							//白领贷
							AfLoanDo loanDo = afLoanDao.getLoanById(afAssetPackageDetail.getBorrowCashId());
							if(loanDo == null){
								logger.error("batchGiveBackCreditInfo error ,loan not exists,id="+afAssetPackageDetail.getBorrowCashId());
								return 0;
							}
							cancelMoney=loanDo.getAmount();
						}
			    		
			    		//更新此债权相关明细
			    		int effectNums = afAssetPackageDetailDao.invalidPackageDetail(afAssetPackageDetail.getBorrowNo());
			    		
			    		if(effectNums>0){
			    			totalMoneyList.add(cancelMoney);
			    			AfAssetPackageDo modifyPackageDo = new AfAssetPackageDo();
			    			modifyPackageDo.setRid(packageDo.getRid());
			    			modifyPackageDo.setRealTotalMoney(cancelMoney.negate());
			    			modifyPackageDo.setGmtModified(currDate);
			    			afAssetPackageDao.updateRealTotalMoneyById(modifyPackageDo);
			    			return 1;
			    		}else{
			    			logger.error("batchGiveBackCreditInfo update AfAssetPackageDetailDo fail ,id="+afAssetPackageDetail.getBorrowCashId());
			    			return 0;
			    		}
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.error("batchGiveBackCreditInfo exe exception ,borrowNo="+borrowNo,e);
						return 0;
					}
		        }
			});
			if(result==1){
				successPackageIds.add(""+packageDo.getRid());
				successPackageDetailIds.add(""+afAssetPackageDetail.getRid());
			}else{
				failPackageIds.add(""+packageDo.getRid());
				failPackageDetailIds.add(""+afAssetPackageDetail.getRid());
			}
		}
		BigDecimal totalMoney = BigDecimal.ZERO;
		for (BigDecimal tempMoney : totalMoneyList) {
			totalMoney = totalMoney.add(tempMoney);
		}
		String refPackageId = StringUtil.joinListToString(successPackageIds, ",")+";"+StringUtil.joinListToString(failPackageIds, ",");
		String refDetailIds = StringUtil.joinListToString(successPackageDetailIds, ",")+";"+StringUtil.joinListToString(failPackageDetailIds, ",")+";"+StringUtil.joinListToString(invalidBorrowNos, ",");
		//资产方操作日志添加
		if(totalMoney.compareTo(BigDecimal.ZERO)>0){
			AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), totalMoney, refPackageId,refDetailIds, "成功退回债权金额："+totalMoney+"元");
			afAssetSideOperaLogDao.saveRecord(operaLogDo);
		}else{
			logger.error("batchGiveBackCreditInfo affect money is 0 ,refPackageId="+refPackageId+",refDetailIds="+refDetailIds);
			return 0;
		}
		
		//end事务
		if(failPackageIds!=null && failPackageIds.size()>0){
			return 0;
		}else{
			return 1;
		}
	}

	@Override
	public int addPackageDetailLoanTime(List<String> orderNos, Date loanTime, Integer debtType) {
		
		for (String tempBorrowNo : orderNos) {
			final String borrowNo = tempBorrowNo;
			final AfAssetPackageDetailDo afAssetPackageDetail = afAssetPackageDetailDao.getByBorrowNo(borrowNo);
			if (afAssetPackageDetail == null) {
				logger.error("batchGiveBackCreditInfo error ,orderNo not exists,borrowNo=" + borrowNo);
				continue;
			}
			final AfAssetPackageDo packageDo = afAssetPackageDao.getById(afAssetPackageDetail.getAssetPackageId());
			if (packageDo == null) {
				logger.error("batchGiveBackCreditInfo error ,packageDo not exists,id=" + afAssetPackageDetail.getAssetPackageId());
				continue;
			}

			try {
				if (!AfAssetPackageDetailStatus.VALID.getCode().equals(afAssetPackageDetail.getStatus())) {
					logger.error("batchGiveBackCreditInfo error ,afAssetPackageDetail is invalid,borrowNo=" + borrowNo);
					return 0;
				}

				if (debtType == 1) {
					//消费分期
					AfBorrowDo BorrowDo = afBorrowDao.getBorrowById(afAssetPackageDetail.getBorrowCashId());
					if (BorrowDo == null) {
						logger.error("batchGiveBackCreditInfo error ,borrow not exists,id=" + afAssetPackageDetail.getBorrowCashId());
						return 0;
					}
					
				} else if (debtType == 0) {
					//现金贷
					AfBorrowCashDo borrowCashDo = afBorrowCashDao.getBorrowCashByrid(afAssetPackageDetail.getBorrowCashId());
					if (borrowCashDo == null) {
						logger.error("batchGiveBackCreditInfo error ,borrowCash not exists,id=" + afAssetPackageDetail.getBorrowCashId());
						return 0;
					}

				} else {
					//白领贷
					AfLoanDo loanDo = afLoanDao.getLoanById(afAssetPackageDetail.getBorrowCashId());
					if (loanDo == null) {
						logger.error("batchGiveBackCreditInfo error ,loan not exists,id=" + afAssetPackageDetail.getBorrowCashId());
						return 0;
					}

				}
				
				//记录放款时间
				AfAssetPackageDetailDo afAssetPackageDetailTemp = new AfAssetPackageDetailDo();
				afAssetPackageDetailTemp.setRid(afAssetPackageDetail.getRid());
				afAssetPackageDetailTemp.setLoanTime(loanTime);
				afAssetPackageDetailDao.updateById(afAssetPackageDetailTemp);
			} catch (Exception e) {
				logger.error("batchGiveBackCreditInfo exe exception ,borrowNo=" + borrowNo, e);
				return 0;
			}
		}

		return 1;
	}

	/**
	 * 单个债权包明细撤回操作
	 * @param orderNo
	 */
	@Override
	public int giveBackCreditInfo(final AfAssetSideInfoDo afAssetSideInfoDo,final String borrowNo){
		//校验,临时调试阶段,简单处理,按单笔处理
		final AfAssetPackageDetailDo afAssetPackageDetail = afAssetPackageDetailDao.getByBorrowNo(borrowNo);
		if(afAssetPackageDetail==null){
			logger.error("giveBackCreditInfo error ,orderNo not exists,borrowNo="+borrowNo);
			return 0;
		}
		if(!AfAssetPackageDetailStatus.VALID.getCode().equals(afAssetPackageDetail.getStatus())){
			logger.error("giveBackCreditInfo error ,afAssetPackageDetail is invalid,borrowNo="+borrowNo);
			return 0;
		}
		
		final AfAssetPackageDo packageDo = afAssetPackageDao.getById(afAssetPackageDetail.getAssetPackageId());
		if(packageDo==null){
			logger.error("giveBackCreditInfo error ,packageDo not exists,id="+afAssetPackageDetail.getAssetPackageId());
			return 0;
		}
		//获取borrowCash
		final AfBorrowCashDo borrowCash = afBorrowCashDao.getBorrowCashByrid(afAssetPackageDetail.getBorrowCashId());
		if(borrowCash==null){
			logger.error("giveBackCreditInfo error ,borrowCash not exists,id="+afAssetPackageDetail.getBorrowCashId());
			return 0;
		}
		//begin事务,记录变更
		int result = transactionTemplate.execute(new TransactionCallback<Integer>() {
	        @Override
            public Integer doInTransaction(TransactionStatus status) {
	        	try {
	        		Date currDate = new Date();
		        	//更新此债权相关明细
		    		int effectNums = afAssetPackageDetailDao.invalidPackageDetail(afAssetPackageDetail.getBorrowNo());
		    		
		    		if(effectNums>0){
		    			AfAssetPackageDo modifyPackageDo = new AfAssetPackageDo();
		    			modifyPackageDo.setRid(packageDo.getRid());
		    			modifyPackageDo.setGmtModified(currDate);
		    			modifyPackageDo.setRealTotalMoney(borrowCash.getAmount().negate());
		    			afAssetPackageDao.updateRealTotalMoneyById(modifyPackageDo);
		    			//资产方操作日志添加
		    			AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), borrowCash.getAmount(), packageDo.getRid()+";",afAssetPackageDetail.getRid()+";", "成功退回债权金额："+borrowCash.getAmount()+"元");
		    			afAssetSideOperaLogDao.saveRecord(operaLogDo);
		    			return 1;
		    		}else{
		    			logger.error("giveBackCreditInfo update AfAssetPackageDetailDo fail ,borrowNo="+borrowNo+",id="+afAssetPackageDetail.getBorrowCashId());
		    			return 0;
		    		}
	        	} catch (Exception e) {
					status.setRollbackOnly();
					logger.error("giveBackCreditInfo exe exception ,borrowNo="+borrowNo,e);
					return 0;
				}
	        }
		});
		//end事务
		return result;
	}
	
	/**
	 * 根据资产方要求,获取资产方对应的现金贷债权信息
	 */
	@Override
	public List<EdspayGetCreditRespBo> getBorrowCashBatchCreditInfo(final FanbeiBorrowBankInfoBo bankInfo,final AfAssetSideInfoDo afAssetSideInfoDo,final BigDecimal totalMoney,final Date gmtCreateStart,final Date gmtCreateEnd,final BigDecimal minMoney){
		final List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		Long result = transactionTemplate.execute(new TransactionCallback<Long>() {
	        @Override
            public Long doInTransaction(TransactionStatus status) {
            	BigDecimal minMoneyNew = minMoney;
            	List<AfViewAssetBorrowCashDo> minDebtList= new ArrayList<AfViewAssetBorrowCashDo>();
       		    List<AfViewAssetBorrowCashDo> maxDebtList=new ArrayList<AfViewAssetBorrowCashDo>();
       		    //根据resource表灵活配置的天数，目前为10/20天
				AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
				if (null == afResourceDo) {
					return 0L;
				}
				String minBorrowTime = afResourceDo.getTypeDesc().split(",")[0];
				String maxBorrowTime = afResourceDo.getTypeDesc().split(",")[1];
            	try {
        			//加锁Lock
        			boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(Constants.CACHEKEY_ASSETPACKAGE_LOCK, Constants.CACHEKEY_ASSETPACKAGE_LOCK_VALUE,10, Constants.SECOND_OF_TEN_MINITS);
        			//校验现在金额是否满足
        			if (isLock) {
        				//分配债权资产包
        				BigDecimal maxMoney = BigDecimal.ZERO;
        				if (minMoneyNew==null){
        					minMoneyNew=BigDecimalUtil.divide(totalMoney, new BigDecimal(2));
        				}
    					//区分借钱天数校验
    					BigDecimal sumMinAmount = afViewAssetBorrowCashService.getSumMinAmount(gmtCreateStart,gmtCreateEnd,minBorrowTime);
    					if (minMoneyNew.compareTo(sumMinAmount) > 0) {
    						logger.error("getBatchCreditInfo  error该时间段内"+minBorrowTime+"天资产金额不足，共"+sumMinAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
    						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
    						return 0L;
    					}
    					BigDecimal sumMaxAmount = afViewAssetBorrowCashService.getSumMaxAmount(gmtCreateStart,gmtCreateEnd,maxBorrowTime);
    					maxMoney = BigDecimalUtil.subtract(totalMoney,minMoneyNew);
    					if (maxMoney.compareTo(sumMaxAmount) > 0){
    						logger.error("getBatchCreditInfo  error该时间段内"+maxBorrowTime+"天资产金额不足，共"+sumMaxAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
    						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
    						return 0L;
    					}
    					minDebtList = matchingBorrowCashDebt(minMoneyNew,gmtCreateStart, gmtCreateEnd,minBorrowTime);
    					maxDebtList = matchingBorrowCashDebt(maxMoney,gmtCreateStart, gmtCreateEnd,maxBorrowTime);
        				
        				//生成资产包
        				Date currDate = new Date();
        				AfAssetPackageDo afAssetPackageDo = new AfAssetPackageDo();
        				afAssetPackageDo.setGmtCreate(currDate);
        				afAssetPackageDo.setGmtModified(currDate);
        				afAssetPackageDo.setStatus(AfAssetPackageStatus.SENDED.getCode());
        				String packageName = "";
        				if(totalMoney.intValue()/10000>0){
        					packageName = afAssetSideInfoDo.getName()+totalMoney.intValue()/10000+"万现金贷资产包"+DateUtil.formatDate(new Date());
        				}else{
        					packageName = afAssetSideInfoDo.getName()+totalMoney+"元现金贷资产包"+DateUtil.formatDate(new Date());
        				}
        				afAssetPackageDo.setAssetName(packageName);
        				afAssetPackageDo.setAssetNo("zcb"+System.currentTimeMillis());
        				afAssetPackageDo.setAssetSideId(afAssetSideInfoDo.getRid());
        				afAssetPackageDo.setBeginTime(gmtCreateStart);
        				afAssetPackageDo.setEndTime(gmtCreateEnd);
        				afAssetPackageDo.setTotalMoney(totalMoney);
        				afAssetPackageDo.setMinMoney(minMoneyNew);
        				afAssetPackageDo.setMinNum(minDebtList.size());
        				afAssetPackageDo.setMaxMoney(maxMoney);
        				afAssetPackageDo.setMaxNum(maxDebtList.size());
        				afAssetPackageDo.setBorrowRate(afAssetSideInfoDo.getBorrowRate());
        				afAssetPackageDo.setAnnualRate(afAssetSideInfoDo.getAnnualRate());
        				afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepaytType());
        				afAssetPackageDo.setValidStatus(YesNoStatus.YES.getCode());
        				afAssetPackageDo.setSendMode(AfAssetPackageSendMode.INTERFACE.getCode());
        				afAssetPackageDo.setType(AfAssetPackageType.ASSET_REQ.getCode());
        				afAssetPackageDo.setBusiType(AfAssetPackageBusiType.BORROWCASH.getCode());
        				afAssetPackageDao.saveRecord(afAssetPackageDo);
        				
        				BigDecimal realMinAmount = BigDecimal.ZERO;
        				BigDecimal realMaxAmount = BigDecimal.ZERO;
        				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : minDebtList) {
        					realMinAmount = realMinAmount.add(afViewAssetBorrowCashDo.getAmount());
        					EdspayGetCreditRespBo edspayGetCreditRespBo = buildCreditBorrowCashRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo,minBorrowTime,maxBorrowTime);
        					creditInfos.add(edspayGetCreditRespBo);
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(currDate);
        					afAssetPackageDetailDo.setGmtModified(currDate);
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
        					afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowNo());
        				}
        				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : maxDebtList) {
        					realMaxAmount = realMaxAmount.add(afViewAssetBorrowCashDo.getAmount());
        					EdspayGetCreditRespBo edspayGetCreditRespBo=buildCreditBorrowCashRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo,minBorrowTime,maxBorrowTime);
        					creditInfos.add(edspayGetCreditRespBo);
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(new Date());
        					afAssetPackageDetailDo.setGmtModified(new Date());
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
        					afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowNo());
        				}
        				
        				//更新实际金额
        				afAssetPackageDo.setRealTotalMoney(realMinAmount.add(realMaxAmount));
        				afAssetPackageDao.updateById(afAssetPackageDo);
        				
        				//资产方操作日志添加
        				AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GET_ASSET.getCode(), afAssetPackageDo.getRealTotalMoney(), afAssetPackageDo.getRid()+"","", "请求现金贷资产包金额totalMoney="+totalMoney+",实际"+minBorrowTime+"天："+realMinAmount+","+maxBorrowTime+"天"+realMaxAmount);
        				afAssetSideOperaLogDao.saveRecord(operaLogDo);
        				
        				bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        				return 1l;
        			}else{
        				 logger.error("getBatchCreditInfo  error获取锁失败"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
        			}
        		} catch (Exception e) {
        			status.setRollbackOnly();
        			logger.error("getBatchCreditInfo exception"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid(),e);
        			bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        		}
            	return 0l;
            }
	    });
		//释放锁Lock完成,结束
		if (result == 1) {
			return creditInfos;
		}else{
			return new ArrayList<EdspayGetCreditRespBo>();
		}
	}
	
	/**
	 * 匹配现金贷债权
	 * @param amount
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @param type
	 * @return
	 */
	private List<AfViewAssetBorrowCashDo> matchingBorrowCashDebt(BigDecimal amount, Date gmtCreateStart,Date gmtCreateEnd,String type) {
		//匹配初始记录;
		Integer limitNums = amount.intValue()/Constants.AVG_BORROWCASH_AMOUNT;
		AfViewAssetBorrowCashQuery query = new AfViewAssetBorrowCashQuery();
		query.setGmtCreateStart(gmtCreateStart);
		query.setGmtCreateEnd(gmtCreateEnd);
		query.setType(type);
		query.setLimitNums(limitNums == 0 ? 1 : limitNums);
		AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
		if (pushWhiteResource != null) {
			String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
			Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
			//推送白名单开启,白名单的userid不推送，仅用于实时推送
			query.setUserIds(Arrays.asList(whiteUserIds));
			/*for (int i = 0; i < debtList.size(); i++) {
				if (Arrays.asList(whiteUserId).contains(debtList.get(i).getUserId())) {
					debtList.remove(i);
				}
			}*/
		}
		List<AfViewAssetBorrowCashDo> debtList = afViewAssetBorrowCashService.getListByQueryCondition(query);
		if(debtList==null || debtList.size()==0){
			return new ArrayList<AfViewAssetBorrowCashDo>();
		}
		
		query.setMinBorrowCashId(debtList.get(debtList.size()-1).getBorrowCashId());
		BigDecimal checkAmount=afViewAssetBorrowCashService.checkAmount(query);
		if (checkAmount.compareTo(amount) < 0) {
			//初始金额不足，逐个债权补充记录直到刚好满足
			while (checkAmount.compareTo(amount) < 0) {
				AfViewAssetBorrowCashDo afViewAssetBorrowCashDo = afViewAssetBorrowCashService.getByQueryCondition(query);
				debtList.add(afViewAssetBorrowCashDo);
				query.setMinBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
				checkAmount = afViewAssetBorrowCashService.checkAmount(query);
			}
		}else{
			//非查库方式进行
			for(int i=debtList.size()-1;i>0 && checkAmount.compareTo(amount)>0;i--){
				if(checkAmount.subtract(debtList.get(i).getAmount()).compareTo(amount)>=0){
					checkAmount = checkAmount.subtract(debtList.get(i).getAmount());
					debtList.remove(i);
				}else{
					break;
				}
			}
		}
		return debtList;
	}
	
	/**
	 * 匹配消费分期债权
	 * @param totalMoney
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @return
	 */
	private List<AfViewAssetBorrowDo> matchingBorrowDebt(BigDecimal totalMoney, Date gmtCreateStart, Date gmtCreateEnd) {
		//匹配初始记录;
		Integer limitNums = totalMoney.intValue()/Constants.AVG_BORROW_AMOUNT;
		AfViewAssetBorrowQuery query = new AfViewAssetBorrowQuery();
		query.setGmtCreateStart(gmtCreateStart);
		query.setGmtCreateEnd(gmtCreateEnd);
		query.setLimitNums(limitNums == 0 ? 1 : limitNums);
		AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
		if (pushWhiteResource != null) {
			String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
			Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
			//推送白名单开启,白名单的userid不推送，仅用于实时推送
			query.setUserIds(Arrays.asList(whiteUserIds));
			/*for (int i = 0; i < debtList.size(); i++) {
				if (Arrays.asList(whiteUserId).contains(debtList.get(i).getUserId())) {
					debtList.remove(i);
				}
			}*/
		}
		List<AfViewAssetBorrowDo> debtList = afViewAssetBorrowService.getListByQueryCondition(query);
		if(debtList==null || debtList.size()==0){
			return new ArrayList<AfViewAssetBorrowDo>();
		}
		
		query.setMinBorrowId(debtList.get(debtList.size()-1).getBorrowId());
		BigDecimal checkAmount=afViewAssetBorrowService.checkAmount(query);
		if (checkAmount.compareTo(totalMoney) < 0) {
			//初始金额不足，逐个债权补充记录直到刚好满足
			while (checkAmount.compareTo(totalMoney) < 0) {
				AfViewAssetBorrowDo afViewAssetBorrowDo = afViewAssetBorrowService.getByQueryCondition(query);
				debtList.add(afViewAssetBorrowDo);
				query.setMinBorrowId(afViewAssetBorrowDo.getBorrowId());
				checkAmount = afViewAssetBorrowService.checkAmount(query);
			}
		}else{
			//非查库方式进行
			for(int i=debtList.size()-1;i>0 && checkAmount.compareTo(totalMoney)>0;i--){
				if(checkAmount.subtract(debtList.get(i).getAmount()).compareTo(totalMoney)>=0){
					checkAmount = checkAmount.subtract(debtList.get(i).getAmount());
					debtList.remove(i);
				}else{
					break;
				}
			}
		}
		return debtList;
	}
	
	/**
	 * 现金贷的EdspayGetCreditRespBo
	 * @param afAssetPackageDo
	 * @param bankInfo
	 * @param afViewAssetBorrowCashDo
	 * @return
	 */
	private EdspayGetCreditRespBo buildCreditBorrowCashRespBo(AfAssetPackageDo afAssetPackageDo,FanbeiBorrowBankInfoBo bankInfo,AfViewAssetBorrowCashDo afViewAssetBorrowCashDo,String minBorrowTime,String maxBorrowTime ){
		Long timeLimit = NumberUtil.objToLongDefault(afViewAssetBorrowCashDo.getType(), null);
		AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(afViewAssetBorrowCashDo.getUserId());
		//获取借款利率配置
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
		BigDecimal borrowRate=BigDecimal.ZERO;
		JSONObject jsonObject=new JSONObject();
		if (afResourceDo!=null &&  afResourceDo.getValue2() != null) {
			JSONArray array= JSONObject.parseArray(afResourceDo.getValue2());
			for (int i = 0; i < array.size(); i++) {
				if (StringUtils.equals((String)array.getJSONObject(i).get("borrowTag"), AfResourceSecType.INTEREST_RATE.getCode())) {
					jsonObject = array.getJSONObject(i);
					break;
				}
			}
			if (StringUtils.equals(afViewAssetBorrowCashDo.getType(), minBorrowTime)) {
				borrowRate=new BigDecimal((String)jsonObject.get("borrowFirstType"));
			}else{
				borrowRate=new BigDecimal((String) jsonObject.get("borrowSecondType"));
			}
		}
		//现金贷的还款计划
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		RepaymentPlan repaymentPlan = new RepaymentPlan();
		repaymentPlan.setRepaymentNo(afViewAssetBorrowCashDo.getBorrowNo());
		repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(DateUtil.addDays(afViewAssetBorrowCashDo.getGmtCreate(), timeLimit.intValue())));
		repaymentPlan.setRepaymentDays(timeLimit);
		repaymentPlan.setRepaymentAmount(afViewAssetBorrowCashDo.getArrivalAmount());
		repaymentPlan.setRepaymentInterest(BigDecimalUtil.multiply(afViewAssetBorrowCashDo.getArrivalAmount(), new BigDecimal(borrowRate.doubleValue()*timeLimit / 36000d)));
		repaymentPlan.setRepaymentPeriod(0);
		repaymentPlans.add(repaymentPlan);
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		creditRespBo.setPackageNo(afAssetPackageDo.getAssetNo());
		creditRespBo.setOrderNo(afViewAssetBorrowCashDo.getBorrowNo());
		creditRespBo.setUserId(afViewAssetBorrowCashDo.getUserId());
		creditRespBo.setName(afViewAssetBorrowCashDo.getRealName());
		creditRespBo.setCardId(afViewAssetBorrowCashDo.getIdNumber());
		creditRespBo.setMobile(afViewAssetBorrowCashDo.getMobile());
		creditRespBo.setBankNo(afViewAssetBorrowCashDo.getCardNumber());
		creditRespBo.setAcctName(bankInfo.getAcctName());
		creditRespBo.setMoney(afViewAssetBorrowCashDo.getArrivalAmount());
		creditRespBo.setApr(borrowRate);
		creditRespBo.setTimeLimit(timeLimit.intValue());
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afViewAssetBorrowCashDo.getGmtCreate()));
		if (StringUtil.isNotBlank(afViewAssetBorrowCashDo.getBorrowRemark())) {
			creditRespBo.setPurpose(afViewAssetBorrowCashDo.getBorrowRemark());
		}else {
			creditRespBo.setPurpose("个人消费");
		}
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepaymentType(repayTypeEnum!=null?repayTypeEnum.getEdsCode():afAssetPackageDo.getRepaymentMethod());
		creditRespBo.setRepayName(bankInfo.getRepayName());
		creditRespBo.setRepayAcct(bankInfo.getRepayAcct());
		creditRespBo.setRepayAcctBankNo(bankInfo.getRepayAcctBankNo());
		creditRespBo.setRepayAcctType(bankInfo.getRepayAcctType());
		creditRespBo.setIsRepayAcctOtherBank(bankInfo.getIsRepayAcctOtherBank());
		creditRespBo.setManageFee(afAssetPackageDo.getAnnualRate());
		if (StringUtil.isNotBlank(afViewAssetBorrowCashDo.getRefundRemark())) {
			creditRespBo.setRepaymentSource(afViewAssetBorrowCashDo.getRefundRemark());
		}else {
			creditRespBo.setRepaymentSource("工资收入");
		}
		creditRespBo.setDebtType(AfAssetPackageBusiType.BORROWCASH.getCode());
		creditRespBo.setIsPeriod(0);
		creditRespBo.setTotalPeriod(1);
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(1);//非实时推送
		return creditRespBo;
	}
	
	/**
	 * 消费分期的EdspayGetCreditRespBo
	 * @param afAssetPackageDo
	 * @param bankInfo
	 * @param afViewAssetBorrowCashDo
	 * @return
	 */
	private EdspayGetCreditRespBo buildCreditBorrowRespBo(AfAssetPackageDo afAssetPackageDo,FanbeiBorrowBankInfoBo bankInfo,AfViewAssetBorrowDo afViewAssetBorrowDo){
		AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(afViewAssetBorrowDo.getUserId());
		//消费分期的还款计划
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		List<AfBorrowBillDo> afBorrowBillDos = afBorrowBillService.getAllBorrowBillByBorrowId(afViewAssetBorrowDo.getBorrowId());
		Date lastBorrowBillGmtPayTime=null;
		for (int i = 0; i < afBorrowBillDos.size(); i++) {
			RepaymentPlan repaymentPlan = new RepaymentPlan();
			repaymentPlan.setRepaymentNo(afBorrowBillDos.get(i).getRid()+"");
			repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(afBorrowBillDos.get(i).getGmtPayTime()));
			repaymentPlan.setRepaymentDays(DateUtil.getNumberOfDayBetween(afViewAssetBorrowDo.getGmtCreate(), afBorrowBillDos.get(i).getGmtPayTime()));
			repaymentPlan.setRepaymentAmount(afBorrowBillDos.get(i).getPrincipleAmount());
			repaymentPlan.setRepaymentInterest(afBorrowBillDos.get(i).getInterestAmount());
			repaymentPlan.setRepaymentPeriod(afBorrowBillDos.get(i).getBillNper()-1);
			repaymentPlans.add(repaymentPlan);
			if (i == afBorrowBillDos.size() - 1) {
				lastBorrowBillGmtPayTime= afBorrowBillDos.get(i).getGmtPayTime();
			}
		}
		Integer nper = afViewAssetBorrowDo.getNper();//分期数
		if (nper == 5 || nper == 11) {//兼容租房5期与11期，作为6期与12期的利率
        	nper++;
        }
		//获取消费分期协议年化利率配置
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.borrowConsume.getCode());
		BigDecimal borrowRate=BigDecimal.ZERO;
		JSONArray array= new JSONArray();
		if (afResourceDo!=null&& afResourceDo.getValue3()!=null) {
			array= JSONObject.parseArray(afResourceDo.getValue3());
			for (int i = 0; i < array.size(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				Integer confNper= (Integer) jsonObject.get("nper");
				if (nper == confNper) {
					borrowRate=(BigDecimal) jsonObject.get("rate");
					break;
				}
			}
		}
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		creditRespBo.setPackageNo(afAssetPackageDo.getAssetNo());
		creditRespBo.setOrderNo(afViewAssetBorrowDo.getBorrowNo());
		creditRespBo.setUserId(afViewAssetBorrowDo.getUserId());
		creditRespBo.setName(afViewAssetBorrowDo.getRealName());
		creditRespBo.setCardId(afViewAssetBorrowDo.getIdNumber());
		creditRespBo.setMobile(afViewAssetBorrowDo.getUserName());
		creditRespBo.setBankNo("");
		creditRespBo.setAcctName(bankInfo.getAcctName());
		creditRespBo.setMoney(afViewAssetBorrowDo.getAmount());
		creditRespBo.setApr(BigDecimalUtil.multiply(borrowRate, new BigDecimal(100)));
		creditRespBo.setTimeLimit((int) DateUtil.getNumberOfDayBetween(afViewAssetBorrowDo.getGmtCreate(), lastBorrowBillGmtPayTime));
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afViewAssetBorrowDo.getGmtCreate()));
		creditRespBo.setPurpose("个人消费");
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepaymentType(repayTypeEnum!=null?repayTypeEnum.getEdsCode():afAssetPackageDo.getRepaymentMethod());
		creditRespBo.setRepayName(bankInfo.getRepayName());
		creditRespBo.setRepayAcct(bankInfo.getRepayAcct());
		creditRespBo.setRepayAcctBankNo(bankInfo.getRepayAcctBankNo());
		creditRespBo.setRepayAcctType(bankInfo.getRepayAcctType());
		creditRespBo.setIsRepayAcctOtherBank(bankInfo.getIsRepayAcctOtherBank());
		creditRespBo.setManageFee(afAssetPackageDo.getAnnualRate());
		creditRespBo.setRepaymentSource("工资收入");
		creditRespBo.setDebtType(AfAssetPackageBusiType.BORROW.getCode());
		creditRespBo.setIsPeriod(1);
		creditRespBo.setTotalPeriod(afViewAssetBorrowDo.getNper());
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(1);//非实时推送
		return creditRespBo;
	}
	
	/**
	 * 根据资产方对应债权订单号,获取对应用户信息
	 * @param afAssetSideInfoDo
	 * @param orderNos
	 * @return
	 */
	@Override
	public List<EdspayGetPlatUserInfoRespBo> getBatchPlatUserInfo(AfAssetSideInfoDo afAssetSideInfoDo, List<String> orderNos){
		List<EdspayGetPlatUserInfoRespBo> platUserInfos = new ArrayList<EdspayGetPlatUserInfoRespBo>();
		for (String borrowNo : orderNos) {
			try {
				EdspayGetPlatUserInfoRespBo userInfoRespBo = null;
				//从缓存中获取，如果命中，直接返回，如果没有从库里面实时查询并放入缓存
				String tempJsonSting = "";
				String cacheKey = Constants.ASSET_SIDE_SEARCH_USER_KEY+afAssetSideInfoDo.getAssetSideFlag()+borrowNo;
				tempJsonSting = StringUtil.null2Str(bizCacheUtil.getObject(cacheKey));
				if(StringUtil.isNotBlank(tempJsonSting)){
					//缓存中取
					try {
						userInfoRespBo = JSON.toJavaObject(JSON.parseObject(tempJsonSting), EdspayGetPlatUserInfoRespBo.class);
						platUserInfos.add(userInfoRespBo);
						logger.info("getBatchPlatUserInfo from redis success,afAssetSideFlag="+afAssetSideInfoDo.getAssetSideFlag()+",tempJsonSting"+tempJsonSting);
					} catch (Exception e) {
						//清除缓存
						bizCacheUtil.delCache(cacheKey);
						logger.error("getBatchPlatUserInfo from redis convert error,afAssetSideFlag="+afAssetSideInfoDo.getAssetSideFlag()+",tempJsonSting"+tempJsonSting,e);
					}
				}
				
				//缓存中不存在或者json解析失败，从库里面查
				if(userInfoRespBo == null){
					//用户债权对应借款及逾期信息获取
					AfBorrowCashDo afBorrowCashDo = afBorrowCashDao.getBorrowCashInfoByBorrowNo(borrowNo);
					if(afBorrowCashDo==null){
						logger.error("getBatchPlatUserInfo afBorrowCashDo not exist,afAssetSideFlag="+afAssetSideInfoDo.getAssetSideFlag()+",borrowNo="+borrowNo);
						continue;
					}
					
					AfUserBorrowCashOverdueInfoDto currOverdueInfo = afBorrowCashDao.getOverdueInfoByUserId(afBorrowCashDo.getUserId());
					if(currOverdueInfo==null){
						logger.error("getBatchPlatUserInfo currOverdueInfo not exist,afAssetSideFlag="+afAssetSideInfoDo.getAssetSideFlag()+",borrowNo="+borrowNo+",userId="+afBorrowCashDo.getUserId());
						continue;
					}
					
					Integer repaymentStatus = 0;
					Long repaymentTime = null;
					Integer isOverdue = 0;
					if(AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus())){
						repaymentStatus = 1;
						repaymentTime = DateUtil.getSpecSecondTimeStamp(afBorrowCashDo.getGmtModified());
					}
					if(afBorrowCashDo.getOverdueDay()>0){
						isOverdue = 1;
					}
					userInfoRespBo = new EdspayGetPlatUserInfoRespBo(afBorrowCashDo.getUserId(), borrowNo, 0, currOverdueInfo.getOverdueNums(),
							currOverdueInfo.getOverdueAmount(), afBorrowCashDo.getRepayAmount(), repaymentStatus,
							repaymentTime, isOverdue);
					platUserInfos.add(userInfoRespBo);
					
					//放入缓存
					tempJsonSting = JSON.toJSONString(userInfoRespBo);
					bizCacheUtil.saveObject(cacheKey, tempJsonSting, Constants.SECOND_OF_HALF_DAY);
				}
			} catch (Exception e) {
				logger.error("getBatchPlatUserInfo exception"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid(),e);
			}
		}
		return platUserInfos;
	}

	@Override
	public List<EdspayGetCreditRespBo> getBorrowBatchCreditInfo(final FanbeiBorrowBankInfoBo bankInfo,final AfAssetSideInfoDo afAssetSideInfoDo,final BigDecimal totalMoney,final Date gmtCreateStart, final Date gmtCreateEnd) {
		final List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		Long result = transactionTemplate.execute(new TransactionCallback<Long>() {
	        @Override
            public Long doInTransaction(TransactionStatus status) {
            	List<AfViewAssetBorrowDo> debtList= new ArrayList<AfViewAssetBorrowDo>();
            	try {
        			//加锁Lock
        			boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(Constants.CACHEKEY_ASSETPACKAGE_LOCK, Constants.CACHEKEY_ASSETPACKAGE_LOCK_VALUE,10, Constants.SECOND_OF_TEN_MINITS);
        			if (isLock) {
        				//校验现在金额是否满足
        				BigDecimal sumAmount = afViewAssetBorrowService.getSumAmount(gmtCreateStart,gmtCreateEnd);
    					if (totalMoney.compareTo(sumAmount) > 0) {
    						logger.error("getBorrowBatchCreditInfo  error该时间段内消费分期资产金额不足，共"+sumAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
    						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
    						return 0L;
    					}
        				//分配消费分期债权资产包
    					debtList = matchingBorrowDebt(totalMoney,gmtCreateStart, gmtCreateEnd);
        				//生成资产包
        				Date currDate = new Date();
        				AfAssetPackageDo afAssetPackageDo = new AfAssetPackageDo();
        				afAssetPackageDo.setGmtCreate(currDate);
        				afAssetPackageDo.setGmtModified(currDate);
        				afAssetPackageDo.setStatus(AfAssetPackageStatus.SENDED.getCode());
        				String packageName = "";
        				if(totalMoney.intValue()/10000>0){
        					packageName = afAssetSideInfoDo.getName()+totalMoney.intValue()/10000+"万消费分期资产包"+DateUtil.formatDate(new Date());
        				}else{
        					packageName = afAssetSideInfoDo.getName()+totalMoney+"元消费分期资产包"+DateUtil.formatDate(new Date());
        				}
        				afAssetPackageDo.setAssetName(packageName);
        				afAssetPackageDo.setAssetNo("zcb"+System.currentTimeMillis());
        				afAssetPackageDo.setAssetSideId(afAssetSideInfoDo.getRid());
        				afAssetPackageDo.setBeginTime(gmtCreateStart);
        				afAssetPackageDo.setEndTime(gmtCreateEnd);
        				afAssetPackageDo.setTotalMoney(totalMoney);
        				afAssetPackageDo.setBorrowRate(afAssetSideInfoDo.getBorrowRate());
        				afAssetPackageDo.setAnnualRate(afAssetSideInfoDo.getAnnualRate());
        				afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepaytType());
        				afAssetPackageDo.setValidStatus(YesNoStatus.YES.getCode());
        				afAssetPackageDo.setSendMode(AfAssetPackageSendMode.INTERFACE.getCode());
        				afAssetPackageDo.setType(AfAssetPackageType.ASSET_REQ.getCode());
        				afAssetPackageDo.setBusiType(AfAssetPackageBusiType.BORROW.getCode());
        				int result=afAssetPackageDao.saveRecord(afAssetPackageDo);
        				
        				BigDecimal realAmount = BigDecimal.ZERO;
        				for (AfViewAssetBorrowDo afViewAssetBorrowDo : debtList) {
        					realAmount = realAmount.add(afViewAssetBorrowDo.getAmount());
        					EdspayGetCreditRespBo edspayGetCreditRespBo = buildCreditBorrowRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowDo);
        					creditInfos.add(edspayGetCreditRespBo);
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(currDate);
        					afAssetPackageDetailDo.setGmtModified(currDate);
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowDo.getBorrowId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowDo.getBorrowNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
        					afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowDo.getBorrowNo());
        				}
        				//更新实际金额
        				afAssetPackageDo.setRealTotalMoney(realAmount);
        				afAssetPackageDao.updateById(afAssetPackageDo);
        				
        				//资产方操作日志添加
        				AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GET_ASSET.getCode(), afAssetPackageDo.getRealTotalMoney(), afAssetPackageDo.getRid()+"","", "请求消费分期资产包金额totalMoney="+totalMoney+",实际金额："+realAmount);
        				afAssetSideOperaLogDao.saveRecord(operaLogDo);
        				
        				bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        				return 1l;
        			}else{
        				 logger.error("getBatchCreditInfo  error获取锁失败"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
        			}
        		} catch (Exception e) {
        			status.setRollbackOnly();
        			logger.error("getBatchCreditInfo exception"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid(),e);
        			bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        		}
            	return 0l;
            }
	    });
		//释放锁Lock完成,结束
		if (result == 1) {
			return creditInfos;
		}else{
			return new ArrayList<EdspayGetCreditRespBo>();
		}
	}

	@Override
	public List<EdspayGetCreditRespBo> getLoanBatchCreditInfo(final FanbeiBorrowBankInfoBo bankInfo,final AfAssetSideInfoDo afAssetSideInfoDo,final BigDecimal totalMoney,final Date gmtCreateStart, final Date gmtCreateEnd) {
		final List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		Long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
            public Long doInTransaction(TransactionStatus status) {
            	List<AfViewAssetLoanDo> debtList= new ArrayList<AfViewAssetLoanDo>();
            	try {
        			//加锁Lock
        			boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(Constants.CACHEKEY_ASSETPACKAGE_LOCK, Constants.CACHEKEY_ASSETPACKAGE_LOCK_VALUE,10, Constants.SECOND_OF_TEN_MINITS);
        			if (isLock) {
        				//校验现在金额是否满足
        				BigDecimal sumAmount = afViewAssetLoanService.getSumAmount(gmtCreateStart,gmtCreateEnd);
    					if (totalMoney.compareTo(sumAmount) > 0) {
    						logger.error("getLoanBatchCreditInfo  error,该时间段内白领贷资产金额不足，共"+sumAmount+"元");
    						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
    						return 0L;
    					}
    					debtList = matchingLoanDebt(totalMoney,gmtCreateStart, gmtCreateEnd);
        				//生成资产包
        				Date cur = new Date();
        				AfAssetPackageDo afAssetPackageDo = new AfAssetPackageDo();
        				afAssetPackageDo.setGmtCreate(cur);
        				afAssetPackageDo.setGmtModified(cur);
        				afAssetPackageDo.setStatus(AfAssetPackageStatus.SENDED.getCode());
        				String packageName = "";
        				if(totalMoney.intValue()/10000>0){
        					packageName = afAssetSideInfoDo.getName()+totalMoney.intValue()/10000+"万白领贷资产包"+DateUtil.formatDate(cur);
        				}else{
        					packageName = afAssetSideInfoDo.getName()+totalMoney+"元白领贷资产包"+DateUtil.formatDate(cur);
        				}
        				afAssetPackageDo.setAssetName(packageName);
        				afAssetPackageDo.setAssetNo("zcb"+System.currentTimeMillis());
        				afAssetPackageDo.setAssetSideId(afAssetSideInfoDo.getRid());
        				afAssetPackageDo.setBeginTime(gmtCreateStart);
        				afAssetPackageDo.setEndTime(gmtCreateEnd);
        				afAssetPackageDo.setTotalMoney(totalMoney);
        				afAssetPackageDo.setBorrowRate(afAssetSideInfoDo.getBorrowRate());
        				afAssetPackageDo.setAnnualRate(afAssetSideInfoDo.getAnnualRate());
        				afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepaytType());
        				afAssetPackageDo.setValidStatus(YesNoStatus.YES.getCode());
        				afAssetPackageDo.setSendMode(AfAssetPackageSendMode.INTERFACE.getCode());
        				afAssetPackageDo.setType(AfAssetPackageType.ASSET_REQ.getCode());
        				afAssetPackageDo.setBusiType(AfAssetPackageBusiType.LOAN.getCode());
        				int result=afAssetPackageDao.saveRecord(afAssetPackageDo);
        				
        				BigDecimal realAmount = BigDecimal.ZERO;
        				for (AfViewAssetLoanDo afViewAssetLoanDo : debtList) {
        					realAmount = realAmount.add(afViewAssetLoanDo.getAmount());
        					EdspayGetCreditRespBo edspayGetCreditRespBo = buildCreditLoanRespBo(afAssetPackageDo,bankInfo,afViewAssetLoanDo);
        					creditInfos.add(edspayGetCreditRespBo);
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(cur);
        					afAssetPackageDetailDo.setGmtModified(cur);
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetLoanDo.getLoanId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetLoanDo.getLoanNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
        					afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetLoanDo.getLoanNo());
        				}
        				//更新实际金额
        				afAssetPackageDo.setRealTotalMoney(realAmount);
        				afAssetPackageDao.updateById(afAssetPackageDo);
        				
        				//资产方操作日志添加
        				AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), cur, AfAssetOperaLogChangeType.GET_ASSET.getCode(), afAssetPackageDo.getRealTotalMoney(), afAssetPackageDo.getRid()+"","", "请求消费分期资产包金额totalMoney="+totalMoney+",实际金额："+realAmount);
        				afAssetSideOperaLogDao.saveRecord(operaLogDo);
        				
        				bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        				return 1l;
        			}else{
        				 logger.error("getLoanBatchCreditInfo  get lock fail");
        			}
        		} catch (Exception e) {
        			status.setRollbackOnly();
        			logger.error("getLoanBatchCreditInfo error:"+e);
        			bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        		}
            	return 0l;
            }
	    });
		//释放锁Lock完成,结束
		if (result == 1) {
			return creditInfos;
		}else{
			return new ArrayList<EdspayGetCreditRespBo>();
		}
	}

	protected EdspayGetCreditRespBo buildCreditLoanRespBo(AfAssetPackageDo afAssetPackageDo, FanbeiBorrowBankInfoBo bankInfo,AfViewAssetLoanDo afViewAssetLoanDo) {
		AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(afViewAssetLoanDo.getUserId());
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		List<AfLoanPeriodsDo> loanPeriodsList = afLoanPeriodsService.getAllLoanPeriodsByLoanId(afViewAssetLoanDo.getLoanId());
		Date lastLoanPeriodsGmtPayTime=null;
		for (int i = 0; i < loanPeriodsList.size(); i++) {
			RepaymentPlan repaymentPlan = new RepaymentPlan();
			repaymentPlan.setRepaymentNo(loanPeriodsList.get(i).getRid()+"");
			repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(loanPeriodsList.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentDays(DateUtil.getNumberOfDayBetween(afViewAssetLoanDo.getGmtCreate(), loanPeriodsList.get(i).getGmtPlanRepay()));
			repaymentPlan.setRepaymentAmount(loanPeriodsList.get(i).getAmount());
			repaymentPlan.setRepaymentInterest(loanPeriodsList.get(i).getInterestFee());
			repaymentPlan.setRepaymentPeriod(loanPeriodsList.get(i).getNper()-1);
			repaymentPlans.add(repaymentPlan);
			if (i == loanPeriodsList.size() - 1) {
				lastLoanPeriodsGmtPayTime= loanPeriodsList.get(i).getGmtPlanRepay();
			}
		}
		Integer nper = afViewAssetLoanDo.getNper();
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		creditRespBo.setPackageNo(afAssetPackageDo.getAssetNo());
		creditRespBo.setOrderNo(afViewAssetLoanDo.getLoanNo());
		creditRespBo.setUserId(afViewAssetLoanDo.getUserId());
		creditRespBo.setName(afViewAssetLoanDo.getRealName());
		creditRespBo.setCardId(afViewAssetLoanDo.getIdNumber());
		creditRespBo.setMobile(afViewAssetLoanDo.getMobile());
		creditRespBo.setBankNo(afViewAssetLoanDo.getBankno());
		creditRespBo.setAcctName(bankInfo.getAcctName());
		creditRespBo.setMoney(afViewAssetLoanDo.getAmount());
		creditRespBo.setApr(BigDecimalUtil.multiply(afViewAssetLoanDo.getInterestRate(),new BigDecimal(100)));
		creditRespBo.setTimeLimit((int) DateUtil.getNumberOfDayBetween(afViewAssetLoanDo.getGmtCreate(), lastLoanPeriodsGmtPayTime));
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afViewAssetLoanDo.getGmtCreate()));
		creditRespBo.setPurpose("个人消费");
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepaymentType(repayTypeEnum!=null?repayTypeEnum.getEdsCode():afAssetPackageDo.getRepaymentMethod());
		creditRespBo.setRepayName(bankInfo.getRepayName());
		creditRespBo.setRepayAcct(bankInfo.getRepayAcct());
		creditRespBo.setRepayAcctBankNo(bankInfo.getRepayAcctBankNo());
		creditRespBo.setRepayAcctType(bankInfo.getRepayAcctType());
		creditRespBo.setIsRepayAcctOtherBank(bankInfo.getIsRepayAcctOtherBank());
		creditRespBo.setManageFee(afAssetPackageDo.getAnnualRate());
		creditRespBo.setRepaymentSource("工资收入");
		creditRespBo.setDebtType(AfAssetPackageBusiType.LOAN.getCode());
		creditRespBo.setIsPeriod(1);
		creditRespBo.setTotalPeriod(afViewAssetLoanDo.getNper());
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(1);//非实时推送
		return creditRespBo;
	}

	protected List<AfViewAssetLoanDo> matchingLoanDebt(BigDecimal totalMoney,Date gmtCreateStart, Date gmtCreateEnd) {
		//匹配初始记录;
		Integer limitNums = totalMoney.intValue()/Constants.AVG_LOAN_AMOUNT;
		AfViewAssetLoanQuery query = new AfViewAssetLoanQuery();
		query.setGmtCreateStart(gmtCreateStart);
		query.setGmtCreateEnd(gmtCreateEnd);
		query.setLimitNums(limitNums == 0 ? 1 : limitNums);
		AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
		if (pushWhiteResource != null) {
			String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
			Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
			//推送白名单开启,白名单的userid不推送，仅用于实时推送
			query.setUserIds(Arrays.asList(whiteUserIds));
			/*for (int i = 0; i < debtList.size(); i++) {
				if (Arrays.asList(whiteUserId).contains(debtList.get(i).getUserId())) {
					debtList.remove(i);
				}
			}*/
		}
		List<AfViewAssetLoanDo> debtList = afViewAssetLoanService.getListByQueryCondition(query);
		if(debtList==null || debtList.size()==0){
			return new ArrayList<AfViewAssetLoanDo>();
		}
		query.setMinLoanId(debtList.get(debtList.size()-1).getLoanId());
		BigDecimal checkAmount=afViewAssetLoanService.checkAmount(query);
		if (checkAmount.compareTo(totalMoney) < 0) {
			//初始金额不足，逐个债权补充记录直到刚好满足
			while (checkAmount.compareTo(totalMoney) < 0) {
				AfViewAssetLoanDo afViewAssetLoanDo = afViewAssetLoanService.getByQueryCondition(query);
				debtList.add(afViewAssetLoanDo);
				query.setMinLoanId(afViewAssetLoanDo.getLoanId());
				checkAmount = afViewAssetLoanService.checkAmount(query);
			}
		}else{
			//非查库方式进行
			for(int i=debtList.size()-1;i>0 && checkAmount.compareTo(totalMoney)>0;i--){
				if(checkAmount.subtract(debtList.get(i).getAmount()).compareTo(totalMoney)>=0){
					checkAmount = checkAmount.subtract(debtList.get(i).getAmount());
					debtList.remove(i);
				}else{
					break;
				}
			}
		}
		return debtList;
	}


}
