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
    @Resource
    private AfUserAuthService afUserAuthService;
    @Resource
    private ApplyLegalBorrowCashService borrowCashService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        boolean loginFlag = userId == null?false:true;
        resp.addResponseData("isLogin",loginFlag );
        if(loginFlag){
            AfUserAuthDo authInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
            if(authInfo!=null){
                String basicStatus = authInfo.getBasicStatus();
                if(StringUtils.equals(basicStatus, "A") || StringUtils.isBlank(basicStatus)) {
                    resp.addResponseData("authStatus", false);
                } else {
                    resp.addResponseData("authStatus", true);
                }
                resp.addResponseData("isRealAuthz", YesNoStatus.YES.getCode().equals(authInfo.getFacesStatus()));
                resp.addResponseData("isRiskAuthz", YesNoStatus.YES.getCode().equals(authInfo.getRiskStatus()));
                resp.addResponseData("isRiskRefusedAuthz", YesNoStatus.YES.getCode().equals(borrowCashService.checkRiskRefusedResult(userId)));
                resp.addResponseData("isSecAuthzAllPass", afUserAuthService.allSupplementAuthPassed(authInfo));
            }else {
                resp.addResponseData("authStatus", false);
            }
        }
        resp.addResponseData("bannerList", afResourceService.getBorrowRecycleHomeListByType());
        resp.addResponseData("recycleInfos", afBorrowRecycleService.getRecycleInfo(userId));
        resp.addResponseData("recycleGoodInfos", afBorrowRecycleGoodsService.getAllRecycleGoodsInfos());
        return resp;
    }
}
