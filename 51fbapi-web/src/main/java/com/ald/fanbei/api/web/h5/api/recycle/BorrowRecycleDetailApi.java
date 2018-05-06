package com.ald.fanbei.api.web.h5.api.recycle;


import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 回收详情
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleDetailApi")
public class BorrowRecycleDetailApi implements H5Handle {

    @Resource
    private AfRepaymentBorrowCashService repaymentBorrowCashService;

    @Resource
    private AfBorrowCashService afBorrowCashService;

    @Resource
    private AfBorrowRecycleService afBorrowRecycleService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Long borrowId = Long.parseLong(context.getData("borrowId").toString());
        if (borrowId == null || borrowId <= 0) {
            throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
        }

        BigDecimal totalRepaidAmount = BigDecimal.ZERO;
        AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
        totalRepaidAmount = BigDecimalUtil.add(totalRepaidAmount, borrowCashDo.getRepayAmount());
        resp.addResponseData("totalRepaidAmount", totalRepaidAmount);
        List<AfRepaymentBorrowCashDo> borrowCashDos = repaymentBorrowCashService.getRepaymentBorrowCashByBorrowId(borrowId);
        resp.addResponseData("repayments", borrowCashDos);
        resp.addResponseData("recycleInfo", afBorrowRecycleService.getRecycleRecordByBorrowId(borrowId));
        return resp;
    }

}
