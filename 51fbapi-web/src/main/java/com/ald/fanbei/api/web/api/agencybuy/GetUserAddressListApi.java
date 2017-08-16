/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午9:36:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getUserAddressListApi")
public class GetUserAddressListApi implements ApiHandle {
	@Resource
	AfUserAddressService afUserAddressService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		List<AfUserAddressDo> list = afUserAddressService.selectUserAddressByUserId(userId);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("addressList", JSON.toJSON(list));
		resp.setResponseData(data);

		return resp;
	}

}
