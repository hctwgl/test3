/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：售后申请状态对应
 * @author chengkang 2017年7月8日下午6:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAftersaleApplyStatusMsgRemark {
	NEW("NEW", "审核中","已提交申请，审核结束前无法修改申请。审核将在提交申请后24小时内完成，麻烦您稍作等待！"),
	CLOSE("CLOSE","已关闭","售后申请已关闭"),
	NOTPASS("NOTPASS","审核未通过",""),
	//审核通过，需回寄商品时，返回给app信息待拼接
	WAIT_GOODS_BACK("WAIT_GOODS_BACK","审核通过",""),
	GOODS_BACKIING("GOODS_BACKIING","货品已回寄","货品回寄签收检查无误后即处理退款"),
	WAIT_REFUND("WAIT_REFUND","货品已回寄","货品回寄签收检查无误后即处理退款"),
	FINISH("FINISH","退款完成","退款完成");
    
    private String code;
    private String statusMsg;
    private String statusRemark;

    private static Map<String,AfAftersaleApplyStatusMsgRemark> codeRoleTypeMap = null;

    AfAftersaleApplyStatusMsgRemark(String code, String statusMsg,String statusRemark) {
        this.code = code;
        this.statusMsg = statusMsg;
        this.statusRemark = statusRemark;
    }

    public static AfAftersaleApplyStatusMsgRemark findRoleTypeByCode(String code) {
        for (AfAftersaleApplyStatusMsgRemark roleType : AfAftersaleApplyStatusMsgRemark.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

    
    public static Map<String,AfAftersaleApplyStatusMsgRemark> getCodeRoleTypeMap(){
        if(codeRoleTypeMap != null && codeRoleTypeMap.size() > 0){
            return codeRoleTypeMap;
        }
        codeRoleTypeMap = new HashMap<String, AfAftersaleApplyStatusMsgRemark>();
        for(AfAftersaleApplyStatusMsgRemark item:AfAftersaleApplyStatusMsgRemark.values()){
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
