package com.ald.fanbei.api.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.FacePlusCardRespBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfContactsOldService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.CouponSceneRuleEnginer;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.yitu.FacePlusUtil;
import com.ald.fanbei.api.biz.third.util.yitu.FileHelper;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfContactsOldDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
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
	@Resource
	UpsUtil upsUtil;
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfUserBankcardDao afUserBankcardDao;
	@Resource
	AfOrderRefundDao afOrderRefundDao;
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	@Resource
	AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	FacePlusUtil facePlusUtil;
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

	/**
	 * 新h5页面处理，针对前端开发新的h5页面时请求的处理
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "testPush" }, method = RequestMethod.GET)
	public String testPush(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userName = request.getParameter("userName");
//		jpushService.refundMobileError(userName, new Date());
//		jpushService.repayRenewalSuccess(userName);
//		jpushService.repayRenewalFail(userName);
//		jpushService.chargeMobileError(userName, userName, new Date());
//		jpushService.gameShareSuccess(userName);
//		jpushService.chargeMobileSucc(userName, userName, new Date());
		jpushService.strongRiskSuccess(userName);
		jpushService.strongRiskFail(userName);
		jpushService.mobileRiskFail(userName);
		jpushService.mobileRiskSuccess(userName);
		return "";
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
		int pageCount = (int) Math.ceil(count / 10) + 1;
		logger.info("------toRiskManagement--count---" + count + ",pageCount=" + pageCount);
		for (int j = 1; j <= pageCount; j++) {
			AfUserAuthQuery query = new AfUserAuthQuery();
			query.setPageNo(j);
			query.setPageSize(120);
			List<AfUserAuthDo> list = afUserAuthService.getUserAuthListWithIvs_statusIsY(query);
			logger.info("j=" + j + ",size=" + list.size());
			for (int i = 0; i < list.size(); i++) {
				try{
					AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
					logger.info("i=" + i + "," + afContactsOldDo !=null?afContactsOldDo.toString():"");
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
				}catch(Exception e){
					e.printStackTrace();
					logger.info("init error="+list.get(i).getUserId());
				}
			}
		}
		return "succ";
	}
	
//	@RequestMapping(value = { "/wxRefund" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	public String wxRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		JSONObject json = JSONObject.parseObject(body);
//		String orderNo = json.getString("orderNo");
//		String payTradeNo = json.getString("payTradeNo");
//		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
//		BigDecimal totalAmount = NumberUtil.objToBigDecimalDefault(json.getString("totalAmount"), null);
//		logger.info("wxRefund begin wxRefund is orderNo = {}, payTradeNo = {}, refundAmount = {}, refundAmount = {}", new Object[] { orderNo, payTradeNo, refundAmount, totalAmount });
//		if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(payTradeNo) || refundAmount == null || totalAmount == null) {
//			return "";
//		}
//		String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
//		logger.info("wxRefund refundResult = {}", refundResult);
//		System.out.println(refundResult);
//		return "succ";
//	}
	

	/**
	 * 银行卡退款
	 * @author fumeiai
	 * @return
	 */
	//处理菠萝觅或者代买没有生成账单的订单，重新生成账单。
//	@RequestMapping(value = { "/dealWithBoluomeBorrow" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String dealWithBoluomeBorrow(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		List<AfOrderDo> orderList = afOrderDao.getNoBorrowOrder();
//		if (CollectionUtils.isNotEmpty(orderList)) {
//			for (AfOrderDo orderInfo : orderList) {
//				AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
//				afBorrowService.dealAgentPayConsumeRisk(userAccountInfo, orderInfo.getActualAmount(),
//						orderInfo.getGoodsName(), orderInfo.getNper(), orderInfo.getRid(),
//						orderInfo.getOrderNo(), null);
//			}
//		}
//		return "succ";
//	}
//	
//
	/**
	 * 银行卡退款
	 * @author fumeiai
	 * @return
	 */
