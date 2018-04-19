package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiao @date 2018/4/16 14:26
 * @类描述：3周年庆典活动
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/thirdAnnivCelebration/")
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

    @Resource
    private AfInterestFreeRulesService afInterestFreeRulesService;

    /**
     * 预热页面--获取商品列表
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "initWarmUpGoodList", method = RequestMethod.POST)
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

        // 获取商品ID集合
        List<Long> goodIdList = Lists.newArrayList();
        if(StringUtils.isNotEmpty(value)){
            String[] goodArray = value.split(",");
            if(null != goodArray && goodArray.length > 0){
                for(String goodId: goodArray){
                    goodIdList.add(Long.parseLong(StringUtils.trim(goodId)));
                }
            }

            if(goodIdList.isEmpty()){
                return goodList;
            }

            // 批量查询到商品信息
            List<Map<String, Object>> goodsInfoList = afGoodsService.getGoodsByIds(goodIdList);

            String  stockCount;
            Integer interestFreeId;
            for(Map<String, Object> goodInfo: goodsInfoList) {
                stockCount = (String) goodInfo.get("stockCount");
                if(StringUtils.isNotEmpty(stockCount) && Integer.parseInt(stockCount) < 0){
                    goodInfo.put("count","0");
                }
                interestFreeId = (Integer) goodInfo.get("interestFreeId");

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
                        (BigDecimal)goodInfo.get("saleAmount"), resource.getValue1(), resource.getValue2(), (Long)goodInfo.get("rid"), "0");
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

    /**
     * 主活动页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "initMainPage", method = RequestMethod.POST)
    public String initMainPage(HttpServletRequest request, HttpServletResponse response) {

        return "";

    }

    // TODO 秒杀商品

    // TODO 秒杀商品预约短信提醒



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
