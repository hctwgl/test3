package com.ald.fanbei.api.web.api.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfBankDao;
import com.ald.fanbei.api.dal.domain.AfBankDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：银行卡列表
 *@author hexin 2017年3月1日 上午10:31:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBankListApi")
public class GetBankListApi implements ApiHandle {

	@Resource
	private AfBankDao afBankDao;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		List<AfBankDo> list = afBankDao.getBankList();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("bankList", list);
		resp.setResponseData(data);
		return resp;
	}
}
