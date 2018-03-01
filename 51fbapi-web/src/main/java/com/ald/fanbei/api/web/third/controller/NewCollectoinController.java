package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/third/newcollection/")
public class NewCollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;

    @ResponseBody
    @RequestMapping("offLineBackMoney")
    public String offLineBackMoney(String sign,String data){
        return cuiShouUtils.offlineRepaymentMoney(sign,data);
    }
}
