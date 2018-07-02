package com.ald.fanbei.api.web.api.category;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
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
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Component("getGoodsListApi")
public class GetGoodsListApi implements ApiHandle {

    @Resource
    AfGoodsTbkService afGoodsTbkService;
    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfGoodsCategoryService afGoodsCategoryService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSeckillActivityService afSeckillActivityService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Map<String, Object> data = new HashMap<String, Object>();
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),0l);
        Map<String,Object> activityData = new HashMap<String,Object> ();
        AfGoodsCategoryQuery query = getCheckParam(requestDataVo);
        AfGoodsQuery goodsQuery = getCheckParams(requestDataVo);

        List<AfGoodsCategoryDto> list = afGoodsCategoryService.selectGoodsInformation(query);// 按照三级类目查到的商品信息
        List<AfGoodsDo> goodList = afGoodsService.getGoodsVerifyByCategoryId(goodsQuery); // 自营商品审核信息
        List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
//        Iterator<Object> it = array.iterator();
//        while (it.hasNext()) {
//            JSONObject json = (JSONObject) it.next();
//            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
//                it.remove();
//                break;
//            }
//        }
        //查找活动商品
        //判断商品是否处于活动中
        List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
        List<Long> goodsIdList = new ArrayList<>();
        for (AfGoodsDo goodsDo : goodList) {
            goodsIdList.add(goodsDo.getRid());
        }
        if(goodsIdList!=null&&goodsIdList.size()>0){
            activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
        }
        for(AfGoodsDo goodsDo : goodList) {
//            double volume = new Long(goodsDo.getVolume()).intValue();
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            for (AfSeckillActivityGoodsDo activityGoodsDo : activityGoodsDos) {
                if(activityGoodsDo.getGoodsId().equals(goodsDo.getRid())){
                    goodsDo.setSaleAmount(activityGoodsDo.getSpecialPrice());
                    BigDecimal secKillRebAmount = goodsDo.getSaleAmount().multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
                        goodsDo.setRebateAmount(secKillRebAmount);
                    }
                    break;
                }
            }
            String url = "";
            goodsInfo.put("goodName",goodsDo.getName());
            goodsInfo.put("rebateAmount",goodsDo.getRebateAmount());
            goodsInfo.put("saleAmount",goodsDo.getSaleAmount());
            goodsInfo.put("priceAmount",goodsDo.getPriceAmount());
            if(!StringUtil.isEmpty(goodsDo.getGoodsPic1())){
                url = goodsDo.getGoodsPic1();
            }else{
                url = goodsDo.getGoodsIcon();
            }
            goodsInfo.put("goodsIcon",url);
            goodsInfo.put("goodsId",goodsDo.getRid());
            goodsInfo.put("goodsUrl",goodsDo.getGoodsUrl());
            goodsInfo.put("source",goodsDo.getSource());
            goodsInfo.put("numId",goodsDo.getNumId());
//            if(volume>10000){
//                DecimalFormat df = new DecimalFormat("0.00");
//                BigDecimal bigDecimal = new BigDecimal(df.format(volume/10000));
//                bigDecimal.setScale(3,bigDecimal.ROUND_HALF_UP).doubleValue();
//                goodsInfo.put("volume",bigDecimal.toString()+"万");
//            }else{
            goodsInfo.put("volume",goodsDo.getSaleCount());
//            }
            goodsInfo.put("goodsType", "0");
            // 如果是分期免息商品，则计算分期
            Long goodsId = goodsDo.getRid();
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
                    interestFreeArray = JSON.parseArray(interestFreeJson);// 免息规则
                }
            }
            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
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

        for(AfGoodsCategoryDto goodsDo : list) {
//            double volume = new Long(goodsDo.getVolume()).intValue();
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            String url = "";
            goodsInfo.put("goodName",goodsDo.getName());
            goodsInfo.put("rebateAmount",goodsDo.getRebateAmount());
            goodsInfo.put("saleAmount",goodsDo.getSaleAmount());
            goodsInfo.put("priceAmount",goodsDo.getPriceAmount());
            if(!StringUtil.isEmpty(goodsDo.getGoodsPic1())){
                url = goodsDo.getGoodsPic1();
            }else{
                url = goodsDo.getGoodsIcon();
            }
            goodsInfo.put("goodsIcon",url);
            goodsInfo.put("goodsId",goodsDo.getId());
            goodsInfo.put("goodsUrl",goodsDo.getGoodsUrl());
            goodsInfo.put("source",goodsDo.getSource());
            goodsInfo.put("numId",goodsDo.getNumId());
//            if(volume>10000){
//                DecimalFormat df = new DecimalFormat("0.00");
//                BigDecimal bigDecimal = new BigDecimal(df.format(volume/10000));
//                bigDecimal.setScale(3,bigDecimal.ROUND_HALF_UP).doubleValue();
//                goodsInfo.put("volume",bigDecimal.toString()+"万");
//            }else{
                goodsInfo.put("volume",goodsDo.getVolume());
//            }
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
                    goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
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
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),0l);
        AfGoodsCategoryQuery query = new AfGoodsCategoryQuery();
        query.setPageNo(pageNo);
        query.setId(id);
        return query;
    }

    private AfGoodsQuery getCheckParams(RequestDataVo requestDataVo){
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"),0l);
        AfGoodsQuery query = new AfGoodsQuery();
        query.setPageNo(pageNo);
        query.setCategoryId(id);
        return query;
    }

}
