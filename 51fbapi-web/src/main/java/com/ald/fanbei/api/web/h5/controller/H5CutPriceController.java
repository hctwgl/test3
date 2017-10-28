package com.ald.fanbei.api.web.h5.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
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
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfDeUserCutInfoVo;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: 双十一砍价 H5
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:28:04
 *
 */
@RestController
@RequestMapping(value = "/activityH5/de", produces = "application/json;charset=UTF-8")
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
    TransactionTemplate transactionTemplate;
    @Resource
    AfGoodsPriceService afGoodsPriceService;
    @Resource
    BizCacheUtil bizCacheUtil;

    String opennative = "/fanbei-web/opennative?name=";

    /**
	 * 
	 * @Title: share @Description: 砍价接口 @param request @param response @return
	 *         String @throws
	 */
	@RequestMapping(value = "/share", method = RequestMethod.POST)
	public String share(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = H5CommonResponse.getNewInstance(false, "砍价分享失败").toString();
		FanbeiH5Context context = new FanbeiH5Context();
		try {
		        context = doH5Check(request, true);
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
//					boolean flag = false;
//					if (userGoodsDoList != null && userGoodsDoList.size() > 0 ) {
//						for(AfDeUserGoodsDo afDeUserGoodsDo:userGoodsDoList){
//							if(afDeUserGoodsDo.getGoodspriceid().equals(goodsPriceId)){
//								flag = true;
//								break;
//							}
//						}
//						if (!flag) {
//							//insert the user goods
//							AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
//							insertDo.setUserid(userId);
//							insertDo.setGmtCreate(new Date());
//							insertDo.setGoodspriceid(goodsPriceId);
//							insertDo.setGmtModified(new Date());
//							insertDo.setIsbuy(0);
//							afDeUserGoodsService.saveRecord(insertDo);
//						}
//					}
				        AfDeUserGoodsDo insertDo = new AfDeUserGoodsDo();
					insertDo.setUserid(userId);
					insertDo.setGmtCreate(new Date());
					insertDo.setGoodspriceid(goodsPriceId);
					insertDo.setGmtModified(new Date());
					insertDo.setIsbuy(1);
					afDeUserGoodsService.saveRecord(insertDo);
				//as long as the goods is iphoneX no matter the flag the result is true.
				        resultStr = H5CommonResponse.getNewInstance(true, "ihponex砍价分享成功").toString();
				 }
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

					
				//}
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
		FanbeiH5Context context = doH5Check(request, false);
		try {
		    String userName = context.getUserName();
		    AfUserDo user = afUserService.getUserByUserName(userName);
		    Long userId = user == null ? -1 : user.getRid();

		    List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
		    data.put("goodsList", userDeGoodsList);
		    //结束时间
		    long endTime = afDeGoodsService.getActivityEndTime();
		    long totalCount = afDeGoodsService.getActivityTotalCount();
		    int iniNum = 0;
		    iniNum = afDeGoodsService.getIniNum();
		    totalCount  = totalCount+iniNum;
		    logger.info("endTime = {}, totalCount = {}",endTime,totalCount);
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
     * @Title: goodsInfo
     * @Description: 获取商品砍价详情
     * @param requst
     * @param response
     * @return String 返回类型
     * @throws
     */
    @RequestMapping(value = "/goodsInfo", method = RequestMethod.POST)
    public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiH5Context context = new FanbeiH5Context();     
	try {
	    String userName = ObjectUtils.toString(request.getParameter("userName"));
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    if (goodsPriceId == null || userName == null || userName.isEmpty()) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/goodsInfo :  goodsPriceId = {}", goodsPriceId);
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
		totalCount  = totalCount+iniNum;
		logger.info("endTime = {}, totalCount = {}",endTime,totalCount);
		afDeUserGoodsInfoDto.setTotalCount(totalCount);
		logger.info("totalCount = {}", totalCount);
	    }
	    // 转成vo?
	    resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情成功", null, afDeUserGoodsInfoDto).toString();

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
    @RequestMapping(value = "/friend", method = RequestMethod.POST)
    public String friend(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	FanbeiH5Context context = new FanbeiH5Context();
	try {
	    // context = doH5Check(request, true);
	    String userName = ObjectUtils.toString(request.getParameter("userName"));
	    Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
	    Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));

	    if (goodsPriceId == null || pageNo == null || userName == null || userName.isEmpty()) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
		return resultStr;
	    }
	    logger.info("/activity/de/friend :ygoodsPriceId = {}, goodsPriceId = {}", goodsPriceId);
	    Long userId = convertUserNameToUserId(userName);
	    // goodsPriceId 和userId 查询 userGoodsId
	    long userGoodsId = 0;
	    AfDeUserGoodsDo queryUserGoods = new AfDeUserGoodsDo();
	    queryUserGoods.setUserid(userId);
	    queryUserGoods.setGoodspriceid(goodsPriceId);
	    AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getByCommonCondition(queryUserGoods);
	    logger.info("h5 afDeUserGoodsDo = {}", afDeUserGoodsDo);
	    if (afDeUserGoodsDo != null) {
		userGoodsId = afDeUserGoodsDo.getRid();
	    }
	    // 获取商品砍价详情用户列表
	    Map<String, Object> map = new HashMap<String, Object>();
	    AfDeUserCutInfoQuery queryCutInfo = new AfDeUserCutInfoQuery();
	    queryCutInfo.setUsergoodsid(userGoodsId);
	    queryCutInfo.setPageNo(pageNo);
	    List<AfDeUserCutInfoDo> afDeUserCutInfoList = afDeUserCutInfoService.getAfDeUserCutInfoList(queryCutInfo);
	    logger.info("h5 afDeUserCutInfoList = {}", afDeUserCutInfoList);
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
     * @Title: endtime
     * @Description: 获取活动结束时间
     * @param request
     * @param response
     * @return String
     * @throws
     */
    @RequestMapping(value = "/endtime", method = RequestMethod.POST)
    public String endtime(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = "";
	try {
	    Map<String, Object> map = new HashMap<String, Object>();
	    // 结束时间
	    long endTime = afDeGoodsService.getActivityEndTime();
	    // 当前时间
	    long currentTime = System.currentTimeMillis() / 1000;
	    map.put("endTime", endTime);
	    map.put("currentTime", currentTime);
	    resultStr = H5CommonResponse.getNewInstance(true, "获取活动结束时间成功", null, map).toString();

	} catch (Exception e) {
	    logger.error("/activity/de/endtime" + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取活动结束时间失败").toString();
	}
	return resultStr;
    }

    /**
     * 
     * @Title: cutPrice
     * @Description: 砍价接口
     * @param requst
     * @param response
     * @return String 返回类型
     * @throws
     */
    @RequestMapping(value = "/cutPrice", method = RequestMethod.POST)
    public String cutPrice(final HttpServletRequest request, HttpServletResponse response) {
	return transactionTemplate.execute(new TransactionCallback<String>() {

	    @Override
	    public String doInTransaction(TransactionStatus status) {
		String resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
		String userIdStr = request.getParameter("userId");
		String goodsPriceIdStr = request.getParameter("goodsPriceId");
		String openId = request.getParameter("openId");
		String nickName = request.getParameter("nickName");
		String headImagUrl = request.getParameter("headImgUrl");

		String key = Constants.CACHKEY_CUT_PRICE_LOCK + ":" + userIdStr + ":" + goodsPriceIdStr;
		try {
		    if (StringUtil.isAllNotEmpty(userIdStr, openId, goodsPriceIdStr)) {
			// try 1000 times to get the lock
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
			if (isNotLock) {
			    Long userId = NumberUtil.objToLong(userIdStr);

			    Long goodsPriceId = NumberUtil.objToLong(goodsPriceIdStr);
			    // to judge if the goods is bought already
			    AfDeUserGoodsDo userGoodsDo = new AfDeUserGoodsDo();
			    userGoodsDo.setUserid(userId);
			    userGoodsDo.setGoodspriceid(goodsPriceId);
			    AfDeUserGoodsDo usergoodsResult = afDeUserGoodsService.getByCommonCondition(userGoodsDo);
			    if (usergoodsResult != null) {
				Map<String, Object> data = new HashMap<>();
				// to judge if the goods is bought already
				if (usergoodsResult.getIsbuy() == 1) {
				    data.put("code", 4);// already been bought
				    resultStr = H5CommonResponse.getNewInstance(false, "已经买了此商品，不能砍价", "", data).toString();
				} else {
				    // to judge if this wechat user has already
				    // helped this
				    // user
				    AfDeUserCutInfoDo userCutInfoDo = new AfDeUserCutInfoDo();
				    userCutInfoDo.setOpenid(openId);
				    userCutInfoDo.setUsergoodsid(usergoodsResult.getRid());
				    AfDeUserCutInfoDo userCutInfoDoResult = afDeUserCutInfoService.getByCommonCondition(userCutInfoDo);
				    if (null != userCutInfoDoResult) {// already
								      // helped
								      // to
								      // cut
					data.put("code", 2);// already been
							    // bought
					resultStr = H5CommonResponse.getNewInstance(false, "重复砍价", "", data).toString();
				    } else {

					// to see if this user has already put
					// this
					// goods to the lowest price.
					AfGoodsPriceDo goodsPriceResult = afGoodsPriceService.getById(usergoodsResult.getGoodspriceid());
					AfDeGoodsDo deGoodsDo = new AfDeGoodsDo();
					deGoodsDo.setGoodspriceid(goodsPriceId);
					AfDeGoodsDo deGoodsDoResult = afDeGoodsService.getByCommonCondition(deGoodsDo);
					if (null != goodsPriceResult && deGoodsDoResult != null) {
					    BigDecimal originalPrice = goodsPriceResult.getActualAmount();
					    BigDecimal cutPrice = usergoodsResult.getCutprice();
					    cutPrice = cutPrice.setScale(2, RoundingMode.HALF_EVEN);

					    BigDecimal lowestPrice = deGoodsDoResult.getLowestprice();
					    // already the lowest price
					    if (lowestPrice.compareTo(originalPrice.subtract(cutPrice)) >= 0) {

						// update the cutCount Field and
						// insert one
						// record for table cutInfo
						// TODO:add lock :Lock the
						// cutCount
						// Field
						usergoodsResult.setCutcount(usergoodsResult.getCutcount() + 1);
						usergoodsResult.setGmtModified(new Date());
						afDeUserGoodsService.updateById(usergoodsResult);

						AfDeUserCutInfoDo insertDo = new AfDeUserCutInfoDo();
						insertDo.setCutprice(BigDecimal.ZERO);
						insertDo.setGmtCreate(new Date());
						insertDo.setGmtModified(new Date());
						insertDo.setHeadimgurl(headImagUrl);
						insertDo.setNickname(nickName);
						insertDo.setOpenid(openId);
						insertDo.setRemainprice(lowestPrice);
						insertDo.setUsergoodsid(usergoodsResult.getRid());
						afDeUserCutInfoService.saveRecord(insertDo);

						data.put("code", 3);
						resultStr = H5CommonResponse.getNewInstance(false, "已砍至最低价，无法完成本次砍价", "", data).toString();
					    } else {// still could cut price

						// TODO:add lock
						// calculate the cutPrice for
						// current
						int cutCount = usergoodsResult.getCutcount() + 1;
						BigDecimal cutPricee = cutPrice(goodsPriceId, cutCount);
						cutPricee = cutPricee.setScale(2, RoundingMode.HALF_EVEN);

						AfDeUserCutInfoDo insertDo = new AfDeUserCutInfoDo();
						insertDo.setCutprice(cutPricee);
						insertDo.setGmtCreate(new Date());
						insertDo.setGmtModified(new Date());
						insertDo.setHeadimgurl(headImagUrl);
						insertDo.setNickname(nickName);
						insertDo.setOpenid(openId);
						insertDo.setRemainprice(originalPrice.subtract(usergoodsResult.getCutprice()).subtract(cutPricee));
						insertDo.setUsergoodsid(usergoodsResult.getRid());

						int cutCountt = usergoodsResult.getCutcount() + 1;
						usergoodsResult.setCutcount(cutCountt);
						usergoodsResult.setCutprice(cutPricee.add(usergoodsResult.getCutprice()));
						usergoodsResult.setGmtModified(new Date());

						// if after cutPrice is the
						// lowest
						// price
						if (lowestPrice.compareTo(originalPrice.subtract(cutPricee)) == 0) {
						    usergoodsResult.setGmtCompletetime(new Date());
						    usergoodsResult.setCompletecount(cutCountt);
						    insertDo.setRemainprice(lowestPrice);
						}
						afDeUserCutInfoService.saveRecord(insertDo);
						afDeUserGoodsService.updateById(usergoodsResult);
						data.put("cutPrice", cutPricee);
						resultStr = H5CommonResponse.getNewInstance(true, "砍价成功", "", data).toString();
					    }
					}
				    }
				}

			    }
			}
		    }
		} catch (FanbeiException e) {
		    resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
		    logger.error("/activity/de/share error：", e);
		    status.setRollbackOnly();
		} catch (Exception e) {
		    logger.error("/activity/de/share error：", e);
		    status.setRollbackOnly();
		} finally {
		    bizCacheUtil.delCache(key);
		}

		return resultStr;
	    }
	});

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

    /**
     * 
     * @Title: cutPrice @Description: calculate cut price @param goodsPriceId @return @return
     *         BigDecimal @throws
     */
    private BigDecimal cutPrice(Long goodsPriceId, int cutCount) {
	BigDecimal result = BigDecimal.ZERO;

	AfGoodsPriceDo goodsPriceResult = afGoodsPriceService.getById(goodsPriceId);
	AfDeGoodsDo deGoodsDo = new AfDeGoodsDo();
	deGoodsDo.setGoodspriceid(goodsPriceId);
	AfDeGoodsDo deGoodsDoResult = afDeGoodsService.getByCommonCondition(deGoodsDo);

	if (null != goodsPriceResult && null != deGoodsDo) {

	    // get the original price
	    BigDecimal originalPrice = goodsPriceResult.getActualAmount();
	    // get the lowest Price
	    BigDecimal lowestPrice = deGoodsDoResult.getLowestprice();

	    BigDecimal stage1 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10)).multiply(((new BigDecimal(5).divide(new BigDecimal(20)))));
	    BigDecimal stage2 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10)).multiply(((new BigDecimal(3).divide(new BigDecimal(20)))));
	    BigDecimal stage3 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10)).multiply(((new BigDecimal(2).divide(new BigDecimal(20)))));

	    if (cutCount < 21) {
		// between stage1 and stage2
		result = getRandomPrice(stage2, stage1);
	    } else if (cutCount < 41) {
		// between stage2 and stage3
		result = getRandomPrice(stage2, stage3);
	    } else if (cutCount < 61) {
		// between stage3 and 0.1
		result = getRandomPrice(new BigDecimal(0.1), stage3);
	    } else if (cutCount > 61) {
		// between 0.1 and 1
		result = getRandomPrice(new BigDecimal(0.01), new BigDecimal(0.1));
	    }

	    // if the result after cut is lower than the lowest price .then
	    if (lowestPrice.compareTo(originalPrice.subtract(result)) > 0) {
		result = originalPrice.subtract(lowestPrice);
	    }
	}
	return result;
    }

    /**
     * 
     * @Title: getRandomPrice
     * @Description: 获得随机数，在特定范围的。
     * @param low
     * @param high
     * @return
     * @return BigDecimal
     * @throws
     */
    private BigDecimal getRandomPrice(BigDecimal low, BigDecimal high) {
	BigDecimal result = BigDecimal.ZERO;
	double max = low.doubleValue();
	double min = high.doubleValue();

	result = new BigDecimal(Math.random() * (max - min) + min);
	result.setScale(2, RoundingMode.HALF_EVEN);
	return result;
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

    private AfDeUserCutInfoVo parseDoToVo(AfDeUserCutInfoDo userCutInfo) {
	AfDeUserCutInfoVo vo = new AfDeUserCutInfoVo();
	vo.setCutPrice(userCutInfo.getCutprice());
	vo.setHeadImgUrl(userCutInfo.getHeadimgurl());
	vo.setNickName(userCutInfo.getNickname());
	vo.setRemainPrice(userCutInfo.getRemainprice());
	return vo;
    }

}
