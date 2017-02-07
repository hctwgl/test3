package com.ald.fanbei;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemGetRequest;
import com.taobao.api.request.TbkItemRecommendGetRequest;
import com.taobao.api.response.TbkItemGetResponse;
import com.taobao.api.response.TbkItemRecommendGetResponse;

/**
 *@类描述：
 *@author Xiaotianjian 2017年2月4日上午11:10:12
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoTest {
	public static void main(String[] args) throws ApiException {
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
		
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
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
		
		
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		TaeItemsListRequest req = new TaeItemsListRequest();
//		req.setFields("title,nick,price");
//		req.setNumIids("123456789,123456789");
//		req.setOpenIids("AAEYxNL_AClXeBuXBI6npdso");
//		TaeItemsListResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		
//		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
//		AtbItemsGetRequest req = new AtbItemsGetRequest();
//		req.setArea("杭州");
//		req.setAutoSend("true");
//		req.setCid(123L);
//		req.setEndCommissionNum("10000");
//		req.setEndCommissionRate("2345");
//		req.setEndCredit("1heart");
//		req.setEndPrice("999");
//		req.setEndTotalnum("10");
//		req.setFields("open_iid,title,nick,pic_url,price,commission,commission_rate,commission_num,commission_volume,seller_credit_score,item_location,volume");
//		req.setGuarantee("true");
//		req.setRealDescribe("true");
//		req.setKeyword("男装");
//		req.setCashCoupon("true");
//		req.setVipCard("true");
//		req.setPageNo(1L);
//		req.setPageSize(40L);
//		req.setOverseasItem("true");
//		req.setOnemonthRepair("true");
//		req.setSevendaysReturn("true");
//		req.setSort("price_desc");
//		req.setStartCommissionNum("1000");
//		req.setStartCommissionRate("1234");
//		req.setStartCredit("1heart");
//		req.setStartPrice("1");
//		req.setStartTotalnum("1");
//		req.setSupportCod("true");
//		req.setMallItem("true");
//		AtbItemsGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
//		System.out.println(JSON.parse(rsp.getBody()));
		
		
		
		//淘宝客，搜索商品，没有返利
		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23417101", "05b0653d4b7573e38c9ef5d3d16bfd1f");
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ("南极人");
		req.setSort("total_sales_des");
		req.setIsOverseas(false);
		req.setStartPrice(0L);
		req.setEndPrice(100000L);
		req.setStartTkRate(123L);
		req.setEndTkRate(1000L);
		req.setPageSize(100L);
//		req.setIsTmall(false);
//		req.setCat("16,18");
//		req.setItemloc("杭州");
//		req.setPlatform(1L);
//		req.setPageNo(123L);
		TbkItemGetResponse rsp = client.execute(req);
		String str = rsp.getBody();
		System.out.println(rsp.getBody());
		
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
		
	}
}
