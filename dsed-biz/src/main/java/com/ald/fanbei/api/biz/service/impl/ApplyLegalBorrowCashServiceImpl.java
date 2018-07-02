package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.RiskReviewStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowCashDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service("applyLegalBorrowCashService")
public class ApplyLegalBorrowCashServiceImpl implements ApplyLegalBorrowCashService {

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;

	@Resource
	AfBorrowCashService afBorrowCashService;

	@Resource
	AfBorrowCashDao afBorrowCashDao;

	@Resource
	AfBorrowRecycleOrderService borrowRecycleOrderService;

	@Resource
	RiskUtil riskUtil;

	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	JpushService jpushService;

	@Resource
	SmsUtil smsUtil;

	@Resource
	UpsUtil upsUtil;

	@Resource
	AfUserService afUserService;

	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;

	@Resource
	TransactionTemplate transactionTemplate;

	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;

	@Resource
	AfBorrowDao afBorrowDao;

	@Resource
	AfBorrowCashDao borrowCashDao;

	@Resource
	AfBorrowRecycleOrderService recycleOrderService;

	@Resource
	NumberWordFormat numberWordFormat;
	private Logger logger = LoggerFactory.getLogger(ApplyLegalBorrowCashServiceImpl.class);

	@Override
	public AfBorrowLegalOrderDo buildBorrowLegalOrder(Long userId, ApplyLegalBorrowCashBo param) {
		AfBorrowLegalOrderDo afBorrowLegalOrderDo = new AfBorrowLegalOrderDo();
		afBorrowLegalOrderDo.setUserId(userId);
		afBorrowLegalOrderDo.setBorrowId(0L);
		afBorrowLegalOrderDo.setGoodsId(param.getGoodsId());
		afBorrowLegalOrderDo.setPriceAmount(param.getGoodsAmount());
		afBorrowLegalOrderDo.setGoodsName(param.getGoodsName());
		afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
		afBorrowLegalOrderDo.setAddress(param.getDeliveryAddress());
		afBorrowLegalOrderDo.setDeliveryPhone(param.getDeliveryPhone());
		afBorrowLegalOrderDo.setDeliveryUser(param.getDeliveryUser());
		return afBorrowLegalOrderDo;
	}

	@Override
	public AfBorrowCashDo buildBorrowCashDo(AfUserBankcardDo afUserBankcardDo, Long userId,
											AfResourceDo rateInfoDo,ApplyLegalBorrowCashBo param) {
		// 获取用户分层利率

		BigDecimal oriRate = riskUtil.getRiskOriRate(userId,(JSONObject)JSONObject.toJSON(param),param.getType());
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
		String oneDay = "";
		String twoDay = "";
		if(null != afResourceDo){
			oneDay = afResourceDo.getTypeDesc().split(",")[0];
			twoDay = afResourceDo.getTypeDesc().split(",")[1];
		}
		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = riskUtil.getObjectWithResourceDolist(list);
		this.checkSwitch(rate, currentDay);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());

		Integer day = NumberUtil.objToIntDefault(param.getType(), 0);
		String type = param.getType();

