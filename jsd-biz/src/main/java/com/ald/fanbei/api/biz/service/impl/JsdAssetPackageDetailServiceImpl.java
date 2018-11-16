package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.bo.aassetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.bo.assetpush.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdViewAssetService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.ald.fanbei.api.dal.query.JsdViewAssetQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.biz.service.JsdAssetPackageDetailService;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:21:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdAssetPackageDetailService")
public class JsdAssetPackageDetailServiceImpl extends ParentServiceImpl<JsdAssetPackageDetailDo, Long> implements JsdAssetPackageDetailService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdAssetPackageDetailServiceImpl.class);
   
    @Resource
    private JsdAssetPackageDetailDao jsdAssetPackageDetailDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	JsdResourceService jsdResourceService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	JsdViewAssetService jsdViewAssetService;
	@Resource
	JsdAssetPackageDao jsdAssetPackageDao;
	@Resource
	JsdAssetSideOperaLogDao jsdAssetSideOperaLogDao;
	@Resource
	JsdBorrowCashDao jsdBorrowCashDao;

	@Override
	public BaseDao<JsdAssetPackageDetailDo, Long> getDao() {
		return jsdAssetPackageDetailDao;
	}

	@Override
	public List<EdspayGetCreditRespBo> getXgJsdBatchCreditInfo(FanbeiBorrowBankInfoBo bankInfo, JsdAssetSideInfoDo afAssetSideInfoDo, BigDecimal totalMoney, Date gmtCreateStart, Date gmtCreateEnd, BigDecimal minMoney) {
		final List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		Long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				BigDecimal minMoneyNew = minMoney;
				List<JsdViewAssetDo> minDebtList= new ArrayList<JsdViewAssetDo>();
				List<JsdViewAssetDo> maxDebtList=new ArrayList<JsdViewAssetDo>();
				//根据resource表灵活配置的天数，目前为10/20天
				JsdResourceDo afResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RATE_INFO.getCode());
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
						BigDecimal sumMinAmount =jsdViewAssetService.getSumMinAmount(gmtCreateStart,gmtCreateEnd,minBorrowTime);
						if (minMoneyNew.compareTo(sumMinAmount) > 0) {
							logger.error("getBatchCreditInfo  error该时间段内"+minBorrowTime+"天资产金额不足，共"+sumMinAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
							bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
							return 0L;
						}
						BigDecimal sumMaxAmount = jsdViewAssetService.getSumMaxAmount(gmtCreateStart,gmtCreateEnd,maxBorrowTime);
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
						JsdAssetPackageDo afAssetPackageDo = new JsdAssetPackageDo();
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
						afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepayType());
						afAssetPackageDo.setValidStatus(YesNoStatus.YES.getCode());
						afAssetPackageDo.setSendMode(AfAssetPackageSendMode.INTERFACE.getCode());
						afAssetPackageDo.setType(AfAssetPackageType.ASSET_REQ.getCode());
						afAssetPackageDo.setBusiType(AfAssetPackageBusiType.BORROWCASH.getCode());
						jsdAssetPackageDao.saveRecord(afAssetPackageDo);

						BigDecimal realMinAmount = BigDecimal.ZERO;
						BigDecimal realMaxAmount = BigDecimal.ZERO;
						logger.info("overdueDebtList="+JSON.toJSONString(minDebtList));
						List<String> existMinBorrowNos=new ArrayList<>();
						for (JsdViewAssetDo afViewAssetBorrowCashDo : minDebtList) {

							if (existMinBorrowNos.contains(afViewAssetBorrowCashDo.getBorrowNo())) {
								logger.info("duplicate borrowNo="+afViewAssetBorrowCashDo.getBorrowNo());
								continue;
							}

							realMinAmount = realMinAmount.add(afViewAssetBorrowCashDo.getAmount());
							EdspayGetCreditRespBo edspayGetCreditRespBo = buildCreditBorrowCashRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo,minBorrowTime,maxBorrowTime);
							creditInfos.add(edspayGetCreditRespBo);
							JsdAssetPackageDetailDo afAssetPackageDetailDo = new JsdAssetPackageDetailDo();
							afAssetPackageDetailDo.setGmtCreate(currDate);
							afAssetPackageDetailDo.setGmtModified(currDate);
							afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
							afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
							afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
							afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
							afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
							afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
							jsdAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
							//标记重新分配记录
							jsdAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowNo());
							existMinBorrowNos.add(afViewAssetBorrowCashDo.getBorrowNo());
						}

						logger.info("overdueDebtList="+JSON.toJSONString(maxDebtList));
						List<String> existMaxBorrowNos=new ArrayList<>();
						for (JsdViewAssetDo afViewAssetBorrowCashDo : maxDebtList) {
							if (existMaxBorrowNos.contains(afViewAssetBorrowCashDo.getBorrowNo())) {
								logger.info("duplicate borrowNo="+afViewAssetBorrowCashDo.getBorrowNo());
								continue;
							}
							realMaxAmount = realMaxAmount.add(afViewAssetBorrowCashDo.getAmount());
							EdspayGetCreditRespBo edspayGetCreditRespBo=buildCreditBorrowCashRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo,minBorrowTime,maxBorrowTime);
							creditInfos.add(edspayGetCreditRespBo);
							JsdAssetPackageDetailDo afAssetPackageDetailDo = new JsdAssetPackageDetailDo();
							afAssetPackageDetailDo.setGmtCreate(new Date());
							afAssetPackageDetailDo.setGmtModified(new Date());
							afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
							afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
							afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
							afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
							afAssetPackageDetailDo.setBorrowRate(edspayGetCreditRespBo.getApr());
							afAssetPackageDetailDo.setProfitRate(edspayGetCreditRespBo.getManageFee());
							jsdAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
							//标记重新分配记录
							jsdAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowNo());
							existMaxBorrowNos.add(afViewAssetBorrowCashDo.getBorrowNo());
						}

						//更新实际金额
						afAssetPackageDo.setRealTotalMoney(realMinAmount.add(realMaxAmount));
						jsdAssetPackageDao.updateById(afAssetPackageDo);

						//资产方操作日志添加
						JsdAssetSideOperaLogDo operaLogDo = new JsdAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GET_ASSET.getCode(), afAssetPackageDo.getRealTotalMoney(), afAssetPackageDo.getRid()+"","", "请求现金贷资产包金额totalMoney="+totalMoney+",实际"+minBorrowTime+"天："+realMinAmount+","+maxBorrowTime+"天"+realMaxAmount);
						jsdAssetSideOperaLogDao.saveRecord(operaLogDo);
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
	public int batchGiveBackCreditInfo(final JsdAssetSideInfoDo afAssetSideInfoDo,final List<String> orderNos,final Integer debtType){
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
			final JsdAssetPackageDetailDo afAssetPackageDetail = jsdAssetPackageDetailDao.getByBorrowNo(borrowNo);
			if(afAssetPackageDetail==null){
				logger.error("batchGiveBackCreditInfo error ,orderNo not exists,borrowNo="+borrowNo);
				invalidBorrowNos.add(borrowNo);
				continue;
			}
			final JsdAssetPackageDo packageDo = jsdAssetPackageDao.getById(afAssetPackageDetail.getAssetPackageId());
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
						if (debtType == 4) {
							//西瓜极速贷2.0
							JsdBorrowCashDo BorrowDo = jsdBorrowCashDao.getBorrowById(afAssetPackageDetail.getBorrowCashId());
							if(BorrowDo==null){
								logger.error("batchGiveBackCreditInfo error ,borrow not exists,id="+afAssetPackageDetail.getBorrowCashId());
								return 0;
							}
							cancelMoney=BorrowDo.getAmount();
						}
						//更新此债权相关明细
						int effectNums = jsdAssetPackageDetailDao.invalidPackageDetail(afAssetPackageDetail.getBorrowNo());

						if(effectNums>0){
							totalMoneyList.add(cancelMoney);
							JsdAssetPackageDo modifyPackageDo = new JsdAssetPackageDo();
							modifyPackageDo.setRid(packageDo.getRid());
							modifyPackageDo.setRealTotalMoney(cancelMoney.negate());
							modifyPackageDo.setGmtModified(currDate);
							jsdAssetPackageDao.updateRealTotalMoneyById(modifyPackageDo);
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
			JsdAssetSideOperaLogDo operaLogDo = new JsdAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), totalMoney, refPackageId,refDetailIds, "成功退回债权金额："+totalMoney+"元");
			jsdAssetSideOperaLogDao.saveRecord(operaLogDo);
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


	private List<JsdViewAssetDo> matchingBorrowCashDebt(BigDecimal amount, Date gmtCreateStart,Date gmtCreateEnd,String type) {
		//匹配初始记录;
		Integer limitNums = amount.intValue()/Constants.AVG_BORROWCASH_AMOUNT;
		JsdViewAssetQuery query = new JsdViewAssetQuery();
		query.setGmtCreateStart(gmtCreateStart);
		query.setGmtCreateEnd(gmtCreateEnd);
		query.setType(type);
		query.setLimitNums(limitNums == 0 ? 1 : limitNums);
		JsdResourceDo pushWhiteResource = jsdResourceService.getByTypeAngSecType(ResourceType.ASSET_PUSH_CONF.getCode(), ResourceType.ASSET_PUSH_WHITE.getCode());
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
		List<JsdViewAssetDo> debtList = jsdViewAssetService.getListByQueryCondition(query);
		if(debtList==null || debtList.size()==0){
			return new ArrayList<JsdViewAssetDo>();
		}

		query.setMinBorrowCashId(debtList.get(debtList.size()-1).getBorrowCashId());
		BigDecimal checkAmount=jsdViewAssetService.checkAmount(query);
		if (checkAmount.compareTo(amount) < 0) {
			//初始金额不足，逐个债权补充记录直到刚好满足
			while (checkAmount.compareTo(amount) < 0) {
				JsdViewAssetDo jsdViewAssetDo = jsdViewAssetService.getByQueryCondition(query);
				debtList.add(jsdViewAssetDo);
				query.setMinBorrowCashId(jsdViewAssetDo.getBorrowCashId());
				checkAmount = jsdViewAssetService.checkAmount(query);
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

	private EdspayGetCreditRespBo buildCreditBorrowCashRespBo(JsdAssetPackageDo afAssetPackageDo,FanbeiBorrowBankInfoBo bankInfo,JsdViewAssetDo afViewAssetBorrowCashDo,String minBorrowTime,String maxBorrowTime ){
		Long timeLimit = NumberUtil.objToLongDefault(afViewAssetBorrowCashDo.getType(), null);
		AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = jsdBorrowCashDao.getOverdueInfoByUserId(afViewAssetBorrowCashDo.getUserId());
		//获取借款利率配置
		JsdResourceServiceImpl.ResourceRateInfoBo borrowRateInfo = jsdResourceService.getRateInfo(afViewAssetBorrowCashDo.getType());
		BigDecimal borrowRate=BigDecimal.ZERO;
		if (borrowRateInfo!=null ) {
			borrowRate=borrowRateInfo.interestRate;
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
		creditRespBo.setApr(BigDecimalUtil.multiply(borrowRate,new BigDecimal(100)));
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
		if (StringUtil.isNotBlank(afViewAssetBorrowCashDo.getRepayRemark())) {
			creditRespBo.setRepaymentSource(afViewAssetBorrowCashDo.getRepayRemark());
		}else {
			creditRespBo.setRepaymentSource("工资收入");
		}
		creditRespBo.setDebtType(AfAssetPackageBusiType.XGJSD.getCode());
		creditRespBo.setIsPeriod(0);
		creditRespBo.setTotalPeriod(1);
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(1);//非实时推送
		return creditRespBo;
	}

	@Override
	public int addPackageDetailLoanTime(List<String> orderNos, Date loanTime, Integer debtType) {

		for (String tempBorrowNo : orderNos) {
			final String borrowNo = tempBorrowNo;
			final JsdAssetPackageDetailDo afAssetPackageDetail = jsdAssetPackageDetailDao.getByBorrowNo(borrowNo);
			if (afAssetPackageDetail == null) {
				logger.error("batchGiveBackCreditInfo error ,orderNo not exists,borrowNo=" + borrowNo);
				continue;
			}
			final JsdAssetPackageDo packageDo = jsdAssetPackageDao.getById(afAssetPackageDetail.getAssetPackageId());
			if (packageDo == null) {
				logger.error("batchGiveBackCreditInfo error ,packageDo not exists,id=" + afAssetPackageDetail.getAssetPackageId());
				continue;
			}

			try {
				if (!AfAssetPackageDetailStatus.VALID.getCode().equals(afAssetPackageDetail.getStatus())) {
					logger.error("batchGiveBackCreditInfo error ,afAssetPackageDetail is invalid,borrowNo=" + borrowNo);
					return 0;
				}

				if (debtType == 4) {
					//消费分期
					JsdBorrowCashDo BorrowDo = jsdBorrowCashDao.getBorrowById(afAssetPackageDetail.getBorrowCashId());
					if (BorrowDo == null) {
						logger.error("batchGiveBackCreditInfo error ,borrow not exists,id=" + afAssetPackageDetail.getBorrowCashId());
						return 0;
					}
				}
				//记录放款时间
				JsdAssetPackageDetailDo afAssetPackageDetailTemp = new JsdAssetPackageDetailDo();
				afAssetPackageDetailTemp.setRid(afAssetPackageDetail.getRid());
				afAssetPackageDetailTemp.setLoanTime(loanTime);
				jsdAssetPackageDetailDao.updateById(afAssetPackageDetailTemp);
			} catch (Exception e) {
				logger.error("batchGiveBackCreditInfo exe exception ,borrowNo=" + borrowNo, e);
				return 0;
			}
		}
		return 1;
	}
}