package com.ald.fanbei.api.web.h5.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.AfDeRandomPropertyService;
import com.ald.fanbei.api.biz.service.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:28:04
 *
 */
@RestController
@RequestMapping("/activityH5/de")
public class H5CutPriceController extends H5Controller {

	@Resource
	AfUserService afUserService;
	@Resource
	AfDeGoodsService afDeGoodsService;
	@Resource
	AfDeGoodsCouponService afDeGoodsCouponService;
	@Resource
	AfDeUserCutInfoService afDeUserCutInfoService;
	@Resource
	AfDeUserGoodsService afDeUserGoodsService;
	@Resource
	AfDeRandomPropertyService afDeRandomPropertyService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: goodsInfo 
	 * @Description: 获取商品砍价详情
	 * @param requst 
	 * @param response 
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/goodsInfo", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			if(goodsPriceId == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/goodsInfo : userName ={} , goodsPriceId = {}",userName,goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//查用户的商品砍价详情
			AfDeUserGoodsDo  afDeUserGoodsDo = new AfDeUserGoodsDo();
			afDeUserGoodsDo.setUserid(userId);
			afDeUserGoodsDo.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getGoodsInfo(afDeUserGoodsDo);
			//转成vo?时间戳转换？
			resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情成功",null,afDeUserGoodsInfoDto).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activityH5/cutPrice" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
		    	logger.error("/activity/de/goodsInfo" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取商品砍价详情失败").toString();
			return resultStr;
		}

		return resultStr;
	}
	/**
	 * 
	 * @Title: friend 
	 * @Description: 获取商品砍价详情
	 * @param requst 
	 * @param response 
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/friend", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String friend(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));
			
			if(goodsPriceId == null || pageNo == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/friend : userName ={} , goodsPriceId = {}, goodsPriceId = {}",userName,goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//goodsPriceId 和userId 查询 userGoodsId
			long userGoodsId = 0;
			AfDeUserGoodsDo  queryUserGoods = new AfDeUserGoodsDo();
			queryUserGoods.setUserid(userId);
			queryUserGoods.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getByCommonCondition(queryUserGoods);
			if(afDeUserGoodsDo!=null){
			    userGoodsId = afDeUserGoodsDo.getRid();
			}
			//获取商品砍价详情用户列表
			AfDeUserCutInfoQuery queryCutInfo = new  AfDeUserCutInfoQuery();
			queryCutInfo.setUsergoodsid(userGoodsId);
			queryCutInfo.setPageNo(pageNo);
			List<AfDeUserCutInfoDo> afDeUserCutInfoList = afDeUserCutInfoService.getAfDeUserCutInfoList(queryCutInfo);
			
			resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情用户列表成功",null,afDeUserCutInfoList).toString();
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activityH5/friend" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
		        logger.error("/activity/de/friend" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取商品砍价详情用户列表失败").toString();
			return resultStr;
		}

		return resultStr;
	}
	
	/**
	 * 
	 * @Title: cutPrice 
	 * @Description: 砍价接口 
	 * @param requst 
	 * @param response 
	 * @return
	 *         String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/cutPrice", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String cutPrice(HttpServletRequest requst, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(requst, true);

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activityH5/cutPrice" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return resultStr;
	}

	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 * Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
}
