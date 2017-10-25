package com.ald.fanbei.api.web.api.goods;

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
import com.ald.fanbei.api.biz.service.AfPropertyValueService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPropertyDo;
import com.ald.fanbei.api.dal.domain.AfPropertyValueDo;
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
		for (AfGoodsPriceDo afGoodsPriceDo : priceDos) {
		    AfDeUserGoodsDo afDeUserGoodsDo = afDeUserGoodsService.getUserGoodsPrice(context.getUserId(), afGoodsPriceDo.getRid());
		    if(afDeUserGoodsDo !=null)
		    {
			//更新商品价格为砍价后价格
			afGoodsPriceDo.setActualAmount(afGoodsPriceDo.getActualAmount().subtract(afDeUserGoodsDo.getCutprice()));
			logger.info("+++++++++1++++++"+afGoodsPriceDo.toString());
		    }
		}
		logger.info("+++++++++2++++++"+JSON.toJSONString(priceDos));

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

		logger.info("+++++++++3++++++"+JSON.toJSONString(priceDos));
		data.put("propertyData", propertyData);
		data.put("goodsId", goodsId);
		data.put("priceData", priceData);
		resp.setResponseData(data);
		logger.info("+++++++++4++++++"+JSON.toJSONString(resp));
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
