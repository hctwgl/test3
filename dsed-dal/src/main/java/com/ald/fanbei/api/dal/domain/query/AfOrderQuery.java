package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

/**
 * 
 *@类描述：AfOrderQuery
 *@author 何鑫 2017年2月17日  16:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderQuery extends Page<AfOrderDo>{

	private static final long serialVersionUID = -722303985401230132L;

	private String orderStatus;//接口返回数据：WC-待完成 WR-待返利 AR-已领取
	
	private Long userId;

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
