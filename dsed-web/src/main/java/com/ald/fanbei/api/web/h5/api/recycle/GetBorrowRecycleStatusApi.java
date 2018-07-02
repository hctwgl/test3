package com.ald.fanbei.api.web.h5.api.recycle;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 获取手机回收主页
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleStatusApi")
public class GetBorrowRecycleStatusApi implements H5Handle {
    @Resource
    private AfBorrowRecycleService afBorrowRecycleService;
    @Resource
    private AfUserAccountService afUserAccountService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        boolean loginFlag = userId == null?false:true;
        resp.addResponseData("isLogin",loginFlag );
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        resp.addResponseData("recycleInfos", afBorrowRecycleService.getRejectCodeAndAction(afUserAccountDo));
        return resp;
    }
}
