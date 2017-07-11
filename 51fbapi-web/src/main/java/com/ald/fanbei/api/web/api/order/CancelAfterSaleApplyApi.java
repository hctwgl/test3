package com.ald.fanbei.api.web.api.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAftersaleApplyStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：用户取消售后申请
 * @author chengkang 2017年7月10日上午11:00:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("cancelAfterSaleApplyApi")
public class CancelAfterSaleApplyApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	
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
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(orderId);
		if(afAftersaleApplyDo==null){
			throw new FanbeiException(FanbeiExceptionCode.AFTERSALE_APPLY_NOT_EXIST);
		}
		if(AfAftersaleApplyStatus.NEW.getCode().equals(afAftersaleApplyDo.getStatus()) || AfAftersaleApplyStatus.NOTPASS.getCode().equals(afAftersaleApplyDo.getStatus())){
			//更新售后申请状态
			afAftersaleApplyDo.setStatus(AfAftersaleApplyStatus.CLOSE.getCode());
			afAftersaleApplyService.updateById(afAftersaleApplyDo);
			
			//更新订单状态，返回至售后申请前状态
			if(StringUtil.isNotBlank(orderInfo.getPreStatus())){
				orderInfo.setStatus(orderInfo.getPreStatus());
				afOrderService.updateOrder(orderInfo);
			}
			logger.info("cancelAfterSaleApply success. orderId="+orderId+",userId="+userId);
		}else{
			throw new FanbeiException(FanbeiExceptionCode.AFTERSALE_PROCESSING);
		}
		
		return resp;
	}
	
}
