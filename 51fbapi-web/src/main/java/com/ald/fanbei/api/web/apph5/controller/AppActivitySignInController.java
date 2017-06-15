package com.ald.fanbei.api.web.apph5.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSignInActivityService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSignInActivityDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author :maqiaopan
 * @version ：2017年6月13日 下午5:02:28
 * @注意：本内容仅限于杭州喜马拉雅家居有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppActivitySignInController extends BaseController {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSignInActivityService afSignInActivityService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponDao afUserCouponDao;
	@Resource
	AfSigninService afSigninService;
	@Resource
	JpushService jpushService;
	@Resource
	CouponSceneRuleEnginerUtil activeRuleEngineUtil;
	@Resource
	AfCouponSceneService afCouponSceneService;

	/**
	 * 
	 * @说明：活动签到的初始页面
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/initActivitySign", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String initActivitySign(HttpServletRequest request, HttpServletResponse response) {
		Calendar calStart = Calendar.getInstance();
		String resultStr = " ";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			Long userId = -1l;
			AfUserDo afUser = null;
			// 和登录有关的
			context = doWebCheck(request, true);
			if (context.isLogin()) {
				afUser = afUserService.getUserByUserName(context.getUserName());
				if (afUser != null) {
					userId = afUser.getRid();
					List<AfResourceDo> listResource = afResourceService.selectActivityConfig();
					if (listResource.equals(null) || listResource.get(0).equals(null)) {
						// TODO:没有活动，走以前的流程。
						AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
						AfCouponSceneDo afCouponSceneDo = afCouponSceneService
								.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
						if (afCouponSceneDo == null) {
							return H5CommonResponse.getNewInstance(false, "初始化失败", "", "").toString();

						}
						Integer seriesTotal = 1;

						List<CouponSceneRuleBo> ruleBoList = afCouponSceneService
								.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");

						if (ruleBoList.size() == 0) {
							return H5CommonResponse.getNewInstance(false, "初始化失败", "", "").toString();

						}

						CouponSceneRuleBo ruleBo = ruleBoList.get(0);
						seriesTotal = NumberUtil.objToIntDefault(ruleBo.getCondition(), 1);
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("cycle", seriesTotal);
						data.put("ruleSignin", ObjectUtils.toString(afCouponSceneDo.getDescription(), "").toString());

						int seriesCount = 0;

						if (afSigninDo == null || null == afSigninDo.getGmtSeries()) {
							data.put("seriesCount", seriesCount);
							data.put("isSignin", "T");

						} else {
							seriesCount = afSigninDo.getSeriesCount();

							Date seriesTime = afSigninDo.getGmtSeries();
							if (DateUtil.isSameDay(new Date(), seriesTime)) {
								data.put("isSignin", "F");

							} else {
								if (!DateUtil.isSameDay(DateUtil.getCertainDay(-1), seriesTime)
										|| seriesCount == seriesTotal) {
									seriesCount = 0;
								}
								data.put("isSignin", "T");
							}

							data.put("seriesCount", seriesCount);

						}
						data.put("type", "N");
						data.put("resultList", "");

						return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
					}
					// 有活动，走活动的流程
					AfResourceDo resourceDo = listResource.get(0);
					Long activityId = resourceDo.getRid();
					if (!activityId.equals(null)) {
						List<Date> listDate = afSignInActivityService.initActivitySign(userId, activityId);
						//TODO:转成String格式
						List<String> listResult = new ArrayList<>();
						SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
						if (!listDate.isEmpty()) {
							for(Date date:listDate){
								String strDate = sFormat.format(date);
								listResult.add(strDate);
							}
						}
						
						
						// 返回活动签到的日期
						String startDate = resourceDo.getValue1();//活动开始的日期
						String  strStartDate = startDate.substring(0, 10);
						HashMap<String, Object> mapResult = new HashMap<String, Object>();
						
						String currentDate = sFormat.format(new java.util.Date());
						// 活动签到
						mapResult.put("type", "Y");
						mapResult.put("cycle", resourceDo.getValue3());
						if (!resourceDo.getDescription().equals(null)) {
							mapResult.put("ruleSignin", resourceDo.getDescription());
						} else {
							mapResult.put("ruleSignin", "");
						}
						mapResult.put("currentDate", currentDate);
						mapResult.put("startDate", strStartDate);
						mapResult.put("resultList", listResult);
						mapResult.put("seriesCount", listResult.size());
						JSONObject jsonResult = new JSONObject(mapResult);

						resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", jsonResult).toString();
					}
				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("fb初始化失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getMessage()).toString();
			logger.error("fb初始化失败" + context, e);
		} finally {
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resultStr, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis());
		}
		return resultStr;

	}

	@RequestMapping(value = "/activitySignIn", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String signIn(HttpServletRequest request, HttpServletResponse response) {

		Calendar calStart = Calendar.getInstance();
		String resultStr = " ";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			Long userId = -1l;
			AfUserDo afUser = null;
			// 和登录有关的
			context = doWebCheck(request, true);
			if (context.isLogin()) {
				afUser = afUserService.getUserByUserName(context.getUserName());
				if (afUser != null) {
					userId = afUser.getRid();
					List<AfResourceDo> listResource = afResourceService.selectActivityConfig();
					// 先判断现在系统时间是否属于新的活动
					if (listResource.equals(null) || listResource.get(0).equals(null)) {
						// TODO:若是不满足条件，走以前的流程
						AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
						AfCouponSceneDo afCouponSceneDo = afCouponSceneService
								.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
						if (afCouponSceneDo == null) {
							return H5CommonResponse.getNewInstance(false, "签到失败", "", FanbeiExceptionCode.FAILED)
									.toString();
						}

						Integer cycle = 1;
						List<CouponSceneRuleBo> ruleBoList = afCouponSceneService
								.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");

						if (ruleBoList.size() == 0) {
							return H5CommonResponse.getNewInstance(false, "签到失败", "", FanbeiExceptionCode.FAILED)
									.toString();

						}
						CouponSceneRuleBo ruleBo = ruleBoList.get(0);
						cycle = NumberUtil.objToIntDefault(ruleBo.getCondition(), 1);
						Integer seriesCount = 1;
						Integer totalCount = 0;
						if (afSigninDo == null) {
							afSigninDo = new AfSigninDo();
							totalCount += 1;
							afSigninDo.setSeriesCount(seriesCount);
							afSigninDo.setTotalCount(totalCount);
							afSigninDo.setUserId(userId);
							if (afSigninService.addSignin(afSigninDo) > 0) {
								return H5CommonResponse.getNewInstance(true, "签到成功", "", "").toString();
							}

						} else {
							Date seriesTime = null;
							if (afSigninDo.getGmtSeries() == null) {
								seriesCount = 1;
							} else {
								seriesTime = afSigninDo.getGmtSeries();
								if (DateUtil.isSameDay(new Date(), seriesTime)) {
									return H5CommonResponse
											.getNewInstance(false, "签到失败", "", FanbeiExceptionCode.FAILED).toString();
								}
								// 当连续签到天数小于循环周期时
								if (DateUtil.isSameDay(DateUtil.getCertainDay(-1), seriesTime)
										&& cycle != afSigninDo.getSeriesCount()) {
									seriesCount = afSigninDo.getSeriesCount() + 1;
								}
							}

							totalCount = afSigninDo.getTotalCount() + 1;

							AfSigninDo signinDo = new AfSigninDo();
							signinDo.setSeriesCount(seriesCount);
							signinDo.setTotalCount(totalCount);
							signinDo.setUserId(userId);
							signinDo.setRid(afSigninDo.getRid());

							if (afSigninService.changeSignin(signinDo) > 0) {
								if (seriesCount == cycle) {
									activeRuleEngineUtil.signin(userId);
									jpushService.getSignCycle(context.getUserName());
								}
							}
						}

						return H5CommonResponse.getNewInstance(false, "签到失败", "", FanbeiExceptionCode.FAILED)
								.toString();

					}
					// :若是满足条件，插入新表
					AfResourceDo resourceDo = listResource.get(0);
					Long activityId = resourceDo.getRid();
					if (!activityId.equals(null)) {
						List<String> listResult = new ArrayList<>();
						List<Date> listDate = afSignInActivityService.initActivitySign(userId, activityId);
						SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
						if (!listDate.isEmpty()) {
							for(Date date:listDate){
								String strDate = sFormat.format(date);
								listResult.add(strDate);
							}
						}
						Integer totalCount = listResult.size() + 1;
						AfSignInActivityDo signInActivityDo = new AfSignInActivityDo();
						signInActivityDo.setActivityId(activityId);
						signInActivityDo.setUserId(userId);
						signInActivityDo.setTotalCount(totalCount);
						Integer intResult = afSignInActivityService.signIn(signInActivityDo);
						
						resultStr = H5CommonResponse.getNewInstance(true, "签到成功", "", "succeed to sign in !")
								.toString();
						
						
						// 若满足券的规则，则加对应券到用户表中
						
						int betweenDays = Integer.valueOf(resourceDo.getValue3()).intValue();
						//说明没有达到领券要求
						if (betweenDays>totalCount.intValue()) {
							return resultStr;
						}

						// 从resource表中解析出来券的id
						String jsonCoupon = resourceDo.getValue4();
						JSONObject jsonObject = JSONObject.parseObject(jsonCoupon);
						// TODO:resource中
						String sign = jsonObject.getString("activitySignin");

						List<JSONObject> listCounpon = JSONObject.parseArray(sign, JSONObject.class);
						Integer amountCoupon = null;
						for (JSONObject jsonCounponId : listCounpon) {
							String strCounponId = jsonCounponId.getString("couponId");
							Long counponId = Long.valueOf(strCounponId);
							AfCouponDo couponDo = afCouponService
									.getCouponById(NumberUtil.objToLongDefault(counponId, 1l));
							if (!couponDo.equals(null)) {

								// 不考虑券的个数限制。
								// id分别插入到user_coupon记录中
								AfUserCouponDo userCoupon = new AfUserCouponDo();
								userCoupon.setCouponId(NumberUtil.objToLongDefault(counponId, 1l));
								userCoupon.setGmtCreate(new java.util.Date());
								userCoupon.setGmtStart(couponDo.getGmtStart());
								userCoupon.setGmtEnd(couponDo.getGmtEnd());
								userCoupon.setUserId(userId);
								afUserCouponDao.addUserCoupon(userCoupon);
								amountCoupon += 1;
							}
						}
						if (!amountCoupon.equals(null) && amountCoupon > 0) {
							resultStr = H5CommonResponse.getNewInstance(true, "恭喜得到" + amountCoupon + "张优惠券！", "",
									"succeed to get a coupon !").toString();
							return resultStr;
						}

						if (!intResult.equals(null)) {
							resultStr = H5CommonResponse.getNewInstance(true, "签到成功", "", "succeed to sign in !")
									.toString();
						}

					}
				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "签到失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("fb签到化失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "签到失败", "", "").toString();
			logger.error("fb签到失败" + context, e);
		} finally {
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resultStr, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis());
		}
		return resultStr;

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
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
