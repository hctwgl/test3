/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
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
		
		if (context.getAppVersion() >= 340) {
			tongdunUtil.getBorrowCashResult(requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request), context.getUserName(), context.getMobile(), accountDo.getIdNumber(), 
					accountDo.getRealName(), "", requestDataVo.getMethod(), "");
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
		//---------------------------------------------
//		AfUserBankcardDo card = new AfUserBankcardDo();
//		card.setBankName("中国农业银行");
//		card.setCardNumber("6228480322828314011");
		//---------------------------------------------
		AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);

		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);
		AfBorrowCashDo afBorrowCashDo = borrowCashDoWithAmount(amount, type, latitude, longitude, card, city, province, county, address, userId, currentDay);

		if (borrowCashDo != null && (!StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.closed.getCode()) 
				&& !StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode()))) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
		}
		afBorrowCashService.addBorrowCash(afBorrowCashDo);
		
		Long borrowId = afBorrowCashDo.getRid();
		
		AfBorrowCashDo cashDo = new AfBorrowCashDo();
		cashDo.setRid(borrowId);

		try {
			RiskVerifyRespBo result = riskUtil.verify(ObjectUtils.toString(userId, ""), "20", afBorrowCashDo.getCardNumber(), appName, ipAddress, blackBox, afBorrowCashDo.getBorrowNo());

			cashDo.setRishOrderNo(result.getOrderNo());
			cashDo.setReviewStatus(AfBorrowCashReviewStatus.waitfbReview.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			
			return resp;
		} catch (Exception e) {
			cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
			afBorrowCashService.updateBorrowCash(cashDo);
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}

	}


	public AfBorrowCashDo borrowCashDoWithAmount(BigDecimal amount, String type, String latitude, String longitude, AfUserBankcardDo afUserBankcardDo, 
			String city, String province, String county, String address, Long userId, int currentDay) {

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
		afBorrowCashDo.setArrivalAmount(BigDecimalUtil.subtract(BigDecimalUtil.subtract(amount, rateAmount), poundageBig));
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
