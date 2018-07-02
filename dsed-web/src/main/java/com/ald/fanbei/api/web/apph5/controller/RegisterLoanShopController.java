package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUnionThirdRegisterDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 * <p>Title:RegisterLoanShopController <p>
 * <p>Description: <p>
 * @Copyright (c)  浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年8月9日下午4:39:30
 *
 */

@Controller
@RequestMapping("/borrowCash")
public class RegisterLoanShopController extends BaseController {

    @Resource
    AfLoanSupermarketService afLoanSupermarketService;
    @Resource
    AfUnionThirdRegisterDao afUnionThirdRegisterDao;
    @RequestMapping(value = "/getRegisterLoanSupermarket", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getRegisterLoanSupermarketInfo(HttpServletRequest request, HttpServletResponse response) {
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try {
            String lsmNo = request.getParameter("lsmNo");
            if(StringUtil.isNotEmpty(lsmNo)){
                AfLoanSupermarketDo afLoanSupermarket  = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
                //Map<String, Object> data = new HashMap<String, Object>();
                //data.put("afLoanSupermarket", afLoanSupermarket);
                JSONObject data = JSONObject.parseObject(JSON.toJSONString(afLoanSupermarket));

                if(afLoanSupermarket!=null){
                    String phone = (request.getParameter("phone"));
                    if(afUnionThirdRegisterDao.getIsRegister(phone,lsmNo)>0){
                        data.put("isRegister","1");
                    }else{
                        data.put("isRegister","0");
                    }
                    resp = H5CommonResponse.getNewInstance(true, "success", "", data);
                }else{
                    resp = H5CommonResponse.getNewInstance(false, "借款超市不存在", "", "");
                }

            }else{
                resp = H5CommonResponse.getNewInstance(false, "参数为空", "", "");
            }
        } catch (Exception e) {
            resp = H5CommonResponse.getNewInstance(false, "获取数据失败", "", e.getMessage());
            logger.error("获取借贷超市数据失败" , e);
        }
        return resp.toString();
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
