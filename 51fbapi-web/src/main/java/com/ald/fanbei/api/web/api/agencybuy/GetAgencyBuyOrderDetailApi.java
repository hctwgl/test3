package com.ald.fanbei.api.web.api.agencybuy;



import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
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
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),-1);
		
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
		
		// 取出所有所需要的值
		String goodName = afOrderDo.getGoodsName();
		String goodsIcon = afOrderDo.getGoodsIcon();
		Long count = NumberUtil.objToLongDefault(afOrderDo.getCount(), -1);
		Long priceAmount = NumberUtil.objToLongDefault(afOrderDo.getPriceAmount(), -1);
		Long rebateAmount = NumberUtil.objToLongDefault(afOrderDo.getRebateAmount(), -1);
		String consignee = afAgentOrderDo.getConsignee();
		String province = afAgentOrderDo.getProvince();
		String city = afAgentOrderDo.getCity();
		String county = afAgentOrderDo.getCounty();
		String address = afAgentOrderDo.getAddress();
		String mobile = afAgentOrderDo.getMobile();
		String capture = afAgentOrderDo.getCapture();
		String remark = afAgentOrderDo.getRemark();
		String gmtCreate = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT,afOrderDo.getGmtCreate());
		String payType =  afOrderDo.getPayType();
		String gmtPay = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT,afOrderDo.getGmtPay());
		/**
		 * 支付状态【N:(notpay)未支付,D:(dealing)支付中,P:(payed)已经支付,R:(refund)退款】
		 */
		String payStatus = afOrderDo.getPayStatus();
		String payTradeNo = afOrderDo.getPayTradeNo();
		/**
		 * 订单状态【NEW:新建/待付款/等待代买,DEALING:支付中,PAID:已支付/待收货,FINISHED:已收货/订单完成,
		 * REBATED:返利成功,CLOSED:订单关闭(未付款或退款成功,用户主动取消代买订单)，
		 * WAITING_REFUND等待退款 DEAL_REFUNDING:退款中】
		 */
		String status = afOrderDo.getStatus();
		String gmtRebated = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afOrderDo.getGmtRebated());
		String gmtFinished = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afOrderDo.getGmtFinished());
		String agentMessage = afAgentOrderDo.getAgentMessage();
		String gmtAgentBuy = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afAgentOrderDo.getGmtAgentBuy());
		String closedReason = afAgentOrderDo.getClosedReason();
		String gmtClosed = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afAgentOrderDo.getGmtClosed()); // 用户取消时间
		// 根据订单状态和支付状态返回不同的值
		/**
		 * 订单状态【NEW:新建/待付款/等待代买,DEALING:支付中,PAID:已支付/待收货,FINISHED:已收货/订单完成,
		 * REBATED:返利成功,CLOSED:订单关闭(未付款或退款成功,用户主动取消代买订单)，
		 * WAITING_REFUND等待退款 DEAL_REFUNDING:退款中】
		 * 
		 * 支付状态【N:(notpay)未支付,D:(dealing)支付中,P:(payed)已经支付,R:(refund)退款】
		 */
		agentOrderDetailVo.setStatus(StringUtils.isBlank(status)?"":status);
		agentOrderDetailVo.setPayStatus(StringUtils.isBlank(payStatus)?payStatus:"");
		agentOrderDetailVo.setConsignee(StringUtils.isBlank(consignee)?"":consignee);
		agentOrderDetailVo.setMobile(StringUtils.isBlank(mobile)?"":mobile);
		agentOrderDetailVo.setProvince(StringUtils.isBlank(province)?"":province);
		agentOrderDetailVo.setCity(StringUtils.isBlank(city)?"":city);
		agentOrderDetailVo.setCounty(StringUtils.isBlank(county)?"":county);
		agentOrderDetailVo.setAddress(StringUtils.isBlank(address)?"":address);
		agentOrderDetailVo.setGoodName(StringUtils.isBlank(goodName)?"":goodName);
		agentOrderDetailVo.setGoodsIcon(StringUtils.isBlank(goodsIcon)?"":goodsIcon);
		agentOrderDetailVo.setCount(count != -1?count:0);
		agentOrderDetailVo.setCapture(StringUtils.isBlank(capture)?"":capture);
		agentOrderDetailVo.setRemark(StringUtils.isBlank(remark)?"":remark);
		agentOrderDetailVo.setPriceAmount(priceAmount != -1?0:priceAmount);
		agentOrderDetailVo.setRebateAmount(rebateAmount != -1?0:rebateAmount);
		agentOrderDetailVo.setAgentMessage(StringUtils.isBlank(agentMessage)?"":agentMessage);
		agentOrderDetailVo.setGmtCreate(StringUtils.isBlank(gmtCreate)?"":gmtCreate);
		agentOrderDetailVo.setPayType(StringUtils.isBlank(payType)?"":payType);
		agentOrderDetailVo.setGmtPay(StringUtils.isBlank(gmtPay)?"":gmtPay);
		agentOrderDetailVo.setPayTradeNo(StringUtils.isBlank(payTradeNo)?"":payTradeNo);
		agentOrderDetailVo.setGmtAgentBuy(StringUtils.isBlank(gmtAgentBuy)?"":gmtAgentBuy);
		agentOrderDetailVo.setGmtRebated(StringUtils.isBlank(gmtRebated)?"":gmtRebated);
		agentOrderDetailVo.setGmtFinished(StringUtils.isBlank(gmtFinished)?"":gmtFinished);
		agentOrderDetailVo.setClosedReason(StringUtils.isBlank(closedReason)?"":closedReason);
		agentOrderDetailVo.setGmtClosed(StringUtils.isBlank(gmtClosed)?"":gmtClosed);
		

		return agentOrderDetailVo;
	}

}
