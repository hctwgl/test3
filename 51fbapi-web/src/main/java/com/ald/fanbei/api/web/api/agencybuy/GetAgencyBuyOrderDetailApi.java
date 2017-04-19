package com.ald.fanbei.api.web.api.agencybuy;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfAgentOrederDetailInforVo;


@Component("GetAgencyBuyOrderDetailApi")
public class GetAgencyBuyOrderDetailApi implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),0);
		
		if (orderId == 0) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
			 
		}
		
		
		AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);
		AfAgentOrderDo afAgentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderId);
		
		AfAgentOrederDetailInforVo agentOrderDetailVo = getAgentOrderDetailInforVo(afOrderDo, afAgentOrderDo);
		
		resp.setResponseData(agentOrderDetailVo);
		return resp;
		
	}
	
	private AfAgentOrederDetailInforVo getAgentOrderDetailInforVo (AfOrderDo afOrderDo, AfAgentOrderDo afAgentOrderDo){
		
		AfAgentOrederDetailInforVo agentOrderDetailVo = new AfAgentOrederDetailInforVo();
		
		agentOrderDetailVo.setGoodName(afOrderDo.getGoodsName());
		agentOrderDetailVo.setGoodsIcon(afOrderDo.getGoodsIcon());
		agentOrderDetailVo.setCount(afOrderDo.getCount());
		agentOrderDetailVo.setRebateAmount(afOrderDo.getRebateAmount());
		String gmtCreate = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT,afOrderDo.getGmtCreate());
		agentOrderDetailVo.setGmtCreate(gmtCreate);
		agentOrderDetailVo.setStatus(afOrderDo.getStatus());
		
		agentOrderDetailVo.setMobile(afAgentOrderDo.getMobile());
		agentOrderDetailVo.setCapture(afAgentOrderDo.getCapture());
		agentOrderDetailVo.setRemark(afAgentOrderDo.getRemark());
		
		
		return agentOrderDetailVo;
	}

}
