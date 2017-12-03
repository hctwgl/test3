package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;
import com.ald.fanbei.api.biz.service.AfViewAssetBorrowCashService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfAssetOperaLogChangeType;
import com.ald.fanbei.api.common.enums.AfAssetPackageDetailStatus;
import com.ald.fanbei.api.common.enums.AfAssetPackageRepaymentType;
import com.ald.fanbei.api.common.enums.AfAssetPackageSendMode;
import com.ald.fanbei.api.common.enums.AfAssetPackageStatus;
import com.ald.fanbei.api.common.enums.AfAssetPackageType;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDetailDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideOperaLogDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
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
    private AfBorrowCashDao  afBorrowCashDao;
	@Resource
	private AfViewAssetBorrowCashService  afViewAssetBorrowCashService;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Override
	public BaseDao<AfAssetPackageDetailDo, Long> getDao() {
		return afAssetPackageDetailDao;
	}
		
	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 */
	@Override
	public int batchGiveBackCreditInfo(final AfAssetSideInfoDo afAssetSideInfoDo,final List<String> orderNos){
		//更新成功的信息
		List<String> successPackageIds = new ArrayList<String>();
		List<String> successPackageDetailIds = new ArrayList<String>();
		//更新失败的信息
		List<String> failPackageIds = new ArrayList<String>();
		List<String> failPackageDetailIds = new ArrayList<String>();
		//不存在的记录
		List<String> invalidBorrowNos = new ArrayList<String>();
		//成功的总金额
		final BigDecimal totalMoney = BigDecimal.ZERO;
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
		        		
			    		//获取borrowCash
			    		AfBorrowCashDo borrowCash = afBorrowCashDao.getBorrowCashByrid(afAssetPackageDetail.getBorrowCashId());
			    		if(borrowCash==null){
			    			logger.error("batchGiveBackCreditInfo error ,borrowCash not exists,id="+afAssetPackageDetail.getBorrowCashId());
			    			return 0;
			    		}
			    		
			    		//更新此债权相关明细
			    		int effectNums = afAssetPackageDetailDao.invalidPackageDetail(afAssetPackageDetail.getRid());
			    		
			    		if(effectNums>0){
			    			totalMoney.add(borrowCash.getAmount());
			    			AfAssetPackageDo modifyPackageDo = new AfAssetPackageDo();
			    			modifyPackageDo.setRid(packageDo.getRid());
			    			modifyPackageDo.setGmtModified(currDate);
			    			modifyPackageDo.setRealTotalMoney(borrowCash.getAmount().negate());
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
		
		String refPackageId = StringUtil.joinListToString(successPackageIds, ",")+";"+StringUtil.joinListToString(failPackageIds, ",");
		String refDetailIds = StringUtil.joinListToString(successPackageDetailIds, ",")+";"+StringUtil.joinListToString(failPackageDetailIds, ",")+";"+StringUtil.joinListToString(invalidBorrowNos, ",");
		//资产方操作日志添加
		AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), totalMoney, refPackageId,refDetailIds, "成功退回债权金额："+totalMoney+"元");
		afAssetSideOperaLogDao.saveRecord(operaLogDo);
		//end事务
		if(failPackageIds!=null && failPackageIds.size()>0){
			return 0;
		}else{
			return 1;
		}
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
		        	//资产方操作日志添加
	    			AfAssetSideOperaLogDo operaLogDo = null;
		        	//更新此债权相关明细
		    		int effectNums = afAssetPackageDetailDao.invalidPackageDetail(afAssetPackageDetail.getRid());
		    		
		    		if(effectNums>0){
		    			AfAssetPackageDo modifyPackageDo = new AfAssetPackageDo();
		    			modifyPackageDo.setRid(packageDo.getRid());
		    			modifyPackageDo.setGmtModified(currDate);
		    			modifyPackageDo.setRealTotalMoney(borrowCash.getAmount().negate());
		    			afAssetPackageDao.updateRealTotalMoneyById(modifyPackageDo);
		    			operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), borrowCash.getAmount(), packageDo.getRid()+";",afAssetPackageDetail.getRid()+";", "成功退回债权金额："+borrowCash.getAmount()+"元");
		    			afAssetSideOperaLogDao.saveRecord(operaLogDo);
		    			return 1;
		    		}else{
		    			operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GIVE_BACK.getCode(), borrowCash.getAmount(), ";"+packageDo.getRid(),";"+afAssetPackageDetail.getRid(), "成功退回债权金额：0元");
		    			afAssetSideOperaLogDao.saveRecord(operaLogDo);
		    			logger.error("giveBackCreditInfo update AfAssetPackageDetailDo fail ,id="+afAssetPackageDetail.getBorrowCashId());
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
	 * 根据资产方要求,获取资产方对应的债权信息
	 */
	@Override
	public List<EdspayGetCreditRespBo> getBatchCreditInfo(final FanbeiBorrowBankInfoBo bankInfo,final AfAssetSideInfoDo afAssetSideInfoDo,final BigDecimal totalMoney,final Date gmtCreateStart,final Date gmtCreateEnd,final BigDecimal sevenMoney){
		final List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		transactionTemplate.execute(new TransactionCallback<Long>() {
	        @Override
            public Long doInTransaction(TransactionStatus status) {
            	BigDecimal sevenMoneyNew = sevenMoney;
            	List<AfViewAssetBorrowCashDo> sevenDebtList= new ArrayList<AfViewAssetBorrowCashDo>();
       		    List<AfViewAssetBorrowCashDo> fourteenDebtList=new ArrayList<AfViewAssetBorrowCashDo>();
            	try {
        			//加锁Lock
        			boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(Constants.CACHEKEY_ASSETPACKAGE_LOCK, Constants.CACHEKEY_ASSETPACKAGE_LOCK_VALUE,10, Constants.SECOND_OF_FIFTEEN);
        			//校验现在金额是否满足
        			if (isLock) {
        				//分配债权资产包
        				BigDecimal fourteenMoney = BigDecimal.ZERO;
        				if (sevenMoneyNew!=null) {
        					//区分7天和14天校验
        					BigDecimal sumSevenAmount = afViewAssetBorrowCashService.getSumSevenAmount(gmtCreateStart,gmtCreateEnd);
        					if (sevenMoneyNew.compareTo(sumSevenAmount) > 0) {
        						logger.error("getBatchCreditInfo  error该时间段内7天资产金额不足，共"+sumSevenAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
        						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        						return 0L;
        					}
        					BigDecimal sumFourteenAmount = afViewAssetBorrowCashService.getSumFourteenAmount(gmtCreateStart,gmtCreateEnd);
        					fourteenMoney = BigDecimalUtil.subtract(totalMoney,sevenMoneyNew);
        					if (fourteenMoney.compareTo(sumFourteenAmount) > 0){
        						logger.error("getBatchCreditInfo  error该时间段内14天资产金额不足，共"+sumFourteenAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
        						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        						return 0L;
        					}
        					sevenDebtList = matchingDebt(sevenMoneyNew,gmtCreateStart, gmtCreateEnd,AfBorrowCashType.SEVEN.getName());
        					fourteenDebtList = matchingDebt(fourteenMoney,gmtCreateStart, gmtCreateEnd,AfBorrowCashType.FOURTEEN.getName());
        				}else{
        					//初始化7天金额
        					sevenMoneyNew = BigDecimal.ZERO;
        					//总的校验
        					BigDecimal sumAmount = afViewAssetBorrowCashService.getSumAmount(gmtCreateStart,gmtCreateEnd);
        					if (totalMoney.compareTo(sumAmount) > 0) {
        						logger.error("getBatchCreditInfo  error,该时间段内资产总金额不足，共"+sumAmount+"元"+",afAssetSideInfoDoId="+afAssetSideInfoDo.getRid());
        						bizCacheUtil.delCache(Constants.CACHEKEY_ASSETPACKAGE_LOCK);
        						return 0L;
        					}
        					
        					List<AfViewAssetBorrowCashDo> debtList = matchingDebt(totalMoney,gmtCreateStart, gmtCreateEnd,null);
        					for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : debtList) {
        						if (AfBorrowCashType.SEVEN.getName().equals(afViewAssetBorrowCashDo.getType())) {
        							sevenDebtList.add(afViewAssetBorrowCashDo);
        						}
        						if (AfBorrowCashType.FOURTEEN.getName().equals(afViewAssetBorrowCashDo.getType())) {
        							fourteenDebtList.add(afViewAssetBorrowCashDo);
        						}
        					}
        				}
        				
        				//生成资产包
        				Date currDate = new Date();
        				AfAssetPackageDo afAssetPackageDo = new AfAssetPackageDo();
        				afAssetPackageDo.setGmtCreate(currDate);
        				afAssetPackageDo.setGmtModified(currDate);
        				afAssetPackageDo.setStatus(AfAssetPackageStatus.SENDED.getCode());
        				String packageName = "";
        				if(totalMoney.intValue()/10000>0){
        					packageName = afAssetSideInfoDo.getName()+totalMoney.intValue()/10000+"万资产包"+DateUtil.formatDate(new Date());
        				}else{
        					packageName = afAssetSideInfoDo.getName()+totalMoney+"元资产包"+DateUtil.formatDate(new Date());
        				}
        				afAssetPackageDo.setAssetName(packageName);
        				afAssetPackageDo.setAssetNo("zcb"+System.currentTimeMillis());
        				afAssetPackageDo.setAssetSideId(afAssetSideInfoDo.getRid());
        				afAssetPackageDo.setBeginTime(gmtCreateStart);
        				afAssetPackageDo.setEndTime(gmtCreateEnd);
        				afAssetPackageDo.setTotalMoney(totalMoney);
        				afAssetPackageDo.setSevenMoney(sevenMoneyNew);
        				afAssetPackageDo.setSevenNum(sevenDebtList.size());
        				afAssetPackageDo.setFourteenMoney(fourteenMoney);
        				afAssetPackageDo.setFourteenNum(fourteenDebtList.size());
        				afAssetPackageDo.setBorrowRate(afAssetSideInfoDo.getBorrowRate());
        				afAssetPackageDo.setAnnualRate(afAssetSideInfoDo.getAnnualRate());
        				afAssetPackageDo.setRepaymentMethod(afAssetSideInfoDo.getRepaytType());
        				afAssetPackageDo.setValidStatus(YesNoStatus.YES.getCode());
        				afAssetPackageDo.setSendMode(AfAssetPackageSendMode.INTERFACE.getCode());
        				afAssetPackageDo.setType(AfAssetPackageType.ASSET_REQ.getCode());
        				afAssetPackageDao.saveRecord(afAssetPackageDo);
        				
        				BigDecimal realSevenAmount = BigDecimal.ZERO;
        				BigDecimal realFourteenAmount = BigDecimal.ZERO;
        				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : sevenDebtList) {
        					realSevenAmount = realSevenAmount.add(afViewAssetBorrowCashDo.getAmount());
        					creditInfos.add(buildCreditRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo));
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(currDate);
        					afAssetPackageDetailDo.setGmtModified(currDate);
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowCashId());
        				}
        				for (AfViewAssetBorrowCashDo afViewAssetBorrowCashDo : fourteenDebtList) {
        					realFourteenAmount = realFourteenAmount.add(afViewAssetBorrowCashDo.getAmount());
        					creditInfos.add(buildCreditRespBo(afAssetPackageDo,bankInfo,afViewAssetBorrowCashDo));
        					AfAssetPackageDetailDo afAssetPackageDetailDo = new AfAssetPackageDetailDo();
        					afAssetPackageDetailDo.setGmtCreate(new Date());
        					afAssetPackageDetailDo.setGmtModified(new Date());
        					afAssetPackageDetailDo.setBorrowCashId(afViewAssetBorrowCashDo.getBorrowCashId());
        					afAssetPackageDetailDo.setBorrowNo(afViewAssetBorrowCashDo.getBorrowNo());
        					afAssetPackageDetailDo.setAssetPackageId(afAssetPackageDo.getRid());
        					afAssetPackageDetailDo.setStatus(AfAssetPackageDetailStatus.VALID.getCode());
        					afAssetPackageDetailDao.saveRecord(afAssetPackageDetailDo);
        					//标记重新分配记录
        					afAssetPackageDetailDao.updateReDisTri(afViewAssetBorrowCashDo.getBorrowCashId());
        				}
        				
        				//更新实际金额
        				afAssetPackageDo.setRealTotalMoney(realSevenAmount.add(realFourteenAmount));
        				afAssetPackageDao.updateById(afAssetPackageDo);
        				
        				//资产方操作日志添加
        				AfAssetSideOperaLogDo operaLogDo = new AfAssetSideOperaLogDo(afAssetSideInfoDo.getRid(), currDate, AfAssetOperaLogChangeType.GET_ASSET.getCode(), afAssetPackageDo.getRealTotalMoney(), afAssetPackageDo.getRid()+"","", "请求资产包金额totalMoney="+totalMoney+",实际7天："+realSevenAmount+",14天"+realFourteenAmount);
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
	
	private EdspayGetCreditRespBo buildCreditRespBo(AfAssetPackageDo afAssetPackageDo,FanbeiBorrowBankInfoBo bankInfo,AfViewAssetBorrowCashDo afViewAssetBorrowCashDo){
		Integer timeLimt = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afViewAssetBorrowCashDo.getType()).getCode(), 7);
		AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo(afAssetPackageDo.getAssetNo(), afViewAssetBorrowCashDo.getBorrowNo(), 
				afViewAssetBorrowCashDo.getUserId(), afViewAssetBorrowCashDo.getRealName(), afViewAssetBorrowCashDo.getIdNumber(),afViewAssetBorrowCashDo.getMobile(),
				afViewAssetBorrowCashDo.getCardNumber(), bankInfo.getAcctName(), afViewAssetBorrowCashDo.getAmount(), afAssetPackageDo.getBorrowRate(),
				timeLimt, DateUtil.getSpecSecondTimeStamp(afViewAssetBorrowCashDo.getGmtArrival()), 0, 
				0, repayTypeEnum!=null?repayTypeEnum.getEdsCode():afAssetPackageDo.getRepaymentMethod(), bankInfo.getRepayName(), bankInfo.getRepayAcct(), bankInfo.getRepayAcctBankNo(), bankInfo.getRepayAcctType(), 
				bankInfo.getIsRepayAcctOtherBank(), afAssetPackageDo.getAnnualRate());
		return creditRespBo;
	}
}