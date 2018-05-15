package com.ald.fanbei.api.web.h5.api.recycle;


import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowRecycleService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * @Description: 回收记录
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleRecordApi")
public class BorrowRecycleRecordApi implements H5Handle {

    @Resource
    private AfBorrowRecycleService afBorrowRecycleService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        boolean loginFlag = userId == null?false:true;
        resp.addResponseData("loginFlag",loginFlag );
        resp.addResponseData("recycleRecords", afBorrowRecycleService.getRecycleRecord(userId));
        return resp;
    }

}
