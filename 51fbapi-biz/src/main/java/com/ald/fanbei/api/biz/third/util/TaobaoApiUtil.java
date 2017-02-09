package com.ald.fanbei.api.biz.third.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.NTbkItem;
import com.taobao.api.domain.XItem;
import com.taobao.api.request.TaeItemsListRequest;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.response.TaeItemsListResponse;
import com.taobao.api.response.TbkItemGetResponse;

/**
 *@类描述：
 *@author xiaotianjian 2017年2月8日下午3:30:25
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("taobaoApiUtil")
public class TaobaoApiUtil extends AbstractThird {
	
	private static TaobaoClient client = null;
	private static final Long PAGE_SIZE = 50L;
	
	private static final String NUM_IID = "numIid";
	
	private static TaobaoClient getTaobaoClient() {
		if (client == null) {
			
			client = new DefaultTaobaoClient(AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_URL), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
			return client;
		}
		return client;
	}
	
	/**
	 * 搜索淘宝客商品，taobao.tbk.item.get接口
	 * @param params
	 * @throws ApiException 
	 */
	public List<XItem> searchTaoBaokeGoods(Map<String, Object> params) throws ApiException {
		logger.info("searchTaoBaokeGoods begin, param = {}", params);
		List<NTbkItem> results = executeTaobaokeSearch(params).getResults();
		
		List<Long> numIidsList = CollectionConverterUtil.convertToListFromList(results, new Converter<NTbkItem, Long>() {
			@Override
			public Long convert(NTbkItem source) {
				return source.getNumIid();
			}
		});
		String numIids = StringUtil.turnListToStr(numIidsList);
		
		Map<String,Object> rebatesParams = new HashMap<String,Object>();
		
		rebatesParams.put(NUM_IID, numIids);
		
		List<XItem> items = executeTbkItemSearch(rebatesParams).getItems();
		
		logger.info("searchTaoBaokeGoods complete.");
		return items;
		
	}
	
	/**
	 * 搜索淘宝客商品，没有返利
	 * @param params
	 * @return
	 * @throws ApiException
	 */
	public TbkItemGetResponse executeTaobaokeSearch(Map<String, Object> params) throws ApiException {
		logger.info("executeTbkApi begin, param = {}", params);
		TaobaoClient client = getTaobaoClient();
		TbkItemGetRequest req = new TbkItemGetRequest();
		String q = ObjectUtils.toString(params.get("q"), null);
		String sort = ObjectUtils.toString(params.get("sort"), null);
		Long startPrice = NumberUtil.objToLongDefault(params.get("startPrice"), null);
		Long endPrice = NumberUtil.objToLongDefault(params.get("endPrice"), null);
		Boolean isTmall = NumberUtil.objToBooleanDefault(params.get("isTmall"), false);
		Long pageNo = NumberUtil.objToPageLongDefault(params.get("pageNo"), 1L);
		if (q != null) {
			req.setQ(q);
		}
		if (sort != null) {
			req.setSort(sort);
		}
		if (startPrice != null) {
			req.setStartPrice(startPrice);
		}
		if (endPrice != null) {
			req.setEndPrice(endPrice);
		}
		if (endPrice != null) {
			req.setEndPrice(endPrice);
		}
		if (pageNo != null) {
			req.setPageNo(pageNo);
		}
		req.setFields(ConfigProperties.get(Constants.CONFKEY_TAOBAO_TBK_ITEM_GET_FIELDS));
		req.setIsOverseas(false);
		req.setPageSize(PAGE_SIZE);
		req.setIsTmall(isTmall);
		logger.info("executeTaobaokeSearch complete");
		return client.execute(req);
	}
	
	/**
	 * 查询定制淘宝客商品，有返利比率，taobao.tae.item.list接口
	 * @param params
	 * @return
	 * @throws ApiException
	 */
	public TaeItemsListResponse executeTbkItemSearch(Map<String, Object> params) throws ApiException {
		logger.info("executeTbkRebateRecords begin, param = {}", params);
		TaobaoClient client = getTaobaoClient();
		TaeItemsListRequest req = new TaeItemsListRequest();
		req.setFields(ConfigProperties.get(Constants.CONFKEY_TAOBAO_TAE_ITEM_LIST_FIELDS));
		req.setNumIids(params.get(NUM_IID).toString());
		logger.info("executeTbkRebateRecords complete.");
		return client.execute(req);
	}
}
