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
 * @类描述：获取活动资源
 * @author  hongzhengpei
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
@Component("getActivieResourceByTypeApi")
public class GetActivieResourceByTypeApi implements ApiHandle {

    @Resource
    AfRecommendUserService afRecommendUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request){

        List<String> typeList = new ArrayList<String>();
        typeList.add("RECOMMEND_PRIZE");    //活动奖品
        typeList.add("RECOMMEND_IMG");      //活动banner
        typeList.add("RECOMMEND_BACK_IMG"); //活动背景
        typeList.add("RECOMMEND_RULE");      //活动规则

        Map<String, Object> params = requestDataVo.getParams();
        String type = ObjectUtils.toString(params.get("type"), "").toString();
        if( typeList.contains(type)){
            ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
            List<AfResourceDo> list = afRecommendUserService.getActivieResourceByType(type);
            if(type.equals("RECOMMEND_IMG")){
                for (AfResourceDo afResourceDo : list){
                    String url = afResourceDo.getValue() + "?name=RECOMMEND_IMG";
                    afResourceDo.setValue(url);
                }
            }


            HashMap map = new HashMap();
            map.put("result",list);
            resp.setResponseData(map);
            return resp;
        }
        else{
            ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
            return  resp;
        }
    }
}