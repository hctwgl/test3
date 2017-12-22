package com.ald.fanbei.api.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomePushPayResponseBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BrandCouponResponseBo;
import com.ald.fanbei.api.biz.bo.InterestFreeJsonBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfContactsOldService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.service.CouponSceneRuleEnginer;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.RiskTrackerService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.third.util.RiskRequestProxy;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.huichaopay.HuichaoUtility;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowCalculateMethod;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentDetalDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfContactsOldDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;
import com.ald.fanbei.api.dal.domain.RiskTrackerDo;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.domain.XItem;


@Controller
public class TestController {
    Logger logger = LoggerFactory.getLogger(TestController.class);

    @Resource
    YiBaoUtility yiBaoUtility;
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
    RiskTrackerService riskTrackerService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfBorrowDao afBorrowDao;
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfUserVirtualAccountService afUserVirtualAccountService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    private TaobaoApiUtil taobaoApiUtil;
    @Autowired
    RiskRequestProxy requestProxy;

    @Resource
    RedisTemplate redisTemplate;

    @RequestMapping("/compensate")
    @ResponseBody
    public String compensate() {
        try{
            RiskTrackerDo riskTrackerDo = new RiskTrackerDo();
            List<RiskTrackerDo> riskTrackerDoList = riskTrackerService.getListByCommonCondition(riskTrackerDo);
            for (RiskTrackerDo item : riskTrackerDoList) {
                HashMap reqBo1 = JSON.parseObject(item.getParams(), HashMap.class);
                //String data1 = getUrlParamsByMap(reqBo1);
//                String reqResult1 = HttpUtil.post(item.getUrl(), reqBo1);
//                if (StringUtil.isBlank(reqResult1)) {
//                    throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
//                }
                if (item.getUrl().indexOf("updateOpenId") != -1 && item.getTrackId().indexOf("success_") == -1) {//未处理过得提额
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        if (StringUtil.isBlank(reqResult)) {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                        RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
                        if (riskResp != null && "0000".equals(riskResp.getCode())) {
                            item.setTrackId("success_" + item.getTrackId());
                            riskTrackerService.updateById(item);
                        } else {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("updateOpenId compensate exception:", e);
                    }

                }
                else if (item.getUrl().indexOf("raiseQuota") != -1 && item.getTrackId().indexOf("success_") == -1) {//未处理过得提额
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        if (StringUtil.isBlank(reqResult)) {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                        RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
                        if (riskResp != null && "0000".equals(riskResp.getCode())) {
                            riskResp.setSuccess(true);
                            JSONObject dataObj = JSON.parseObject(riskResp.getData());
                            BigDecimal au_amount = new BigDecimal(dataObj.getString("amount"));
                            Long consumerNum = Long.parseLong(reqBo.get("consumerNo").toString());

                            AfUserAccountDo accountDo = new AfUserAccountDo();
                            accountDo.setUserId(consumerNum);
                            accountDo.setAuAmount(au_amount);
                            afUserAccountService.updateUserAccount(accountDo);
                            item.setTrackId("success_" + item.getTrackId());
                            riskTrackerService.updateById(item);
                        } else {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("raiseQuota compensate exception:", e);
                    }

                } else if (item.getUrl().indexOf("/overdueOrder.htm") != -1 && item.getTrackId().indexOf("success_") == -1) {
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        logger.info("overdueOrder.htm compensate  overdueOrder:" + reqResult);
                        item.setTrackId("success_" + item.getTrackId());
                        riskTrackerService.updateById(item);
                    } catch (Exception e) {
                        logger.error("overdueOrder compensate exception:", e);
                    }

                } else if (item.getUrl().indexOf("/repayment.htm") != -1 && item.getTrackId().indexOf("success_") == -1) {
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        logger.info("repayment.htm compensate  repayment:" + reqResult);
                        item.setTrackId("success_" + item.getTrackId());
                        riskTrackerService.updateById(item);
                    } catch (Exception e) {
                        logger.error("repayment compensate exception:", e);
                    }
                }

            }
        }catch (Exception e){
            logger.error("compensate error:",e);
        }
        return "调用处理中^";
    }

