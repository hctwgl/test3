package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ActivityType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
*@类现描述：会场相关接口
*@author hqj 2018年3月30日 下午5:00:00
*@version
*@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
*/
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5EnjoyLifeController extends BaseController {
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfSubjectGoodsService afSubjectGoodsService;

    @Resource
    AfModelH5ItemService afModelH5ItemService;

    @Resource
    AfSubjectService afSubjectService;

    @Resource
    AfSchemeGoodsService afSchemeGoodsService;

    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfCouponService afCouponService;

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserCouponService afUserCouponService;

    @Resource
    AfModelH5Service afModelH5Service;
    @Resource
    BizCacheUtil bizCacheUtil;

    @Resource
    Cache scheduledCache;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfSeckillActivityService afSeckillActivityService;

    ExecutorService pool = Executors.newFixedThreadPool(1);

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "partActivityInfoV2", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String partActivityInfoV2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 分会场接口
        FanbeiWebContext context = new FanbeiWebContext();

        Calendar calStart = Calendar.getInstance();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try {
            context = doWebCheck(request, false);
            String modelId = ObjectUtils.toString(request.getParameter("modelId"), null);
            if(modelId == null || "".equals(modelId)) {
                resp = H5CommonResponse.getNewInstance(false, "模版id不能为空！");
                return resp.toString();
            }
            // 数据埋点
            request.setAttribute("context", context);
            //doMaidianLog(request,H5CommonResponse.getNewInstance(true,"succ"));

            //获取借款分期配置信息
            final AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            final JSONArray array = JSON.parseArray(resource.getValue());
            //删除2分期
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            //removeSecondNper(array);

            JSONObject jsonObj = new JSONObject();
            //String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+ H5OpenNativeType.GoodsInfo.getCode();

            // 根据modelId 取优惠券信息
            List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(Long.parseLong(modelId),
                    ActivityType.ACTIVITY_TEMPLATE.getCode());
            String userName = context.getUserName();
            AfUserDo userDo = afUserService.getUserByUserName(userName);

            for(AfCouponDto couponDto : couponList) {
                // 判断用户是否领
                if(userDo == null) {
                    couponDto.setUserAlready(0);
                } else {
                    int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
                    couponDto.setUserAlready(pickCount);
                }
            }
            jsonObj.put("couponList", couponList);

            //jsonObj.put("notifyUrl", notifyUrl);
            // 根据modelId查询banner信息
            List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "BANNER");
            if(bannerList != null && bannerList.size() > 0){
                AfModelH5ItemDo bannerInfo = bannerList.get(0);
                jsonObj.put("bannerImage", bannerInfo.getItemIcon());
            } else {
                resp = H5CommonResponse.getNewInstance(false, "banner信息为空");
                return resp.toString();
            }
            //获取可用额度
            AfUserAccountSenceDo userAccountInfo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userDo.getRid());
            jsonObj.put("userAccountInfo", userAccountInfo);

            // 查询会场下所有二级会场
            final List<AfModelH5ItemDo> subjectList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelTypeSortById(Long.parseLong(modelId), "SUBJECT");
            //List<Map> activityList = new ArrayList<Map>();
            if(subjectList == null || subjectList.size() == 0){
                resp = H5CommonResponse.getNewInstance(false, "分会场信息为空");
                return resp.toString();
            }
            AfModelH5ItemDo subjectH5ItemDo = subjectList.get(0);
            String secSubjectId = subjectH5ItemDo.getItemValue();
            AfSubjectDo parentSubjectDo = afSubjectService.getSubjectInfoById(secSubjectId);
            Long parentId = parentSubjectDo.getParentId();
            String subjectName  = parentSubjectDo.getName();
            jsonObj.put("modelName", subjectName); // 主会场名称
            //测试并发
            int threadCount = 100;
            final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
            for (int i = 0; i < threadCount; i++) {

                //Runnable process = new SecKillOrder(afGoodsService,afSeckillActivityService,afGoodsPriceService);
                new Thread() {
                    public void run() {
                        try {
                            List<Map> activityList = new ArrayList<Map>();

                            String cacheKey = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode();
                            String processKey = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode();
                            bizCacheUtil.delCache(processKey);
                            //Boolean isProcess = (Boolean) bizCacheUtil.getObject(processKey);
                            activityList = bizCacheUtil.getObjectList(cacheKey);
                            logger.info(Thread.currentThread().getName() + "1activityList:" + activityList);
                            if(activityList==null){
                                boolean isGetLock = bizCacheUtil.getLock30Second(processKey, "1");
                                activityList = (List<Map>) scheduledCache.getObject(cacheKey);
                                logger.info(Thread.currentThread().getName() + "2activityList:" + activityList + ",isGetLock:"+isGetLock);

                                //调用异步请求加入缓存
                                if(isGetLock){
                                    logger.info(Thread.currentThread().getName() + "3activityList:" + activityList);
                                    //bizCacheUtil.saveObjectForever(processKey,isProcess);
                                    Runnable process = new GetActivityListThread(subjectList,resource,array,afSubjectService,afSubjectGoodsService,afSchemeGoodsService,afInterestFreeRulesService,bizCacheUtil,scheduledCache);
                                    pool.execute(process);
                                }
                            }
                            if(activityList==null){
                                activityList = getActivityList(subjectList,resource,array);
                                bizCacheUtil.saveListByTime(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityList, 1*60);
                                scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                }.start();
                countDownLatch.countDown();
                System.out.println(countDownLatch.getCount());
            }



            //jsonObj.put("activityList", activityList);
            resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
        }catch(FanbeiException e){
            resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
            logger.error("请求失败"+context,e);
        }catch(Exception e){
            resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
            logger.error("请求失败"+context,e);
        }finally{
            Calendar calEnd = Calendar.getInstance();
            doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
        }
        return resp.toString();
    }

    private List<Map> getActivityList(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array) {
        //获取所有活动
        Map secActivityInfoMap = new HashMap();
        List<AfSeckillActivityDo> afSeckillActivityDos = afSeckillActivityService.getActivityNow();
        for(AfSeckillActivityDo afSeckillActivityDo : afSeckillActivityDos){
            Long activityId = afSeckillActivityDo.getRid();
            String actName = afSeckillActivityDo.getName();
            //取出该活动所有商品
            List<AfSeckillActivityGoodsDto> afSeckillActivityGoodsDtos = afSeckillActivityService.getActivityGoodsByActivityId(activityId);
            secActivityInfoMap.put("afSeckillActivityGoodsDtos",afSeckillActivityGoodsDtos);
        }


        List<Map> activityList = new ArrayList<Map>();
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
            for(AfGoodsDo goodsDo : subjectGoodsList) {
                Map activityGoodsInfo = new HashMap();
                activityGoodsInfo.put("goodName",goodsDo.getName());
                activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
                activityGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
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
                    AfSchemeGoodsDo  schemeGoodsDo = null;
                    try {
                        schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
                    } catch(Exception e){
                        logger.error(e.toString());
                    }

                    if(schemeGoodsDo != null){
                        AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                        String interestFreeJson = interestFreeRulesDo.getRuleJson();
                        if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                            interestFreeArray = JSON.parseArray(interestFreeJson);
                        }
                    }

                }
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                        goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsDo.getRid());

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
        return activityList;
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}