//	@RequestMapping(value = { "/bankRefund" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String bankRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		JSONObject json = JSONObject.parseObject(body);
////		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
//		String payTradeNo = json.getString("payTradeNo");
//		AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNoWithStatusY(payTradeNo);
//		String message = "succ!";
//		if (null!=afRepaymentBorrowCashDo) {
//			BigDecimal refundAmount = afRepaymentBorrowCashDo.getActualAmount();
////			BigDecimal refundAmount = new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP);
//			AfUserBankcardDo card = afUserBankcardDao.getUserBankcardByCardNo(afRepaymentBorrowCashDo.getCardNumber());
//			AfUserDo userDo = afUserDao.getUserById(card.getUserId());
//			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(refundAmount, userDo.getRealName(), card.getCardNumber(), card.getUserId()+"", 
//					card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02",OrderType.MOBILE.getCode(),"");
//			String refundNo = generatorClusterNo.getRefundNo(new Date());
//			if(!upsResult.isSuccess()){
//				AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo,refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "", 
//						OrderRefundStatus.FAIL, PayType.BANK,card.getCardNumber(),card.getBankName(),"用户现金借中重复还款后的退款",RefundSource.PLANT_FORM.getCode(),upsResult.getOrderNo());
//				afOrderRefundDao.addOrderRefund(afOrderRefundDo);
//				message = "Fail!";
//			} else {
//				AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo,refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "", 
//						OrderRefundStatus.FINISH, PayType.BANK,card.getCardNumber(),card.getBankName(),"用户现金借中重复还款后的退款",RefundSource.PLANT_FORM.getCode(),upsResult.getOrderNo());
//				AfRepaymentBorrowCashDo repaymentBorrowCashDo = new AfRepaymentBorrowCashDo();
//				repaymentBorrowCashDo.setRid(afRepaymentBorrowCashDo.getRid());
//				repaymentBorrowCashDo.setStatus("R");
//				afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repaymentBorrowCashDo);
//				afOrderRefundDao.addOrderRefund(afOrderRefundDo);
//			}
//		} else {
//			message = "There is no trade can refund!";
//		}
//		
//		return message;
//	}
//	
	// TongdunUtil
	
	@RequestMapping(value = { "/wxRefundMobile" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String wxRefundMobile(@RequestBody String body, HttpServletRequest request, HttpServletResponse response){
		String message = "succ!";
		try {
			JSONObject json = JSONObject.parseObject(body);
			String orderId = json.getString("orderId");
			String scret = json.getString("scret");
			if(!"zsdERfds2123".equals(scret)){
				throw new RuntimeException("秘钥不对");
			}
			AfOrderDo order = afOrderDao.getOrderById(Long.valueOf(orderId));
			String refundNo = generatorClusterNo.getRefundNo(new Date());
			String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
			if(!"SUCCESS".equals(refundResult)){
				afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL,PayType.WECHAT,"",null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),order.getPayTradeNo()));
				throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
			}else{
            	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH,PayType.WECHAT,"",null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),order.getPayTradeNo()));
			}
		} catch (Exception e) {
			logger.info("wxRefund error:",e);
			message = "There is no trade can refund!";
		}
		return message;
	}

	/**
	 * app中微信支付回调接口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/allowcateBrandCoupon" }, method = RequestMethod.POST)
	@ResponseBody
	public String allowcateBrandCoupon(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			String brandUrl = request.getParameter("brandUrl");
			
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				pickBrandCoupon(line, brandUrl);
			}
		} catch (Exception e) {
			logger.error("allowcateBrandCoupon", e);
			return "fail";
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return "success";
	}
	
	@RequestMapping(value = { "/testJPush" }, method = RequestMethod.POST)
	@ResponseBody
	public String testJPush(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			String userName = request.getParameter("userName");
			jpushService.pushHeaderImage(userName);;
		} catch (Exception e) {
			logger.error("allowcateBrandCoupon", e);
			return "fail";
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return "success";
	}
	
	@RequestMapping(value = { "/testAllJPush" }, method = RequestMethod.POST)
	@ResponseBody
	public String testAllJPush(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			jpushService.pushAllHeaderImage();
		} catch (Exception e) {
			logger.error("allowcateBrandCoupon", e);
			return "fail";
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return "success";
	}
	
	private void pickBrandCoupon(String userName, String brandUrl) {
		AfUserDo userInfo = afUserDao.getUserByUserName(userName);
		if (userInfo == null) {
			logger.info(userName + "userName dosn't exist");
			return;
		}
		PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
		bo.setUser_id(userInfo.getRid()+StringUtil.EMPTY);
		String resultString = HttpUtil.doHttpPostJsonParam(brandUrl, JSONObject.toJSONString(bo));
		logger.info("userName = " + userName + " brandUrl = " + brandUrl);
		logger.info("allowcateBrandCoupon pickBrandCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
	}
	
	@RequestMapping(value = { "/testRiskQueryOverdueOrder" }, method = RequestMethod.POST)
	@ResponseBody
	public String testRiskQueryOverdueOrder(HttpServletRequest request, HttpServletResponse response) {
		RiskQueryOverdueOrderRespBo resp = riskUtil.queryOverdueOrder("68885");
		System.out.println(resp);
		return "success";
	}
	
	@RequestMapping(value = { "/test11" }, method = RequestMethod.POST)
	@ResponseBody
	public String bo(HttpServletRequest request, HttpServletResponse response) {
		
		String identity = System.currentTimeMillis() + StringUtil.EMPTY;
		String orderNo = riskUtil.getOrderNo("over", identity.substring(identity.length() - 4, identity.length()));
		List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
		RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
		bo.setBorrowNo("jk2017071020281800843");
		bo.setOverdueDays(0);
		bo.setOverdueTimes(1);
		boList.add(bo);
		logger.info("dealWithSynchronizeOverduedOrder begin orderNo = {} , boList = {}", orderNo, boList);
		riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
		logger.info("dealWithSynchronizeOverduedOrder completed");
		return "success";
	}
	
	@RequestMapping(value = { "/testCard" }, method = RequestMethod.POST)
	@ResponseBody
	public String testCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String api_key = "vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5";
		String api_secret = "Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm";
		String image = FileHelper.getImageByteArrayString("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/294bd573d8e95674.jpg");
		JSONObject json = new JSONObject();
		json.put("api_key", api_key);
		json.put("api_secret", api_secret);
		json.put("image", image);
		String str = HttpUtil.doHttpPost("https://api.faceid.com/faceid/v1/ocridcard", json.toJSONString());
		System.out.println(str);
		return "success";
	}
	
	@RequestMapping(value = { "/ttt" }, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String ttt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		 request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
		 BufferedReader in = null;
	     OutputStream out = null;
	     String result = "";
	     try {
	         URL realUrl = new URL("https://api.faceid.com/faceid/v1/ocridcard");
	         SslUtils.ignoreSsl();
	         HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
	         conn.setRequestMethod("POST");
	         conn.setDoOutput(true);
	         conn.setDoInput(true);
	         conn.setUseCaches(false);  
	         
     		 String[] props = new String[]{"api_key","api_secret"}; // 字段名
    		 String[] values = new String[]{"vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5","Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm"};// 字段值
    		 byte[] file = FileHelper.getImageByteArray("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/294bd573d8e95674.jpg");
    		 String BOUNDARY = "---------------------------7d4a6d158c9"; // 分隔符
    		 StringBuffer sb = new StringBuffer();
    		 // 发送每个字段:
    		 for(int i=0; i < props.length ; i ++ ) {
	    		 sb = sb.append("--");
	    		 sb = sb.append(BOUNDARY);
	    		 sb = sb.append("\r\n");
	    		 sb = sb.append("Content-Disposition: form-data; name=\""+ props[i] + "\"\r\n\r\n");
	    		 sb = sb.append(URLEncoder.encode(values[i]));
	    		 sb = sb.append("\r\n");
    		 }
    		 // 发送文件:
    		 sb = sb.append("--");
    		 sb = sb.append(BOUNDARY);
    		 sb = sb.append("\r\n");
    		 sb = sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"1.txt\"\r\n");
    		 sb = sb.append("Content-Type: application/octet-stream\r\n\r\n");
    		 byte[] data = sb.toString().getBytes();
    		 byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
    		 
    		 conn.setRequestProperty("Accept-Charset", "UTF-8");
    		 conn.setRequestProperty("contentType", "UTF-8");
    		 // 设置HTTP头:
    		 conn.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + BOUNDARY);
    		 conn.setRequestProperty("Content-Length", String.valueOf(data.length + file.length + end_data.length));
    		 // 输出:
//    		 out = new OutputStreamWriter(conn.getOutputStream());
    		 out = conn.getOutputStream();
    		 out.write(data);
    		 out.write(file);
    		 out.write(end_data);

	         // 把数据写入请求的Body
//	         out.write(param);
	         out.flush();
	         in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	         String line;
	         while ((line = in.readLine()) != null) {
	             result += line;
	         }
	     } catch (Exception e) {
	         logger.error("发送失败" + e);
	         e.printStackTrace();
	     } finally {
	         try {
	             if (out != null) {
	                 out.close();
	             }
	             if (in != null) {
	                 in.close();
	             }
	         } catch (IOException ex) {
	             ex.printStackTrace();
	         }
	     }
	    return result;
	}
	
	
	
	@RequestMapping(value = { "/tttver" }, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String tttver(HttpServletRequest request, HttpServletResponse response) throws Exception {
		 request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
		 BufferedReader in = null;
	     OutputStream out = null;
	     String result = "";
	     try {
	         URL realUrl = new URL("https://api.megvii.com/faceid/v2/verify");
	         SslUtils.ignoreSsl();
	         HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
	         conn.setRequestMethod("POST");
	         conn.setDoOutput(true);
	         conn.setDoInput(true);
     		 String[] props = new String[]{"api_key","api_secret","comparison_type","face_image_type","idcard_name","idcard_number","delta"}; // 字段名
    		 String[] values = new String[]{"vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5","Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm","1","meglive","余发军","420322198610102733","YWHDF-HHY"};// 字段值
    		 byte[] file = FileHelper.getImageByteArray("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/3874227a17ed4fca.jpg");
    		 String BOUNDARY = "---------------------------7d4a6d158c9"; // 分隔符
    		 StringBuffer sb = new StringBuffer();
    		 // 发送每个字段:
    		 for(int i=0; i < props.length ; i ++ ) {
	    		 sb = sb.append("--");
	    		 sb = sb.append(BOUNDARY);
	    		 sb = sb.append("\r\n");
	    		 sb = sb.append("Content-Disposition: form-data; name=\""+ props[i] + "\"\r\n\r\n");
	    		 sb = sb.append(URLEncoder.encode(values[i]));
	    		 sb = sb.append("\r\n");
    		 }
    		 // 发送文件:
    		 sb = sb.append("--");
    		 sb = sb.append(BOUNDARY);
    		 sb = sb.append("\r\n");
    		 sb = sb.append("Content-Disposition: form-data; name=\"image_best\"; filename=\"1.txt\"\r\n");
    		 sb = sb.append("Content-Type: application/octet-stream\r\n\r\n");
    		 byte[] data = sb.toString().getBytes();
    		 byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
    		 
    		 conn.setRequestProperty("Accept-Charset", "UTF-8");
    		 conn.setRequestProperty("contentType", "UTF-8");
    		 // 设置HTTP头:
    		 conn.setRequestProperty("Content-Type", "multipart/form-data" + "; boundary=" + BOUNDARY);
    		 conn.setRequestProperty("Content-Length", String.valueOf(data.length + file.length + end_data.length));
    		 // 输出:
//    		 out = new OutputStreamWriter(conn.getOutputStream());
    		 out = conn.getOutputStream();
    		 out.write(data);
    		 out.write(file);
    		 out.write(end_data);

	         // 把数据写入请求的Body
//	         out.write(param);
	         out.flush();
	         in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	         String line;
	         while ((line = in.readLine()) != null) {
	             result += line;
	         }
	     } catch (Exception e) {
	         logger.error("发送失败" + e);
	         e.printStackTrace();
	     } finally {
	         try {
	             if (out != null) {
	                 out.close();
	             }
	             if (in != null) {
	                 in.close();
	             }
	         } catch (IOException ex) {
	             ex.printStackTrace();
	         }
	     }
	    return result;
	}
	
	
	@RequestMapping(value = { "/tttttt" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String tttttt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://api.faceid.com/faceid/v1/ocridcard");
		URL url = new URL("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/de6fb62d42ef080c.jpg");
		InputStream is = url.openStream();
		HttpEntity entity = MultipartEntityBuilder.create().addTextBody("api_key", "vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5")
				.addTextBody("api_secret", "Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm")
				.addPart("image", new InputStreamBody(is, "aa.txt"))
				.build();
		SslUtils.ignoreSsl();
		httppost.setHeader("contentType", "UTF-8");  
		httppost.setEntity(entity);
		HttpResponse httpResponse = httpclient.execute(httppost);
		httpResponse.getStatusLine().getStatusCode();
//		  if(httpResponse.getStatusLine().getStatusCode() == 200)  
              HttpEntity httpEntity = httpResponse.getEntity();  
              String sresult = EntityUtils.toString(httpEntity,"UTF-8");//取出应答字符串  
              System.out.println(sresult);
          return sresult;
	}

	@RequestMapping(value = { "/tttttt111" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String tttttt111(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://api.megvii.com/faceid/v2/verify");
//		SslUtils.ignoreSsl();
		URL url = new URL("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/294bd573d8e95674.jpg");
		InputStream is = url.openStream();
		byte[] file = FileHelper.getImageByteArray("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/70b3470cb2c5501d.png");
		HttpEntity entity = MultipartEntityBuilder.create().addTextBody("api_key", "vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5")
				.addTextBody("api_secret", "Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm")
				.addTextBody("comparison_type", "1")
				.addTextBody("face_image_type", "meglive")
				.addTextBody("idcard_name", "俞毓民")
				.addTextBody("idcard_number", "330726199311151519")
				.addTextBody("delta", "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")
				.addPart("image", new InputStreamBody(is, "aa.txt"))
				.addPart("image_best", new InputStreamBody(is, "aa.txt"))
				.build();
		httppost.setHeader("contentType", "UTF-8");  
		httppost.setEntity(entity);
		HttpResponse httpResponse = httpclient.execute(httppost);
		httpResponse.getStatusLine().getStatusCode();
//		  if(httpResponse.getStatusLine().getStatusCode() == 200)  
              HttpEntity httpEntity = httpResponse.getEntity();  
              String sresult = EntityUtils.toString(httpEntity,"UTF-8");//取出应答字符串  
              System.out.println(sresult);
          return sresult;
	}
	
	@RequestMapping(value = { "/aaa" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String aaaa(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		FacePlusCardRespBo resp = facePlusUtil.checkCard("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/3901abddd5614a55.jpg", "http://51fanbei.oss-cn-hangzhou.aliyuncs.com/test/de6fb62d42ef080c.jpg");
        return resp.toString();
	}
	
}
