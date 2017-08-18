package com.ald.fanbei.api.web.api.user;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：客户端提交分享行为，针对某些h5页面用户去分享时服务端需要记录是否分享、分享了之后需要做一些业务。针对需要服务端统计分享的页面客户端需把分享的行为告诉服务端
 *@author chenjinhu 2017年6月7日 下午4:08:18
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("submitShareActionApi")
public class SubmitShareActionApi implements ApiHandle {
	protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");//埋点日志
	@Resource
	private AfGameChanceService afGameChanceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String sharePage = (String)requestDataVo.getParams().get("sharePage");
		if(StringUtil.isBlank(sharePage)){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		if(!CommonUtil.isMobile(context.getMobile())){
			return resp;
		}
		if("gameShare".equals(sharePage)){
			afGameChanceService.dealWithShareGame(context.getMobile());
		}
		if("ggShare".equals(sharePage)){
			maidianLog.info(context.getUserName() + "ggShare");
		}
		return resp;
	}

}
