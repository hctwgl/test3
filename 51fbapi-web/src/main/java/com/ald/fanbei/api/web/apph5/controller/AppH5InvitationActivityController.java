package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类描述：
 * 推荐活动
 * @author caowu 2017年10月17日下午2:26:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5InvitationActivityController extends BaseController {

    @Resource
    AfRecommendUserService afRecommendUserService;


    /**
     * 活动页面的基本信息
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "activityUserInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String activityUserInfo(HttpServletRequest request){
        FanbeiContext context =new FanbeiContext();
        Long userId =context.getUserId();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        if(userId==null){
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
            return resp.toString();
        }
        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        //查看活动规则,图片,标题,描述
        List listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
        List listPic=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_IMG");
        List listTitle=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_TITLE");
        List listDesc=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_DESCRIPTION");
        //用户的邀请码
        String invitationCode=afRecommendUserService.getUserRecommendCode(userId);
        //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId);
        DecimalFormat df = new DecimalFormat("######0.00");//金钱格式 保留两位小数
        map.put("listRule",listRule);
        map.put("listPic",listPic);
        map.put("listTitle",listTitle);
        map.put("listDesc",listDesc);
        map.put("invitationCode",invitationCode);
        map.put("sumPrizeMoney",df.format(sumPrizeMoney));
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        return ret;
    }

    /**
     * 奖励详细查询
     * @param request
     * @param currentPage
     * @param type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "rewardQuery", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String rewardQuery(HttpServletRequest request,String type,Integer currentPage, Integer pageSize){
        FanbeiContext context =new FanbeiContext();
        Long userId =context.getUserId();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        if(userId==null){
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
            return resp.toString();
        }
        if(currentPage==null){
            currentPage=1;
        }
        String ret = null;
        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        List<AfRecommendUserDo> rewardQueryList =new ArrayList<>();
        if("1".equals(type)||"2".equals(type)){
            rewardQueryList=afRecommendUserService.rewardQuery(userId,type,currentPage,pageSize);
            Integer count =afRecommendUserService.rewardQueryCount(userId,type);
            map.put("rewardQueryList",rewardQueryList);
            map.put("count",count);
            map.put("currentPage",currentPage);
            hashMapList.add(map);
            ret =JSON.toJSONString(hashMapList);
        }else{
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null);
            ret =resp.toString();
        }
        return ret;
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
