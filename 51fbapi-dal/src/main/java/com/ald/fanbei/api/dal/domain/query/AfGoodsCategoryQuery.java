package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;

import java.math.BigDecimal;

/**
 * 
 *@类描述：AfGoodsQuery
 *@author 何鑫 2017年2月17日  09:50:53
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsCategoryQuery extends Page<AfGoodsCategoryDo>{


	private static final long serialVersionUID = 4484018625119839235L;
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
