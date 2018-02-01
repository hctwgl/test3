package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @类描述：
 * 获取临时额度
 * @author caowu 2017年11月17日下午2:36:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5InterimAuController extends BaseController {

    @Resource
    AfUserService afUserService;

    @Resource
    AfInterimAuService afInterimAuService;

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfResourceService afResourceService;

    @Resource
    RiskUtil riskUtil;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    DecimalFormat   df   =new DecimalFormat("0.00");


    /**
     * 悬浮窗信息 floatInfo
     */
    @ResponseBody
    @RequestMapping(value = "floatInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String floatInfo(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        HashMap<String,Object> data =new HashMap<>();
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndValue("SUSPENSION_FRAME_SETTING","0");
                data.put("name",afResourceDo.getName());
                data.put("pic1",afResourceDo.getPic1());
                data.put("pic2",afResourceDo.getPic2());
                //afUser = afUserService.getUserByUserName(context.getUserName());
                //Long userId = afUser.getRid();
                resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
                return resp.toString();
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
                return resp.toString();
            }

        }catch (Exception e) {
            logger.error("commitChannelRegister", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

    }


    /**
     *进入申请临时提额页面
     */
    @ResponseBody
    @RequestMapping(value = "applyInterimAuPage", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String applyInterimAuPage(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        HashMap<String,Object> data =new HashMap<>();
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("SUSPENSION_FRAME_SETTING","0");
//                data.put("rule",afResourceDo.getValue3());
                String rule =afResourceDo.getValue3().replaceAll("\r\n","<br/>");
                rule =rule.replaceAll("\n","<br/>");
                data.put("rule",rule.substring(rule.indexOf("<br/>"),rule.length()));
                String ruleTitle=rule.substring(0,rule.indexOf("<br/>"));
                data.put("ruleTitle",ruleTitle.replaceAll("：",""));
                afUser = afUserService.getUserByUserName(context.getUserName());
                Long userId = afUser.getRid();
                //Long userId = 520l; //666

                BigDecimal interimAmount=new BigDecimal(0);
                //判断是否已提过额,还清状态,有效状态
                if(afInterimAuService.selectExistAuByUserId(userId)>0){
                    AfInterimAuDo afInterimAuDo= afInterimAuService.getAfInterimAuByUserId(userId);
                    data.put("type",0);//临时额度
                    data.put("interimAmount",df.format(afInterimAuDo.getInterimAmount()));//临时额度
                    data.put("interimUsed",df.format(afInterimAuDo.getInterimUsed()));//已使用的额度
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    data.put("gmtFailuretime",sdf.format(afInterimAuDo.getGmtFailuretime()));//失效时间
                    int failureStatus =0;//0未失效,1失效
                    if(afInterimAuDo.getGmtFailuretime().getTime()< new Date().getTime()){
                        failureStatus=1;
                    }else{
                        interimAmount =afInterimAuDo.getInterimAmount();
                    }
                    data.put("failureStatus",failureStatus);
                }else{
                    //是否申请失败记录
                    List<AfInterimAuDo> applyFailList =afInterimAuService.selectApplyFailByUserId(userId);
                    if(applyFailList!=null&&applyFailList.size()>0){
                        data.put("type",1);//临时额度
                        int differentDays =differentDaysByMillisecond(applyFailList.get(0).getGmtCreate(),new Date());
                        if(differentDays>Integer.parseInt(afResourceDo.getValue2())){
                            data.put("againApplyTime",0);
                            data.put("againApplyDesc","申请提额!");
                        }else{
                            int againApplyTime=Integer.parseInt(afResourceDo.getValue2())-differentDays;
                            data.put("againApplyTime",againApplyTime);
                            data.put("againApplyDesc","请在"+againApplyTime+"天后再试!");
                        }
                    }else{
                        data.put("type",2);//未申请过
                    }

                }
                //可用额度=可用额度+临时额度
                AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndType(UserAccountSceneType.ONLINE.getCode(),userId);
                BigDecimal auAmount = new BigDecimal(0);
                if(afUserAccountSenceDo != null){
                    auAmount = afUserAccountSenceDo.getAuAmount();
                }
                data.put("amount", df.format(auAmount.add(interimAmount)));

                resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
                return resp.toString();
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
                return resp.toString();
            }
        }catch  (Exception e) {
            logger.error("commitChannelRegister", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

    }


    /**
     * 申请临时提额
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "applyInterimAu", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String applyInterimAu(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        HashMap<String,Object> data =new HashMap<>();
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("SUSPENSION_FRAME_SETTING","0");
                //判断临时提额活动是否已开启
                if("0".equals(afResourceDo.getValue())){
                    afUser = afUserService.getUserByUserName(context.getUserName());
                    //afUser =afUserService.getUserByUserName(request.getParameter("userName"));
                    if(afUser != null){
                        Long userId = afUser.getRid();
                        //判断是否有申请失败记录
                        List<AfInterimAuDo> applyFailList =afInterimAuService.selectApplyFailByUserId(userId);
                        boolean judge=true;
                        if(applyFailList!=null&&applyFailList.size()>0){
                            int differentDays =differentDaysByMillisecond(applyFailList.get(0).getGmtCreate(),new Date());
                            if(differentDays<Integer.parseInt(afResourceDo.getValue2())){
                                judge=false;
                            }
                        }
                        if(judge){
                            //判断是否已提过额,还清状态,无效状态
                            if(afInterimAuService.selectExistAuByUserId(userId)>0){
                                int failureStatus =0;//0未失效,1失效
                                int i =1;
                                AfInterimAuDo afInterimAuDo= afInterimAuService.getAfInterimAuByUserId(userId);
                                i =afInterimAuDo.getInterimUsed().compareTo(BigDecimal.ZERO);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                data.put("gmtFailuretime",sdf.format(afInterimAuDo.getGmtFailuretime()));//失效时间
                                if(afInterimAuDo.getGmtFailuretime().getTime()< new Date().getTime()){
                                    failureStatus=1;
                                }
                                if(failureStatus!=1&&i==0){
                                    data.put("type",0);
                                    data.put("interimAmount",afInterimAuDo.getInterimAmount());
                                    resp = H5CommonResponse.getNewInstance(false, "您当前已有临时额度,无法再次申请!", "", data);
                                    return resp.toString();
                                }
                            }

                            //判断是否已经过强风控
                            String msg="";
                            AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,UserAccountSceneType.ONLINE.getCode());
                            String userAuthStatus = "N";
                            if(afUserAuthStatusDo != null){
                                userAuthStatus = afUserAuthStatusDo.getStatus();
                            }
                            if("Y".equals(userAuthStatus)){
                                //推送用户信息给风控
                                Date date = new Date();//取时间
                                boolean isSuccess =true;//调用风控接口 默认成功
                                BigDecimal interimAmount = riskUtil.userTempQuota(userId);
                         //       BigDecimal interimAmount = new BigDecimal(1000);
                                int i =interimAmount.compareTo(BigDecimal.ZERO);
                                if(i==1){
                                    Calendar calendar  =   Calendar.getInstance();
                                    calendar.setTime(date); //需要将date数据转移到Calender对象中操作
                                    calendar.add(calendar.DATE, Integer.parseInt(afResourceDo.getValue1()));//把日期往后增加n天.正数往后推,负数往前移动
                                    date=calendar.getTime();   //这个时间就是日期往后推多少天的结果
                                }else{
                                    isSuccess= false;
                                }
                                //向表里写入数据
                                if(afInterimAuService.insertInterimAmountAndLog(isSuccess,userId,interimAmount,date)>0){
                                    if(isSuccess){
                                        data.put("type",1);
                                        data.put("interimAmount",df.format(interimAmount));
                                        resp = H5CommonResponse.getNewInstance(true, "申请成功！", "", data);
                                        return resp.toString();
                                    }else{
                                        data.put("type",3);
                                        resp = H5CommonResponse.getNewInstance(false, "抱歉，您此次申请不符合提额要求，听说多分期和借钱能提高通过率~！", "", data);
                                        return resp.toString();
                                    }

                                }else{
                                    resp = H5CommonResponse.getNewInstance(false, "插入数据失败！", "", data);
                                    return resp.toString();
                                }

                            }else{
                                data.put("type",2); //未经过强风控
                                msg= "请先提交基础认证审核!";
                            }
                            resp = H5CommonResponse.getNewInstance(false, msg, "", data);
                            return resp.toString();

                        }else{
                            resp = H5CommonResponse.getNewInstance(false, "未到风控审核拒绝后申请天数!", "", data);
                            return resp.toString();
                        }
                    }else{
                        resp = H5CommonResponse.getNewInstance(false, "未查询到有效用户!!!", "", null);
                        return resp.toString();
                    }
                }else{
                    resp = H5CommonResponse.getNewInstance(false, "临时额度活动未开启!!!", "", data);
                    return resp.toString();
                }
            }else{
                data.put("type",55);
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", data);
                return resp.toString();
            }
        }catch  (Exception e) {
            logger.error("commitChannelRegister", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     * 获取临时额度活动页面信息
     *
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getInterimActivity", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
    public String getInterimActivity(HttpServletRequest request) {
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        HashMap<String, Object> data = new HashMap<>();
        try {
            //获取临时额度活动
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("SUSPENSION_FRAME_SETTING", "0");
            data.put("rules", afResourceDo.getValue3());           //活动规则
            data.put("amount", new BigDecimal(0));           //可用额度
            data.put("interimAmount", new BigDecimal(0));   //临时额度
            data.put("failureTime", "");                          //失效时间
            data.put("retryDay", afResourceDo.getValue2());        //重新申请天数
            data.put("type", "0");                                 //申请状态 0.正常 1.暂时无法再次提额 2.请在7天后再试
            context = doWebCheck(request, false);
            if (context.isLogin()) {
                afUser = afUserService.getUserByUserName(context.getUserName());
                if (afUser != null) {
                    //获取可用额度
                    data.put("amount", afUserAccountService.getAuAmountByUserId(afUser.getRid()));
                    //获取申请状态
                    AfInterimAuLogDo afInterimAuLogDo = afInterimAuService.getLastLogByUserId(afUser.getRid());
                    if (afInterimAuLogDo != null) {
                        if (afInterimAuLogDo.getStatus() == 1) {
                            data.put("type", "2");
                        }
                    }
                    //获取临时额度
                    AfInterimAuDo afInterimAuDo = afInterimAuService.getByUserId(afUser.getRid());
                    if (afInterimAuDo != null) {
                        data.put("interimAmount", afInterimAuDo.getInterimAmount());
                        data.put("failureTime", DateUtil.getShortNow(afInterimAuDo.getGmtFailuretime()));
                        if (afInterimAuDo.getGmtFailuretime().compareTo(DateUtil.getToday()) < 0) {
                            data.put("type", "1");
                        }
                    }
                    resp = H5CommonResponse.getNewInstance(true, "查询成功", "", data);
                    return resp.toString();
                } else {
                    resp = H5CommonResponse.getNewInstance(false, "未查询到有效用户!!!", "", data);
                    return resp.toString();
                }
            } else {
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", data);
                return resp.toString();
            }
        } catch (Exception e) {
            logger.error("getInterimActivity", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }


    /**
     * 两个时间相差多少天
     * @param date1
     * @param date2
     * @return
     */
    public int differentDaysByMillisecond(Date date1, Date date2) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString1=formatter.format(date1);
        String dateString2=formatter.format(date2);
        Date d1 = formatter.parse(dateString1);
        Date d2 = formatter.parse(dateString2);
        int days = (int) ((d2.getTime() - d1.getTime()) / (1000*3600*24));
        return days;
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
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return null;
    }
}
