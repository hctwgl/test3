package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeBusinessInfoDto;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAftersaleApplyStatus;
import com.ald.fanbei.api.common.enums.AfOrderStatusMsgRemark;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOrderListVo;

/**
 * 
 * @类描述：获取订单列表
 * @author 何鑫 2017年2月17日下午16:41:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderListApi")
public class GetOrderListApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	AfTradeOrderService afTradeOrderService;
	@Resource
	AfTradeBusinessInfoService afTradeBusinessInfoService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
        Integer pageNo = NumberUtil.objToPageIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
        String orderStatus = ObjectUtils.toString(requestDataVo.getParams().get("orderStatus"),"");
        List<AfOrderDo> orderList = afOrderService.getOrderListByStatus(pageNo, orderStatus, userId);
        List<AfOrderListVo> orderVoList = new ArrayList<AfOrderListVo>();
        for (AfOrderDo afOrderDo : orderList) {
        	orderVoList.add(getOrderListVo(afOrderDo, context));
		}
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("orderList", orderVoList);
        map.put("pageNo", pageNo);
        resp.setResponseData(map);
		return resp;
	}

	private AfOrderListVo getOrderListVo(AfOrderDo order ,FanbeiContext context){
		AfOrderListVo vo = new AfOrderListVo();
		vo.setGmtCreate(order.getGmtCreate());
		vo.setGoodsIcon(order.getGoodsIcon());
		vo.setGoodsName(order.getGoodsName());
		vo.setOrderId(order.getRid());
		vo.setOrderNo(order.getOrderNo());
		String status =  order.getStatus();
		if (context.getAppVersion() < 364){
			if (status.equals(OrderStatus.DEALING.getCode())) {
				status =OrderStatus.PAID.getCode();
			}
		}
		vo.setOrderStatus(status);
		vo.setRebateAmount(order.getRebateAmount());
		vo.setType(order.getOrderType());
		vo.setSaleAmount(order.getSaleAmount());
		
		vo.setActualAmount(order.getActualAmount());
		vo.setCouponAmount(order.getSaleAmount().subtract(order.getActualAmount()));
		vo.setPayType(order.getPayType());
		//商品售价处理(订单价格除以商品数量)
		BigDecimal saleCount = NumberUtil.objToBigDecimalZeroToDefault(BigDecimal.valueOf(order.getCount()), BigDecimal.ONE);
		vo.setGoodsSaleAmount(order.getSaleAmount().divide(saleCount, 2));
		//售后相关设置
		Boolean isExistAftersaleApply = false;
		String afterSaleStatus = "";
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(order.getRid());
		if(afAftersaleApplyDo!=null){
			afterSaleStatus = afAftersaleApplyDo.getStatus();
			vo.setAfterSaleStatus(afAftersaleApplyDo.getStatus());
		}else{
			vo.setAfterSaleStatus("");
		}
		vo.setIsCanApplyAfterSale(afOrderService.isCanApplyAfterSale(order.getRid()));
		//状态备注及说明 
		String closeReason = "";
		closeReason = order.getCancelReason();
		if(StringUtil.isBlank(closeReason)){
			closeReason = order.getClosedReason();
		}
		if(afAftersaleApplyDo!=null && !AfAftersaleApplyStatus.CLOSE.getCode().equals(afAftersaleApplyDo.getStatus())){
			//如果存在售后申请记录并且未被用户关闭，则标识记录，后续结合订单关闭状态，确认订单关闭是退款还是其它操作导致
			isExistAftersaleApply = true;
		}
		AfOrderStatusMsgRemark orderStatusMsgRemark = AfOrderStatusMsgRemark.findRoleTypeByCodeAndOrderType(order.getStatus(), order.getOrderType(), order.getPayType(),
				order.getRebateAmount().compareTo(BigDecimal.ZERO)>0, afterSaleStatus,isExistAftersaleApply,closeReason,order.getStatusRemark());
		if(orderStatusMsgRemark!=null){
			vo.setOrderStatusMsg(orderStatusMsgRemark.getStatusMsg());
			vo.setOrderStatusRemark(orderStatusMsgRemark.getStatusRemark());	
		}else{
			vo.setOrderStatusMsg("");
			vo.setOrderStatusRemark("");
		}

		//商圈订单
		if(order.getOrderType().equals(OrderType.TRADE.getCode())) {
			List<AfTradeBusinessInfoDto> list = afTradeBusinessInfoService.getByOrderId(order.getRid());
			if(list != null && list.size() > 0) {
				AfTradeBusinessInfoDto dto = list.get(0);
				vo.setBusinessIcon(dto.getImageUrl());
			}
		}
		return vo;
	}
}
