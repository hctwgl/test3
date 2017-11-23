package com.ald.fanbei.api.web.api.checkoutcounter;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.api.agencybuy.SubmitAgencyBuyOrderApi;
import com.ald.fanbei.api.web.api.order.BuySelfGoodsApi;
import com.ald.fanbei.api.web.api.order.TradeOrderApi;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 收营台前置步骤
 * 1.生成订单
 * 2.跳转到收银台
 * 为了规避风险一期仅做分发处理
 */
@Component("getNperListApi")
public class GetNperListApi implements ApiHandle {

    @Resource
    AfTradeOrderService afTradeOrderService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfGoodsService afGoodsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> params = requestDataVo.getParams();
        Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
        String orderType = ObjectUtils.toString(params.get("orderType"), "");
        BigDecimal nperAmount = NumberUtil.objToBigDecimalDefault(params.get("nperAmount"), BigDecimal.ZERO);
        AfOrderDo orderInfo = afOrderService.getOrderById(orderId);


        if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
            AfTradeOrderDo tradeOrderDo = afTradeOrderService.getById(orderInfo.getRid());
            AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(tradeOrderDo.getBusinessId());

            //region 没有配置就采用默认值
            JSONArray rebateModels = new JSONArray();
            //#endregion
            String configRebateModel = afTradeBusinessInfoDo.getConfigRebateModel();
            if (StringUtils.isNotBlank(configRebateModel)) {
                try {
                    rebateModels = JSON.parseArray(configRebateModel);
                } catch (Exception e) {
                    logger.info("GetTradeNperInfoApi process error", e.getCause());
                }

            }
            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_TRADE);
            JSONArray array = JSON.parseArray(resource.getValue());

            //分期金额限制
            String oneNper = checkMoneyLimit(array,orderInfo.getOrderType(),nperAmount);

            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, rebateModels, BigDecimal.ONE.intValue(),
                    nperAmount, resource.getValue1(), resource.getValue2());
            resp.addResponseData("nperList", nperList);
            resp.addResponseData("oneFlag", oneNper);
            return resp;
        } else {
            JSONArray interestFreeArray = null;
            if(orderInfo.getOrderType().equals(OrderType.SELFSUPPORT.getCode())){
                String numId = orderInfo.getGoodsId() + "";
                interestFreeArray = getInterestFreeArray(numId, orderType);
            }else{
                if (orderInfo.getGoodsId()!=0&&orderInfo.getGoodsId() != null&&!orderInfo.getGoodsId().equals("")) {
                    String numId = orderInfo.getGoodsId() + "";
                    interestFreeArray = getInterestFreeArray(numId, orderType);
                }else if (!StringUtil.isEmpty(orderInfo.getNumId()) ) {
                    String numId = orderInfo.getNumId() + "";
                    interestFreeArray = getInterestFreeArray(numId, orderType);
                }
            }

            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            //删除2分期
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            removeSecondNper(array);

            //分期金额限制
            String oneNper = checkMoneyLimit(array,orderInfo.getOrderType(),nperAmount);

            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    nperAmount.compareTo(BigDecimal.ZERO) == 0 ? orderInfo.getActualAmount() : nperAmount, resource.getValue1(), resource.getValue2());
            resp.addResponseData("nperList", nperList);
            resp.addResponseData("oneFlag", oneNper);
            return resp;
        }


    }

    private JSONArray getInterestFreeArray(String numId, String type) {
        JSONArray interestFreeArray = null;
        if (StringUtils.isBlank(numId)) {
            return null;
        }
        Long goodsId = 0L;
        if (StringUtils.equals(type, OrderType.TAOBAO.getCode())||StringUtils.equals(type, OrderType.AGENTBUY.getCode())) {
            //获取商品信息
            AfGoodsDo afGoodsDo = afGoodsService.getGoodsByNumId(numId);
            if (null == afGoodsDo) {
                return null;
            }
            goodsId = afGoodsDo.getRid();
        } else {
            goodsId = NumberUtil.objToLongDefault(numId, 0);
        }
        //通过商品查询免息规则配置
        AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
        if (null == afSchemeGoodsDo) {
            return null;
        }
        Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
        AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
        if (null != afInterestFreeRulesDo && StringUtils.isNotBlank(afInterestFreeRulesDo.getRuleJson())) {
            interestFreeArray = JSON.parseArray(afInterestFreeRulesDo.getRuleJson());
        }
        return interestFreeArray;
    }

    private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
                it.remove();
                break;
            }
        }

    }
    private String checkMoneyLimit(JSONArray array,String orderType,BigDecimal totalamount) {
        String oneNper = "1";
        try{
            String cType = "";
            if(OrderType.AGENTBUY.getCode().equals(orderType)|| OrderType.SELFBUILD.equals(orderType)){
                cType = ResourceType.STAGE_MONEY_LIMIT_D.getCode();
            }else if (OrderType.SELFSUPPORT.getCode().equals(orderType)){
                cType = ResourceType.STAGE_MONEY_LIMIT_Z.getCode();
            }else if (OrderType.BOLUOME.getCode().equals(orderType)){
                cType = ResourceType.STAGE_MONEY_LIMIT_B.getCode();
            }else if (OrderType.TRADE.getCode().equals(orderType)){
                cType = ResourceType.STAGE_MONEY_LIMIT_S.getCode();
            }
            if (!"".equals(cType)){
                List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes(cType);
                if (array != null&&afResourceList !=  null&& afResourceList.size()>0) {
                    AfResourceDo afdao = afResourceList.get(0);
                    String value = afdao.getValue();
                    Map<String,JSONObject> config = new HashMap<String,JSONObject>();
                    if (!StringUtils.isBlank(value)){
                        JSONArray arraytemp = JSON.parseArray(value);
                        for (int i=0;i<array.size();i++){
                            JSONObject obj = array.getJSONObject(i);
                            config.put(obj.getString("stage").replace("s",""),obj);
                            if(obj.getString("stage").equals("s1")){
                                String up = obj.getString("stageUp");
                                String down = obj.getString("stageDown");
                                if("0".equals(up) && "0".equals(down)){
                                    continue;
                                }
                                if (new BigDecimal(down).compareTo(totalamount)>0) {
                                    oneNper = "0";
                                }
                            }
                        }
                    }
                    Iterator<Object> it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject json = (JSONObject) it.next();
                        String nper = json.getString(Constants.DEFAULT_NPER);
                        if(config.containsKey(nper)){
                            JSONObject objtemp = config.get(nper);
                            String up = objtemp.getString("stageUp");
                            String down = objtemp.getString("stageDown");
                            if("0".equals(up) && "0".equals(down)){
                                continue;
                            }
                            if (new BigDecimal(down).compareTo(totalamount)>0) {
                                it.remove();
                            }
                        }

                    }
                }
            }
        }catch (Exception e){
            logger.error("分期金额限制过滤失败："+e);
        }
        return oneNper;
    }
}
