package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.bo.AfTradeRebateModelBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 沈铖 2017/7/17 上午10:56
 * @类描述: 创建商圈订单
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tradeOrderApi")
public class TradeOrderApi implements ApiHandle {
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTradeOrderService afTradeOrderService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;
    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String businessName = ObjectUtils.toString(requestDataVo.getParams().get("businessName"));
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
        BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"), BigDecimal.ZERO);
        boolean fromCashier = NumberUtil.objToIntDefault(request.getAttribute("fromCashier"), 0) == 0 ? false : true;
        if (fromCashier) {
            nper = 0;
        }
        Date currTime = new Date();
        Date gmtPayEnd = DateUtil.addHoures(currTime, Constants.ORDER_PAY_TIME_LIMIT);

        if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
        }

        AfOrderDo afOrder = new AfOrderDo();
        afOrder.setUserId(userId);
        afOrder.setPriceAmount(actualAmount);
        afOrder.setSaleAmount(actualAmount);
        afOrder.setActualAmount(actualAmount);
        afOrder.setOrderNo(generatorClusterNo.getOrderNo(OrderType.TRADE));
        afOrder.setOrderType(OrderType.TRADE.getCode());
        afOrder.setPayType(PayType.AGENT_PAY.getCode());
        afOrder.setShopName(businessName);
        afOrder.setGmtPayEnd(gmtPayEnd);
        afOrder.setNper(nper);
        AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(businessId);
        String code = afTradeBusinessInfoService.getCodeById(afTradeBusinessInfoDo.getType());
        afOrder.setSecType(code);
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
        if (!fromCashier && nper.intValue() > 0) {
            // 保存手续费信息
            BorrowRateBo borrowRate = afResourceService.borrowRateWithResourceForTrade(nper);
            afOrder.setInterestFreeJson(JSON.toJSONString(rebateModels));
            afOrder.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
        }

        //计算返利信息

        if (rebateModel.getRebatePercent() != null && rebateModel.getRebatePercent().compareTo(BigDecimal.ZERO) > 0 && afTradeBusinessInfoDo.getRebateMax().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rebateAmount = afOrder.getActualAmount().multiply(rebateModel.getRebatePercent()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            rebateAmount = rebateAmount.compareTo(afTradeBusinessInfoDo.getRebateMax()) < 0 ? rebateAmount : afTradeBusinessInfoDo.getRebateMax();
            afOrder.setRebateAmount(rebateAmount);
        }
        //下单时所有场景额度使用情况
        List<AfOrderSceneAmountDo> listSceneAmount = new ArrayList<AfOrderSceneAmountDo>();
        //线上使用情况
        AfOrderSceneAmountDo onlineSceneAmount = new AfOrderSceneAmountDo();
        //培训使用情况
        AfOrderSceneAmountDo trainSceneAmount = new AfOrderSceneAmountDo();
        //获取所有场景额度
        List<AfUserAccountSenceDo> list = afUserAccountSenceService.getByUserId(userId);
        //当前场景额度
        AfUserAccountSenceDo afUserAccountSenceDo = null;

        for (AfUserAccountSenceDo item:list){
            if(item.getScene().equals(UserAccountSceneType.ONLINE.getCode())){
                onlineSceneAmount.setAuAmount(item.getAuAmount());
                onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
                onlineSceneAmount.setUsedAmount(item.getUsedAmount());
                onlineSceneAmount.setUserId(userId);
            }
            if(item.getScene().equals(UserAccountSceneType.TRAIN.getCode())){
                afUserAccountSenceDo = item;
                trainSceneAmount.setAuAmount(item.getAuAmount());
                trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
                trainSceneAmount.setUsedAmount(item.getUsedAmount());
                trainSceneAmount.setUserId(userId);
            }
        }
        if(afUserAccountSenceDo == null){
            afUserAccountSenceDo = new AfUserAccountSenceDo();
            afUserAccountSenceDo.setAuAmount(new BigDecimal(0));
            afUserAccountSenceDo.setFreezeAmount(new BigDecimal(0));
            afUserAccountSenceDo.setUsedAmount(new BigDecimal(0));
        }
        BigDecimal useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount()).subtract(afUserAccountSenceDo.getFreezeAmount());
        afOrder.setAuAmount(afUserAccountSenceDo.getAuAmount());
		afOrder.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
		//新增下单时记录 IP 、设备指纹 2017年12月12日13:21:39 cxk
		afOrder.setIp(CommonUtil.getIpAddr(request));//用户ip地址
		afOrder.setBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("blackBox")));//加入同盾设备指纹
        afOrder.setBqsBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox")));//加入白骑士设备指纹
        afOrderService.createOrder(afOrder);
        //获取现金贷额度
        AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
        //现金贷使用情况
        AfOrderSceneAmountDo cashSceneAmount = new AfOrderSceneAmountDo();
        cashSceneAmount.setOrderId(afOrder.getRid());
        cashSceneAmount.setAuAmount(userAccountInfo.getAuAmount());
        cashSceneAmount.setScene(UserAccountSceneType.CASH.getCode());
        cashSceneAmount.setUsedAmount(userAccountInfo.getUsedAmount());
        cashSceneAmount.setUserId(userId);
        if(onlineSceneAmount.getUserId() == null) {
            onlineSceneAmount.setAuAmount(new BigDecimal(0));
            onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
            onlineSceneAmount.setUsedAmount(new BigDecimal(0));
            onlineSceneAmount.setUserId(userId);
        }
        if(trainSceneAmount.getUserId() == null) {
            trainSceneAmount.setAuAmount(new BigDecimal(0));
            trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
            trainSceneAmount.setUsedAmount(new BigDecimal(0));
            trainSceneAmount.setUserId(userId);
        }
        onlineSceneAmount.setOrderId(afOrder.getRid());
        trainSceneAmount.setOrderId(afOrder.getRid());
        listSceneAmount.add(cashSceneAmount);
        listSceneAmount.add(onlineSceneAmount);
        listSceneAmount.add(trainSceneAmount);
        //添加下单时所有场景额度使用情况
        afOrderService.addSceneAmount(listSceneAmount);

        AfTradeOrderDo afTradeOrderDo = new AfTradeOrderDo();
        afTradeOrderDo.setOrderId(afOrder.getRid());
        afTradeOrderDo.setBusinessId(businessId);
        afTradeOrderDo.setBalanceAmount(actualAmount);
        afTradeOrderService.saveRecord(afTradeOrderDo);
        String isEnoughAmount = "Y";
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(afOrder.getRid()));
        if (!fromCashier) {
             if (useableAmount.compareTo(actualAmount) < 0) {
                isEnoughAmount = "N";
            }

        }
        data.put("isEnoughAmount", isEnoughAmount);
        resp.setResponseData(data);
        return resp;
    }
}
