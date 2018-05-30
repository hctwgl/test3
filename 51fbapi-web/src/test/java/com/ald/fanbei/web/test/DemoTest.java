package com.ald.fanbei.web.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

import javax.servlet.http.HttpServletRequest;

public class DemoTest extends BaseTest{

	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://localhost:8080";
	//String userName = "17710378476";
	String userName = "15258801185";

	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
//	@Test
	public void demoApiNoLogin(){
		String url = urlBase + "/resource/getRedRainRounds";
		Map<String,String> params = new HashMap<>();
		params.put("pushId", "82");
		params.put("www", "qqq");
		testApi(url, params, userName, false);
	}

	//根据分类ID获取优惠券列表
	//@Test
	public void  activityCouponInfo() {
		String url = urlBase + "/fanbei-web/activityCouponInfo";
		Map<String,String> params = new HashMap<>();
		params.put("groupId", "995");
		testH5(url, params, userName, true);
	}

	//优惠券立即领取
	//@Test
	public void  pickActivityCoupon() {
		String url = urlBase + "/fanbei-web/pickActivityCoupon";
		Map<String,String> params = new HashMap<>();
		params.put("couponId", "1688");
		testH5(url, params, userName, true);
	}

	//点击分享的时候 插入数据
	//@Test
	public void  shareActivity() {
		String url = urlBase + "/fanbei-web/shareActivity";
		Map<String,String> params = new HashMap<>();
		params.put("shareWith", "sharewithWEIXIN");
		testH5(url, params, userName, true);
	}

	//新增优惠券
	//@Test
	public void  addShared() {
		String url = urlBase + "/fanbei-web/addShared";
		Map<String,String> params = new HashMap<>();
		params.put("type", "2");
		//testApi(url, params, userName, false);
		testH5(url, params, userName, true);
	}

	//分享成功，随机赠送优惠券
	//@Test
	public void  sendCouponAfterSuccessShare() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/sendCouponAfterSuccessShare";
		Map<String,String> params = new HashMap<>();
		params.put("groupId", "994");
		testH5(url, params, userName, true);
	}

	//当前秒杀活动商品列表和下一场秒杀活动ID
	//@Test
	public void  getCurrentSecKillGoods() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/getCurrentSecKillGoods";
		Map<String,String> params = new HashMap<>();
		params.put("groupId", "107");
		testH5(url, params, userName, true);
	}

	//我的活动会场
	//@Test
	public void  mineActivityInfo() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/mineActivityInfo";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "13656640521");
		testH5(url, params, userName, true);
	}

	//活动预售商品列表
	//@Test
	public void  getReservationGoodsList() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/getReservationGoodsList";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "13685746702");
		testH5(url, params, userName, false);
	}

	//测试发送短信
	//@Test
	public void  ceshiSendSms() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/ceshiSendSms";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "13656640521");
		testH5(url, params, userName, true);
	}

	//根据活动ID获取活动商品列表
	//@Test
	public void  couponCategoryInfo() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/getActivityGoodList";
		Map<String,String> params = new HashMap<>();
			params.put("activityId", "626");
		testH5(url, params, userName, true);
	}

	//根据活动ID获取活动商品列表
	//@Test
	public void  partActivityInfoV2() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/partActivityInfoV2";
		Map<String,String> params = new HashMap<>();
		params.put("modelId", "257");
		testH5(url, params, userName, true);
	}
	//根据活动ID获取活动商品列表
	@Test
	public void  getPushingGoods() {
		String url = urlBase + "/visualH5/getPushingGoods";
		Map<String,String> params = new HashMap<>();
		params.put("modelId", "257");
		params.put("pageIndex", "1");
		params.put("pageSize", "100");
		testH5(url, params, userName, true);
	}

	//
	//@Test
	public void  ceshiPayGoodsInfo() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/ceshiPayGoodsInfo";
		Map<String,String> params = new HashMap<>();
		params.put("modelId", "257");
		testH5(url, params, userName, true);
	}
	//测试支付回调
	//@Test
	public void  ceshiPay() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/ceshiPay";
		Map<String,String> params = new HashMap<>();
		params.put("modelId", "257");
		testH5(url, params, userName, true);
	}

	//
	//@Test
	public void  getMineCouponInfo() {
		String url = urlBase + "/fanbei-web/getMineCouponInfo";
		Map<String,String> params = new HashMap<>();
		params.put("pageNo", "1");
		params.put("status", "NOUSE");
		testH5(url, params, userName, true);
	}
	//@Test
	public void  getMineCouponList() {
		String url = urlBase + "/fanbei-web/getMineCouponList";
		Map<String,String> params = new HashMap<>();
		params.put("pageNo", "1");
		params.put("status", "NOUSE");
		testH5(url, params, userName, true);
	}

	//根据活动ID获取活动商品列表
	/*@Test
	public void  partActivityInfoV2() {
		String url = urlBase + "/fanbei-web/thirdAnnivCelebration/partActivityInfoV2";
		Map<String,String> params = new HashMap<>();
		params.put("modelId", "248");
		testH5(url, params, userName, true);
	}*/
	
	/**
	 * 测试完成业务流
	 * 示例业务(更换手机号)：1发起短信 -> 2验证短信 -> 3更换手机号
	 */
//	@Test
	public void demoFlow() throws InterruptedException {
//		testGetVerifyCodeApi(); 1
		TimeUnit.SECONDS.sleep(2);
		
//		testChangeMobileVerifyApi(); 2
		TimeUnit.SECONDS.sleep(2);
		
//		testChangeMobileSyncConactsApi(); 3
	}
	
}
