package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.util.List;

import com.ald.fanbei.api.common.util.StringUtil;

/**
 * @类现描述：钱包平台退回债权请求实体
 * @author chengkang 2017年11月29日 14:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayBackCreditReqBo implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;
	/**
	 * 债权总金额(单位:元,精度统一到小数点后2位)
	 */
	private String orderNos;
	
	public String getOrderNos() {
		return orderNos;
	}
	
	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}
	
	public List<String> parseOrderNoLists(){
		return StringUtil.splitToList(orderNos, ",");
	}
}
