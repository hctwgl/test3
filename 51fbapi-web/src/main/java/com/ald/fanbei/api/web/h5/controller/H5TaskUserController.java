package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luoxiao @date 2018/5/16 18:21
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping(value = "/taskUser/")
public class H5TaskUserController extends BaseController {

    @Resource
    private AfTaskUserService afTaskUserService;

    @Resource
    private AfTaskBrowseGoodsService afTaskBrowseGoodsService;

    @Resource
    private AfResourceService afResourceService;

    /**
     * 浏览商品、品牌、分类
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addBrowseTaskUser", method = RequestMethod.POST, produces = "application/json")
    public String addBrowseTaskUser(HttpServletRequest request, HttpServletResponse response){
        FanbeiH5Context context = doH5Check(request, false);
        Long userId = context.getUserId();
        if(null != userId){
            String goodsId = request.getParameter("goodsId");
            if(StringUtils.isNotEmpty(goodsId)){
                boolean result = afTaskUserService.browerAndShoppingHandler(userId, Long.parseLong(goodsId), AfTaskType.BROWSE.getCode());
                if(result){
                    return H5CommonResponse.getNewInstance(true, "").toString();
                }
            }
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
    }

    /**
     * 浏览活动任务
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addBrowseActivityTaskUser", method = RequestMethod.POST, produces = "application/json")
    public String addBrowseActivityTaskUser(HttpServletRequest request, HttpServletResponse response){
        FanbeiH5Context context = doH5Check(request, false);
        Long userId = context.getUserId();
        if(null != userId){
            String taskContition = request.getParameter("activityUrl");
            if(StringUtils.isNotEmpty(taskContition)){
                boolean result = afTaskUserService.taskHandler(userId, taskContition, AfTaskType.BROWSE.getCode());
                if(result){
                    return H5CommonResponse.getNewInstance(true, "").toString();
                }
            }
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
    }

    @ResponseBody
    @RequestMapping(value = "addBrowseGoodsTaskUserRecord", method = RequestMethod.POST, produces = "application/json")
    public String addBrowseGoodsTaskUserRecord(HttpServletRequest request, HttpServletResponse response){
        FanbeiH5Context context = doH5Check(request, false);
        Long userId = context.getUserId();
        if(null != userId){
            String goodsId = request.getParameter("goodsId");
            if(StringUtils.isNotEmpty(goodsId)){
                if(0 < afTaskBrowseGoodsService.addBrowseGoodsTaskUserRecord(userId, Long.parseLong(goodsId))){
                    return H5CommonResponse.getNewInstance(true, "").toString();
                }
            }
        }

        return H5CommonResponse.getNewInstance(false, "").toString();
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
