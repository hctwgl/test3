package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 郭帅强 2017年12月12日  16:46:23
 * @类描述：判断是否是新版借款页面
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashPageTypeApi")
public class GetCashPageTypeApi implements ApiHandle {

    @Resource
    private AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

    @Resource
    private AfBorrowCashService afBorrowCashService;

    @Resource
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,
                                     FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String resourceType = "BORROW_BACK";//从resource中获取参数判断是否为回退
        String secType = "BORROW_BACK_RESULT";
        boolean openStatus = false;//判断是否为回退
        String pageType = "old";
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(resourceType, secType);
        if (afResourceDo != null && null != afResourceDo.getValue()) {
            if ("true".equals(afResourceDo.getValue())) {
                openStatus = true;//回退
            }
        }
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);//查询borrowCash中是否有借款信息
        if (afBorrowCashDo != null) {
            if (!"FINSH".equals(afBorrowCashDo.getStatus())) {//borrowCash存在借款信息，并且未结清
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
                if (afBorrowLegalOrderCashDo != null) {//查询是否有新版本借款
                    pageType = "new";
                }
            } else if (!openStatus) {
                pageType = "new";
            }
        } else {
            if (!openStatus) {//不是回退且没有借款信息
                pageType = "new";
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("pageType", pageType);
        resp.setResponseData(data);
        return resp;
    }
}
