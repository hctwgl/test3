package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

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
    @Resource
    AfResourceService afResourceService;

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
	doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"), "/activity/de/share", request.getParameter("goodsPriceId"));
	try {
	    context = doWebCheck(request, true);

	    long endTime = afDeGoodsService.getActivityEndTime();
	    Long now = new Date().getTime();
	    if (now > endTime) {
		resultStr = H5CommonResponse.getNewInstance(false, "活动已经结束").toString();
		return resultStr;
	    }

	    String userName = context.getUserName();
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    logger.info("activity/de/share params: userName ={} , goodsPriceId = {}", userName, goodsPriceId);
	    Long userId = convertUserNameToUserId(userName);
	    // find all the goods List for this user
	    AfDeUserGoodsDo userGoodsDo = new AfDeUserGoodsDo();
	    userGoodsDo.setUserid(userId);
	    List<AfDeUserGoodsDo> userGoodsDoList = afDeUserGoodsService.getListByCommonCondition(userGoodsDo);

	    // find goodsPriceId for iphonex
	    AfDeGoodsDo iphoneDo = new AfDeGoodsDo();
	    iphoneDo.setType(1);
	    AfDeGoodsDo iphoneDoo = afDeGoodsService.getByCommonCondition(iphoneDo);
	    if (iphoneDoo != null) {
		// to judge if the goods is iphoneX
		if (goodsPriceId.equals(iphoneDoo.getGoodspriceid())) {
		    boolean flag = false;
		    if (userGoodsDoList != null && userGoodsDoList.size() > 0) {
			for (AfDeUserGoodsDo afDeUserGoodsDo : userGoodsDoList) {
			    if (afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId)) {
				flag = true;
				logger.info("activity/de/share userName ={} , goodsPriceId = {} the user has already shared this goods", userName, goodsPriceId);
				break;
			    }
			}
			if (!flag) {
			    // insert the user goods
			    AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
			    insertDo.setUserid(userId);
			    insertDo.setGmtCreate(new Date());
			    insertDo.setGoodspriceid(goodsPriceId);
			    insertDo.setGmtModified(new Date());
			    insertDo.setIsbuy(0);
			    afDeUserGoodsService.saveRecord(insertDo);
			    logger.info("activity/de/share userName ={} , goodsPriceId = {} save this goods for this user succeed and this user has already another shared goods", userName, goodsPriceId);
			}
		    } else {
			// insert the user goods
			AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
			insertDo.setUserid(userId);
			insertDo.setGmtCreate(new Date());
			insertDo.setGoodspriceid(goodsPriceId);
			insertDo.setGmtModified(new Date());
			insertDo.setIsbuy(0);
			afDeUserGoodsService.saveRecord(insertDo);
			logger.info("activity/de/share userName ={} , goodsPriceId = {} save this goods for this user succeed and this user donest have any shared goods", userName, goodsPriceId);
		    }

		    // as long as the goods is iphoneX no matter the flag the
		    // result is true.
		    resultStr = H5CommonResponse.getNewInstance(true, "ihponex砍价分享成功").toString();
		    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"), "/activity/de/share", goodsPriceId.toString());
		} else {
		    // needs to know if this goods has been shared by this user
		    boolean flag = false;
		    if (userGoodsDoList != null && userGoodsDoList.size() > 0) {
			List<Long> userGoodsPriceList = new ArrayList<>();

			for (AfDeUserGoodsDo afDeUserGoodsDo : userGoodsDoList) {
			    // if
			    // (afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId))
			    // {
			    userGoodsPriceList.add(afDeUserGoodsDo.getGoodspriceid());
			    // }
			}

			// to judge if the user has already bought another two
			// goodses
			if ((userGoodsDoList.size() >= 6 && !userGoodsPriceList.contains(goodsPriceId)) || !userGoodsPriceList.contains(goodsPriceId) && (userGoodsDoList.size() >= 5 && !userGoodsPriceList.contains(iphoneDoo.getGoodspriceid()))) {
			    logger.info("activity/de/share userName ={}  has already had {} goodses shared", userName, userGoodsDoList.size());
			    resultStr = H5CommonResponse.getNewInstance(false, "除了iphoneX只能砍价五件商品，不要太贪心哦！").toString();
			    return resultStr;
			}

			for (AfDeUserGoodsDo afDeUserGoodsDo : userGoodsDoList) {
			    if (afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId)) {
				flag = true;
				logger.info("activity/de/share userName ={}  has already shared this goods {}", userName, goodsPriceId);
				break;
			    }
			}
			if (!flag) {
			    // insert the user goods if this user does'nt have
			    // this
			    // goods
			    AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
			    insertDo.setUserid(userId);
			    insertDo.setGmtCreate(new Date());
			    insertDo.setGmtModified(new Date());
			    insertDo.setGoodspriceid(goodsPriceId);
			    insertDo.setIsbuy(0);
			    afDeUserGoodsService.saveRecord(insertDo);
			    logger.info("activity/de/share userName ={}  succeed to share this goods {}", userName, goodsPriceId);
			    resultStr = H5CommonResponse.getNewInstance(true, "商品砍价分享成功").toString();
			    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"), "/activity/de/share", goodsPriceId.toString());
			}
			resultStr = H5CommonResponse.getNewInstance(true, "商品砍价分享成功").toString();
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"), "/activity/de/share", goodsPriceId.toString());

		    } else {
			logger.info("activity/de/share userName ={}  had no goods shared before and now begin to share this goods {}", userName, goodsPriceId);
			AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
			insertDo.setUserid(userId);
			insertDo.setGmtCreate(new Date());
			insertDo.setGmtModified(new Date());
			insertDo.setIsbuy(0);
			insertDo.setGoodspriceid(goodsPriceId);
			afDeUserGoodsService.saveRecord(insertDo);
			logger.info("activity/de/share userName ={}  had no goods shared before and now succeed to share this goods {}", userName, goodsPriceId);
			resultStr = H5CommonResponse.getNewInstance(true, "商品砍价分享成功").toString();
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"), "/activity/de/share", goodsPriceId.toString());
		    }

		    // }
		}
	    }
	} catch (FanbeiException e) {
	    if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
		Map<String, Object> data = new HashMap<>();
		String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
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
	FanbeiWebContext context = new FanbeiWebContext();

	try {

	    try {
		context = doWebCheck(request, false);
	    } catch (Exception e) {
		logger.error("doWebCheck error" + context + "error = {}", e);

	    }

	    String userName = context.getUserName();
	    AfUserDo user = afUserService.getUserByUserName(userName);
	    Long userId = user == null ? -1 : user.getRid();

	    List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
	    data.put("goodsList", userDeGoodsList);
	    // 结束时间
	    long endTime = afDeGoodsService.getActivityEndTime();
	    long totalCount = afDeGoodsService.getActivityTotalCount();
	    int iniNum = 0;
	    iniNum = afDeGoodsService.getIniNum();
	    totalCount = totalCount + iniNum;
	    logger.info("endTime = {}, totalCount = {}", endTime, totalCount);
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
     * @Title: endtime @Description: 获取活动结束时间 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/endtime", method = RequestMethod.POST)
    public String endtime(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	try {
	    Map<String, Object> map = new HashMap<String, Object>();
	    // 结束时间
	    long endTime = afDeGoodsService.getActivityEndTime();
	    // 当前时间
	    long currentTime = System.currentTimeMillis();
	    map.put("endTime", endTime);
	    map.put("currentTime", currentTime);
	    resultStr = H5CommonResponse.getNewInstance(true, "获取活动时间成功", null, map).toString();

	} catch (Exception e) {
	    logger.error("/activity/de/endtime" + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
	}
	return resultStr;
    }

    /**
     * 
     * @Title: goodsInfo @Description: 获取商品砍价详情 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/goodsInfo", method = RequestMethod.POST)
    public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiWebContext context = new FanbeiWebContext();
	try {
	    context = doWebCheck(request, true);
	    String userName = context.getUserName();
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    if (goodsPriceId == null) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/goodsInfo : userName ={} , goodsPriceId = {}", userName, goodsPriceId);
	    Long userId = convertUserNameToUserId(userName);
	    // 查用户的商品砍价详情
	    AfDeUserGoodsDo afDeUserGoodsDo = new AfDeUserGoodsDo();
	    // afDeUserGoodsDo.setUserid(userId);
	    afDeUserGoodsDo.setGoodspriceid(goodsPriceId);
	    AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getUserGoodsInfo(afDeUserGoodsDo);
	    logger.info("afDeUserGoodsInfoDto = {}", afDeUserGoodsInfoDto);
	    AfDeUserGoodsInfoDto afDeUserCutPrice = new AfDeUserGoodsInfoDto();
	    if (afDeUserGoodsInfoDto != null) {
		afDeUserGoodsDo.setUserid(userId);
		afDeUserCutPrice = afDeUserGoodsService.getUserCutPrice(afDeUserGoodsDo);
	    }
	    if (afDeUserCutPrice == null) {
		BigDecimal cutPrice = new BigDecimal(0);
		afDeUserGoodsInfoDto.setCutPrice(cutPrice);
	    } else {
		afDeUserGoodsInfoDto.setCutPrice(afDeUserCutPrice.getCutPrice());
	    }
	    if (afDeUserGoodsInfoDto != null) {
		// 结束时间
		long endTime = afDeGoodsService.getActivityEndTime();
		afDeUserGoodsInfoDto.setEndTime(endTime);
		// 参与人数
		long totalCount = afDeGoodsService.getActivityTotalCount();
		int iniNum = 0;
		iniNum = afDeGoodsService.getIniNum();
		totalCount = totalCount + iniNum;
		logger.info("endTime = {}, totalCount = {}", endTime, totalCount);

		afDeUserGoodsInfoDto.setTotalCount(totalCount);
		logger.info("totalCount = {}", totalCount);
	    }
	    // 转成vo?
	    resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情成功", null, afDeUserGoodsInfoDto).toString();

	} catch (FanbeiException e) {
	    if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
		Map<String, Object> data = new HashMap<>();
		String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();

		AfDeUserGoodsDo afDeUserGoodsDo = new AfDeUserGoodsDo();
		// afDeUserGoodsDo.setUserid(userId);
		afDeUserGoodsDo.setGoodspriceid(NumberUtil.objToLong(request.getParameter("goodsPriceId")));
		AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getUserGoodsInfo(afDeUserGoodsDo);

		// 结束时间
		long endTime = afDeGoodsService.getActivityEndTime();

		// 参与人数
		long totalCount = afDeGoodsService.getActivityTotalCount();
		int iniNum = 0;
		iniNum = afDeGoodsService.getIniNum();
		totalCount = totalCount + iniNum;
		data.put("loginUrl", loginUrl);
		data.put("goodsPriceId", request.getParameter("goodsPriceId"));
		data.put("totalCount", totalCount);
		data.put("goodsDetail", endTime);
		data.put("originalPrice", 0);
		data.put("image", afDeUserGoodsInfoDto.getImage());
		data.put("goodsId", afDeUserGoodsInfoDto.getGoodsId());
		data.put("endTime", endTime);
		data.put("originalPrice", afDeUserGoodsInfoDto.getOriginalPrice());
		data.put("cutPrice", afDeUserGoodsInfoDto.getCutPrice());
		data.put("name", afDeUserGoodsInfoDto.getName());
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
     * @Title: friend @Description: 获取商品砍价详情用户列表 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/friend", method = RequestMethod.POST)
    public String friend(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiWebContext context = new FanbeiWebContext();
	try {
	    context = doWebCheck(request, true);
	    String userName = context.getUserName();
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));

	    if (goodsPriceId == null || pageNo == null) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/friend : userName ={} , goodsPriceId = {}, goodsPriceId = {}", userName, goodsPriceId);
	    Long userId = convertUserNameToUserId(userName);
	    // goodsPriceId 和userId 查询 userGoodsId
	    long userGoodsId = 0;
	    AfDeUserGoodsDo queryUserGoods = new AfDeUserGoodsDo();
	    queryUserGoods.setUserid(userId);
	    queryUserGoods.setGoodspriceid(goodsPriceId);
	    AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getByCommonCondition(queryUserGoods);
	    logger.info("afDeUserGoodsDo = {}", afDeUserGoodsDo);
	    if (afDeUserGoodsDo != null) {
		userGoodsId = afDeUserGoodsDo.getRid();
	    }
	    // 获取商品砍价详情用户列表
	    Map<String, Object> map = new HashMap<String, Object>();
	    AfDeUserCutInfoQuery queryCutInfo = new AfDeUserCutInfoQuery();
	    queryCutInfo.setUsergoodsid(userGoodsId);
	    queryCutInfo.setPageNo(pageNo);
	    List<AfDeUserCutInfoDo> afDeUserCutInfoList = afDeUserCutInfoService.getAfDeUserCutInfoList(queryCutInfo);
	    logger.info("afDeUserCutInfoList = {}", afDeUserCutInfoList);
	    List<AfDeUserCutInfoVo> friendList = new ArrayList<AfDeUserCutInfoVo>();
	    if (CollectionUtil.isNotEmpty(afDeUserCutInfoList)) {
		friendList = CollectionConverterUtil.convertToListFromList(afDeUserCutInfoList, new Converter<AfDeUserCutInfoDo, AfDeUserCutInfoVo>() {
		    @Override
		    public AfDeUserCutInfoVo convert(AfDeUserCutInfoDo source) {
			return parseDoToVo(source);
		    }
		});
	    }
	    map.put("friendList", friendList);
	    map.put("pageNo", pageNo);
	    resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情用户列表成功", null, map).toString();
	} catch (FanbeiException e) {
	    if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
		Map<String, Object> data = new HashMap<>();
		String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
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
     * @Title: top @Description:获取砍价商品榜单信息 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    public String top(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiWebContext context = new FanbeiWebContext();
	try {
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    if (goodsPriceId == null) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/top :  goodsPriceId = {}, goodsPriceId = {}", goodsPriceId);
	    AfDeGoodsInfoVo vo = new AfDeGoodsInfoVo();
	    AfDeGoodsDo afDeGoodsDo = new AfDeGoodsDo();
	    afDeGoodsDo.setGoodspriceid(goodsPriceId);

	    AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeGoodsService.getGoodsInfo(afDeGoodsDo);
	    logger.info("afDeUserGoodsInfoDto = {}", afDeUserGoodsInfoDto);

	    // 转vo
	    if (afDeUserGoodsInfoDto != null) {
		// 结束时间
		long endTime = afDeGoodsService.getActivityEndTime();
		// 参与人数
		long totalCount = afDeGoodsService.getActivityTotalCount();
		int iniNum = 0;
		iniNum = afDeGoodsService.getIniNum();
		totalCount = totalCount + iniNum;
		logger.info("endTime = {}, totalCount = {}", endTime, totalCount);
		vo.setName(afDeUserGoodsInfoDto.getName());
		vo.setImage(afDeUserGoodsInfoDto.getImage());
		vo.setEndTime(endTime);
		vo.setTotalCount(totalCount);
	    } else {
		return H5CommonResponse.getNewInstance(false, "未查询到砍价商品榜单信息", null, "").toString();
	    }
	    resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单信息成功", null, vo).toString();

	} catch (Exception e) {
	    logger.error("/activity/de/top" + context + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取砍价商品榜单信息失败").toString();
	    return resultStr;
	}

	return resultStr;
    }

    /**
     * 
     * 
     * @Title: topList @Description: 获取砍价商品榜单列表 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/topList", method = RequestMethod.POST)
    public String topList(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiWebContext context = new FanbeiWebContext();
	try {
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));

	    if (goodsPriceId == null || pageNo == null) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/friend : goodsPriceId = {}, goodsPriceId = {}", goodsPriceId);
	    // 分页获取整个列表，排行

	    AfDeUserGoodsQuery queryGoods = new AfDeUserGoodsQuery();
	    queryGoods.setPageNo(pageNo);
	    queryGoods.setGoodspriceid(goodsPriceId);
	    List<AfDeUserGoodsDto> afDeUserGoodsDoList = afDeUserGoodsService.getAfDeUserGoogsList(queryGoods);
	    logger.info("afDeUserGoodsDoList = {}", afDeUserGoodsDoList);
	    List<AfDeUserGoodsVo> vo = new ArrayList<AfDeUserGoodsVo>();
	    Map<String, Object> map = new HashMap<String, Object>();
	    // 转vo
	    int i = 0;
	    for (AfDeUserGoodsDto top : afDeUserGoodsDoList) {
		i++;
		AfDeUserGoodsVo goodsVo = new AfDeUserGoodsVo();
		String phone = changePhone(top.getUserName());
		int index = ((pageNo - 1) * (queryGoods.getPageSize())) + i;
		goodsVo.setIndex(index);
		goodsVo.setPhone(phone);
		goodsVo.setCutCount(top.getCutcount());
		goodsVo.setCutPrice(top.getCutprice());
		vo.add(goodsVo);

	    }
	    map.put("listPerson", vo);
	    map.put("pageNo", pageNo);
	    resultStr = H5CommonResponse.getNewInstance(true, "获取砍价商品榜单列表成功", null, map).toString();

	} catch (Exception e) {
	    logger.error("/activity/de/topList" + context + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取砍价商品榜单列表失败").toString();
	    return resultStr;
	}

	return resultStr;
    }

    /**
     * 根据授权code 获取用户ID nickname headimgurl
     */
    @RequestMapping(value = "/wechat/userInfo", method = RequestMethod.POST)
    public String getUserInfo(HttpServletRequest request, HttpServletResponse response) {
	doWebCheck(request, false);
	String code = request.getParameter("code");

	if (StringUtils.isBlank(code)) {
	    return H5CommonResponse.getNewInstance(false, "获取用户微信信息失败", null, "").toString();
	}

	try {
	    // 获取access_token
	    AfResourceDo afResourceDo = afResourceService.getWechatConfig();
	    String appid = afResourceDo.getValue();
	    String secret = afResourceDo.getValue1();
	    JSONObject wxUserInfo = WxUtil.getUserInfo(appid, secret, code);

	    logger.info(JSON.toJSONString(wxUserInfo));
	    String openId = wxUserInfo.getString("openid");
	    if (StringUtils.isNotBlank(openId)) {
		return H5CommonResponse.getNewInstance(true, "获取用户信息成功", null, wxUserInfo.toJSONString()).toString();
	    } else {
		return H5CommonResponse.getNewInstance(false, "获取用户信息失败", null, wxUserInfo.toJSONString()).toString();
	    }
	} catch (Exception e) {
	    logger.error("/activity/de/wechat/userInfo error = {}", e.getStackTrace());
	    return H5CommonResponse.getNewInstance(false, "获取用户微信信息失败", null, "").toString();
	}
    }

    /**
     * 发送https请求
     * 
     * @param requestUrl
     *            请求地址
     * @param requestMethod
     *            请求方式（GET、POST）
     * @param outputStr
     *            提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    private static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
	JSONObject jsonObject = null;
	try {
	    // 创建SSLContext对象，并使用我们指定的信任管理器初始化
	    TrustManager[] tm = { new MyX509TrustManager() };
	    SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
	    sslContext.init(null, tm, new java.security.SecureRandom());
	    // 从上述SSLContext对象中得到SSLSocketFactory对象
	    SSLSocketFactory ssf = sslContext.getSocketFactory();

	    URL url = new URL(requestUrl);
	    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	    conn.setSSLSocketFactory(ssf);

	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    // 设置请求方式（GET/POST）
	    conn.setRequestMethod(requestMethod);

	    // 当outputStr不为null时向输出流写数据
	    if (null != outputStr) {
		OutputStream outputStream = conn.getOutputStream();
		// 注意编码格式
		outputStream.write(outputStr.getBytes("UTF-8"));
		outputStream.close();
	    }

	    // 从输入流读取返回内容
	    InputStream inputStream = conn.getInputStream();
	    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	    String str = null;
	    StringBuffer buffer = new StringBuffer();
	    while ((str = bufferedReader.readLine()) != null) {
		buffer.append(str);
	    }

	    // 释放资源
	    bufferedReader.close();
	    inputStreamReader.close();
	    inputStream.close();
	    inputStream = null;
	    conn.disconnect();
	    jsonObject = JSONObject.parseObject(buffer.toString());
	} catch (ConnectException ce) {
	    System.out.println(ce);
	} catch (Exception e) {
	    System.out.println(e);
	}
	return jsonObject;
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
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
    }

}

class MyX509TrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	// TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	// TODO Auto-generated method stub

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
	// TODO Auto-generated method stub
	return null;
    }

}
