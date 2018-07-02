package com.ald.fanbei.api.web.h5.api.recycle;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 获取回收说明
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleExplainApi")
public class BorrowRecycleExplain implements H5Handle {

   @Resource
   private AfResourceService afResourceService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_RECYCLE_INFO_LEGAL_NEW.getCode());
        resp.addResponseData("description10", afResourceDo.getDescription());
        resp.addResponseData("description20", afResourceDo.getPic1());
        return resp;
    }
}
