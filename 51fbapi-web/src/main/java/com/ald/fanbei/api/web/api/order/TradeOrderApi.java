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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
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
        afOrderService.createOrder(afOrder);

        AfTradeOrderDo afTradeOrderDo = new AfTradeOrderDo();
        afTradeOrderDo.setOrderId(afOrder.getRid());
        afTradeOrderDo.setBusinessId(businessId);
        afTradeOrderDo.setBalanceAmount(actualAmount);
        afTradeOrderService.saveRecord(afTradeOrderDo);




        String isEnoughAmount = "Y";
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(afOrder.getRid()));
        if (!fromCashier) {
            AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
            BigDecimal useableAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount()).subtract(userAccountInfo.getFreezeAmount());
            if (useableAmount.compareTo(actualAmount) < 0) {
                isEnoughAmount = "N";
            }

        }
        data.put("isEnoughAmount", isEnoughAmount);
        resp.setResponseData(data);
        return resp;
    }
}
