package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsPropertyService;
import com.ald.fanbei.api.biz.service.AfPropertyValueService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPropertyDo;
import com.ald.fanbei.api.dal.domain.AfPropertyValueDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsPriceVo;
import com.ald.fanbei.api.web.vo.AfPropertyVo;
import com.ald.fanbei.api.web.vo.AfpropertyValueVo;

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
						pValueVo.setPropertyValueName(pValueVo.getPropertyValueName());
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

				goodsPriceVo.setActualAmount(priceDo.getActualAmount());
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
