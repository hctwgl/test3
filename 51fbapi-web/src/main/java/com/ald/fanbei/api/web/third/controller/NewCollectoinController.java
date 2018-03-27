package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.github.tomakehurst.wiremock.common.Json;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/third/newcollection/")
public class NewCollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;

    /**
     * 线下还款
     * @param sign
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping("offLineBackMoney")
    public String offLineBackMoney(String sign,String data){
        return cuiShouUtils.offlineRepaymentMoney(sign,data);
    }

    /**
     * 平帐
     * @param data
     * @param sign
     * @return
     */
    @ResponseBody
    @RequestMapping("finishBorrow")
    public String finishBorrow(String data,String sign){
        return cuiShouUtils.finishBorrow(data,sign);
    }


    @Resource
    AfBorrowBillService afBorrowBillService;

    @ResponseBody
    @RequestMapping("geBillAllClear")
    public HashMap geBillAllClear(Long userId,Long billId){
        HashMap resulitMap = new HashMap();
        List<AllBarlyClearanceBo> list = afBorrowBillService.getAllClear(userId,billId);

        resulitMap.put("result",list);
        return resulitMap;
//        return JSON.toJSONString(resulitMap);
    }
}
