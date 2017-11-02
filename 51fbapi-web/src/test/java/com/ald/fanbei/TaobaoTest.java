package com.ald.fanbei;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TaeItemsListRequest;
import com.taobao.api.response.TaeItemsListResponse;





/**
 *@类描述：
 *@author Xiaotianjian 2017年2月4日上午11:10:12
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoTest {
	public static void main(String[] args) throws ApiException {
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23427320", "f96804bd151c12114fa51ddb1bff91b6");
		
//		TaeItemsListRequest req = new TaeItemsListRequest();
//		req.setFields("title,nick,pic_url,location,cid,price,post_fee,promoted_service,ju,shop_name");
//		req.setNumIids("542598579311");
//		System.out.println(client.execute(req).getBody());
		
//		TaobaoClient client = new DefaultTaobaoClient("", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		AtbItemsDetailGetRequest req = new AtbItemsDetailGetRequest();
//		req.setFields("open_iid,title");
//		req.setOpenIids("abc,bcd");
//		AtbItemsDetailGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
//		
//		TaobaoClient client1 = new DefaultTaobaoClient(url, appkey, secret);
//		ItemcatsGetRequest req1 = new ItemcatsGetRequest();
//		req.setFields("cid,parent_cid,name,is_parent");
//		req1.setParentCid(0L);
//		ItemcatsGetResponse response = client.execute(req);
		
//		AtbItemsGetRequest req = new AtbItemsGetRequest();
//		req.setCid(50008075L);
//		req.setFields("open_iid,title,nick,pic_url,price,commission,commission_rate,commission_num,commission_volume,seller_credit_score,item_location,volume");
//		AtbItemsGetResponse response = client.execute(req);
//		System.out.println(response.getBody());
//		TaobaoClient client =  new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		TaeItemDetailGetRequest req = new TaeItemDetailGetRequest();
//		req.setBuyerIp("127.0.0.1");
//		req.setFields("itemInfo,priceInfo,skuInfo,stockInfo,rateInfo,descInfo,sellerInfo,mobileDescInfo,deliveryInfo,storeInfo,itemBuyInfo,couponInfo");
//		req.setOpenIid("AAEYxNL_AClXeBuXBI6npdso");
//		req.setId("AAEYxNL_AClXeBuXBI6npdso");
//		TaeItemDetailGetResponse rsp = client.execute(req, "");
//		System.out.println(rsp.getBody());
		
		
	/*	
		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
		AtbItemsGetRequest req = new AtbItemsGetRequest();
		req.setArea("杭州");
		req.setAutoSend("true");
		req.setCid(123L);
		req.setEndCommissionNum("10000");
		req.setEndCommissionRate("2345");
		req.setEndCredit("1heart");
		req.setEndPrice("999");
		req.setEndTotalnum("10");
		req.setFields("open_iid,title,nick,pic_url,price,commission,commission_rate,commission_num,commission_volume,seller_credit_score,item_location,volume");
		req.setGuarantee("true");
		req.setRealDescribe("true");
		req.setKeyword("男装");
		req.setCashCoupon("true");
		req.setVipCard("true");
		req.setPageNo(1L);
		req.setPageSize(40L);
		req.setOverseasItem("true");
		req.setOnemonthRepair("true");
		req.setSevendaysReturn("true");
		req.setSort("price_desc");
		req.setStartCommissionNum("1000");
		req.setStartCommissionRate("1234");
		req.setStartCredit("1heart");
		req.setStartPrice("1");
		req.setStartTotalnum("1");
		req.setSupportCod("true");
		req.setMallItem("true");
		AtbItemsGetResponse rsp = client.execute(req);
		System.out.println(rsp.getBody());
		System.out.println(JSON.parse(rsp.getBody()));
		*/
		
		
//		//淘宝客，搜索商品，没有返利
//		TbkItemGetRequest req = new TbkItemGetRequest();
//		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
//		req.setQ("2017新款春季男士运动休闲鞋韩版潮流板鞋男鞋子阿甘帆");
//		req.setSort("total_sales_des");
//		req.setIsOverseas(false);
//		req.setStartPrice(0L);
//		req.setEndPrice(100000L);
//		req.setStartTkRate(123L);
//		req.setEndTkRate(1000L);
//		req.setPageSize(100L);
//		TbkItemGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
//		System.out.println(rsp.getResults());
//		System.out.println(rsp.getTotalResults());
//		System.out.println(rsp.getErrorCode());
//		System.out.println(rsp.getParams());
		
		//获取淘宝客返利
		TaobaoClient client1 = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
		TaeItemsListRequest req1 = new TaeItemsListRequest();
		req1.setFields("title,nick,price");
		req1.setNumIids("25354992789");
		TaeItemsListResponse rsp1 = client1.execute(req1);
		System.out.println(rsp1.getBody());
		System.out.println(rsp1.getItems());
		System.out.println(rsp1.getParams());
		
		
