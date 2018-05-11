package com.ald.fanbei.api.web.h5.api.recycle;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.yeepay.g3.utils.common.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 获取手机回收主页
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
@Component("borrowRecycleHomeInfoApi")
public class GetBorrowRecycleHomeInfoApi implements H5Handle {
    @Resource
    private AfBorrowRecycleService afBorrowRecycleService;
    @Resource
    private AfResourceService afResourceService;
    @Resource
    private AfBorrowRecycleGoodsService afBorrowRecycleGoodsService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String reqId = context.getId();
        Map<String,String> params=new HashMap<>();
        params.put("appName",reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length()));
        params.put("ipAddress",context.getClientIp());
        boolean loginFlag = userId == null?false:true;
        resp.addResponseData("isLogin",loginFlag );
        resp.addResponseData("bannerList", afResourceService.getBorrowRecycleHomeListByType());
        resp.addResponseData("recycleInfos", afBorrowRecycleService.getRecycleInfo(userId,params));
        resp.addResponseData("recycleGoodInfos", afBorrowRecycleGoodsService.getAllRecycleGoodsInfos());
        return resp;
    }
}
