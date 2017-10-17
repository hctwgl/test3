package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "activityUserInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String activityUserInfo(HttpServletRequest request,long userId){
        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        //查看活动规则
        List listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
        //用户的邀请码
        String InvitationCode=afRecommendUserService.getUserRecommendCode(userId);
        //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId);
        map.put("listRule",listRule);
        map.put("InvitationCode",InvitationCode);
        map.put("sumPrizeMoney",sumPrizeMoney);
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
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
