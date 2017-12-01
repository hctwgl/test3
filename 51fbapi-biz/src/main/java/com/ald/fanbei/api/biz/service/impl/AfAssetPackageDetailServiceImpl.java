package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfAssetPackageService;
import com.ald.fanbei.api.biz.service.AfAssetSideInfoService;
import com.ald.fanbei.api.biz.service.AfViewAssetBorrowCashService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfAssetPackageDetailStatus;
import com.ald.fanbei.api.common.enums.AfAssetPackageStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDetailDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowCashDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowCashQuery;



/**
 * 资产包与债权记录关系ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
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
	private AfViewAssetBorrowCashService  afViewAssetBorrowCashService;
	
	 @Override
	public BaseDao<AfAssetPackageDetailDo, Long> getDao() {
		return afAssetPackageDetailDao;
	}
		
	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 */
	@Override
	public void batchGiveBackCreditInfo(AfAssetSideInfoDo afAssetSideInfoDo,List<String> orderNos){
		//校验
		
		//begin事务,记录变更
		
		//end事务
		
		//重新生成资产包上传oss  begin
		
		//重新生成资产包上传oss  end
		
	}
	
	
	
	/**
	 * 根据资产方要求,获取资产方对应的债权信息
	 */
	@Override
	public List<EdspayGetCreditRespBo> getBatchCreditInfo(AfAssetSideInfoDo afAssetSideInfoDo,BigDecimal totalMoney,Date gmtCreateStart,Date gmtCreateEnd,BigDecimal sevenMoney){
		List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		List<AfViewAssetBorrowCashDo> sevenDebtList= new ArrayList<AfViewAssetBorrowCashDo>();
		List<AfViewAssetBorrowCashDo> fourteenDebtList=new ArrayList<AfViewAssetBorrowCashDo>();
		try {
			//加锁Lock
			boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(Constants.CACHEKEY_ASSETPACKAGE_LOCK, Constants.CACHEKEY_ASSETPACKAGE_LOCK_VALUE,5, Constants.SECOND_OF_FIFTEEN);
			//校验现在金额是否满足
			if (isLock) {
				//分配债权资产包
				if (sevenMoney!=null) {
					//区分7天和14天校验
					BigDecimal sumSevenAmount = afViewAssetBorrowCashService.getSumSevenAmount(gmtCreateStart,gmtCreateEnd);
					if (sevenMoney.compareTo(sumSevenAmount) > 0) {
						logger.error("getBatchCreditInfo  error该时间段内7天资产金额不足，共"+sumSevenAmount+"元");
						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
						return creditInfos;
					}
					BigDecimal sumFourteenAmount = afViewAssetBorrowCashService.getSumFourteenAmount(gmtCreateStart,gmtCreateEnd);
					BigDecimal FourteenMoney = BigDecimalUtil.subtract(totalMoney,sevenMoney);
					if (FourteenMoney.compareTo(sumFourteenAmount) > 0){
						logger.error("getBatchCreditInfo  error该时间段内14天资产金额不足，共"+sumFourteenAmount+"元");
						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
						return creditInfos;
					}
					sevenDebtList = matchingDebt(sevenMoney,gmtCreateStart, gmtCreateEnd,AfBorrowCashType.SEVEN.getCode());
					fourteenDebtList = matchingDebt(FourteenMoney,gmtCreateStart, gmtCreateEnd,AfBorrowCashType.FOURTEEN.getCode());
				}else{
					//总的校验
					BigDecimal sumAmount = afViewAssetBorrowCashService.getSumAmount(gmtCreateStart,gmtCreateEnd);
					if (totalMoney.compareTo(sumAmount) > 0) {
						logger.error("getBatchCreditInfo  error,该时间段内资产总金额不足，共"+sumAmount+"元");
						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
						return creditInfos;
					}
					//初始化7天金额
					sevenMoney = BigDecimal.ZERO;
					List<AfViewAssetBorrowCashDo> debtList = matchingDebt(totalMoney,gmtCreateStart, gmtCreateEnd,null);
					for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : debtList) {
						if (AfBorrowCashType.SEVEN.getCode().equals(afViewAssetBorrowCashDo.getType())) {
							sevenDebtList.add(afViewAssetBorrowCashDo);
							sevenMoney=BigDecimalUtil.add(sevenMoney,afViewAssetBorrowCashDo.getAmount());
						}
						if (AfBorrowCashType.FOURTEEN.getCode().equals(afViewAssetBorrowCashDo.getType())) {
							fourteenDebtList.add(afViewAssetBorrowCashDo);
						}
					}
				}
				
				//生成资产包
				Date currDate = new Date();
				//资产方操作日志添加TODO
				
				
				AfAssetPackageDo afAssetPackageDo = new AfAssetPackageDo();
				afAssetPackageDo.setGmtCreate(currDate);
				afAssetPackageDo.setGmtModified(currDate);
				afAssetPackageDo.setStatus(AfAssetPackageStatus.SENDED.getCode());
				afAssetPackageDo.setAssetName(afAssetSideInfoDo.getName()+totalMoney.intValue()/10000+"万资产包"+DateUtil.formatDate(new Date()));
				afAssetPackageDo.setAssetNo("zcb"+System.currentTimeMillis());
				afAssetPackageDo.setAssetSideId(afAssetSideInfoDo.getRid());
				afAssetPackageDo.setBeginTime(gmtCreateStart);
				afAssetPackageDo.setEndTime(gmtCreateEnd);
				afAssetPackageDo.setTotalMoney(totalMoney);
				afAssetPackageDo.setSevenMoney(sevenMoney);
				afAssetPackageDo.setSevenNum(sevenDebtList.size());
				afAssetPackageDo.setFourteenMoney(BigDecimalUtil.subtract(totalMoney, sevenMoney));
				afAssetPackageDo.setFourteenNum(fourteenDebtList.size());
				afAssetPackageDo.setBorrowRate(afAssetSideInfoDo.getBorrowRate());
				afAssetPackageDo.setAnnualRate(afAssetSideInfoDo.getAnnualRate());
				afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepaytType());
				afAssetPackageDao.saveRecord(afAssetPackageDo);
				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : sevenDebtList) {
					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
					afAssetPackageDetailDo.setGmtCreate(currDate);
					afAssetPackageDetailDo.setGmtModified(currDate);
					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
				}
				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : fourteenDebtList) {
					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
					afAssetPackageDetailDo.setGmtCreate(new Date());
					afAssetPackageDetailDo.setGmtModified(new Date());
					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
				}
				bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
			}else{
				 logger.error("saveAssetPackage  error获取锁失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
		}
		//释放锁Lock完成,结束
		return creditInfos;
	}
	
	/**
	 * 匹配债权
	 * @param amount
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @param type
	 * @return
	 */
	private List<AfViewAssetBorrowCashDo> matchingDebt(BigDecimal amount, Date gmtCreateStart,Date gmtCreateEnd,String type) {
		//匹配初始记录;
		Integer limitNums = BigDecimalUtil.divide(amount, new BigDecimal(1500)).intValue();
		AfViewAssetBorrowCashQuery query = new AfViewAssetBorrowCashQuery();
		query.setGmtCreateStart(gmtCreateStart);
		query.setGmtCreateEnd(gmtCreateEnd);
		query.setType(type);
		query.setLimitNums(limitNums == 0 ? 1 : limitNums);
		List<AfViewAssetBorrowCashDo> debtList = afViewAssetBorrowCashService.getListByQueryCondition(query);
		query.setMinBorrowCashId(debtList.get(debtList.size()-1).getBorrowCashId());
		BigDecimal checkAmount=afViewAssetBorrowCashService.checkAmount(query);
		if (checkAmount.compareTo(amount) < 0) {
			//初始金额不足，逐个债权补充记录直到刚好满足
			query.setMinBorrowCashId(debtList.get(debtList.size()-1).getBorrowCashId());
			while (checkAmount.compareTo(amount) < 0) {
				AfViewAssetBorrowCashDo afViewAssetBorrowCashDo = afViewAssetBorrowCashService.getByQueryCondition(query);
				debtList.add(afViewAssetBorrowCashDo);
				query.setMinBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
				checkAmount = afViewAssetBorrowCashService.checkAmount(query);
			}
		}else{
			//初始金额足够，逐个减少债权记录直到刚好满足
			while (checkAmount.compareTo(amount) >= 0) {
				checkAmount = afViewAssetBorrowCashService.checkAmount(query);
			    debtList.remove(debtList.size()-1);
				query.setMinBorrowCashId(debtList.get(debtList.size()-1).getBorrowCashId());
			}
			AfViewAssetBorrowCashDo afViewAssetBorrowCashDo = afViewAssetBorrowCashService.getByQueryCondition(query);
			debtList.add(afViewAssetBorrowCashDo);
			checkAmount=BigDecimalUtil.add(checkAmount,afViewAssetBorrowCashDo.getAmount());
		}
		return debtList;
	}
}