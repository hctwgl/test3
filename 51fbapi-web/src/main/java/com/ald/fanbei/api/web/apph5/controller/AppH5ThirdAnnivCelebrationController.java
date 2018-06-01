package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfActivityReservationGoodsDao;
import com.ald.fanbei.api.dal.dao.AfActivityReservationGoodsUserDao;
import com.ald.fanbei.api.dal.dao.AfSeckillActivityDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.api.goods.GetGoodsDetailInfoApi;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;
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
import java.io.IOException;
import java.math.BigDecimal;
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

    @Resource
    private AfActivityReservationGoodsUserService afActivityReservationGoodsUserService;

    @Resource
    private AfSeckillActivityDao afSeckillActivityDao;

    @Resource
    private AfActivityReservationGoodsDao afActivityReservationGoodsDao;

    @Resource
    private AfActivityReservationGoodsUserDao afActivityReservationGoodsUserDao;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfSchemeGoodsService afSchemeGoodsService;

    @Resource
    AfSchemeService afSchemeService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService ;

    @Resource
    private SmsUtil smsUtil ;
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
                    logger.info("sendCouponAfterSuccessShare groupId: " + request.getParameter("groupId"));
                    String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
                    if (groupId == null) {
                        return H5CommonResponse.getNewInstance(false, "groupId can't be null or empty.", null, "").toString();
                    }

                    AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
                    if (null != couponCategory) {
                        String coupons = couponCategory.getCoupons();
                        JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
                        int size = couponsArray.size();
                        int index = size;
                        if(size > 1){
                            index = RandomUtil.getRandomInt(size - 1);
                        }
                        Long couponId = Long.parseLong(couponsArray.getString(index));

                        AfCouponDo couponDo = afCouponService.getCouponById(couponId);
                      //  System.out.println(couponDo.getRid());
                        if (couponDo == null) {
                            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(),
                                    "", data).toString();
                        }else{
                            if(couponDo.getQuota() != -1 && couponDo.getQuota() <= couponDo.getQuotaAlready()){
                                return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc(), "", "").toString();
                            }
                        }
