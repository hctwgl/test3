package com.ald.fanbei.api.biz.bo.taobao;

import java.util.List;
import com.taobao.api.internal.mapping.ApiField;
import com.ald.fanbei.api.biz.bo.taobao.NTbkShop;
import com.taobao.api.internal.mapping.ApiListField;

import com.taobao.api.TaobaoResponse;

/**
 * TOP API: taobao.tbk.shop.get response.
 * 
 * @author top auto create
 * @since 1.0, null
 * 
 */
public class TbkShopGetResponse extends TaobaoResponse {

	private static final long serialVersionUID = 6535377455283383212L;

	/** 
	 * 淘宝客店铺
	 */
	@ApiListField("results")
	@ApiField("n_tbk_shop")
	private List<NTbkShop> results;

	/** 
	 * 搜索到符合条件的结果总数
	 */
	@ApiField("total_results")
	private Long totalResults;


	public void setResults(List<NTbkShop> results) {
		this.results = results;
	}
	public List<NTbkShop> getResults( ) {
		return this.results;
	}

	public void setTotalResults(Long totalResults) {
		this.totalResults = totalResults;
	}
	public Long getTotalResults( ) {
		return this.totalResults;
	}
	


}
