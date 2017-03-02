package com.ald.fanbei.api.web.api.borrow;

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
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfConsumeConfirmVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.domain.XItem;

/**
 * 
 *@类描述：GetConsumeConfirmInfoApi
 *@author 何鑫 2017年2月20日  09:50:48
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConsumeConfirmInfoApi")
public class GetConsumeConfirmInfoApi implements ApiHandle{

	@Resource
	private AfResourceService afResourceService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Resource
	private AfGoodsService afGoodsService;
	
	@Resource
	private TaobaoApiUtil taobaoApiUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		BigDecimal goodsAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsAmount")), BigDecimal.ZERO);
		//获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONObject obj  = JSON.parseObject(resource.getValue());
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		if(null == card){
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
		if(obj == null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		Integer goodsNum = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsNum")), 1);
	    
		List<Map<String,Object>> list = getConsumeList(obj,goodsNum,goodsAmount,resource);
		if(goodsNum>1){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("timeLimitList", list);
			resp.setResponseData(map);
		}else{
			try {
				AfConsumeConfirmVo vo = getConsumeConfirmVo(requestDataVo,card,list,goodsAmount);
				resp.setResponseData(vo);
			} catch (ApiException e) {
				logger.info("getConsumeConfirmInfoApi error:",e);
				throw new FanbeiException(FanbeiExceptionCode.FAILED);
			}
		}
		return resp;
	}
	
	private List<Map<String,Object>> getConsumeList(JSONObject obj,int goodsNum,BigDecimal goodsAmount,AfResourceDo resource){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, Object> entry : obj.entrySet()) {
			Map<String, Object> attrs = new HashMap<String, Object>();
			String key = entry.getKey();
			String value = entry.getValue().toString();
			if (value != null && !"".equals(value.trim())) {
				BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
				BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
				String[] range = StringUtil.split(resource.getValue2(), ",");
				if(null != range && range.length==2){
					rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
					rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
				}
				BigDecimal perAmount =  BigDecimalUtil.getConsumeAmount(goodsAmount.multiply(new BigDecimal(goodsNum)), Integer.parseInt(key), 
						new BigDecimal(value).divide(new BigDecimal(Constants.MONTH_OF_YEAR),8,BigDecimal.ROUND_HALF_UP), 
						BigDecimalUtil.getTotalPoundage(goodsAmount.multiply(new BigDecimal(goodsNum)), 
								Integer.parseInt(key),new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd));
				attrs.put("nper", key);
				attrs.put("perAmount", perAmount);
				list.add(attrs);
			}
		}
		return list;
	}
	
	private AfConsumeConfirmVo getConsumeConfirmVo(RequestDataVo requestDataVo,AfUserBankcardDo card,List<Map<String,Object>> list,BigDecimal goodsAmount) throws ApiException{
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0);
	    String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"));
		AfConsumeConfirmVo vo = new AfConsumeConfirmVo();
		AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
	    if(null ==goods){
	    	Map<String, Object> params = new HashMap<String, Object>();
	    	params.put("numIid", numId);
	    	List<XItem> itemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
	    	if(null == itemList ||itemList.size()==0){
				throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
	    	}else{
	    		XItem item= itemList.get(0);
	    		vo.setGoodsIcon(item.getPicUrl());
	    		vo.setGoodsName(item.getTitle());
	    		vo.setOpenId(item.getOpenIid());
	    	}
	    }else{
	    	vo.setGoodsIcon(goods.getGoodsIcon());
			vo.setGoodsName(goods.getName());
			vo.setOpenId(goods.getOpenId());
	    }
	    vo.setGoodsId(goodsId);
		vo.setSaleAmount(goodsAmount);
		vo.setBankName(card.getBankName());
		vo.setCardId(card.getRid());
		vo.setCardNo(StringUtil.getLastString(card.getCardNumber(), 4));
		vo.setCardIcon(card.getBankIcon());
		vo.setTimeLimitList(list);
		return vo;
	}
}
