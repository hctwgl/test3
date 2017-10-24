package com.ald.fanbei.api.web.h5.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.context.Theme;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeRandomPropertyService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

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
	AfDeRandomPropertyService afDeRandomPropertyService;
	@Resource
	AfGoodsPriceService afGoodsPriceService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: cutPrice @Description: 砍价接口 @param requst @param response @return
	 * String 返回类型 @throws
	 */
	// TODO: add transaction
	@RequestMapping(value = "/cutPrice", method = RequestMethod.POST)
	public String cutPrice(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
		try {
			String userIdStr = request.getParameter("userId");
			String goodsPriceIdStr = request.getParameter("goodsPriceId");
			String openId = request.getParameter("openId");
			String nickName = request.getParameter("nickName");
			String headImagUrl = request.getParameter("headImgUrl");
			if (StringUtil.isAllNotEmpty(userIdStr, openId, nickName, headImagUrl, goodsPriceIdStr)) {
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
										// time
									int cutCount = usergoodsResult.getCutcount() + 1;
									BigDecimal cutPricee = cutPrice(goodsPriceId, cutCount);

								}
							}
						}
					}

				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
			logger.error("/activity/de/share error = {}", e.getStackTrace());

		} catch (Exception e) {
			logger.error("/activity/de/share error = {}", e.getStackTrace());
		}

		return resultStr;
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
				
			} else if (cutCount < 41) {
				//between stage2 and stage3
				
			} else if (cutCount < 61) {
				//between stage3 and 0.1
				
			} else if (cutCount > 61) {
				//between 0.1 and 1
				
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
}
