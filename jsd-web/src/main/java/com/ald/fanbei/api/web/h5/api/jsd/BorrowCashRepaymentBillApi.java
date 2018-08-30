package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("borrowCashRepaymentBillApi")
public class BorrowCashRepaymentBillApi implements DsedH5Handle {

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        String borrowNo = ObjectUtils.toString(context.getData("borrowNo"), null);
        JsdBorrowCashDo cashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
        JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());
        Map<String, Object> map = buildBorrowBill(cashDo,orderCashDo);
        resp.setData(map);
        return resp;
    }
    private Map<String,Object> buildBorrowBill(JsdBorrowCashDo cashDo,JsdBorrowLegalOrderCashDo orderCashDo){
        Map<String, Object> map = new HashMap<String, Object>();
        BigDecimal unrepayGoodsAmount=BigDecimal.ZERO;
        BigDecimal unrepayGoodsSellAmount=BigDecimal.ZERO;
        if(orderCashDo!=null){
            unrepayGoodsAmount=unrepayGoodsAmount.add(orderCashDo.getAmount()).add(orderCashDo.getSumRepaidPoundage()).add(orderCashDo.getSumRepaidOverdue())
                    .add(orderCashDo.getSumRepaidInterest()).subtract(orderCashDo.getRepaidAmount());
            map.put("unrepayGoodsAmount",unrepayGoodsAmount);
            unrepayGoodsSellAmount=unrepayGoodsSellAmount.add(orderCashDo.getOverdueAmount()).add(orderCashDo.getInterestAmount()).add(orderCashDo.getPoundageAmount());
            map.put("unrepayGoodsSellAmount",unrepayGoodsSellAmount);
        }
        List<Map<String,String>>  borrowBillDetails=new ArrayList<>();
        Map<String,String> borrowBillDetail=new HashMap<>();
        borrowBillDetail.put("status",cashDo.getStatus());
        borrowBillDetail.put("overdueStatus",cashDo.getOverdueStatus());
        borrowBillDetail.put("totalPeriod","1");
        borrowBillDetail.put("period","1");
        borrowBillDetail.put("amount", String.valueOf(cashDo.getAmount()));
        BigDecimal totalAmount=BigDecimal.ZERO;
        totalAmount=totalAmount.add(cashDo.getAmount()).add(cashDo.getOverdueAmount()).add(cashDo.getPoundage()).add(cashDo.getRateAmount()).add(cashDo.getSumOverdue())
                .add(cashDo.getSumRate()).add(cashDo.getSumRenewalPoundage());
        borrowBillDetail.put("totalAmount", String.valueOf(totalAmount));
        borrowBillDetail.put("interestAmount", String.valueOf(cashDo.getRateAmount().add(cashDo.getSumRate())));
        borrowBillDetail.put("serviceAmount", String.valueOf(cashDo.getPoundage().add(cashDo.getSumRenewalPoundage())));
        borrowBillDetail.put("overdueAmount", String.valueOf(cashDo.getOverdueAmount().add(cashDo.getSumOverdue())));
        borrowBillDetail.put("gmtPlanRepay", String.valueOf(cashDo.getGmtPlanRepayment()));
        borrowBillDetail.put("repaidAmount", String.valueOf(cashDo.getRepayAmount()));
        BigDecimal unrepayAmount=BigDecimal.ZERO;
        unrepayAmount=unrepayAmount.add(cashDo.getAmount()).add(cashDo.getOverdueAmount()).add(cashDo.getPoundage()).add(cashDo.getRateAmount()).add(cashDo.getSumOverdue())
                .add(cashDo.getSumRate()).add(cashDo.getSumRenewalPoundage()).subtract(cashDo.getRepayAmount());
        borrowBillDetail.put("unrepayAmount", String.valueOf(unrepayAmount));
        borrowBillDetail.put("unrepayInterestAmount", String.valueOf(cashDo.getRateAmount()));
        borrowBillDetail.put("unrepayOverdueAmount", String.valueOf(cashDo.getOverdueAmount()));
        borrowBillDetail.put("unrepayServiceAmount", String.valueOf(cashDo.getPoundage()));
        borrowBillDetails.add(borrowBillDetail);
        map.put("borrowBillDetails",borrowBillDetails);
        return map;
    }
}
