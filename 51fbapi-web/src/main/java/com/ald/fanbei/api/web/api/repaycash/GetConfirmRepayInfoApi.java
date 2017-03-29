/**
 * 
 */
package com.ald.fanbei.api.web.api.repaycash;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import org.dbunit.util.Base64;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月25日下午2:19:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmRepayInfoApi")
public class GetConfirmRepayInfoApi implements ApiHandle {

	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), BigDecimal.ZERO);
		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
		BigDecimal userAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
		Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowId")), 0l);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);

		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
		if (userDto == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		if(cardId!=-1){
			String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
			if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
		
		AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
		if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}
		
		Map<String,Object> map;
		if(cardId==-2){//余额支付
			map	=afRepaymentBorrowCashService.createRepayment(repaymentAmount, actualAmount, coupon, userAmount, borrowId, cardId, userId, "", userDto);
			
			resp.addResponseData("refId", map.get("refId"));
			resp.addResponseData("type", map.get("type"));
		}else if(cardId==-1){//微信支付
			map	=afRepaymentBorrowCashService.createRepayment(repaymentAmount, actualAmount, coupon, userAmount, borrowId, cardId, userId, "", userDto);

			resp.setResponseData(map);
		}else if(cardId>0){//银行卡支付
			AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
			if(null == card){
				throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			map	=afRepaymentBorrowCashService.createRepayment(repaymentAmount, actualAmount, coupon, userAmount, borrowId, cardId, userId, request.getRemoteAddr(), userDto);

			//代收
			UpsCollectRespBo upsResult = (UpsCollectRespBo) map.get("resp");
			if(!upsResult.isSuccess()){
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			Map<String,Object> newMap = new HashMap<String,Object>();
			newMap.put("outTradeNo", upsResult.getOrderNo());
			newMap.put("tradeNo", upsResult.getTradeNo());
			newMap.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
			newMap.put("refId", map.get("refId"));
			newMap.put("type", map.get("type"));
			resp.setResponseData(newMap);
		}
		
		return resp;
	}

}
