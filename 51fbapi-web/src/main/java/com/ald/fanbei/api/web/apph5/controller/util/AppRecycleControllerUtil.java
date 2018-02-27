package com.ald.fanbei.api.web.apph5.controller.util;

import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import org.apache.commons.lang.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @Description: 有得卖三方 辅助类
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
public class AppRecycleControllerUtil {

    public static final String PARTNER_ID = "1136587444";//与有得卖合作id，对应我们平台的appid

    /**
     * 数据封装
     * @param request
     * @return
     */
    public static AfRecycleQuery buildParam(HttpServletRequest request){
        String partnerId = ObjectUtils.toString(request.getParameter("partnerId"), null);
        String orderId = ObjectUtils.toString(request.getParameter("orderId"), null);
        BigDecimal settlePrice = NumberUtil.objToBigDecimalZeroToDefault(request.getParameter("settlePrice"), null);
        Long uid = NumberUtil.objToLongDefault(request.getParameter("uid"), null);
        Integer status = NumberUtil.objToIntDefault(request.getParameter("status"),null);
        AfRecycleQuery afRecycleQuery = new AfRecycleQuery();
        afRecycleQuery.setPartnerId(partnerId);
        afRecycleQuery.setRefOrderId(orderId);
        afRecycleQuery.setSettlePrice(settlePrice);
        afRecycleQuery.setUid(uid);
        afRecycleQuery.setStatus(status);
        return afRecycleQuery;
    }
}
