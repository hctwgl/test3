package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.BigDecimalUtil;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

@Component("borrowCashRepaymentDetailApi")
public class BorrowCashRepaymentDetailApi implements JsdH5Handle {


    @Resource
    private JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");

        String borrowNo = ObjectUtils.toString(context.getData("borrowNo"), null);
        Long timestamp = Long.valueOf(context.getData("timestamp").toString());
        JsdBorrowCashDo cashDo=jsdBorrowCashService.getByTradeNoXgxy(borrowNo);
        if(cashDo==null){
            throw new BizException(BizExceptionCode.JSD_BORROW_IS_NULL);
        }
        JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());
        BorrowAmount bo=new BorrowAmount();
        calculateAmount(bo,cashDo,orderCashDo);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("amount",bo.amount);
        map.put("remark",bo.remark);
        map.put("timestamp",timestamp);
        resp.setData(map);
        return resp;
    }

    private void calculateAmount(BorrowAmount bo,JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo){

        bo.cashAmount=BigDecimal.ZERO;
        bo.cashPoundage=BigDecimal.ZERO;
        bo.principle=BigDecimal.ZERO;
        bo.amount=BigDecimal.ZERO;
        bo.orderAmount=BigDecimal.ZERO;
        bo.orderPoundage=BigDecimal.ZERO;

        bo.cashAmount=jsdBorrowCashService.calcuUnrepayAmount(cashDo, orderCashDo);
        bo.cashPoundage=bo.cashPoundage.add(cashDo.getOverdueAmount()).add(cashDo.getPoundageAmount()).add(cashDo.getInterestAmount());
        bo.principle=bo.principle.add(cashDo.getAmount()).add(cashDo.getSumRepaidPoundage().add(cashDo.getSumRepaidInterest()).add(cashDo.getSumRepaidOverdue())).subtract(cashDo.getRepayAmount());
        if(orderCashDo!=null){
            bo.orderAmount=
                    BigDecimalUtil.add(orderCashDo.getAmount(), orderCashDo.getInterestAmount(), orderCashDo.getPoundageAmount(), orderCashDo.getOverdueAmount(),
                            orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidOverdue()).subtract(orderCashDo.getRepaidAmount());
            bo.orderPoundage=bo.orderPoundage.add(orderCashDo.getInterestAmount()).add(orderCashDo.getOverdueAmount()).add(orderCashDo.getPoundageAmount());
        }
        bo.amount= bo.cashAmount;
        bo.remark="费用明细: 1.其中含借款本金"+bo.principle+"元，利息&手续费&逾期费"+bo.cashPoundage+"元。2.商品金额为"+bo.orderAmount+"元，利息&手续费&逾期费"+bo.orderPoundage+"元。";
    }

    public class BorrowAmount{

        public BigDecimal cashAmount;
        public BigDecimal cashPoundage;
        public BigDecimal principle;
        public BigDecimal amount;
        public BigDecimal orderAmount;
        public BigDecimal orderPoundage;
        public String remark;


    }




}
