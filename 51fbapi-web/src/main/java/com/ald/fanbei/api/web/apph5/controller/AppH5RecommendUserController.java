package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @类描述：
 * 推荐活动
 * @author 洪正沛 2017年8月12日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5RecommendUserController extends BaseController {

    @Resource
    AfRecommendUserService afRecommendUserService;

    @Resource
    AfRecommendUserDao afRecommendUserDao;

    @Resource
    AfUserDao afUserDao;

    @ResponseBody
    @RequestMapping(value = "getPrizeByLastMonth",produces = "text/html;charset=UTF-8")
    public String getPrizeByLastMonth(){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();//获取当前时间
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间
        Date lastMonth = calendar.getTime();//获取一年前的时间，或者一个月前的时间

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式

        String year_month = df.format(lastMonth);

        List<HashMap> mapList = afRecommendUserService.getPrizeUser(year_month);

        HashMap retMap = new HashMap();
        retMap.put("datalist",mapList);


        SimpleDateFormat df1 = new SimpleDateFormat("MM");//设置日期格式
        String m = String.valueOf( Integer.parseInt(df1.format(lastMonth)));
        retMap.put("month",m);
        retMap.put("total",10);


        String ret = JSON.toJSONString(retMap);
//        String ret = JSON.toJSONString(mapList);
        return ret;

    }


    /**
     * 获奖名单
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "prizeUser",produces = "text/html;charset=UTF-8")
    public String getPrizeUser(String dataMonth) throws IOException {
//        FanbeiWebContext context = doWebCheck(request, false);
//        String userName = context.getUserName();

        List<HashMap> mapList = afRecommendUserService.getPrizeUser(dataMonth);
        //model.put("dataList",mapList);
        String ret = JSON.toJSONString(mapList);
        return ret;
    }


    /**
     * 全国排行
     * @param request
     * @param model
     */
    @ResponseBody
    @RequestMapping(value = "recommendListSort", method = RequestMethod.POST)
    public String getRecommendListSort(){
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

        SimpleDateFormat df = new SimpleDateFormat("MM");//设置日期格式
        String m = String.valueOf( Integer.parseInt(df.format(start)));
        HashMap map = new HashMap();
        map.put("month",m);
        map.put("total",20);
        map.put("datalist",list);
        String ret = JSON.toJSONString(map);
//        String ret = JSON.toJSONString(list);
        return ret;
    }

    /**
     *新增分享
     * @param request
     * @param type 类型 0 微信朋友圈，1 微信好友，2 qq空间 ，3二维码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addShared", method = RequestMethod.POST)
    public int addShared(HttpServletRequest request,int type){
        FanbeiWebContext context = doWebCheck(request, false);
        String userName = context.getUserName();
//        String userName ="13588469645";

        AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
        AfRecommendShareDo afRecommendShareDo = new AfRecommendShareDo();
        afRecommendShareDo.setUser_id(afUserDo.getRid());
        afRecommendShareDo.setType(type);
        afRecommendShareDo.setRecommend_code(afUserDo.getRecommendCode());
        return afRecommendUserService.addRecommendShared(afRecommendShareDo);
    }


    /**
     * 分享页
     * @param sharedId
     * @param request
     * @param model
     */
    @ResponseBody
    @RequestMapping(value = "recommendShared", method = RequestMethod.GET)
    public String recommendShared(String sharedId ) {
        HashMap afRecommendShareDo =  afRecommendUserService.getRecommendSharedById(sharedId);
        //model.put("data",afRecommendShareDo);
        String ret = JSON.toJSONString(afRecommendShareDo);
        return ret;
    }


    @Resource
    AfOrderDao afOrderDao;
    @Resource
    AfBorrowCashDao afBorrowCashDao;
    @ResponseBody
    @RequestMapping(value = "insertTestData", method = RequestMethod.GET)
    public String insertTestData(){

//        AfRecommendUserDo afRecommendUserDo1 = new AfRecommendUserDo();
//        afRecommendUserDo1.setUser_id(201582284L);
//        afRecommendUserDo1.setParentId(13989455667L);
//        afRecommendUserDao.addRecommendUser(afRecommendUserDo1);

//        afRecommendUserService.updateRecommendByBorrow(201582284L,new Date());

        HashMap map =  afBorrowCashDao.getBorrowCashByRemcommend(69399);
        Long count = (Long)map.get("count");

        long orderId =  217730;
        

        return  "success";
    }



    private Date getMonthLast(){
        Calendar ca = Calendar.getInstance();
        ca.set(2017, 8, 1, 0, 0, 0);
        return  ca.getTime();
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
