package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：获取现金借款确认页面
 *@author 何鑫 2017年2月18日  16:46:23
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashConfirmInfoApi")
public class GetCashConfirmInfoApi implements ApiHandle{
	
	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfResourceService afResourceService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		//获取利率说明信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH);
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		if(null == card){
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
		//账户关联信息
		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
		Map<String, Object> data = getCashInfo(resource, card, userDto,context);
		resp.setResponseData(data);
		return resp;
	}
	private Map<String, Object> getCashInfo(AfResourceDo resource,AfUserBankcardDo card,AfUserAccountDo userDto,FanbeiContext context){
		Map<String, Object> data = new HashMap<String, Object>();
		BigDecimal usableAmount = userDto.getAuAmount().divide(new BigDecimal(Constants.DEFAULT_CASH_DEVIDE),2,BigDecimal.ROUND_HALF_UP).subtract(userDto.getUcAmount());
		if((userDto.getAuAmount().subtract(userDto.getUsedAmount())).compareTo(usableAmount)==-1){
			usableAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount());
		}
		data.put("usableAmount",usableAmount);
		data.put("cardNo", StringUtil.getLastString(card.getCardNumber(),4));
		data.put("cardName",card.getBankName());
		data.put("cardId", card.getRid());
		data.put("cardIcon", card.getBankIcon());
		if(context.getAppVersion()>330){
			String[] range = StringUtil.split(resource.getValue2(), ",");
			BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
			BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
			if(null != range && range.length==2){
				rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
				rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
			}
			BigDecimal borrowCashPoundage = new BigDecimal(resource.getValue1());
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userDto.getUserId());
			if (poundageRateCash != null) {
				borrowCashPoundage = new BigDecimal(poundageRateCash.toString());
			}
			data.put("cashRate", new BigDecimal(resource.getValue()));
			data.put("poundageRate", borrowCashPoundage);
			data.put("minPoundage", rangeBegin);
			data.put("maxPoundage", rangeEnd);
		}
		data.put("desc", new StringBuffer("日利率").append(new BigDecimal(resource.getValue()).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString()).append("%"));
		return data;
	}
}
