package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 *@类现描述：客户端提交分享行为，针对某些h5页面用户去分享时服务端需要记录是否分享、分享了之后需要做一些业务。针对需要服务端统计分享的页面客户端需把分享的行为告诉服务端
 *@author chenjinhu 2017年6月7日 下午4:08:18
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("submitShareActionApi")
public class SubmitShareActionApi extends BaseController implements ApiHandle {
	//protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");//埋点日志
	@Resource
	private AfGameChanceService afGameChanceService;
	@Resource
	AfBoluomeActivityUserItemsService afBoluomeActivityUserItemsService;
	@Resource
	private AfFacescoreShareCountService faceScoreShareCountService;
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
		if ("faceScore".equals(sharePage)){
			faceScoreShareCountService.dealWithShareCount(context.getUserId());
		}
		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "分享"),"sharePage="+sharePage, context.getMobile());
		//maidianLog.info("sharePage="+sharePage, context.getMobile(),requestDataVo.getParams().get("shareAppUrl"));
//		if("ggIndexShare".equals(sharePage)){
//			maidianLog.info(context.getUserName() + "ggIndexShare");
//		}
//
		//若是逛逛点亮活动则形式为类似 ggpresents_userItemsId_5 格式
//		String[] strings = sharePage.split("_");
//		if (strings != null && strings.length == 3) {
//		       maidianLog.info(context.getUserName() +strings[0]);
//			if ("ggpresents".equals(strings[0])) {
//				String strUserItemsId = strings[2];
//				Long userItemsId = Long.parseLong(strUserItemsId);
//				//进行冻结卡片
//				try {
//					AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsService.getById(userItemsId);
//					if (prevousDo != null && "NORMAL".equals(prevousDo.getStatus())) {
//						afBoluomeActivityUserItemsService.updateUserItemsStatus(userItemsId, "FROZEN");
//					}
//				} catch (Exception e) {
//					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
//					
//				}
//				
//			}
//			
//		}
		
		return resp;
	}
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
	    // TODO Auto-generated method stub
	    return null;
	}
	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(),
					FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	    // TODO Auto-generated method stub
	    return null;
	}
}