		BigDecimal borrowAmount = param.getAmount();
		// 计算手续费和利息
		String borrowRate = rateInfoDo.getValue2();
		JSONArray array = JSONObject.parseArray(borrowRate);
		double interestRate = 0; // 利率
		double serviceRate = 0; // 手续费率
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString("borrowTag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, type)) {
					interestRate = info.getDouble("borrowFirstType");
				} else if (StringUtils.equals(twoDay, type)){
					interestRate = info.getDouble("borrowSecondType");
				}
			} else if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, type)) {
					serviceRate = info.getDouble("borrowFirstType");
				} else if (StringUtils.equals(twoDay, type)){
					serviceRate = info.getDouble("borrowSecondType");
				}
			}
		}
		BigDecimal rateAmount = new BigDecimal(interestRate / 100).multiply(borrowAmount).multiply(new BigDecimal(day))
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		BigDecimal poundageAmount = new BigDecimal(serviceRate / 100).multiply(borrowAmount)
				.multiply(new BigDecimal(day)).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		AfBorrowCashDo afBorrowCashDo = new AfBorrowCashDo();
		afBorrowCashDo.setGmtCreate(new Date());
		afBorrowCashDo.setAmount(borrowAmount);
		afBorrowCashDo.setCardName(afUserBankcardDo.getBankName());
		afBorrowCashDo.setCardNumber(afUserBankcardDo.getCardNumber());
		afBorrowCashDo.setLatitude(param.getLatitude());
		afBorrowCashDo.setLongitude(param.getLongitude());
		afBorrowCashDo.setCity(ObjectUtils.toString(param.getCity()));
		afBorrowCashDo.setProvince(ObjectUtils.toString(param.getProvince()));
		afBorrowCashDo.setCounty(ObjectUtils.toString(param.getCounty()));
		afBorrowCashDo.setType(type);
		afBorrowCashDo.setStatus(AfBorrowCashStatus.apply.getCode());
		afBorrowCashDo.setUserId(userId);
		afBorrowCashDo.setRateAmount(rateAmount);
		afBorrowCashDo.setPoundage(poundageAmount);
		afBorrowCashDo.setAddress(param.getAddress());
		// 借款金额支付商品后剩余金额为到账金额
		afBorrowCashDo.setArrivalAmount(borrowAmount.subtract(param.getGoodsAmount()));
		afBorrowCashDo.setPoundageRate(new BigDecimal(serviceRate));
		afBorrowCashDo.setBaseBankRate(bankRate);
		afBorrowCashDo.setRiskDailyRate(oriRate);
		return afBorrowCashDo;
	}

	@Override
	public AfBorrowCashDo buildRecycleBorrowCashDo(AfUserBankcardDo afUserBankcardDo, Long userId, AfResourceDo rateInfoDo, ApplyLegalBorrowCashBo param) {
		// 获取用户分层利率

		BigDecimal oriRate = riskUtil.getRiskOriRate(userId,(JSONObject)JSONObject.toJSON(param),param.getType());
		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = riskUtil.getObjectWithResourceDolist(list);
		this.checkSwitch(rate, currentDay);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());

		Integer day = NumberUtil.objToIntDefault(param.getType(), 0);
		String type = param.getType();

		BigDecimal borrowAmount = param.getAmount();
		// 计算手续费和利息
		String borrowRate = rateInfoDo.getValue2();
		JSONArray array = JSONObject.parseArray(borrowRate);
		BigDecimal rateAmount = oriRate.multiply(borrowAmount).multiply(new BigDecimal(day));
		AfBorrowCashDo afBorrowCashDo = new AfBorrowCashDo();
		Calendar   calendar   =   new   GregorianCalendar();
		calendar.add(Calendar.DATE,Integer.parseInt(param.getType()));
		afBorrowCashDo.setGmtPlanRepayment( calendar.getTime() );
		afBorrowCashDo.setAmount(borrowAmount);
		afBorrowCashDo.setCardName(afUserBankcardDo.getBankName());
		afBorrowCashDo.setCardNumber(afUserBankcardDo.getCardNumber());
		afBorrowCashDo.setLatitude(param.getLatitude());
		afBorrowCashDo.setLongitude(param.getLongitude());
		afBorrowCashDo.setCity(ObjectUtils.toString(param.getCity()));
		afBorrowCashDo.setProvince(ObjectUtils.toString(param.getProvince()));
		afBorrowCashDo.setCounty(ObjectUtils.toString(param.getCounty()));
		afBorrowCashDo.setType(type);
		afBorrowCashDo.setStatus(AfBorrowCashStatus.apply.getCode());
		afBorrowCashDo.setUserId(userId);
		afBorrowCashDo.setRateAmount(rateAmount);
		afBorrowCashDo.setPoundage(BigDecimal.ZERO);
		afBorrowCashDo.setAddress(param.getAddress());
		// 借款金额支付商品后剩余金额为到账金额
		afBorrowCashDo.setArrivalAmount(borrowAmount);
		afBorrowCashDo.setPoundageRate(BigDecimal.ZERO);
		afBorrowCashDo.setBaseBankRate(bankRate);
		afBorrowCashDo.setRiskDailyRate(oriRate);
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

	@Override
	public void checkLock(String lockKey) {
		// boolean isGetLock = bizCacheUtil.getLock30Second(lockKey, "1");
		boolean isGetLock = bizCacheUtil.getLock30Minute(lockKey, "1");
		logger.info("borrowCash checkLock,key="+lockKey+",isGetLock="+isGetLock);
		if (!isGetLock) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}
	}

	/**
	 * 计算最多能计算多少额度 150取100 250.37 取200
	 *
	 * @param usableAmount
	 * @return
	 */
	@Override
	public BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
		// 可使用额度
		Integer amount = usableAmount.intValue();
		return new BigDecimal(amount / 100 * 100);
	}

	/**
	 * 校验账户信息
	 */
	@Override
	public void checkAccount(AfUserAccountDo accountDo, AfUserAuthDo authDo) {
		if (accountDo == null || authDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.SYSTEM_ERROR);
		}
	}

	/**
	 * 校验借款金额
	 */
	@Override
	public void checkAmount(ApplyLegalBorrowCashBo param, AfResourceDo rateInfoDo) {
		BigDecimal borrowAmount = param.getAmount();
		if (rateInfoDo != null) {
			BigDecimal minAmount = new BigDecimal(rateInfoDo.getValue4());
			BigDecimal maxAmount = new BigDecimal(rateInfoDo.getValue1());
			if (borrowAmount.compareTo(minAmount) < 0 || borrowAmount.compareTo(maxAmount) > 0) {
				throw new FanbeiException(FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
			}
		}
	}

	/**
	 * 检验支付密码
	 */
	@Override
	public void checkPassword(AfUserAccountDo accountDo, ApplyLegalBorrowCashBo param) {
		String inputOldPwd = UserUtil.getPassword(param.getPwd(), accountDo.getSalt());
		if (!StringUtils.equals(inputOldPwd, accountDo.getPassword())) {
			throw new FanbeiException(FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
	}

	/**
	 * 校验用户是否绑定主卡
	 */
	@Override
	public void checkBindCard(AfUserAuthDo authDo) {
		if (StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
	}

	/**
	 * 校验认证信息
	 */
	@Override
	public void checkAuth(AfUserAuthDo authDo) {
		if (!StringUtils.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getFacesStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getMobileStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getTeldirStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getRiskStatus(), YesNoStatus.YES.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.AUTH_ALL_AUTH_ERROR);
		}

	}

	/**
	 * 校验是否可借钱
	 */
	@Override
	public void checkCanBorrow(AfUserAccountDo accountDo, ApplyLegalBorrowCashBo param) {
		BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
		BigDecimal accountBorrow = this.calculateMaxAmount(usableAmount);

		if (accountBorrow.compareTo(param.getAmount()) < 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);
		}
		
		AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("enabled_type_borrow");//是否允许这种类型的借款
		if(afResourceDo!=null 
				&& afResourceDo.getValue().equals(YesNoStatus.YES.getCode()) 
				&& afResourceDo.getValue1().contains(param.getType())){
			throw new FanbeiException(afResourceDo.getValue2(), true);
		}
	}

	/**
	 * 业务逻辑校验
	 */
	@Override
	public void checkBusi(AfUserAccountDo accountDo, AfUserAuthDo authDo, AfResourceDo rateInfoDo,
						  AfUserBankcardDo bankCard,ApplyLegalBorrowCashBo param) {
		this.checkAccount(accountDo, authDo);
		this.checkAmount(param, rateInfoDo);
		this.checkPassword(accountDo, param);
		this.checkBindCard(authDo);
		this.checkAuth(authDo);
		this.checkCanBorrow(accountDo, param);
		this.checkBorrowFinish(accountDo.getUserId());
		this.checkRiskRefused(accountDo.getUserId());
		this.checkCardNotEmpty(bankCard);
		this.checkBorrowType(param,rateInfoDo);
	}

	@Override
	public void checkRecycleBusi(AfUserAccountDo accountDo, AfUserAuthDo authDo, AfResourceDo rateInfoDo, AfUserBankcardDo bankCard, ApplyLegalBorrowCashBo param) {
		this.checkAccount(accountDo, authDo);
		this.checkAmount(param, rateInfoDo);
		this.checkPassword(accountDo, param);
		this.checkBindCard(authDo);
		this.checkAuth(authDo);
		this.checkCanBorrow(accountDo, param);
		this.checkRecycleBorrowFinish(accountDo.getUserId());
		this.checkRiskRefused(accountDo.getUserId());
		this.checkCardNotEmpty(bankCard);
		this.checkBorrowType(param,rateInfoDo);
	}

	private void checkBorrowType(ApplyLegalBorrowCashBo param, AfResourceDo rateInfoDo) {
		String type = param.getType();
		String borrowDays = rateInfoDo.getTypeDesc();
		if(StringUtils.isNotBlank(borrowDays) && !borrowDays.contains(type)) {
			throw new FanbeiException("借钱天数有误，请返回借钱首页重试",true);
		}
	}

	@Override
	public void addTodayTotalAmount(int day, BigDecimal amount) {
		AfBorrowCacheAmountPerdayDo amountCurrentDay = new AfBorrowCacheAmountPerdayDo();
		amountCurrentDay.setDay(day);
		amountCurrentDay.setAmount(amount);
		afBorrowCacheAmountPerdayService.updateBorrowCacheAmount(amountCurrentDay);
	}

	/**
	 * 判断用户是否有借款未完成
	 */
	@Override
	public void checkBorrowFinish(Long userId) {
		boolean borrowFlag = afBorrowCashService.isCanBorrowCash(userId);
		if (!borrowFlag) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}
	}

	@Override
	public void checkRecycleBorrowFinish(Long userId) {
		List<AfBorrowCashDo> notFinishBorrowList = afBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
		for (AfBorrowCashDo borrowCashDo :notFinishBorrowList) {
			AfBorrowRecycleOrderDo recycleOrderDo = recycleOrderService.getBorrowRecycleOrderByBorrowId(borrowCashDo.getRid());
			if (recycleOrderDo!= null){
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RECYCLE_STATUS_ERROR);
			}
			throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
		}
	}

	@Override
	public void delegatePay(String consumerNo, String orderNo, String result,
							final AfBorrowLegalOrderDo afBorrowLegalOrderDo, AfUserBankcardDo mainCard,final AfBorrowCashDo afBorrowCashDo) {
		Long userId = Long.parseLong(consumerNo);
		final AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
		Date currDate = new Date();
		AfUserDo afUserDo = afUserService.getUserById(userId);
		AfUserAccountDo accountInfo = afUserAccountService.getUserAccountByUserId(userId);
		delegateBorrowCashDo.setRid(afBorrowCashDo.getRid());

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

		if (whiteIdsList.contains(afUserDo.getUserName()) || StringUtils.equals(RiskVerifyRespBo.RISK_SUCC_CODE, result)) {
			jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
			String bankNumber = mainCard.getCardNumber();
			String lastBank = bankNumber.substring(bankNumber.length() - 4);
			if (afBorrowCashDo.getMajiabaoName().contains("borrowSuperman")){
				smsUtil.sendJKCRBorrowCashCode(afUserDo.getUserName(), lastBank);
			}else {
				smsUtil.sendBorrowCashCode(afUserDo.getUserName(), lastBank);
			}
			String title = "恭喜您，审核通过啦！";
			String msgContent = "您的借款审核通过，请留意您尾号&bankCardNo的银行卡资金变动，请注意按时还款，保持良好的信用记录。";
			msgContent = msgContent.replace("&bankCardNo", lastBank);
			jpushService.pushUtil(title, msgContent, afUserDo.getUserName());
			// 审核通过
			delegateBorrowCashDo.setGmtArrival(currDate);
			delegateBorrowCashDo.setStatus(AfBorrowCashStatus.transeding.getCode());
			afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
			// 打款
			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(),
					afUserDo.getRealName(), afBorrowCashDo.getCardNumber(), consumerNo + "", mainCard.getMobile(),
					mainCard.getBankName(), mainCard.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
					UserAccountLogType.BorrowCash.getCode(), afBorrowCashDo.getRid() + "");
			delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.AGREE.getCode());
