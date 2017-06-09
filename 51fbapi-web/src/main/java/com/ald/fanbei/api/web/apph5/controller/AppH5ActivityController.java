package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfGoodsReservationStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.GoodsReservationWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.OrderNoUtils;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
 
/**
 * @类描述：
 * h5活动-预约等
 * @author chengkang 2017年4月5日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/activity/")
public class AppH5ActivityController extends BaseController {
    String  opennative = "/fanbei-web/opennative?name=";
     
    @Resource
    AfGoodsReservationService afGoodsReservationService;
    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfUserService afUserService;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfResourceService afResourceService;
 
 
    @ResponseBody
    @RequestMapping(value = "/reserveActivityGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String reserveActivityGoods(HttpServletRequest request, ModelMap model) throws IOException {
        try {
            String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            Map<String, Object> returnData = new HashMap<String, Object>();
            
            Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), null);
            Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), null);
            //Long rsvNums = NumberUtil.objToLongDefault(request.getParameter("rsvNums"), 1L);
            Long rsvNums =  1L;
            
            if (afUserDo == null) {
                String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();
                returnData.put("status", GoodsReservationWebFailStatus.UserNotexist.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.UserNotexist.getName(), notifyUrl,returnData )
                        .toString();
            }
 
            //前端没传递时，活动相关设置
            if(activityId==null || goodsId==null){
                //默认走的是oppo预约,获取配置中的oppo活动信息
                AfResourceDo activityResource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceSecType.OppoReservationActivity.getCode());
                if(activityResource==null || StringUtil.isEmpty(activityResource.getValue3())){
                    returnData.put("status", GoodsReservationWebFailStatus.ReservationConfigInvalid.getCode());
                    return H5CommonResponse
                            .getNewInstance(false, GoodsReservationWebFailStatus.ReservationConfigInvalid.getName(), "",returnData )
                            .toString();
                }
                
                //解析对应值
                Map<String,Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(activityResource.getValue3());
                goodsId = NumberUtil.objToLongDefault(jsonObjRes.get("goodsId"), 0L);
                activityId = activityResource.getRid();
            }
            
            AfResourceDo currActivityResource = afResourceService.getResourceByResourceId(activityId);
            if(currActivityResource==null){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationActNotExist.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationActNotExist.getName(), "",returnData )
                        .toString();
            }
            
            
            AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
            if(afGoodsDo==null){
                returnData.put("status", GoodsReservationWebFailStatus.GoodsNotExist.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.GoodsNotExist.getName(), "",returnData )
                        .toString();
            }
            
            if(!AfCounponStatus.O.getCode().equals(currActivityResource.getValue4())){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationClosed.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationClosed.getName(), "",returnData )
                        .toString();
            }
            //解析对应配置并校验
            Map<String,Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(currActivityResource.getValue3());
            Date startTime = DateUtil.parseDateyyyyMMddHHmmss(StringUtil.null2Str(jsonObjRes.get("startTime")));
            Date endTime = DateUtil.parseDateyyyyMMddHHmmss(StringUtil.null2Str(jsonObjRes.get("endTime")));
            
            //活动开始结束校验
            Date currDate = new Date();
            if(!DateUtil.compareDate(currDate,startTime)){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationNotStart.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationNotStart.getName(), "",returnData )
                        .toString();
            }
            
            if(DateUtil.compareDate(currDate,endTime)){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationHaveFinish.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationHaveFinish.getName(), "",returnData )
                        .toString();
            }
            
            String rsvNo = OrderNoUtils.getInstance().getSerialNumber();
            AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(afUserDo.getRid(), activityId, goodsId, rsvNums, rsvNo, new Date(), new Date(), AfGoodsReservationStatus.SUCCESS.getCode(), "");
            
            Integer revCountNums = afGoodsReservationService.getRevCountNumsByQueryCondition(afGoodsReservationDo);
            if(revCountNums>0){
                //同活动同商品只允许一次预约
            	logger.warn("用户预约商品次数超限,预约失败。userId:"+afUserDo.getRid()+",activityId:"+activityId+",goodsId"+goodsId+",revCountNums"+revCountNums);
                returnData.put("status", GoodsReservationWebFailStatus.ReservationTimesOverrun.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationTimesOverrun.getName(), "",returnData )
                        .toString();
            }
            
            if(!(afGoodsReservationService.addGoodsReservation(afGoodsReservationDo)>0)){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationFail.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationFail.getName(), "",returnData )
                        .toString();
            }
            
            //预约成功，短信通知
            try {
                smsUtil.sendGoodsReservationSuccessMsg(afUserDo.getMobile(), afGoodsDo.getName(), rsvNo);
            } catch (Exception e) {
                logger.error("活动产品预约成功消息通知异常userId："+afUserDo.getRid()+",",e);
            }
            
            returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
            return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", returnData).toString();
        } catch (Exception e) {
            return H5CommonResponse
                    .getNewInstance(false, GoodsReservationWebFailStatus.ReservationFail.getName(), "",null )
                    .toString();
        }
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/reserveActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String reserveActivityInfo(HttpServletRequest request, ModelMap model) throws IOException {
        Map<String, Object> returnData = new HashMap<String, Object>();
        try {
            String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            
            Long activityId = 0L;
            Long goodsId = 0L;
            String isCanReservation = YesNoStatus.NO.getCode();
            String activityStatus = AfCounponStatus.O.getCode();
            
            AfResourceDo activityResource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceSecType.OppoReservationActivity.getCode());
            if(activityResource==null || StringUtil.isEmpty(activityResource.getValue3())){
                returnData.put("status", GoodsReservationWebFailStatus.ReservationConfigInvalid.getCode());
                return H5CommonResponse
                        .getNewInstance(false, GoodsReservationWebFailStatus.ReservationConfigInvalid.getName(), "",returnData )
                        .toString();
            }
            
            //解析对应值
            Map<String,Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(activityResource.getValue3());
            goodsId = NumberUtil.objToLongDefault(jsonObjRes.get("goodsId"), 0L);
            activityId = activityResource.getRid();
            activityStatus = activityResource.getValue4();
            
            String startTime = StringUtil.null2Str(jsonObjRes.get("startTime"));
            String endTime = StringUtil.null2Str(jsonObjRes.get("endTime"));
            
            if(afUserDo!=null){
                AfGoodsReservationDo afGoodsReservationDo = new AfGoodsReservationDo(afUserDo.getRid(), activityId, goodsId, AfGoodsReservationStatus.SUCCESS.getCode());
                Integer revCountNums = afGoodsReservationService.getRevCountNumsByQueryCondition(afGoodsReservationDo);
                if(revCountNums==0){
                    isCanReservation = YesNoStatus.YES.getCode();
                }
            }
            
            Date startTimeDate = DateUtil.parseDateyyyyMMddHHmmss(startTime);
            Date endTimeDate = DateUtil.parseDateyyyyMMddHHmmss(endTime);
            //活动开关校验
            if(!AfCounponStatus.O.getCode().equals(activityResource.getValue4())){
                isCanReservation = YesNoStatus.NO.getCode();
            }
            
            //活动开始结束校验
            Date currDate = new Date();
            if(!DateUtil.compareDate(currDate,startTimeDate)){
                isCanReservation = YesNoStatus.NO.getCode();
            }
            
            if(DateUtil.compareDate(currDate,endTimeDate)){
                isCanReservation = YesNoStatus.NO.getCode();
            }
            
            
            returnData.put("status", FanbeiExceptionCode.SUCCESS.getCode());
            returnData.put("activityId", activityId);
            returnData.put("activityStatus", activityStatus);
            returnData.put("startTime", startTime);
            returnData.put("endTime", endTime);
            returnData.put("goodsId", goodsId);
            returnData.put("isCanReservation", isCanReservation);
            
            return H5CommonResponse
                    .getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "",returnData )
                    .toString();
        } catch (Exception e) {
            returnData.put("status", FanbeiExceptionCode.FAILED.getCode());
            return H5CommonResponse
                    .getNewInstance(false, FanbeiExceptionCode.FAILED.getCode(), "",returnData )
                    .toString();
        }
    }
    
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        // TODO Auto-generated method stub
        return null;
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see com.ald.fanbei.api.web.common.BaseController#parseRequestData(java.lang. String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see com.ald.fanbei.api.web.common.BaseController#doProcess(com.ald.fanbei.api .web.common.RequestDataVo, com.ald.fanbei.api.common.FanbeiContext, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return null;
    }
 
}