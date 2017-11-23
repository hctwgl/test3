package com.ald.fanbei.api.web.third.controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.biz.foroutapi.service.HomeBorrowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxXMLParser;
import com.ald.fanbei.api.biz.third.util.yibaopay.YeepayService;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.WxTradeState;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;

/**
 * @author chenjinhu 2017年2月20日 下午2:59:32
 * @类现描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/ups")
public class PayRoutController {
    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    AfUpsLogDao afUpsLogDao;
    @Resource
    private AfOrderDao afOrderDao;
    @Resource
    private AfOrderService afOrderService;
    @Resource
    private AfBorrowService afBorrowService;
    @Resource
    private AfCashRecordDao afCashRecordDao;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    private AfRepaymentService afRepaymentService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    private AfBorrowCashService afBorrowCashService;
    @Resource
    private AfUserAccountService afUserAccountService;
    @Resource
    private AfOrderRefundService afOrderRefundService;
    @Resource
    private AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    private AfTradeWithdrawRecordService afTradeWithdrawRecordService;
    @Resource
    private AfYibaoOrderDao afYibaoOrderDao;

    @Resource
    private YiBaoUtility yiBaoUtility;
    @Resource
    RedisTemplate redisTemplate;

    @Resource
    AfBankDao afBankDao;
    private static String TRADE_STATUE_SUCC = "00";
    private static String TRADE_STATUE_FAIL = "10"; // 处理失败

    @RequestMapping(value = {"/authSignReturn"}, method = RequestMethod.POST)
    @ResponseBody
    public String authSignReturn(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }

    @RequestMapping(value = {"/authSignNotify"}, method = RequestMethod.POST)
    @ResponseBody
    public String authSignNotify(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }

    @RequestMapping(value = {"/authSignValidNotify"}, method = RequestMethod.POST)
    @ResponseBody
    public String authSignValidNotify(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }

    @RequestMapping(value = {"/authPay"}, method = RequestMethod.POST)
    @ResponseBody
    public String authPay(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }

    @RequestMapping(value = {"/authPayConfirm"}, method = RequestMethod.POST)
    @ResponseBody
    public String authPayConfirm(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }


    @RequestMapping(value = {"/delegatePay"}, method = RequestMethod.POST)
    @ResponseBody
    public String delegatePay(HttpServletRequest request, HttpServletResponse response) {
        String outTradeNo = request.getParameter("orderNo");
        String merPriv = request.getParameter("merPriv");
        String tradeState = request.getParameter("tradeState");
        long result = NumberUtil.objToLongDefault(request.getParameter("reqExt"), 0);
        logger.info("delegatePay begin merPriv=" + merPriv + ",tradeState=" + tradeState + ",reqExt=" + result+ ",outTradeNo=" + outTradeNo);
        try {
            if (TRADE_STATUE_SUCC.equals(tradeState)) {//代付成功
                if (UserAccountLogType.CASH.getCode().equals(merPriv)) {//现金借款
                    //生成账单
                    AfBorrowDo borrow = afBorrowService.getBorrowById(result);
                    afBorrowService.cashBillTransfer(borrow, afUserAccountService.getUserAccountByUserId(borrow.getUserId()));
                } else if (UserAccountLogType.CONSUME.getCode().equals(merPriv)) {//分期借款
                    //生成账单
                    AfBorrowDo borrow = afBorrowService.getBorrowById(result);
                    afBorrowService.consumeBillTransfer(afBorrowService.getBorrowById(result), afUserAccountService.getUserAccountByUserId(borrow.getUserId()));
                } else if (UserAccountLogType.REBATE_CASH.getCode().equals(merPriv)) {//提现
                    AfCashRecordDo record = new AfCashRecordDo();
                    record.setRid(result);
                    record.setStatus("TRANSED");
                    afCashRecordDao.updateCashRecord(record);
                } else if (UserAccountLogType.BorrowCash.getCode().equals(merPriv)) {//借款
                    Long rid = NumberUtil.objToLong(result);
                    AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
                    afBorrowCashDo.setStatus(AfBorrowCashStatus.transed.getCode());
//        			afBorrowCashService.updateBorrowCash(afBorrowCashDo);
                    afBorrowCashService.borrowSuccess(afBorrowCashDo);
                } else if (UserAccountLogType.BANK_REFUND.getCode().equals(merPriv)) {//菠萝觅银行卡退款
                    //退款记录
                    AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
                    AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
                    afOrderRefundService.dealWithOrderRefund(refundInfo, orderInfo, true);
                } else if (UserAccountLogType.AGENT_BUY_BANK_REFUND.getCode().equals(merPriv)) {//代买
                    //退款记录
                    AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
                    AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
                    afOrderRefundService.dealWithOrderRefund(refundInfo, orderInfo, false);
                } else if (UserAccountLogType.NORMAL_BANK_REFUND.getCode().equals(merPriv)) {
                    AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
                    AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
                    afOrderRefundService.dealWithSelfGoodsOrderRefund(refundInfo, orderInfo);
                } else if (UserAccountLogType.TRADE_BANK_REFUND.getCode().equals(merPriv)) {
                    AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
                    AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
                    afOrderRefundService.dealWithTradeOrderRefund(refundInfo, orderInfo);
                } else if (UserAccountLogType.TRADE_WITHDRAW.getCode().equals(merPriv)) {
                    afTradeWithdrawRecordService.dealWithDrawSuccess(result);
                }
                return "SUCCESS";
            } else if (TRADE_STATUE_FAIL.equals(tradeState)) {//只处理失败代付
                if (afUserAccountService.dealUserDelegatePayError(merPriv, result) > 0) {
                    return "SUCCESS";
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            logger.error("delegatePay", e);
            return "ERROR";
        }
    }

    @RequestMapping(value = {"/signRelease"}, method = RequestMethod.POST)
    @ResponseBody
    public String signRelease(HttpServletRequest request, HttpServletResponse response) {
        for (String paramKey : request.getParameterMap().keySet()) {
            thirdLog.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
        }
        return "succ";
    }

    @RequestMapping(value = {"/batchDelegatePay"}, method = RequestMethod.POST)
    @ResponseBody
    public String batchDelegatePay(HttpServletRequest request, HttpServletResponse response) {

/*		List<JSONObject> list = new ArrayList<JSONObject>();
        JSONObject obj = new JSONObject();
		obj.put("tradeNo", "dele200000000111");
		obj.put("amount", "0.01");
		obj.put("certNo", "330330330330");
		obj.put("bankName", "工商银行");
		obj.put("realName", "张三");
		obj.put("cardNo", "6222222222222");
		list.add(obj);
		UpsUtil.batchDelegatePay(new BigDecimal(0.01), "1377777777", "1", "代收", list.toString(), "02");*/

        return "succ";
    }

    /**
     * app中微信支付回调接口
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/wxpayNotify"}, method = RequestMethod.POST)
    @ResponseBody
    public String wxpayNotify(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        StringBuffer xmlStr = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }
            thirdLog.info("wxpayNotify=" + xmlStr.toString());
            // 验证通知是否是微信的通知
            Properties properties = WxXMLParser.parseXML(xmlStr.toString());
            String resultStr = WxSignBase.checkWxSign(properties, AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
            String checkSign = WxSignBase.byteToHex(WxSignBase.MD5Digest(resultStr.getBytes("utf-8")));

            if (!StringUtil.equals(properties.getProperty("sign"), checkSign.toUpperCase())) {
                return "succ";
            }

            String resultCode = properties.getProperty("result_code");
            String outTradeNo = properties.getProperty("out_trade_no");
            String transactionId = properties.getProperty("transaction_id");
            String attach = properties.getProperty("attach");

            if (StringUtil.equals(resultCode, WxTradeState.SUCCESS.getCode())) {
                if (PayOrderSource.ORDER.getCode().equals(attach)) {
                    afOrderService.dealMobileChargeOrder(outTradeNo, transactionId);
                } else if (PayOrderSource.REPAYMENT.getCode().equals(attach)) {
                    afRepaymentService.dealRepaymentSucess(outTradeNo, transactionId);
                } else if (PayOrderSource.BRAND_ORDER.getCode().equals(attach) || PayOrderSource.SELFSUPPORT_ORDER.getCode().equals(attach)) {
                    afOrderService.dealBrandOrderSucc(outTradeNo, transactionId, PayType.WECHAT.getCode());
                } else if (PayOrderSource.REPAYMENTCASH.getCode().equals(attach)) {
                    afRepaymentBorrowCashService.dealRepaymentSucess(outTradeNo, transactionId);
                } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(attach)) {
                    afRenewalDetailService.dealRenewalSucess(outTradeNo, transactionId);
                }
            } else {
                if (PayOrderSource.REPAYMENTCASH.getCode().equals(attach)) {
                    afRepaymentBorrowCashService.dealRepaymentFail(outTradeNo, transactionId, false, "");
                } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(attach)) {
                    afRenewalDetailService.dealRenewalFail(outTradeNo, transactionId,"");
                } else if (PayOrderSource.BRAND_ORDER.getCode().equals(attach) || PayOrderSource.SELFSUPPORT_ORDER.getCode().equals(attach)) {
                    afOrderService.dealBrandOrderFail(outTradeNo, transactionId, PayType.WECHAT.getCode());
                }
            }
        } catch (Exception e) {
            logger.error("wxpayNotify", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @RequestMapping(value = {"/collect"}, method = RequestMethod.POST)
    @ResponseBody
    public String collect(HttpServletRequest request, HttpServletResponse response) {
        String outTradeNo = request.getParameter("orderNo");
        String merPriv = request.getParameter("merPriv");
        String tradeNo = request.getParameter("tradeNo");
        String tradeState = request.getParameter("tradeState");
        String respCode = StringUtil.null2Str(request.getParameter("respCode"));
        String respDesc = StringUtil.null2Str(request.getParameter("respDesc"));
        String tradeDesc = StringUtil.null2Str(request.getParameter("tradeDesc"));
        logger.info("collect begin merPriv=" + merPriv + ",tradeState=" + tradeState + "tradeDesc:" + tradeDesc + ",outTradeNo=" + outTradeNo + ",tradeNo=" + tradeNo + ",respCode=" + respCode + ",respDesc=" + respDesc);
        try {
            if (TRADE_STATUE_SUCC.equals(tradeState)) {// 代收成功
                if (OrderType.MOBILE.getCode().equals(merPriv)) {// 手机充值订单处理
                    afOrderService.dealMobileChargeOrder(outTradeNo, tradeNo);
                } else if (UserAccountLogType.REPAYMENT.getCode().equals(merPriv)) {// 还款成功处理
                    afRepaymentService.dealRepaymentSucess(outTradeNo, tradeNo);
                    //回调成功发送短信
                    try{
                        String userNo = StringUtil.null2Str(request.getParameter("userNo"));
                        afRepaymentService.sendSuccessMessage(Long.valueOf(userNo),outTradeNo,tradeNo);
                    }catch (Exception e){
                        logger.error("send borrowbill send message error for:" + e);
                    }
                } else if (OrderType.BOLUOME.getCode().equals(merPriv) || OrderType.SELFSUPPORT.getCode().equals(merPriv)) {
                    afOrderService.dealBrandOrderSucc(outTradeNo, tradeNo, PayType.BANK.getCode());
                } else if (OrderType.AGENTCPBUY.getCode().equals(merPriv)) {
                    int result=afOrderService.dealAgentCpOrderSucc(outTradeNo, tradeNo, PayType.COMBINATION_PAY.getCode());
                    if(result<=0){
                        return "ERROR";
                    }
                } else if (OrderType.BOLUOMECP.getCode().equals(merPriv) || OrderType.SELFSUPPORTCP.getCode().equals(merPriv)) {
                   int result= afOrderService.dealBrandOrderSucc(outTradeNo, tradeNo, PayType.COMBINATION_PAY.getCode());
                    if (result <= 0) {
                        return "ERROR";
                    }
                } else if (UserAccountLogType.REPAYMENTCASH.getCode().equals(merPriv)) {
                    afRepaymentBorrowCashService.dealRepaymentSucess(outTradeNo, tradeNo);
                } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(merPriv)) {
                    afRenewalDetailService.dealRenewalSucess(outTradeNo, tradeNo);
                }
            } else if (TRADE_STATUE_FAIL.equals(tradeState)) {// 只处理代收失败的
                String errorWarnMsg = "";
                if (UserAccountLogType.REPAYMENTCASH.getCode().equals(merPriv)) {
                    if (StringUtil.isNotBlank(respDesc)) {
                        errorWarnMsg = StringUtil.processRepayFailThirdMsg(respDesc);
                    } else {
                        errorWarnMsg = StringUtil.processRepayFailThirdMsg(tradeDesc);
                    }
                    //加上银行卡信息
                    try{
                        String addMsg = "";
                        String bankCode = StringUtil.null2Str(request.getParameter("bankCode"));
                        String cardNum = StringUtil.null2Str(request.getParameter("cardNo"));
                        logger.info("发送短信增加银行卡信息,bankCode:"+bankCode+",cardNum:"+cardNum);
                        if(StringUtil.isNotBlank(errorWarnMsg)&&StringUtil.isNotBlank(bankCode)&&StringUtil.isNotBlank(cardNum)){
                            if(cardNum.length()>4){
                                cardNum = cardNum.substring(cardNum.length()- 4,cardNum.length());
                                String bankName =afBankDao.getBankNameByCode(bankCode);
                                if(StringUtil.isNotBlank(bankName)){
                                    addMsg = "{"+ bankName + cardNum + "}";
                                    errorWarnMsg = addMsg + errorWarnMsg;
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("get cardName error for:" + e);
                    }

                    afRepaymentBorrowCashService.dealRepaymentFail(outTradeNo, tradeNo, true, errorWarnMsg);
                } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(merPriv)) {


                    if (StringUtil.isNotBlank(respDesc)) {
                        errorWarnMsg = StringUtil.processRepayFailThirdMsg(respDesc);
                    } else {
                        errorWarnMsg = StringUtil.processRepayFailThirdMsg(tradeDesc);
                    }
                    afRenewalDetailService.dealRenewalFail(outTradeNo, tradeNo,errorWarnMsg);
                } else if (UserAccountLogType.REPAYMENT.getCode().equals(merPriv)) { // 分期还款失败	311
                    afRepaymentService.dealRepaymentFail(outTradeNo, tradeNo);
                    //加上银行卡信息，发送短信
                    try{
                        if (StringUtil.isNotBlank(respDesc)) {
                            errorWarnMsg = StringUtil.processRepayFailThirdMsg(respDesc);
                        } else {
                            errorWarnMsg = StringUtil.processRepayFailThirdMsg(tradeDesc);
                        }
                        String addMsg = "";
                        String bankCode = StringUtil.null2Str(request.getParameter("bankCode"));
                        String cardNum = StringUtil.null2Str(request.getParameter("cardNo"));
                        String userNo = StringUtil.null2Str(request.getParameter("userNo"));
                        logger.info("发送短信增加银行卡信息,bankCode:"+bankCode+",cardNum:"+cardNum+",userNo"+userNo);
                        if(StringUtil.isNotBlank(errorWarnMsg)&&StringUtil.isNotBlank(bankCode)&&StringUtil.isNotBlank(cardNum)&&StringUtil.isNotBlank(userNo)){
                            if(cardNum.length()>4){
                                cardNum = cardNum.substring(cardNum.length()- 4,cardNum.length());
                                String bankName =afBankDao.getBankNameByCode(bankCode);
                                if(StringUtil.isNotBlank(bankName)){
                                    addMsg = "{"+ bankName + cardNum + "}";
                                    errorWarnMsg = addMsg + errorWarnMsg;
                                    //还款失败短信通知
                                    afRepaymentService.sendFailMessage(Long.valueOf(userNo), errorWarnMsg, outTradeNo, tradeNo);
                                }
                            }
                        }
                    }catch (Exception e){
                        logger.error("get cardName error for:" + e);
                    }
                } else if (OrderType.BOLUOME.getCode().equals(merPriv) || OrderType.SELFSUPPORT.getCode().equals(merPriv)) {
                    int result= afOrderService.dealBrandOrderFail(outTradeNo, tradeNo, PayType.BANK.getCode());
                    if (result <= 0) {
                        return "ERROR";
                    }
                } else if (OrderType.BOLUOMECP.getCode().equals(merPriv) || OrderType.SELFSUPPORTCP.getCode().equals(merPriv) || OrderType.AGENTCPBUY.getCode().equals(merPriv)) {
                    int result= afOrderService.dealBrandPayCpOrderFail(outTradeNo, tradeNo, PayType.COMBINATION_PAY.getCode());
                    if (result <= 0) {
                        return "ERROR";
                    }
                }
            }
            return "SUCCESS";
        } catch (Exception e) {
            logger.error("collect", e);
            return "ERROR";
        }
    }


    /**
     * 易宝订单回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/yibaoback"})
    @ResponseBody
    public String YiBaoBack(HttpServletRequest request, HttpServletResponse response) {

        String responseMsg = request.getParameter("response");
        thirdLog.info("yibaoresonseMsg = " + responseMsg);
        Map<String, String> result = YeepayService.callback(responseMsg);

        String parentMerchantNo = formatString(result.get("parentMerchantNo"));
        String merchantNo = formatString(result.get("merchantNo"));
        String orderId = formatString(result.get("orderId"));
        String uniqueOrderNo = formatString(result.get("uniqueOrderNo"));
        String status = formatString(result.get("status"));
        String orderAmount = formatString(result.get("orderAmount"));
        String payAmount = formatString(result.get("payAmount"));
        String requestDate = formatString(result.get("requestDate"));
        String paySuccessDate = formatString(result.get("paySuccessDate"));

        thirdLog.info("yibaoresonseMsg = " + JSON.toJSONString(result));

        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(orderId);
        if (afYibaoOrderDo == null) {
            thirdLog.info("yibaoresonseMsg_NoMatch = " + orderId);
            return "SUCCESS";
        }
        thirdLog.info("yibaoresonseMsg_Match = " + JSON.toJSONString(afYibaoOrderDo));

        String attach = afYibaoOrderDo.getPayType();


        if (status.toLowerCase().equals("timeout") || status.toLowerCase().equals("closed")) {
            thirdLog.info("yibaoresonse fail: " + "status=" + status + ",orderNo=" + orderId);

            if (PayOrderSource.REPAYMENTCASH.getCode().equals(attach)) {
                afRepaymentBorrowCashService.dealRepaymentFail(orderId, uniqueOrderNo, false, "");
            } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(attach)) {
                afRenewalDetailService.dealRenewalFail(orderId, uniqueOrderNo,"");
            } else if (PayOrderSource.BRAND_ORDER.getCode().equals(attach) || PayOrderSource.SELFSUPPORT_ORDER.getCode().equals(attach)) {
                afOrderService.dealBrandOrderFail(orderId, uniqueOrderNo, PayType.WECHAT.getCode());
            } else if (PayOrderSource.REPAYMENT.getCode().equals(attach)) {
                afRepaymentService.dealRepaymentFail(orderId, uniqueOrderNo);
            }

            return "SUCCESS";
        }


        if (PayOrderSource.ORDER.getCode().equals(attach)) {
            afOrderService.dealMobileChargeOrder(orderId, uniqueOrderNo);
        } else if (PayOrderSource.REPAYMENT.getCode().equals(attach)) {
            afRepaymentService.dealRepaymentSucess(orderId, uniqueOrderNo);
        } else if (PayOrderSource.BRAND_ORDER.getCode().equals(attach) || PayOrderSource.SELFSUPPORT_ORDER.getCode().equals(attach)) {
            afOrderService.dealBrandOrderSucc(orderId, uniqueOrderNo, PayType.WECHAT.getCode());
        } else if (PayOrderSource.REPAYMENTCASH.getCode().equals(attach)) {
            afRepaymentBorrowCashService.dealRepaymentSucess(orderId, uniqueOrderNo);
        } else if (PayOrderSource.RENEWAL_PAY.getCode().equals(attach)) {
            afRenewalDetailService.dealRenewalSucess(orderId, uniqueOrderNo);
        }
        return "SUCCESS";
    }

    private String formatString(String text) {
        return text == null ? "" : text.trim();
    }

    /**
     * 易宝清算回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/yibaoqsback"})
    @ResponseBody
    public String YiBaoQsBack(HttpServletRequest request, HttpServletResponse response) {
        return "SUCCESS";
    }


    /**
     * 处理易宝业务逻辑
     */
    @RequestMapping(value = {"/yibaoupdate"})
    @ResponseBody
    public String YiBaoUpdate() {
        try {
            final String key = "getyiBao_success_repayCash";
            long count = redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);
            if (count != 1) {
                return "error";
            }
            //Map a = yiBaoUtility.getYiBaoOrder("hq2017091523034389983", "1001201709150000000017453099");
            thirdLog.info("YiBaoUpdate start ");
            yiBaoUtility.updateYiBaoAllNotCheck();
            thirdLog.info("YiBaoUpdate end ");
            redisTemplate.delete(key);
            return "success";
        } catch (Exception e) {
            logger.error("yibaoUpdate error", e);
            return e.toString();
        }
    }


    /**
     * 给admin 端调用生成帐单和代款数据。不写入数据库
     */
    @RequestMapping(value = {"/addHomeBorrow"})
    @ResponseBody
    public HashMap addHomeBorrow(final Long orderId, final int nper, final Long userId, long timer) throws Exception {
        try {
            logger.info("addHomeBorrow params:orderId=" + orderId + ",nper=" + nper + ",userId=" + userId + ",timer=" + timer);

            Date date = new Date(timer);
            HashMap ret = afBorrowService.addHomeBorrow(orderId, nper, userId, date);
            return ret;
        } catch (Exception e) {
            HashMap ret = new HashMap();
            logger.error("addHomeBorrow error:" + e);
            return ret;
        }

    }


    /**
     * 处理错误数据
     */
    @RequestMapping(value = {"/updateOrderLen"})
    @ResponseBody
    public String updateOrderLen() {
        try {
            final String key = "getyiBao_success_repayCash111";
            long count = redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);
            if (count != 1) {
                return "error";
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2017);
            cal.set(Calendar.MONTH, 8);
            cal.set(Calendar.DAY_OF_MONTH, 19);
            cal.set(Calendar.HOUR_OF_DAY, 13);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            Calendar cal1 = Calendar.getInstance();
            cal1.set(Calendar.YEAR, 2017);
            cal1.set(Calendar.MONTH, 8);
            cal1.set(Calendar.DAY_OF_MONTH, 20);
            cal1.set(Calendar.HOUR_OF_DAY, 15);
            cal1.set(Calendar.MINUTE, 0);
            cal1.set(Calendar.SECOND, 0);


            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = cal.getTime();//new Date(2017,8,19,17,00,00);
            Date end = cal1.getTime();//new Date(2017,8,20,17,00,00);
            List<AfOrderDo> list = afOrderDao.getOrderByTimeAndType(start, end);
            for (AfOrderDo afOrderDo : list) {
                if (afOrderDo.getPayType().equals("CP")) {
                    if (afOrderDo.getPayStatus().toLowerCase().equals("p")) {
                        addBorrowBill(afOrderDo, PayType.AGENT_PAY.getCode());
                    } else if (afOrderDo.getPayType().toLowerCase().equals("d")) {
                        addBorrowBill(afOrderDo, PayType.COMBINATION_PAY.getCode());
                    }
                } else if (afOrderDo.getPayType().equals(PayType.AGENT_PAY.getCode())) {
                    if (afOrderDo.getPayStatus().toLowerCase().equals("p")) {
                        addBorrowBill(afOrderDo, PayType.AGENT_PAY.getCode());
                    }
                }
            }
            thirdLog.info("updateOrderLen start ");
            //yiBaoUtility.updateYiBaoAllNotCheck();


            thirdLog.info("updateOrderLen end ");
            redisTemplate.delete(key);
            return "success";
        } catch (Exception e) {
            logger.error("updateOrderLen error", e);
            return e.toString();
        }
    }


    @Resource
    AfBorrowBillService afBorrowBillService;

    private void addBorrowBill(AfOrderDo afOrderDo, String payType) {
        AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(afOrderDo.getRid());
        if (afBorrowDo != null) {
            //查询是否己产生
            List<AfBorrowBillDo> borrowList = afBorrowBillService.getAllBorrowBillByBorrowId(afBorrowDo.getRid());
            if (borrowList == null || borrowList.size() == 0) {
                List<AfBorrowBillDo> billList = afBorrowService.buildBorrowBillForNewInterest(afBorrowDo, payType);
                afBorrowService.addBorrowBill(billList);
            }
        }
    }

}
