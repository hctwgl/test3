package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfContactsOldDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.alibaba.druid.util.StringUtils;
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
	
	@RequestMapping(value = { "/wxRefund" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String wxRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		JSONObject json = JSONObject.parseObject(body);
		String orderNo = json.getString("orderNo");
		String payTradeNo = json.getString("payTradeNo");
		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
		BigDecimal totalAmount = NumberUtil.objToBigDecimalDefault(json.getString("totalAmount"), null);
		logger.info("wxRefund begin wxRefund is orderNo = {}, payTradeNo = {}, refundAmount = {}, refundAmount = {}", new Object[] { orderNo, payTradeNo, refundAmount, totalAmount });
		if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(payTradeNo) || refundAmount == null || totalAmount == null) {
			return "";
		}
		String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
		logger.info("wxRefund refundResult = {}", refundResult);
		System.out.println(refundResult);
		return "succ";
	}
	

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
//	/**
//	 * 银行卡退款
//	 * @author fumeiai
//	 * @return
//	 */
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
	
	// TongdunUtil
	
//	@RequestMapping(value = { "/wxRefundMobile" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String wxRefundMobile(@RequestBody String body, HttpServletRequest request, HttpServletResponse response){
//		String message = "succ!";
//		try {
//			JSONObject json = JSONObject.parseObject(body);
//			String orderId = json.getString("orderId");
//			String scret = json.getString("scret");
//			if(!"zsdERfds2123".equals(scret)){
//				throw new RuntimeException("秘钥不对");
//			}
//			AfOrderDo order = afOrderDao.getOrderById(Long.valueOf(orderId));
//			String refundNo = generatorClusterNo.getRefundNo(new Date());
//			String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
//			if(!"SUCCESS".equals(refundResult)){
//				afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL,PayType.WECHAT,"",null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),order.getPayTradeNo()));
//				throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
//			}else{
//            	afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(),order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH,PayType.WECHAT,"",null,"充值失败微信退款",RefundSource.PLANT_FORM.getCode(),order.getPayTradeNo()));
//			}
//		} catch (Exception e) {
//			logger.info("wxRefund error:",e);
//			message = "There is no trade can refund!";
//		}
//		return message;
//	}
}
