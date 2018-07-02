package com.ald.fanbei.api.web.api.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：刪除订单
 * @author chengkang 2017年6月15日下午20:46:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteOrderInfoApi")
public class DeleteOrderInfoApi implements ApiHandle {

    @Resource
    AfOrderService afOrderService;
    @Resource
    AfGoodsService afGoodsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	Long userId = context.getUserId();
	Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));

	afOrderService.deleteOrder(userId, orderId);
	return resp;
    }

}
