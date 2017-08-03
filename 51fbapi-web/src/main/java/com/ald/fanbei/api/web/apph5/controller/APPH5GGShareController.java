package com.ald.fanbei.api.web.apph5.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeOrderSearchResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityResultService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityResultDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.BoluomeCouponForGGVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>Title:APPH5GGShareController <p>
 * <p>Description: <p>
 * @Copyright (c)  浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 * @author qiao
 * @date 2017年8月1日下午3:25:58
 *
 */
@Controller
@RequestMapping("/ggShare")
public class APPH5GGShareController extends BaseController{


	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfCouponService afCouponService;
	@Resource 
	AfBoluomeActivityService afBoluomeActivityService;
	@Resource 
	AfBoluomeActivityUserItemsService afBoluomeActivityUserItemsService;
	@Resource
	AfBoluomeActivityCouponService afBoluomeActivityCouponService;
	@Resource
	AfBoluomeActivityResultService afBoluomeActivityResultService;
	
	private static String couponUrl = null;
	
	/**
	 * 
	 * @说明：活动点亮初始化
	 * @param: @param request
	 * @param: @param response
	 * @param: @return
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/initHomePage",method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String initHomepage(HttpServletRequest request , HttpServletResponse response ){
		Calendar calStart = Calendar.getInstance();
		String resultStr = " ";
		FanbeiWebContext context = new FanbeiWebContext();
		try{
			//TODO:获取活动的id
			Long activityId = null;
			
			//TODO:获取登录着的userName或者id
			Long userId = null;
			
			//TODO:banner轮播图后台增加一个类型，和配置。GG_TOP_BANNER.根据类型和活动id去取。
			List<AfResourceDo> bannerList = new ArrayList<>();
			
			//TODO:resource+终极大奖的人数.初始化数据,根据类型和活动id去取。
			AfResourceDo fakeResourceDo = new AfResourceDo();
			//TODO:取得对应条件的实体
			
			String fakeFinalStr = fakeResourceDo.getValue1();
			Integer fakeFinalInt = new Integer(fakeFinalStr);
			
			AfBoluomeActivityResultDo t = new AfBoluomeActivityResultDo();
			t.setBoluomeActivityId(activityId);
			List<AfBoluomeActivityResultDo> listResult = afBoluomeActivityResultService.getListByCommonCondition(t);
			if (listResult != null && listResult.size() >0) {
				fakeFinalInt += listResult.size() ;
			}
			
			//TOOD:resource +表中获取参与人数（user_items）
			String fakeJoinStr = fakeResourceDo.getValue2();
			Integer fakeJoinInt = new Integer(fakeJoinStr);
			AfBoluomeActivityUserItemsDo itemsDo = new AfBoluomeActivityUserItemsDo();
			itemsDo.setBoluomeActivityId(activityId);
			List<AfBoluomeActivityUserItemsDo> listItems = afBoluomeActivityUserItemsService.getListByCommonCondition(itemsDo);
			if (listItems != null && listItems.size() > 0) {
				fakeJoinInt += listItems.size();
			}
			//TOOD:活动表和resource表中获取优惠券,信息
			AfBoluomeActivityCouponDo bDo = new AfBoluomeActivityCouponDo();
			bDo.setBoluomeActivityId(activityId);
			bDo.setStatus("O");
			bDo.setType("B");
			List<AfBoluomeActivityCouponDo> bList = afBoluomeActivityCouponService.getListByCommonCondition(bDo);
			if (bList != null && bList.size()>0) {
				for(AfBoluomeActivityCouponDo bCouponDo : bList){
					Long resourceId = bCouponDo.getCouponId();
					AfResourceDo couponResourceDo = afResourceService.getResourceByResourceId(resourceId);
					if (couponResourceDo != null ) {
						String uri = couponResourceDo.getValue();
						String[] pieces = uri.split("/");
						String app_id = pieces[6];
						String campaign_id = pieces[8];
						String user_id = "0";
						//获取boluome的券的内容
						String url = getCouponUrl()+"?"+"app_id"+app_id+"&user_id="+user_id+"&campaign_id="+campaign_id;
						String reqResult = HttpUtil.doGet(url, 10);
						JSONObject result = JSONObject.parseObject(reqResult);
						BoluomeCouponForGGVO couponForGGVO = new BoluomeCouponForGGVO();
						if ("1000".equals(result.getString("code"))) {
							BoluomeCouponForGGVO responseBo = JSONObject.parseObject(result.getString("data"),BoluomeCouponForGGVO.class);
							logger.info("");
						}
					}
				}
			}
			//TODO:活动和coupon表获取终极大奖的信息
			//TODO:活动的卡片
			//TODO:用户如果登录，则用户的该活动获得的卡片list
			//TODO:活动表活动规则
			
			
		}catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败","",e.getErrorCode().getDesc()).toString();
			logger.error("活动点亮初始化数据失败",e);
		}catch(Exception exception){
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("活动点亮初始化数据失败",exception);
		}
		finally {
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resultStr, context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(), context.getUserName());
			
		}
		
		
		return resultStr;
	}
	
	private static String getCouponUrl(){
		if(couponUrl==null){
			couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
			return couponUrl;
		}
		return couponUrl;
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
