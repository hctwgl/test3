/**
 * 
 */
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

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
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
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if (StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);

		}
		String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
		String pwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"));
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		String latitude = ObjectUtils.toString(requestDataVo.getParams().get("latitude"));
		String longitude = ObjectUtils.toString(requestDataVo.getParams().get("longitude"));
		String province = ObjectUtils.toString(requestDataVo.getParams().get("province"));
		String city = ObjectUtils.toString(requestDataVo.getParams().get("city"));
		String county = ObjectUtils.toString(requestDataVo.getParams().get("county"));
		String address = ObjectUtils.toString(requestDataVo.getParams().get("address"));

		if (StringUtils.equals(amountStr, "") || AfBorrowCashType.findRoleTypeByCode(type) == null
				|| StringUtils.equals(pwd, "") || StringUtils.equals(latitude, "") || StringUtils.equals(longitude, "")
				|| StringUtils.equals(province, "") || StringUtils.equals(city, "") || StringUtils.equals(county, "")) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		if (borrowCashDo != null && (!StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.closed.getCode())
				&& !StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode()))) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);

		}
		
		AfBorrowCashDo afBorrowCashDo = borrowCashDoWithAmount(amount, type, latitude, longitude, card, city, province, county, address, userId);
		 afBorrowCashService.addBorrowCash(afBorrowCashDo);
		 Long borrowId = afBorrowCashDo.getRid();
		 AfBorrowCashDo cashDo = new AfBorrowCashDo();
		 cashDo.setRid(borrowId);	
		try {
			 RiskVerifyRespBo result = riskUtil.verify(ObjectUtils.toString(userId, "") , "20", afBorrowCashDo.getCardNumber());
				Date currDate = new Date();

			 
			 AfUserDo afUserDo= afUserService.getUserById(userId);

			 if(StringUtils.equals("10", result.getResult())){
				 jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
				//审核通过
				 cashDo.setGmtArrival(currDate);
				 cashDo.setStatus(AfBorrowCashStatus.transed.getCode());
				 AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
				 //打款
					UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(), userDto.getRealName(), afBorrowCashDo.getCardNumber(), afBorrowCashDo.getUserId()+"",
							card.getMobile(), card.getBankName(), card.getBankCode(),Constants.DEFAULT_BORROW_PURPOSE, "02",
							UserAccountLogType.BorrowCash.getCode(),borrowId+"");
					cashDo.setReviewStatus(AfBorrowCashReviewStatus.agree.getCode());
					if(!upsResult.isSuccess()){
						logger.info("upsResult error:"+FanbeiExceptionCode.BANK_CARD_PAY_ERR);
						cashDo.setStatus(AfBorrowCashStatus.transedfail.getCode()); 
					}
					afBorrowCashService.updateBorrowCash(cashDo);
				 
			 }else if(StringUtils.equals("30", result.getResult())){
				 cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				 cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
				 cashDo.setReviewDetails(AfBorrowCashReviewStatus.refuse.getName());
				 jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);

			 }else{
				 cashDo.setReviewStatus(AfBorrowCashReviewStatus.waitfbReview.getCode());
			 }
			 afBorrowCashService.updateBorrowCash(cashDo);
			 return resp;
		} catch (Exception e) {
			 cashDo.setStatus(AfBorrowCashStatus.closed.getCode());

			 afBorrowCashService.updateBorrowCash(cashDo);

			 	throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);


		}

	}
	
	public AfBorrowCashDo borrowCashDoWithAmount(BigDecimal amount,String type,String latitude,String longitude,
			AfUserBankcardDo afUserBankcardDo,String city,String province,String county,String address,Long userId){
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();

		Map<String, Object> rate = getObjectWithResourceDolist(list);

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
		afBorrowCashDo
				.setArrivalAmount(BigDecimalUtil.subtract(BigDecimalUtil.subtract(amount, rateAmount), poundageBig));
		;
		return afBorrowCashDo;
	}

}
