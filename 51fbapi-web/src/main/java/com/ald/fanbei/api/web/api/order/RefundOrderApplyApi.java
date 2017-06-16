package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：订单退款申请
 * @author chengkang 2017年6月15日下午16:49:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("refundOrderApplyApi")
public class RefundOrderApplyApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfOrderRefundService afOrderRefundService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String orderNo = ObjectUtils.toString(requestDataVo.getParams().get("orderNo"),"");
		String contactsMobile = ObjectUtils.toString(requestDataVo.getParams().get("contactsMobile"),"");
		
		//参数基本检查
		if(StringUtil.isBlank(orderNo) || StringUtil.isBlank(contactsMobile) ){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoByOrderNoAndUserId(orderNo,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		
		//退单处理，如果不存在退单项，则新增退单项，如果存在，且手机号有改动，则更新退单中的联系人手机号
		AfOrderRefundDo afOrderRefundDo = afOrderRefundService.getOrderRefundByOrderId(orderInfo.getRid());
		if(afOrderRefundDo==null){
			//插入最新退款记录
			//退款方式默认为订单支付方式 ，如果为空，则默认微信
			PayType payType = PayType.findRoleTypeByCode(orderInfo.getPayType());
			if(payType==null){
				payType = PayType.WECHAT;	
			}
			afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo("", orderInfo.getActualAmount(), BigDecimal.ZERO, userId, orderInfo.getRid(),
					orderInfo.getOrderNo(), OrderRefundStatus.NEW,payType,StringUtils.EMPTY, null,"自营商品用户退款申请",RefundSource.USER.getCode(),StringUtils.EMPTY);
			afOrderRefundDo.setContactsMobile(contactsMobile);
			afOrderRefundService.addOrderRefund(afOrderRefundDo);
			
			//订单状态改为退款中
			orderInfo.setStatus(OrderStatus.WAITING_REFUND.getCode());
			afOrderService.updateOrder(orderInfo);
			return resp;
		}else{
			//退款记录已存在，校验状态等各种信息，如果退款未完成，更新退款单中的联系人手机号信息
			if(OrderRefundStatus.FINISH.getCode().equals(afOrderRefundDo.getStatus())){
				//返回提示订单已处理完成
				throw new FanbeiException(FanbeiExceptionCode.REFUND_HAVE_SUCCESS);
			}else{
				//更新订单中联系人手机号信息
				afOrderRefundDo.setContactsMobile(contactsMobile);
				afOrderRefundService.updateOrderRefund(afOrderRefundDo);
			}
		}
		
		return resp;
	}

}
