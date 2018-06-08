package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.ActivityGoodsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ActivityType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfActivityReservationGoodsUserDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Resource
    AfSubjectGoodsService afSubjectGoodsService;

    @Resource
    AfModelH5ItemService afModelH5ItemService;

    @Resource
    AfSubjectService afSubjectService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfCouponService afCouponService;

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserCouponService afUserCouponService;

    @Resource
    AfSchemeService afSchemeService;

    @Resource
    AfModelH5Service afModelH5Service;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    ExecutorService pool = Executors.newFixedThreadPool(1);

    @Resource
    ActivityGoodsUtil activityGoodsUtil;
    @Resource
    AfActivityUserSmsService afActivityUserSmsService;
    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    private AfActivityReservationGoodsUserDao afActivityReservationGoodsUserDao;

    /**
     * 活动管理-新增活动 商品列表
     * @param data
     * @param activityId
     */
    public void getActivityGoodList(Map<String, Object> data, Long activityId){
        String key = CacheConstants.CACHE_KEY_NEW_ACTIVITY_GOODS_PREFIX + activityId;
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
            BigDecimal saleAmount = null;
            for(AfEncoreGoodsDto goodsDo : activityGoodsDoList) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("goodName", goodsDo.getName());
                goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
                if(null != goodsDo.getActivityPrice()){
                    saleAmount = goodsDo.getActivityPrice();
                }
                else{
                    saleAmount = goodsDo.getSaleAmount();
                }
                goodsInfo.put("saleAmount", saleAmount);
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
                        saleAmount, resource.getValue1(), resource.getValue2(), goodsId, "0");

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
            goodsInfo.put("rebateAmount", homePageSecKillGoods.getRebateAmount());//返利金额
            goodsInfo.put("saleAmount", homePageSecKillGoods.getSaleAmount());//折后价
            goodsInfo.put("priceAmount", homePageSecKillGoods.getPriceAmount());//商品原价
            goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());//活动价
            goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());//商品图标
            goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());//
            goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());//商品链接
            goodsInfo.put("goodsType", "0");//（0:不分期;1:分期）
            goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());//是否秒杀（1：已秒杀）
            goodsInfo.put("volume", homePageSecKillGoods.getVolume());//售出数量
            goodsInfo.put("goodsCount", homePageSecKillGoods.getTotal());//商品限购数量
            goodsInfo.put("source", homePageSecKillGoods.getSource());
            goodsInfo.put("activityId", homePageSecKillGoods.getActivityId());
            goodsInfo.put("saleCount", 0);
            // 如果是分期免息商品，则计算分期
            Long goodsId = homePageSecKillGoods.getGoodsId();
            JSONArray interestFreeArray = null;
            if (homePageSecKillGoods.getInterestFreeId() != null) {//interestFreeId : 免息规则id
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

    /**
     * 分会场
     * @param modelId
     * @param userName
     * @param activityName
     * @param activityStartTime
     * @return
     */
    public JSONObject partActivityInfoV2(String modelId, String userName, String activityName, String activityStartTime) {
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        JSONObject jsonObj = new JSONObject();

        //获取借款分期配置信息
        final AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        final JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }

        // 根据modelId 取优惠券信息
        List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(Long.parseLong(modelId),
                ActivityType.ACTIVITY_TEMPLATE.getCode());
        AfUserDo userDo = afUserService.getUserByUserName(userName);

        for (AfCouponDto couponDto : couponList) {
            // 判断用户是否领
            if (userDo == null) {
                couponDto.setUserAlready(0);
            } else {
                int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
                couponDto.setUserAlready(pickCount);
            }
        }
        jsonObj.put("nowDate", new Date());
        jsonObj.put("couponList", couponList);

//        //获取可用额度
//        AfUserAccountSenceDo userAccountInfo = new AfUserAccountSenceDo();
//        try {
//            if (userDo != null) {
//                userAccountInfo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userDo.getRid());
//                if (userAccountInfo == null) {
//                    userAccountInfo = new AfUserAccountSenceDo();
//                    userAccountInfo.setAuAmount(new BigDecimal(5000));
//                    userAccountInfo.setUsedAmount(new BigDecimal(0));
//                } else {
//                    // 通过强风控审核
//                    // 授予的额度
//                    BigDecimal onlineAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount());
//                    // 临时额度
//                    AfInterimAuDo interimAuDo = afBorrowBillService.selectInterimAmountByUserId(userDo.getRid());
//                    if (interimAuDo != null
//                            && interimAuDo.getGmtFailuretime().getTime() > new Date().getTime()) {
//                        onlineAmount = onlineAmount.add(interimAuDo.getInterimAmount()).subtract(interimAuDo.getInterimUsed());
//                    }
//                    if (onlineAmount.compareTo(BigDecimal.ZERO) < 0) {
//                        onlineAmount = BigDecimal.ZERO;
//                    }
//                    userAccountInfo.setAuAmount(onlineAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
//                    userAccountInfo.setUsedAmount(new BigDecimal(0));
//                }
//            } else {
//                userAccountInfo.setAuAmount(new BigDecimal(5000));
//                userAccountInfo.setUsedAmount(new BigDecimal(0));
//            }
//        } catch (Exception e) {
//            logger.error("partActivityInfoV2 get account error for:" + e);
//        }
//        jsonObj.put("userAccountInfo", userAccountInfo);
        AfSeckillActivityQuery query = new AfSeckillActivityQuery();
        query.setName(activityName);
        query.setGmtStart(DateUtil.parseDate(activityStartTime));

        // 查询会场下所有二级会场
        final List<AfModelH5ItemDo> subjectList = afModelH5ItemService.getModelH5ItemListByModelIdAndModelTypeSortById(Long.parseLong(modelId), "SUBJECT");
        if (subjectList == null || subjectList.size() == 0) {
            jsonObj.put("error", "分会场信息为空");
            return jsonObj;
        }
        AfModelH5ItemDo subjectH5ItemDo = subjectList.get(0);
        String secSubjectId = subjectH5ItemDo.getItemValue();
        AfSubjectDo parentSubjectDo = afSubjectService.getSubjectInfoById(secSubjectId);
        String subjectName = parentSubjectDo.getName();
        jsonObj.put("modelName", subjectName); // 主会场名称
        List<Map> activityList = new ArrayList<Map>();

        String cacheKey = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST.getCode();
        String cacheKey2 = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST_CACHE2.getCode();
        String processKey = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode();
        activityList = bizCacheUtil.getObjectList(cacheKey);
        logger.info("partActivityInfoV2" + Thread.currentThread().getName() + "1activityList:");
        if (activityList == null) {
            boolean isGetLock = bizCacheUtil.getLock30Second(processKey, "1");
            activityList = bizCacheUtil.getObjectList(cacheKey2);
            logger.info("partActivityInfoV2" + Thread.currentThread().getName() + "2activityList is null,isGetLock:" + isGetLock);

            //调用异步请求加入缓存
            if (isGetLock) {
                logger.info("partActivityInfoV2" + Thread.currentThread().getName() + "3activityList is null");
                Runnable process = new GetActivityListThread(subjectList, resource, array, afSubjectService, afSubjectGoodsService, afSchemeGoodsService, afInterestFreeRulesService,
                        bizCacheUtil, afSeckillActivityService, activityGoodsUtil, query);
                pool.execute(process);
            }
        }
       // activityList = null;
        if (activityList == null) {
            activityList = getActivityPartList(subjectList, resource, array);
            bizCacheUtil.saveListByTime(cacheKey, activityList, 10 * 60);
            bizCacheUtil.saveListForever(cacheKey2, activityList);
        }

        jsonObj.put("activityPartList", activityList);
        return jsonObj;
    }

    /**
     * 活动预售商品列表
     * @return
     */
    public JSONObject reservationGoodsList(Map<String, Object> map) {

        // 查询会场下所有商品信息
        List<AfGoodsDo> subjectGoodsList = afActivityReservationGoodsUserDao.getReservationGoodsList( map);

        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        JSONObject jsonObj = new JSONObject();

        //获取借款分期配置信息
        final AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        final JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        List<Map> activityGoodsList  = new ArrayList<Map>();
        BigDecimal saleAmount = null;
        for(AfGoodsDo goodsDo : subjectGoodsList) {
            Map activityGoodsInfo = new HashMap();
            activityGoodsInfo.put("goodName",goodsDo.getName());
            activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());

            if(null != goodsDo.getActivityPrice()){
                saleAmount = goodsDo.getActivityPrice();
            }
            else{
                saleAmount = goodsDo.getSaleAmount();
            }

            //返利金额
            BigDecimal secKillRebAmount = BigDecimalUtil.multiply(saleAmount, goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
            if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
                activityGoodsInfo.put("rebateAmount", secKillRebAmount);
            }

            activityGoodsInfo.put("saleAmount", saleAmount);
            activityGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
            activityGoodsInfo.put("goodsId", goodsDo.getRid());
            activityGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
            activityGoodsInfo.put("source", goodsDo.getSource());
            activityGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
            activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
            activityGoodsInfo.put("remark", goodsDo.getRemark());
            // 如果是分期免息商品，则计算分期
            AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsDo.getRid());
            JSONArray interestFreeArray = null;
            if(null != afSchemeGoodsDo){
                Long goodsId = goodsDo.getRid();
                AfSchemeDo  afSchemeDo = null;
                try {
                    afSchemeDo = afSchemeService.getSchemeById(afSchemeGoodsDo.getSchemeId());
                } catch(Exception e){
                    logger.error(e.toString());
                }

                if(afSchemeDo != null){
                    if (freeflag(afSchemeDo.getGmtStart(),afSchemeDo.getGmtEnd(),afSchemeDo.getIsOpen()) ){
                        AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(afSchemeGoodsDo.getInterestFreeId());
                        String interestFreeJson = interestFreeRulesDo.getRuleJson();
                        if (org.apache.commons.lang.StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                            interestFreeArray = JSON.parseArray(interestFreeJson);
                        }
                    }
                }
            }


            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                    saleAmount, resource.getValue1(), resource.getValue2(),goodsDo.getRid(),"0");

            if(nperList!= null){
                activityGoodsInfo.put("goodsType", "1");
                Map nperMap = nperList.get(nperList.size() - 1);
                activityGoodsInfo.put("nperMap", nperMap);
            }
            activityGoodsList.add(activityGoodsInfo);
        }
        jsonObj.put("reservationGoodsList", activityGoodsList);

        return jsonObj;
    }

    private List<Map> getActivityPartList(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array) {
        //获取所有活动
        logger.info("partActivityInfoV2 getFrom sql");
        List<Map> activityList = new ArrayList<Map>();
        try{
            for(AfModelH5ItemDo subjectDo : subjectList) {
                Map activityInfoMap = new HashMap();
                String subjectId = subjectDo.getItemValue();
                // 查询会场信息
                AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
                if(subjectInfo == null) {
                    return null;
                }
                activityInfoMap.put("name", subjectInfo.getName());
                activityInfoMap.put("subjectId", subjectInfo.getId());
                // 获取一级会场名称
                AfSubjectDo parentSubjectInfo = afSubjectService.getParentSubjectInfoById(subjectId);
                String activityName = "";
                if(parentSubjectInfo != null){
                    activityName = parentSubjectInfo.getName();
                }

                // 查询会场下所有商品信息
                List<AfGoodsDo> subjectGoodsList = afSubjectGoodsService.listAllSubjectGoodsV1(subjectId);
                List<Map> activityGoodsList  = new ArrayList<Map>();
                BigDecimal saleAmount = null;
                for(AfGoodsDo goodsDo : subjectGoodsList) {
                    Map activityGoodsInfo = new HashMap();
                    activityGoodsInfo.put("goodName",goodsDo.getName());
                    activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());

                    if(null != goodsDo.getActivityPrice()){
                        saleAmount = goodsDo.getActivityPrice();
                    }
                    else{
                        saleAmount = goodsDo.getSaleAmount();
                    }

                    //返利金额
                    BigDecimal secKillRebAmount = BigDecimalUtil.multiply(saleAmount, goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
                        activityGoodsInfo.put("rebateAmount", secKillRebAmount);
                    }

                    activityGoodsInfo.put("saleAmount", saleAmount);
                    activityGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
                    activityGoodsInfo.put("goodsId", goodsDo.getRid());
                    activityGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
                    activityGoodsInfo.put("source", goodsDo.getSource());
                    activityGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
                    activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
                    activityGoodsInfo.put("remark", goodsDo.getRemark());
                    activityGoodsInfo.put("activityName", activityName);
                    // 如果是分期免息商品，则计算分期
                    AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsDo.getRid());
                    JSONArray interestFreeArray = null;
                    if(null != afSchemeGoodsDo){
                        Long goodsId = goodsDo.getRid();
                        AfSchemeDo  afSchemeDo = null;
                        try {
                             afSchemeDo = afSchemeService.getSchemeById(afSchemeGoodsDo.getSchemeId());
                        } catch(Exception e){
                            logger.error(e.toString());
                        }

                        if(afSchemeDo != null){
                            if (freeflag(afSchemeDo.getGmtStart(),afSchemeDo.getGmtEnd(),afSchemeDo.getIsOpen()) ){
                                AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(afSchemeGoodsDo.getInterestFreeId());
                                String interestFreeJson = interestFreeRulesDo.getRuleJson();
                                if (org.apache.commons.lang.StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                                    interestFreeArray = JSON.parseArray(interestFreeJson);
                                }
                            }
                        }
                    }


                    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                            saleAmount, resource.getValue1(), resource.getValue2(),goodsDo.getRid(),"0");

                    if(nperList!= null){
                        activityGoodsInfo.put("goodsType", "1");
                        Map nperMap = nperList.get(nperList.size() - 1);
                        activityGoodsInfo.put("nperMap", nperMap);
                    }
                    activityGoodsList.add(activityGoodsInfo);
                }
                activityInfoMap.put("activityGoodsList", activityGoodsList);
                activityList.add(activityInfoMap);
            }
        }catch (Exception e){
            logger.error("partActivityInfoV2 getFrom sql error for" + e);
        }
        return activityList;
    }

    private boolean freeflag(Date start,Date end,String isOpen){
        try {
            if (!"Y".equals(isOpen)){
                return false;
            }
            if (DateUtil.compareDate(end,new Date()) && DateUtil.compareDate(new Date(),start)){
                return true;

            }else {
                return false;
            }
        }catch (Exception e){
            logger.info("freeflag",e);
            return false;

        }

    }

}
