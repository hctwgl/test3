package com.ald.fanbei.api.web.api.tradeWeiXin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.TradeTenementService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("getTenementApi")
public class GetTenementApi implements ApiHandle {

	@Resource
	private TradeTenementService tradeTenementService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"), 0l);
        String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"), "").toString();
        
        AfTradeTenementInfoDo afTradeTenementInfoDo = tradeTenementService.getTenementInfoDoById(id);
//        AfIdNumberDo afIdNumberDo = tradeTenementService.getUserIdentityUrl(mobile);
//        String idFrontUrl = afIdNumberDo.getIdFrontUrl();
//        String idBehindUrl = afIdNumberDo.getIdBehindUrl();
        
        resp.addResponseData("afTradeTenementInfoDo", afTradeTenementInfoDo);
//        resp.addResponseData("idFrontUrl", idFrontUrl);
//        resp.addResponseData("idBehindUrl", idBehindUrl);
		return resp;
	}

}
