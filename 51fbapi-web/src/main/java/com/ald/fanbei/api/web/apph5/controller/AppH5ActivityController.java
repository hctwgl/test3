package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.URIDereferencer;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfGoodsReservationStatus;
import com.ald.fanbei.api.common.enums.AfGoodsSource;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.GoodsReservationWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.OrderNoUtils;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsVo;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.util.URI;

/**
 * @类描述 h5活动-预约等
 * @author chengkang 2017年4月5日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/activity/")
@SuppressWarnings("unchecked")
public class AppH5ActivityController extends BaseController {
	String opennative = "/fanbei-web/opennative?name=";
	private static Integer PAGE_SIZE = 20;

	@Resource
	AfGoodsReservationService afGoodsReservationService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfUserService afUserService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	private AfBorrowCashService afBorrowCashService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	JpushService jpushService;
	
	
	
	@ResponseBody
	@RequestMapping(value = "/reserveActivityGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String reserveActivityGoods(HttpServletRequest request,
			ModelMap model) throws IOException {
		try {
			String userName = ObjectUtils.toString(
					request.getParameter("userName"), "").toString();
			AfUserDo afUserDo = afUserService.getUserByUserName(userName);
			Map<String, Object> returnData = new HashMap<String, Object>();

			Long activityId = NumberUtil.objToLongDefault(
					request.getParameter("activityId"), null);
			Long goodsId = NumberUtil.objToLongDefault(
					request.getParameter("goodsId"), null);
			// Long rsvNums =
			// NumberUtil.objToLongDefault(request.getParameter("rsvNums"), 1L);
			Long rsvNums = 1L;
			// 预约成功后发送短信开关 Y发送 N不发送
			String sendMsgStatus = "";
			String sendMsgInfo = "";
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties
						.get(Constants.CONFKEY_NOTIFY_HOST)
						+ opennative
						+ H5OpenNativeType.AppLogin.getCode();
				returnData.put("status",
						GoodsReservationWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse.getNewInstance(false,
						GoodsReservationWebFailStatus.UserNotexist.getName(),
						notifyUrl, returnData).toString();
			}

			// 前端没传递时，活动相关设置
			if (activityId == null || goodsId == null) {
				// 默认走的是oppo预约,获取配置中的oppo活动信息
				AfResourceDo activityResource = afResourceService
						.getConfigByTypesAndSecType(
								AfResourceType.ReservationActivity.getCode(),
								AfResourceSecType.OppoReservationActivity
										.getCode());
				if (activityResource == null
						|| StringUtil.isEmpty(activityResource.getValue3())) {
					returnData
							.put("status",
									GoodsReservationWebFailStatus.ReservationConfigInvalid
											.getCode());
					return H5CommonResponse
							.getNewInstance(
									false,
									GoodsReservationWebFailStatus.ReservationConfigInvalid
											.getName(), "", returnData)
							.toString();
				}

				// 解析对应值
				Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject
						.parse(activityResource.getValue3());
				goodsId = NumberUtil.objToLongDefault(
						jsonObjRes.get("goodsId"), 0L);
				activityId = activityResource.getRid();
			}

			AfResourceDo currActivityResource = afResourceService
					.getResourceByResourceId(activityId);
			if (currActivityResource == null) {
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationActNotExist
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationActNotExist
								.getName(), "", returnData).toString();
			}

			// AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
			// if(afGoodsDo==null){
			// returnData.put("status",
			// GoodsReservationWebFailStatus.GoodsNotExist.getCode());
			// return H5CommonResponse
			// .getNewInstance(false,
			// GoodsReservationWebFailStatus.GoodsNotExist.getName(),
			// "",returnData )
			// .toString();
			// }

			if (!AfCounponStatus.O.getCode().equals(
					currActivityResource.getValue4())) {
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationClosed
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationClosed
								.getName(), "", returnData).toString();
			}
			// 解析对应配置并校验
			Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject
					.parse(currActivityResource.getValue3());
			Date startTime = DateUtil.parseDateyyyyMMddHHmmss(StringUtil
					.null2Str(jsonObjRes.get("startTime")));
			Date endTime = DateUtil.parseDateyyyyMMddHHmmss(StringUtil
					.null2Str(jsonObjRes.get("endTime")));
			sendMsgStatus = StringUtil
					.null2Str(jsonObjRes.get("sendMsgStatus"));
			sendMsgInfo = StringUtil.null2Str(jsonObjRes.get("sendMsgInfo"));

			// 活动开始结束校验
			Date currDate = new Date();
			if (!DateUtil.compareDate(currDate, startTime)) {
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationNotStart
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationNotStart
								.getName(), "", returnData).toString();
			}

			if (DateUtil.compareDate(currDate, endTime)) {
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationHaveFinish
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationHaveFinish
								.getName(), "", returnData).toString();
			}

			String rsvNo = OrderNoUtils.getInstance().getSerialNumber();
			AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(
					afUserDo.getRid(), activityId, goodsId, rsvNums, rsvNo,
					new Date(), new Date(),
					AfGoodsReservationStatus.SUCCESS.getCode(), "");

			Integer revCountNums = afGoodsReservationService
					.getRevCountNumsByQueryCondition(afGoodsReservationDo);
			if (revCountNums > 0) {
				// 同活动同商品只允许一次预约
				logger.warn("用户预约商品次数超限,预约失败。userId:" + afUserDo.getRid()
						+ ",activityId:" + activityId + ",goodsId" + goodsId
						+ ",revCountNums" + revCountNums);
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationTimesOverrun
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationTimesOverrun
								.getName(), "", returnData).toString();
			}

			if (!(afGoodsReservationService
					.addGoodsReservation(afGoodsReservationDo) > 0)) {
				returnData
						.put("status",
								GoodsReservationWebFailStatus.ReservationFail
										.getCode());
				return H5CommonResponse
						.getNewInstance(
								false,
								GoodsReservationWebFailStatus.ReservationFail
										.getName(), "", returnData).toString();
			}

			// 预约成功，短信通知
			if (StringUtil.isBlank(sendMsgStatus)
					|| sendMsgStatus.equals(YesNoStatus.YES.getCode())) {
				try {
					boolean result = smsUtil.sendGoodsReservationSuccessMsg(
							afUserDo.getMobile(), sendMsgInfo);
					if (result == false) {
						logger.error("活动产品预约成功消息通知发送失败userId："
								+ afUserDo.getRid());
					}
				} catch (Exception e) {
					logger.error("活动产品预约成功消息通知异常userId：" + afUserDo.getRid()
							+ ",", e);
				}
			}

			returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
			return H5CommonResponse.getNewInstance(true,
					FanbeiExceptionCode.SUCCESS.getDesc(), "", returnData)
					.toString();
		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false,
					GoodsReservationWebFailStatus.ReservationFail.getName(),
					"", null).toString();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/reserveActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String reserveActivityInfo(HttpServletRequest request, ModelMap model)
			throws IOException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		try {
			String userName = ObjectUtils.toString(
					request.getParameter("userName"), "").toString();
			AfUserDo afUserDo = afUserService.getUserByUserName(userName);

			Long activityId = 0L;
			Long goodsId = 0L;
			String isHaveReservationRecord = YesNoStatus.NO.getCode();
			String isCanReservation = YesNoStatus.NO.getCode();
			String activityStatus = AfCounponStatus.O.getCode();

			AfResourceDo activityResource = afResourceService
					.getConfigByTypesAndSecType(
							AfResourceType.ReservationActivity.getCode(),
							AfResourceSecType.OppoReservationActivity.getCode());
			if (activityResource == null
					|| StringUtil.isEmpty(activityResource.getValue3())) {
				returnData.put("status",
						GoodsReservationWebFailStatus.ReservationConfigInvalid
								.getCode());
				return H5CommonResponse.getNewInstance(
						false,
						GoodsReservationWebFailStatus.ReservationConfigInvalid
								.getName(), "", returnData).toString();
			}

			// 解析对应值
			Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject
					.parse(activityResource.getValue3());
			goodsId = NumberUtil
					.objToLongDefault(jsonObjRes.get("goodsId"), 0L);
			activityId = activityResource.getRid();
			activityStatus = activityResource.getValue4();

			String startTime = StringUtil.null2Str(jsonObjRes.get("startTime"));
			String endTime = StringUtil.null2Str(jsonObjRes.get("endTime"));

			if (afUserDo != null) {
				AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(
						afUserDo.getRid(), activityId, goodsId,
						AfGoodsReservationStatus.SUCCESS.getCode());
				Integer revCountNums = afGoodsReservationService
						.getRevCountNumsByQueryCondition(afGoodsReservationDo);
				if (revCountNums == 0) {
					isCanReservation = YesNoStatus.YES.getCode();
				} else {
					isHaveReservationRecord = YesNoStatus.YES.getCode();
				}
			}

			Date startTimeDate = DateUtil.parseDateyyyyMMddHHmmss(startTime);
			Date endTimeDate = DateUtil.parseDateyyyyMMddHHmmss(endTime);
			// 活动开关校验
			if (!AfCounponStatus.O.getCode().equals(
					activityResource.getValue4())) {
				isCanReservation = YesNoStatus.NO.getCode();
			}

			// 活动开始结束校验
			Date currDate = new Date();
			if (!DateUtil.compareDate(currDate, startTimeDate)) {
				isCanReservation = YesNoStatus.NO.getCode();
			}

			if (DateUtil.compareDate(currDate, endTimeDate)) {
				isCanReservation = YesNoStatus.NO.getCode();
			}

			returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
			returnData.put("activityId", activityId);
			returnData.put("activityStatus", activityStatus);
			returnData.put("startTime", startTime);
			returnData.put("endTime", endTime);
			returnData.put("goodsId", goodsId);
			returnData.put("isCanReservation", isCanReservation);
			returnData.put("isHaveReservationRecord", isHaveReservationRecord);

			return H5CommonResponse.getNewInstance(true,
					FanbeiExceptionCode.SUCCESS.getDesc(), "", returnData)
					.toString();
		} catch (Exception e) {
			returnData.put("status", FanbeiExceptionCode.FAILED.getCode());
			return H5CommonResponse.getNewInstance(false,
					FanbeiExceptionCode.FAILED.getCode(), "", returnData)
					.toString();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getSelfSupportGoodsInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getSelfSupportGoodsInfo(HttpServletRequest request,
			ModelMap model) throws IOException {
		Map<String, Object> returnData = new HashMap<String, Object>();
		String URL = URLDecoder.decode(request.getHeader("Referer"), "UTF-8");
		String appInfoStr = URL.substring(URL.indexOf("{"));
		JSONObject appInfo = JSONObject.parseObject(appInfoStr);
		Integer appVersion = Integer.parseInt(appInfo.get("appVersion")
				.toString());
		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)
				+ opennative + H5OpenNativeType.GoodsInfo.getCode();
		Integer pageNo = NumberUtil.objToIntDefault(
				request.getParameter("pageNo"), 1);
		try {
			AfGoodsQuery query = new AfGoodsQuery();
			query.setPageSize(PAGE_SIZE);
			query.setPageNo(pageNo);
			query.setSource(AfGoodsSource.SELFSUPPORT.getCode());
			query.setAppVersion(appVersion);
			// 获取自营商品信息列表
			List<AfGoodsDo> goodsDoList = afGoodsService
					.getCateGoodsList(query);
			List<AfGoodsVo> goodsList = getGoodsList(goodsDoList);
			returnData.put("goodsList", goodsList);
			returnData.put("notifyUrl", notifyUrl);

			returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
			return H5CommonResponse.getNewInstance(true,
					FanbeiExceptionCode.SUCCESS.getDesc(), "", returnData)
					.toString();
		} catch (Exception e) {
			returnData.put("status", FanbeiExceptionCode.FAILED.getCode());
			return H5CommonResponse.getNewInstance(false,
					FanbeiExceptionCode.FAILED.getCode(), "", returnData)
					.toString();
		}
	}

	/**
	 * 商品信息转换
	 * 
	 * @param goodsList
	 * @return
	 */
	private List<AfGoodsVo> getGoodsList(List<AfGoodsDo> goodsList) {
		List<AfGoodsVo> goodsVoList = new ArrayList<AfGoodsVo>();
		for (AfGoodsDo afGoods : goodsList) {
			AfGoodsVo vo = new AfGoodsVo();
			vo.setGoodsId(afGoods.getRid());
			vo.setPrivateGoodsId(afGoods.getRid());
			vo.setGoodsIcon(afGoods.getGoodsIcon());
			vo.setGoodsName(afGoods.getName());
			vo.setRemark(StringUtil.null2Str(afGoods.getRemark()));
			vo.setPriceAmount(afGoods.getPriceAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP)
					+ "");
			vo.setRealAmount(afGoods.getRealAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP)
					+ "");
			vo.setRebateAmount(afGoods.getRebateAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP)
					+ "");
			vo.setSaleAmount(afGoods.getSaleAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP)
					+ "");
			vo.setNumId(afGoods.getNumId());
			goodsVoList.add(vo);
		}
		return goodsVoList;
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#parseRequestData(java.lang.
	 * String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#doProcess(com.ald.fanbei.api
	 * .web.common.RequestDataVo, com.ald.fanbei.api.common.FanbeiContext,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		return null;
	}

	@RequestMapping(value ="/borrowCashActivities", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String borrowCashActivities(Model model) {
		try {
			BigDecimal sumAmount = (BigDecimal) bizCacheUtil
					.getObject("BorrowCash_Sum_Amount");
			if (sumAmount != null) {
				model.addAttribute("sumAmount", sumAmount+"");
				return sumAmount+"";
			}
		} catch (Exception e) {
			logger.info("borrowCashActivities redis get is fail" + e);
		}
		BigDecimal sumAmount = afBorrowCashService.getBorrowCashSumAmount();
		try {
			bizCacheUtil.saveObject("BorrowCash_Sum_Amount", sumAmount, 60*60*24*7);
		} catch (Exception e) {
			logger.info("borrowCashActivities redis save is fail" + e);
		}
		model.addAttribute("sumAmount", sumAmount+"");
		return sumAmount+"";
	}

	@RequestMapping(value ="/randomUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String randomUser(HttpServletRequest request,ModelMap model) {
		String winAmount = request.getParameter("winAmount");
		
		List<String> users = afBorrowCashService.getRandomUser();	//得到中奖用户id
		List<String> list1=afBorrowCashService.getNotRandomUser(users);//得到当天未中奖用户id
		List<String> userNames = afUserService.getUserNameByUserId(users);	//得到中奖用户user_name
		List<String> list2=afUserService.getUserNameByUserId(list1);
		String jsonString = JsonUtil.toJSONString(userNames);
		//每日中奖用户外推送
		for (String userName : userNames) {
			try{
				jpushService.pushBorrowCashActivitys(userName, winAmount,"Win");
			}catch(Exception e){
				logger.info(userName+"pushBorrowCashActivitys is fail," +e);
			}
		}
		//每日除中奖用户外全部用户推送
		for (String userName : list2) {
			try{
				jpushService.pushBorrowCashActivitys(userName, winAmount,"notWin");
			}catch(Exception e){
				logger.info(userName+"pushBorrowCashActivitys is fail," +e);
			}
		}
		//发送短信
		for (String userName : userNames) {
			try{
				smsUtil.sendBorrowCashActivitys(userName, "哇！幸运值爆棚的你在“破十亿”活动中获得***元现金红包，快去查收惊喜吧。回T退订");
			}catch(Exception e){
				logger.info("sendBorrowCashActivitys "+userName+" is fails," +e);
			}
		}
		//给用户账号打钱
		int amunt = Integer.parseInt(winAmount);
		afUserAccountService.updateBorrowCashActivity(amunt, users);
		//中奖用户存入缓存
		try{
			bizCacheUtil.saveObject(winAmount+"_Win_User", jsonString, 60*60*24*7);
		} catch (Exception e) {
			logger.info("randomUser redis save is fail,"+jsonString+"" + e);
		}
		/*if(winAmount.equals("600")){
			bizCacheUtil.saveObject("Six_Hundred_Win_User", jsonString, 60);
		} else if(winAmount.equals("700")){
			bizCacheUtil.saveObject("Seven_Hundred_Win_User", jsonString, 60);
		} else if(winAmount.equals("800")){
			bizCacheUtil.saveObject("Eight_Hundred_Win_User", jsonString, 60);
		} else if(winAmount.equals("900")){
			bizCacheUtil.saveObject("Nine_Hundred_Win_User", jsonString, 60);
		} else {
			bizCacheUtil.saveObject("Thousand_Win_User", jsonString, 60);
		}*/

		return jsonString;
	}

	@RequestMapping(value ="/getWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getWinUser(Model model) {
		try {
			String amount = (String) bizCacheUtil.getObject("600_Win_User");
			List<String> list = JSONObject.parseObject(amount, List.class);
			model.addAttribute("Six_Hundred", list);
		} catch (Exception e) {
			logger.error("600_Win_User get is fail");
		}
		try {
			String amount = (String) bizCacheUtil.getObject("700_Win_User");
			List<String> list = JSONObject.parseObject(amount, List.class);
			model.addAttribute("Seven_Hundred", list);
		} catch (Exception e) {
			logger.error("700_Win_User get is fail");
		}
		try {
			String amount = (String) bizCacheUtil.getObject("800_Win_User");
			List<String> list = JSONObject.parseObject(amount, List.class);
			model.addAttribute("Eight_Hundred", list);
			for (String string : list) {
				System.out.println(string);
			}
		} catch (Exception e) {
			logger.error("800_Win_User get is fail");
		}
		try {
			String amount = (String) bizCacheUtil.getObject("900_Win_User");
			List<String> list = JSONObject.parseObject(amount, List.class);
			model.addAttribute("Nine_Hundred", list);
		} catch (Exception e) {
			logger.error("900_Win_User get is fail");
		}
		try {
			String amount = (String) bizCacheUtil.getObject("1000_Win_User");
			List<String> list = JSONObject.parseObject(amount, List.class);
			model.addAttribute("Thousand", list);
		} catch (Exception e) {
			logger.error("1000_Win_User get is fail");
		}
	
		return null;
	}
	@RequestMapping(value ="/getBillionWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBillionWinUser(){
		String amount=null;
		try {
			 amount = (String) bizCacheUtil.getObject("Billion_Win_User");
		} catch (Exception e) {
			logger.error("Billion_Win_User get is fail");
		}
		return amount;
	}

}