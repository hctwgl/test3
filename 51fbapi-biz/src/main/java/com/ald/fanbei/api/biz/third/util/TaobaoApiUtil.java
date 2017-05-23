package com.ald.fanbei.api.biz.third.util;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TaeItemDetailGetRequest;
import com.taobao.api.request.TaeItemsListRequest;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.request.TbkItemInfoGetRequest;
//import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkItemRecommendGetRequest;
import com.taobao.api.response.TaeItemDetailGetResponse;
import com.taobao.api.response.TaeItemsListResponse;
import com.taobao.api.response.TbkItemGetResponse;
import com.taobao.api.response.TbkItemInfoGetResponse;
//import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemRecommendGetResponse;



/**
 *@类描述：
 *@author xiaotianjian 2017年2月8日下午3:30:25
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("taobaoApiUtil")
public class TaobaoApiUtil extends AbstractThird {
	
	private static TaobaoClient client = null;
	private static final Long PAGE_SIZE = 50L;
	
	public static final String NUM_IID = "numIid";
	public static final String OPEN_IID = "openIids";
	
	private static TaobaoClient getTaobaoClient() {
		if (client == null) {
			client = new DefaultTaobaoClient(AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_URL), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
			return client;
		}
		return client;
	}
	private static TaobaoClient getTaobaoLianMengClient() {
		if (client == null) {
			client = new DefaultTaobaoClient(AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_LIANMENG_URL), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_LIANMENG_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY)),
					AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_LIANMENG_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
			return client;
		}
		return client;
	}
	
	/**
	 * 搜索淘宝客商品，没有返利 taobao.tbk.item.get接口
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
		if (params.get(NUM_IID) != null) {
			req.setNumIids(params.get(NUM_IID).toString());
		}
		if (params.get(OPEN_IID) != null) {
			req.setOpenIids(params.get(OPEN_IID).toString());
		}
		logger.info("executeTbkRebateRecords complete.");
		return client.execute(req);
	}
	
	/**
	 * 查询商品详情接口 taobao.tae.item.detail.get
	 * @param params
	 * @return
	 * @throws ApiExceptionx
	 */
	public TaeItemDetailGetResponse executeTaeItemDetailSearch(String openId) throws ApiException{
		logger.info("executeTaeItemDetailSearch start,openId={}",openId);
		TaobaoClient client = getTaobaoClient();
		TaeItemDetailGetRequest req = new TaeItemDetailGetRequest();
		req.setFields(ConfigProperties.get(Constants.CONFKEY_TAOBAO_TAE_ITEM_DETAIL_GET_FIELDS));
		req.setOpenIid(openId);
		return client.execute(req);
	}
	/**
	 * 查询商品详情接口 taobao.tbk.item.recommend.get
	 * @param params
	 * @return
	 * @throws ApiExceptionx
	 */
	public TbkItemRecommendGetResponse executeTaeItemRecommendSearch(String numId) throws ApiException{
		logger.info("executeTaeItemDetailSearch start,numId={}",numId);
		TaobaoClient client = getTaobaoClient();
		TbkItemRecommendGetRequest req = new TbkItemRecommendGetRequest();
		req.setFields(ConfigProperties.get(Constants.CONFKEY_TAOBAO_TAE_ITEM_DETAIL_GET_FIELDS));
		Long numIdL = NumberUtil.objToLongDefault(numId, 0L);
		req.setNumIid(numIdL);

		return client.execute(req);
	}
	
	/**
	 * 查询商品详情接口 taobao.tbk.item.info.get
	 * @param params
	 * @return
	 * @throws ApiExceptionx
	 */
	public TbkItemInfoGetResponse executeTakItemDetailSearch(String numIids) throws ApiException{
		logger.info("executeTaeItemDetailSearch start,openId={}",numIids);
//		TaobaoClient client = getTaobaoClient();
		TaobaoClient client = getTaobaoLianMengClient();

		
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
		req.setFields(ConfigProperties.get(Constants.CONFKEY_TAOBAO_TBK_ITEM_GET_FIELDS));
		req.setPlatform(1L);
		req.setNumIids(numIids);
		TbkItemInfoGetResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
		return client.execute(req);
//		return null;
	}
}
