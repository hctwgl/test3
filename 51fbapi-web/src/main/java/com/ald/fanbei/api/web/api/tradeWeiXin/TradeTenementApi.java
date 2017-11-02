 package com.ald.fanbei.api.web.api.tradeWeiXin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.TradeTenementService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("tradeTenementApi")
public class TradeTenementApi implements ApiHandle {

	@Resource
	private TradeTenementService tradeTenementService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		
		String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        
       
        
        List<String> timeList = tradeTenementService.getTimeByBusinessId(businessId);
        Map<String,Object> newMap = new LinkedHashMap<String,Object>();
        for (String applyTime : timeList) {
        	String dateTime = applyTime.substring(0, 10);
        	List<AfTradeTenementInfoDo> tenementInfo = tradeTenementService.getTenementInfoDoByTime(dateTime,businessId);
        	newMap.put(dateTime, tenementInfo);
        	
		}
        resp.setResponseData(newMap);
        return resp;
	}

	
}
