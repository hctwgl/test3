package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取取现日利率
 * @author huyang 2017年4月10日上午9:42:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getDailyRateApi")
public class GetDailyRateApi implements ApiHandle {

    @Resource
    private AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        // 获取利率说明信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH);
        resp.addResponseData("cashRate",new BigDecimal(resource.getValue()));
        return resp;
    }

}
