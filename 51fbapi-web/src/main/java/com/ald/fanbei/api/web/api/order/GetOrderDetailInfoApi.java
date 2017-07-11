package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfOrderStatusMsgRemark;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOrderVo;

/**
 * 
 * @类描述：获取订单详情
 * @author 何鑫 2017年2月17日下午16:13:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderDetailInfoApi")
public class GetOrderDetailInfoApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("orderId"),""), 0l);
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		AfOrderVo orderVo = getOrderVo(orderInfo);
		resp.setResponseData(orderVo);
		return resp;
	}

	private AfOrderVo getOrderVo(AfOrderDo order){
		AfOrderVo vo = new AfOrderVo();
		vo.setGmtCreate(order.getGmtCreate());
		vo.setGmtFinished(order.getGmtFinished());
		vo.setGmtRebated(order.getGmtRebated());
		vo.setGoodsCount(order.getCount());
		vo.setGoodsIcon(order.getGoodsIcon());
		vo.setGoodsName(order.getGoodsName());
		vo.setOrderAmount(order.getSaleAmount());
		vo.setCouponAmount(order.getSaleAmount().subtract(order.getActualAmount()));
		vo.setActualAmount(order.getActualAmount());
		vo.setOrderNo(order.getOrderNo());
		vo.setOrderStatus(order.getStatus());
		vo.setRebateAmount(order.getRebateAmount());
		vo.setType(order.getOrderType());
		vo.setGmtClosed(order.getGmtModified());
		vo.setMobile(order.getMobile());
		
		vo.setGmtPay(DateUtil.formatDateToYYYYMMddHHmmss(order.getGmtPay()));
		vo.setAddress(order.getAddress());
		vo.setConsignee(order.getConsignee());
		vo.setConsigneeMobile(order.getConsigneeMobile());
		vo.setInvoiceHeader(order.getInvoiceHeader());
		vo.setLogisticsInfo(order.getLogisticsInfo());
		vo.setPayType(order.getPayType());
		
		//商品售价处理(订单价格除以商品数量)
		BigDecimal saleCount = NumberUtil.objToBigDecimalZeroToDefault(BigDecimal.valueOf(order.getCount()), BigDecimal.ONE);
		vo.setGoodsSaleAmount(order.getSaleAmount().divide(saleCount, 2));
		//售后相关设置
		Boolean isExistAftersaleApply = false;
		String afterSaleStatus = "";
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(order.getRid());
		if(afAftersaleApplyDo!=null){
			isExistAftersaleApply = true;
			afterSaleStatus = afAftersaleApplyDo.getStatus();
			vo.setAfterSaleStatus(afAftersaleApplyDo.getStatus());
			vo.setGmtRefundApply(afAftersaleApplyDo.getGmtApply());
		}else{
			vo.setGmtRefundApply(new Date(0));
			vo.setAfterSaleStatus("");
		}
		//状态备注及说明 
		AfOrderStatusMsgRemark orderStatusMsgRemark = AfOrderStatusMsgRemark.findRoleTypeByCodeAndOrderType(order.getStatus(), order.getOrderType(), order.getPayType(),
				order.getRebateAmount().compareTo(BigDecimal.ZERO)>0,afterSaleStatus, isExistAftersaleApply);
		if(orderStatusMsgRemark!=null){
			vo.setOrderStatusMsg(orderStatusMsgRemark.getStatusMsg());
			vo.setOrderStatusRemark(orderStatusMsgRemark.getStatusRemark());	
		}else{
			vo.setOrderStatusMsg("");
			vo.setOrderStatusRemark("");
		}
		return vo;
	}
}
