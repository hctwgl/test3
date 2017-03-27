/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：品牌订单进行支付
 * @author xiaotianjian 2017年3月27日上午10:53:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("payOrderApi")
public class PayOrderApi implements ApiHandle {

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
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),null);
		Long payId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("payId"),null);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		
		if (orderId == null || payId == null || StringUtils.isEmpty(payPwd)) {
			logger.error("orderId is empty or payId is empty or payId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		//TODO获取用户订单
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo ==  null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if(payId >= 0){
			String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
			if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
		
		Map<String,Object> map;
		if(payId < 0 ){//微信支付
			map = afOrderService.payBrandOrder(payId, orderInfo, null, null);
			resp.setResponseData(map);
		} else if (payId > 0){//银行卡支付 代收
			//TODO 获取用户银行卡
			AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(payId);
			if(null == card){
				throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			map = afOrderService.payBrandOrder(payId, orderInfo, null, null);
			
			UpsUtil.collect(orderInfo.getOrderNo(),orderInfo.getSaleAmount(), userId+"", afUserAccountDo.getRealName(), card.getMobile(), 
					card.getBankCode(), card.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_BRAND_SHOP, "手机充值", "02",OrderType.BOLUOME.getCode());
			
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
