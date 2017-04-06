package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsVo;

/**
 * 
 *@类描述：GetCategoryListApi
 *@author 何鑫 2017年2月17日  8:50:37
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCateGoodsListApi")
public class GetCateGoodsListApi implements ApiHandle{

	@Resource
	private AfGoodsService afGoodsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		AfGoodsQuery query = getCheckParam(requestDataVo);
		List<AfGoodsDo> goodsList = afGoodsService.getCateGoodsList(query);
		Map<String,Object> data = getGoodsList(goodsList);
		data.put("pageNo", query.getPageNo());
		resp.setResponseData(data);
		return resp;
	}
	
	private Map<String,Object> getGoodsList(List<AfGoodsDo> goodsList){
		Map<String,Object> data = new HashMap<String,Object>();
		List<AfGoodsVo> goodsVoList = new ArrayList<AfGoodsVo>();
		for (AfGoodsDo afGoods : goodsList) {
			AfGoodsVo vo = new AfGoodsVo();
			vo.setGoodsId(afGoods.getRid());
			vo.setGoodsIcon(afGoods.getGoodsIcon());
			vo.setGoodsName(afGoods.getName());
			vo.setGoodsUrl(afGoods.getGoodsUrl());
			vo.setNumId(afGoods.getNumId());
			vo.setOpenId(afGoods.getOpenId());
			vo.setRealAmount(afGoods.getRealAmount().setScale(2,BigDecimal.ROUND_HALF_UP)+"");
			vo.setRebateAmount(afGoods.getRebateAmount().setScale(2,BigDecimal.ROUND_HALF_UP)+"");
			vo.setSaleAmount(afGoods.getSaleAmount().setScale(2,BigDecimal.ROUND_HALF_UP)+"");
			vo.setThumbnailIcon(afGoods.getThumbnailIcon());
			goodsVoList.add(vo);
		}
		data.put("goodsList", goodsVoList);
		return data;
	}
	
	private AfGoodsQuery getCheckParam(RequestDataVo requestDataVo){
		Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
	    Long categoryId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("categoryId")), 0l);
	    BigDecimal minAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("minAmount")), BigDecimal.ZERO);
	    BigDecimal maxAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("maxAmount")), BigDecimal.ZERO);
	    AfGoodsQuery query = new AfGoodsQuery();
	    query.setPageNo(pageNo);
	    query.setCategoryId(categoryId);
	    query.setMinAmount(minAmount);
	    query.setMaxAmount(maxAmount);
	    return query;
	}
}
