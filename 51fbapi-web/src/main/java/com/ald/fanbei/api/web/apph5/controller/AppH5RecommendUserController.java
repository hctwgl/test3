package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @类描述：
 * 推荐活动
 * @author 洪正沛 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/recommenduser/")
public class AppH5RecommendUserController extends BaseController {

    @Resource
    AfRecommendUserService afRecommendUserService;


    @RequestMapping(value = "prizeUser", method = RequestMethod.GET)
    public void getPrizeUser(HttpServletRequest request, ModelMap model) throws IOException {
//        FanbeiWebContext context = doWebCheck(request, false);
//        String userName = context.getUserName();
        String dataMonth= request.getParameter("datamonth");
        List<HashMap> mapList = afRecommendUserService.getPrizeUser(dataMonth);
        model.put("dataList",mapList);
    }


    @RequestMapping(value = "recommendListSort", method = RequestMethod.GET)
    public void getRecommendListSort(HttpServletRequest request, ModelMap model){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        Date start = c.getTime();

        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至0
        ca.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至0
        ca.set(Calendar.MINUTE, 59);
        //将秒至0
        ca.set(Calendar.SECOND,59);
        //将毫秒至0
        ca.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        Date last = ca.getTime();

        List<HashMap> list = afRecommendUserService.getRecommendListSort(start,last);

        model.put("dataList",list);
    }





    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
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
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

}
