
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityMsgIndexService;
import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5GgActivity;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityMsgIndexDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfCardDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfRebateDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBoluomeUserCouponVo;
import com.ald.fanbei.api.web.vo.BoluomeActivityInviteCeremonyVo;
import com.ald.fanbei.api.web.vo.BoluomeActivityInviteFriendVo;
import com.ald.fanbei.api.web.vo.BoluomeActivityPoPupVo;
import com.ald.fanbei.api.web.vo.userReturnBoluomeCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: APPH5GgActivityController
 * @Description: 吃玩住行活动
 * @author chenqiwei
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年11月14日 下午2:07:40
 *
 */
@RestController
@RequestMapping(value = "/h5GgActivity", produces = "application/json;charset=UTF-8")
public class APPH5GgActivityController extends BaseController {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfBoluomeUserCouponService afBoluomeUserCouponService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfShopService afShopService;
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfResourceDao afResourceDao;
	@Resource
	AfBoluomeActivityMsgIndexService afBoluomeActivityMsgIndexService;
	@Resource
	AfBoluomeRebateService afBoluomeRebateService;
	@Resource
	AfBoluomeActivityItemsService afBoluomeActivityItemsService;
//	@Resource
//	AfBoluomeActivityUserLoginService afBoluomeActivityUserLoginService;
	

	String opennative = "/fanbei-web/opennative?name=";

	
	/** 
	* author chenqiwei
	* @Title: returnCoupon 
	* @Description: 返券列表 
	* @param @param request
	* @param @param response
	* @param @param model
	* @param @return
	* @param @throws IOException     
	* @return String    返回类型 
	* @throws 
	*/
	@RequestMapping(value = "/returnCoupon", method = RequestMethod.POST)
	public String returnCoupon(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		
		FanbeiWebContext context = new FanbeiWebContext();
		String resultStr = "";
		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());

			List<userReturnBoluomeCouponVo> returnCouponList = new ArrayList<userReturnBoluomeCouponVo>();
			AfBoluomeUserCouponVo vo = new AfBoluomeUserCouponVo();
			// 未登录时初始化一些数据
			AfShopDo shopDo = new AfShopDo();
			shopDo.setType(H5GgActivity.WAIMAI.getCode());
			AfShopDo shop = afShopService.getShopInfoBySecTypeOpen(shopDo);
			if (shop != null) {
				vo.setShopId(shop.getRid());
			}
			BigDecimal couponAmount = new BigDecimal(String.valueOf(0));
			BigDecimal inviteAmount = new BigDecimal(String.valueOf(0));
			vo.setInviteAmount(inviteAmount);
			vo.setCouponAmount(couponAmount);
			vo.setReturnCouponList(returnCouponList);
			if (userId == null) {
				return H5CommonResponse.getNewInstance(true, "获取返券列表成功", null, vo).toString();
			}

			// 从登录表取数据，遍历list.查询是否有订单，没有订单：未消费。有订单未完成：未完成。有订单且已完成：已消费

//			long activityId = 1000L;
//			List<AfBoluomeActivityUserLoginDo> afBoluomeActivityUserLoginList = afBoluomeActivityUserLoginService
//					.getByRefUserIdAndActivityId(userId, activityId);
			
			
			AfRecommendUserDo queryRecommendUser = new AfRecommendUserDo();
        			queryRecommendUser.setParentId(userId);
        			queryRecommendUser.setType(1);
        			AfResourceDo startTime = new  AfResourceDo();
        		    	startTime = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
		    	   if(startTime != null){
		    	        SimpleDateFormat parser = new SimpleDateFormat(DateUtil.DATE_TIME_SHORT);
		    	        Date gmtCreate =  parser.parse(startTime.getValue());
        		        queryRecommendUser.setGmt_create(gmtCreate);
		    	   }
		    	
			List<AfRecommendUserDo> afRecommendUserDoList = afRecommendUserService.getListByParentIdAndType(queryRecommendUser);
			
