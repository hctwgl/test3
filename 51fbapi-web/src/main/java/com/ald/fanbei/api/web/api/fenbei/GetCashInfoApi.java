package com.ald.fanbei.api.web.api.fenbei;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：GetCashInfoApi
 *@author 何鑫 2017年2月07日  11:01:26
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashInfoApi")
public class GetCashInfoApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		//账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("usableAmount", userDto.getCcAmount());
		data.put("cardNo", userDto.getAlipayAccount());
		data.put("realName", userDto.getRealName());
		resp.setResponseData(data);
		return resp;
	}

}
