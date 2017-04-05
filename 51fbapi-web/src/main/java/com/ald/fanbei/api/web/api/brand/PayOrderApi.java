/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
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
	@Resource
	BoluomeUtil boluomeUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),null);
		Long payId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("payId"),null);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"),null);
		  
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		
		if (orderId == null || payId == null) {
			logger.error("orderId is empty or payId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		//TODO获取用户订单
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo ==  null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		if(payId >= 0){
			String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
		try {
			
			Map<String,Object> result = afOrderService.payBrandOrder(payId, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(), orderInfo.getSaleAmount(), nper);
			if (payId == 0) {
				boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, userId, orderInfo.getSaleAmount());
			}
			resp.setResponseData(result);
		} catch (FanbeiException exception) {
			throw new FanbeiException("pay order failed", exception.getErrorCode());
		} catch (Exception e) {
			logger.error("pay order failed e = {}", e);
			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		return resp;
	}
}