			if (afRecommendUserDoList.size() > 0) {
				for (AfRecommendUserDo uDo : afRecommendUserDoList) {
					userReturnBoluomeCouponVo returnCouponVo = new userReturnBoluomeCouponVo();
					returnCouponVo.setInviteeMobile(changePhone(uDo.getUserName()));
					returnCouponVo.setRegisterTime(DateUtil.formatDateForPatternWithHyhen(uDo.getGmt_create()));
					returnCouponVo.setReward("0");
					// 订单状态
					AfOrderDo order = new AfOrderDo();
					order.setUserId(uDo.getUserId());
					int queryCount = afOrderService.getOrderCountByStatusAndUserId(order);
					logger.info("/h5GgActivity/returnCoupon queryCount = {}"+queryCount+",userId = {}"+userId);
					if (queryCount <= 0) {
						returnCouponVo.setStatus(H5GgActivity.NO_CONSUME.getDescription());
					}
					if (queryCount > 0) {
						AfOrderDo orderStatus = new AfOrderDo();
						orderStatus.setUserId(uDo.getUserId());
						orderStatus.setOrderStatus("FINISHED");
						int orderCount = afOrderService.getOrderCountByStatusAndUserId(orderStatus);
						logger.info("/h5GgActivity/returnCoupon orderCount = {}"+orderCount+",userId = {}"+userId);
						if (orderCount <= 0) {
							returnCouponVo.setStatus(H5GgActivity.NO_FINISH.getDescription());
						} else {
							returnCouponVo.setStatus(H5GgActivity.ALREADY_FINISH.getDescription());
							// 查询该优惠券金额
							AfBoluomeUserCouponDo queryUserCoupon = new AfBoluomeUserCouponDo();
							queryUserCoupon.setUserId(uDo.getParentId());
							queryUserCoupon.setRefId(uDo.getUserId());
							queryUserCoupon.setChannel(H5GgActivity.RECOMMEND.getCode());

							AfBoluomeUserCouponDo userCoupon = afBoluomeUserCouponService
									.getUserCouponByUerIdAndRefIdAndChannel(queryUserCoupon);
							logger.info("/h5GgActivity/returnCoupon userCoupon = {}"+userCoupon+",userId = {}"+userId);
							if (userCoupon != null) {
								long couponId = userCoupon.getCouponId();
								//AfResourceDo rDo = afResourceService.getResourceByResourceId(couponId);
								// if (rDo != null) {
								// returnCouponVo.setReward(rDo.getName());
								// }
								// 通过af_resoource 获取url，再调用菠萝觅接口,获取对应金额
								try {
									AfResourceDo afResourceDo = afResourceService.getResourceByResourceId(couponId);
									logger.info("/h5GgActivity/returnCoupon afResourceDo = {},"+userCoupon+"couponId = {}"+couponId);
									if (afResourceDo != null) {
										BigDecimal money = new BigDecimal(String.valueOf(afResourceDo.getPic1()));
										returnCouponVo.setReward(money + "元外卖券");
										couponAmount = couponAmount.add(money);
									}
								} catch (Exception e) {
									logger.error("/h5GgActivity/returnCoupon get boluome activity inviteAmount error", e.getStackTrace());
								}
							}

						}
					}
					returnCouponList.add(returnCouponVo);
				} // for
			} // if

			String  activityTime = null;
			    AfResourceDo activityStart = new AfResourceDo();
				   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
				   activityStart = list.get(0);
				    if(activityStart !=null){
					activityTime = activityStart.getValue();
                           }
			
			// 好友借钱邀请者得到的奖励总和 inviteAmount af_recommend_money表
			inviteAmount = new BigDecimal(afRecommendUserService.getSumPrizeMoney(userId,activityTime));

