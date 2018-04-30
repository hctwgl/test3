package com.ald.fanbei.api.web.h5.api.recycle;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.RiskReviewStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.util.LogUtil;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;
import com.ald.fanbei.api.web.validator.bean.ApplyRecycleBorrowCashParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱V2
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyRecycleBorrowCashApi")
@Validator("applyRecycleBorrowCashParam")
public class ApplyRecycleBorrowCashApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	ApplyLegalBorrowCashService applyLegalBorrowCashService;
	@Resource
	AfUserService afUserService;
	@Resource
	JpushService jpushService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfBorrowBillService afBorrowBillService;
	// [end]

	private void doMaidianLog(HttpServletRequest request, AfBorrowCashDo afBorrowCashDo, RequestDataVo requestDataVo,
			FanbeiContext context) {
		String ext1 = afBorrowCashDo.getBorrowNo();
		String ext2 =  ObjectUtils.toString(afBorrowCashDo.getUserId());
		String ext3 =  ObjectUtils.toString(afBorrowCashDo.getAmount());
		String ext4 =  ObjectUtils.toString(context.getAppVersion());
		LogUtil.doMaidianLog(request, afBorrowCashDo, requestDataVo, context, ext1, ext2, ext3, ext4);
	}

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		String reqId = context.getId();
		Long userId = context.getUserId();
		String appName = reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length());
		String appType = reqId.startsWith("i") ? "alading_ios" : "alading_and";
		String ipAddress = context.getClientIp();
		// 获取客户端请求参数
		ApplyRecycleBorrowCashParam param = (ApplyRecycleBorrowCashParam) context.getParamEntity();
		try{
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("enabled_type_borrow");//是否允许这种类型的借款
			if(afResourceDo!=null&&afResourceDo.getValue().equals(YesNoStatus.YES.getCode())&&afResourceDo.getValue1().contains(param.getType())){
				throw new FanbeiException(afResourceDo.getValue2(),true);
			}
		}catch (FanbeiException e){
			throw e;

		}catch (Exception e){
			logger.error("enabled_type_borrow error",e);
		}
		ApplyLegalBorrowCashBo paramBo =  new ApplyLegalBorrowCashBo();

		BeanUtil.copyProperties(paramBo,param);
		paramBo.setIpAddress(ipAddress);
		paramBo.setAppName(appName);
		// 获取用户账户和认证信息
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);

		// 获取后台配置借款利率信息
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL_NEW);
		// 获取主卡信息
		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
		// 业务逻辑校验
		applyLegalBorrowCashService.checkBusi(accountDo, authDo, rateInfoDo, mainCard, paramBo);
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		try {
			// 业务加锁处理
			applyLegalBorrowCashService.checkLock(lockKey);

			final AfBorrowCashDo afBorrowCashDo = applyLegalBorrowCashService.buildRecycleBorrowCashDo(
					mainCard, userId, rateInfoDo, paramBo);
			// 用户借钱时app来源区分
			afBorrowCashDo.setMajiabaoName(appName);
			// 数据库中新增借钱记录
			Long borrowId = applyLegalBorrowCashService.addBorrowRecord(afBorrowCashDo);
			// 生成借款信息失败
			applyLegalBorrowCashService.checkGenRecordError(borrowId);

			// 借过款的放入缓存，借钱按钮不需要高亮显示
			bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(userId));
			try {
				String cardNo = mainCard.getCardNumber();
				String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
				// 更新风控审核状态未待审核
				applyLegalBorrowCashService.updateBorrowStatus2Apply(borrowId,riskOrderNo);

				// 提交风控审核
				RiskVerifyRespBo verifyBo = applyLegalBorrowCashService.submitRiskReview(borrowId,appType,ipAddress,paramBo,
						accountDo,userId,afBorrowCashDo,riskOrderNo);
				// 判断是否在风控白名单里面
				List<String> whiteIdsList = new ArrayList<String>();
				final int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
				AfUserDo afUserDo = afUserService.getUserById(userId);
				// 判断是否在风控白名单里面
				AfResourceDo whiteListInfo = afResourceService.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
				if (whiteListInfo != null) {
					whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","),
							new Converter<String, String>() {
								@Override
								public String convert(String source) {
									return source.trim();
								}
							});
				}
				Date currDate = new Date();
				if((verifyBo.isSuccess()&&StringUtils.equals("10", verifyBo.getResult()))||whiteIdsList.contains(afUserDo.getUserName())) {
					applyLegalBorrowCashService.delegatePay(verifyBo.getConsumerNo(), verifyBo.getOrderNo(),
							verifyBo.getResult(), mainCard, afBorrowCashDo);
					// 增加借款埋点信息
//					doMaidianLog(httpServletRequest, afBorrowCashDo, requestDataVo, context);
					//百度智能地址
					try {
						//smartAddressEngine.setScoreAsyn(afBorrowLegalOrderDo.getAddress(),borrowId,afBorrowLegalOrderDo.getOrderNo());
					}catch (Exception e){
						logger.info("smart address {}",e);
					}
				} else {
					// 风控拒绝，更新借款状态
					AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
					delegateBorrowCashDo.setRid(borrowId);
					delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
					// 更新订单状态为关闭
					applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo);
					AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("extend_koudai");
					try{
						if(afResourceDo!=null&&afResourceDo.getValue().equals("Y")&&afResourceDo.getValue4().contains(appType)){
							jpushService.dealBorrowCashApplyFailForKoudai(afUserDo.getUserName(), currDate,afResourceDo.getValue1());
							if (afResourceDo.getValue3().contains(afUserDo.getUserName().substring(0, 3))) {
								smsUtil.sendMarketingSmsToDhstForEC(afUserDo.getUserName(), afResourceDo.getValue2());
							}
						}else{
							jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
						}
					}catch (Exception e){
						logger.error("push legal borrow cash error", e);
					}
					Integer countUnpayOverDue= afBorrowBillService.countNotPayOverdueBill(afUserDo.getRid());
					if(countUnpayOverDue>0){
						throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR_BORROW);
					}
					throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
				}
				return resp;
			} catch (Exception e) {
				logger.error("apply legal borrow cash error", e);
				AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
				delegateBorrowCashDo.setRid(borrowId);
				// 关闭借款
				delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				// 更新风控审核状态为拒绝
				delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				// 如果属于非返呗自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
				if (e instanceof FanbeiException) {
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				} else {
					logger.error("apply legal borrow cash exist unexpected exception,cause:{}" , e.getCause());
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
					delegateBorrowCashDo.setReviewDetails("弱风控认证存在捕获外异常");
				}
				applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo);
				Integer countUnpayOverDue= afBorrowBillService.countNotPayOverdueBill(userId);
				if(countUnpayOverDue>0){
					throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR_BORROW);
				}
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
		} finally {
			bizCacheUtil.delCache(lockKey);
		}
	}
}
