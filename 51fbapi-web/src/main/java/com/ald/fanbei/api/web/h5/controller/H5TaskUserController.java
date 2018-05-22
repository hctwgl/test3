package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        FanbeiH5Context context = doH5Check(request, false);
        Long userId = context.getUserId();
        if(null != userId){
            Map<String, Object> data = Maps.newHashMap();
            String goodsId = request.getParameter("goodsId");
            String taskContition = request.getParameter("activityUrl");

            if(StringUtils.isNotEmpty(goodsId)){
                // 指定浏览商品、品牌、分类任务
                boolean browseSpecifiedTask = afTaskUserService.browerAndShoppingHandler(userId, Long.parseLong(goodsId), AfTaskType.BROWSE.getCode());

                // 每日浏览任务
                Long browseQuantityCoinAmount = afTaskBrowseGoodsService.addBrowseGoodsTaskUserRecord(userId, Long.parseLong(goodsId));
                data.put("browseQuantityCoinAmount", browseQuantityCoinAmount);

                if(null != browseQuantityCoinAmount || browseSpecifiedTask){
                    return H5CommonResponse.getNewInstance(true, "", "", data).toString();
                }
            }
            else if(StringUtils.isNotEmpty(taskContition)){
                // 浏览活动链接任务
                boolean result = afTaskUserService.taskHandler(userId, taskContition, AfTaskType.BROWSE.getCode());
                if(result){
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