    @RequestMapping("/cuishou")
    @ResponseBody
    public String cuishou() {
        //AfRepaymentBorrowCashDo existItem = afRepaymentBorrowCashService.getRepaymentBorrowCashByTradeNo(1302389l, "20170727200040011100260068825762");
        ExecutorService pool = Executors.newFixedThreadPool(16);
        for (int i=0;i<1000;i++){
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(new Date());
                    System.out.println("---"+repayNo);
                }
            });
        }


       // riskUtil.syncOpenId(1302389,"268811897276756002554870029");
        return "调用处理中^";

    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByHashMap(HashMap<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
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
    @RequestMapping(value = {"/h5/app/*_new", "/h5/app/sys/*_new", "/h5/app/goods/*_new", "/h5/app/mine/*_new", "/h5/app/order/*_new"}, method = RequestMethod.GET)
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
    @RequestMapping(value = {"testPush"}, method = RequestMethod.GET)
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
     *
     * @return
     * @author fumeiai
     */
    @RequestMapping(value = {"/SyncUserAddressList/toRiskManagement"}, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
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
                try {
                    AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
                    logger.info("i=" + i + "," + afContactsOldDo != null ? afContactsOldDo.toString() : "");
                    if (null != afContactsOldDo) {
                        String moblieBook = afContactsOldDo.getMobileBook();
                        String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"") + 1, moblieBook.lastIndexOf("\""));

                        JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
                        StringBuffer data = new StringBuffer();
                        for (Object object : moblieBookJsons) {
                            JSONObject json = JSONObject.parseObject(object.toString());
                            data.append(json.getString("name") + ":");
                            data.append(json.getString("phone_number") + ",");
                        }
                        logger.info("i=" + i + "," + data.toString());

                        riskUtil.addressListPrimaries(afContactsOldDo.getUid().toString(), data.toString().substring(0, data.toString().length() - 1));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("init error=" + list.get(i).getUserId());
                }
            }
        }
        return "succ";
    }

    @RequestMapping(value = {"/wxRefund"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String wxRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = JSONObject.parseObject(body);
        String orderNo = json.getString("orderNo");
        String payTradeNo = json.getString("payTradeNo");
        BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
        BigDecimal totalAmount = NumberUtil.objToBigDecimalDefault(json.getString("totalAmount"), null);
        logger.info("wxRefund begin wxRefund is orderNo = {}, payTradeNo = {}, refundAmount = {}, refundAmount = {}", new Object[]{orderNo, payTradeNo, refundAmount, totalAmount});
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

    /**
     * 银行卡退款
     *
     * @return
     * @author fumeiai
     */
    @RequestMapping(value = {"/bankRefund"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String bankRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = JSONObject.parseObject(body);
//		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
        String payTradeNo = json.getString("payTradeNo");
        AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNoWithStatusY(payTradeNo);
        String message = "succ!";
        if (null != afRepaymentBorrowCashDo) {
            BigDecimal refundAmount = afRepaymentBorrowCashDo.getActualAmount();
//			BigDecimal refundAmount = new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP);
            AfUserBankcardDo card = afUserBankcardDao.getUserBankcardByCardNo(afRepaymentBorrowCashDo.getCardNumber());
            AfUserDo userDo = afUserDao.getUserById(card.getUserId());
            UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(refundAmount, userDo.getRealName(), card.getCardNumber(), card.getUserId() + "",
                    card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", OrderType.MOBILE.getCode(), "");
            String refundNo = generatorClusterNo.getRefundNo(new Date());
            if (!upsResult.isSuccess()) {
                AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "",
                        OrderRefundStatus.FAIL, PayType.BANK, card.getCardNumber(), card.getBankName(), "用户现金借中重复还款后的退款", RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo());
                afOrderRefundDao.addOrderRefund(afOrderRefundDo);
                message = "Fail!";
            } else {
                AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "",
                        OrderRefundStatus.FINISH, PayType.BANK, card.getCardNumber(), card.getBankName(), "用户现金借中重复还款后的退款", RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo());
                AfRepaymentBorrowCashDo repaymentBorrowCashDo = new AfRepaymentBorrowCashDo();
                repaymentBorrowCashDo.setRid(afRepaymentBorrowCashDo.getRid());
                repaymentBorrowCashDo.setStatus("R");
                afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repaymentBorrowCashDo);
                afOrderRefundDao.addOrderRefund(afOrderRefundDo);
            }
        } else {
            message = "There is no trade can refund!";
        }

        return message;
    }

    // TongdunUtil

    @RequestMapping(value = {"/wxRefundMobile"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxRefundMobile(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        String message = "succ!";
        try {
            JSONObject json = JSONObject.parseObject(body);
            String orderId = json.getString("orderId");
            String scret = json.getString("scret");
            if (!"zsdERfds2123".equals(scret)) {
                throw new RuntimeException("秘钥不对");
            }
            AfOrderDo order = afOrderDao.getOrderById(Long.valueOf(orderId));
            String refundNo = generatorClusterNo.getRefundNo(new Date());
            String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
            if (!"SUCCESS".equals(refundResult)) {
                afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL, PayType.WECHAT, "", null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
                throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
            } else {
                afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH, PayType.WECHAT, "", null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
            }
        } catch (Exception e) {
            logger.info("wxRefund error:", e);
            message = "There is no trade can refund!";
        }
        return message;
    }

    @RequestMapping(value = {"/changeShopName"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String changeShopName(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        String message = "succ!";
        try {
            JSONObject json = JSONObject.parseObject(body);
            String pageNo = json.getString("pageNo");

            String scret = json.getString("scret");
            if (!"zsdERfds2123".equals(scret)) {
                throw new RuntimeException("秘钥不对");
            }
            List<AfOrderDo> list = afOrderDao.getNotShopNameByAgentBuyOrder(Long.valueOf(pageNo));
            List<String> orderNumIdsList = CollectionConverterUtil.convertToListFromList(list,
                    new Converter<AfOrderDo, String>() {
                        @Override
                        public String convert(AfOrderDo source) {
                            return source.getNumId();
                        }
                    });
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("numIid", StringUtil.turnListToStr(orderNumIdsList));
            List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();

            for (XItem xItem : nTbkItemList) {
                String orderType = xItem.getMall() ? "TMALL" : "TAOBAO";
                String nick = xItem.getNick();
                if (xItem.getOpenId() != 0) {
                    for (AfOrderDo orderDo : list) {
                        if (StringUtils.equals(xItem.getOpenId() + "", orderDo.getNumId())) {
                            AfOrderDo orderN = new AfOrderDo();
                            orderN.setRid(orderDo.getRid());
                            orderN.setShopName(nick);
                            orderN.setSecType(orderType);
                            afOrderDao.updateOrder(orderN);
                        }
                    }
                }

            }

        } catch (Exception e) {
            logger.info("changeShopName error:", e);
            message = "There is  changeShopName ";
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
    @RequestMapping(value = {"/allowcateBrandCoupon"}, method = RequestMethod.POST)
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

    @RequestMapping(value = {"/jPushByType"}, method = RequestMethod.GET)
    @ResponseBody
    public String jPushByType(int jumpType, String type, String userName) {
        PrintWriter out = null;
        try {
            jpushService.jPushByType(jumpType, type, userName);
            ;
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


    @RequestMapping(value = {"/jPushCoupon"}, method = RequestMethod.GET)
    @ResponseBody
    public String jPushCoupon(String type, String userName) {
        PrintWriter out = null;
        try {
            jpushService.jPushCoupon(type, userName);
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


    @RequestMapping(value = {"/testJPush"})
    @ResponseBody
    public String testJPush(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String userName = request.getParameter("userName");
            jpushService.pushHeaderImage(userName);
            ;
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

    @RequestMapping(value = {"/testAllJPush"})
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

    @RequestMapping(value = {"/testOrderPay"}, method = RequestMethod.POST)
    @ResponseBody
    public BoluomePushPayResponseBo testOrderPay(HttpServletRequest request, HttpServletResponse response) {
        long orderId = 198649;
        AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
        BoluomePushPayResponseBo b = boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount() ,orderInfo.getSecType());

        return b;
    }

    private void pickBrandCoupon(String userName, String brandUrl) {
        AfUserDo userInfo = afUserDao.getUserByUserName(userName);
        if (userInfo == null) {
            logger.info(userName + "userName dosn't exist");
            return;
        }
        PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
        bo.setUser_id(userInfo.getRid() + StringUtil.EMPTY);
        String resultString = HttpUtil.doHttpPostJsonParam(brandUrl, JSONObject.toJSONString(bo));
        logger.info("userName = " + userName + " brandUrl = " + brandUrl);
        logger.info("allowcateBrandCoupon pickBrandCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
    }

    @RequestMapping(value = {"/testRiskQueryOverdueOrder"}, method = RequestMethod.POST)
    @ResponseBody
    public String testRiskQueryOverdueOrder(HttpServletRequest request, HttpServletResponse response) {
        RiskQueryOverdueOrderRespBo resp = riskUtil.queryOverdueOrder("68885");
        System.out.println(resp);
        return "success";
    }

    @RequestMapping(value = {"/test11"}, method = RequestMethod.POST)
    @ResponseBody
    public String bo(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", "100000000123");
        params.put("appKey", "2607839913");
        params.put("timestamp", "1500628924755");
        System.out.println(BoluomeCore.builSign(params));

//		String identity = System.currentTimeMillis() + StringUtil.EMPTY;
//		String orderNo = riskUtil.getOrderNo("over", identity.substring(identity.length() - 4, identity.length()));
//		List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
//		RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
//		bo.setBorrowNo("jk2017071020281800843");
//		bo.setOverdueDays(0);
//		bo.setOverdueTimes(1);
//		boList.add(bo);
//		logger.info("dealWithSynchronizeOverduedOrder begin orderNo = {} , boList = {}", orderNo, boList);
//		riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
//		logger.info("dealWithSynchronizeOverduedOrder completed");
        return "success";
    }

    @RequestMapping(value = {"/dealWithBorrow"}, method = RequestMethod.POST)
    @ResponseBody
    public String dealWithBorrow(HttpServletRequest request, HttpServletResponse response) {
        List<AfOrderDo> list = afOrderDao.get20170801ExceptionOrder();
        for (AfOrderDo orderInfo : list) {
            AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
            String name = orderInfo.getGoodsName();

            AfBorrowDo borrow = buildAgentPayBorrow(name, BorrowType.TOCONSUME, orderInfo.getUserId(), orderInfo.getActualAmount(),
                    orderInfo.getNper(), BorrowStatus.APPLY.getCode(), orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson());

            Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
            String virtualCode = getVirtualCode(virtualMap);
            //是虚拟商品
            if (StringUtils.isNotBlank(virtualCode)) {
                AfUserVirtualAccountDo virtualAccountInfo = BuildInfoUtil.buildUserVirtualAccountDo(orderInfo.getUserId(), orderInfo.getActualAmount(), orderInfo.getActualAmount(),
                        orderInfo.getRid(), orderInfo.getOrderNo(), virtualCode);
                //增加虚拟商品记录
                afUserVirtualAccountService.saveRecord(virtualAccountInfo);
            }

            orderInfo.setPayStatus(PayStatus.PAYED.getCode());
            orderInfo.setStatus(OrderStatus.PAID.getCode());
            orderInfo.setPayType(PayType.AGENT_PAY.getCode());
            // 新增借款信息
            afBorrowDao.addBorrow(borrow);
            // 在风控审批通过后额度不变生成账单
            afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(), userAccountInfo.getUserName(), orderInfo.getActualAmount(), PayType.AGENT_PAY.getCode(), orderInfo.getOrderType());
            // 修改用户账户信息
            AfUserAccountDo account = new AfUserAccountDo();
            account.setUsedAmount(orderInfo.getActualAmount());
            account.setUserId(userAccountInfo.getUserId());
            afUserAccountDao.updateUserAccount(account);

            logger.info("updateOrder orderInfo = {}", orderInfo);
            afOrderDao.updateOrder(orderInfo);
        }

        return "success";
    }

    //3.7.6初始化借钱缓存，用于app端高亮显示
    @RequestMapping(value = {"/initBorrowCache"}, method = RequestMethod.GET)
    @ResponseBody
    public void initBorrowCache() {
        logger.info("initBorrowCache,start");
        List<String> ids = afBorrowCashService.getBorrowedUserIds();
        if (ids != null) {
            bizCacheUtil.saveRedistSet(Constants.HAVE_BORROWED, ids);
        }
        logger.info("initBorrowCache,end");
    }


    @RequestMapping(value = {"/boluomeCoupon"}, method = RequestMethod.GET)
    @ResponseBody
    public void boluomeCoupon() {
        List<BrandCouponResponseBo> list = boluomeUtil.getUserCouponList(68885L, 1, 1, 20);
        logger.info("boluomeCoupon,start");
        List<BrandActivityCouponResponseBo> list1 = boluomeUtil.getActivityCouponList("https://dev-api.otosaas.com/bss/v1/apps/157/campaigns/775/give");
        logger.info("boluomeCoupon,end");
        System.out.println(boluomeUtil.isUserHasCoupon("https://dev-api.otosaas.com/bss/v1/apps/157/campaigns/775/give", 68885L, 1));

    }

    @Resource
    AfDeGoodsService afDeGoodsService;
    @Resource
    AfUserService afUserService;

    @RequestMapping(value = "/activity/de/goods/test", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public H5CommonResponse getGoodsList(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            JSONObject json = JSONObject.parseObject(body);
            String userName = json.getString("userName");
            AfUserDo user = afUserService.getUserByUserName(userName);
            Long userId = user.getRid();

            List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
            data.put("goodsList", userDeGoodsList);

            data.put("endTime", System.currentTimeMillis() / 1000 + 10000);
            data.put("totalCount", "100");

            return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
        } catch (Exception e) {
            logger.error("/activity/de/goods error = {}", e);
            return H5CommonResponse.getNewInstance(false, "获取砍价商品列表失败");
        }
    }

    public String getVirtualCode(Map<String, Object> resultMap) {
        if (resultMap == null) {
            return null;
        }
        if (resultMap.get(Constants.VIRTUAL_CODE) == null) {
            return null;
        }
        return resultMap.get(Constants.VIRTUAL_CODE).toString();
    }

    /**
     * @param name             分期名称
     * @param type             分期类型
     * @param userId           用户id
     * @param amount           分期金额
     * @param nper             分期期数
     * @param perAmount        每期金额
     * @param status           状态
     * @param orderId          订单id
     * @param orderNo          订单编号
     * @param borrowRate       借款利率等参数
     * @param interestFreeJson 分期规则
     * @return
     */
    private AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal amount, int nper, String status, Long orderId, String orderNo, String borrowRate, String interestFreeJson) {

        Integer freeNper = 0;
        List<InterestFreeJsonBo> interestFreeList = StringUtils.isEmpty(interestFreeJson) ? null : JSONObject.parseArray(interestFreeJson, InterestFreeJsonBo.class);
        if (CollectionUtils.isNotEmpty(interestFreeList)) {
            for (InterestFreeJsonBo bo : interestFreeList) {
                if (bo.getNper().equals(nper)) {
                    freeNper = bo.getFreeNper();
                    break;
                }
            }
        }
        //拿到日利率快照Bo
        BorrowRateBo borrowRateBo = BorrowRateBoUtil.parseToBoFromDataTableStr(borrowRate);
        //每期本金
        BigDecimal principleAmount = amount.divide(new BigDecimal(nper), 2, RoundingMode.DOWN);
        //每期利息
        BigDecimal interestAmount = amount.multiply(borrowRateBo.getRate()).divide(Constants.DECIMAL_MONTH_OF_YEAR, 2, RoundingMode.CEILING);
        //每期手续费
        BigDecimal poundageAmount = BigDecimalUtil.getPerPoundage(amount, nper, borrowRateBo.getPoundageRate(), borrowRateBo.getRangeBegin(), borrowRateBo.getRangeEnd(), freeNper);

        BigDecimal perAmount = BigDecimalUtil.add(principleAmount, interestAmount, poundageAmount);

        Date currDate = new Date();
        AfBorrowDo borrow = new AfBorrowDo();
        borrow.setGmtCreate(currDate);
        borrow.setAmount(amount);
        borrow.setType(type.getCode());
        borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
        borrow.setStatus(status);// 默认转账成功
        borrow.setName(name);
        borrow.setUserId(userId);
        borrow.setNper(nper);
        borrow.setNperAmount(perAmount);
        borrow.setCardNumber(StringUtils.EMPTY);
        borrow.setCardName("代付");
        borrow.setRemark(name);
        borrow.setOrderId(orderId);
        borrow.setOrderNo(orderNo);
        borrow.setBorrowRate(borrowRate);
        borrow.setCalculateMethod(BorrowCalculateMethod.DENG_BEN_DENG_XI.getCode());
        borrow.setFreeNper(freeNper);
        return borrow;
    }

    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;

    @Resource
    AfYibaoOrderDao afYiBaoOrderDao;
    @Resource
    private AfRepaymentDetalDao afRepaymentDetalDao;

	@Resource
	HuichaoUtility huichaoUtility;
    /**
     *
     */
    @RequestMapping(value = {"/testYiBao"}, method = RequestMethod.GET)
    public void testAddYiBao() {
//		String appid =  AesUtil.decrypt("Ehw14/ML0cbFSiBoVFC1mu6iw/dWDLmwlSlJTCWC/veLZVdz4LtvQ7My3Rfnuzwq/du56FGZDN1TRs9yv/Zn+4N2RXBD1dBHtugwJDhi3Bs=","Cw5bM6x@6sH$2dlw^3JueH");
//		String puk = AesUtil.decrypt("QRaDVWb2pC9by28Rxt8sMQ==","Cw5bM6x@6sH$2dlw^3JueH");
//		String pik = AesUtil.decrypt("fnzwqgFMW/RyuzDKRkH9uP/XN6RgBH5QkvtGwHR2gVs=","Cw5bM6x@6sH$2dlw^3JueH");
//
//		AesUtil.encryptToBase64(appid,"Cw5bM6x@6sH$2dlw^3JueH");

//		Map m = yiBaoUtility.createOrder(BigDecimal.TEN,"hk"+String.valueOf (new Date().getTime()/1000),20158l,PayOrderSource.BORROWCASH);
//		String token =  m.get("token").toString();
//		String d= yiBaoUtility.getCashier(token,20158l);
		//Map<String,String> addda = yiBaoUtility.getYiBaoOrder("hq2017090815262700180","1001201709080000000015990156");
//		String e="";
		//huichaoUtility.getHuiCaoOrder("1509598766744");

		huichaoUtility.getOrderStatus("xj2017112411401400326");

		//HashMap srce =  huichaoUtility.createOrderZFB("zfaaaabcdef1","1",13989456178L, PayOrderSource.RENEWAL_PAY);
		//afYiBaoOrderDao.updateYiBaoOrderStatusByOrderNo("adfasdfadsf11111",1);
		String e = "";
    }


    private List<HashMap> getListByStatus(List<AfBorrowBillDo> list, int status) {
        List<HashMap> _list = new ArrayList<HashMap>();
        for (AfBorrowBillDo afBorrowBillDo : list) {
            HashMap map = getBillByStatus(afBorrowBillDo, status);
            if (map != null && map.size() > 0) {
                _list.add(map);
            }
        }
        return _list;
    }

    /**
     * @param afBorrowDo
     * @param status     1 己出，2 逾期，3 未出
     * @return
     */
    private HashMap getBillByStatus(AfBorrowBillDo afBorrowBillDo, int status) {

        HashMap map = new HashMap();
        if (status == 1) {
            if (afBorrowBillDo.getOverdueStatus().equals("Y")) {
                return map;
            }
            if (isOut(afBorrowBillDo.getBillYear(), afBorrowBillDo.getBillMonth())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        } else if (status == 2) {
            if (afBorrowBillDo.getOverdueStatus().equals("Y")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        } else if (status == 3) {
            if (!isOut(afBorrowBillDo.getBillYear(), afBorrowBillDo.getBillMonth())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        return map;
    }


    private boolean isOut(int year, int month) {
        Date d = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, year);
        c1.set(Calendar.MONTH, month - 1);
        c1.set(Calendar.DAY_OF_MONTH, 10);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String a = s.format(c1.getTime());
        boolean flag = c1.getTime().before(d);
        return flag;
    }


    @Resource
    AfUserOutDayDao afUserOutDayDao;

    /**
     * 帐单日处理
     *
     * @param userId
     */
    public void addOutDay(long userId) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            int out_day = calendar.get(Calendar.DAY_OF_MONTH);
            List<Integer> _nodays = new ArrayList<>();
            _nodays.add(19);
            _nodays.add(20);
            List<Integer> _nodays1 = new ArrayList<>();
            _nodays1.add(29);
            _nodays1.add(30);
            _nodays1.add(31);
            AfUserOutDayDo afUserOutDayDo = new AfUserOutDayDo();
            int pay_day = 0;
            if (_nodays.contains(out_day)) {
                out_day = 21;
                pay_day = 1;

            } else if (_nodays1.contains(out_day)) {
                out_day = 1;
                pay_day = 11;
            } else {
                pay_day = out_day + 10;
                if (pay_day > 30) pay_day = pay_day - 30;

            }
            afUserOutDayDo.setUserId(userId);
            afUserOutDayDo.setOutDay(out_day);
            afUserOutDayDo.setPayDay(pay_day);
            afUserOutDayDao.addserOutDay(afUserOutDayDo);
        } catch (Exception e) {
            logger.info("add out_day error:", e);
            logger.error("add out_day error", e);
        }
    }


}
