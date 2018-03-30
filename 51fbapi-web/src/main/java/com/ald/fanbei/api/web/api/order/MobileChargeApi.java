/**
 * 
 */
package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.util.CommonUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：话费充值下单
 * @author 何鑫 2017年2月16日下午14:15:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("mobileChargeApi")
public class MobileChargeApi implements ApiHandle {

	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	private AfOrderService afOrderService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		Long bankId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("bankId")),0);
		BigDecimal money = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("amount")),BigDecimal.ZERO);
		BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if(bankId>0){
			String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
			if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
		AfUserCouponDto coupon =  afUserCouponService.getUserCouponById(couponId);
		if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}
		
		Map<String,Object> map;
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		if(bankId<0){//微信支付
			map = afOrderService.createMobileChargeOrder(null,context.getUserName(),userId, coupon, money, mobile, rebateAmount,bankId,"",afUserAccountDo,blackBox,bqsBlackBox);
			resp.setResponseData(map);
		}else{//银行卡支付 代收
			AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(bankId);
			if(null == card){
				throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			map = afOrderService.createMobileChargeOrder(card,context.getUserName(),userId, coupon, money, mobile, rebateAmount,bankId, CommonUtil.getIpAddr(request),afUserAccountDo,blackBox,bqsBlackBox);
			UpsCollectRespBo upsResult = (UpsCollectRespBo) map.get("resp");
			if(!upsResult.isSuccess()){
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			Map<String,Object> newMap = new HashMap<String,Object>();
			newMap.put("outTradeNo", upsResult.getOrderNo());
			newMap.put("tradeNo", upsResult.getTradeNo());
			newMap.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
			resp.setResponseData(newMap);
		}
		return resp;
	}
}
