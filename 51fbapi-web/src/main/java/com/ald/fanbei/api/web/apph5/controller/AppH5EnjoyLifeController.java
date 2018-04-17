package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.ActivityGoodsUtil;
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
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfActGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.vo.afu.Data;
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
    @Resource
    ActivityGoodsUtil activityGoodsUtil;
    @Resource
    AfActivityUserSmsService afActivityUserSmsService;
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
            //userName = "18314896619";
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
            jsonObj.put("nowDate",new Date());
            jsonObj.put("couponList", couponList);

            //jsonObj.put("notifyUrl", notifyUrl);
            // 根据modelId查询banner信息
            /*List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "BANNER");
            if(bannerList != null && bannerList.size() > 0){
                AfModelH5ItemDo bannerInfo = bannerList.get(0);
                jsonObj.put("bannerImage", bannerInfo.getItemIcon());
            } else {
                resp = H5CommonResponse.getNewInstance(false, "banner信息为空");
                return resp.toString();
            }*/
            //获取配置商品信息
            List<AfResourceDo> activityResource = new ArrayList<>();
            try{
                activityResource = afResourceService.getConfigsByTypesAndSecType(Constants.ENJOYLIFE_ACTIVITY_INFO, Constants.ACTIVITY_INFO_GOODSID);
            }catch (Exception e){
                logger.error("get activityGoodsList error" + e);
            }
            jsonObj.put("activityGoodsList", activityResource);
            //获取可用额度
            AfUserAccountSenceDo userAccountInfo = new AfUserAccountSenceDo();
            if(userDo!=null){
                userAccountInfo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userDo.getRid());
            }else{
                userAccountInfo.setAuAmount(new BigDecimal(5000));
                userAccountInfo.setUsedAmount(new BigDecimal(0));
            }
            if(userAccountInfo==null){
                userAccountInfo = new AfUserAccountSenceDo();
                userAccountInfo.setAuAmount(new BigDecimal(5000));
                userAccountInfo.setUsedAmount(new BigDecimal(0));
            }
            jsonObj.put("userAccountInfo", userAccountInfo);
            AfSeckillActivityQuery query = new AfSeckillActivityQuery();
            query.setName("乐享生活节");
            query.setGmtStart(DateUtil.parseDate("2018-04-12 00:00:00"));
            //活动信息
            /*List<Map<String, Object>> activityInfoList = bizCacheUtil.getObjectList(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode());
            if(activityInfoList == null) {
                // redis取不到，则从一级缓存获取
                activityInfoList = (List<Map<String, Object>>) scheduledCache.getObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode());
            }
            AfSeckillActivityQuery query = new AfSeckillActivityQuery();
            query.setName("乐享生活节");
            query.setGmtStart(DateUtil.parseDate("2018-04-12 00:00:00"));
            if(activityInfoList == null) {
                // 一级缓存获取不到，则从数据库获取
                //activityInfoList = getActivityList();
                activityInfoList = activityGoodsUtil.getActivityGoods(query);
                bizCacheUtil.saveListForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
                scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
            }
            jsonObj.put("activityInfoList", activityInfoList);*/
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
            /*int threadCount = 1;
            final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
            for (int i = 0; i < threadCount; i++) {

                //Runnable process = new SecKillOrder(afGoodsService,afSeckillActivityService,afGoodsPriceService);
                new Thread() {
                    public void run() {
                        try {*/
                            List<Map> activityList = new ArrayList<Map>();

                            String cacheKey = CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST.getCode();
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
                                    Runnable process = new GetActivityListThread(subjectList,resource,array,afSubjectService,afSubjectGoodsService,afSchemeGoodsService,afInterestFreeRulesService,
                                            bizCacheUtil,scheduledCache,afSeckillActivityService,activityGoodsUtil,query);
                                    pool.execute(process);
                                }
                            }
                            if(activityList==null){
                                activityList = getActivityPartList(subjectList,resource,array);
                                bizCacheUtil.saveListByTime(cacheKey, activityList, 1*60);
                                scheduledCache.putObject(cacheKey, activityList);
                            }
                        /*} catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                }.start();
                countDownLatch.countDown();
                System.out.println(countDownLatch.getCount());
            }*/



            jsonObj.put("activityPartList", activityList);
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
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "getSecActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getSecActivityInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        JSONObject jsonObj = new JSONObject();
        FanbeiWebContext context = new FanbeiWebContext();
        try{
            context = doWebCheck(request, false);
            String userName = context.getUserName();
            //userName = "15293971826";
            AfUserDo userDo = new AfUserDo();
            if(StringUtil.isNotBlank(userName)){
                userDo = afUserService.getUserByUserName(userName);
            }
            //活动信息
            List<Map<String, Object>> activityInfoList = bizCacheUtil.getObjectList(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode());
            if(activityInfoList == null) {
                // redis取不到，则从一级缓存获取
                activityInfoList = (List<Map<String, Object>>) scheduledCache.getObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode());
            }
            AfSeckillActivityQuery query = new AfSeckillActivityQuery();
            query.setName("乐享生活节");
            query.setGmtStart(DateUtil.parseDate("2018-04-12 00:00:00"));
            if(activityInfoList != null) {
                // 一级缓存获取不到，则从数据库获取
                //activityInfoList = getActivityList();
                activityInfoList = activityGoodsUtil.getActivityGoods(query);
                bizCacheUtil.saveListForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
                scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
            }
            //获取该用户所有预约信息
            AfActivityUserSmsDo afActivityUserSmsDo = new AfActivityUserSmsDo();
            List<AfActivityUserSmsDo> afActivityUserSmsDos = new ArrayList<>();
            if(StringUtil.isNotBlank(userName)&&userDo!=null){
                afActivityUserSmsDo.setUserId(userDo.getRid());
                afActivityUserSmsDos = afActivityUserSmsService.getListByCommonCondition(afActivityUserSmsDo);
            }
            Date now = new Date();
            if(activityInfoList!=null){
                //更新商品库存和销量
                for(int i=0;i<activityInfoList.size();i++){
                    Map<String, Object> activityData = activityInfoList.get(i);
                    List<Map<String, Object>> goodsInfo = (List<Map<String, Object>>)activityData.get("goodsList");
                    Date pStartDate = (Date)(activityData.get("gmtPStart"));
                    Date endDate = (Date)(activityData.get("gmtEnd"));
                    //Date pStartDate = DateUtil.parseDate(String.valueOf(activityData.get("gmtPStart")));
                    //Date endDate = DateUtil.parseDate(String.valueOf(activityData.get("gmtEnd")));
                    Long nowctivityId = 0l;
                    Long activityId = Long.valueOf(String.valueOf(activityData.get("rid")));
                    for(int j=0;j<goodsInfo.size();j++){
                        Map<String, Object> afActGoodsDto = goodsInfo.get(j);
                        afActGoodsDto.put("isReserve","0");
                        Long goodsId = Long.valueOf(String.valueOf(afActGoodsDto.get("goodsId")));
                        //Long activityId = Long.valueOf(String.valueOf(afActGoodsDto.get("activityId")));
                        if(userDo!=null&&afActivityUserSmsDos!=null&&afActivityUserSmsDos.size()>0){
                            //判断是否预约
                            for(AfActivityUserSmsDo afActivityUserSmsDo1:afActivityUserSmsDos){
                                if(activityId.equals(afActivityUserSmsDo1.getActivityId())&&goodsId.equals(afActivityUserSmsDo1.getGoodsId())){
                                    //设置以预约
                                    afActGoodsDto.put("isReserve","1");
                                    break;
                                }
                            }
                        }
                        //判断是否是当前场次

                        afActGoodsDto.put("goodsCount",0);
                        afActGoodsDto.put("saleCount",0);
                    }
                    if(pStartDate.getTime()<=now.getTime()&&endDate.getTime()>=now.getTime()){
                        nowctivityId = activityId;
                        //获取商品库存跟销量
                        List<AfSeckillActivityDo> afSeckillActivityDos1 = afSeckillActivityService.getActivityGoodsCountList(activityId);
                        List<AfSeckillActivityDo> afSeckillActivityDos2 = afSeckillActivityService.getActivitySaleCountList(activityId);
                        for(int j=0;j<goodsInfo.size();j++){
                            Map<String, Object> afActGoodsDto = goodsInfo.get(j);
                            Long goodsId = Long.valueOf(String.valueOf(afActGoodsDto.get("goodsId")));
                            for(AfSeckillActivityDo afSeckillActivityDo:afSeckillActivityDos1){
                                if(afSeckillActivityDo.getRid().equals(goodsId)){
                                    afActGoodsDto.put("goodsCount",afSeckillActivityDo.getGoodsCount());
                                }
                            }
                            for(AfSeckillActivityDo afSeckillActivityDo:afSeckillActivityDos2){
                                if(afSeckillActivityDo.getRid().equals(goodsId)){
                                    afActGoodsDto.put("saleCount",afSeckillActivityDo.getSaleCount());
                                }
                            }
                        }
                    }
                }
                bizCacheUtil.saveListForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
                scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
            }

            jsonObj.put("activityInfoList", activityInfoList);
        }catch (Exception e){
            resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
            logger.error("请求失败"+e);
            return resp.toString();
        }

        resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
        return resp.toString();
    }

    //活动预约接口
    @RequestMapping(value = "/reserveGoodsV2", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ReserveGoods(HttpServletRequest request,  HttpServletResponse response) {
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        FanbeiWebContext context = new FanbeiWebContext();
        try{
            context = doWebCheck(request,true);
            String userName = context.getUserName();
            //userName = "18314896619";
            AfUserDo userDo = afUserService.getUserByUserName(userName);
            Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"),0l);
            Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"),0l);
            String goodsName = "商品名称";
            AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
            if(null != afGoodsDo){
                goodsName = afGoodsDo.getName();
            }
            Date sendTime = null;
            Date startTime = null;
            AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityService.getById(activityId);
            if(afSeckillActivityDo==null){
                return H5CommonResponse.getNewInstance(false, "活动已结束").toString();
            }else{
                Date endTime = afSeckillActivityDo.getGmtEnd();
                if(endTime!=null&&endTime.getTime()<(new Date().getTime())){
                    return H5CommonResponse.getNewInstance(false, "活动已结束").toString();
                }
                startTime = afSeckillActivityDo.getGmtStart();
                sendTime = DateUtil.addMins(startTime,-10);
            }
            AfActivityUserSmsDo afActivityUserSmsDo = new AfActivityUserSmsDo();
            afActivityUserSmsDo.setGoodsId(goodsId);
            afActivityUserSmsDo.setUserId(userDo.getRid());
            afActivityUserSmsDo.setActivityId(activityId);

            AfActivityUserSmsDo userSms = afActivityUserSmsService.getByCommonCondition(afActivityUserSmsDo);
            if(null != userSms){
                return  H5CommonResponse.getNewInstance(false, "商品已预约").toString();
            }
            try{
                afActivityUserSmsDo.setGoodsName(goodsName);
                afActivityUserSmsDo.setActivityTime(startTime);
                afActivityUserSmsDo.setSendTime(sendTime);
                int flag = afActivityUserSmsService.saveRecord(afActivityUserSmsDo);
                if(flag <= 0){
                    return H5CommonResponse.getNewInstance(false, "预约失败").toString();
                }
            }catch (Exception e){
                return H5CommonResponse.getNewInstance(false, "预约失败" + e.toString()).toString();
            }
            return H5CommonResponse.getNewInstance(true, "设置提醒成功，商品开抢后将通过短信通知您", "", goodsId).toString();
        } catch(FanbeiException e){
            String opennative = "/fanbei-web/opennative?name=";
            String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+ H5OpenNativeType.AppLogin.getCode();
            return H5CommonResponse
                    .getNewInstance(false, "登陆之后才能进行查看", notifyUrl,null )
                    .toString();
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return H5CommonResponse.getNewInstance(false, "预约失败").toString();
        }

    }

    private List<Map> getActivityPartList(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array) {
        //获取所有活动
        Map secActivityInfoMap = new HashMap();
        List<AfSeckillActivityDo> afSeckillActivityDos = afSeckillActivityService.getActivityNow();
        for(AfSeckillActivityDo afSeckillActivityDo : afSeckillActivityDos){
            Long activityId = afSeckillActivityDo.getRid();
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
                        goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsDo.getRid(),"0");

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

class GetActivityListThread implements Runnable {
    protected static final Logger logger = LoggerFactory.getLogger(GetActivityListThread.class);
    private List<AfModelH5ItemDo> subjectList;
    private AfResourceDo resource;
    private JSONArray array;
    private AfSubjectService afSubjectService;
    private AfSubjectGoodsService afSubjectGoodsService;
    private AfSchemeGoodsService afSchemeGoodsService;
    private AfInterestFreeRulesService afInterestFreeRulesService;
    private AfSeckillActivityService afSeckillActivityService;
    private ActivityGoodsUtil activityGoodsUtil;
    private AfSeckillActivityQuery query;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    Cache scheduledCache;
    GetActivityListThread(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array,
                          AfSubjectService afSubjectService,AfSubjectGoodsService afSubjectGoodsService,AfSchemeGoodsService afSchemeGoodsService,AfInterestFreeRulesService afInterestFreeRulesService,
                          BizCacheUtil bizCacheUtil,Cache scheduledCache,AfSeckillActivityService afSeckillActivityService,ActivityGoodsUtil activityGoodsUtil,AfSeckillActivityQuery query) {
        this.subjectList = subjectList;
        this.resource = resource;
        this.array = array;
        this.afInterestFreeRulesService = afInterestFreeRulesService;
        this.afSchemeGoodsService = afSchemeGoodsService;
        this.afSubjectGoodsService = afSubjectGoodsService;
        this.afSubjectService = afSubjectService;
        this.bizCacheUtil = bizCacheUtil;
        this.scheduledCache = scheduledCache;
        this.afSeckillActivityService = afSeckillActivityService;
        this.activityGoodsUtil = activityGoodsUtil;
        this.query = query;
    }

    @Override
    public void run() {
        getActivityPartList(subjectList,resource,array,afSubjectService,
                afSubjectGoodsService,afSchemeGoodsService,afInterestFreeRulesService,bizCacheUtil,scheduledCache);
        getActivityList(afSeckillActivityService,bizCacheUtil,scheduledCache,activityGoodsUtil,query);

    }
    private List<Map> getActivityList(AfSeckillActivityService afSeckillActivityService,BizCacheUtil bizCacheUtil,Cache scheduledCache,ActivityGoodsUtil activityGoodsUtil,AfSeckillActivityQuery query) {
        //获取所有活动
        List<Map<String, Object>> activityInfoList = activityGoodsUtil.getActivityGoods(query);
        bizCacheUtil.saveListForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
        scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST.getCode(), activityInfoList);
        return null;
    }
    private List<Map> getActivityPartList(List<AfModelH5ItemDo> subjectList,AfResourceDo resource,JSONArray array,
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
                            goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsDo.getRid(),"0");
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
            bizCacheUtil.saveListByTime(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST.getCode(), activityList, 10*60);
            scheduledCache.putObject(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST.getCode(), activityList);
            //Boolean isProcess = false;
            //bizCacheUtil.saveObjectForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode(),isProcess);
        }catch (Exception e){
            logger.error("");
        }finally {
            //Boolean isProcess = false;
            //bizCacheUtil.saveObjectForever(CacheConstants.PART_ACTIVITY.GET_ACTIVITY_INFO_V2_PROCESS_KEY.getCode(),isProcess);
        }

        return null;
    }
}
