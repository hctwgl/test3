package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeBusinessInfoDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOrderVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 沈铖 2017/7/19 下午3:36
 * @类描述: GetTradeOrderDetailInfoApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getTradeOrderDetailInfoApi")
public class GetTradeOrderDetailInfoApi implements ApiHandle {
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfOrderRefundService afOrderRefundService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Long orderId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("orderId"),""), 0l);
        AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
        if(orderInfo == null){
            throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
        }
        AfOrderVo orderVo = getOrderVo(orderInfo, context);
        resp.setResponseData(orderVo);
        return resp;
    }

    private AfOrderVo getOrderVo(AfOrderDo order, FanbeiContext context) {
        AfOrderVo vo = new AfOrderVo();
        vo.setGmtCreate(order.getGmtCreate());
        vo.setOrderNo(order.getOrderNo());
        vo.setGmtPay(DateUtil.formatDateToYYYYMMddHHmmss(order.getGmtPay()));
        vo.setActualAmount(order.getActualAmount());
        vo.setMobile(context.getUserName());
        vo.setGmtPayStart(new Date());
        vo.setGmtPayEnd(DateUtil.addHoures(order.getGmtCreate(), Constants.ORDER_PAY_TIME_LIMIT));
        //订单状态
        vo.setOrderStatus(order.getStatus());
        if(OrderStatus.CLOSED.getCode().equals(order.getStatus())) {
            //查询退款表
            AfOrderRefundDo afOrderRefundDo = afOrderRefundService.getOrderRefundByOrderId(order.getRid());
            //订单状态为取消
            if(afOrderRefundDo == null) {
                vo.setOrderStatus("CANCEL");
                vo.setGmtClosed(order.getGmtClosed());
            }
            else {
                vo.setRefundTime(DateUtil.formatDateToYYYYMMddHHmmss(afOrderRefundDo.getGmtModified()));
                vo.setRefundActualAmount(afOrderRefundDo.getActualAmount());
                vo.setRefundContent(afOrderRefundDo.getContent());
            }
        }
        //查询分期信息
        AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(order.getRid());
        if(afBorrowDo != null){
            //分期信息设置 ¥300.00X12期
            vo.setInstallmentInfo(NumberUtil.format2Str(afBorrowDo.getNperAmount())+"x"+afBorrowDo.getNper()+"期");
            vo.setNper(afBorrowDo.getNper());
        }
        List<AfTradeBusinessInfoDto> list = afTradeBusinessInfoService.getByOrderId(order.getRid());
        if(list != null && list.size() > 0) {
            AfTradeBusinessInfoDto dto = list.get(0);
            vo.setBusinessIcon(dto.getImageUrl());
            vo.setBusinessName(dto.getName());
        }
            return vo;
    }
}