			vo.setReturnCouponList(returnCouponList);
			vo.setInviteAmount(inviteAmount);
			vo.setCouponAmount(couponAmount);
			resultStr = H5CommonResponse.getNewInstance(true, "获取返券列表成功", null, vo).toString();
		} catch (Exception e) {
			logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取返券列表失败").toString();

		}
		return resultStr;

	}
	
	/** 
	* author chenqiwei
	* @Title: inviteFriend 
	* @Description: 邀请好友（吃霸王餐）
	* @param @param request
	* @param @param response
	* @param @param model
	* @param @return
	* @param @throws IOException     
	* @return String    返回类型 
	* @throws 
	*/
	@RequestMapping(value = "/inviteFriend", method = RequestMethod.POST)
	public String inviteFriend(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		// String userName =
		// ObjectUtils.toString(request.getParameter("userName"),
		// "").toString();
		FanbeiWebContext context = new FanbeiWebContext();
		String resultStr = "";
		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());
			BoluomeActivityInviteFriendVo inviteFriendVo = new BoluomeActivityInviteFriendVo();
			AfResourceDo resourceInfo = new AfResourceDo();

			resourceInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),
					H5GgActivity.DINE_AND_DASH.getCode());
			// 未登录时初始化一些数据
			if (resourceInfo != null) {
				inviteFriendVo.setImage(resourceInfo.getValue());
				// inviteFriendVo.setText(text);
				inviteFriendVo.setActivityRule(resourceInfo.getValue1());
				inviteFriendVo.setExample(resourceInfo.getValue2());

			}
			if (userId == null) {
				return H5CommonResponse.getNewInstance(true, "获取邀请好友吃霸王餐页面信息成功", null, inviteFriendVo).toString();
			}

			// 登录时返回数据
			AfUserDo uDo = afUserService.getUserById(userId);
			    if (uDo != null) {
				  //用户的邀请码
				        String invitationCode=uDo.getRecommendCode();
				        if(invitationCode.equals("0")){
				            //生成邀请码
				            AfUserDo userDo = new AfUserDo();
					    Long invteLong = Constants.INVITE_START_VALUE + userId;
					    String inviteCode = Long.toString(invteLong, 36);
					    userDo.setRecommendCode(inviteCode);
					    userDo.setRid(userId);
					    afUserService.updateUser(userDo);
					    invitationCode = inviteCode;
				        }
					inviteFriendVo.setInviteCode(invitationCode);
			}

			resultStr = H5CommonResponse.getNewInstance(true, "获取邀请好友吃霸王餐页面信息成功", null, inviteFriendVo).toString();
		} catch (Exception e) {
			logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取邀请好友吃霸王餐页面信息失败").toString();
		}
		return resultStr;

	}

	
	/** 
	* author chenqiwei
	* @Title: inviteCeremony 
	* @Description: 邀请有礼（邀请好友拿奖励）
	* @param @param request
	* @param @param response
	* @param @param model
	* @param @return
	* @param @throws IOException     
	* @return String    返回类型 
	* @throws 
	*/
	@RequestMapping(value = "/inviteCeremony", method = RequestMethod.POST)
	public String inviteCeremony(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		// String userName =
		// ObjectUtils.toString(request.getParameter("userName"),
		// "").toString();
		FanbeiWebContext context = new FanbeiWebContext();
		String resultStr = "";
		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());
			BoluomeActivityInviteCeremonyVo inviteCeremonyVo = new BoluomeActivityInviteCeremonyVo();
			AfResourceDo resourceInfo = new AfResourceDo();

			resourceInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),
					H5GgActivity.INVITE_CERMONY.getCode());
			// 未登录时初始化一些数据
			if (resourceInfo != null) {
				AfResourceDo resource = afResourceService
						.getResourceByResourceId(Long.parseLong(resourceInfo.getValue1()));
				if (resource != null) {
					inviteCeremonyVo.setCouponAmount(resource.getPic1());
				}
				inviteCeremonyVo.setImage(resourceInfo.getValue());
				inviteCeremonyVo.setSpePreference(resourceInfo.getValue2());
				inviteCeremonyVo.setActivityRule(resourceInfo.getValue3());
				inviteCeremonyVo.setExample(resourceInfo.getValue4());
			}
			if (userId == null) {
				return H5CommonResponse.getNewInstance(true, "获取邀请有礼页面信息成功", null, inviteCeremonyVo).toString();
			}

			// 登录时返回数据
