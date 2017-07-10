package com.ald.fanbei.api.web.api.order;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfAftersaleApplyVo;
import com.ald.fanbei.api.web.vo.AfGoodsVo;

/**
 * @类描述：获取订单售后信息
 * @author chengkang 2017年7月10日上午11:48:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderAfterSaleInfoApi")
public class GetOrderAfterSaleInfoApi implements ApiHandle{

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
		
		AfAftersaleApplyVo afAftersaleApplyVo = getAftersaleApplyVo(afAftersaleApplyDo);
		resp.setResponseData(afAftersaleApplyVo);
		return resp;
	}
	
	//售后信息vo转换
	private AfAftersaleApplyVo getAftersaleApplyVo(AfAftersaleApplyDo asApplyDo){
		AfAftersaleApplyVo afAftersaleApplyVo = new  AfAftersaleApplyVo();
		List<AfGoodsVo> goodsList = new ArrayList<AfGoodsVo>();
		
		
		
		return afAftersaleApplyVo;
		
	}
}
