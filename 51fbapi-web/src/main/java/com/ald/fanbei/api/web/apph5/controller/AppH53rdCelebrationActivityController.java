package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiao @date 2018/4/16 14:26
 * @类描述：3周年庆典活动
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/activity/thirdAnnivCelebration")
public class AppH53rdCelebrationActivityController extends BaseController {
    @Resource
    private AfRecommendUserService afRecommendUserService;

    @Resource
    private AfUserCouponService afUserCouponService;

    @Resource
    private AfCouponService afCouponService;

    @Resource
    private AfUserService afUserService;

    @Resource
    private AfResourceService afResourceService;

    @Resource
    private AfGoodsService afGoodsService;

    /**
     * 预热页面--获取商品列表
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/initWarmUpGoodList", method = RequestMethod.POST)
    public String initWarmUpPage(HttpServletRequest request, HttpServletResponse response){
        JSONObject data = new JSONObject();
        try{
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(Constants.TAC_WARM_UP_GOODS, Constants.DEFAULT);
            if(null != afResourceDo){
                String value = afResourceDo.getValue();
                if(!StringUtils.isEmpty(value)){
                    List<Map<String, Object>> goodList = getGoodMapList(value);
                    if(null == goodList || goodList.isEmpty()){
                        return H5CommonResponse.getNewInstance(false, "获取商品列表为空", "", "").toString();
                    }
                    data.put("goodList", goodList);
                }
            }
        }catch(Exception e){
            logger.error("initWarmUpGoodList exception", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", "").toString();
        }
        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", data).toString();
    }

    /**
     * 根据配置商品ID组装商品信息
     * @param value
     * @return
     */
    private List<Map<String, Object>> getGoodMapList(String value){
        List<Map<String, Object>> goodList = Lists.newArrayList();

        if(!StringUtils.isEmpty(value)){
            String[] goodArray = value.split(",");
            String  stockCount = null;
            Map<String, Object> goodMap;
            for(String goodId: goodArray) {
                AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(Long.parseLong(StringUtils.trim(goodId)));
                goodMap = Maps.newHashMap();
                if (afGoodsDo != null) {
                    goodMap.put("numId",String.valueOf(afGoodsDo.getRid()));
                    goodMap.put("saleAmount",afGoodsDo.getPriceAmount().toString());
                    goodMap.put("realAmount",afGoodsDo.getSaleAmount().toString());
                    goodMap.put("rebateAmount",afGoodsDo.getRebateAmount().toString());
                    goodMap.put("goodsName",afGoodsDo.getName());
                    goodMap.put("goodsIcon",afGoodsDo.getGoodsIcon());
                    goodMap.put("thumbnailIcon",afGoodsDo.getThumbnailIcon());
                    goodMap.put("goodsUrl",afGoodsDo.getGoodsDetail().split(";")[0]);
                    goodMap.put("openId",afGoodsDo.getOpenId());
                    goodMap.put("source",afGoodsDo.getSource());
                    stockCount = afGoodsDo.getStockCount();
                    goodMap.put("stockCount",stockCount);
                    if(StringUtils.isNotEmpty(stockCount) && Integer.parseInt(stockCount) < 0){
                        goodMap.put("count","0");
                    }
                    goodList.add(goodMap);
                }
            }
        }
        return goodList;
    }
    /**
     * 每天首次分享成功，赠送优惠券
     * @return
     */
    @ResponseBody
    @RequestMapping(value="sendCouponAfterSuccessShare", method=RequestMethod.POST)
    public String sendCouponAfterSuccessShare(HttpServletRequest request, HttpServletResponse response){
        FanbeiWebContext context = doWebCheck(request, false);
        String userName = context.getUserName();
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        try{
            if(null != afUserDo){
                Integer sharedTimes = afRecommendUserService.getTodayShareTimes(afUserDo.getRid());
                if(null != sharedTimes && sharedTimes > 0){
                    // TODO 优惠券
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
                }
            }
        } catch (Exception e) {
            logger.error("分享三周年庆典活动页面成功，发送优惠券失败，userId：" + afUserDo.getRid() + ",", e);
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", "").toString();
        }

        return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", "").toString();
    }

    // 优惠券信息列表
//    AppH5CouponController.activityCouponInfo();

    // 领取优惠券
//    AppH5CouponController.pickCoupon

    /**
     * 主活动页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/initMainPage", method = RequestMethod.POST)
    public String initMainPage(HttpServletRequest request, HttpServletResponse response) {

        return "";

    }

    // TODO 秒杀商品

    // TODO 秒杀商品预约短信提醒

    // TODO 红包雨


    // TODO 商品列表

    // TODO 人气畅销
    // TODO 我的活动会场
    // 优惠券列表
    //


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
