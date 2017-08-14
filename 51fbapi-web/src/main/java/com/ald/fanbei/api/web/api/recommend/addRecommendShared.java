package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增分享
 */
@Component("addRecommendSharedApi")
public class addRecommendShared implements ApiHandle {
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserService afUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

        String notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);


        long userId = context.getUserId();
        AfUserDo afUserDo = afUserService.getUserById(userId);
        Map<String, Object> params = requestDataVo.getParams();
        Integer type = Integer.parseInt(ObjectUtils.toString(params.get("type"), "0").toString());
        String uuid = ObjectUtils.toString(params.get("uuid"), "").toString();

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

        AfRecommendShareDo afRecommendShareDo = new AfRecommendShareDo();
        afRecommendShareDo.setUser_id(afUserDo.getRid());
        afRecommendShareDo.setType(type);
        afRecommendShareDo.setRecommend_code(afUserDo.getRecommendCode());
        if(uuid !=null && !uuid.equals("")){
            afRecommendShareDo.setId(uuid);
        }

        String url = notifyHost +"/fanbei-web/app/inviteShare?sharedId="+afRecommendShareDo.getId();

        HashMap ret = new HashMap();
        int i= afRecommendUserService.addRecommendShared(afRecommendShareDo);
        ret.put("url",url);
        resp.setResponseData(ret);
        return resp;
    }
}
