package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author luoxiao @date 2018/4/23 19:31
 * @类描述：不同类型活动的商品列表
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("appActivityGoodListUtil")
public class AppActivityGoodListUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private AfResourceService afResourceService;

    @Resource
    private AfInterestFreeRulesService afInterestFreeRulesService;

    @Resource
    AfActivityGoodsService afActivityGoodsService;

    @Resource
    private AfSeckillActivityService afSeckillActivityService;

    @Resource
    private BizCacheUtil bizCacheUtil;

    @Resource
    private AfSchemeGoodsService afSchemeGoodsService;

    /**
     * 活动管理-H5页面管理 商品列表
     * @param data
     * @param activityId
     */
    public void getH5PageActivityGoodList(Map<String, Object> data, Long activityId){
        String key = CacheConstants.CACHE_KEY_H5_PAGE_ACTIVITY_GOODS_PREFIX + activityId;
        List<Map<String, Object>> goodList = bizCacheUtil.getObjectList(key);

        if(null == goodList){
            List<AfEncoreGoodsDto> activityGoodsDoList = afActivityGoodsService.listNewEncoreGoodsByActivityId(activityId);
            if(null == activityGoodsDoList){
                return;
            }

            //获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            //删除2分期
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }

            goodList = Lists.newArrayList();
            for(AfEncoreGoodsDto goodsDo : activityGoodsDoList) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goodName", goodsDo.getName());
                goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
                goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
                goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
                goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
                goodsInfo.put("goodsId", goodsDo.getRid());
                goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
                goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
                goodsInfo.put("source", goodsDo.getSource());
                String doubleRebate = goodsDo.getDoubleRebate();
                goodsInfo.put("doubleRebate", "0".equals(doubleRebate) ? "N" : "Y");
                goodsInfo.put("goodsType", "0");
                goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
                // 如果是分期免息商品，则计算分期
                Long goodsId = goodsDo.getRid();
                AfSchemeGoodsDo schemeGoodsDo = null;
                try {
                    schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
                } catch (Exception e) {
                    logger.error(e.toString());
                }
                JSONArray interestFreeArray = null;
                if (schemeGoodsDo != null) {
                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                    String interestFreeJson = interestFreeRulesDo.getRuleJson();
                    if (org.apache.commons.lang.StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                        interestFreeArray = JSON.parseArray(interestFreeJson);
                    }
                }
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                        goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");

                if (nperList != null) {
                    goodsInfo.put("goodsType", "1");
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    goodsInfo.put("nperMap", nperMap);
                }

                goodList.add(goodsInfo);
            }
            bizCacheUtil.saveListByTime(key,goodList,Constants.SECOND_OF_TEN_MINITS);
        }

        data.put("goodList", goodList);
    }

    /**
     * 秒杀商品列表
     * @param userId
     * @param activityId
     * @param data
     */
    public void getSecKillGoodList(Long userId, Long activityId, Map<String, Object> data) {
        List<HomePageSecKillGoods> list = afSeckillActivityService.getHomePageSecKillGoodsById(userId, activityId);

        if (null != list && !list.isEmpty()) {
            data.put("startTime", list.get(0).getActivityStart().getTime());
            data.put("endTime", list.get(0).getActivityEnd().getTime());
        }
        data.put("currentTime", new Date().getTime());

        List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
        // 获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }

        for (HomePageSecKillGoods homePageSecKillGoods : list) {
            Map<String, Object> goodsInfo = new HashMap<String, Object>();
            goodsInfo.put("goodsName", homePageSecKillGoods.getGoodName());
            goodsInfo.put("rebateAmount", homePageSecKillGoods.getRebateAmount());
            goodsInfo.put("saleAmount", homePageSecKillGoods.getSaleAmount());
            goodsInfo.put("priceAmount", homePageSecKillGoods.getPriceAmount());
            goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());
            goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());
            goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());
            goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());
            goodsInfo.put("goodsType", "0");
            goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());
            goodsInfo.put("volume", homePageSecKillGoods.getVolume());
            goodsInfo.put("total", homePageSecKillGoods.getTotal());
            goodsInfo.put("source", homePageSecKillGoods.getSource());
            goodsInfo.put("activityId", homePageSecKillGoods.getActivityId());
            // 如果是分期免息商品，则计算分期
            Long goodsId = homePageSecKillGoods.getGoodsId();
            JSONArray interestFreeArray = null;
            if (homePageSecKillGoods.getInterestFreeId() != null) {
                AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(homePageSecKillGoods.getInterestFreeId().longValue());
                String interestFreeJson = interestFreeRulesDo.getRuleJson();
                if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                    interestFreeArray = JSON.parseArray(interestFreeJson);
                }
            }

            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    homePageSecKillGoods.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");
            if (nperList != null) {
                goodsInfo.put("goodsType", "1");
                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                String isFree = (String) nperMap.get("isFree");
                if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                    nperMap.put("freeAmount", nperMap.get("amount"));
                }
                goodsInfo.put("nperMap", nperMap);
            }
            goodsList.add(goodsInfo);
        }

        data.put("goodsList", goodsList);
    }
}
