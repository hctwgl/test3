package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;

/**
 * 
 * @类描述：AfBorrowLegalOrderQuery
 * @author Jiang Rongbo 2017年2月17日 09:50:53
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowLegalOrderQuery extends Page<AfBorrowLegalOrderDo> {

	private static final long serialVersionUID = -722303985401230132L;

	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	

}