//		req.setIsTmall(false);
//		req.setCat("16,18");
//		req.setItemloc("杭州");
//		req.setPlatform(1L);
//		req.setPageNo(123L);
//		String str = rsp.getBody();
//		25354992789,521524876965,537010515173
//		537034608173  扬州专卖 
//		System.out.println(((Map<String,Object>)((Map<String,Object>)JSON.parse(str)).get("tbk_item_get_response")).get("results"));
		
		//淘宝客 推荐商品
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		TbkItemRecommendGetRequest req = new TbkItemRecommendGetRequest();
//		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url");
//		req.setNumIid(537034608173L);
//		req.setCount(20L);
//		req.setPlatform(1L);
//		TbkItemRecommendGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
//		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url");
//		req.setPlatform(1L);
//		req.setNumIids("537034608173");
//		TbkItemInfoGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		ShopGetRequest req = new ShopGetRequest();
//		req.setFields("sid,cid,title,nick,desc,bulletin,pic_path,created,modified");
//		req.setNick("南极人扬州专卖店");
//		ShopGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
//		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//		ItemcatsGetRequest req = new ItemcatsGetRequest();
//		req.setCids("18957,19562");
//		req.setDatetime(StringUtils.parseDateTime("2000-01-01 00:00:00"));
//		req.setFields("cid,parent_cid,name,is_parent");
//		req.setParentCid(50011999L);
//		ItemcatsGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
//		查询当天剩余订阅次数
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemSubscribeDailyLeftQueryRequest req = new BaichuanItemSubscribeDailyLeftQueryRequest();
//		BaichuanItemSubscribeDailyLeftQueryResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		//订阅单个商品
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemSubscribeRequest req = new BaichuanItemSubscribeRequest();
//		req.setItemId(22116135732L);
//		BaichuanItemSubscribeResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		//删除单个商品订阅
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemUnsubscribeRequest req = new BaichuanItemUnsubscribeRequest();
//		req.setItemId(22116135732L);
//		BaichuanItemUnsubscribeResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		//查询单个商品订阅情况
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemSubscribeRelationQueryRequest req = new BaichuanItemSubscribeRelationQueryRequest();
//		req.setItemId(537034608173L);
//		BaichuanItemSubscribeRelationQueryResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		//批量订阅商品,最多100个
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemsSubscribeRequest req = new BaichuanItemsSubscribeRequest();
//		req.setItemIds("22116135732,538337335488,537642216604");
//		BaichuanItemsSubscribeResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
		//批量删除商品订阅， 最多100个
//		TaobaoClient client1 = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemsUnsubscribeRequest req1 = new BaichuanItemsUnsubscribeRequest();
//		req1.setItemIds("22116135732,538337335488,537642216604");
//		BaichuanItemsUnsubscribeResponse rsp1 = client1.execute(req1);
//		System.out.println(rsp1.getBody());
		
		
		//按条件查询订阅关系，最多100
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemSubscribeRelationsQueryRequest req = new BaichuanItemSubscribeRelationsQueryRequest();
//		Condition obj1 = new Condition();
//		obj1.setLimit(100L);
//		req.setCondition(obj1);
//		BaichuanItemSubscribeRelationsQueryResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
//		obj1.setStartTime(StringUtils.parseDateTime("2015-11-23 12:00:32"));
//		obj1.setStartId(0L);
//		obj1.setItemStatus(0L);
//		obj1.setEndTime(StringUtils.parseDateTime("2015-11-26 12:00:32"));
		
		//根据条件删除，最多100
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		BaichuanItemsUnsubscribeByConditionRequest req = new BaichuanItemsUnsubscribeByConditionRequest();
//		Condition obj1 = new Condition();
//		obj1.setLimit(100L);
//		req.setCondition(obj1);
//		BaichuanItemsUnsubscribeByConditionResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
//		obj1.setStartTime(StringUtils.parseDateTime("2015-11-23 12:00:32"));
//		obj1.setStartId(23L);
//		obj1.setItemStatus(0L);
//		obj1.setEndTime(StringUtils.parseDateTime("2015-11-26 12:00:32"));
		
		//添加淘宝分组
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		TmcGroupAddRequest req = new TmcGroupAddRequest();
//		req.setGroupName("dev_test");
//		req.setNicks("51返呗");
//		TmcGroupAddResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
	}
}
