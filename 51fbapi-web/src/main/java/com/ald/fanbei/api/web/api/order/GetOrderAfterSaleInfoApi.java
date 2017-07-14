package com.ald.fanbei.api.web.api.order;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAftersaleApplyStatusMsgRemark;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
		
		AfAftersaleApplyVo afAftersaleApplyVo = getAftersaleApplyVo(orderInfo,afAftersaleApplyDo);
		resp.setResponseData(afAftersaleApplyVo);
		return resp;
	}
	
	//售后信息vo转换
	private AfAftersaleApplyVo getAftersaleApplyVo(AfOrderDo orderInfo,AfAftersaleApplyDo asApplyDo){
		AfAftersaleApplyVo afAftersaleApplyVo = new  AfAftersaleApplyVo();
		//商品信息填充
		List<AfGoodsVo> goodsList = new ArrayList<AfGoodsVo>();
		goodsList.add(new AfGoodsVo(orderInfo.getGoodsName(), orderInfo.getGoodsIcon()));
		afAftersaleApplyVo.setGoodsList(goodsList);
		//售后信息填充
		afAftersaleApplyVo.setPicVouchers(StringUtil.splitToList(asApplyDo.getPicVouchers(), ","));
		afAftersaleApplyVo.setOrderId(asApplyDo.getOrderId());
		afAftersaleApplyVo.setGmtApply(asApplyDo.getGmtApply());
		afAftersaleApplyVo.setStatus(asApplyDo.getStatus());
		afAftersaleApplyVo.setUserReason(asApplyDo.getUserReason());
		afAftersaleApplyVo.setVerifyRemark(asApplyDo.getVerifyRemark());
		afAftersaleApplyVo.setGoodsBackAddress(asApplyDo.getGoodsBackAddress());
		afAftersaleApplyVo.setLogisticsCompany(asApplyDo.getLogisticsCompany());
		afAftersaleApplyVo.setLogisticsNo(asApplyDo.getLogisticsNo());
		AfAftersaleApplyStatusMsgRemark statusMsgRemark = AfAftersaleApplyStatusMsgRemark.findRoleTypeByCode(asApplyDo.getStatus());
		if(statusMsgRemark!=null){
			afAftersaleApplyVo.setStatusMsg(statusMsgRemark.getStatusMsg());
			afAftersaleApplyVo.setStatusRemark(statusMsgRemark.getStatusRemark());
			//对审核不通过及审核通过待回寄商品做特殊处理
			if(StringUtil.isNotBlank(asApplyDo.getGoodsBackAddress()) && AfAftersaleApplyStatusMsgRemark.WAIT_GOODS_BACK.getCode().equals(asApplyDo.getStatus())){
				afAftersaleApplyVo.setStatusRemark("请将货品寄回到："+asApplyDo.getGoodsBackAddress()+"\n并在下方填写寄回的物流单号\n货品回寄签收检查无误后即处理退款");
			}else if(AfAftersaleApplyStatusMsgRemark.NOTPASS.getCode().equals(asApplyDo.getStatus())){
				afAftersaleApplyVo.setStatusRemark(asApplyDo.getVerifyRemark());
			}
		}
		
		return afAftersaleApplyVo;
	}
	
	
	
}
