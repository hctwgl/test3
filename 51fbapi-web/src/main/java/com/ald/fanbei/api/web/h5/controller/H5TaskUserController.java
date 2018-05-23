package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiao @date 2018/5/16 18:21
 * @类描述：任务
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping(value = "/taskUser/")
public class H5TaskUserController extends BaseController {

    @Resource
    private AfTaskUserService afTaskUserService;

    @Resource
    private AfTaskBrowseGoodsService afTaskBrowseGoodsService;

    /**
     * 浏览任务
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addBrowseTaskUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String addBrowseTaskUser(HttpServletRequest request, HttpServletResponse response){
        try{
            FanbeiH5Context context = doH5Check(request, false);
            Long userId = context.getUserId();
            if(null != userId){
                Map<String, Object> data = Maps.newHashMap();
                String goodsId = request.getParameter("goodsId");
                String taskContition = request.getParameter("activityUrl");

                if(StringUtils.isNotEmpty(goodsId)) {
                    // 指定浏览商品、品牌、分类任务等
                    List<AfTaskUserDo> specifiedTaskUserList = afTaskUserService.browerAndShoppingHandler(userId, Long.parseLong(goodsId), AfTaskType.BROWSE.getCode());

                    // 每日浏览任务
                    AfTaskUserDo taskUserDo = afTaskBrowseGoodsService.addBrowseGoodsTaskUserRecord(userId, Long.parseLong(goodsId));
                    if (null != taskUserDo) {
                        specifiedTaskUserList.add(taskUserDo);
                    }

                    String taskUserIds = buildTaskUserIds(specifiedTaskUserList);
                    if(StringUtils.isNotEmpty(taskUserIds)){
                        data.put("taskUserIds", taskUserIds);
                        return H5CommonResponse.getNewInstance(true, "", "", data).toString();
                    }
                }
                else if(StringUtils.isNotEmpty(taskContition)){
                    // 浏览活动链接任务
                    List<AfTaskUserDo> specifiedTaskUserList = afTaskUserService.taskHandler(userId, taskContition, AfTaskType.BROWSE.getCode());
                    String taskUserIds = buildTaskUserIds(specifiedTaskUserList);
                    if(StringUtils.isNotEmpty(taskUserIds)){
                        data.put("taskUserIds", taskUserIds);
                        return H5CommonResponse.getNewInstance(true, "", "", data).toString();
                    }
                }
            }
        }catch(Exception e){
            logger.error("addBrowseTaskUser error, ", e);
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
    }

    /**
     * 领取浏览奖励
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "receiveBrowseTaskRewards", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String receiveBrowseTaskRewards(HttpServletRequest request, HttpServletResponse response) {
        try{
            FanbeiH5Context context = doH5Check(request, true);
            Long userId = context.getUserId();
            Map<String, Object> data = Maps.newHashMap();
            String taskUserIds = request.getParameter("taskUserIds");
            if (StringUtils.isNotEmpty(taskUserIds)) {
                List<Long> taskUserIdList = Lists.newArrayList();
                String[] taskUserIdAray = taskUserIds.split(",");
                for (String id : taskUserIdAray) {
                    taskUserIdList.add(Long.parseLong(id));
                }

                List<AfTaskUserDo> taskUserList = afTaskUserService.getTaskUserListByIds(taskUserIdList);
                int rewardType = afTaskUserService.isSameRewardType(taskUserList);
                if (-1 == rewardType) {
                    return H5CommonResponse.getNewInstance(false, "").toString();
                } else if (-2 == rewardType) {
                    data.put("rewardType", rewardType);
                    afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
                    return H5CommonResponse.getNewInstance(true, "", "", data).toString();
                } else {
                    data.put("rewardType", rewardType);
                    data.put("rewardAmount", afTaskUserService.getRewardAmount(taskUserList, rewardType));
                    afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
                    return H5CommonResponse.getNewInstance(true, "", "", data).toString();
                }
            }
        }catch(Exception e){
            logger.error("receiveBrowseTaskRewards error, ", e);
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
    }

    /**
     * 是否完成今日浏览数量的任务
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "isCompletedBrowseQuantityTask", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String isCompletedBrowseQuantityTask(HttpServletRequest request, HttpServletResponse response) {
        try{
            Map<String, Object> data = Maps.newHashMap();
            AfTaskUserDo taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(Constants.BROWSE_TASK_NAME);
            if(null != taskUserDo){
                data.put("status", taskUserDo.getStatus());
                return H5CommonResponse.getNewInstance(true,"","",data).toString();
            }
        }catch (Exception e){
            logger.error("isCompletedBrowseQuantityTask error", e);
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
    }
    /**
     * 生成完成任务IDs
     * @param taskUserDoList
     * @return
     */
    private static String buildTaskUserIds(List<AfTaskUserDo> taskUserDoList) {
        if(null == taskUserDoList || taskUserDoList.isEmpty()){
            return null;
        }
        StringBuffer idStringBuffer = new StringBuffer();
        for (AfTaskUserDo tempTaskUser : taskUserDoList) {
            idStringBuffer.append(tempTaskUser.getRid()).append(",");
        }
        idStringBuffer.deleteCharAt(idStringBuffer.length() - 1);
        return idStringBuffer.toString();
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
