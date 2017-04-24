package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfContactsOldService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.CouponSceneRuleEnginer;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.domain.AfContactsOldDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class TestController {
	Logger logger = LoggerFactory.getLogger(TestController.class);

	@Resource
	SmsUtil smsUtil;

	@Resource
	AfOrderService afOrderService;
	@Resource
	CouponSceneRuleEnginer authRealnameRuleEngine;
	@Resource
	CouponSceneRuleEnginer signinRuleEngine;
	@Resource
	JpushService jpushService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfAuthContactsService afAuthContactsService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfContactsOldService afContactsOldService;
	@Resource
	RiskUtil riskUtil;

	/**
	 * 新h5页面处理，针对前端开发新的h5页面时请求的处理
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "/h5/app/*_new", "/h5/app/sys/*_new", "/h5/app/goods/*_new", "/h5/app/mine/*_new", "/h5/app/order/*_new" }, method = RequestMethod.GET)
	public String newVmPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String returnUrl = request.getRequestURI().replace("/h5/", "");
		return returnUrl;
	}

//	@RequestMapping(value = { "/test1" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String goodsRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		afOrderService.dealMobileChargeOrder("MB17040100045", "222000");
//		riskUtil.modify("73772", "胡潮永", "13958004662", "330624198509136450", "", "", "星耀城", "");
//		Map<String, Object> inputData = new HashMap<String, Object>();
//		inputData.put("userId", 11l);
//		inputData.put("seriesCount", 5);
//		signinRuleEngine.executeRule(inputData);
//
//		jpushService.chargeMobileSucc("13607665702", "13607665702", new Date());
//
//		String reportId = TongdunUtil.applyPreloan("362525198601022112", "陈金虎", "15958119936", "410228573@qq.com");
//		// ER2017012122013411346564
//		TongdunResultBo result = TongdunUtil.queryPreloan("ER2017012121595110613362");
//
//		System.out.println("-----reportId---" + 11 + ",result=" + result);
//		smsUtil.sendRegistVerifyCode("15958119936");
//
//		SmsUtil.sendSms("15958119936", "验证码:1234");
//		afOrderService.createOrderTrade("{'buyer_id':'AAGtxNL8AClXeBuXBPILbV-s','paid_fee':'138.00','shop_title':'佐祥车品旗舰店','is_eticket':false,'create_order_time':'2017-02-17 14:36:28','order_id':'3065189213875206','order_status':'7','seller_nick':'佐祥车品旗舰店','auction_infos':[{'detail_order_id':'3065189213875206','auction_id':'AAEnxNL_AClXeBuXBIxwBj6s','real_pay':'138.00','auction_pict_url':'i1/2208256900/TB2uxTDXNXkpuFjy0FiXXbUfFXa_!!2208256900.jpg','auction_title':'汽车载氧吧空气净化雾霾器负离子杀菌香薰除甲醛异味全自动过滤','auction_amount':'1'}]}");
//		return "succ";
//	}

//	@RequestMapping(value = { "/test2" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String batchRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		riskUtil.batchRegister(5, "13958004662");
//		return "succ";
//	}

//	@RequestMapping(value = { "/testRefund" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	public String testRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		JSONObject json = JSONObject.parseObject(body);
//		String orderNo = json.getString("orderNo");
//		String payTradeNo = json.getString("payTradeNo");
//		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
//		BigDecimal totalAmount = NumberUtil.objToBigDecimalDefault(json.getString("totalAmount"), null);
//		logger.info("testRefund begin testRefund is orderNo = {}, payTradeNo = {}, refundAmount = {}, refundAmount = {}", new Object[] { orderNo, payTradeNo, refundAmount, totalAmount });
//		if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(payTradeNo) || refundAmount == null || totalAmount == null) {
//			return "";
//		}
//		String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
//		logger.info("testRefund refundResult = {}", refundResult);
//		return "succ";
//	}

//	@RequestMapping(value = { "/test3" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String addressListRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		int count = afUserAccountService.getUserAccountCountWithHasRealName();
//		int pageCount = (int) Math.ceil(count / 10) + 1;
//		for (int j = 1; j <= pageCount; j++) {
//			AfUserAccountQuery query = new AfUserAccountQuery();
//			query.setPageNo(j);
//			query.setPageSize(10);
//			List<AfUserAccountDto> list = afUserAccountService.getUserAndAccountListWithHasRealName(query);
//			for (int i = 0; i < list.size(); i++) {
//				List<AfAuthContactsDo> contacts = afAuthContactsService.getContactsByUserId(list.get(i).getUserId());
//				riskUtil.addressListPrimaries(list.get(i).getUserId().toString(), contacts);
//			}
//		}
//		return "succ";
//	}
//	/**
//	 * 同步通讯录
//	 * @return
//	 */
//	@RequestMapping(value = { "/SyncAddressList/toRiskManagement" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String SyncAddressListRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		int count = afUserAuthService.getUserAuthCountWithIvs_statusIsY();
//		int pageCount = (int) Math.ceil(count / 10) + 1;
//		for (int j = 1; j <= pageCount; j++) {
//			AfUserAuthQuery query = new AfUserAuthQuery();
//			query.setPageNo(j);
//			query.setPageSize(10);
//			List<AfUserAuthDo> list = afUserAuthService.getUserAuthListWithIvs_statusIsY(query);
//			for (int i = 0; i < list.size(); i++) {
//				AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
//				if (null != afContactsOldDo) {
//					String moblieBook = afContactsOldDo.getMobileBook();
//					String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"")+1,moblieBook.lastIndexOf("\""));
//					 
//					JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
//					List<AfAuthContactsDo> contacts = new ArrayList<AfAuthContactsDo>();
//					for (Object object : moblieBookJsons) {
//						JSONObject json = JSONObject.parseObject(object.toString());
//						AfAuthContactsDo afAuthContactsDo = new AfAuthContactsDo();
//						afAuthContactsDo.setFriendNick(json.getString("name"));
//						afAuthContactsDo.setFriendPhone(json.getString("phone_number"));
//						contacts.add(afAuthContactsDo);
//					}
//					riskUtil.addressListPrimaries(list.get(i).getUserId().toString(), contacts);
//				}
//			}
//		}
//		return "succ";
//	}
//	/**
//	 * 同步一条通讯录
//	 * @return
//	 */
//	@RequestMapping(value = { "/SyncOneAddress" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String SyncAddressRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//			
//		Long userId = (long) 68424;
//		AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(userId);
//		String moblieBook = afContactsOldDo.getMobileBook();
//		String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"")+1,moblieBook.lastIndexOf("\""));
//		 
//		JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
//		List<AfAuthContactsDo> contacts = new ArrayList<AfAuthContactsDo>();
//		for (Object object : moblieBookJsons) {
//			JSONObject json = JSONObject.parseObject(object.toString());
//			AfAuthContactsDo afAuthContactsDo = new AfAuthContactsDo();
//			afAuthContactsDo.setFriendNick(json.getString("name"));
//			afAuthContactsDo.setFriendPhone(json.getString("phone_number"));
//			contacts.add(afAuthContactsDo);
//		}
//		RiskAddressListRespBo riskAddressListRespBo = riskUtil.addressListPrimaries(userId.toString(), contacts);
//		return JSONObject.toJSONString(riskAddressListRespBo);
//	}
	/**
	 * 用户通讯录集合同步
	 * @author fumeiai
	 * @return
	 */
	@RequestMapping(value = { "/SyncUserAddressList/toRiskManagement" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String SyncUserAddressList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("------toRiskManagement-----");
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		int count = afUserAuthService.getUserAuthCountWithIvs_statusIsY();
		logger.info("------toRiskManagement--count---" + count);
		int pageCount = (int) Math.ceil(count / 10) + 1;
		for (int j = 1; j <= pageCount; j++) {
			AfUserAuthQuery query = new AfUserAuthQuery();
			query.setPageNo(j);
			query.setPageSize(10);
			List<AfUserAuthDo> list = afUserAuthService.getUserAuthListWithIvs_statusIsY(query);
			logger.info("j=" + j + ",size=" + list.size());
			for (int i = 0; i < list.size(); i++) {
				AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
				if (null != afContactsOldDo) {
					String moblieBook = afContactsOldDo.getMobileBook();
					String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"")+1,moblieBook.lastIndexOf("\""));
					 
					JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
					StringBuffer data = new StringBuffer();
					for (Object object : moblieBookJsons) {
						JSONObject json = JSONObject.parseObject(object.toString());
						data.append(json.getString("name")+":");
						data.append(json.getString("phone_number")+",");
					}
					logger.info("i=" + i + "," + data.toString());
					riskUtil.addressListPrimaries(afContactsOldDo.getUid().toString(), data.toString().substring(0,data.toString().length()-1));
				}
			}
		}
		return "succ";
	}
	
	// TongdunUtil
}