//			Integer day = NumberUtil
//					.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
			Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
			Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(delegateBorrowCashDo.getGmtArrival());
			Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
			delegateBorrowCashDo.setGmtPlanRepayment(repaymentDay);
			if (!upsResult.isSuccess()) {
				// 大款失败，更新状态
				logger.info("upsResult error:" + FanbeiExceptionCode.BANK_CARD_PAY_ERR);
				delegateBorrowCashDo.setStatus(AfBorrowCashStatus.transedfail.getCode());
				// 关闭订单
				afBorrowLegalOrderDo.setStatus(AfBorrowCashStatus.closed.getCode());
				afBorrowLegalOrderDo.setClosedDetail("transed fail");

			} else {
				// 打款成功，更新借款状态、可用额度等信息
				try {
					BigDecimal auAmount = afUserAccountService.getAuAmountByUserId(userId);
					afBorrowCashService.updateAuAmountByRid(delegateBorrowCashDo.getRid(), auAmount);
				} catch (Exception e) {
					logger.error("updateAuAmountByRid is fail;msg=" + e);
				}
				// 减少额度，包括搭售商品借款
				afUserAccountSenceService.syncLoanUsedAmount(userId, SceneType.CASH, afBorrowCashDo.getAmount());

				// 增加日志
				AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash,
						afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
				afUserAccountLogDao.addUserAccountLog(accountLog);
			}

		} else {
			delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
			delegateBorrowCashDo.setReviewDetails(RiskReviewStatus.REFUSE.getName());
			// 更新订单状态
			afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.CLOSED.getCode());
			logger.info("test1 ");
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("extend_koudai");
			if(afResourceDo!=null&&afResourceDo.getValue().equals("Y")){
				jpushService.dealBorrowCashApplyFailForKoudai(afUserDo.getUserName(), currDate,afResourceDo.getValue1());
				smsUtil.sendSms(afUserDo.getUserName(),afResourceDo.getValue2());
			}else{
				jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
			}
		}

		transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				// 更新借款状态
				afBorrowCashService.updateBorrowCash(delegateBorrowCashDo);
				// 更新订单状态
				afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
				ApplyLegalBorrowCashServiceImpl.this.addTodayTotalAmount(currentDay, afBorrowCashDo.getAmount());
				return "success";
			}

		});
	}

	@Override
	public void recycleDelegatePay(final Long userId, String orderNo, AfUserBankcardDo mainCard, final AfBorrowCashDo afBorrowCashDo) {
		Date currDate = new Date();
		AfUserDo afUserDo = afUserService.getUserById(userId);

		// 打款
		final UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(),
				afUserDo.getRealName(), afBorrowCashDo.getCardNumber(), userId.toString(), mainCard.getMobile(),
				mainCard.getBankName(), mainCard.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
				UserAccountLogType.BorrowCash.getCode(), afBorrowCashDo.getRid() + "");
		
		if (!upsResult.isSuccess()) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
		}else {
			afBorrowCashDo.setGmtArrival(currDate);
			Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
			Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(afBorrowCashDo.getGmtArrival());
			Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
			afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
		}
	}

	/**
	 * 检查是否风控拒绝过
	 */
	@Override
	public void checkRiskRefused(Long userId) {
		// 获取最后一笔借款信息
		AfBorrowCashDo lastBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
		if (lastBorrowCashDo != null && RiskReviewStatus.REFUSE.getCode().equals(lastBorrowCashDo.getReviewStatus())) {
			// 借款被拒绝
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(
					AfResourceType.RiskManagementBorrowcashLimit.getCode(),
					AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && StringUtils.equals("O", afResourceDo.getValue4())) {

				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(lastBorrowCashDo.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) {
					throw new FanbeiException(FanbeiExceptionCode.RISK_REFUSE_ERROR);
				}
			}
		}

	}

	@Override
	public boolean checkRiskRefusedResult(Long userId) {
		boolean result = false;
		AfBorrowCashDo lastBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
		if (lastBorrowCashDo != null && RiskReviewStatus.REFUSE.getCode().equals(lastBorrowCashDo.getReviewStatus())) {
			// 借款被拒绝
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(
					AfResourceType.RiskManagementBorrowcashLimit.getCode(),
					AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && StringUtils.equals("O", afResourceDo.getValue4())) {

				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(lastBorrowCashDo.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) {
					result = true;//限制时间内有风控审核拒绝记录
				}
			}
		}
			return result;
	}

	/**
	 * 校验卡号不能为空
	 */
	@Override
	public void checkCardNotEmpty(AfUserBankcardDo mainCard) {
		String cardNo = mainCard.getCardNumber();
		if (StringUtils.isEmpty(cardNo)) {
			throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
		}
	}

	@Override
	public void updateBorrowStatus(final AfBorrowCashDo cashDo, final AfBorrowLegalOrderDo afBorrowLegalOrderDo) {
		transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus ts) {
				afBorrowCashService.updateBorrowCash(cashDo);
				afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
				return "success";
			}
		});
	}

	@Override
	public void updateBorrowStatus(final AfBorrowCashDo cashDo) {
		transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus ts) {
				afBorrowCashService.updateBorrowCash(cashDo);
				return "success";
			}
		});
	}

	@Override
	public Long addBorrowRecord(final AfBorrowCashDo afBorrowCashDo,final AfBorrowLegalOrderDo afBorrowLegalOrderDo) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus ts) {
				afBorrowCashService.addBorrowCash(afBorrowCashDo);
				Long borrowId = afBorrowCashDo.getRid();
				afBorrowLegalOrderDo.setBorrowId(borrowId);
				// 新增搭售商品订单
				afBorrowLegalOrderService.saveBorrowLegalOrder(afBorrowLegalOrderDo);
				return borrowId;
			}
		});
	}

	@Override
	public RiskVerifyRespBo submitRiskReview(Long borrowId, String appType, String ipAddress,
											 ApplyLegalBorrowCashBo param, AfUserAccountDo accountDo, Long userId, AfBorrowCashDo afBorrowCashDo,String riskOrderNo) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String borrowTime = sdf.format(new Date());
		HashMap<String, HashMap> riskDataMap = new HashMap();

		HashMap summaryData = afBorrowDao.getUserSummary(userId);
		riskDataMap.put("summaryData", summaryData);
		riskDataMap.put("summaryOrderData", new HashMap<>());
		RiskVerifyRespBo verifyBo = riskUtil.weakRiskForXd(
				ObjectUtils.toString(userId),
				afBorrowCashDo.getBorrowNo(),
				param.getType(), "50",
				afBorrowCashDo.getCardNumber(),
				appType, ipAddress, param.getBlackBox(),
				riskOrderNo, accountDo.getUserName(),
				param.getAmount(), afBorrowCashDo.getPoundage(),
				borrowTime, "借钱", StringUtils.EMPTY, null, null, 0l,
				afBorrowCashDo.getCardName(), null, "", riskDataMap,param.getBqsBlackBox(),null);
		return verifyBo;
	}

	@Override
	public void updateBorrowStatus2Apply(Long borrowId, String riskOrderNo) {
		AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
		delegateBorrowCashDo.setRid(borrowId);
		delegateBorrowCashDo.setGmtModified(new Date());
		delegateBorrowCashDo.setRishOrderNo(riskOrderNo);
		delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
		afBorrowCashService.updateBorrowCash(delegateBorrowCashDo);
	}

	@Override
	public void checkGenRecordError(Long borrowId) {
		if (borrowId == null) {
			throw new FanbeiException(FanbeiExceptionCode.ADD_BORROW_CASH_INFO_FAIL);
		}
	}

	@Override
	public AfBorrowCashDto getBorrowCashInfoById(Long rid) {
		return borrowCashDao.getBorrowCashInfoById(rid);
	}

	public static class ApplyCheckBo{
		public AfBorrowCashDo afBorrowCashDo;
		public AfBorrowLegalOrderDo afBorrowLegalOrderDo;
		public Long borrowId;
	}

}
