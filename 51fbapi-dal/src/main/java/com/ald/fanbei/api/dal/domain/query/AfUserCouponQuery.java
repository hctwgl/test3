package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;

/**
 * 
 *@类描述：AfUserCouponQuery
 *@author 何鑫 2017年1月20日  11:38:56
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserCouponQuery extends Page<AfUserCouponDo>{

	private static final long serialVersionUID = 4751520912481980575L;

	private String status;//EXPIRE:过期 ; NOUSE:未使用 ， USED:已使用
	
	private Long   userId;//用户id

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
