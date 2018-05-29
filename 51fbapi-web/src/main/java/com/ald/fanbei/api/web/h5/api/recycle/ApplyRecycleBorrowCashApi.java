package com.ald.fanbei.api.web.h5.api.recycle;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleGoodsService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.impl.AfBorrowRecycleServiceImpl.ApplyCheckBo;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.common.RiskResultCode;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyRecycleBorrowCashParam;

/**
 * @author Guosq 2017年3月25日下午1:06:18 optimize by ZJF
 * @类描述：回收申请
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowRecycleCashApi")
@Validator("applyRecycleBorrowCashParam")
public class ApplyRecycleBorrowCashApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	ApplyLegalBorrowCashService applyLegalBorrowCashService;
	@Resource
	AfBorrowRecycleService afBorrowRecycleService;
	@Resource
	AfBorrowRecycleGoodsService recycleGoodsService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	TransactionTemplate transactionTemplate;
	
	@Resource
	JpushService jpushService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	RiskUtil riskUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	// [end]

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		String reqId = context.getId();
		final String appName = reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length());
		String appType = reqId.startsWith("i") ? "alading_ios" : "alading_and";
		final Long userId = context.getUserId();
		String ipAddress = context.getClientIp();
		
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		applyLegalBorrowCashService.checkLock(lockKey);// 业务加锁处理
		try {
			ApplyRecycleBorrowCashParam param = (ApplyRecycleBorrowCashParam) context.getParamEntity();
			final ApplyLegalBorrowCashBo paramBo =  new ApplyLegalBorrowCashBo();
			BeanUtil.copyProperties(paramBo, param);
			paramBo.setIpAddress(ipAddress);
			paramBo.setAppName(appName);
			
			final AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
			final AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
			final AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, AfResourceSecType.BORROW_RECYCLE_INFO_LEGAL_NEW.getCode());
			final AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);// 获取主卡信息
			final ApplyCheckBo applyCheckBo = new ApplyCheckBo();
			
			transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					applyLegalBorrowCashService.checkRecycleBusi(accountDo, authDo, rateInfoDo, mainCard, paramBo); // 业务逻辑校验
		
					applyCheckBo.afBorrowCashDo = applyLegalBorrowCashService.buildRecycleBorrowCashDo(mainCard, userId, rateInfoDo, paramBo);
					applyCheckBo.afBorrowCashDo.setMajiabaoName(appName);// 用户借钱时app来源区分
					AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_RECYCLE_INFO_LEGAL_NEW.getCode());
					Map<String, Object> map = afResourceService.getRateInfo(afResourceDo.getValue2(),paramBo.getType(),"borrow","BORROW_RECYCLE_INFO_LEGAL_NEW");
					AfBorrowRecycleOrderDo recycleOrderDo = new AfBorrowRecycleOrderDo();
					recycleOrderDo.setPropertyValue(URLDecoder.decode(paramBo.getPropertyValue()));
					recycleOrderDo.setUserId(userId);
					recycleOrderDo.setGoodsImg(paramBo.getGoodsImg());
					recycleOrderDo.setGoodsName(paramBo.getGoodsName());
					recycleOrderDo.setOverdueRate(new BigDecimal((Double) map.get("overdueRate")));
					applyCheckBo.borrowId = afBorrowRecycleService.addBorrowRecord(applyCheckBo.afBorrowCashDo, recycleOrderDo);
					return 1l;
				}
			});
			
			final AfBorrowCashDo afBorrowCashDo = applyCheckBo.afBorrowCashDo;
			Long borrowId = applyCheckBo.borrowId;
			
			RiskVerifyRespBo verifyBo;
			try {
				bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(userId));// 借过款的放入缓存，借钱按钮不需要高亮显示
				
				String cardNo = mainCard.getCardNumber();
				String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
				applyLegalBorrowCashService.updateBorrowStatus2Apply(borrowId, riskOrderNo);

				verifyBo = applyLegalBorrowCashService.submitRiskReview(borrowId, appType, ipAddress, paramBo, accountDo, userId, afBorrowCashDo, riskOrderNo);
				
				if((verifyBo.isSuccess() && StringUtils.equals(RiskResultCode.PASS.code, verifyBo.getResult())) 
						|| afResourceService.getBorrowCashWhiteList().contains(context.getUserName()) ) {
					afBorrowCashDo.setReviewStatus(RiskReviewStatus.AGREE.getCode());
				} else {
					if (verifyBo.getRejectCode().equals("104") && verifyBo.getResult().equals("30")){
						throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
					}
					throw new FanbeiException("weak risk direct fail, msg=" + verifyBo.getMsg());
				}
			} catch (Exception e) {
				logger.error("apply legal borrow cash error", e);
				String msg = e instanceof FanbeiException ? e.getMessage(): "弱风控认证存在捕获外异常";
				dealWeakRiskFail(borrowId, userId, appType, msg);
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
			
			jpushService.dealBorrowCashApplySuccss(context.getUserName(), new Date());
			
			afBorrowCashDo.setStatus(AfBorrowCashStatus.transeding.getCode());
			afBorrowCashService.updateBorrowCash(afBorrowCashDo);
			
			try {
				applyLegalBorrowCashService.recycleDelegatePay(userId, verifyBo.getOrderNo(), mainCard, afBorrowCashDo);
			}catch (Exception e) {
				logger.error("upsResult error", e);
				afBorrowCashDo.setStatus(AfBorrowCashStatus.transedfail.getCode());
				afBorrowCashService.updateBorrowCash(afBorrowCashDo);
				throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
			}
			
			this.notifyUserSucc(context.getUserName(), mainCard);
			
			transactionTemplate.execute(new TransactionCallback<String>() {
				@Override
				public String doInTransaction(TransactionStatus status) {
					try {
						afBorrowCashService.updateAuAmountByRid(afBorrowCashDo.getRid(), afUserAccountService.getAuAmountByUserId(userId));
						afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash, afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid()));
					} catch (Exception e) {
						logger.error("recycleDelegatePay secFail, msg=" + e);
					}
						
					afUserAccountSenceService.syncLoanUsedAmount(userId, SceneType.CASH, afBorrowCashDo.getAmount());
					afBorrowCashService.updateBorrowCash(afBorrowCashDo);
					applyLegalBorrowCashService.addTodayTotalAmount(Integer.parseInt(DateUtil.getNowYearMonthDay()), afBorrowCashDo.getAmount());
					return "success";
				}
			});
			
			return resp;
		} finally {
			bizCacheUtil.delCache(lockKey);
		}
	}
	
	private void dealWeakRiskFail(Long borrowId, Long userId, String appType, String msg) {
		try{
			// 风控拒绝，更新借款状态
			AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
			delegateBorrowCashDo.setRid(borrowId);
			delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
			delegateBorrowCashDo.setReviewDetails(msg);
			// 更新订单状态为关闭
			applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo);
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("extend_koudai");
		
			Date currDate = new Date();
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if(afResourceDo!=null && afResourceDo.getValue().equals("Y") && afResourceDo.getValue4().contains(appType)){
				jpushService.dealBorrowCashApplyFailForKoudai(afUserDo.getUserName(), currDate,afResourceDo.getValue1()); // TODO 修改为回收
				if (afResourceDo.getValue3().contains(afUserDo.getUserName().substring(0, 3))) {
					smsUtil.sendMarketingSmsToDhstForEC(afUserDo.getUserName(), afResourceDo.getValue2());
				}
			}else{
				jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
			}
		}catch (Exception e){
			logger.error("dealWeakRiskFail error", e);
		}
	}
	
	private void notifyUserSucc(String userName, AfUserBankcardDo mainCard) {
		String bankNumber = mainCard.getCardNumber();
		String lastBank = bankNumber.substring(bankNumber.length() - 4);
		smsUtil.sendBorrowCashCode(userName, lastBank);
		String title = "恭喜您，审核通过啦！";
		String msgContent = "您的借款审核通过，请留意您尾号&bankCardNo的银行卡资金变动，请注意按时还款，保持良好的信用记录。";
		msgContent = msgContent.replace("&bankCardNo", lastBank);
		jpushService.pushUtil(title, msgContent, userName);
	}
}
