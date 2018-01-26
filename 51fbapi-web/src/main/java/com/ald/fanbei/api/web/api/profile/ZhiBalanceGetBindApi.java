package com.ald.fanbei.api.web.api.profile;

import com.ald.fanbei.api.biz.service.AfUserProfileService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 支付宝线下转账查询绑定支付宝账号接口
 *
 * @author xieqiang
 * @create 2018-01-25 13:25
 **/
@Component("zhiBalanceGetBindApi")
public class ZhiBalanceGetBindApi implements ApiHandle {
    @Resource
    private AfUserProfileService afUserProfileService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        AfUserProfileDo userProfileDo = new AfUserProfileDo();
        userProfileDo.setType("Z");
        userProfileDo.setUserId(userId);
        AfUserProfileDo userprofile = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
        resp.addResponseData("zhiBind",userprofile);
        return resp;
    }
}
