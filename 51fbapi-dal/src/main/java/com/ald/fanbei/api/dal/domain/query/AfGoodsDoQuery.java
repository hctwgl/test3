package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;

/**  
 * @Title: AfGoodsDoQuery.java
 * @Package com.ald.fanbei.api.dal.domain.query
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月25日 下午2:42:09
 * @version V1.0  
 */
public class AfGoodsDoQuery extends Page<AfGoodsDo>{

	private static final long serialVersionUID = -2000955393324256962L;

	private String keyword;
	private String sortword;
	private String sort;
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSortword() {
		return sortword;
	}

	public void setSortword(String sortword) {
		this.sortword = sortword;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "AfGoodsDoQuery [keyword=" + keyword + ", sortword=" + sortword + ", sort=" + sort + "]";
	}


	
	
}
