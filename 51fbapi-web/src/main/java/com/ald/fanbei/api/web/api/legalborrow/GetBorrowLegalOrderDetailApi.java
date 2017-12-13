package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBorrowLegalOrderDeatilVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：查询用户订单记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalOrderDetailApi")
public class GetBorrowLegalOrderDetailApi implements ApiHandle {

    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> data = Maps.newHashMap();
        resp.setResponseData(data);
        Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));
        // 判断用户是否登录
        Long userId = context.getUserId();
        if (userId == null) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
        }
        AfBorrowLegalOrderDeatilVo afBorrowLegalOrderDeatilVo = new AfBorrowLegalOrderDeatilVo();
        AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderById(orderId);
        AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(orderId);
        if (afBorrowLegalOrderCashDo != null){
            afBorrowLegalOrderDeatilVo.setAmount(afBorrowLegalOrderCashDo.getAmount());
            afBorrowLegalOrderDeatilVo.setGmtPlanRepay(afBorrowLegalOrderCashDo.getGmtPlanRepay());
            afBorrowLegalOrderDeatilVo.setStatus(afBorrowLegalOrderCashDo.getStatus());
            afBorrowLegalOrderDeatilVo.setType(afBorrowLegalOrderCashDo.getType());
            afBorrowLegalOrderDeatilVo.setPoundageAmount(afBorrowLegalOrderCashDo.getPoundageAmount());
            afBorrowLegalOrderDeatilVo.setInterestAmount(afBorrowLegalOrderCashDo.getInterestAmount());
            afBorrowLegalOrderDeatilVo.setGmtPlanRepay(afBorrowLegalOrderCashDo.getGmtPlanRepay());
            afBorrowLegalOrderDeatilVo.setOverdueDay(afBorrowLegalOrderCashDo.getOverdueDay());
            afBorrowLegalOrderDeatilVo.setOverdueAmount(afBorrowLegalOrderCashDo.getOverdueAmount());
        }
        if (afBorrowLegalOrderCashDo != null){
            afBorrowLegalOrderDeatilVo.setDeliveryPhone(afBorrowLegalOrderDo.getDeliveryPhone());
            afBorrowLegalOrderDeatilVo.setDeliveryUser(afBorrowLegalOrderDo.getDeliveryUser());
            afBorrowLegalOrderDeatilVo.setAddress(afBorrowLegalOrderDo.getAddress());
            afBorrowLegalOrderDeatilVo.setGoodsName(afBorrowLegalOrderDo.getGoodsName());
            afBorrowLegalOrderDeatilVo.setPriceAmount(afBorrowLegalOrderDo.getPriceAmount());
            afBorrowLegalOrderDeatilVo.setOrderNo(afBorrowLegalOrderDo.getOrderNo());
            afBorrowLegalOrderDeatilVo.setLogisticsInfo(afBorrowLegalOrderDo.getLogisticsInfo());
            afBorrowLegalOrderDeatilVo.setGmtCreate(afBorrowLegalOrderCashDo.getGmtCreate());
            afBorrowLegalOrderDeatilVo.setGmtDeliver(afBorrowLegalOrderDo.getGmtDeliver());
        }
        data.put("afBorrowLegalOrderDeatilVo", afBorrowLegalOrderDeatilVo);
        resp.setResponseData(data);
        return resp;
    }

}
