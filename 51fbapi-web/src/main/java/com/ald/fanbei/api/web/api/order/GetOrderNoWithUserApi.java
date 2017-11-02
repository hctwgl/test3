package com.ald.fanbei.api.web.api.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取用户订单关联信息
 * @author 何鑫 2017年2月17日下午17:40:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderNoWithUserApi")
public class GetOrderNoWithUserApi implements ApiHandle{

	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String orderNo = ObjectUtils.toString(requestDataVo.getParams().get("orderNo"),"");
		if(afOrderService.syncOrderNoWithUser(userId, orderNo)>0){
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}
}
