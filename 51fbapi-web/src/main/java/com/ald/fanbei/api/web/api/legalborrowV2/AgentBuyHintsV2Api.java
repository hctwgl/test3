package com.ald.fanbei.api.web.api.legalborrowV2;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.ProtocolUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("agentBuyHintsV2Api")
public class AgentBuyHintsV2Api  implements ApiHandle {

    private static final String RESOURCE_TYPE = "AGENTBUY_HINTS";

    @Resource
    AfResourceService afResourceService;
    @Resource
    ProtocolUtil protocolUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        logger.info("agentBuyHintsV2Api = ");
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(RESOURCE_TYPE);
        Map<String, Object> data = new HashMap<>();
        String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
        String ipAddress = CommonUtil.getIpAddr(request);
        if(null != afResourceDo){
            if(StringUtils.equals(afResourceDo.getValue1(),"1")){
                data.put("hint",afResourceDo.getValue());
            }
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userName",context.getUserName());
        map.put("appName",appName);
        map.put("ipAddress",ipAddress);
        List<AfResourceDo> agentBuyList = protocolUtil.getProtocolList("agentbuy",map);
        data.put("agentBuyList",agentBuyList);
        resp.setResponseData(data);
        logger.info("agentBuyHintsV2Api = agentBuyHintsV2Api");
        return resp;
    }
}
