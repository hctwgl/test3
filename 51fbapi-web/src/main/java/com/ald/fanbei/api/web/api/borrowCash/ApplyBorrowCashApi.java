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
		if (StringUtils.equals(amountStr, "") ||  AfBorrowCashType.findRoleTypeByCode(type)==null|| StringUtils.equals(pwd, "")||
				StringUtils.equals(latitude, "")|| StringUtils.equals(longitude, "")||
				StringUtils.equals(province, "")|| StringUtils.equals(city, "")|| StringUtils.equals(county, "")) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(amountStr, BigDecimal.ZERO);
		AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
		AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		if(borrowCashDo!=null&&( !StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.closed.getCode())||!StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode()) ||
				!StringUtils.equals(borrowCashDo.getStatus(), AfBorrowCashStatus.refuse.getCode()))){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_STATUS_ERROR);

		}
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();

		Map<String, Object> rate = getObjectWithResourceDolist(list);

		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService =bankRate.multiply(bankDouble).divide(new BigDecimal( 360),6,RoundingMode.HALF_UP);
		BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());


		BigDecimal serviceRate =bankService.divide(new BigDecimal(100));
		BigDecimal poundageRate =poundage.divide(new BigDecimal(100)) ;
		BigDecimal serviceAmountDay =serviceRate.multiply(amount) ;
		BigDecimal poundageAmountDay =poundageRate.multiply(amount);
		
		
		Integer day = NumberUtil.objToIntDefault(type, 0);
		
		BigDecimal latitudeBig = NumberUtil.objToBigDecimalDefault(latitude, BigDecimal.ZERO);
		BigDecimal longitudeBig = NumberUtil.objToBigDecimalDefault(longitude, BigDecimal.ZERO);
		BigDecimal rateAmount = BigDecimalUtil.multiply(serviceAmountDay, new BigDecimal(day));
		BigDecimal poundageBig = BigDecimalUtil.multiply(poundageAmountDay, new BigDecimal(day));

		AfBorrowCashDo afBorrowCashDo = new AfBorrowCashDo();
		afBorrowCashDo.setAmount(amount);
		afBorrowCashDo.setCardName(afUserBankcardDo.getBankName());
		afBorrowCashDo.setCardNumber( afUserBankcardDo.getCardNumber());
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
		afBorrowCashService.addBorrowCash(afBorrowCashDo);
		
		return resp;
	}

}
