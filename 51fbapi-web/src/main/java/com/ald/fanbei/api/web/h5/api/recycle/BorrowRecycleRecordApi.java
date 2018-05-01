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
 * @Description: 回收记录
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleRecordApi")
public class BorrowRecycleRecordApi implements H5Handle {

    @Resource
    private AfBorrowLegalService afBorrowLegalService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        int start = Integer.parseInt(context.getData("start").toString());
        boolean loginFlag = userId == null?false:true;
        resp.addResponseData("loginFlag",loginFlag );
        resp.addResponseData("recycleRecords", afBorrowLegalService.getRecycleRecord(userId,start));
        return resp;
    }

}
