package com.ald.fanbei.api.biz.service.supplier;

import java.util.List;

import com.ald.fanbei.api.dal.domain.supplier.AfSearchItemDo;
import com.ald.fanbei.api.dal.domain.supplier.AfSolrSearchResultDo;

/**
 * @author chenxuankai
 * @version 1.0.0 初始化
 * @date 2018年4月2日16:45:10
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSearchItemService {

	AfSolrSearchResultDo getSearchList(String keyword, Integer pageNo, Integer pageSize);
	
}
