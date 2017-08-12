package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：活动资源
 * @author  2017年月10日上午11:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
@Component("getActivieResourceByTypeApi")
public class GetActivieResourceByTypeApi implements ApiHandle {

    @Resource
    AfRecommendUserService afRecommendUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request){

        List<String> typeList = new ArrayList<String>();
        typeList.add("RECOMMEND_PRIZE");
        typeList.add("RECOMMEND_IMG");


        Map<String, Object> params = requestDataVo.getParams();
        String type = ObjectUtils.toString(params.get("type"), "").toString();
        if( typeList.contains(type)){
            ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
            List<AfResourceDo> list = afRecommendUserService.getActivieResourceByType(type);
            resp.setResponseData(list);
            return resp;
        }
        else{
            ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
            return  resp;
        }
    }
}