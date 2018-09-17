package com.ald.fanbei.api.web.controller;


import com.ald.fanbei.api.biz.third.util.CuiShouUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/third/collection/")
public class CollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;

    protected static final Logger thirdLog = LoggerFactory.getLogger("DSED_THIRD");
    /**
     * 线下还款
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/offLineBackMoney"}, method = RequestMethod.POST)
    public String offLineBackMoney(HttpServletRequest request){
        return cuiShouUtils.offlineRepaymentMoney(request);
    }


    @ResponseBody
    @RequestMapping(value = {"/collectImport"}, method = RequestMethod.POST)
    public String collectImport(String data){
        return cuiShouUtils.collectImport(data);
    }

}
