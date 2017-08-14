package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Component("getUserRecommedApi")
public class GetUserRecommedApi implements ApiHandle{
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserService afUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        long userId = context.getUserId();
        HashMap totalData = afRecommendUserService.getRecommedData(userId);
        List<AfResourceDo> list = afRecommendUserService.getActivieResourceByType("RECOMMEND_BACK_IMG");
        HashMap ret = new HashMap();
        if(totalData == null){
            totalData = new HashMap();
        }
        ret.put("userData",totalData);
        ret.put("pic",list);
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(ret);
        return resp;
    }
}