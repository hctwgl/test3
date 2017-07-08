package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午6:28:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmBorrowInfoApi")
public class GetConfirmBorrowInfoApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Map<String, Object> data = new HashMap<String, Object>();
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list);
		if (!StringUtils.equals(rate.get("supuerSwitch").toString(), YesNoStatus.YES.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_SWITCH_NO);

		}
		data.put("realNameStatus", authDo.getRealnameStatus());
		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			data.put("riskStatus", RiskStatus.A.getCode());
		} else {
			data.put("riskStatus", authDo.getRiskStatus());
		}
		
		data.put("faceStatus", authDo.getFacesStatus());
		
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		
		data.put("idNumber", Base64.encodeString(accountDo.getIdNumber()));
		data.put("realName", accountDo.getRealName());
		
		// 判断是否绑定主卡
		data.put("isBind", authDo.getBankcardStatus());
		Boolean isPromote = true;
//		if (!StringUtils.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getFacesStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getMobileStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getContactorStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getLocationStatus(), YesNoStatus.YES.getCode()) || !StringUtils.equals(authDo.getTeldirStatus(), YesNoStatus.YES.getCode())) {
		if (!StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			isPromote = false;
		}
		data.put("isPromote", isPromote ? YesNoStatus.YES.getCode() : YesNoStatus.NO.getCode());

		if (isPromote == true || StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.YES.getCode())) {
			// 可以借钱
			String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
			String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
			if (StringUtils.equals(amountStr, "") || AfBorrowCashType.findRoleTypeByCode(type) == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_AMOUNT_ERROR);
			}
			/** 去掉后台配置的金额限制(用户的借款额度根据可用额度进行限制)
			AfResourceDo limitRangeResource = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashRange.getCode());
			if (limitRangeResource != null) {
				BigDecimal limitRangeStart =  new BigDecimal(limitRangeResource.getValue1());
				BigDecimal limitRangeEnd =  new BigDecimal(limitRangeResource.getValue());
				BigDecimal amount = new BigDecimal(amountStr);
				if (amount.compareTo(limitRangeStart) < 0 || amount.compareTo(limitRangeEnd) > 0) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.APPLY_CASHED_AMOUNT_ERROR);
				}
			}
			*/
			BigDecimal  usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
			BigDecimal accountBorrow = calculateMaxAmount(usableAmount);
			if(StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode()) && accountBorrow.compareTo(new BigDecimal(amountStr))<0){
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_MORE_ACCOUNT_ERROR);
			}
			
			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);

			boolean isCanBorrowCash = afBorrowCashService.isCanBorrowCash(userId);
			if (!isCanBorrowCash) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
			}

			BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);

			// BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
			// BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
			// BigDecimal bankService =bankRate.multiply(bankDouble).divide(new BigDecimal(360),6,RoundingMode.HALF_UP);
			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			// BigDecimal serviceRate =bankService.add(poundageRate);
			// BigDecimal serviceAmountDay = serviceRate.multiply(amount);

			BigDecimal serviceAmountDay = poundageRate.multiply(amount);// 新版本(v3.6) 服务费的计算 去掉利息

			Integer day = NumberUtil.objToIntDefault(type, 0);

			BigDecimal serviceAmount = serviceAmountDay.multiply(new BigDecimal(day));
			data.put("serviceAmount", serviceAmount);
			data.put("amount", amount);
			data.put("arrivalAmount", BigDecimalUtil.subtract(amount, serviceAmount));
			data.put("banKName", afUserBankcardDo.getBankName());
			data.put("bankCard", afUserBankcardDo.getCardNumber());
			data.put("type", type);
		}
		resp.setResponseData(data);
		return resp;
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
