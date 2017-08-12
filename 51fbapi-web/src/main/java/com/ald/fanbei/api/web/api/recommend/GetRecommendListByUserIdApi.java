package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：个人活动数据列表
 * @author  2017年hongzhengpei08月10日上午11:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
@Component("getRecommendListByUserIdApi")
public class GetRecommendListByUserIdApi implements ApiHandle {

    @Resource
    AfRecommendUserService afRecommendUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request){
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        Map<String, Object> params = requestDataVo.getParams();
        Integer pageIndex = Integer.parseInt( ObjectUtils.toString(params.get("pageIndex"), "0").toString());
        Integer pageSize = Integer.parseInt(ObjectUtils.toString(params.get("pageSize"), "15").toString());


        List<HashMap> list = afRecommendUserService.getRecommendListByUserId(userId,pageIndex,pageSize);
        resp.setResponseData(list);
        return  resp;
    }
}