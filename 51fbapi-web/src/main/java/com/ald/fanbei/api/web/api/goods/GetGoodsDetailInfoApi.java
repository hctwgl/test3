package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;
import com.ald.fanbei.api.web.vo.GoodsDetailPicInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 *@类描述：GetGoodsDetailInfoApi
 *@author 何鑫 2017年3月3日  11:41:32
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getGoodsDetailInfoApi")
public class GetGoodsDetailInfoApi implements ApiHandle{

	@Resource
	private AfGoodsService afGoodsService;
	
	@Resource
	private TaobaoApiUtil taobaoApiUtil;
	@Resource
	AfActivityGoodsService afActivityGoodsService;
	@Resource
	private AfResourceService afResourceService;
	
	@Resource
	private AfSchemeGoodsService afSchemeGoodsService;
	
	@Resource
	private AfInterestFreeRulesService afInterestFreeRulesService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
    	AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
    	if(null == goods){
			throw new FanbeiException("goods not exist error", FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
    	}
    	//获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        //removeSecondNper(array);
        
    	BigDecimal saleAmount = goods.getSaleAmount();
    	AfSchemeGoodsDo schemeGoodsDo = null;
		try {
			schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
		} catch(Exception e){
			logger.error(e.toString());
		}
		JSONArray interestFreeArray = null;
		if(schemeGoodsDo != null){
			AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
			String interestFreeJson = interestFreeRulesDo.getRuleJson();
			if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
				interestFreeArray = JSON.parseArray(interestFreeJson);
			}
		}
		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
				saleAmount, resource.getValue1(), resource.getValue2());
		AfGoodsDetailInfoVo vo = getGoodsVo(goods);
		if(nperList!= null){
			Map nperMap = nperList.get(nperList.size() - 1);
			vo.setNperMap(nperMap);
		}
		resp.setResponseData(vo);
		return resp;
	}
	
    private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
                it.remove();
                break;
            }
        }
    }
	
	private AfGoodsDetailInfoVo getGoodsVo(AfGoodsDo goods){
		AfGoodsDetailInfoVo vo = new AfGoodsDetailInfoVo();
		List<String> goodsPics = new ArrayList<String>();
		List<GoodsDetailPicInfoVo> goodsDetail = new ArrayList<GoodsDetailPicInfoVo>(); 
		vo.setGoodsId(goods.getRid());
		vo.setGoodsIcon(goods.getGoodsIcon());
		vo.setGoodsName(goods.getName());
		vo.setGoodsUrl(goods.getGoodsUrl());
		vo.setNumId(goods.getNumId());
		vo.setOpenId(goods.getOpenId());
		vo.setRealAmount(goods.getRealAmount()+"");
		vo.setRebateAmount(goods.getRebateAmount()+"");
		vo.setSaleAmount(goods.getSaleAmount());
		vo.setSource(goods.getSource());
		vo.setSaleCount(goods.getSaleCount());
		if(StringUtil.isNotBlank(goods.getGoodsDetail())){
			String[] gdArray = goods.getGoodsDetail().split(";");
			if(gdArray!=null && gdArray.length == Constants.GOODSDETAIL_PIC_PARTS){
				GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], gdArray[1], gdArray[2]);
				goodsDetail.add(goodsDetailPicInfoVo);
			}
		}
		vo.setGoodsDetail(goodsDetail);
		//商品图片汇总处理
		if(StringUtil.isNotBlank(goods.getGoodsPic1())){
			goodsPics.add(goods.getGoodsPic1());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic2())){
			goodsPics.add(goods.getGoodsPic2());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic3())){
			goodsPics.add(goods.getGoodsPic3());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic4())){
			goodsPics.add(goods.getGoodsPic4());
		}
		vo.setGoodsPics(goodsPics);
		//如果为自营商品，numId设置为goodsId
		if(AfGoodsSource.SELFSUPPORT.getCode().equals(goods.getSource())){
			vo.setNumId(goods.getRid()+"");
			//是否限购
			AfActivityGoodsDo afActivityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsIdAndType(goods.getRid());
			if(null != afActivityGoodsDo){
				if(StringUtils.isEmpty(afActivityGoodsDo.getLimitCount()+"") || (afActivityGoodsDo.getLimitCount() == 0)){
					vo.setLimitedPurchase(-1);
				}else{
					vo.setLimitedPurchase(new Long(afActivityGoodsDo.getLimitCount()).intValue());
				}
			}else{
				vo.setLimitedPurchase(-1);
			}
		}else{
			vo.setLimitedPurchase(-1);
		}
		return vo;
	}
	
}
