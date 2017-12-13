package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：查询用户订单记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalChargeInfoApi")
public class GetBorrowLegalChargeInfoApi implements ApiHandle {

    @Resource
    private AfResourceService afResourceService;

    private static final String BORROW_RATE = "BORROW_RATE";
    private static final String BORROW_CASH_INFO_LEGAL = "BORROW_CASH_INFO_LEGAL";

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> data = Maps.newHashMap();
        resp.setResponseData(data);

        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(BORROW_RATE, BORROW_CASH_INFO_LEGAL);
        if (afResourceDo != null) {
            data.put("description", afResourceDo.getDescription());
        } else {
            data.put("description", "");
        }
        return resp;
    }

}
