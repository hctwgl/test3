package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfTaskService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 新增分享
 */
@Component("addRecommendSharedApi")
public class addRecommendShared implements ApiHandle {
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfTaskService afTaskService;
    @Resource
    AfTaskUserService afTaskUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
//        String notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);


        long userId = context.getUserId();
//        AfUserDo afUserDo = afUserService.getUserById(userId);
        Map<String, Object> params = requestDataVo.getParams();
//        Integer type = Integer.parseInt(ObjectUtils.toString(params.get("type"), "0").toString());
//        String uuid = ObjectUtils.toString(params.get("uuid"), "").toString();
//        String source = ObjectUtils.toString(params.get("source"), "").toString();
//        String sourceType= ObjectUtils.toString(params.get("sourceType"), "").toString();
        String shareUrl = ObjectUtils.toString(params.get("shareUrl"), "").toString();

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

            List<AfTaskUserDo> toAddTaskUserList = Lists.newArrayList();
            List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
            List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(TaskType.share.getCode(), userLevelList, shareUrl);
            if (null != taskList && !taskList.isEmpty()) {
                List<AfTaskDo> notDailyTaskUserList = afTaskService.getNotDailyTaskListByUserId(userId, TaskType.share.getCode());
                List<AfTaskDo> taskUserCompleteList = Lists.newArrayList();
                if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
                    taskUserCompleteList.addAll(notDailyTaskUserList);
                }
                if (!taskUserCompleteList.isEmpty()) {
                    Iterator<AfTaskDo> iter = taskList.iterator();
                    while(iter.hasNext()){
                        if(taskUserCompleteList.contains(iter.next())){
                            iter.remove();
                        }
                    }
                }
                if (!taskList.isEmpty()) {
                    AfTaskUserDo taskUserDo;
                    for (AfTaskDo taskDo : taskList) {
                        taskUserDo = buildTaskUserDo(taskDo, userId);
                        toAddTaskUserList.add(taskUserDo);
                    }
                    for(AfTaskUserDo afTaskUserDo : toAddTaskUserList){
                        afTaskUserService.insertTaskUserDo(afTaskUserDo);
                    }
                }
            }

//
//
//        AfRecommendShareDo afRecommendShareDo = new AfRecommendShareDo();
//        afRecommendShareDo.setUser_id(afUserDo.getRid());
//        afRecommendShareDo.setType(type);
//        afRecommendShareDo.setRecommend_code(afUserDo.getRecommendCode());
//        if(uuid !=null && !uuid.equals("")){
//            afRecommendShareDo.setId(uuid);
//        }
//
//        afRecommendShareDo.setSource(StringUtils.isEmpty(source) ? null : source);
//        afRecommendShareDo.setSourceType(StringUtils.isEmpty(sourceType) ? null : Integer.parseInt(sourceType));
//        afRecommendShareDo.setShareUrl(StringUtils.isEmpty(shareUrl) ? null : StringUtils.trim(shareUrl));
//
//        //RECOMMEND_SHARED_IMG
//        //RECOMMEND_SHARED_TITLE
//        //RECOMMEND_SHARED_DESCRIPTION
//        String sharedImg ="";
//        String sharedTitle ="";
//        String sharedDesc ="";
//        List<AfResourceDo> listImg = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_IMG");
//        List<AfResourceDo> listTitle = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_TITLE");
//        List<AfResourceDo> listDes = afRecommendUserService.getActivieResourceByType("RECOMMEND_SHARED_DESCRIPTION");
//        if(listImg != null && listImg.size()>0){
//            sharedImg = listImg.get(0).getValue();
//        }
//        if(listTitle != null && listTitle.size()>0){
//            sharedTitle = listTitle.get(0).getValue();
//        }
//
//        if(listDes != null && listDes.size()>0){
//            sharedDesc = listDes.get(0).getValue();
//        }
//
//
//        String url = notifyHost +"/fanbei-web/app/inviteShare?sharedId="+afRecommendShareDo.getId();
//
//        HashMap ret = new HashMap();
//        afRecommendUserService.addRecommendShared(afRecommendShareDo);
//
//        try{
//            if(StringUtils.isNotEmpty(shareUrl)){
//
//            }
//        }catch(Exception e){
//
//        }
//
//        ret.put("url",url);
//        ret.put("title",sharedTitle);
//        ret.put("img",sharedImg);
//        ret.put("desc",sharedDesc);
//        resp.setResponseData(ret);
        return resp;
    }

    /**
     * 构造taskUserDo 对象
     * @param taskDo
     * @param userId
     * @return
     */
    public AfTaskUserDo buildTaskUserDo(AfTaskDo taskDo, Long userId){
        AfTaskUserDo taskUserDo = new AfTaskUserDo();
        int rewardType = taskDo.getRewardType();
        taskUserDo.setRewardType(rewardType);
        if(0 == rewardType){
            taskUserDo.setCoinAmount(taskDo.getCoinAmount());
        } else if(1 == rewardType){
            taskUserDo.setCashAmount(taskDo.getCashAmount());
        } else{
            taskUserDo.setCouponId(taskDo.getCouponId());
        }
        taskUserDo.setTaskId(taskDo.getRid());
        taskUserDo.setTaskName(taskDo.getTaskName());
        taskUserDo.setUserId(userId);
        taskUserDo.setGmtCreate(new Date());
        taskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_0);
        return taskUserDo;
    }

}
