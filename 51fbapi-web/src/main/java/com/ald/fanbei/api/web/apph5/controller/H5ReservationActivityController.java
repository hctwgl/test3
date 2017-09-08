package com.ald.fanbei.api.web.apph5.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsReservationStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.GoodsReservationWebFailStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.OrderNoUtils;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述:iphone8-预约
 * @author fanmanfu 创建时间：2017年9月8日 上午10:49:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/activity")
public class H5ReservationActivityController extends BaseController {

	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfGoodsService afGoodsService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfUserService afUserService;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	private AfGoodsReservationService afGoodsReservationService;

	@RequestMapping(value = "/getActivityGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getActivityGoods() {
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceSecType.Iphone8ReservationActivity.getCode());
		String value1 = resource.getValue();
		List<Long> list = (List<Long>) JSONArray.parse(value1);
		List<AfGoodsDo> afGoodsDo = afGoodsService.getAfGoodsDo(list);
		Map map = new HashMap();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = DateUtil.parseDateyyyyMMddHHmmss(sdf.format(date));
		System.out.println(currentDate);
		System.out.println("=====");
		List<String> winUsers = (List<String>) bizCacheUtil.getObject("winUser");
		map.put("currentDate", date);
		map.put("winUsers", winUsers);
		map.put("row", afGoodsDo);
		return JsonUtil.toJSONString(map);
	}

	@RequestMapping(value = "/reservationActivityDate", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String reservationActivityDate(HttpServletRequest request, ModelMap model) {
		try {
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			AfUserDo afUserDo = afUserService.getUserByUserName(userName);
			Map<String, Object> returnData = new HashMap<String, Object>();

			Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), null);
			Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), null);
			Long rsvNums = 1L;
			// 预约成功后发送短信开关 Y发送 N不发送
			String sendMsgStatus = "";
			String sendMsgInfo = "";
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceSecType.Iphone8ReservationActivity.getCode());
			// 解析对应配置并校验
			Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(resource.getValue3());
			sendMsgStatus = StringUtil.null2Str(jsonObjRes.get("sendMsgStatus"));
			sendMsgInfo = StringUtil.null2Str(jsonObjRes.get("sendMsgInfo"));
     
			AfResourceDo currActivityResource = afResourceService.getResourceByResourceId(activityId);
			if (currActivityResource == null) {
				returnData.put("status", GoodsReservationWebFailStatus.ReservationActNotExist.getCode());
				return H5CommonResponse.getNewInstance(false, GoodsReservationWebFailStatus.ReservationActNotExist.getName(), "", returnData).toString();
			}

			String rsvNo = OrderNoUtils.getInstance().getSerialNumber();
			AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(afUserDo.getRid(), activityId, goodsId, rsvNums, rsvNo, new Date(), new Date(), AfGoodsReservationStatus.SUCCESS.getCode(), "");

			Integer revCountNums = afGoodsReservationService.getRevCountNumsByQueryCondition(afGoodsReservationDo);
			if (revCountNums > 0) {
				// 同活动同商品只允许一次预约
				logger.warn("用户预约商品次数超限,预约失败。userId:" + afUserDo.getRid() + ",activityId:" + activityId + ",goodsId" + goodsId + ",revCountNums" + revCountNums);
				returnData.put("status", GoodsReservationWebFailStatus.ReservationTimesOverrun.getCode());
				return H5CommonResponse.getNewInstance(false, GoodsReservationWebFailStatus.ReservationTimesOverrun.getName(), "", returnData).toString();
			}

			if (!(afGoodsReservationService.addGoodsReservation(afGoodsReservationDo) > 0)) {
				returnData.put("status", GoodsReservationWebFailStatus.ReservationFail.getCode());
				return H5CommonResponse.getNewInstance(false, GoodsReservationWebFailStatus.ReservationFail.getName(), "", returnData).toString();
			}

			// 预约成功，短信通知
			if (StringUtil.isBlank(sendMsgStatus) || sendMsgStatus.equals(YesNoStatus.YES.getCode())) {
				try {
					boolean result = smsUtil.sendGoodsReservationSuccessMsg(afUserDo.getMobile(), sendMsgInfo);
					if (result == false) {
						logger.error("活动产品预约成功消息通知发送失败userId：" + afUserDo.getRid());
					}
				} catch (Exception e) {
					logger.error("活动产品预约成功消息通知异常userId：" + afUserDo.getRid() + ",", e);
				}
			}
			returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", returnData).toString();
		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, GoodsReservationWebFailStatus.ReservationFail.getName(), "", null).toString();
		}
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