//                        Date gmtStartTime = couponDo.getGmtStart();
//                        Date gmtEndTime = couponDo.getGmtEnd();
//                        Date now = new Date();
//                        if(DateUtil.compareDate(gmtStartTime,now)){
//                            return H5CommonResponse.getNewInstance(true, "优惠券活动未开始", "", "").toString();
//                        }
//                        if(DateUtil.compareDate(now, gmtEndTime)){
//                            return H5CommonResponse.getNewInstance(true, "优惠券已过期", "", "").toString();
//                        }

                        AfUserCouponDo userCoupon = new AfUserCouponDo();
                        userCoupon.setCouponId(couponDo.getRid());
                        userCoupon.setGmtCreate(new Date());
                        userCoupon.setGmtStart(couponDo.getGmtStart());
                        userCoupon.setGmtEnd(couponDo.getGmtEnd());
                        userCoupon.setUserId(afUserDo.getRid());
                        userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
                        userCoupon.setSourceType(CouponSenceRuleType.SHARE_ACTIVITY.getCode());
                        afUserCouponService.addUserCoupon(userCoupon);
                        AfCouponDo couponDoT = new AfCouponDo();
                        couponDoT.setRid(couponDo.getRid());
                        couponDoT.setQuotaAlready(1);
                        afCouponService.updateCouponquotaAlreadyById(couponDoT);

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
        List<String> activityIds = activityIds = afSeckillActivityService.getActivityListByName(activityName, null, null);

        // 查询每日活动场次(每日第一场时间前为上一场次信息)
        Date now = new Date();
        Date gmtStart = DateUtil.getStartOfDate(now);
        Date gmtEnd = DateUtil.getEndOfDate(now);
        List<String> todayActivityIds = afSeckillActivityService.getActivityListByName(activityName, gmtStart, gmtEnd);

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
            // 活动时间配置
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            String startHourKey = CacheConstants.CACHE_KEY_ACTIVITY_START_HOUR_ARRAY + activityName;
            String[] activityStartHourArray = (String[]) bizCacheUtil.getObject(startHourKey);
            if (null == activityStartHourArray) {
                AfResourceDo activityStartHour = afResourceService.getSingleResourceBytype(Constants.TAC_SEC_KILL_ACTIVITY_START_TIME);
                if (null != activityStartHour) {
                    activityStartHourArray = activityStartHour.getValue().split(",");
                    bizCacheUtil.saveObject(startHourKey, activityStartHourArray, Constants.SECOND_OF_ONE_DAY);
                } else {
                    activityStartHourArray = new String[]{"10", "14", "18"};
                }
            }

            int arrayLength = activityStartHourArray.length;
            int todayActivitySize = todayActivityIds.size();
            int activitySize = activityIds.size();

            // 秒杀活动每次场次和时间配置应该是一致的
            if (arrayLength != todayActivitySize) {
                logger.error("当前活动场次和时间配置不一致，请检查");
                return H5CommonResponse.getNewInstance(false, "当前活动场次和时间配置不一致", "", "").toString();
            }

            // 第一天第一场前展示第一场
            if (StringUtils.equals(activityIds.get(0), todayActivityIds.get(0)) && currentHour < Integer.parseInt(activityStartHourArray[0])) {
                activityId = activityIds.get(0);
                nextActivityId = activityIds.get(1);
            } else {
                if (currentHour < Integer.parseInt(activityStartHourArray[0])) {
                    nextActivityId = todayActivityIds.get(0);
                    int index = activityIds.indexOf(nextActivityId);
                    activityId = activityIds.get(index - 1);
                } else if (currentHour >= Integer.parseInt(activityStartHourArray[arrayLength - 1])) {
                    activityId = todayActivityIds.get(todayActivitySize - 1);
                    int index = activityIds.indexOf(activityId);
                    if (index < activitySize - 1) {
                        nextActivityId = activityIds.get(index + 1);
                    } else {
                        nextActivityId = "";
                    }
                } else {
                    for (int i = 0; i < arrayLength - 1; i++) {
                        if ((currentHour >= Integer.parseInt(activityStartHourArray[i])) && (currentHour < Integer.parseInt(activityStartHourArray[i + 1]))) {
                            activityId = todayActivityIds.get(i);
                            int index = activityIds.indexOf(activityId);
                            nextActivityId = activityIds.get(index + 1);
                        }
                    }
                }
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("activityId", activityId);
        data.put("nextActivityId", nextActivityId);
        appActivityGoodListUtil.getSecKillGoodList(userId, Long.parseLong(activityId), data);
        List<Map<String, Object>> goodsList = (List<Map<String, Object>>) data.get("goodsList");

        List<AfSeckillActivityDo> afSeckillActivityDos1 = afSeckillActivityService.getActivityGoodsCountList(Long.parseLong(activityId));
        List<AfSeckillActivityDo> afSeckillActivityDos2 = afSeckillActivityService.getActivitySaleCountList(Long.parseLong(activityId));
        int size = goodsList.size();
        for (int i = 0; i < size; i++) {
            Map<String, Object> afActGoodsDto = goodsList.get(i);
            Long goodsId = Long.valueOf(String.valueOf(afActGoodsDto.get("goodsId")));
            for (AfSeckillActivityDo afSeckillActivityDo : afSeckillActivityDos1) {
                if (afSeckillActivityDo.getRid().equals(goodsId)) {
                    afActGoodsDto.put("goodsCount", afSeckillActivityDo.getGoodsCount());
                }
            }
            for (AfSeckillActivityDo afSeckillActivityDo : afSeckillActivityDos2) {
                if (afSeckillActivityDo.getRid().equals(goodsId)) {
                    afActGoodsDto.put("saleCount", afSeckillActivityDo.getSaleCount());
                } else {
                    afActGoodsDto.put("saleCount", 0);
                }
            }
        }

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
      //  System.out.println("****************************************");
        doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
        FanbeiWebContext context = doWebCheck(request, false);
        String activityId = request.getParameter("activityId");
        if (StringUtils.isEmpty(activityId)) {
            return H5CommonResponse.getNewInstance(false, "没有下一场活动了!", "", "").toString();
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
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getActivityGoodList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String getActivityGoodList(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 0);
        if (0 == activityId) {
            return H5CommonResponse.getNewInstance(false, "活动不存在").toString();
        }

        AfActivityDo activityInfo = afActivityService.getActivityById(activityId);
        if (activityInfo == null) {
            return H5CommonResponse.getNewInstance(false, "活动信息不存在！id=" + activityId).toString();
        }

        Map<String, Object> data = Maps.newHashMap();
        data.put("validStartTime", activityInfo.getGmtStart().getTime());
        data.put("validEndTime", activityInfo.getGmtEnd().getTime());
        appActivityGoodListUtil.getActivityGoodList(data, activityId);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * 我的活动会场
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "mineActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String mineActivityInfo(HttpServletRequest request, HttpServletResponse response) {
        FanbeiWebContext context = doWebCheck(request, false);
        AfUserDo afUserDo = null;
        String userName = context.getUserName();
        if (StringUtils.isNotEmpty(userName)) {
            afUserDo = afUserService.getUserByUserName(userName);
        }
        Map<String, Object> data = Maps.newHashMap();
        if (null == afUserDo) {
            String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
                    + H5OpenNativeType.AppLogin.getCode();
            data.put("loginUrl", loginUrl);
            return H5CommonResponse.getNewInstance(false, "用户未登陆").toString();
        }
        data.put("afUserDo", afUserDo);

        // 购物额度
        afThirdAnnivCelebrationService.getUserAuAmountInfo(afUserDo, data);
        int couponCount = afUserCouponService.getUserCouponByUserNouse(afUserDo.getRid());
        data.put("couponCount", couponCount);

        //用户预售商品列表
        //获取资源信息
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.ACTIVITY_RESERVATION_GOODS);
        if(null != resourceInfo) {
            Long activityId = Long.valueOf(resourceInfo.getValue());
            List<AfActivityReservationGoodsUserDo> userReservationGoodsList = afActivityReservationGoodsUserService.getUserActivityReservationGoodsList(afUserDo.getRid(), activityId);
            data.put("reservationGoodsList", userReservationGoodsList);
        }

        return H5CommonResponse.getNewInstance(true, "", "", data).toString();
    }

    /**
     * 活动预售商品列表
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getReservationGoodsList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getReservationGoodsList(HttpServletRequest request, HttpServletResponse response) {
        doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
       FanbeiWebContext context = doWebCheck(request, false);
        Map<String, Object> data = Maps.newHashMap();
        List<AfActivityReservationGoodsUserDo> userReservationGoodsList = null;
        // 商品展示
        Long userId = 0L;
        AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
        if (userDo != null)
            userId = userDo.getRid();

        //资源信息
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.ACTIVITY_RESERVATION_GOODS);
        if(null != resourceInfo) {
            Long activityId = Long.valueOf(resourceInfo.getValue());
            //活动预售商品列表
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId",userId );
            map.put("activityId",activityId );
            userReservationGoodsList = afActivityReservationGoodsUserService.getActivityReservationGoodsList(map);

        }

        data.put("reservationGoodsList", userReservationGoodsList);
        return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
    }

    /**
     * ceshi
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ceshiPay", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String ceshiPay(HttpServletRequest request, HttpServletResponse response) {

        // 预售商品回调 处理
        AfOrderDo orderInfo = new AfOrderDo();
        orderInfo.setUserId(13989456327L);
        orderInfo.setRid(193477L);
        orderInfo.setGoodsId(136893L);
        orderInfo.setCount(3);
        afSeckillActivityService.updateUserActivityGoodsInfo(orderInfo);
        return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();
    }

    /**
     * ceshi
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ceshiSMS", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String ceshiSMS(HttpServletRequest request, HttpServletResponse response) {
        //获取资源信息
        AfResourceDo resourceInfo1 = afResourceService.getConfigByTypesAndSecType(Constants.SMS_TEMPLATE, Constants.SMS_ACTIVITY_RESERVATION_GOODS);
        String content = resourceInfo1.getValue();
        //发送短信
        String mobile = "13685746702";
        logger.info("sendSMS mobile:" + mobile + "  content: " + content);
        smsUtil.sendSmsToDhstAishangjie(mobile, content);
        return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();
    }
    /**
     * ceshi
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ceshiPayGoodsInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String ceshiPayGoodsInfo(HttpServletRequest request, HttpServletResponse response) {

        Long goodsId = 136567L;
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
        String freedesc = null;
        String iconMark = null;
        if(schemeGoodsDo != null){
            AfSchemeDo afSchemeDo = afSchemeService.getSchemeById(schemeGoodsDo.getSchemeId());

        }
        AfGoodsDetailInfoVo vo = new AfGoodsDetailInfoVo();
        //秒杀、促销活动商品信息
        AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityService.getActivityByGoodsId(goodsId);
        if(afSeckillActivityDo!=null){
            Long activityId = afSeckillActivityDo.getRid();
            AfSeckillActivityGoodsDo afSeckillActivityGoodsDo = afSeckillActivityService.getActivityGoodsByGoodsIdAndActId(activityId,goodsId);
            if(afSeckillActivityGoodsDo!=null){
                //活动商品配置有问题，秒杀不参加活动，特惠按照原来
                int actSaleCount = 0;
                //获取活动已售商品数量
                if(afSeckillActivityDo.getType()==2){
                    actSaleCount = afSeckillActivityService.getSaleCountByActivityIdAndGoodsId(activityId,goodsId);
                    vo.setLimitCount(afSeckillActivityGoodsDo.getLimitCount());
                    //兼容老版本
                    vo.setSaleCount(actSaleCount);
                }else{
                    Integer limitCount = afSeckillActivityService.getSumCountByGoodsId(goodsId);
                    vo.setLimitCount(limitCount);
                }
                Date gmtStart = afSeckillActivityDo.getGmtStart();
                Date gmtEnd = afSeckillActivityDo.getGmtEnd();
                Date gmtPStart = afSeckillActivityDo.getGmtPStart();
                //返利金额
                BigDecimal secKillRebAmount = BigDecimalUtil.multiply(afSeckillActivityGoodsDo.getSpecialPrice(), goods.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
                if(goods.getRebateAmount().compareTo(secKillRebAmount)>0){
                    vo.setRebateAmount(secKillRebAmount+"");
                }
                //vo.setRebateAmount(BigDecimalUtil.multiply(afSeckillActivityGoodsDo.getSpecialPrice(), goods.getRebateRate())+"");
                vo.setActivityId(activityId);
                vo.setActivityType(afSeckillActivityDo.getType());
                vo.setActivityName(afSeckillActivityDo.getName());
                vo.setGmtStart(gmtStart.getTime());
                vo.setGmtEnd(gmtEnd.getTime());
                vo.setNowDate(afSeckillActivityDo.getNowDate().getTime());
                if(gmtPStart.getTime()>=gmtStart.getTime()){
                    vo.setGmtPstart(0l);
                }else{
                    vo.setGmtPstart(gmtPStart.getTime());
                }
                vo.setLimitedPurchase(afSeckillActivityDo.getGoodsLimitCount());
                vo.setPayType(afSeckillActivityDo.getPayType());
                vo.setActSaleCount(actSaleCount);
                vo.setSpecialPrice(afSeckillActivityGoodsDo.getSpecialPrice());
                saleAmount = afSeckillActivityGoodsDo.getSpecialPrice();
                //兼容老版本
                 vo.setSaleAmount(saleAmount);

            }else{
                vo.setActivityId(0l);
                vo.setActivityType(0);
                vo.setActivityName("");
                vo.setGmtStart(0l);
                vo.setGmtEnd(0l);
                vo.setGmtPstart(0l);
                vo.setNowDate(0l);
                vo.setLimitCount(0);
                //vo.setLimitedPurchase(0);
                vo.setPayType("");
                vo.setActSaleCount(0);
                vo.setSpecialPrice(BigDecimal.ZERO);
            }
        }else{
            vo.setActivityId(0l);
            vo.setActivityType(0);
            vo.setActivityName("");
            vo.setGmtStart(0l);
            vo.setGmtEnd(0l);
            vo.setGmtPstart(0l);
            vo.setNowDate(0l);
            vo.setLimitCount(0);
            //vo.setLimitedPurchase(0);
            vo.setPayType("");
            vo.setActSaleCount(0);
            vo.setSpecialPrice(BigDecimal.ZERO);
        }
        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                saleAmount, resource.getValue1(), resource.getValue2(),goodsId,"0");

        if(nperList!= null){
            Map nperMap = nperList.get(nperList.size() - 1);
            vo.setNperMap(nperMap);
        }
        vo.setNperList(nperList);

        vo.setRemark(goods.getRemark());
        AfResourceDo reflag = afResourceService.getSingleResourceBytype(Constants.GOODS_DETAIL_RECYCLE_FLAG);
        if (reflag != null){
            String value3 = reflag.getValue3();
            if (value3 != null&&value3.contains(goods.getCategoryId()+"")){
                vo.setIsShow(1);
            }
        }
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("ss", vo);
        return H5CommonResponse.getNewInstance(true, "成功", "", resp).toString();
    }

    /*
     * 会场
     */
    @ResponseBody
    @RequestMapping(value = "partActivityInfoV2", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String partActivityInfoV2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FanbeiWebContext context = new FanbeiWebContext();

        Calendar calStart = Calendar.getInstance();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try {
            context = doWebCheck(request, false);
            String modelId = org.apache.commons.lang.ObjectUtils.toString(request.getParameter("modelId"), null);
            if (modelId == null || "".equals(modelId)) {
                resp = H5CommonResponse.getNewInstance(false, "模版id不能为空！");
                return resp.toString();
            }
            // 数据埋点
            request.setAttribute("context", context);

            AfResourceDo activity = afResourceService.getSingleResourceBytype(Constants.TAC_ACTIVITY);
            String activityName = "";
            String activityStartTime = "";
            if (null != activity) {
                activityName = StringUtils.trim(activity.getValue2());
                activityStartTime = activity.getValue().split(",")[0];
                if (StringUtils.isEmpty(activityName)) {
                    activityName = "三周年庆典";
                }
                if (StringUtils.isEmpty(activityStartTime)) {
                    activityStartTime = "2018-05-08 00:00:00";
                }
            }

            JSONObject data = appActivityGoodListUtil.partActivityInfoV2(modelId, context.getUserName(), activityName, activityStartTime);
            String error = data.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                resp = H5CommonResponse.getNewInstance(false, error);
            } else {
                resp = H5CommonResponse.getNewInstance(true, "成功", "", data);
            }
        } catch (FanbeiException e) {
            resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
            logger.error("请求失败" + context, e);
        } catch (Exception e) {
            resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
            logger.error("请求失败" + context, e);
        } finally {
            Calendar calEnd = Calendar.getInstance();
            doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(), context.getUserName());
        }
        return resp.toString();
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