//			AfUserDo uDo = afUserService.getUserById(userId);
//			if (uDo != null) {
//				inviteCeremonyVo.setInviteCode(uDo.getRecommendCode());
//			}

			resultStr = H5CommonResponse.getNewInstance(true, "获取邀请有礼页面信息成功", null, inviteCeremonyVo).toString();
		} catch (Exception e) {
			logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取邀请有礼页面信息失败").toString();
		}
		return resultStr;

	}

	/**
	 * @author qiao
	 * @说明：逛逛活动点亮过程中的领券
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCouponV1(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			FanbeiWebContext context = new FanbeiWebContext();
			context = doWebCheck(request, false);
			String userName = context.getUserName();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
			}

			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");

			if ("10222".equals(code) || "10206".equals(code) || "11206".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您已领过优惠券，快去使用吧~").toString();
			} else if ("10305".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您下手慢了哦，优惠券已领完，下次再来吧").toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(true, resultJson.getString("msg")).toString();
			}
			// 存入数据库
			AfBoluomeUserCouponDo userCoupon = new AfBoluomeUserCouponDo();
			userCoupon.setChannel(H5GgActivity.PICK.getCode());
			userCoupon.setCouponId(sceneId);
			userCoupon.setUserId(afUserDo.getRid());
			userCoupon.setStatus(1);
			int result = afBoluomeUserCouponService.saveRecord(userCoupon);
			if (result == 0) {
				logger.info("pickBoluomeCoupon boluome and save userCoupon fail userCoupon = {},",
						JSONObject.toJSONString(userCoupon));
			}

			// bizCacheUtil.saveObject("boluome:coupon:"+resourceInfo.getRid()+afUserDo.getUserName(),"Y",2*Constants.SECOND_OF_ONE_MONTH);
			return H5CommonResponse.getNewInstance(true, "恭喜您领券成功").toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}

	/**
	 * 
	 * @Title: homePage @author qiao @date 2017年11月16日
	 *         下午5:29:58 @Description: @param request @param
	 *         response @return @return String @throws
	 */
	@RequestMapping(value = "/homePage", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String homePage(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = H5CommonResponse.getNewInstance(false, "初始化数据失败");
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			String userName = context.getUserName();

			String log = String.format("/homePage userName = %s", userName);
			logger.info(log);
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "HOME_PAGE_INFO");
			AfResourceDo resourceInfo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "HOME_PAGE_RULE");
			log = log + String.format("middle business params: resourceDo = %s", resourceDo.toString());

			logger.info(log);
			logger.info("/h5GgActivity/homePage resourceInfo = {}",resourceInfo);
			Map<String, Object> data = new HashMap<>();
			if (resourceDo != null) {
				// if the user has not login
				// String ruleDescript = resourceDo.getDescription();
				String image = resourceDo.getValue();
				List<Object> resultList = new ArrayList<>();
				Map<String, Object> map = new HashMap<>();
				map.put("name", resourceDo.getValue1());
				map.put("value", resourceDo.getValue2());
				resultList.add(map);
				Map<String, Object> map1 = new HashMap<>();
				map1.put("name", resourceDo.getValue3());
				map1.put("value", resourceDo.getValue4());
				resultList.add(map1);
				  AfResourceDo do1 = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
				// the dark list
				List<AfCardDo> cardList = new ArrayList<>();
				AfBoluomeActivityItemsDo t = new AfBoluomeActivityItemsDo();
				//t.setStatus("O");
				t.setBoluomeActivityId(1000L);

				List<AfBoluomeActivityItemsDo> itemsList = afBoluomeActivityItemsService.getListByCommonCondition(t);
				String activityTime = null;
				
				if (itemsList != null && itemsList.size() > 0) {
					cardList = convertItemsListToCardList(itemsList, false);

					// if the user has already login
				if (userName != null) {
						Long userId = convertUserNameToUserId(userName);
						if (userId != null) {

							//AfResourceDo do1 = afResourceService.getConfigByTypesAndSecType("GG_TWICE_LIGHT", "GET_START_TIME");
						  
						    logger.info("/h5GgActivity/homePage do1 = {}",do1);
							if(do1 != null){
								String startTime = do1.getValue();
								if (StringUtil.isNotBlank(startTime)) {

								List<AfBoluomeRebateDo> rebateList = new ArrayList<>();
								//在活动时间之后
								rebateList = afBoluomeRebateService.getListByUserId(userId,startTime);
								// the status of items
								logger.info("/h5GgActivity/homePage rebateList = {}",rebateList);
								//List<AfCardDo> cardsList = convertItemsListToCardList(rebateList, itemsList,userId);
								
								List<AfCardDo> cardsList = afBoluomeActivityItemsService.getUserCards(userId,startTime);
								
								if (cardsList != null && cardsList.size() > 0) {
									cardList = cardsList;
								} else {
									cardList = convertItemsListToCardList(itemsList, true);
								}
								// the rebate stuff
								logger.info("/h5GgActivity/homePage cardsList = {}",cardsList);
								List<AfRebateDo> rebateeList = afBoluomeRebateService.getRebateList(userId,startTime);
								logger.info("/h5GgActivity/homePage rebateeList = {}",rebateeList);
								BigDecimal totalRebate = getTotalRebate(rebateeList);
								data.put("totalRebate", totalRebate);
								data.put("rebateList", rebateeList);
								}
							}


						}
					}
					Long shopId = afShopService.getWaiMainShopId();
					data.put("waiMaiShopId", shopId);
					data.put("image", image);
					data.put("resultList", resultList);
					data.put("cardList", cardList);

					resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data);
					log = log + String.format("response: resultStr = %s", resultStr);
					logger.info(log);

				}
			}
			if (resourceInfo != null) {
				String ruleDescript = resourceInfo.getValue2();
				String popupDescript = resourceInfo.getValue3();
				data.put("ruleDescript", ruleDescript);
				data.put("popupDescript", popupDescript);
			}

			return resultStr.toString();
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data);
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("/homePage resultStr = {}", resultStr);
			logger.error("/homePage 初始化数据失败  e = {} , resultStr = {}", e, resultStr);
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage());
			logger.error("/homePage 初始化数据失败  e = {} , resultStr = {}", exception, resultStr);
		}

		return resultStr.toString();
	}

	/**
	 * 
	 * @说明：获得用户优惠券列表
	 * @param: @return
	 * @return: String
	 */
	private static String couponUrl = null;

	private static String getCouponUrl() {
		if (couponUrl == null) {
			couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
			return couponUrl;
		}
		return couponUrl;
	}

	/**
	 * 
	 * @Title: boluomeCoupon @author qiao @date 2017年11月17日
	 *         下午2:58:57 @Description: 优惠券展示接口 @param request @param
	 *         response @return @return String @throws
	 */
	@RequestMapping(value = "/boluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String boluomeCoupon(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = H5CommonResponse.getNewInstance(false, "初始化数据失败");
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			List<BoluomeCouponResponseBo> boluomeCouponList = new ArrayList<>();

			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "BOLUOME_COUPON");
			if (resourceDo != null) {
				List<String> bList = new ArrayList<>();
				bList.add(resourceDo.getValue2());
				bList.add(resourceDo.getValue3());
				if (bList != null && bList.size() > 0) {
					for (String resouceIdStr : bList) {
						Long resourceId = Long.parseLong(resouceIdStr);
						AfResourceDo couponResourceDo = afResourceService.getResourceByResourceId(resourceId);
						logger.info("boluomeCoupon  resourceId = {},couponResourceDo = {}", resourceId,
								couponResourceDo);
						if (couponResourceDo != null) {
							String uri = couponResourceDo.getValue();
							String name = couponResourceDo.getName();
							String[] pieces = uri.split("/");
							if (pieces.length > 9) {
								String app_id = pieces[6];
								String campaign_id = pieces[8];
								String user_id = "0";
								// 获取boluome的券的内容
								String url = getCouponUrl() + "?" + "app_id=" + app_id + "&user_id=" + user_id
										+ "&campaign_id=" + campaign_id;
								String reqResult = HttpUtil.doGet(url, 10);
								logger.info("initHomePage getCouponUrl reqResult = {}", reqResult);
								if (!StringUtil.isBlank(reqResult)) {
									ThirdResponseBo thirdResponseBo = JSONObject.parseObject(reqResult,
											ThirdResponseBo.class);
									if (thirdResponseBo != null && "0".equals(thirdResponseBo.getCode())) {
										List<BoluomeCouponResponseParentBo> listParent = JSONArray.parseArray(
												thirdResponseBo.getData(), BoluomeCouponResponseParentBo.class);
										if (listParent != null && listParent.size() > 0) {
											BoluomeCouponResponseParentBo parentBo = listParent.get(0);
											if (parentBo != null) {
												String activityCoupons = parentBo.getActivity_coupons();
												String result = activityCoupons.substring(1,
														activityCoupons.length() - 1);
												String replacement = "," + "\"sceneId\":" + resourceId + "}";
												String rString = result.replaceAll("}", replacement);
												// 字符串转为json对象
												BoluomeCouponResponseBo BoluomeCouponResponseBo = JSONObject
														.parseObject(rString, BoluomeCouponResponseBo.class);
												String userName = context.getUserName();
												// 为了兼容从我也要点亮中调用主页接口
												if (StringUtil.isBlank(userName)) {
													userName = request.getParameter("userName");
												}
												List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
														.getActivityCouponList(uri);
												if (userName != null) {
													Long userId = convertUserNameToUserId(userName);
													if (userId != null) {
														// 判断用户是否拥有该优惠券
														// 或者已经被领取完毕
														Integer flag = afBoluomeUserCouponService
																.isHasCouponInDb(userId, resourceId);
														if (flag != 0 && flag != null) {
															BoluomeCouponResponseBo.setIsHas(YesNoStatus.YES.getCode());
														} else {
															BoluomeCouponResponseBo.setIsHas(YesNoStatus.NO.getCode());
														}
													}
												}
												BoluomeCouponResponseBo.setName(name);
												boluomeCouponList.add(BoluomeCouponResponseBo);
												Map<String, Object> data = new HashMap<>();
												data.put("boluomeCouponList", boluomeCouponList);
												resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data);

											}
										}

									}
								}
							}
						}
					}
				}
			}

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data);
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("/boluomeCoupon resultStr = {}", resultStr);
			logger.error("/homePage 初始化数据失败  e = {} , resultStr = {}", e, resultStr);
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage());
			logger.error("/boluomeCoupon 初始化数据失败  e = {} , resultStr = {}", exception, resultStr);
		}

		return resultStr.toString();
	}

	/** 
	* author chenqiwei
	* @Title: popUp 
	* @Description: 活动首页弹窗(完成)
	* @param @param request
	* @param @param response
	* @param @param model
	* @param @return
	* @param @throws IOException     
	* @return String    返回类型 
	* @throws 
	*/
	@RequestMapping(value = "/popUp", method = RequestMethod.POST)
	public String popUp(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
		FanbeiWebContext context = new FanbeiWebContext();
		String resultStr = "";
		try {
			context = doWebCheck(request, false);
			Long userId = convertUserNameToUserId(context.getUserName());
			// 未登录时不弹窗
			BoluomeActivityPoPupVo poPupVo = new BoluomeActivityPoPupVo();
			poPupVo.setCouponToPop(H5GgActivity.NOPOPUP.getCode());
			poPupVo.setRebateToPop(H5GgActivity.NOPOPUP.getCode());
			if (userId == null) {
				return H5CommonResponse.getNewInstance(true, "获取弹窗信息成功", null, poPupVo).toString();
			}
			// 登录时返回数据
			//忽略手动领券
			Long newUser = null ;
			Long inviter = null ;
			AfResourceDo coupon = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),
				H5GgActivity.BOLUOME_COUPON.getCode());
			try{
        			if(coupon !=null){
        			    newUser = Long.parseLong(coupon.getValue()) ;
        			    inviter =  Long.parseLong(coupon.getValue1());
        			}
			}catch(Exception e){
			    logger.error("coupon get value error", e.getStackTrace());
			}
			AfBoluomeUserCouponDo userCouponDo = afBoluomeUserCouponService.getLastUserCouponByUserIdSentCouponId(userId,newUser,inviter);
			//AfBoluomeUserCouponDo userCouponDo = afBoluomeUserCouponService.getLastUserCouponByUserId(userId);
			// mqp:modify to the highest rebate that never popped up before
			//AfBoluomeRebateDo userRebateDo = afBoluomeRebateService.getHighestNeverPopedRebate(userId);
			AfBoluomeRebateDo lastUserRebateDo = afBoluomeRebateService.getLastUserRebateByUserId(userId);
			// AfBoluomeRebateDo userRebateDo =
			// afBoluomeRebateService.getLastUserRebateByUserId(userId);

			AfBoluomeActivityMsgIndexDo afBoluomeActivityMsgIndexDo = new AfBoluomeActivityMsgIndexDo();
			// ---------------------------------------------------优惠券------------------------------------------
			// 该用户获得最后一张优惠券是否有记录
			if (userCouponDo != null) {
				// 设置金额
				Long couponId = userCouponDo.getCouponId();
				AfResourceDo couponInfo = afResourceService.getResourceByResourceId(couponId);
				if (couponInfo != null) {
					logger.error("popUp couponInfo = {},userId = {}", couponInfo, userId);
					poPupVo.setCouponAmount(couponInfo.getPic1());
				}
				// 设置图片
				AfResourceDo imageInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),
						H5GgActivity.COUPON_IMAGE.getCode());
				
				if (imageInfo != null) {
					logger.error("popUp coupon imageInfo = {}", imageInfo);
					if(newUser.longValue() == couponId.longValue() ){
					    poPupVo.setCouponImage(imageInfo.getValue());
					}else{
					    poPupVo.setCouponImage(imageInfo.getValue1());
					}
					
				}
				// 是否有弹窗记录
				AfBoluomeActivityMsgIndexDo msgIndexDo = afBoluomeActivityMsgIndexService.getByUserId(userId);
				if (msgIndexDo == null) {
					// 没有记录，设置弹窗并添加记录到db
					poPupVo.setCouponToPop(H5GgActivity.TOPOPUP.getCode());
					afBoluomeActivityMsgIndexDo.setCouponIndex(userCouponDo.getRid());
					afBoluomeActivityMsgIndexDo.setUserId(userId);
					afBoluomeActivityMsgIndexService.saveRecord(afBoluomeActivityMsgIndexDo);
				} else if (msgIndexDo != null) {
					// 有记录。couponId = coupon_index ?
					if (msgIndexDo.getCouponIndex() == null) {
						poPupVo.setCouponToPop(H5GgActivity.TOPOPUP.getCode());
						afBoluomeActivityMsgIndexDo.setCouponIndex(userCouponDo.getRid());
						afBoluomeActivityMsgIndexDo.setUserId(userId);
						afBoluomeActivityMsgIndexDo.setRid(msgIndexDo.getRid());
						afBoluomeActivityMsgIndexDo.setGmtModified(new Date());
						afBoluomeActivityMsgIndexService.updateById(afBoluomeActivityMsgIndexDo);
					} else if (msgIndexDo.getCouponIndex() != null) {
						if (msgIndexDo.getCouponIndex() < userCouponDo.getRid()) {
							poPupVo.setCouponToPop(H5GgActivity.TOPOPUP.getCode());
							afBoluomeActivityMsgIndexDo.setCouponIndex(userCouponDo.getRid());
							afBoluomeActivityMsgIndexDo.setUserId(userId);
							afBoluomeActivityMsgIndexDo.setRid(msgIndexDo.getRid());
							afBoluomeActivityMsgIndexDo.setGmtModified(new Date());
							afBoluomeActivityMsgIndexService.updateById(afBoluomeActivityMsgIndexDo);
						}
					}

				}
			}

			// ---------------------------------------返利-----------------------------------------------------
			// 该用户获得最后返利是否有记录
			if (lastUserRebateDo != null) {
			    //获取弹窗的返利记录。如果是空设置为0
			    AfBoluomeActivityMsgIndexDo msgIndexDo = afBoluomeActivityMsgIndexService.getByUserId(userId);
			    long  rebateIndex =0;
			    if(msgIndexDo != null){
				if(msgIndexDo.getRebateIndex() != null){
				    rebateIndex = msgIndexDo.getRebateIndex();
				    if(lastUserRebateDo.getRid() <= rebateIndex){
					//不弹窗
					return resultStr = H5CommonResponse.getNewInstance(true, "获取弹窗信息成功", null, poPupVo).toString();
				    }
				    
				}
			    }
			    //否则弹窗，设置弹窗信息并保存记录
			    //范围内最大的记录
			    AfBoluomeRebateDo maxUserRebateDo = afBoluomeRebateService.getMaxUserRebateByStartIdAndEndIdAndUserId(rebateIndex,lastUserRebateDo.getRid(),userId);
			    
			      // 设置金额
			        if(maxUserRebateDo !=null ){
        				String rebateAmount = maxUserRebateDo.getRebateAmount().toString();
        				logger.error("popUp rebateAmount = {},userId = {}", rebateAmount, userId);
        				poPupVo.setRebateAmount(rebateAmount);
        				// 设置图片
			        }
				AfResourceDo imageInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),
						H5GgActivity.REBATE_IMAGE.getCode());
				if (imageInfo != null) {
					logger.error("popUp rebate imageInfo = {}", imageInfo);
					poPupVo.setRebateImage(imageInfo.getValue());
				}
				// 设置场景名
				long orderId = maxUserRebateDo.getOrderId();
				AfBoluomeActivityItemsDo itemsInfo = afBoluomeActivityItemsService.getItemsInfoByOrderId(orderId);
				if (itemsInfo != null) {
					poPupVo.setSceneName(itemsInfo.getName());
				}
				afBoluomeActivityMsgIndexDo.setRebateIndex(lastUserRebateDo.getRid());
				afBoluomeActivityMsgIndexDo.setUserId(userId);
				if(msgIndexDo == null){
				    afBoluomeActivityMsgIndexService.saveRecord(afBoluomeActivityMsgIndexDo);
				}else{
				    afBoluomeActivityMsgIndexDo.setGmtModified(new Date());
				    afBoluomeActivityMsgIndexDo.setRid(msgIndexDo.getRid());
				    afBoluomeActivityMsgIndexService.updateById(afBoluomeActivityMsgIndexDo);
				}
				//弹窗
				poPupVo.setRebateToPop(H5GgActivity.TOPOPUP.getCode());
				resultStr = H5CommonResponse.getNewInstance(true, "获取弹窗信息成功", null, poPupVo).toString();
			}else{
			    return resultStr = H5CommonResponse.getNewInstance(true, "获取弹窗信息成功", null, poPupVo).toString();
			}

			
		} catch (Exception e) {
			logger.error("/h5GgActivity/popUp" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取弹窗信息失败").toString();
		}
		return resultStr;

	}

	private String changePhone(String userName) {
		String newUserName = "";
		if (!StringUtil.isBlank(userName)) {
			newUserName = userName.substring(0, 3);
			newUserName = newUserName + "****";
			newUserName = newUserName + userName.substring(7, 11);
		}
		return newUserName;
	}

	private BigDecimal getTotalRebate(List<AfRebateDo> rebateList) {
		BigDecimal result = BigDecimal.ZERO;
		if (rebateList != null && rebateList.size() > 0) {
			for (AfRebateDo rebateDo : rebateList) {
				result = result.add(rebateDo.getSceneRebate()).add(rebateDo.getSurpriseRebate());
			}
		}
		return result;
	}

	private List<AfCardDo> convertItemsListToCardList(List<AfBoluomeActivityItemsDo> itemsList, boolean isDark) {

		logger.info("convertItemsListToCardList params :　itemsList.size() = {} , isDark = {}",
				new Object[] { itemsList.size(), isDark });
		List<AfCardDo> resultList = new ArrayList<>();
		if (itemsList != null && itemsList.size() > 0) {
			for (AfBoluomeActivityItemsDo itemsDo : itemsList) {
				AfCardDo cardDo = changeFromItemsDo(itemsDo, isDark);
				resultList.add(cardDo);
			}
		}
		logger.info("convertItemsListToCardList params :　itemsList.size() = {} , isDark = {} result.size() = {} ",
				new Object[] { itemsList.size(), isDark, resultList.size() });
		return resultList;
	}

	private AfCardDo changeFromItemsDo(AfBoluomeActivityItemsDo itemsDo, boolean isDark) {
		logger.info("changeFromItemsDo params :　itemsDo = {} , isDark = {} ",
				new Object[] { itemsDo.toString(), isDark });
		AfCardDo cardDo = new AfCardDo();
		cardDo.setActivityId(itemsDo.getBoluomeActivityId());

		cardDo.setImage(itemsDo.getIconUrl());
		if (isDark) {
			cardDo.setImage(itemsDo.getLogo());
		}
		cardDo.setName(itemsDo.getName());
		cardDo.setRid(itemsDo.getRid());
		cardDo.setSort(itemsDo.getSort());
		cardDo.setShopId(itemsDo.getRefId());
		logger.info("changeFromItemsDo params :　itemsDo = {} , isDark = {} result cardDo = {} ",
				new Object[] { itemsDo.toString(), isDark, cardDo.toString() });
		return cardDo;
	}

	private List<AfCardDo> convertItemsListToCardList(List<AfBoluomeRebateDo> rebateList,
			List<AfBoluomeActivityItemsDo> itemsList,Long userId) {
		String log = String.format("convertItemsListToCardList params : rebateList.size() = %s , itemsList.size() = %s ", rebateList.size(),itemsList.size());
		logger.info(log);
		String activityTime = null;
		 AfResourceDo do1 = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
			if( do1!=null){
			    activityTime =  do1.getValue();
			}
		List<AfCardDo> resultList = new ArrayList<>();
		if (itemsList != null && itemsList.size() > 0 ) {
			for (AfBoluomeActivityItemsDo itemsDo : itemsList) {
				Long shopId = itemsDo.getRefId();
				int isHave = afBoluomeRebateService.getRebateCount(shopId,userId,activityTime);
				AfCardDo cardDo = new AfCardDo();
				if (isHave != 0) {
					//isDark = false
					cardDo = changeFromItemsDo(itemsDo, false);
				}else{
					//isDark = true
					cardDo = changeFromItemsDo(itemsDo, true);
				}
				resultList.add(cardDo);
			}
		}
		log = log + String.format("resultList.size() = %s  ", resultList.size());
		logger.info(log);
		return resultList;
	}

	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}
		}
		logger.info("convertUserNameToUserId params userName = {} , userId = {}", userName, userId);
		return userId;
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
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

}

