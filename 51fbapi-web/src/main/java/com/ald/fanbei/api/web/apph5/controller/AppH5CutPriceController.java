package com.ald.fanbei.api.web.apph5.controller;

import java.text.SimpleDateFormat;
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
import com.ald.fanbei.api.biz.service.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: AppH5CutPriceController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:27:46
 *
 */
@RestController
@RequestMapping("/activity/de")
public class AppH5CutPriceController extends BaseController {

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
	AfResourceService afResourceService;
	
	String opennative = "/fanbei-web/opennative?name=";

	
	/**
	 * 
	 *  @Title: goodsInfo 
	 *  @Description: 获取商品砍价详情
	 *  @param request 
	 *  @param response
	 *  @return String
	 *  @throws
	 */
	@RequestMapping(value = "/goodsInfo", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
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
				logger.error("/activity/de/goodsInfo" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/goodsInfo" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取商品砍价详情失败").toString();
			return resultStr;
		}

		return resultStr;
	}
	
	/**
	 * 
	 *  @Title: friend 
	 *  @Description: 获取商品砍价详情用户列表
	 *  @param request 
	 *  @param response
	 *  @return String
	 *  @throws
	 */
	@RequestMapping(value = "/friend", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String friend(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
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
				logger.error("/activity/de/friend" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/friend" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取商品砍价详情用户列表失败").toString();
			return resultStr;
		}

		return resultStr;
	}
	
	/**
	 * 
	 *  @Title: top
	 *  @Description:获取砍价商品榜单信息
	 *  @param request 
	 *  @param response
	 *  @return String
	 *  @throws
	 */
	@RequestMapping(value = "/top", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String top(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			
			if(goodsPriceId == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/top : userName ={} , goodsPriceId = {}, goodsPriceId = {}",userName,goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//goodsPriceId
			AfDeUserGoodsDo  afDeUserGoodsDo = new AfDeUserGoodsDo();
			afDeUserGoodsDo.setUserid(userId);
			afDeUserGoodsDo.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getGoodsInfo(afDeUserGoodsDo);
			//结束时间
			long endTime = afDeGoodsService.getActivityEndTime();
			//参与人数
			long totalCount = afDeGoodsService.getActivityTotalCount();
			//转vo
			
			resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单信息成功",null,afDeUserGoodsInfoDto).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activity/de/top" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/top" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取砍价商品榜单信息失败").toString();
			return resultStr;
		}

		return resultStr;
	}
	
	/**
	 * 
	 *  @Title: topList 
	 *  @Description: 获取砍价商品榜单列表
	 *  @param request 
	 *  @param response
	 *  @return String
	 *  @throws
	 */
	@RequestMapping(value = "/topList", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String topList(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));
			
			if(goodsPriceId == null || pageNo == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/friend : userName ={} , goodsPriceId = {}, goodsPriceId = {}",userName,goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			String phone = changePhone(userName);
			//分页获取整个列表，排行

			AfDeUserGoodsQuery  queryGoods = new AfDeUserGoodsQuery();
			queryGoods.setPageNo(pageNo);
			List<AfDeUserGoodsDo> afDeUserGoodsDoList = afDeUserGoodsService.getAfDeUserGoogsList(queryGoods);
			//转vo? for index
			
			resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单列表成功",null,afDeUserGoodsDoList).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activity/de/topList" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/topList" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "获取砍价商品榜单列表失败").toString();
			return resultStr;
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
	
	/**
	 * 
	 * @Title: share @Description: 砍价接口 @param request @param response @return
	 *         String @throws
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST, produces = "text/html;charset = UTF-8")
	public String share(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			logger.info("activity/de/share params: userName ={} , goodsPriceId = {}",userName,goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//查处改用户的所有的砍价商品
			AfDeUserGoodsDo goodsDo = new AfDeUserGoodsDo();
			goodsDo.setUserid(userId);
			goodsDo.setGoodspriceid(goodsPriceId);
			
			
			//判断是否是
			
			

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activity/de/share" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/share" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "分享砍价商品失败").toString();
			return resultStr;
		}

		return resultStr;
	}

	/**
	 * 
	* @Title: convertUserNameToUserId
	* @Description: 
	* @param userName
	* @return Long   
	* @throws
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
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
