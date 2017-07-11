package com.ald.fanbei.api.web.api.borrowCash;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketTabService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketTabDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 沈铖 2017/7/5 下午4:11
 * @类描述: GetLoanSupermarketTabListApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLoanSupermarketTabListApi")
public class GetLoanSupermarketTabListApi implements ApiHandle {
    @Resource
    private AfLoanSupermarketTabService afLoanSupermarketTabService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        List<AfLoanSupermarketTabDo> tabList = afLoanSupermarketTabService.getTabList();
        resp.addResponseData("tabList",tabList);
        return resp;
    }
}
