package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewSwitch;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.dto.req.ResourceReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.yeepay.g3.utils.common.json.JSONUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Controller
@ResponseBody
@RequestMapping("/api/configuremodify/")
@NotNeedLogin
public class ResourceController {

    @Resource
    JsdResourceService jsdResourceService;

    @RequestMapping(value = {"getProductConfigureList.json"})
    public Resp<Map<String, Object>> getProductConfigureList(HttpServletRequest request){
        Map<String, Object> data=new HashMap<String, Object>();
        JsdResourceDo jsdResourceDo=jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),ResourceType.JSD_RATE_INFO.name());
        Map map=JSONUtils.jsonToBean(jsdResourceDo.getValue(),Map.class);
        Map seven= (Map) map.get("7");
        Map fourteen = (Map) map.get("14");
        BigDecimal base=new BigDecimal("100");
        BigDecimal sInterestRate=new BigDecimal((String) seven.get("interestRate"));
        BigDecimal sServiceRate=new BigDecimal((String) seven.get("serviceRate"));
        BigDecimal sOverdueRate=new BigDecimal((String) seven.get("overdueRate"));
        BigDecimal fInterestRate=new BigDecimal((String) fourteen.get("interestRate"));
        BigDecimal fServiceRate=new BigDecimal((String) fourteen.get("serviceRate"));
        BigDecimal fOverdueRate=new BigDecimal((String) fourteen.get("overdueRate"));
        BigDecimal defaultRate=new BigDecimal(jsdResourceDo.getValue1());
        data.put("sInterestRate",sInterestRate.multiply(base));
        data.put("sServiceRate",sServiceRate.multiply(base));
        data.put("sOverdueRate",sOverdueRate.multiply(base));
        data.put("fInterestRate",fInterestRate.multiply(base));
        data.put("fServiceRate",fServiceRate.multiply(base));
        data.put("fOverdueRate",fOverdueRate.multiply(base));
        data.put("defaultRate",defaultRate.multiply(base));
        String[] loanSection=jsdResourceDo.getValue2().split(",");
        data.put("littleAmount",loanSection[0]);
        data.put("bigAmount",loanSection[1]);
        data.put("loanTerm","7天、14天");
        data.put("id",jsdResourceDo.getRid());
        data.put("productName","极速贷");
        data.put("loanPattern","搭售砍头模式");
        data.put("payRoute","宝付");
        data.put("repaymentStyle","一次性还本付息");
        return Resp.succ(data,"");
    }

    @RequestMapping(value = {"updateProductConfigure.json"})
    public Resp<JsdResourceDo> updateProductConfigure(@RequestBody String json, HttpServletRequest request){
        Map map= JSONUtils.jsonToBean(json,Map.class);
        BigDecimal base=new BigDecimal(100);
        BigDecimal defaultRate = (new BigDecimal((String)map.get("defaultRate"))).divide(base);
        Long id=Long.parseLong((String)map.get("id"));
        String littleAmount= (String) map.get("littleAmount");
        String bigAmount= (String) map.get("bigAmount");
        String value2=littleAmount+","+bigAmount;
        JsdResourceDo jsdResourceDo=new JsdResourceDo();
        jsdResourceDo.setValue1(defaultRate.toString());
        jsdResourceDo.setRid(id);
        jsdResourceDo.setValue(json);
        jsdResourceDo.setValue2(value2);
        jsdResourceService.updateById(jsdResourceDo);
        return Resp.succ(jsdResourceDo,"");
    }

    @RequestMapping(value = {"getLoanInfo.json"})
    public Resp<Map<String, Object>> getLoanInfo(HttpServletRequest request){
        Map<String, Object> data=new HashMap<String, Object>();
        JsdResourceDo jsdResourceDo=jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(), ResourceType.JSD_CONFIG_REVIEW_MODE.name());
        if(jsdResourceDo.getValue().equals(JsdBorrowCashReviewSwitch.AUTO.name())){
            data.put("pattern","自动");
        }else if(jsdResourceDo.getValue().equals(JsdBorrowCashReviewSwitch.MANUAL.name())){
            data.put("pattern","手动");
        }else {
            data.put("pattern","兼容");
        }
        data.put("loanAmount",jsdResourceDo.getValue1());
        data.put("id",jsdResourceDo.getRid());
        return Resp.succ(data,"");
    }

    @RequestMapping(value = {"updateLoanInfo.json"})
    public Resp<JsdResourceDo> getLoanInfo(@RequestBody ResourceReq resourceReq, HttpServletRequest request){
        JsdResourceDo data=new JsdResourceDo();
        data.setValue(resourceReq.pattern);
        data.setValue1(resourceReq.loanAmount);
        data.setRid(resourceReq.id);
        jsdResourceService.updateById(data);
        return Resp.succ(data,"");
    }
}
