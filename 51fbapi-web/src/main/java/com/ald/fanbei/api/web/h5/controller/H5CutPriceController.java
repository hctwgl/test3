package com.ald.fanbei.api.web.h5.controller;

import java.math.BigDecimal;
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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfDeUserCutInfoVo;
import com.sun.tools.javac.util.Context.Key;

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
			if(goodsPriceId == null || userName == null || userName.isEmpty() ){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/goodsInfo :  goodsPriceId = {}",goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//查用户的商品砍价详情
			AfDeUserGoodsDo  afDeUserGoodsDo = new AfDeUserGoodsDo();
			afDeUserGoodsDo.setUserid(userId);
			afDeUserGoodsDo.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsInfoDto afDeUserGoodsInfoDto = afDeUserGoodsService.getUserGoodsInfo(afDeUserGoodsDo);
			logger.info("h5 afDeUserGoodsInfoDto = {}",afDeUserGoodsInfoDto);
			//结束时间
			if(afDeUserGoodsInfoDto != null){
			    long endTime = afDeGoodsService.getActivityEndTime();
			    afDeUserGoodsInfoDto.setEndTime(endTime);
			}else{
			    resultStr = H5CommonResponse.getNewInstance(false, "获取商品砍价详情失败",null,afDeUserGoodsInfoDto).toString();
			    return resultStr;
			}
			//转成vo?
			resultStr = H5CommonResponse.getNewInstance(true, "获取商品砍价详情成功",null,afDeUserGoodsInfoDto).toString();
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
			//context = doH5Check(request, true);
		    	String userName = ObjectUtils.toString(request.getParameter("userName"));
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			Integer pageNo = NumberUtil.objToInteger(request.getParameter("pageNo"));
			
			if(goodsPriceId == null || pageNo == null){
			    resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
			    return resultStr;
			}
			logger.info("/activity/de/friend :ygoodsPriceId = {}, goodsPriceId = {}",goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//goodsPriceId 和userId 查询 userGoodsId
			long userGoodsId = 0;
			AfDeUserGoodsDo  queryUserGoods = new AfDeUserGoodsDo();
			queryUserGoods.setUserid(userId);
			queryUserGoods.setGoodspriceid(goodsPriceId);
			AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getByCommonCondition(queryUserGoods);
			logger.info("h5 afDeUserGoodsDo = {}",afDeUserGoodsDo);
			if(afDeUserGoodsDo!=null){
			    userGoodsId = afDeUserGoodsDo.getRid();
			}
			//获取商品砍价详情用户列表
			Map<String,Object> map = new  HashMap<String,Object>();
			AfDeUserCutInfoQuery queryCutInfo = new  AfDeUserCutInfoQuery();
			queryCutInfo.setUsergoodsid(userGoodsId);
			queryCutInfo.setPageNo(pageNo);
			List<AfDeUserCutInfoDo> afDeUserCutInfoList = afDeUserCutInfoService.getAfDeUserCutInfoList(queryCutInfo);
			logger.info("h5 afDeUserCutInfoList = {}",afDeUserCutInfoList);
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
   		        map.put("endTime", endTime);   		        
   			resultStr = H5CommonResponse.getNewInstance(true, "获取活动结束时间成功",null,map).toString();
 
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
				
				//try 1000 times to get the lock 
				String key = Constants.CACHKEY_CUT_PRICE_LOCK + userIdStr;
				boolean isLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
				
				try {
					if (StringUtil.isAllNotEmpty(userIdStr, openId, nickName, headImagUrl, goodsPriceIdStr) && isLock) {
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
								// to judge if this wechat user has already helped this
								// user
								AfDeUserCutInfoDo userCutInfoDo = new AfDeUserCutInfoDo();
								userCutInfoDo.setOpenid(openId);
								userCutInfoDo.setUsergoodsid(usergoodsResult.getRid());
								AfDeUserCutInfoDo userCutInfoDoResult = afDeUserCutInfoService
										.getByCommonCondition(userCutInfoDo);
								if (null == userCutInfoDoResult) {// already helped to
																	// cut
									data.put("code", 2);// already been bought
									resultStr = H5CommonResponse.getNewInstance(false, "重复砍价", "", data).toString();
								} else {// this wechat user could help this user to cut
										// this goods
										// to see if this user has already put this
										// goods to the lowest price.
									AfGoodsPriceDo goodsPriceResult = afGoodsPriceService.getById(usergoodsResult.getRid());
									AfDeGoodsDo deGoodsDo = new AfDeGoodsDo();
									deGoodsDo.setGoodspriceid(goodsPriceId);
									AfDeGoodsDo deGoodsDoResult = afDeGoodsService.getByCommonCondition(deGoodsDo);
									if (null != goodsPriceResult && deGoodsDoResult != null) {
										BigDecimal originalPrice = goodsPriceResult.getActualAmount();
										BigDecimal cutPrice = usergoodsResult.getCutprice();
										BigDecimal lowestPrice = deGoodsDoResult.getLowestprice();
										if (lowestPrice.equals(originalPrice.subtract(cutPrice))) { // already
																									// the
																									// lowest
																									// price
											// update the cutCount Field and insert one
											// record for table cutInfo
											// TODO:add lock :Lock the cutCount Field
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
											resultStr = H5CommonResponse.getNewInstance(false, "已砍至最低价，无法完成本次砍价", "", data)
													.toString();
										} else {// still could cut price
											
												// TODO:add lock
												// calculate the cutPrice for current
											int cutCount = usergoodsResult.getCutcount() + 1;
											BigDecimal cutPricee = cutPrice(goodsPriceId, cutCount);
											
											AfDeUserCutInfoDo insertDo = new AfDeUserCutInfoDo();
											insertDo.setCutprice(BigDecimal.ZERO);
											insertDo.setGmtCreate(new Date());
											insertDo.setGmtModified(new Date());
											insertDo.setHeadimgurl(headImagUrl);
											insertDo.setNickname(nickName);
											insertDo.setOpenid(openId);
											insertDo.setRemainprice(cutPricee);
											insertDo.setUsergoodsid(usergoodsResult.getRid());
											afDeUserCutInfoService.saveRecord(insertDo);
											
											int cutCountt = usergoodsResult.getCutcount() + 1;
											usergoodsResult.setCutcount(cutCountt);
											usergoodsResult.setCutprice(cutPricee);
											usergoodsResult.setGmtModified(new Date());
											
											//if after cutPrice is the lowest price
											if (lowestPrice.compareTo(originalPrice.subtract(cutPricee)) == 0) {
												usergoodsResult.setGmtCompletetime(new Date());
												usergoodsResult.setCompletecount(cutCountt);
											}
											
											afDeUserGoodsService.updateById(usergoodsResult);
										}
									}
								}
							}

						}
					}
				} catch (FanbeiException e) {
					resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
					logger.error("/activity/de/share error = {}", e.getStackTrace());
					status.setRollbackOnly();
				} catch (Exception e) {
					logger.error("/activity/de/share error = {}", e.getStackTrace());
					status.setRollbackOnly();
				}finally{
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
	 * @Title: cutPrice @Description: calculate cut price @param
	 * goodsPriceId @return @return BigDecimal @throws
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

			BigDecimal stage1 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10))
					.multiply(((new BigDecimal(5).divide(new BigDecimal(20)))));
			BigDecimal stage2 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10))
					.multiply(((new BigDecimal(3).divide(new BigDecimal(20)))));
			BigDecimal stage3 = (originalPrice.subtract(lowestPrice)).divide(new BigDecimal(10))
					.multiply(((new BigDecimal(2).divide(new BigDecimal(20)))));

			if (cutCount < 21) {
				//between stage1 and stage2
				result = getRandomPrice(stage2, stage1);
			} else if (cutCount < 41) {
				//between stage2 and stage3
				result = getRandomPrice(stage2, stage3);
			} else if (cutCount < 61) {
				//between stage3 and 0.1
				result = getRandomPrice(new BigDecimal(0.1), stage3);
			} else if (cutCount > 61) {
				//between 0.1 and 1
				result = getRandomPrice(new BigDecimal(0.01), new BigDecimal(0.1));
			}
			
			//if the result after cut is lower than the lowest price .then 
			if (lowestPrice.compareTo(originalPrice.subtract(result)) > 0) {
				result = originalPrice.subtract(lowestPrice);
			}
		}
		return result;
	}

	private BigDecimal getRandomPrice(BigDecimal low,BigDecimal high) {  
		BigDecimal result = BigDecimal.ZERO;
        double max = low.doubleValue();
        double min = high.doubleValue();
        for (int i = 0; i < 10; i++) {  
            BigDecimal db = new BigDecimal(Math.random() * (max - min) + min);  
            System.out.println(db.setScale(4, BigDecimal.ROUND_HALF_UP) 
                    .toString());  
        }  
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
		vo.setCutprice(userCutInfo.getCutprice());
		vo.setHeadImgUrl(userCutInfo.getHeadimgurl());
		vo.setNickname(userCutInfo.getNickname());
		vo.setRemainPrice(userCutInfo.getRemainprice());
		return vo;
	}
	
}
