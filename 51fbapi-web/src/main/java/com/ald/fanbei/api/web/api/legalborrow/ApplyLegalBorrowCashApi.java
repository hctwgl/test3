package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.netty.util.internal.StringUtil;

/**
 * @类描述：申请借钱
 * 
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyLegalBorrowCashApi")
public class ApplyLegalBorrowCashApi extends GetBorrowCashBase implements ApiHandle {

	protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志

	@Resource
	SmsUtil smsUtil;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	JpushService jpushService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserService afUserService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;
	@Resource
	CommitRecordUtil commitRecordUtil;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
		String pwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"));
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		String latitude = ObjectUtils.toString(requestDataVo.getParams().get("latitude"));
		String longitude = ObjectUtils.toString(requestDataVo.getParams().get("longitude"));
		String province = ObjectUtils.toString(requestDataVo.getParams().get("province"));
		String city = ObjectUtils.toString(requestDataVo.getParams().get("city"));
		String county = ObjectUtils.toString(requestDataVo.getParams().get("county"));
		String address = ObjectUtils.toString(requestDataVo.getParams().get("address"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String borrowRemark = ObjectUtils.toString(requestDataVo.getParams().get("borrowRemark"));
		String refundRemark = ObjectUtils.toString(requestDataVo.getParams().get("refundRemark"));
		// 获取销售商品信息
		String goodsId = ObjectUtils.toString(requestDataVo.getParams().get("goodsId"));
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"));
		double goodsAmount = NumberUtil.objToDoubleDefault(requestDataVo.getParams().get("goodsAmount"), 0);
		// 收货地址信息
		String deliveryAddress = ObjectUtils.toString(requestDataVo.getParams().get("deliveryAddress"));
		String deliveryUser = ObjectUtils.toString(requestDataVo.getParams().get("deliveryUser"));
		String deliveryPhone = ObjectUtils.toString(requestDataVo.getParams().get("deliveryPhone"));

		if (StringUtils.isBlank(amountStr) || StringUtils.isBlank(pwd) || StringUtils.isBlank(latitude)
				|| StringUtils.isBlank(longitude) || StringUtils.isBlank(blackBox) || StringUtils.isBlank(goodsId)
				|| AfBorrowCashType.findRoleTypeByCode(type) == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}

		// 获取用户账户和认证信息
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (accountDo == null || authDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}

		// 判断用户借款金额是否在后台配置的金额范围内
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL);

		if (rateInfoDo != null) {
			BigDecimal minAmount = new BigDecimal(rateInfoDo.getValue4());
			BigDecimal maxAmount = new BigDecimal(rateInfoDo.getValue1());
			BigDecimal amount = new BigDecimal(amountStr);
			if (amount.compareTo(minAmount) < 0 || amount.compareTo(maxAmount) > 0) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
			}
		}

		// 检验支付密码
		String inputOldPwd = UserUtil.getPassword(pwd, accountDo.getSalt());
		if (!StringUtils.equals(inputOldPwd, accountDo.getPassword())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}

		// 校验用户是否绑定主卡
		if (StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}

		// 校验认证信息
		if (!StringUtils.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getFacesStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getMobileStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getTeldirStatus(), YesNoStatus.YES.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_ALL_AUTH_ERROR);
		}

		BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);
		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);

		// 获取用户可借金额
		BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
		BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
		if (accountBorrow.compareTo(borrowAmount.add(new BigDecimal(goodsAmount))) < 0) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);
		}
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		boolean isGetLock = bizCacheUtil.getLock30Second(lockKey, "1");

		if (!isGetLock) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}

		try {
			// 判断用户是否有借款未完成
			boolean isCanBorrow = afBorrowCashService.isCanBorrowCash(userId);
			if (!isCanBorrow) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
			}
			int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
			String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
			String ipAddress = CommonUtil.getIpAddr(request);
			final AfBorrowCashDo afBorrowCashDo = buildBorrowCashDo(borrowAmount, type, latitude, longitude, mainCard,
					city, province, county, address, userId, currentDay, rateInfoDo);
			// 用户借钱时app来源区分
			String majiabaoName = requestDataVo.getId().substring(requestDataVo.getId().lastIndexOf("_") + 1,
					requestDataVo.getId().length());
			afBorrowCashDo.setMajiabaoName(majiabaoName);

			// 搭售商品订单
			final AfBorrowLegalOrderDo afBorrowLegalOrderDo = buildBorrowLegalOrder(new BigDecimal(goodsAmount), userId,
					0l, Long.parseLong(goodsId), goodsName, deliveryAddress, deliveryUser, deliveryPhone);

			// 订单借款
			final AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = buildBorrowLegalOrderCashDo(
					new BigDecimal(goodsAmount), type, userId, 0l, BigDecimal.ZERO, 0l, BigDecimal.ZERO, borrowRemark,
					refundRemark, rateInfoDo);

			Long borrowId = transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus arg0) {
					afBorrowCashService.addBorrowCash(afBorrowCashDo);
					Long borrowId = afBorrowCashDo.getRid();
					afBorrowLegalOrderDo.setBorrowId(borrowId);
					// 新增搭售商品订单
					afBorrowLegalOrderService.saveBorrowLegalOrder(afBorrowLegalOrderDo);
					Long orderId = afBorrowLegalOrderDo.getRid();
					afBorrowLegalOrderCashDo.setBorrowId(borrowId);
					afBorrowLegalOrderCashDo.setBorrowLegalOrderId(orderId);
					afBorrowLegalOrderCashService.saveBorrowLegalOrderCash(afBorrowLegalOrderCashDo);
					return borrowId;
				}
			});
			// 生成借款信息失败
			if (borrowId == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ADD_BORROW_CASH_INFO_FAIL);
			}
			// 借过款的放入缓存，借钱按钮不需要高亮显示
			bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(userId));
			final AfBorrowCashDo cashDo = new AfBorrowCashDo();
			cashDo.setRid(borrowId);
			try {
				AfBorrowCashDo latestBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
				if (latestBorrowCashDo != null
						&& AfBorrowCashReviewStatus.refuse.getCode().equals(latestBorrowCashDo.getReviewStatus())) {
					// 借款被拒绝
					AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(
							AfResourceType.RiskManagementBorrowcashLimit.getCode(),
							AfResourceSecType.RejectTimePeriod.getCode());
					if (afResourceDo != null && StringUtils.equals("O", afResourceDo.getValue4())) {
						Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
						Date desTime = DateUtil.addDays(latestBorrowCashDo.getGmtCreate(), rejectTimePeriod);
						if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime),
								DateUtil.getToday()) < 0) {
							// 风控拒绝日期内
							logger.info("风控拒绝过");
							throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
						}
					}
				}

				String cardNo = mainCard.getCardNumber();
				// 主卡号不能为空
				if (StringUtils.isEmpty(cardNo)) {
					return new ApiHandleResponse(requestDataVo.getId(),
							FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
				}
				String riskOrderNo = riskUtil.getOrderNo("vefy",
						cardNo.substring(cardNo.length() - 4, cardNo.length()));
				cashDo.setUserId(userId);
				cashDo.setGmtCreate(new Date());
				cashDo.setRishOrderNo(riskOrderNo);
				cashDo.setReviewStatus(AfBorrowCashReviewStatus.apply.getCode());
				afBorrowCashService.updateBorrowCash(cashDo);
				// 更新订单借款状态为“申请/待风控审核”
				afBorrowLegalOrderCashDo.setStatus(AfBorrowCashReviewStatus.apply.getCode());
				afBorrowLegalOrderCashService.updateById(afBorrowLegalOrderCashDo);

				BigDecimal riskReviewAmount = borrowAmount.add(new BigDecimal(goodsAmount));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String borrowTime = sdf.format(new Date());
				RiskVerifyRespBo verifyBo = riskUtil.verifyNew(ObjectUtils.toString(userId, ""),
						afBorrowCashDo.getBorrowNo(), type, "50", afBorrowCashDo.getCardNumber(), appName, ipAddress,
						blackBox, riskOrderNo, accountDo.getUserName(), riskReviewAmount, afBorrowCashDo.getPoundage(),
						borrowTime, "借钱", StringUtil.EMPTY_STRING, null, null);

				if (verifyBo.isSuccess()) {
					delegatePay(verifyBo.getConsumerNo(), verifyBo.getOrderNo(), verifyBo.getResult(),
							afBorrowLegalOrderDo, afBorrowLegalOrderCashDo);
					// 加入借款埋点信息,来自哪个包等
					doMaidianLog(request, afBorrowCashDo, requestDataVo, context);
				} else {
					// 风控拒绝
					cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());

					// 更新订单借款状态
					afBorrowLegalOrderCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					afBorrowLegalOrderCashDo.setGmtClose(new Date());

					// 更新订单状态为关闭
					afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
					afBorrowLegalOrderDo.setClosedDetail("risk refuse");
					afBorrowLegalOrderDo.setGmtClosed(new Date());

				}
				transactionTemplate.execute(new TransactionCallback<String>() {
					@Override
					public String doInTransaction(TransactionStatus ts) {
						afBorrowCashService.updateBorrowCash(cashDo);
						afBorrowLegalOrderCashService.updateById(afBorrowLegalOrderCashDo);
						afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
						return "success";
					}
				});

				return resp;
			} catch (Exception e) {
				logger.error("apply legal borrow cash  error", e);
				// 关闭借款
				cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				// 关闭搭售商品订单
				afBorrowLegalOrderDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());
				// 关闭搭售商品借款
				afBorrowLegalOrderCashDo.setStatus(BorrowLegalOrderStatus.CLOSED.getCode());

				cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
				// 如果属于非返呗自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
				if (e instanceof FanbeiException) {
					cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
				} else {
					logger.error("apply legal borrow cash exist unexpected exception,cause:" + e.getCause());
					cashDo.setReviewStatus(AfBorrowCashReviewStatus.apply.getCode());
					cashDo.setReviewDetails("弱风控认证存在捕获外异常");
				}
				transactionTemplate.execute(new TransactionCallback<String>() {
					@Override
					public String doInTransaction(TransactionStatus ts) {
						afBorrowCashService.updateBorrowCash(cashDo);
						afBorrowLegalOrderCashService.updateById(afBorrowLegalOrderCashDo);
						afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
						return "success";
					}
				});

				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
		} finally {
			bizCacheUtil.delCache(lockKey);
		}

	}

	private void delegatePay(String consumerNo, String orderNo, String result,
			final AfBorrowLegalOrderDo afBorrowLegalOrderDo, final AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo) {
		Long userId = Long.parseLong(consumerNo);
		final AfBorrowCashDo cashDo = new AfBorrowCashDo();
		Date currDate = new Date();
		AfUserDo afUserDo = afUserService.getUserById(userId);
		AfUserAccountDo accountInfo = afUserAccountService.getUserAccountByUserId(userId);
		final AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByRishOrderNo(orderNo);
		cashDo.setRid(afBorrowCashDo.getRid());

		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);

		List<String> whiteIdsList = new ArrayList<String>();
		final int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		// 判断是否在白名单里面
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

		if (whiteIdsList.contains(afUserDo.getUserName()) || StringUtils.equals("10", result)) {
			jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
			String bankNumber = card.getCardNumber();
			String lastBank = bankNumber.substring(bankNumber.length() - 4);
			smsUtil.sendBorrowCashCode(afUserDo.getUserName(), lastBank);
			// 审核通过
			cashDo.setGmtArrival(currDate);
			cashDo.setStatus(AfBorrowCashStatus.transeding.getCode());
			afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
			afBorrowLegalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.APPLYING.getCode());
			
			AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(Long.parseLong(consumerNo));
			// 打款
			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(),
					userDto.getRealName(), afBorrowCashDo.getCardNumber(), consumerNo + "", card.getMobile(),
					card.getBankName(), card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
					UserAccountLogType.BorrowCash.getCode(), afBorrowCashDo.getRid() + "");
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.agree.getCode());
			Integer day = NumberUtil
					.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
			Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(cashDo.getGmtArrival());
			Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
			cashDo.setGmtPlanRepayment(repaymentDay);
			if (!upsResult.isSuccess()) {
				// 大款失败，更新状态
				logger.info("upsResult error:" + FanbeiExceptionCode.BANK_CARD_PAY_ERR);
				cashDo.setStatus(AfBorrowCashStatus.transedfail.getCode());
				// 关闭订单
				afBorrowLegalOrderDo.setStatus(AfBorrowCashStatus.closed.getCode());
				afBorrowLegalOrderDo.setClosedDetail("transed fail");
				// 更新订单借钱状态为失败
				afBorrowLegalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());

			} else {
				// 打款成功，更新借款状态、可用额度等信息
				try {
					BigDecimal auAmount = afUserAccountService.getAuAmountByUserId(userId);
					afBorrowCashService.updateAuAmountByRid(cashDo.getRid(), auAmount);
				} catch (Exception e) {
					logger.error("updateAuAmountByRid is fail;msg=" + e);
				}
				// 减少额度，包括搭售商品借款
				accountInfo.setUsedAmount(BigDecimalUtil.add(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount(),
						afBorrowLegalOrderDo.getPriceAmount()));

				afUserAccountService.updateOriginalUserAccount(accountInfo);
				// 增加日志
				AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash,
						afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
				afUserAccountLogDao.addUserAccountLog(accountLog);
				// 设置订单状态为待发货
				afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.AWAIT_DELIVER.getCode());
				// 设置订单借款状态为待还款
				afBorrowLegalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode());
			}

		} else {
			cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
			cashDo.setReviewDetails(AfBorrowCashReviewStatus.refuse.getName());
			// 更新订单状态
			afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.CLOSED.getCode());
			// 更新订单借款状态
			afBorrowLegalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());
			jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
		}

		transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				// 更新借款状态
				afBorrowCashService.updateBorrowCash(cashDo);
				// 更新订单状态
				afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
				// 更新订单借款状态
				afBorrowLegalOrderCashService.updateById(afBorrowLegalOrderCashDo);
				addTodayTotalAmount(currentDay, afBorrowCashDo.getAmount());
				return "success";
			}
		});
	}

	/**
	 * 增加当天审核的金额
	 * 
	 * @param day
	 * @param amount
	 */
	private void addTodayTotalAmount(int day, BigDecimal amount) {
		AfBorrowCacheAmountPerdayDo amountCurrentDay = new AfBorrowCacheAmountPerdayDo();
		amountCurrentDay.setDay(day);
		amountCurrentDay.setAmount(amount);
		afBorrowCacheAmountPerdayService.updateBorrowCacheAmount(amountCurrentDay);
	}

	public AfBorrowLegalOrderCashDo buildBorrowLegalOrderCashDo(BigDecimal goodsAmount, String type, Long userId,
			Long orderId, BigDecimal poundage, Long borrowId, BigDecimal overdueAmount, String borrowRemark,
			String refundRemark, AfResourceDo rateInfoDo) {
		Integer day = NumberUtil.objToIntDefault(type, 0);
		// 计算手续费和利息
		String borrowRate = rateInfoDo.getValue3();
		JSONArray array = JSONObject.parseArray(borrowRate);
		double interestRate = 0; // 利率
		double serviceRate = 0; // 手续费率
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString("borrowTag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), type)) {
					interestRate = info.getDouble("consumeSevenDay");
				} else {
					interestRate = info.getDouble("consumeFourteenDay");
				}
			} else if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), type)) {
					serviceRate = info.getDouble("consumeSevenDay");
				} else {
					serviceRate = info.getDouble("consumeFourteenDay");
				}
			}
		}
		interestRate = interestRate / 100;
		serviceRate = serviceRate / 100;

		BigDecimal rateAmount = new BigDecimal(interestRate).multiply(new BigDecimal(day)).multiply(goodsAmount)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		BigDecimal poundageAmount = new BigDecimal(serviceRate).multiply(new BigDecimal(day)).multiply(goodsAmount)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = new AfBorrowLegalOrderCashDo();
		afBorrowLegalOrderCashDo.setAmount(goodsAmount);
		afBorrowLegalOrderCashDo.setType(AfBorrowCashType.findRoleTypeByCode(type).getName());
		afBorrowLegalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.APPLYING.getCode());
		afBorrowLegalOrderCashDo.setUserId(userId);
		afBorrowLegalOrderCashDo.setPoundageRate(poundage);
		afBorrowLegalOrderCashDo.setBorrowLegalOrderId(orderId);
		afBorrowLegalOrderCashDo.setBorrowId(borrowId);
		afBorrowLegalOrderCashDo.setOverdueAmount(overdueAmount);
		afBorrowLegalOrderCashDo.setBorrowRemark(borrowRemark);
		afBorrowLegalOrderCashDo.setRefundRemark(refundRemark);
		afBorrowLegalOrderCashDo.setInterestAmount(rateAmount);
		afBorrowLegalOrderCashDo.setPoundageAmount(poundageAmount);
		// 获取借款天数
		Integer planRepayDays = NumberUtil.objToIntDefault(type, 0);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, planRepayDays - 1);
		afBorrowLegalOrderCashDo.setPlanRepayDays(planRepayDays);
		afBorrowLegalOrderCashDo.setGmtPlanRepay(cal.getTime());
		return afBorrowLegalOrderCashDo;
	}

	public AfBorrowLegalOrderDo buildBorrowLegalOrder(BigDecimal goodsAmount, Long userId, Long borrowId, Long goodsId,
			String goodsName, String deliveryAddress, String deliveryUser, String deliveryPhone) {

		AfBorrowLegalOrderDo afBorrowLegalOrderDo = new AfBorrowLegalOrderDo();
		afBorrowLegalOrderDo.setUserId(userId);
		afBorrowLegalOrderDo.setBorrowId(borrowId);
		afBorrowLegalOrderDo.setGoodsId(goodsId);
		afBorrowLegalOrderDo.setPriceAmount(goodsAmount);
		afBorrowLegalOrderDo.setGoodsName(goodsName);
		afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
		afBorrowLegalOrderDo.setAddress(deliveryAddress);
		afBorrowLegalOrderDo.setDeliveryPhone(deliveryPhone);
		afBorrowLegalOrderDo.setDeliveryUser(deliveryUser);
		afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
		return afBorrowLegalOrderDo;
	}

	public AfBorrowCashDo buildBorrowCashDo(BigDecimal amount, String type, String latitude, String longitude,
			AfUserBankcardDo afUserBankcardDo, String city, String province, String county, String address, Long userId,
			int currentDay, AfResourceDo rateInfoDo) {

		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list);

		this.checkSwitch(rate, currentDay);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());

		Integer day = NumberUtil.objToIntDefault(type, 0);

		BigDecimal latitudeBig = NumberUtil.objToBigDecimalDefault(latitude, BigDecimal.ZERO);
		BigDecimal longitudeBig = NumberUtil.objToBigDecimalDefault(longitude, BigDecimal.ZERO);

		// 计算手续费和利息
		String borrowRate = rateInfoDo.getValue2();
		JSONArray array = JSONObject.parseArray(borrowRate);
		double interestRate = 0; // 利率
		double serviceRate = 0; // 手续费率
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString("borrowTag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), type)) {
					interestRate = info.getDouble("borrowSevenDay");
				} else {
					interestRate = info.getDouble("borrowFourteenDay");
				}
			} else if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), type)) {
					serviceRate = info.getDouble("borrowSevenDay");
				} else {
					serviceRate = info.getDouble("borrowFourteenDay");
				}
			}
		}
		interestRate = interestRate / 100;
		serviceRate = serviceRate / 100;

		BigDecimal rateAmount = new BigDecimal(interestRate).multiply(new BigDecimal(day)).multiply(amount)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		BigDecimal poundageAmount = new BigDecimal(serviceRate).multiply(new BigDecimal(day)).multiply(amount)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		AfBorrowCashDo afBorrowCashDo = new AfBorrowCashDo();
		afBorrowCashDo.setAmount(amount);
		afBorrowCashDo.setCardName(afUserBankcardDo.getBankName());
		afBorrowCashDo.setCardNumber(afUserBankcardDo.getCardNumber());
		afBorrowCashDo.setLatitude(latitudeBig);
		afBorrowCashDo.setLongitude(longitudeBig);
		afBorrowCashDo.setCity(city);
		afBorrowCashDo.setProvince(province);
		afBorrowCashDo.setCounty(county);
		afBorrowCashDo.setType(AfBorrowCashType.findRoleTypeByCode(type).getName());
		afBorrowCashDo.setStatus(AfBorrowCashStatus.apply.getCode());
		afBorrowCashDo.setUserId(userId);
		afBorrowCashDo.setRateAmount(rateAmount);
		afBorrowCashDo.setPoundage(poundageAmount);
		afBorrowCashDo.setAddress(address);
		// 到账金额和借款金额相同
		afBorrowCashDo.setArrivalAmount(amount);
		afBorrowCashDo.setPoundageRate(new BigDecimal(serviceRate));
		afBorrowCashDo.setBaseBankRate(bankRate);
		return afBorrowCashDo;
	}

	/**
	 * 检查放款开关
	 * 
	 * @param rate
	 * @param currentDay
	 */
	private void checkSwitch(Map<String, Object> rate, Integer currentDay) {

		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_SWITCH_NO);
		}

		AfBorrowCacheAmountPerdayDo currentAmount = afBorrowCacheAmountPerdayService.getSigninByDay(currentDay);
		if (currentAmount == null) {
			AfBorrowCacheAmountPerdayDo temp = new AfBorrowCacheAmountPerdayDo();
			temp.setAmount(BigDecimal.ZERO);
			temp.setDay(currentDay);
			afBorrowCacheAmountPerdayService.addBorrowCacheAmountPerday(temp);
			currentAmount = temp;
		}
		if (currentAmount.getAmount().compareTo(new BigDecimal((String) rate.get("amountPerDay"))) >= 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_SWITCH_NO);
		}
	}

	/**
	 * 计算最多能计算多少额度 150取100 250.37 取200
	 * 
	 * @param usableAmount
	 * @return
	 */
	private BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
		// 可使用额度
		Integer amount = usableAmount.intValue();
		return new BigDecimal(amount / 100 * 100);
	}

	/**
	 * 记录埋点相关日志日志
	 *
	 * @param request
	 * @param cashDo
	 * @param requestDataVo
	 * @param context
	 */
	private void doMaidianLog(HttpServletRequest request, AfBorrowCashDo cashDo, RequestDataVo requestDataVo,
			FanbeiContext context) {
		try {
			// 获取可变参数
			String ext1 = cashDo.getBorrowNo();
			String ext2 = cashDo.getUserId() + "";
			String ext3 = cashDo.getAmount() + "";
			String ext4 = context.getAppVersion() + "";
			maidianLog.info(com.ald.fanbei.api.common.util.StringUtil.appendStrs("	",
					DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
					com.ald.fanbei.api.common.util.StringUtil.judgeClientDeviceFlag(requestDataVo.getId()), "	rmtIP=",
					CommonUtil.getIpAddr(request), "	userName=", context.getUserName(), "	", 0, "	",
					request.getRequestURI(), "	", cashDo.getRid() + "", "	",
					DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "userBorrowCashApply", "	", ext1,
					"	", ext2, "	", ext3, "	", ext4,
					"	reqD=", "appFlag:" + requestDataVo.getId() + ",appVersion:" + context.getAppVersion()
							+ ",userId=" + cashDo.getUserId() + ",cashAmount:" + cashDo.getAmount(),
					"	resD=", "null"));
		} catch (Exception e) {
			logger.error("userBorrowCashApply maidian logger error", e);
		}
	}

}
