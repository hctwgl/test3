package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsPropertyService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfPropertyValueService;
import com.ald.fanbei.api.biz.service.AfShareGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPropertyDo;
import com.ald.fanbei.api.dal.domain.AfPropertyValueDo;
import com.ald.fanbei.api.dal.domain.AfShareGoodsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsPriceVo;
import com.ald.fanbei.api.web.vo.AfPropertyVo;
import com.ald.fanbei.api.web.vo.AfpropertyValueVo;
import com.alibaba.fastjson.JSON;

/**
 * 
 * <p>
 * Title:GetGoodsSpecApi
 * <p>
 * <p>
 * Description:
 * <p>
 * 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年7月14日下午1:58:09
 *
 */
@Component("getGoodsSpecApi")
public class GetGoodsSpecApi implements ApiHandle {

	@Resource
	AfGoodsPriceService afGoodsPriceService;

	@Resource
	AfGoodsPropertyService afGoodsPropertyService;

	@Resource
	AfPropertyValueService afPropertyValueService;
	
	@Autowired
	AfDeUserGoodsService afDeUserGoodsService;
	
	@Resource
	AfOrderService afOrderService;
	
	@Resource
	AfShareGoodsService afShareGoodsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);

		Map<String, Object> data = new HashMap<>();
		List<AfGoodsPriceVo> priceData = new ArrayList<>();
		List<AfPropertyVo> propertyData = new ArrayList<>();
		AfGoodsPriceDo goodsPriceDo = new AfGoodsPriceDo();
		AfGoodsPropertyDo propertyDo = new AfGoodsPropertyDo();

		goodsPriceDo.setGoodsId(goodsId);
		List<AfGoodsPriceDo> priceDos = afGoodsPriceService.getListByCommonCondition(goodsPriceDo);
		priceData = convertListForPrice(priceDos);
				
		//双十一砍价活动增加逻辑
		for ( AfGoodsPriceVo afGoodsPriceDo : priceData) {
		    AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getUserGoodsPrice(context.getUserId(), afGoodsPriceDo.getPriceId());
		    if(afDeUserGoodsDo !=null)
		    {
		    //后端优化:商品详情页面展示的商品价格，各个规格的价格取后台商品的售价即可；（商品的售价会维护成商品折扣后的新人价）
			//更新商品价格为砍价后价格
			//BigDecimal actualAmount = afGoodsPriceDo.getActualAmount().subtract(afDeUserGoodsDo.getCutprice());
			//afGoodsPriceDo.setActualAmount(actualAmount);
		    }
		}
		
		//mqp_新人专享活动增加逻辑
		Long userId = context.getUserId();
		if (userId != null) {
			//查询用户订单数
			int oldUserOrderAmount = afOrderService.getOldUserOrderAmount(userId);
			if(oldUserOrderAmount==0){
				//是新用户
				AfShareGoodsDo condition = new AfShareGoodsDo();
				condition.setGoodsId(goodsId);
				AfShareGoodsDo resultDo = afShareGoodsService.getByCommonCondition(condition);
				if (null != resultDo) {
					//这个商品是否是砍价商品
					BigDecimal decreasePrice = resultDo.getDecreasePrice();
					for ( AfGoodsPriceVo afGoodsPriceDo : priceData) {
						//更新商品价格为砍价后价格
						BigDecimal actualAmount = afGoodsPriceDo.getActualAmount().subtract(decreasePrice);
						afGoodsPriceDo.setActualAmount(actualAmount);
					    
					}
				}
				
			}
		}
		//----------------------------------------------

		propertyDo.setGoodsId(goodsId);
		List<AfGoodsPropertyDo> propertyDos = afGoodsPropertyService.getListByCommonCondition(propertyDo);
		if (propertyDos != null && propertyDos.size() > 0) {
			for (AfGoodsPropertyDo pdo : propertyDos) {
				AfPropertyVo propertyVo = new AfPropertyVo();

				List<AfPropertyValueDo> valueDos = new ArrayList<>();
				List<AfpropertyValueVo> propertyValues = new ArrayList<>();
				AfPropertyValueDo valueDo = new AfPropertyValueDo();
				valueDo.setPropertyId(pdo.getRid());
				valueDos = afPropertyValueService.getListByCommonCondition(valueDo);

				if (valueDos != null && valueDos.size() > 0) {
					for (AfPropertyValueDo pvd : valueDos) {
						AfpropertyValueVo pValueVo = new AfpropertyValueVo();
						pValueVo.setPropertyValueId(pvd.getRid());
						pValueVo.setPropertyValueName(pvd.getValue());
						propertyValues.add(pValueVo);
					}
				}

				propertyVo.setPropertyId(pdo.getRid());
				propertyVo.setPropertyName(pdo.getName());
				propertyVo.setPropertyValues(propertyValues);
				propertyData.add(propertyVo);
			}

		}

		data.put("propertyData", propertyData);
		data.put("goodsId", goodsId);
		data.put("priceData", priceData);
		resp.setResponseData(data);
		return resp;
	}

	private List<AfGoodsPriceVo> convertListForPrice(List<AfGoodsPriceDo> priceDos) {
		List<AfGoodsPriceVo> propertyData = new ArrayList<>();
		if (priceDos != null && priceDos.size() > 0) {
			for (AfGoodsPriceDo priceDo : priceDos) {
				AfGoodsPriceVo goodsPriceVo = new AfGoodsPriceVo();

				goodsPriceVo.setStock(priceDo.getStock());
				goodsPriceVo.setActualAmount(priceDo.getActualAmount());
				goodsPriceVo.setIsSale(priceDo.getIsSale());
				goodsPriceVo.setPriceAmount(priceDo.getPriceAmount());
				goodsPriceVo.setPriceId(priceDo.getRid());
				goodsPriceVo.setPropertyValueIds(priceDo.getPropertyValueIds());
				goodsPriceVo.setPropertyValueNames(priceDo.getPropertyValueNames());

				propertyData.add(goodsPriceVo);
			}
		}
		return propertyData;
	}

}
