package com.ald.fanbei.api.web.api.legalborrowV2;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.RiskReviewStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.util.LogUtil;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyLegalBorrowCashV2Api")
@Validator("applyLegalBorrowCashParam")
public class ApplyLegalBorrowCashV2Api extends GetBorrowCashBase implements ApiHandle {

	// [start] 依赖注入
	@Resource
	AfBorrowCashService afBorrowCashService;
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

	// [end]
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		ApplyLegalBorrowCashParam param = (ApplyLegalBorrowCashParam) requestDataVo.getParamObj();

		ApplyLegalBorrowCashBo paramBo =  new ApplyLegalBorrowCashBo();
		
		BeanUtil.copyProperties(paramBo,param);
		
		// 获取用户账户和认证信息
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);

		// 获取后台配置借款利率信息
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL);
		// 获取主卡信息
		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		// 业务逻辑校验
		applyLegalBorrowCashService.checkBusi(accountDo, authDo, rateInfoDo, mainCard, paramBo);
		try {
			// 业务加锁处理
			applyLegalBorrowCashService.checkLock(lockKey);
			
			AfBorrowCashDo afBorrowCashDo = applyLegalBorrowCashService.buildBorrowCashDo(
					mainCard, userId, rateInfoDo, paramBo);
			// 用户借钱时app来源区分
			String appName = getAppName(requestDataVo);
			afBorrowCashDo.setMajiabaoName(appName);
			// 搭售商品订单
			AfBorrowLegalOrderDo afBorrowLegalOrderDo = applyLegalBorrowCashService.buildBorrowLegalOrder(userId,
					paramBo);

			Long borrowId = applyLegalBorrowCashService.addBorrowResult(afBorrowCashDo,afBorrowLegalOrderDo);
			// 生成借款信息失败
			if (borrowId == null) {
				throw new FanbeiException(FanbeiExceptionCode.ADD_BORROW_CASH_INFO_FAIL);
			}
			// 借过款的放入缓存，借钱按钮不需要高亮显示
			bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(userId));
			
			String appType = getAppType(requestDataVo);
			String ipAddress = CommonUtil.getIpAddr(request);
			final AfBorrowCashDo cashDo = new AfBorrowCashDo();
			try {
				String cardNo = mainCard.getCardNumber();
				String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
				cashDo.setRid(borrowId);
				cashDo.setUserId(userId);
				cashDo.setGmtModified(new Date());
				cashDo.setRishOrderNo(riskOrderNo);
				cashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
				afBorrowCashService.updateBorrowCash(cashDo);
				// 提交风控审核
				RiskVerifyRespBo verifyBo = applyLegalBorrowCashService.submitRiskReview(borrowId,appType,ipAddress,paramBo,
						accountDo,userId,afBorrowCashDo,riskOrderNo);
				
				if (verifyBo.isSuccess()) {
					applyLegalBorrowCashService.delegatePay(verifyBo.getConsumerNo(), verifyBo.getOrderNo(),
							verifyBo.getResult(), afBorrowLegalOrderDo, mainCard);
					// 增加借款埋点信息
					LogUtil.doMaidianLog(request, afBorrowCashDo, requestDataVo, context);
				} else {
					// 风控拒绝
					cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					cashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
					// 更新订单状态为关闭
					afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
					afBorrowLegalOrderDo.setClosedDetail("risk refuse");
					afBorrowLegalOrderDo.setGmtClosed(new Date());
					applyLegalBorrowCashService.updateBorrowStatus(cashDo,afBorrowLegalOrderDo);
				}
				return resp;
			} catch (Exception e) {
				logger.error("apply legal borrow cash  error", e);
				// 关闭借款
				cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				// 关闭搭售商品订单
				afBorrowLegalOrderDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());

				cashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				// 如果属于非返呗自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
				if (e instanceof FanbeiException) {
					cashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				} else {
					logger.error("apply legal borrow cash exist unexpected exception,cause:" + e.getCause());
					cashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
					cashDo.setReviewDetails("弱风控认证存在捕获外异常");
				}
				applyLegalBorrowCashService.updateBorrowStatus(cashDo,afBorrowLegalOrderDo);
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
		} finally {
			bizCacheUtil.delCache(lockKey);
		}
	}
	
	private String getAppName(RequestDataVo requestDataVo) {
		String appName = requestDataVo.getId().substring(requestDataVo.getId().lastIndexOf("_") + 1,
				requestDataVo.getId().length());
		return appName;
	}

	private String getAppType(RequestDataVo requestDataVo) {
		return (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
	}


}
