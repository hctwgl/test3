package com.ald.fanbei.api.web.api.brand;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.bo.AfTradeRebateModelBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.IPTransferBo;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.service.wxpay.WxpayConfig;
import com.ald.fanbei.api.biz.third.util.IPTransferUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author xiaotianjian 2017年3月27日上午10:53:43
 * @类描述：品牌订单进行支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("payOrderV1Api")
public class PayOrderV1Api implements ApiHandle {

    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    private AfOrderService afOrderService;
    @Resource
    private AfUserAccountService afUserAccountService;
    @Resource
    private AfUserBankcardService afUserBankcardService;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    RiskUtil riskUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    IPTransferUtil iPTransferUtil;
    @Resource
    UpsUtil upsUtil;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;
    @Autowired
    AfDeUserGoodsService afDeUserGoodsService;
    @Autowired
    AfTradeOrderService afTradeOrderService;
    @Resource
    AfShareUserGoodsService afShareUserGoodsService;
    @Resource
    AfShareGoodsService afShareGoodsService;
    @Resource
	AfGoodsDouble12Service afGoodsDouble12Service;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
        Long payId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("payId"), null);
        Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), null);
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"), OrderType.BOLUOME.getCode()).toString();
        BigDecimal lat = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("lat"), null);
        BigDecimal lng = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("lng"), null);
        boolean fromCashier = true; //NumberUtil.objToIntDefault(request.getAttribute("fromCashier"), 0) == 0 ? false : true;
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        String isCombinationPay = ObjectUtils.toString(requestDataVo.getParams().get("isCombinationPay"), "").toString();

        if (orderId == null || payId == null) {
            logger.error("orderId is empty or payId is empty");
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        // TODO获取用户订单
        AfOrderDo orderInfo = afOrderService.getOrderById(orderId);

        if (orderInfo == null) {
            logger.error("orderId is invalid");
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }

        if (orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_PAY_DEALING);
        }
        if (orderInfo == null) {
            logger.error("orderId is invalid");
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }

        //双十一砍价添加
        if (OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType()) && StringUtils.isNotBlank(orderInfo.getThirdOrderNo())) {
            AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getById(Long.parseLong(orderInfo.getThirdOrderNo()));
            if (afDeUserGoodsDo != null && afDeUserGoodsDo.getIsbuy() == 1) {
                logger.error(orderInfo.getThirdOrderNo() + ":afDeUserGoodsService the goods is buy.");
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CUT_PRICE_ISBUY);
            }
        }
        //----------------

        // mqp_新人专享活动增加逻辑
        /*if (OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())
                && StringUtils.isNotBlank(orderInfo.getThirdOrderNo())) {
            AfShareUserGoodsDo shareUserGoodsDo = afShareUserGoodsService
                    .getById(Long.parseLong(orderInfo.getThirdOrderNo()));
            if(shareUserGoodsDo!= null)
            {
                        AfShareUserGoodsDo result = afShareUserGoodsService
                                .getByUserId(shareUserGoodsDo.getUserId());
            
                        if (result != null && result.getIsBuy() == 1) {
                            logger.error(orderInfo.getThirdOrderNo() + ":afShareUserGoodsService the goods is buy.");
                            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SHARE_PRICE_BOUGHT);
                        }
            }
        }*/

        if (OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType()) 
        		&& afShareGoodsService.getCountByGoodsId(orderInfo.getGoodsId())!=0){
        	
    		if(afOrderService.getOverOrderByUserId(userId).size() >0){

        		logger.error(orderInfo.getThirdOrderNo() + ":afShareUserGoodsService the goods is buy.");
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SHARE_PRICE_BOUGHT);
    		}
        }
        
        // ----------------

        // 双十二秒杀新增逻辑+++++++++++++>
		double12GoodsCheck(userId, orderInfo.getGoodsId());
		// +++++++++++++++++++++++++<
        
        if (orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_PAY_DEALING);
        }

        if (orderInfo.getStatus().equals(OrderStatus.PAID.getCode())) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_HAS_PAID);
        }

        if (orderInfo.getStatus().equals(OrderStatus.CLOSED.getCode())) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_HAS_CLOSED);
        }
        //region 支付方式在这里处理
        if (fromCashier && nper != null) {
            orderInfo.setNper(nper);
            if (nper == 1) {
                //信用支付
                BorrowRateBo borrowRate = afResourceService.borrowRateWithResourceCredit(nper);
                orderInfo.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
            } else {
                //分期支付
                BorrowRateBo borrowRate = null;
                if (OrderType.TRADE.getCode().equals(orderInfo.getOrderType())) {
                    AfTradeOrderDo tradeOrderDo = afTradeOrderService.getById(orderInfo.getRid());
                    AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(tradeOrderDo.getBusinessId());
                    String configRebateModel = afTradeBusinessInfoDo.getConfigRebateModel();
                    //region 没有配置就采用默认值
                    AfTradeRebateModelBo rebateModel = null;

                    //#endregion
                    if (StringUtils.isNotBlank(configRebateModel)) {
                        List<AfTradeRebateModelBo> rebateModels = JSON.parseArray(configRebateModel, AfTradeRebateModelBo.class);
                        for (AfTradeRebateModelBo item : rebateModels) {
                            if (item.getNper() == nper) {
                                rebateModel = item;
                            }
                        }
                    }
                    if (rebateModel == null) {
                        rebateModel = new AfTradeRebateModelBo();
                        rebateModel.setFreeNper(0);
                        rebateModel.setNper(nper);
                        rebateModel.setRebatePercent(BigDecimal.ZERO);
                    }
                    //region 没有配置就采用默认值
                    JSONArray rebateModels = new JSONArray();
                    //#endregion
                    if (StringUtils.isNotBlank(configRebateModel)) {
                        try {
                            rebateModels = JSON.parseArray(configRebateModel);
                        } catch (Exception e) {
                            logger.info("GetTradeNperInfoApi process error", e.getCause());
                        }

                    }
                    borrowRate = afResourceService.borrowRateWithResourceForTrade(nper);
                    orderInfo.setInterestFreeJson(JSON.toJSONString(rebateModels));
                    orderInfo.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
                    if (rebateModel.getRebatePercent() != null && rebateModel.getRebatePercent().compareTo(BigDecimal.ZERO) > 0 && afTradeBusinessInfoDo.getRebateMax().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal rebateAmount = orderInfo.getActualAmount().multiply(rebateModel.getRebatePercent()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                        rebateAmount = rebateAmount.compareTo(afTradeBusinessInfoDo.getRebateMax()) < 0 ? rebateAmount : afTradeBusinessInfoDo.getRebateMax();
                        orderInfo.setRebateAmount(rebateAmount);
                    }
                } else {
                    borrowRate = afResourceService.borrowRateWithResource(nper);
                    orderInfo.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
                }

            }

        }

        //endregion
        AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
        if (payId >= 0) {
            String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
            if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
                //自营或代买订单记录支付失败原因
                if (OrderType.getNeedRecordPayFailCodes().contains(orderInfo.getOrderType())) {
                    AfOrderDo currUpdateOrder = new AfOrderDo();
                    currUpdateOrder.setRid(orderInfo.getRid());
                    currUpdateOrder.setPayStatus(PayStatus.NOTPAY.getCode());
                    currUpdateOrder.setStatus(OrderStatus.PAYFAIL.getCode());
                    //支付失败
                    //boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_FAIL, orderInfo.getUserId(), orderInfo.getActualAmount());
                    currUpdateOrder.setStatusRemark(Constants.PAY_ORDER_PASSWORD_ERROR);
                    afOrderService.updateOrder(currUpdateOrder);
                }
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }
        }

        //原来的微信支付号，以及原来的微信支付类型
        String wxPayOrderNo = orderInfo.getPayTradeNo();
        String wxPayType = orderInfo.getPayType();
        //用来判断之前的支付方式是否为微信支付
        if (wxPayType.equals(PayType.WECHAT.getCode())) {
            //如果查询出来，在支付，或者已经支付，则抛出正在处理中的状态
            Map<String, String> wxResultMap = upsUtil.wxQueryOrder(wxPayOrderNo);
            String resultCode = wxResultMap.get(WxpayConfig.RESULT_CODE);
            String tradeCode = wxResultMap.get(WxpayConfig.TRADE_STATE);
            if (WxpayConfig.RESULT_CODE_SUCCESS.equals(resultCode)
                    && (WxpayConfig.TRADE_STATE_SUCCESS.equals(tradeCode)
                    || WxpayConfig.TRADE_STATE_USERPAYING.equals(tradeCode))) {
                throw new FanbeiException(FanbeiExceptionCode.ORDER_PAY_DEALING);
            }
        }

        String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
        String ipAddress = CommonUtil.getIpAddr(request);
        if (lat == null || lng == null) {
            IPTransferBo bo = iPTransferUtil.parseIpToLatAndLng(ipAddress);
            orderInfo.setLat(bo.getLatitude());
            orderInfo.setLng(bo.getLongitude());
        } else {
            orderInfo.setLat(lat);
            orderInfo.setLng(lng);
        }
        afOrderService.updateOrder(orderInfo);

        try {
            BigDecimal saleAmount = orderInfo.getSaleAmount();
            if (StringUtils.equals(type, OrderType.AGENTBUY.getCode()) || StringUtils.equals(type, OrderType.SELFSUPPORT.getCode()) || StringUtils.equals(type, OrderType.TRADE.getCode())) {
                saleAmount = orderInfo.getActualAmount();
            }
            if (payId == 0 && (StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode()) || StringUtils.equals(orderInfo.getOrderType(), OrderType.TRADE.getCode()) || nper == null)) {
                nper = orderInfo.getNper();
            }

            String payType = PayType.AGENT_PAY.getCode();
            //代付
            if (payId < 0) {
                payType = PayType.WECHAT.getCode();
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.WEBCHAT_NOT_USERD);


            } else if (payId > 0) {
                payType = PayType.BANK.getCode();
                //银行卡
            }

            if (StringUtil.equals(YesNoStatus.YES.getCode(), isCombinationPay)) {
                payType = PayType.COMBINATION_PAY.getCode();
                //组合
            }


            // ----------------

            // mqp_新人专享活动增加逻辑
            if (OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())
                    && StringUtils.isNotBlank(orderInfo.getThirdOrderNo())) {
                AfShareUserGoodsDo shareUserGoodsDo = afShareUserGoodsService
                        .getById(Long.parseLong(orderInfo.getThirdOrderNo()));
                if (shareUserGoodsDo != null && !payType.equals(PayType.AGENT_PAY.getCode())) {
                    logger.error(orderInfo.getThirdOrderNo() + ":afShareUserGoodsService the payType is error.");
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SHARE_PAYTYPE_ERROR);
                }
            }

            // ----------------


            Map<String, Object> result = afOrderService.payBrandOrder(payId, payType, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(), saleAmount, nper, appName, ipAddress);

            Object success = result.get("success");
            Object payStatus = result.get("status");
            if (success != null) {
                if (Boolean.parseBoolean(success.toString())) {
                    //判断是否菠萝觅，如果是菠萝觅,额度支付成功，则推送成功消息，银行卡支付,则推送支付中消息
                    if (StringUtils.equals(type, OrderType.BOLUOME.getCode())) {
                        if (payId.intValue() == 0) {
                            riskUtil.payOrderChangeAmount(orderInfo.getRid());
                        } else if (payId > 0 && PayStatus.DEALING.getCode().equals(payStatus.toString())) {
                            boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_DEALING, orderInfo.getUserId(), orderInfo.getActualAmount());
                        }
                    }

                    // 更新砍价商品为已购买(订单为自营且第三方订单号不为空),双十一添加
                    //mqp_新人专享活动增加逻辑  ---->update
                    if (OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())
                            && StringUtils.isNotBlank(orderInfo.getThirdOrderNo())) {
                        afDeUserGoodsService.updateIsBuyById(Long.parseLong(orderInfo.getThirdOrderNo()), 1);
                        afShareUserGoodsService.updateIsBuyById(Long.parseLong(orderInfo.getThirdOrderNo()), 1);
                    }
                    
                } else {
                    FanbeiExceptionCode errorCode = (FanbeiExceptionCode) result.get("errorCode");
                    ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(), errorCode);
                    response.setResponseData(result);
                    return response;
                }
            }
            resp.setResponseData(result);

        } catch (FanbeiException exception) {
            return new ApiHandleResponse(requestDataVo.getId(), exception.getErrorCode());
        } catch (Exception e) {
            logger.error("pay order failed e = {}", e);
            resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
        }
        return resp;
    }

    
    /**
	 * 
	 * @Title: double12GoodsCheck
	 * @Description:  双十二秒杀新增逻辑 —— 秒杀商品校验
	 * @return  void  
	 * @author yanghailong
	 * @data  2017年11月21日
	 */
	private void double12GoodsCheck(Long userId, Long goodsId){
		
		AfGoodsDouble12Do afGoodsDouble12Do = afGoodsDouble12Service.getByGoodsId(goodsId);
		if(null != afGoodsDouble12Do){
			//这个商品是双十二秒杀商品
			if(afOrderService.getOverOrderByGoodsIdAndUserId(goodsId, userId).size()>0){
				//报错提示已秒杀过（已生成过秒杀订单）
				throw new FanbeiException(FanbeiExceptionCode.ONLY_ONE_DOUBLE12GOODS_ACCEPTED);
			}
			
			if(afGoodsDouble12Do.getCount()<=0){
				//报错提示秒杀商品已售空
				throw new FanbeiException(FanbeiExceptionCode.NO_DOUBLE12GOODS_ACCEPTED);
			}
		}
	}
}
