package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.ActivityGoodsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luoxiao @date 2018/4/16 14:26
 * @类描述：三周年庆典活动
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/thirdAnnivCelebration/")
public class AppH5ThirdAnnivCelebrationController extends BaseController {
    String opennative = "/fanbei-web/opennative?name=";

    @Resource
    private AfRecommendUserService afRecommendUserService;

    @Resource
    private AfUserCouponService afUserCouponService;

    @Resource
    private AfCouponService afCouponService;

    @Resource
    private AfCouponCategoryService afCouponCategoryService;

    @Resource
    private AfUserService afUserService;

    @Resource
    private AfResourceService afResourceService;

    @Resource
    private AfGoodsService afGoodsService;

    @Resource
    private AfInterestFreeRulesService afInterestFreeRulesService;

    @Resource
    private AfActivityService afActivityService;

    @Resource
    AfActivityGoodsService afActivityGoodsService;

    @Resource
    private AfSeckillActivityService afSeckillActivityService;

    @Resource
    private BizCacheUtil bizCacheUtil;

    @Resource
    private AfSchemeGoodsService afSchemeGoodsService;

//    /**
//     * 预热商品列表
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "initWarmUpGoodList", method = RequestMethod.POST,  produces = "application/json;charset=UTF-8")
//    public String initWarmUpPage(HttpServletRequest request, HttpServletResponse response) {
//        JSONObject data = new JSONObject();
//        try {
//            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(Constants.TAC_WARM_UP_GOODS, Constants.DEFAULT);
//            if (null != afResourceDo) {
//                String value = afResourceDo.getValue();
//                if (!StringUtils.isEmpty(value)) {
//                    List<Map<String, Object>> goodList = getGoodMapList(value);
//                    if (null == goodList || goodList.isEmpty()) {
//                        return H5CommonResponse.getNewInstance(false, "获取商品列表为空", "", "").toString();
//                    }
//                    data.put("goodList", goodList);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("initWarmUpGoodList exception", e);
//            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", "").toString();
//        }
//        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", data).toString();
//    }

    /**
     * 每天首次分享成功，随机赠送优惠券（优惠券类型不限定领取数量）
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "sendCouponAfterSuccessShare", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String sendCouponAfterSuccessShare(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        String userName = context.getUserName();
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map<String, Object> data = Maps.newHashMap();
        try {
            if (null != afUserDo) {
                Integer sharedTimes = afRecommendUserService.getTodayShareTimes(afUserDo.getRid());
                if (null != sharedTimes && sharedTimes == 0) {
                    String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
                    if (groupId == null) {
                        return H5CommonResponse.getNewInstance(false, "groupId can't be null or empty.", null, "").toString();
                    }

                    AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
                    String coupons = couponCategory.getCoupons();
                    JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
                    int size = couponsArray.size();
                    int index = RandomUtil.getRandomInt(size);
                    Long couponId = Long.parseLong(couponsArray.getString(index));

                    AfCouponDo couponDo = afCouponService.getCouponById(0l);
                    AfUserCouponDo userCoupon = new AfUserCouponDo();
                    userCoupon.setCouponId(couponDo.getRid());
                    userCoupon.setGmtCreate(new Date());
                    userCoupon.setGmtStart(couponDo.getGmtStart());
                    userCoupon.setGmtEnd(couponDo.getGmtEnd());
                    userCoupon.setUserId(afUserDo.getRid());
                    userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
                    userCoupon.setSourceType(CouponSenceRuleType.RESERVATION.getCode());
                    afUserCouponService.addUserCoupon(userCoupon);

                    data.put("couponName", couponDo.getName());
                }
            }
        } catch (Exception e) {
            logger.error("分享三周年庆典活动页面成功，发送优惠券失败，userId：" + afUserDo.getRid() + ",", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", "").toString();
        }

        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", data).toString();
    }

    /**
     * 当前秒杀活动商品列表和下一场秒杀活动ID
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCurrentSecKillGoods", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getCurrentSecKillGoods(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        String activityName = "";
        AfResourceDo activityNameResourceDo = afResourceService.getSingleResourceBytype(Constants.TAC_SEC_KILL_ACTIVITY_NAME);
        if (null != activityNameResourceDo) {
            activityName = activityNameResourceDo.getValue();
        }

        // 查询秒杀活动列表
        Date now = new Date();
        Date gmtStart = DateUtil.getStartOfDate(now);
        Date gmtEnd = DateUtil.getEndOfDate(now);
        List<String> activityIds = bizCacheUtil.getObjectList(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_ACT_LIST.getCode());
        if(null == activityIds){
            activityIds = afSeckillActivityService.getActivityListByName(activityName, null, null);
            bizCacheUtil.saveListForever(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_ACT_LIST.getCode(), activityIds);
        }

        // 查询每日活动场次
        List<String> todayActivityIds = bizCacheUtil.getObjectList(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_TODAY_ACT_LIST.getCode());
        if(null == todayActivityIds){
            todayActivityIds = afSeckillActivityService.getActivityListByName(activityName, gmtStart, gmtEnd);
            bizCacheUtil.saveListByTime(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_TODAY_ACT_LIST.getCode(),todayActivityIds,Constants.SECOND_OF_TEN_MINITS);
        }

        if (null != todayActivityIds && !todayActivityIds.isEmpty()) {
            Long userId = null;
            if (context.getUserName() != null) {
                AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
                if (userDo != null)
                    userId = userDo.getRid();
            }

            int activitySize = todayActivityIds.size();
            String activityId = todayActivityIds.get(0);
            String nextActivityId = "";
            if (activitySize > 1) {
                // 获取当前场次的活动ID
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                AfResourceDo activityStartHour = afResourceService.getSingleResourceBytype(Constants.TAC_SEC_KILL_ACTIVITY_NAME);
                if (null != activityStartHour) {
                    String[] activityStartHourArray = activityStartHour.getValue().split(",");
                    int arrayLength = activityStartHourArray.length;
                    if (currentHour > Integer.parseInt(activityStartHourArray[arrayLength - 1])) {
                        activityId = todayActivityIds.get(activitySize - 1);
                    } else {
                        for (int i = 0; i < arrayLength - 1; i++) {
                            if ((currentHour >= Integer.parseInt(activityStartHourArray[i])) && (currentHour <= Integer.parseInt(activityStartHourArray[i + 1]))) {
                                if (activitySize < (i + 1)) {
                                    activityId = todayActivityIds.get(activitySize - 1);
                                } else {
                                    activityId = todayActivityIds.get(i + 1);
                                }
                            }
                        }
                    }
                    int index = activityIds.indexOf(activityId);
                    // 非第一场，直到第二天前均展示前一天的最后一场
                    if(index > 0){
                        if(currentHour < Integer.parseInt(activityStartHourArray[0])){
                            nextActivityId = activityId;
                            activityId = activityIds.get(index - 1);
                        }
                        else{
                            if (index < (activityIds.size() - 1)) {
                                nextActivityId = todayActivityIds.get(index + 1);
                            } else {
                                nextActivityId = "";
                            }
                        }
                    }
                }
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("nextActivityId", nextActivityId);
            buildSecKillGoodList(data, userId, Long.parseLong(activityId));
            return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
        }
        else{
            return H5CommonResponse.getNewInstance(true, "活动已结束", "", null).toString();
        }
    }

    /**
     * 根据秒杀活动ID获取下一场秒杀商品信息
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getNextSecKillGoodList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getNextSecKillGoodList(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        String activityId = request.getParameter("activityId");
        if (StringUtils.isEmpty(activityId)) {
            return H5CommonResponse.getNewInstance(true, "没有下一场活动了!", "", "").toString();
        }

        Map<String, Object> data = new HashMap<String, Object>();

        // 商品展示
        Long userId = null;
        if (context.getUserName() != null) {
            AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
            if (userDo != null)
                userId = userDo.getRid();
        }
        buildSecKillGoodList(data, userId, Long.parseLong(activityId));
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * 获取“活动管理-H5配置页面”活动商品列表
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getH5PageGoodList", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public String getH5PageGoodList(HttpServletRequest request, HttpServletResponse response){
        Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 0);
        if(0 == activityId){
            return H5CommonResponse.getNewInstance(false, "活动不存在").toString();
        }

        AfActivityDo activityInfo =afActivityService.getActivityById(activityId);
        if(activityInfo == null) {
            return H5CommonResponse.getNewInstance(false, "活动信息不存在！id=" + activityId).toString();
        }

        Map<String, Object> data = Maps.newHashMap();
        data.put("validStartTime", activityInfo.getGmtStart().getTime());
        data.put("validEndTime", activityInfo.getGmtEnd().getTime());
        getH5PageActivityGoodList(data, activityId);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * @param data
     * @param activityId
     */
    private void getH5PageActivityGoodList(Map<String, Object> data, Long activityId){
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
     *
     * @param data       返回MAP
     * @param userId
     * @param activityId
     */
    private void buildSecKillGoodList(Map<String, Object> data, Long userId, Long activityId) {
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

    // TODO 秒杀商品预约短信提醒
//    reserveGoods


    // TODO 我的活动会场
    @ResponseBody
    @RequestMapping(value = "mineActivityInfo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public String mineActivityInfo(HttpServletRequest request, HttpServletResponse response){
        // 我的信息


        // 获取购物额度

        // 我的优惠券数量

        // 猜你喜欢商品






        return null;
    }


    /**
     * 根据商品ID获取商品信息
     *
     * @param value
     * @return
     */
    private List<Map<String, Object>> getGoodMapList(String value) {
        List<Map<String, Object>> goodList = Lists.newArrayList();

        // 获取商品ID集合
        List<Long> goodIdList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(value)) {
            String[] goodArray = value.split(",");
            if (null != goodArray && goodArray.length > 0) {
                for (String goodId : goodArray) {
                    goodIdList.add(Long.parseLong(StringUtils.trim(goodId)));
                }
            }

            if (goodIdList.isEmpty()) {
                return goodList;
            }

            // 批量查询到商品信息
            List<Map<String, Object>> goodsInfoList = afGoodsService.getGoodsByIds(goodIdList);

            String stockCount;
            Long interestFreeId;
            for (Map<String, Object> goodInfo : goodsInfoList) {
                interestFreeId = (Long) goodInfo.get("interestFreeId");

                // 获取借款分期配置信息
                AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
                JSONArray array = JSON.parseArray(resource.getValue());
                if (array == null) {
                    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
                }

                // 如果是分期免息商品，则计算分期
                JSONArray interestFreeArray = null;
                if (interestFreeId != null) {
                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId.longValue());
                    String interestFreeJson = interestFreeRulesDo.getRuleJson();
                    if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                        interestFreeArray = JSON.parseArray(interestFreeJson);
                    }
                }

                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                        (BigDecimal) goodInfo.get("saleAmount"), resource.getValue1(), resource.getValue2(), (Long) goodInfo.get("rid"), "0");
                if (nperList != null) {
                    goodInfo.put("goodsType", "1");
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    String isFree = (String) nperMap.get("isFree");
                    if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
                        nperMap.put("freeAmount", nperMap.get("amount"));
                    }
                    goodInfo.put("nperMap", nperMap);
                }
                goodList.add(goodInfo);
            }
        }
        return goodList;
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

}
