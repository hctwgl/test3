package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

import com.ald.fanbei.api.common.util.StatusConvertUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * @类描述：订单状态对应app描述转换
 * @author chengkang 2017年7月10日下午15:31:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfOrderStatusMsgRemark {
	
	NEW("NEW", "待支付","后未支付将自动关闭订单"),
	DEALING("DEALING", "支付中","支付可能会有几分钟延迟，请稍作等待"),
	PAYFAIL("PAYFAIL", "支付失败",""),
	PAID("PAID", "待发货","订单正准备发货"),
	REVIEW("REVIEW", "审核中","分期申请正在审核，请耐心等待"),
	AGENCYCOMPLETED("AGENCYCOMPLETED", "待发货","订单正准备发货"),
	DELIVERED("DELIVERED", "待收货","请确认已签收商品/服务"),
	FINISHED("FINISHED", "订单完成",""),
	REBATED("REBATED", "订单完成",""),
	CLOSED("CLOSED", "订单关闭",""),
	WAITING_REFUND("WAIT_REFUND", "售后处理中",""),
	DEAL_REFUNDING("DEAL_REFUNDING", "退款中","");
	
	
	private String code;
    private String statusMsg;
    private String statusRemark;

    private static Map<String,AfOrderStatusMsgRemark> codeRoleTypeMap = null;

    AfOrderStatusMsgRemark(String code, String statusMsg, String statusRemark) {
        this.code = code;
        this.statusMsg = statusMsg;
        this.statusRemark = statusRemark;
    }

    public static StatusConvertUtil findRoleTypeByCodeAndOrderType(String code,String orderType,String payType,Boolean isExistRebates,String afterSaleStatus,Boolean isExistAftersaleApply,String closeReason,String payFailReason) {
        for (AfOrderStatusMsgRemark roleType : AfOrderStatusMsgRemark.values()) {
            if (roleType.getCode().equals(code)) {
            	StatusConvertUtil statusConvertUtil = new StatusConvertUtil(roleType.getCode(), roleType.getStatusMsg(), roleType.getStatusRemark());
            	//OrderType
            	if(PAID.getCode().equals(roleType.getCode())){
            		if(OrderType.AGENTBUY.getCode().equals(orderType) && PayType.AGENT_PAY.getCode().equals(payType)){
            			statusConvertUtil.setStatusMsg("待审核");
            			statusConvertUtil.setStatusRemark("分期申请将尽快审核，请耐心等待");
            		}else if(OrderType.MOBILE.getCode().equals(orderType) 
            				|| OrderType.TAOBAO.getCode().equals(orderType)
            				|| OrderType.TMALL.getCode().equals(orderType)
            				|| OrderType.BOLUOME.getCode().equals(orderType)){
            			statusConvertUtil.setStatusMsg("待收货");
            			statusConvertUtil.setStatusRemark("请确认已签收商品/服务");
            		}else if(OrderType.TRADE.getCode().equals(orderType)) {
            			statusConvertUtil.setStatusMsg("已支付");
            			statusConvertUtil.setStatusRemark("订单已支付");
					}
            	}
            	if(FINISHED.getCode().equals(roleType.getCode())){
            		if(isExistRebates){
            			statusConvertUtil.setStatusMsg("待返利");
            			statusConvertUtil.setStatusRemark("");
            		}else{
            			statusConvertUtil.setStatusMsg("订单完成");
            			statusConvertUtil.setStatusRemark("");
            		}
            	}
            	if(CLOSED.getCode().equals(roleType.getCode())){
            		if(isExistAftersaleApply){
            			statusConvertUtil.setStatusMsg("订单关闭");
            			statusConvertUtil.setStatusRemark("已退款");
            		}else{
            			statusConvertUtil.setStatusMsg("订单关闭");
            			statusConvertUtil.setStatusRemark(StringUtil.null2Str(closeReason));
            		}
            	}
            	if(WAITING_REFUND.getCode().equals(roleType.getCode())){
            		if(AfAftersaleApplyStatus.WAIT_REFUND.getCode().equals(afterSaleStatus) || AfAftersaleApplyStatus.REFUNDING.getCode().equals(afterSaleStatus)){
            			statusConvertUtil.setStatusMsg("待退款");
            			statusConvertUtil.setStatusRemark("");
            		}
            	}
            	if(PAYFAIL.getCode().equals(roleType.getCode())){
            		statusConvertUtil.setStatusRemark(StringUtil.null2Str(payFailReason));
            	}
                return statusConvertUtil;
            }
        }
        return null;
    }
    
    public static AfOrderStatusMsgRemark findRoleTypeByCode(String code) {
        for (AfOrderStatusMsgRemark roleType : AfOrderStatusMsgRemark.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfOrderStatusMsgRemark> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfOrderStatusMsgRemark>();
        for(AfOrderStatusMsgRemark item:AfOrderStatusMsgRemark.values()){
            codeRoleTypeMap.put(item.getCode(), item);
        }
        return codeRoleTypeMap;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getStatusRemark() {
		return statusRemark;
	}

	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}

	
}
