package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
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
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：申请借钱
 * 
 * @author suweili 2017年3月25日下午1:06:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowCashV1Api")
public class ApplyBorrowCashV1Api extends GetBorrowCashBase implements ApiHandle {
	
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

		if (StringUtils.isBlank(amountStr) || AfBorrowCashType.findRoleTypeByCode(type) == null || StringUtils.isBlank(pwd) || StringUtils.isBlank(latitude)
				|| StringUtils.isBlank(longitude) || StringUtils.isBlank(blackBox)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}

		// 密码判断
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);

		if (accountDo == null || authDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}

		
		String inputOldPwd = UserUtil.getPassword(pwd, accountDo.getSalt());
		if (!StringUtils.equals(inputOldPwd, accountDo.getPassword())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}

		if (StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}

		// 认证信息判断
		if (!StringUtils.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getFacesStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getMobileStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getContactorStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getLocationStatus(), YesNoStatus.YES.getCode())
				|| !StringUtils.equals(authDo.getTeldirStatus(), YesNoStatus.YES.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_ALL_AUTH_ERROR);

		}

		BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		// ---------------------------------------------
		// AfUserBankcardDo card = new AfUserBankcardDo();
		// card.setBankName("中国农业银行");
		// card.setCardNumber("6228480322828314011");
		// ---------------------------------------------

		///// 临时处理，如果当天内有申请，以最后一条的状态为准 start hy 2017年5月11日09:54:20//////
		
		//对风控拒绝通过配置化处理，按配置期限，如果期限内有拒绝，则不可申请，如果期限内无拒绝记录，则可发起申请 start  alter by ck 2017年6月13日17:47:20
		
		boolean doRish = true;
		AfBorrowCashDo afnewstBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		
		if(afnewstBorrowCashDo!=null && AfBorrowCashStatus.closed.getCode().equals(afnewstBorrowCashDo.getStatus())){
			//借款被拒绝
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if(afResourceDo!=null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())){
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(afnewstBorrowCashDo.getGmtCreate(), rejectTimePeriod);
				if(DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime),DateUtil.getToday())<0){
					//风控拒绝日期内
					doRish = false;
				}
			}
		}
		//对风控拒绝通过配置化处理，按配置期限，如果期限内有拒绝，则不可申请，如果期限内无拒绝记录，则可发起申请 end alter by ck 2017年6月13日17:47:20
		
		BigDecimal  usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
		BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
		if(accountBorrow.compareTo(amount)<0){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);

		}
		///// 临时处理，如果当天内有申请，以最后一条的状态为准 end hy 2017年5月11日09:54:20//////

		boolean isCanBorrow = afBorrowCashService.isCanBorrowCash(userId);

		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);
		AfBorrowCashDo afBorrowCashDo = borrowCashDoWithAmount(amount, type, latitude, longitude, card, city, province, county, address, userId, currentDay);

		if (!isCanBorrow) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}
		
		afBorrowCashService.addBorrowCash(afBorrowCashDo);

		Long borrowId = afBorrowCashDo.getRid();

		AfBorrowCashDo cashDo = new AfBorrowCashDo();
		cashDo.setRid(borrowId);

		try {
			/// 临时解决方案
			if (!doRish) {
				logger.info("风控拒绝过");
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}

			String cardNo = card.getCardNumber();
			String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
			cashDo.setRishOrderNo(riskOrderNo);
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.apply.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String borrowTime = sdf.format(new Date(System.currentTimeMillis()));
			RiskVerifyRespBo verybo = riskUtil.verifyNew(ObjectUtils.toString(userId, ""), afBorrowCashDo.getBorrowNo(), type, "20", afBorrowCashDo.getCardNumber(), appName, ipAddress, blackBox, riskOrderNo, 
					accountDo.getUserName(), amount, afBorrowCashDo.getPoundage(), borrowTime);
			
			if (verybo.isSuccess()) {
				delegatePay(verybo.getConsumerNo(), verybo.getOrderNo(), verybo.getResult());
			}
			return resp;
		} catch (Exception e) {
			cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
	}

	private void delegatePay(String consumerNo, String orderNo, String result) {
		Long userId = Long.parseLong(consumerNo);
		AfBorrowCashDo cashDo = new AfBorrowCashDo();
		// cashDo.setRishOrderNo(orderNo);
		Date currDate = new Date(System.currentTimeMillis());

		AfUserDo afUserDo = afUserService.getUserById(Long.parseLong(consumerNo));
		AfUserAccountDo accountInfo = afUserAccountService.getUserAccountByUserId(Long.parseLong(consumerNo));
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByRishOrderNo(orderNo);
		cashDo.setRid(afBorrowCashDo.getRid());

		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(Long.parseLong(consumerNo));

		List<String> whiteIdsList = new ArrayList<String>();
		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		// 判断是否在白名单里面
		AfResourceDo whiteListInfo = afResourceService.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
		if (whiteListInfo != null) {
			whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","), new Converter<String, String>() {
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
			AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(Long.parseLong(consumerNo));
			// 打款
			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(), userDto.getRealName(), afBorrowCashDo.getCardNumber(), consumerNo + "", card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02", UserAccountLogType.BorrowCash.getCode(), afBorrowCashDo.getRid() + "");
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.agree.getCode());
			Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
			Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(cashDo.getGmtArrival());
			Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
			cashDo.setGmtPlanRepayment(repaymentDay);
			//减少额度
			accountInfo.setUsedAmount(BigDecimalUtil.add(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
			afUserAccountService.updateOriginalUserAccount(accountInfo);
			//增加日志
			AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.BorrowCash, 
					afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
			afUserAccountService.updateOriginalUserAccount(accountInfo);
			afUserAccountLogDao.addUserAccountLog(accountLog);
			
			if (!upsResult.isSuccess()) {
				logger.info("upsResult error:" + FanbeiExceptionCode.BANK_CARD_PAY_ERR);
				cashDo.setStatus(AfBorrowCashStatus.transedfail.getCode());
			}
			afBorrowCashService.updateBorrowCash(cashDo);
			addTodayTotalAmount(currentDay, afBorrowCashDo.getAmount());
		} else if (StringUtils.equals("30", result)) {
			cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
			cashDo.setReviewDetails(AfBorrowCashReviewStatus.refuse.getName());
			jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
		} else {
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.waitfbReview.getCode());
		}
		afBorrowCashService.updateBorrowCash(cashDo);
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
	
	public AfBorrowCashDo borrowCashDoWithAmount(BigDecimal amount, String type, String latitude, String longitude, AfUserBankcardDo afUserBankcardDo, String city, String province,
			String county, String address, Long userId, int currentDay) {

		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list);

		this.checkSwitch(rate, currentDay);

		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
		BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());

		BigDecimal serviceRate = bankService;
		BigDecimal poundageRate = poundage;
		BigDecimal serviceAmountDay = serviceRate.multiply(amount);
		BigDecimal poundageAmountDay = poundageRate.multiply(amount);

		Integer day = NumberUtil.objToIntDefault(type, 0);

		BigDecimal latitudeBig = NumberUtil.objToBigDecimalDefault(latitude, BigDecimal.ZERO);
		BigDecimal longitudeBig = NumberUtil.objToBigDecimalDefault(longitude, BigDecimal.ZERO);
		BigDecimal rateAmount = BigDecimalUtil.multiply(serviceAmountDay, new BigDecimal(day));
		BigDecimal poundageBig = BigDecimalUtil.multiply(poundageAmountDay, new BigDecimal(day));

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
		afBorrowCashDo.setPoundage(poundageBig);
		afBorrowCashDo.setAddress(address);
		afBorrowCashDo.setArrivalAmount(BigDecimalUtil.subtract(amount, poundageBig));
		afBorrowCashDo.setPoundageRate(poundage);
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
			temp.setAmount(new BigDecimal(0));
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
	 * @param usableAmount
	 * @return
	 */
	private BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
		//可使用额度
		Integer amount = usableAmount.intValue();
		
		return new BigDecimal(amount/100*100);
		
	}

	
}
