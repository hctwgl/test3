package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component("getUserRecommedApi")
public class GetUserRecommedApi implements ApiHandle{
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserService afUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        long userId = context.getUserId();
        AfUserDo afUserDo = afUserService.getUserById(userId);
        HashMap totalData = afRecommendUserService.getRecommedData(userId);
        List<AfResourceDo> list = afRecommendUserService.getActivieResourceByType("RECOMMEND_BACK_IMG");
        HashMap ret = new HashMap();
        String color = "#FE4B2D";
        if(totalData == null){
            totalData = new HashMap();
        }

        String notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);


        //# regoin 分享
        String sharedImg ="";
        String sharedTitle ="";
        String sharedDesc ="";
        List<AfResourceDo> listImg = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_IMG");
        List<AfResourceDo> listTitle = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_TITLE");
        List<AfResourceDo> listDes = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_DESCRIPTION");
        if(listImg != null && listImg.size()>0){
            sharedImg = listImg.get(0).getValue();
        }
        if(listTitle != null && listTitle.size()>0){
            sharedTitle = listTitle.get(0).getValue();
        }

        if(listDes != null && listDes.size()>0){
            sharedDesc = listDes.get(0).getValue();
        }


        String allOrderUrl = notifyHost+"/fanbei-web/app/inviteRank";
        Date now = new Date();
        Date last = getMonthLast();
        if(now.getTime() > last.getTime()){
            allOrderUrl +="?addUiName = LAST_WIN_RANK";
        }
        //test
//        if(notifyHost.equals("http://testapp.51fanbei.com")) {
        allOrderUrl += "?addUiName = LAST_WIN_RANK";
//        }



        totalData.put("allOrderUrl",allOrderUrl);
        totalData.put("activeRule","http://www.baidu.com");
        totalData.put("recommendCode",afUserDo.getRecommendCode());

//        addUiName = LAST_WIN_RANK

        String sharedurl = notifyHost +"/fanbei-web/app/inviteShare?recommendCode="+afUserDo.getRecommendCode();
        totalData.put("url",sharedurl);
        totalData.put("title",sharedTitle);
        totalData.put("img",sharedImg);
        totalData.put("desc",sharedDesc);

        //# endregion
        ret.put("userData",totalData);
        ret.put("pic",list);

        if(list != null && list.size()>0){
            color = list.get(0).getValue1();
        }
        ret.put("color",color);
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(ret);
        return resp;
    }


    private Date getMonthLast(){
        Calendar ca = Calendar.getInstance();
        ca.set(2017, 8, 1, 0, 0, 0);
        return  ca.getTime();
    }
}