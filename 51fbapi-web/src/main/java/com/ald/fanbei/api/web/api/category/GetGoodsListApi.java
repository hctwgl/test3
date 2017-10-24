package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsCategoryDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfGoodsCategoryDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsCategoryQuery;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("getGoodsListApi")
public class GetGoodsListApi implements ApiHandle {

    @Resource
    AfGoodsTbkService afGoodsTbkService;

    @Resource
    AfGoodsCategoryService afGoodsCategoryService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfResourceService afResourceService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> data = new HashMap<String, Object>();
        String level = ObjectUtils.toString(requestDataVo.getParams().get("level"),"3");
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),4l);
        Map<String,Object> activityData = new HashMap<String,Object> ();
        AfGoodsCategoryQuery query = getCheckParam(requestDataVo);
        List<AfGoodsCategoryDto> list = afGoodsCategoryService.selectGoodsInformation(query);
        List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        for(AfGoodsCategoryDto goodsDo : list) {
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            goodsInfo.put("goodName",goodsDo.getName());
            goodsInfo.put("rebateAmount",goodsDo.getRebateAmount());
            goodsInfo.put("saleAmount",goodsDo.getSaleAmount());
            goodsInfo.put("priceAmount",goodsDo.getPriceAmount());
            goodsInfo.put("goodsIcon",goodsDo.getGoodsIcon());
            goodsInfo.put("goodsId",goodsDo.getId());
            goodsInfo.put("goodsUrl",goodsDo.getGoodsUrl());
            goodsInfo.put("source",goodsDo.getSource());
            goodsInfo.put("numId",goodsDo.getNumId());
            goodsInfo.put("volume",goodsDo.getVolume());
            goodsInfo.put("saleCount",goodsDo.getSaleCount());
            goodsInfo.put("goodsType", "0");
            // 如果是分期免息商品，则计算分期
            Long goodsId = goodsDo.getId();
            AfSchemeGoodsDo schemeGoodsDo = null;
            try {
                schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
            } catch(Exception e){
                logger.error(e.toString());
            }
            JSONArray interestFreeArray = null;
            if(schemeGoodsDo != null){
                AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                String interestFreeJson = interestFreeRulesDo.getRuleJson();
                if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                    interestFreeArray = JSON.parseArray(interestFreeJson);
                }
            }
            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
            if(nperList!= null){
                goodsInfo.put("goodsType", "1");
                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                String isFree = (String)nperMap.get("isFree");
                if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    nperMap.put("freeAmount", nperMap.get("amount"));
                }
                goodsInfo.put("nperMap", nperMap);
            }
            goodsList.add(goodsInfo);
        }
        data.put("goodsList",goodsList);
        resp.setResponseData(data);
        return resp;
    }

    private AfGoodsCategoryQuery getCheckParam(RequestDataVo requestDataVo){
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),4l);
        AfGoodsCategoryQuery query = new AfGoodsCategoryQuery();
        query.setPageNo(pageNo);
        query.setId(id);
        return query;
    }

}
