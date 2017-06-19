package com.ald.fanbei.api.web.api.order;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：刪除订单
 * @author chengkang 2017年6月15日下午20:46:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteOrderInfoApi")
public class DeleteOrderInfoApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));
		
		//参数基本检查
		if(orderId == null){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		
		//校验订单状态是否满足删除条件
		List<String> canDelStatus = new ArrayList<String>();
		canDelStatus.add(OrderStatus.NEW.getCode());
		canDelStatus.add(OrderStatus.FINISHED.getCode());
		canDelStatus.add(OrderStatus.REBATED.getCode());
		canDelStatus.add(OrderStatus.CLOSED.getCode());
		if(canDelStatus.contains(orderInfo.getStatus())){
			//订单状态改为删除
			int nums = afOrderService.deleteOrder(orderInfo.getRid());
			if(nums<=0){
				throw new FanbeiException(FanbeiExceptionCode.FAILED);
			}
		}else{
			throw new FanbeiException(FanbeiExceptionCode.ORDER_NOFINISH_CANNOT_DELETE);
		}
		return resp;
	}

}
