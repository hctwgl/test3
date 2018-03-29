package com.ald.fanbei.api.web.apph5.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfFacescoreRedService;
import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfFacescoreShareCountDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 颜值测试游戏颜值变现功能接口
 * @author liutengyuan 
 * @date 2018年3月22日
 */
@Controller
public class AppH5FaceScoreWithdrawJudgeController extends BaseController {

	@Resource
	private AfUserService afUserService;
	@Resource
	private AfFacescoreShareCountService faceScoreShareCountService;
	@Resource
	private AfFacescoreRedService faceScoreRedService;
	@Resource
	private AfResourceService afResourceService;

	@ResponseBody
	@RequestMapping(value = "/fanbei_api/withDraw/judge", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String withDrawJudge(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			FanbeiWebContext context = new FanbeiWebContext();
			Long userId = -1l;
			AfUserDo afUser = null;
			// 和登录有关的
			context = doWebCheck(request, false);
			// 2判断用户是否处于登陆状态
			if (context.isLogin()) {
				afUser = afUserService.getUserByUserName(context.getUserName());
				if (afUser == null) {
					return H5CommonResponse.getNewInstance(false, "用户非法", null,
							null).toString();
				}
				if (afUser != null) {
					userId = afUser.getRid();
					// 1先查询有没有测试过，是否对红包进行了提现
					int count = faceScoreRedService
							.findUserAndRedRelationRecordByUserId(userId);
					List<AfResourceDo> configList = afResourceService
							.getConfigByTypes("USER_FACETEST");
					if (CollectionUtil.isEmpty(configList)) {
						return H5CommonResponse.getNewInstance(false, "颜值测试活动已经结束！",
								"", null).toString();
					}
					Integer totalAllowedCount = Integer.valueOf(configList.get(0)
							.getValue1());
					if (count >= totalAllowedCount) {
						return H5CommonResponse.getNewInstance(false,
								"拆红包的次数已经用完 ,快去将您的颜值昭告天下吧！", "", null).toString();
					} else if (count == totalAllowedCount - 1) {
						// 已经领取过一次，进行分享次数的判断
						AfFacescoreShareCountDo shareCountDo = faceScoreShareCountService
								.getByUserId(userId);
						if (shareCountDo == null) {
							return H5CommonResponse.getNewInstance(false,
									"拆红包的次数已经用完，分享五个群可再得一次拆红包的机会！", "", null)
									.toString();
						}
						Integer sharedCount = shareCountDo.getCount();
						// 获取需要分享的次数
						int needSharedCount = Integer.valueOf(configList.get(0)
								.getValue());
						if (sharedCount < needSharedCount) {
							int a = needSharedCount - sharedCount;
							return H5CommonResponse.getNewInstance(false,
									"拆红包的次数已经用完 ,分享"+ a +"个群可再得一次拆红包的机会！", "", null)
									.toString();
						}
					}
					return H5CommonResponse.getNewInstance(true, "可以进行下一步的拆红包", "",
							null).toString();
				}
			}
			return H5CommonResponse.getNewInstance(true, "用户没有登陆，可以接着拆红包!", null,
					null).toString();
		} catch (Exception exception) {
			String result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
			return result;
		}
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
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
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
