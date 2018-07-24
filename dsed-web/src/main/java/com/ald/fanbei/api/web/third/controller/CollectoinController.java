package com.ald.fanbei.api.web.third.controller;


import com.ald.fanbei.api.biz.third.util.CuiShouUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        thirdLog.error("offlineRepaymentMoney error", 111);
        return cuiShouUtils.offlineRepaymentMoney(request);
    }


}
