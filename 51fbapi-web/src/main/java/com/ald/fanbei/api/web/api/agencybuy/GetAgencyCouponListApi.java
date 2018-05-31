/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ActivityType;
import com.ald.fanbei.api.common.enums.CouponCateGoryType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @类描述：代买订单可使用优惠券列表获取
 * @author suweili 2017年6月12日上午11:47:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyCouponListApi")
public class GetAgencyCouponListApi implements ApiHandle {
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfSubjectGoodsService afSubjectGoodsService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfModelH5ItemService afModelH5ItemService;
	@Resource
	AfActivityModelService afActivityModelService;
	@Resource
	AfShareGoodsService afShareGoodsService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Resource
	AfGoodsService afGoodsService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("actualAmount"),BigDecimal.ZERO);
		Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"), 0);
		
		/*
		List<AfSubjectGoodsDo> subjectGoods = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
		List<AfUserCouponDto> subjectUserCouponList  = new ArrayList<AfUserCouponDto>();
		if(!CollectionUtil.isEmpty(subjectGoods)){
			AfSubjectGoodsDo subjectGood = subjectGoods.get(0);
			Long subjectId = subjectGood.getParentSubjectId();
			// 获取会场信息
			AfSubjectDo afSubjectDo = afSubjectService.getSubjectInfoById(subjectId + "");
			if(afSubjectDo != null) {
				Long couponCategoryId = afSubjectDo.getCouponId();
				// 查询优惠券分类信息
				AfCouponCategoryDo afCouponCategoryDo = afCouponCategoryService.getCouponCategoryById(couponCategoryId + "");
				if (afCouponCategoryDo != null) {
					String coupons = afCouponCategoryDo.getCoupons();
					JSONArray array = (JSONArray) JSONArray.parse(coupons);
	        		for(int i = 0; i < array.size(); i++){
	        			String couponId = (String)array.getString(i);
	        			// 查询用户领券优惠券
	        			AfUserCouponDto afUserCouponDo = afUserCouponService.getSubjectUserCouponByAmountAndCouponId(userId,actualAmount,couponId);
	        			if (afUserCouponDo != null) {
	        				subjectUserCouponList.add(afUserCouponDo);
	        			}
	        		}
				}
			}
		}
		
		list.addAll(subjectUserCouponList);
		*/	

		//判断是否是秒杀活动
		try{
			AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityService.getStartActivityByGoodsId(goodsId);
			if(afSeckillActivityDo!=null&&afSeckillActivityDo.getType()==2){
				logger.error("secKillActivity type=2 credit userCoupon ");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("couponList",null);
				data.put("pageNo", 1);
				data.put("totalCount", 0);
				resp.setResponseData(data);
				return resp;
			}
		}catch(Exception e){
			logger.error("secKillActivity credit userCoupon error", e);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("couponList",null);
			data.put("pageNo", 1);
			data.put("totalCount", 0);
			resp.setResponseData(data);
			return resp;
		}
		List<AfUserCouponDto>  newList = new ArrayList<AfUserCouponDto>();
		//新版优惠券接口
		//先查询所有有效期内的优惠券
		try {
			AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
			AfCouponCategoryDo  couponCategory  = null;
			AfCouponCategoryDo fCouponCategory = null;
			List<AfModelH5ItemDo> afModelH5ItemList = null;
			List<AfSubjectGoodsDo> subjectGoodsList = null;
			List<AfModelH5ItemDo> modelH5ItemList = null;
			int goodsCount = 0;
			boolean shareFlag = false;
			List<AfUserCouponDto> userCouponList = new ArrayList<AfUserCouponDto>();
			if(context.getAppVersion()>414){
				userCouponList = afUserCouponService.getUserAllAcgencyCouponByAmount(userId,actualAmount);
			}else{
				userCouponList = afUserCouponService.getUserOldAllAcgencyCouponByAmount(userId,actualAmount);
			}
			for(AfUserCouponDto afUserCouponDto : userCouponList){
				try{
					Integer isGlobal = afUserCouponDto.getIsGlobal();
					Long activityId = afUserCouponDto.getActivityId();
					if(isGlobal==0){//全场通用
						newList.add(afUserCouponDto);
					}else if(isGlobal==1){
						String activityType = afUserCouponDto.getActivityType();
						if(StringUtil.equals("EXCLUSIVE_CREDIT",activityType)){
							if(shareFlag==false){
								goodsCount = afShareGoodsService.getCountByGoodsId(goodsId);
								shareFlag = true;
							}
							if(goodsCount!=0){
								if(couponCategory==null){
									couponCategory  = afCouponCategoryService.getCouponCategoryByTag(CouponCateGoryType._EXCLUSIVE_CREDIT_.getCode());
								}
								if(couponCategory != null){
									String coupons = couponCategory.getCoupons();
									JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
									for (int i = 0; i < couponsArray.size(); i++) {
										String couponId = couponsArray.getString(i);
										if(couponId.equals(String.valueOf(afUserCouponDto.getCouponId()))){
											AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
											if (couponDo != null) {
												newList.add(afUserCouponDto);
												break;
											}
										}
									}
								}
							}
						}else if(StringUtil.equals("FIRST_SINGLE",activityType)){
							if(afModelH5ItemList==null){
								afModelH5ItemList = afModelH5ItemService.getModelH5ItemForFirstSingleByGoodsId(goodsId);
							}
							if(afModelH5ItemList!= null && afModelH5ItemList.size()>0){
								for(AfModelH5ItemDo afModelH5ItemDo : afModelH5ItemList) {
									Long modelId = afModelH5ItemDo.getModelId();
									if(modelId.equals(activityId)){
										if(fCouponCategory==null){
											fCouponCategory  = afCouponCategoryService.getCouponCategoryByTag(CouponCateGoryType._FIRST_SINGLE_.getCode());
										}
										if(fCouponCategory != null){
											String coupons = fCouponCategory.getCoupons();
											JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
											for (int i = 0; i < couponsArray.size(); i++) {
												String couponId = couponsArray.getString(i);
												if(couponId.equals(String.valueOf(afUserCouponDto.getCouponId()))){
													AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
													if (couponDo != null) {
														newList.add(afUserCouponDto);
														break;
													}
												}
											}
										}
										break;
									}
								}
							}
						}else if(StringUtil.equals("ENCORE_TEMPLATE",activityType)){
							List<AfActivityModelDo> activityModelList = afActivityModelService.getActivityModelByGoodsId(goodsId);
							for(AfActivityModelDo afActivityModelDo :activityModelList) {
								Long actId = afActivityModelDo.getActivityId();
								if(actId.equals(activityId)){
									newList.add(afUserCouponDto);
									break;
								}
							}
						}else if(StringUtil.equals("ACTIVITY_TEMPLATE",activityType)){
							if(subjectGoodsList==null){
								subjectGoodsList = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
							}
							if(subjectGoodsList!=null&&subjectGoodsList.size()>0){
								for(AfSubjectGoodsDo afSubjectGoodsDo : subjectGoodsList) {
									String subjectId = afSubjectGoodsDo.getSubjectId();
									if(subjectId.equals(String.valueOf(activityId))){
										newList.add(afUserCouponDto);
										break;
									}
								}
							}
						}else if(StringUtil.equals("H5_TEMPLATE",activityType)){
							if(modelH5ItemList == null){
								modelH5ItemList = afModelH5ItemService.getModelH5ItemByGoodsId(goodsId);
							}
							if(modelH5ItemList!=null&&modelH5ItemList.size()>0){
								for(AfModelH5ItemDo afModelH5ItemDo : modelH5ItemList) {
									Long modelId = afModelH5ItemDo.getModelId();
									if(modelId.equals(activityId)){
										newList.add(afUserCouponDto);
										break;
									}
								}
							}
						}
					}else if(isGlobal==2){//指定商品
						String goodsIds = afUserCouponDto.getGoodsIds();//当券信息为商品专用时，goods_ids存放的信息为特定商品的goodsId集合
						if(StringUtils.isNotBlank(goodsIds)){//当前券信息中的goodsId不为空
							goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
							if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(goodsId))){//当前商品id被包含在券信息的商品id集合里面
								newList.add(afUserCouponDto);
							}
						}
					}else if(isGlobal==3){//指定会场
						String goodsIds = afUserCouponDto.getGoodsIds();//当券信息为会场专用时，goods_ids存放的信息二级会场的id集合
						if(StringUtils.isNotBlank(goodsIds)){//当前券信息中的goodsId不为空
							goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
							if(subjectGoodsList==null){
								subjectGoodsList = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
							}
							if(subjectGoodsList!=null&&subjectGoodsList.size()>0){
								for(AfSubjectGoodsDo afSubjectGoodsDo : subjectGoodsList){
									String subjectId = afSubjectGoodsDo.getSubjectId();
									if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(subjectId))){//当前会场id被包含在券信息的会场id集合里面
										newList.add(afUserCouponDto);
										break;
									}
								}
							}
						}

					}else if(isGlobal==4){//指定分类
						String goodsIds = afUserCouponDto.getGoodsIds();//当券信息为分类专用时，goods_ids存放的信息二级会场的id集合
						if(StringUtils.isNotBlank(goodsIds)){//当前券信息中的goodsId不为空
							goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
							if(afGoodsDo!=null){
								Long categoryId = afGoodsDo.getCategoryId();
								if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(categoryId))){//当前会场id被包含在券信息的会场id集合里面
									newList.add(afUserCouponDto);
								}
							}
						}
					}else if(isGlobal==5){//指定品牌
						String goodsIds = afUserCouponDto.getGoodsIds();//当券信息为分类专用时，goods_ids存放的信息二级会场的id集合
						if(StringUtils.isNotBlank(goodsIds)){//当前券信息中的goodsId不为空
							goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
							if(afGoodsDo!=null){
								Long brandId = afGoodsDo.getBrandId();
								if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(brandId))){//当前会场id被包含在券信息的会场id集合里面
									newList.add(afUserCouponDto);
								}
							}
						}
					}
				}catch (Exception e){
					logger.error("getAgencyCouponListApi userCoupon error", e);
				}
			}
		}catch(Exception e){
			logger.error("getAgencyCouponListApi userCoupon error", e);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("couponList",newList.size());
			data.put("pageNo", 1);
			data.put("totalCount", newList.size());
			resp.setResponseData(data);
			return resp;
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("couponList", JSON.toJSON(newList));
		data.put("pageNo", 1);
		data.put("totalCount", newList.size());
		resp.setResponseData(data);
		return resp;
		//新人专享--信用专享优惠券,特定商品
		//——————————————
		/*List<AfUserCouponDto>  cList = new ArrayList<AfUserCouponDto>();
		try{
			if(afShareGoodsService.getCountByGoodsId(goodsId)!=0){
				// 查询
				List<AfUserCouponDto> couponList =   new ArrayList<AfUserCouponDto>();
				List<AfUserCouponDto> h5TemplateCouponList =  afUserCouponService.getActivitySpecialCouponByAmount(userId,actualAmount,null,ActivityType.EXCLUSIVE_CREDIT.getCode());
				if(h5TemplateCouponList !=null && h5TemplateCouponList.size()>0){
					for(AfUserCouponDto userCouponDto:h5TemplateCouponList){
						AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(CouponCateGoryType._EXCLUSIVE_CREDIT_.getCode());
						if(couponCategory != null){
							String coupons = couponCategory.getCoupons();
							JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
							for (int i = 0; i < couponsArray.size(); i++) {
								String couponId = couponsArray.getString(i);
								AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
								if (couponDo != null) {
									if(couponDo.getRid().equals(userCouponDto.getCouponId())){
										couponList.add(userCouponDto);
								  	 }
									}
								}
						}
					}
				}
				cList.addAll(couponList);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("couponList", JSON.toJSON(cList));
				data.put("pageNo", 1);
				data.put("totalCount", cList.size());
				resp.setResponseData(data);
				return resp;
        		}
        	 }catch(Exception e){
        	     logger.error("exclusive credit userCoupon error", e);
        	        Map<String, Object> data = new HashMap<String, Object>();
         		data.put("couponList",cList);
         		data.put("pageNo", 1);
         		data.put("totalCount", 0);
         		resp.setResponseData(data);
         		return resp;
        	 }*/
		
		//新人专享添加逻辑
//		if(afShareGoodsService.getCountByGoodsId(goodsId)!=0){
//			
//			//后端优化:商品详情页面展示的商品价格，各个规格的价格取后台商品的售价即可；（商品的售价会维护成商品折扣后的新人价）
//			//List<AfGoodsPriceDo> byGoodsId = afGoodsPriceService.getByGoodsId(goodsId);
//			//for (AfGoodsPriceDo afGoodsPriceDo : byGoodsId) {
//				
//				//if(afGoodsPriceDo.getActualAmount() != actualAmount){ 
//					Map<String, Object> data = new HashMap<String, Object>();
//					data.put("couponList", null);
//					data.put("pageNo", 1);
//					data.put("totalCount", 0);
//					resp.setResponseData(data);
//					return resp;
//				//}
//			//}
//		}
		//———————mqp doubleEggs add function———————
		
		//新人专享--首单爆品优惠券,特定商品
		//——————————————
		/*List<AfUserCouponDto>  uList = new ArrayList<AfUserCouponDto>();
	    try{
			List<AfModelH5ItemDo> afModelH5ItemList = afModelH5ItemService.getModelH5ItemForFirstSingleByGoodsId(goodsId);
			if(afModelH5ItemList!= null && afModelH5ItemList.size()>0){
				for(AfModelH5ItemDo afModelH5ItemDo : afModelH5ItemList) {
					Long modelId = afModelH5ItemDo.getModelId();
					// 查询
					List<AfUserCouponDto> userCouponList =   new ArrayList<AfUserCouponDto>();
					List<AfUserCouponDto> firstSingleCouponList =  afUserCouponService.getActivitySpecialCouponByAmount(userId,actualAmount,modelId,ActivityType.FIRST_SINGLE.getCode());
					if(firstSingleCouponList !=null && firstSingleCouponList.size()>0){
						for(AfUserCouponDto userCouponDto:firstSingleCouponList){
							AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(CouponCateGoryType._FIRST_SINGLE_.getCode());
							if(couponCategory != null){
								String coupons = couponCategory.getCoupons();
								JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
								for (int i = 0; i < couponsArray.size(); i++) {
									String couponId = couponsArray.getString(i);
									AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
									if (couponDo != null) {
										if(couponDo.getRid().longValue() == userCouponDto.getCouponId().longValue()){
											userCouponList.add(userCouponDto);
											}
										}
									}
							}
						}
					}
					uList.addAll(userCouponList);
				}
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("couponList", JSON.toJSON(uList));
				data.put("pageNo", 1);
				data.put("totalCount", uList.size());
				resp.setResponseData(data);
				return resp;
			 }
	    }catch(Exception e){
		    logger.error("first single userCoupon error", e);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("couponList", uList);
			data.put("pageNo", 1);
			data.put("totalCount", 0);
			resp.setResponseData(data);
			return resp;
		}

		//———————mqp doubleEggs add function———————
		List<AfGoodsDoubleEggsDo> doubleEggsDos = afGoodsDoubleEggsService.getByGoodsId(goodsId);
		if(CollectionUtil.isNotEmpty(doubleEggsDos)){
			//不使用优惠券
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("couponList", null);
			data.put("pageNo", 1);
			data.put("totalCount", 0);
			resp.setResponseData(data);
			return resp;
		}&/
		//———————end mqp doubleEggs add function———————

/*		// 双十二秒杀新增逻辑+++++++++++++>
		if(afGoodsDouble12Service.getByGoodsId(goodsId).size()!=0 || afGoodsDoubleEggsService.getByGoodsId(goodsId) != null){
			//是双十二秒杀活动商品，不使用优惠券
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("couponList", null);
			data.put("pageNo", 1);
			data.put("totalCount", 0);
			resp.setResponseData(data);
			return resp;
		}
		// +++++++++++++++++++++++++<
*/		
		/*List<AfUserCouponDto> list = afUserCouponService.getUserAcgencyCouponByAmount(userId,actualAmount);//全场通用券
		//add by weiqingeng
		List<AfUserCouponDto> specialGoodsCouponList = afUserCouponService.getSpecialGoodsCouponByAmount(userId,actualAmount);//商品专用券
		if(CollectionUtils.isNotEmpty(specialGoodsCouponList)){
			for(AfUserCouponDto specialGoodsCouponDo : specialGoodsCouponList){
				String goodsIds = specialGoodsCouponDo.getGoodsIds();//当券信息为商品专用时，goods_ids存放的信息为特定商品的goodsId集合
				if(StringUtils.isNotBlank(goodsIds)){//当前券信息中的goodsId不为空
					goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
					if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(goodsId))){//当前商品id被包含在券信息的商品id集合里面
						list.add(specialGoodsCouponDo);
					}
				}
			}
		}*/

		/**
		 * 以下是活动专用券
		 */
		// 查询商品是否在H5活动中
		/*List<AfModelH5ItemDo> modelH5ItemList = afModelH5ItemService.getModelH5ItemByGoodsId(goodsId);
		for(AfModelH5ItemDo afModelH5ItemDo : modelH5ItemList) {
			Long modelId = afModelH5ItemDo.getModelId();
			// 查询
			List<AfUserCouponDto> h5TemplateCouponList = afUserCouponService.getActivitySpecialCouponByAmount(userId,actualAmount,modelId,ActivityType.H5_TEMPLATE.getCode());
			list.addAll(h5TemplateCouponList);
		}
		
		// 查询商品是否在返场活动模版中
		List<AfActivityModelDo> activityModelList = afActivityModelService.getActivityModelByGoodsId(goodsId);
		for(AfActivityModelDo afActivityModelDo :activityModelList) {
			Long activityId = afActivityModelDo.getActivityId();
			List<AfUserCouponDto> encodeTemplateCouponList =  afUserCouponService.getActivitySpecialCouponByAmount(userId,actualAmount,activityId,ActivityType.ENCORE_TEMPLATE.getCode());
			list.addAll(encodeTemplateCouponList);
		}
		// 查询商品是否在618会场活动模版中
		List<AfSubjectGoodsDo> subjectGoodsList = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
		for(AfSubjectGoodsDo afSubjectGoodsDo : subjectGoodsList) {
			String subjectId = afSubjectGoodsDo.getSubjectId();
			List<AfUserCouponDto> activityTemplateCouponList =  afUserCouponService.getActivitySpecialCouponByAmount(userId,actualAmount,Long.parseLong(subjectId),ActivityType.ENCORE_TEMPLATE.getCode());
			list.addAll(activityTemplateCouponList);
		}*/

	}
}
