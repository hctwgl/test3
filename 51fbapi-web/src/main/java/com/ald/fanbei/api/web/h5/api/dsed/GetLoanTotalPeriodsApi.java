package com.ald.fanbei.api.web.h5.api.dsed;


import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 贷款发起前确认
 *
 * @author ZJF
 */
@Component("dsedLoanTotalPeriodsApi")
public class GetLoanTotalPeriodsApi implements DsedH5Handle {

    @Resource
    private DsedLoanProductService dsedLoanProductService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        HashMap<String, Object> data = new HashMap<String, Object>();
//		String prdType = context.getData("prdType").toString();
        String prdType = "DSED_LOAN";
        Integer periods = dsedLoanProductService.getMaxPeriodsByPrdType(prdType);
        List periodArray = new ArrayList();
        for (int i = 1; i <= periods; i++) {
            periodArray.add(i);
        }
        data.put("realPeriods", periodArray);
        resp.setData(data);
        return resp;
    }

}
