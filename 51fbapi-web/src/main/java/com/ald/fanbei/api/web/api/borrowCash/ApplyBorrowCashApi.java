package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：申请借钱
 * 
 * @author suweili 2017年3月25日下午1:06:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowCashApi")
public class ApplyBorrowCashApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	BizCacheUtil bizCacheUtil;
	
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
	AfUserCouponService afUserCouponService;

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

		try{
			//纬度3.86~53.55,经度73.66~135.05
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("latitude_longitude_limit");
			if(afResourceDo!=null&&afResourceDo.getValue().equals(YesNoStatus.YES.getCode())){
				BigDecimal latitudeDecimal =  new BigDecimal(latitude) ;
				BigDecimal longitudeDecimal =  new BigDecimal(longitude) ;
				BigDecimal laMin= new BigDecimal( afResourceDo.getValue1().split(",")[0]);
				BigDecimal laMax= new BigDecimal( afResourceDo.getValue1().split(",")[1]);
				BigDecimal loMin= new BigDecimal( afResourceDo.getValue2().split(",")[0]);
				BigDecimal loMax= new BigDecimal( afResourceDo.getValue2().split(",")[1]);
				if(latitudeDecimal.compareTo(laMin)==-1 ||latitudeDecimal.compareTo(laMax)==1 ){
					logger.error("position exception,user_name:"+accountDo.getUserName());
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.POSITION_EXCEPTION);
				}
				if(longitudeDecimal.compareTo(loMin)==-1 ||longitudeDecimal.compareTo(loMax)==1 ){
					logger.error("position exception,user_name:"+accountDo.getUserName()+",at:"+latitudeDecimal+","+longitudeDecimal);
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.POSITION_EXCEPTION);
				}
			}
		}catch (Exception e){
			logger.error("position limit exception,user_name:"+accountDo.getUserName(),e);
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
//				|| !StringUtils.equals(authDo.getContactorStatus(), YesNoStatus.YES.getCode())/* || !StringUtils.equals(authDo.getLocationStatus(), YesNoStatus.YES.getCode())*/
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
		
		if(afnewstBorrowCashDo!=null && AfBorrowCashReviewStatus.refuse.getCode().equals(afnewstBorrowCashDo.getReviewStatus())){
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
		
		BigDecimal accountBorrow = accountDo.getBorrowCashAmount();
		if(accountBorrow.compareTo(amount)<0){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);

		}
		///// 临时处理，如果当天内有申请，以最后一条的状态为准 end hy 2017年5月11日09:54:20//////

		AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);

		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);
		AfBorrowCashDo afBorrowCashDo = borrowCashDoWithAmount(amount, type, latitude, longitude, card, city, province, county, address, userId, currentDay);

		if (borrowCashDo != null && (!StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.closed.getCode())
				&& !StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode()))) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}
		//FIXME Add by jrb, 如果有免息券，则实际到账金额为借钱金额
		try {
			if (doRish) {
				String couponId = ObjectUtils.toString(requestDataVo.getParams().get("couponId"));
				logger.error("ApplyBorrowCashApi couponId=>" + couponId);
				if (!StringUtils.isBlank(couponId)) {
					AfUserCouponDo afUserCouponDoTmp = new AfUserCouponDo();
					afUserCouponDoTmp.setCouponId(Long.parseLong(couponId));
					afUserCouponDoTmp.setUserId(userId);
					logger.error("ApplyBorrowCashApi userId=>" + userId);
					AfUserCouponDo afUserCouponDo = afUserCouponService.getUserCouponByDo(afUserCouponDoTmp);
					if(afUserCouponDo != null) {
						afUserCouponDo.setStatus(CouponStatus.USED.getCode());
						afBorrowCashDo.setArrivalAmount(afBorrowCashDo.getAmount());
						// 更新券的状态为已使用
						logger.error("ApplyBorrowCashApi afUserCouponDo.getRid()=>" + afUserCouponDo.getRid());
						afUserCouponService.updateUserCouponSatusUsedById(afUserCouponDo.getRid());
					}
				}
			}
		} catch (Exception e){
			logger.error(e.getMessage());
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
			String riskOrderNo = riskUtil.getOrderNo("vefy",
					cardNo.substring(cardNo.length() - 4, cardNo.length()));
			cashDo.setRishOrderNo(riskOrderNo);
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.apply.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			
			riskUtil.verify(ObjectUtils.toString(userId, ""), "20", afBorrowCashDo.getCardNumber(), appName, ipAddress, blackBox,
					afBorrowCashDo.getBorrowNo(), null,riskOrderNo);

			return resp;
		} catch (Exception e) {
			cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}

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
		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
		if (poundageRateCash != null) {
			poundage = new BigDecimal(poundageRateCash.toString());
		}
		
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

}
