package com.ald.fanbei.api.web.h5.api.recycle;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 获取金融科技主页
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowFinanceHomeApi")
public class GetBorrowFinanceHomeApi implements H5Handle {

    @Resource
    private AfResourceService afResourceService;

    @Resource
    private AfLoanSupermarketService afLoanSupermarketService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        String appType = context.getId().startsWith("i") ? "1" : "2";
        resp.addResponseData("bannerList", afResourceService.getBorrowFinanceHomeListByType());
        resp.addResponseData("borrowMarkets", afLoanSupermarketService.getBorrowHomeListByLable(Constants.BORROW_FINANCE,appType));
        return resp;
    }


}
