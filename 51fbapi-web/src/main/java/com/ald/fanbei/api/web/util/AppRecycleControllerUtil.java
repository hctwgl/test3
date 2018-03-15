package com.ald.fanbei.api.web.util;

import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @describe: 有得卖三方 辅助类
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
public class AppRecycleControllerUtil {

    /**
     * 数据封装
     * @param request
     * @return
     */
    public static AfRecycleQuery buildParam(HttpServletRequest request){
        String partnerId = ObjectUtils.toString(request.getParameter("partnerId"), null);
        String orderId = ObjectUtils.toString(request.getParameter("orderId"), null);
        String url = ObjectUtils.toString(request.getParameter("url"), null);
        BigDecimal settlePrice = NumberUtil.objToBigDecimalZeroToDefault(request.getParameter("settlePrice"), null);
        Long userId = NumberUtil.objToLongDefault(request.getParameter("userId"), null);
        Integer status = NumberUtil.objToIntDefault(request.getParameter("status"),null);
        Integer payType = NumberUtil.objToIntDefault(request.getParameter("payType"),null);
        AfRecycleQuery afRecycleQuery = new AfRecycleQuery();
        afRecycleQuery.setPartnerId(partnerId);
        afRecycleQuery.setRefOrderId(orderId);
        afRecycleQuery.setSettlePrice(settlePrice);
        afRecycleQuery.setUserId(userId);
        afRecycleQuery.setStatus(status);
        afRecycleQuery.setPayType(payType);
        afRecycleQuery.setUrl(url);
        return afRecycleQuery;
    }



}