class GetActivityListThread implements Runnable {
    protected static final Logger logger = LoggerFactory.getLogger(GetActivityListThread.class);
    private List<AfModelH5ItemDo> subjectList;
    private AfResourceDo resource;
    private JSONArray array;
    private AfSubjectService afSubjectService;
    private AfSubjectGoodsService afSubjectGoodsService;
    private AfSchemeGoodsService afSchemeGoodsService;
    private AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    Cache scheduledCache;
    GetActivityListThread(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array,
                          AfSubjectService afSubjectService,AfSubjectGoodsService afSubjectGoodsService,AfSchemeGoodsService afSchemeGoodsService,AfInterestFreeRulesService afInterestFreeRulesService,
                          BizCacheUtil bizCacheUtil,Cache scheduledCache) {
        this.subjectList = subjectList;
        this.resource = resource;
        this.array = array;
        this.afInterestFreeRulesService = afInterestFreeRulesService;
        this.afSchemeGoodsService = afSchemeGoodsService;
        this.afSubjectGoodsService = afSubjectGoodsService;
        this.afSubjectService = afSubjectService;
        this.bizCacheUtil = bizCacheUtil;
        this.scheduledCache = scheduledCache;
    }

    @Override
    public void run() {
        getActivityList(subjectList,resource,array,afSubjectService,
                afSubjectGoodsService,afSchemeGoodsService,afInterestFreeRulesService,bizCacheUtil,scheduledCache);

    }

    private List<Map> getActivityList(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array,
                                      AfSubjectService afSubjectService,AfSubjectGoodsService afSubjectGoodsService,AfSchemeGoodsService afSchemeGoodsService,AfInterestFreeRulesService afInterestFreeRulesService,
                                      BizCacheUtil bizCacheUtil,Cache scheduledCache) {
        List<Map> activityList = new ArrayList<Map>();
        logger.info(Thread.currentThread().getName() + "new Thread");
        try {
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
                for(AfGoodsDo goodsDo : subjectGoodsList) {
                    Map activityGoodsInfo = new HashMap();
                    activityGoodsInfo.put("goodName",goodsDo.getName());
                    activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
                    activityGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
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
                        AfSchemeGoodsDo  schemeGoodsDo = null;
                        try {
                            schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
                        } catch(Exception e){
                            logger.error(e.toString());
                        }

                        if(schemeGoodsDo != null){
                            AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
                            String interestFreeJson = interestFreeRulesDo.getRuleJson();
                            if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
                                interestFreeArray = JSON.parseArray(interestFreeJson);
                            }
                        }

                    }
                    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
                            goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsDo.getRid());

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
            bizCacheUtil.saveListByTime(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityList, 10*60);
            scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityList);
            Boolean isProcess = false;
            bizCacheUtil.saveObjectForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode(),isProcess);
        }catch (Exception e){
            logger.error("");
        }finally {
            //Boolean isProcess = false;
            //bizCacheUtil.saveObjectForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode(),isProcess);
        }

        return activityList;
    }
}
