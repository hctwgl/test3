package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增分享
 */
@Component("addRecommendSharedApi")
public class addRecommendShared implements ApiHandle {
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserService afUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        logger.info("activity true = "+ context.getUserId());
        String notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);


        long userId = context.getUserId();
        AfUserDo afUserDo = afUserService.getUserById(userId);
        Map<String, Object> params = requestDataVo.getParams();
        Integer type = Integer.parseInt(ObjectUtils.toString(params.get("type"), "0").toString());
        String uuid = ObjectUtils.toString(params.get("uuid"), "").toString();
        String source = ObjectUtils.toString(params.get("source"), "").toString();
        String sourceType= ObjectUtils.toString(params.get("sourceType"), "").toString();
        String shareUrl = ObjectUtils.toString(params.get("shareUrl"), "").toString();

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

        AfRecommendShareDo afRecommendShareDo = new AfRecommendShareDo();
        afRecommendShareDo.setUser_id(afUserDo.getRid());
        afRecommendShareDo.setType(type);
        afRecommendShareDo.setRecommend_code(afUserDo.getRecommendCode());
        if(uuid !=null && !uuid.equals("")){
            afRecommendShareDo.setId(uuid);
        }

        afRecommendShareDo.setSource(StringUtils.isEmpty(source) ? null : source);
        afRecommendShareDo.setSourceType(StringUtils.isEmpty(sourceType) ? null : Integer.parseInt(sourceType));
        afRecommendShareDo.setShareUrl(StringUtils.isEmpty(shareUrl) ? null : StringUtils.trim(shareUrl));

        //RECOMMEND_SHARED_IMG
        //RECOMMEND_SHARED_TITLE
        //RECOMMEND_SHARED_DESCRIPTION
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


        String url = notifyHost +"/fanbei-web/app/inviteShare?sharedId="+afRecommendShareDo.getId();

        HashMap ret = new HashMap();
        afRecommendUserService.addRecommendShared(afRecommendShareDo);

        try{
            if(StringUtils.isNotEmpty(shareUrl)){

            }
        }catch(Exception e){

        }

        ret.put("url",url);
        ret.put("title",sharedTitle);
        ret.put("img",sharedImg);
        ret.put("desc",sharedDesc);
        resp.setResponseData(ret);
        return resp;
    }
}
