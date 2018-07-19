package com.ald.fanbei.api.web.third.controller;


import com.ald.fanbei.api.biz.bo.dsed.DsedParam;
import com.ald.fanbei.api.biz.third.cuishou.CuiShouUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/third/collection/")
public class CollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;

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


}
