/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Map<String, Object> data = new HashMap<String, Object>();

		//判断是否绑定主卡
		data.put("isBind", authDo.getBankcardStatus());
		Boolean isPromote = true;
		if (!StringUtils.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getFacesStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getMobileStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getYdStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getContactorStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getLocationStatus(), YesNoStatus.YES.getCode())||
				!StringUtils.equals(authDo.getTeldirStatus(), YesNoStatus.YES.getCode())) {
			isPromote = false;
			
		}
		data.put("isPromote", isPromote? YesNoStatus.YES.getCode(): YesNoStatus.NO.getCode());

		if(isPromote == true || StringUtils.equals(authDo.getBankcardStatus(), YesNoStatus.YES.getCode())){
			//可以借钱
			String amountStr = ObjectUtils.toString(requestDataVo.getParams().get("amount"));
			String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
			if (StringUtils.equals(amountStr, "") || AfBorrowCashType.findRoleTypeByCode(type)==null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_AMOUNT_ERROR);
			}
			
			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
			
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
			if(afBorrowCashDo!=null&&( !StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.closed.getCode())&&
					!StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode()) )){
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);
			}
			
			BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);

			List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
			Map<String, Object> rate = getObjectWithResourceDolist(list);
			BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
			BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
			BigDecimal bankService =bankRate.multiply(bankDouble).divide(new BigDecimal( 360),6,RoundingMode.HALF_UP);
			BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

			BigDecimal serviceRate =bankService.add(poundageRate);

			BigDecimal serviceAmountDay = serviceRate.multiply(amount);
			
			Integer day = NumberUtil.objToIntDefault(type, 0);

			BigDecimal serviceAmount =serviceAmountDay.multiply(new BigDecimal(day));
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

}
