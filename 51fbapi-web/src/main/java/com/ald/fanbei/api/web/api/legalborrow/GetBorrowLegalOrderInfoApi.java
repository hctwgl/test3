package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowLegalOrderQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：查询用户订单记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalOrderInfoApi")
public class GetBorrowLegalOrderInfoApi implements ApiHandle {

    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> data = Maps.newHashMap();
        resp.setResponseData(data);

        // 判断用户是否登录
        Long userId = context.getUserId();
        if (userId == null) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
        }
        // 获取查询页码
        int pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 0);

        AfBorrowLegalOrderQuery query = new AfBorrowLegalOrderQuery();
        query.setUserId(userId);
        query.setPageNo(pageNo);
        // 查询用户订单记录
        List<AfBorrowLegalOrderDo> borrowLegalOrdersList = afBorrowLegalOrderService.getUserBorrowLegalOrderList(query);
        data.put("orderList", borrowLegalOrdersList);
        resp.setResponseData(data);
        return resp;
    }

}
