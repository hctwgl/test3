package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfDeGoodsInfoVo;
import com.ald.fanbei.api.web.vo.AfDeUserCutInfoVo;
import com.ald.fanbei.api.web.vo.AfDeUserGoodsVo;
import com.ald.fanbei.api.web.vo.AfShopVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: AppH5CutPriceController
 * @Description: 双十一砍价活动，appH5
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:27:46
 *
 */
@RestController
@RequestMapping(value = "/activity/de", produces = "application/json;charset=UTF-8")
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

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: share @Description: 砍价接口 @param request @param response @return
	 *         String @throws
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST)
	public String share(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = H5CommonResponse.getNewInstance(false, "砍价分享失败").toString();
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			logger.info("activity/de/share params: userName ={} , goodsPriceId = {}", userName, goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			// find all the goods List for this user
			AfDeUserGoodsDo userGoodsDo = new AfDeUserGoodsDo();
			userGoodsDo.setUserid(userId);
			userGoodsDo.setGoodspriceid(goodsPriceId);
			List<AfDeUserGoodsDo> userGoodsDoList = afDeUserGoodsService.getListByCommonCondition(userGoodsDo);
			
			// find goodsPriceId for iphonex
			AfDeGoodsDo iphoneDo = new AfDeGoodsDo();
			iphoneDo.setType(1);
			AfDeGoodsDo iphoneDoo = afDeGoodsService.getByCommonCondition(iphoneDo);
			if (iphoneDoo != null) {
				//to judge if the goods is iphoneX
				if (goodsPriceId.equals(iphoneDoo.getGoodspriceid())) {
					boolean flag = false;
					if (userGoodsDoList != null && userGoodsDoList.size() > 0 ) {
						for(AfDeUserGoodsDo afDeUserGoodsDo:userGoodsDoList){
							if(afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId)){
								flag = true;
								break;
							}
						}
						if (!flag) {
							//insert the user goods
							AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
							insertDo.setUserid(userId);
							insertDo.setGmtCreate(new Date());
							insertDo.setGoodspriceid(goodsPriceId);
							insertDo.setGmtModified(new Date());
							insertDo.setIsbuy(0);
							afDeUserGoodsService.saveRecord(insertDo);
						}
					}
				//as long as the goods is iphoneX no matter the flag the result is true.
				resultStr = H5CommonResponse.getNewInstance(true, "ihponex砍价分享成功").toString();
				}else{
					//needs to know if this goods has been shared by this user
					boolean flag = false;
					if (userGoodsDoList != null && userGoodsDoList.size() > 0 ) {
						for(AfDeUserGoodsDo afDeUserGoodsDo:userGoodsDoList){
							if(afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId)){
								flag = true;
								break;
							}
						}
						if (!flag) {
							//insert the user goods if this user does'nt have this goods
							AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
							insertDo.setUserid(userId);
							insertDo.setGmtCreate(new Date());
							insertDo.setGmtModified(new Date());
							insertDo.setGoodspriceid(goodsPriceId);
							insertDo.setIsbuy(0);
							afDeUserGoodsService.saveRecord(insertDo);
							resultStr = H5CommonResponse.getNewInstance(true, "商品砍价分享成功").toString();
						}else{
							resultStr = H5CommonResponse.getNewInstance(false, "只能砍价两件商品，不要太贪心哦！").toString();
						}
					}else{// the user doent have shared this goods. 
						//insert the user goods if this user does'nt have this goods
						AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
						insertDo.setUserid(userId);
						insertDo.setGmtCreate(new Date());
						insertDo.setGmtModified(new Date());
						insertDo.setIsbuy(0);
						insertDo.setGoodspriceid(goodsPriceId);
						afDeUserGoodsService.saveRecord(insertDo);
						resultStr = H5CommonResponse.getNewInstance(true, "商品砍价分享成功").toString();
					}

					
				}
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activity/de/share" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/share" + context + "error = {}", e.getStackTrace());
		}

		return resultStr;
	}


    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public H5CommonResponse getGoodsList(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiWebContext context = doWebCheck(request, false);
	try {
	    String userName = context.getUserName();
	    AfUserDo user = afUserService.getUserByUserName(userName);
	    Long userId = user == null ? -1 : user.getRid();

	    List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
	    data.put("goodsList", userDeGoodsList);
	    //结束时间
	    long endTime = afDeGoodsService.getActivityEndTime();
	    long totalCount = afDeGoodsService.getActivityTotalCount();
	    data.put("endTime", endTime);
	    data.put("totalCount", totalCount);

	    return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
	} catch (Exception e) {
	    logger.error("/activity/de/goods" + context + "error = {}", e);
	    return H5CommonResponse.getNewInstance(false, "获取砍价商品列表失败");
	}
    }
    /**
   	 * 
   	 *  @Title: endtime 
   	 *  @Description: 获取活动结束时间
   	 *  @param request 
   	 *  @param response
   	 *  @return String
   	 *  @throws
   	 */
   	@RequestMapping(value = "/endtime", method = RequestMethod.POST)
   	public String endtime(HttpServletRequest request, HttpServletResponse response) {
   		String resultStr = "";
   		try { 
   		    	Map<String,Object> map = new  HashMap<String,Object>();
   		        //结束时间
   		        long endTime = afDeGoodsService.getActivityEndTime();
   		        //当前时间
   		        long currentTime = System.currentTimeMillis();
   		        map.put("endTime", endTime);   	
   		        map.put("currentTime", currentTime);   	
   			resultStr = H5CommonResponse.getNewInstance(true, "获取活动时间成功",null,map).toString();
 
   		} catch (Exception e) {
   			logger.error("/activity/de/endtime" + "error = {}", e.getStackTrace());
   			resultStr = H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
   		}
   		return resultStr;
   	}
    /**
	 * 
	 *  @Title: goodsInfo 
	 *  @Description: 获取商品砍价详情
	 *  @param request 
	 *  @param response
	 *  @return String
	 *  @throws
	 */
	@RequestMapping(value = "/goodsInfo", method = RequestMethod.POST)
	public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
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
			//afDeUserGoodsDo.setUserid(userId);
			afDeUserGoodsDo.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getUserGoodsInfo(afDeUserGoodsDo);
			logger.info("afDeUserGoodsInfoDto = {}",afDeUserGoodsInfoDto);
			 AfDeUserGoodsInfoDto afDeUserCutPrice = new AfDeUserGoodsInfoDto();
			if(afDeUserGoodsInfoDto != null){
			    afDeUserGoodsDo.setUserid(userId);
			    afDeUserCutPrice = afDeUserGoodsService.getUserCutPrice(afDeUserGoodsDo);
			}
			if( afDeUserCutPrice ==null){
			    BigDecimal cutPrice =new BigDecimal(0);
			    afDeUserGoodsInfoDto.setCutPrice(cutPrice);
			}else{
			    afDeUserGoodsInfoDto.setCutPrice(afDeUserCutPrice.getCutPrice());
			}
			if(afDeUserGoodsInfoDto != null){
        			 //结束时间
        			long endTime = afDeGoodsService.getActivityEndTime();
        			afDeUserGoodsInfoDto.setEndTime(endTime);
        			//参与人数
        			long totalCount = afDeGoodsService.getActivityTotalCount();
        			afDeUserGoodsInfoDto.setTotalCount(totalCount);
        			logger.info("totalCount = {}",totalCount);
			}
			//转成vo?
			resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情成功",null,afDeUserGoodsInfoDto).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
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
	@RequestMapping(value = "/friend", method = RequestMethod.POST)
	public String friend(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {	context = doWebCheck(request, false);
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
			logger.info("afDeUserGoodsDo = {}",afDeUserGoodsDo);
			if(afDeUserGoodsDo!=null){
			    userGoodsId = afDeUserGoodsDo.getRid();
			}
			//获取商品砍价详情用户列表
			Map<String,Object> map = new  HashMap<String,Object>();
			AfDeUserCutInfoQuery queryCutInfo = new  AfDeUserCutInfoQuery();
			queryCutInfo.setUsergoodsid(userGoodsId);
			queryCutInfo.setPageNo(pageNo);
			List<AfDeUserCutInfoDo> afDeUserCutInfoList = afDeUserCutInfoService.getAfDeUserCutInfoList(queryCutInfo);
			logger.info("afDeUserCutInfoList = {}",afDeUserCutInfoList);
			List<AfDeUserCutInfoVo> friendList = new ArrayList<AfDeUserCutInfoVo>();
			if (CollectionUtil.isNotEmpty(afDeUserCutInfoList)) {
			    friendList = CollectionConverterUtil.convertToListFromList(afDeUserCutInfoList, new Converter<AfDeUserCutInfoDo, AfDeUserCutInfoVo>() {
					@Override
					public AfDeUserCutInfoVo convert(AfDeUserCutInfoDo source) {
						return parseDoToVo(source);
					}
				});
			}
			map.put("friendList",friendList);
			map.put("pageNo", pageNo);
			resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情用户列表成功",null,map).toString();

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
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
	@RequestMapping(value = "/top", method = RequestMethod.POST)
	public String top(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			if(goodsPriceId == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/top :  goodsPriceId = {}, goodsPriceId = {}",goodsPriceId);
			AfDeGoodsInfoVo  vo = new AfDeGoodsInfoVo();
			AfDeGoodsDo  afDeGoodsDo = new AfDeGoodsDo();
			afDeGoodsDo.setGoodspriceid(goodsPriceId);
			
			AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeGoodsService.getGoodsInfo(afDeGoodsDo);
			logger.info("afDeUserGoodsInfoDto = {}",afDeUserGoodsInfoDto);
			
			//转vo
			if(afDeUserGoodsInfoDto!=null){
			        //结束时间
				long endTime = afDeGoodsService.getActivityEndTime();
				//参与人数
				long totalCount = afDeGoodsService.getActivityTotalCount();
				int iniNum = 0;
				iniNum = afDeGoodsService.getIniNum();
				totalCount  = totalCount+iniNum;
				logger.info("endTime = {}, totalCount = {}",endTime,totalCount);
        			vo.setName(afDeUserGoodsInfoDto.getName());
        			vo.setImage(afDeUserGoodsInfoDto.getImage());
        			vo.setEndTime(endTime);
        			vo.setTotalCount(totalCount);
			}else{
			    return H5CommonResponse.getNewInstance(false, "未查询到砍价商品榜单信息",null,"").toString();
			}
			resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单信息成功",null,vo).toString();
		
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
	@RequestMapping(value = "/topList", method = RequestMethod.POST)
	public String topList(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));
			
			if(goodsPriceId == null || pageNo == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/friend : goodsPriceId = {}, goodsPriceId = {}",goodsPriceId);
			//分页获取整个列表，排行

			AfDeUserGoodsQuery  queryGoods = new AfDeUserGoodsQuery();
			queryGoods.setPageNo(pageNo);
			queryGoods.setGoodspriceid(goodsPriceId);
			List<AfDeUserGoodsDto> afDeUserGoodsDoList = afDeUserGoodsService.getAfDeUserGoogsList(queryGoods);
			logger.info("afDeUserGoodsDoList = {}",afDeUserGoodsDoList);
			List<AfDeUserGoodsVo> vo = new ArrayList<AfDeUserGoodsVo>()  ;
			Map<String,Object> map = new  HashMap<String,Object>();
			//转vo
			int i = 0;
			for(AfDeUserGoodsDto top:afDeUserGoodsDoList){
			    i++;
			    AfDeUserGoodsVo goodsVo = new AfDeUserGoodsVo();
			    String phone = changePhone(top.getUserName());
			    int index = ( (pageNo-1) * (queryGoods.getPageSize()) ) + i;
			    goodsVo.setIndex(index);
			    goodsVo.setPhone(phone);
			    goodsVo.setCutCount(top.getCutcount());
			    goodsVo.setCutPrice(top.getCutprice());
			    vo.add(goodsVo);
			   
			}
			map.put("listPerson",vo);
			map.put("pageNo", pageNo);
			resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单列表成功",null,map).toString();
		
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
	

	private AfDeUserCutInfoVo parseDoToVo(AfDeUserCutInfoDo userCutInfo) {
	    	AfDeUserCutInfoVo vo = new AfDeUserCutInfoVo();
		vo.setCutPrice(userCutInfo.getCutprice());
		vo.setHeadImgUrl(userCutInfo.getHeadimgurl());
		vo.setNickName(userCutInfo.getNickname());
		vo.setRemainPrice(userCutInfo.getRemainprice());
		return vo;
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
