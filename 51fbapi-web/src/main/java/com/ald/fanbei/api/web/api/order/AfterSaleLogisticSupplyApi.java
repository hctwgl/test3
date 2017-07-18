package com.ald.fanbei.api.web.api.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
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
 * @类描述：售后货物回寄提交确认
 * @author chengkang 2017年7月10日上午11:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("afterSaleLogisticSupplyApi")
public class AfterSaleLogisticSupplyApi implements ApiHandle{

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
		//回寄物流公司
		String logisticsCompany = ObjectUtils.toString(requestDataVo.getParams().get("logisticsCompany"),"");
		//回寄物流单号
		String logisticsNo = ObjectUtils.toString(requestDataVo.getParams().get("logisticsNo"),"");
		
		//参数基本检查
		if(orderId == null || StringUtil.isBlank(logisticsCompany) || StringUtil.isBlank(logisticsNo)){
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
		
		if(!AfAftersaleApplyStatus.WAIT_GOODS_BACK.getCode().equals(afAftersaleApplyDo.getStatus())){
			throw new FanbeiException(FanbeiExceptionCode.AFTERSALE_PROCESSING);
		}
		
		//更新售后记录
		afAftersaleApplyDo.setLogisticsCompany(logisticsCompany);
		afAftersaleApplyDo.setLogisticsNo(logisticsNo);
		afAftersaleApplyDo.setStatus(AfAftersaleApplyStatus.GOODS_BACKIING.getCode());
		afAftersaleApplyService.updateById(afAftersaleApplyDo);
		
		return resp;
	}
	
}
