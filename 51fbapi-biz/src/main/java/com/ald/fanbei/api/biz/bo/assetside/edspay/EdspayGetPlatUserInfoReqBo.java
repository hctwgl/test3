package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.util.List;

/**
 * @类现描述：钱包平台获取债权对应的用户信息请求实体
 * @author chengkang 2017年12月04日 11:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetPlatUserInfoReqBo implements Serializable {

	private static final long serialVersionUID = 6196722700949810867L;

	/**
	 * 查询对应的订单列表
	 */
	private List<String> orderNos;

	public List<String> getOrderNos() {
		return orderNos;
	}

	public void setOrderNos(List<String> orderNos) {
		this.orderNos = orderNos;
	}
	
}
