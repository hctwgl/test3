package com.ald.fanbei.api.dal.domain.query;

import java.util.List;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import lombok.Getter;
import lombok.Setter;

/**  
 * @Title: AfGoodsDoQuery.java
 * @Package com.ald.fanbei.api.dal.domain.query
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月25日 下午2:42:09
 * @version V1.0  
 */
@Setter
@Getter
public class AfGoodsDoQuery extends Page<AfGoodsDo>{

	private static final long serialVersionUID = -2000955393324256962L;

	private String keyword;
	private String sortword;
	private String sort;
	private Long goodsId;
	private List<Long> goodsIds;
	private Long excludeGoodsId;//列表需要排除的商品id
	private List<Long> categoryIds;
	private List<Long> brandIds;
	
	public List<Long> getGoodsIds() {
		return goodsIds;
	}

	public void setGoodsIds(List<Long> goodsIds) {
		this.goodsIds = goodsIds;
	}

	public String getKeyword() {
		return keyword;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
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

	public Long getExcludeGoodsId() {
		return excludeGoodsId;
	}

	public void setExcludeGoodsId(Long excludeGoodsId) {
		this.excludeGoodsId = excludeGoodsId;
	}

	@Override
	public String toString() {
		return "AfGoodsDoQuery [keyword=" + keyword + ", sortword=" + sortword + ", sort=" + sort + "]";
	}


	
	
}
