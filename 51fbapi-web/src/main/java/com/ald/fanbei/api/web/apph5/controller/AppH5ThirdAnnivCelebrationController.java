package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
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
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.*;

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
    private AfThirdAnnivCelebrationService afThirdAnnivCelebrationService;

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
    private AfActivityService afActivityService;

    @Resource
    private AfSeckillActivityService afSeckillActivityService;

    @Resource
    private BizCacheUtil bizCacheUtil;

    @Resource
    private AppActivityGoodListUtil appActivityGoodListUtil;

    /**
     * 每天首次分享成功，随机赠送优惠券（优惠券类型不限定领取数量）
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "sendCouponAfterSuccessShare", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String sendCouponAfterSuccessShare(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        String userName = context.getUserName();
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map<String, Object> data = Maps.newHashMap();
        try {
            if (null != afUserDo) {
                Integer sharedTimes = afRecommendUserService.getTodayShareTimes(afUserDo.getRid());
                if (null != sharedTimes && sharedTimes == 1) {
                    String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
                    if (groupId == null) {
                        return H5CommonResponse.getNewInstance(false, "groupId can't be null or empty.", null, "").toString();
                    }

                    AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
                    if(null != couponCategory){
                        String coupons = couponCategory.getCoupons();
                        JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
                        int size = couponsArray.size();
                        int index = RandomUtil.getRandomInt(size - 1);
                        Long couponId = Long.parseLong(couponsArray.getString(index));

                        AfCouponDo couponDo = afCouponService.getCouponById(couponId);
                        AfUserCouponDo userCoupon = new AfUserCouponDo();
                        userCoupon.setCouponId(couponDo.getRid());
                        userCoupon.setGmtCreate(new Date());
                        userCoupon.setGmtStart(couponDo.getGmtStart());
                        userCoupon.setGmtEnd(couponDo.getGmtEnd());
                        userCoupon.setUserId(afUserDo.getRid());
                        userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
                        userCoupon.setSourceType(CouponSenceRuleType.SHARE_ACTIVITY.getCode());
                        afUserCouponService.addUserCoupon(userCoupon);

                        data.put("couponCondititon", couponDo.getLimitAmount());
                        data.put("couponAmount", couponDo.getAmount());
                        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", data).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("分享三周年庆典活动页面成功，发送优惠券失败，userId：" + afUserDo.getRid() + ",", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", "").toString();
        }

        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", "").toString();
    }

    /**
     * 当前秒杀活动商品列表和下一场秒杀活动ID
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCurrentSecKillGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
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
        if (null == activityIds) {
            activityIds = afSeckillActivityService.getActivityListByName(activityName, null, null);
            bizCacheUtil.saveListByTime(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_ACT_LIST.getCode(), activityIds, Constants.SECOND_OF_ONE_DAY);
        }

        // 查询每日活动场次(每日第一场时间前为上一场次信息)
        List<String> todayActivityIds = bizCacheUtil.getObjectList(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_TODAY_ACT_LIST.getCode());
        if (null == todayActivityIds) {
            todayActivityIds = afSeckillActivityService.getActivityListByName(activityName, gmtStart, gmtEnd);
            bizCacheUtil.saveListByTime(CacheConstants.THIRD_ANNIV_CELEBRATION_ACT.GET_THIRD_ANNIV_CELEBRATION_TODAY_ACT_LIST.getCode(), todayActivityIds, Constants.SECOND_OF_AN_HOUR_INT);
        }

        String activityId = "";
        String nextActivityId = "";
        if (null == activityIds || activityIds.isEmpty()) {
            return H5CommonResponse.getNewInstance(false, "活动未开始，敬请期待", "", "").toString();
        }

        Long userId = null;
        if (context.getUserName() != null) {
            AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
            if (userDo != null)
                userId = userDo.getRid();
        }

        // 活动未开始，取第一场活动（不考虑活动结束）
        if (null == todayActivityIds || todayActivityIds.isEmpty()) {
            activityId = activityIds.get(0);
            nextActivityId = activityIds.get(1);
        } else {
            if (StringUtils.equals(activityIds.get(0), todayActivityIds.get(0))) {
                activityId = activityIds.get(0);
                nextActivityId = activityIds.get(1);
            } else {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

                String startHourKey = CacheConstants.CACHE_KEY_ACTIVITY_START_HOUR_ARRAY + activityName;
                String[] activityStartHourArray = (String[]) bizCacheUtil.getObject(startHourKey);
                if (null == activityStartHourArray) {
                    AfResourceDo activityStartHour = afResourceService.getSingleResourceBytype(Constants.TAC_SEC_KILL_ACTIVITY_START_TIME);
                    if (null != activityStartHour) {
                        activityStartHourArray = activityStartHour.getValue().split(",");
                        bizCacheUtil.saveObject(startHourKey, activityStartHourArray, Constants.SECOND_OF_ONE_MONTH);
                    } else {
                        activityStartHourArray = new String[]{"10", "14", "18"};
                    }
                }

                int arrayLength = activityStartHourArray.length;
                if (currentHour < Integer.parseInt(activityStartHourArray[0])) {
                    nextActivityId = todayActivityIds.get(0);
                    int index = activityIds.indexOf(nextActivityId);
                    activityId = activityIds.get(index - 1);
                } else {
                    int todayActivitySize = todayActivityIds.size();
                    int activitySize = activityIds.size();
                    for (int i = 0; i < arrayLength - 1; i++) {
                        if ((currentHour >= Integer.parseInt(activityStartHourArray[i])) && (currentHour <= Integer.parseInt(activityStartHourArray[i + 1]))) {
                            if (todayActivitySize < (i + 1)) {
                                activityId = todayActivityIds.get(todayActivitySize - 1);
                            } else {
                                activityId = todayActivityIds.get(i);
                                int index = activityIds.indexOf(activityId);
                                if (index < activitySize - 1) {
                                    nextActivityId = activityIds.get(index + 1);
                                } else {
                                    nextActivityId = "";
                                }
                            }
                        }
                    }
                }
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("activityId", activityId);
        data.put("nextActivityId", nextActivityId);
        appActivityGoodListUtil.getSecKillGoodList(userId, Long.parseLong(activityId), data);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * 根据秒杀活动ID获取下一场秒杀商品信息
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSecKillGoodListByActivityId", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String getSecKillGoodListByActivityId(HttpServletRequest request, HttpServletResponse response) {
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
        appActivityGoodListUtil.getSecKillGoodList(userId, Long.parseLong(activityId), data);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * 获取“活动管理-新增活动”活动商品列表
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getActivityGoodList", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public String getActivityGoodList(HttpServletRequest request, HttpServletResponse response){
        FanbeiWebContext context = doWebCheck(request, false);
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
        appActivityGoodListUtil.getH5PageActivityGoodList(data, activityId);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * 我的活动会场
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "mineActivityInfo", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public String mineActivityInfo(HttpServletRequest request, HttpServletResponse response){
        FanbeiWebContext context = doWebCheck(request, false);
        AfUserDo afUserDo = null;
        String userName = context.getUserName();
        if(StringUtils.isNotEmpty(userName)){
            afUserDo = afUserService.getUserByUserName(userName);
        }

        Map<String, Object> data = Maps.newHashMap();
        if(null == afUserDo){
            String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
                    + H5OpenNativeType.AppLogin.getCode();
            data.put("loginUrl", loginUrl);
            return H5CommonResponse.getNewInstance(false, "用户未登陆").toString();
        }

        // 购物额度
        afThirdAnnivCelebrationService.getUserAuAmountInfo(afUserDo, data);
        int couponCount = afUserCouponService.getUserCouponByUserNouse(afUserDo.getRid());
        data.put("couponCount", couponCount);

        return H5CommonResponse.getNewInstance(true, "","",data).toString();
    }

//    /**
//     * 根据商品ID获取商品信息
//     *
//     * @param value
//     * @return
//     */
//    private List<Map<String, Object>> getGoodMapList(String value) {
//        List<Map<String, Object>> goodList = Lists.newArrayList();
//
//        // 获取商品ID集合
//        List<Long> goodIdList = Lists.newArrayList();
//        if (StringUtils.isNotEmpty(value)) {
//            String[] goodArray = value.split(",");
//            if (null != goodArray && goodArray.length > 0) {
//                for (String goodId : goodArray) {
//                    goodIdList.add(Long.parseLong(StringUtils.trim(goodId)));
//                }
//            }
//
//            if (goodIdList.isEmpty()) {
//                return goodList;
//            }
//
//            // 批量查询到商品信息
//            List<Map<String, Object>> goodsInfoList = afGoodsService.getGoodsByIds(goodIdList);
//
//            String stockCount;
//            Long interestFreeId;
//            for (Map<String, Object> goodInfo : goodsInfoList) {
//                interestFreeId = (Long) goodInfo.get("interestFreeId");
//
//                // 获取借款分期配置信息
//                AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
//                JSONArray array = JSON.parseArray(resource.getValue());
//                if (array == null) {
//                    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
//                }
//
//                // 如果是分期免息商品，则计算分期
//                JSONArray interestFreeArray = null;
//                if (interestFreeId != null) {
//                    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId.longValue());
//                    String interestFreeJson = interestFreeRulesDo.getRuleJson();
//                    if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
//                        interestFreeArray = JSON.parseArray(interestFreeJson);
//                    }
//                }
//
//                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
//                        (BigDecimal) goodInfo.get("saleAmount"), resource.getValue1(), resource.getValue2(), (Long) goodInfo.get("rid"), "0");
//                if (nperList != null) {
//                    goodInfo.put("goodsType", "1");
//                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
//                    String isFree = (String) nperMap.get("isFree");
//                    if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
//                        nperMap.put("freeAmount", nperMap.get("amount"));
//                    }
//                    goodInfo.put("nperMap", nperMap);
//                }
//                goodList.add(goodInfo);
//            }
//        }
//        return goodList;
//    }

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
