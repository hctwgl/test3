package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfRecommendUserDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
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

    /**
     * 获奖名单
     * @param request
     * @param model
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "prizeUser", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
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
    @RequestMapping(value = "recommendListSort", method = RequestMethod.GET)
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

        //model.put("dataList",list);

        String ret = JSON.toJSONString(list);
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





    @ResponseBody
    @RequestMapping(value = "insertTestData", method = RequestMethod.GET)
    public String insertTestData(){

        long userId = 178;
        HashMap totalData = afRecommendUserService.getRecommedData(userId);
        List<AfResourceDo> list = afRecommendUserService.getActivieResourceByType("RECOMMEND_ONE_IMG");
        HashMap ret = new HashMap();
        ret.put("userData",totalData);
        ret.put("pic",list);

        String json = JSON.toJSONString(ret);


//        for (int i=0;i<30000;i++){
//            long parentId = i;
//            for(int j = 0;j<10;j++) {
//                long userId = i*10 +j;
//                AfRecommendUserDo afRecommendUserDo = new AfRecommendUserDo();
//                afRecommendUserDo.setUser_id(userId);
//                afRecommendUserDo.setParentId(parentId);
//                if(j>4){
//                    afRecommendUserDo.setIs_loan(true);
//                    afRecommendUserDo.setLoan_time(new Date());
//                    afRecommendUserDo.setLoan_user_count(1);
//                }
//                afRecommendUserDao.addRecommendUser(afRecommendUserDo);
//            }
//        }
        return  "success";
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
