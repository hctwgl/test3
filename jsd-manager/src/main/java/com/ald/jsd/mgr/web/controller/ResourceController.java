package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewSwitch;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.jsd.mgr.dal.dao.MgrOperateLogDao;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.req.ResourceReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.ObjectUtils;
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
public class ResourceController {

    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    private MgrOperateLogDao mgrOperateLogDao;

    @RequestMapping(value = {"getProductConfigureList.json"})
    public Resp<Map<String, Object>> getProductConfigureList(HttpServletRequest request){
        Map<String, Object> data=new HashMap<String, Object>();
        JsdResourceDo jsdResourceDo=jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),ResourceType.JSD_RATE_INFO.name());
        Map map= JSON.parseObject(jsdResourceDo.getValue(),Map.class);
        Set<Map.Entry> keys=map.entrySet();
        List<String> list=new ArrayList<>();
        for (Map.Entry key : keys) {
            String loanTerm=key.getKey().toString();
            list.add(loanTerm);
        }
        String bigDay="";
        String smallDay="";
        if(Integer.parseInt(list.get(0))>Integer.parseInt(list.get(1))){
            bigDay=list.get(0);
            smallDay=list.get(1);
        }else{
            bigDay=list.get(1);
            smallDay=list.get(0);
        }
        Map seven= (Map) map.get(smallDay);
        Map fourteen = (Map) map.get(bigDay);
        BigDecimal base=new BigDecimal("100");
        BigDecimal sInterestRate= (BigDecimal)seven.get("interestRate");
        BigDecimal sServiceRate=(BigDecimal)(seven.get("serviceRate"));
        BigDecimal sOverdueRate=(BigDecimal)(seven.get("overdueRate"));
        BigDecimal fInterestRate=(BigDecimal)(fourteen.get("interestRate"));
        BigDecimal fServiceRate=(BigDecimal)(fourteen.get("serviceRate"));
        BigDecimal fOverdueRate=(BigDecimal)(fourteen.get("overdueRate"));
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
        data.put("minLoanTerm",smallDay);
        data.put("maxLoanTerm",bigDay);
        data.put("id",jsdResourceDo.getRid());
        data.put("productName","极速贷");
        data.put("loanPattern","搭售砍头模式");
        data.put("payRoute","宝付");
        data.put("repaymentStyle","一次性还本付息");
        return Resp.succ(data,"");
    }

    @RequestMapping(value = {"updateProductConfigure.json"})
    public Resp<JsdResourceDo> updateProductConfigure(@RequestBody String json, HttpServletRequest request){
        Map map= JSON.parseObject(json,Map.class);
        BigDecimal base=new BigDecimal(100);
        BigDecimal defaultRate = (new BigDecimal((String)map.get("defaultRate"))).divide(base);
        Long id=Long.parseLong((String)map.get("id"));
        Set<Map.Entry> keys=map.entrySet();
        List<String> list=new ArrayList<>();
        for (Map.Entry key : keys) {
            String loanTerm=key.getKey().toString();
            try {
                int num=Integer.valueOf(loanTerm);
                list.add(String.valueOf(num));
            }catch (Exception e){
                continue;
            }
        }
        String bigDay="";
        String smallDay="";
        if(Integer.parseInt(list.get(0))>Integer.parseInt(list.get(1))){
            bigDay=list.get(0);
            smallDay=list.get(1);
        }else{
            bigDay=list.get(1);
            smallDay=list.get(0);
        }
        Map seven= (Map) map.get(smallDay);
        Map fourteen = (Map) map.get(bigDay);
        //获取数据
        BigDecimal sInterestRate=new BigDecimal((String) seven.get("sInterestRate"));
        BigDecimal sServiceRate=new BigDecimal((String) seven.get("sServiceRate"));
        BigDecimal sOverdueRate=new BigDecimal((String) seven.get("sOverdueRate"));
        BigDecimal fInterestRate=new BigDecimal((String) fourteen.get("fInterestRate"));
        BigDecimal fServiceRate=new BigDecimal((String) fourteen.get("fServiceRate"));
        BigDecimal fOverdueRate=new BigDecimal((String) fourteen.get("fOverdueRate"));
        //移除以前的键，不改变数据库的键
        seven.put("interestRate",seven.remove("sInterestRate"));
        seven.put("serviceRate",seven.remove("sServiceRate"));
        seven.put("overdueRate",seven.remove("sOverdueRate"));
        fourteen.put("interestRate",fourteen.remove("fInterestRate"));
        fourteen.put("serviceRate",fourteen.remove("fServiceRate"));
        fourteen.put("overdueRate",fourteen.remove("fOverdueRate"));
        seven.put("interestRate",sInterestRate.divide(base));
        seven.put("serviceRate",sServiceRate.divide(base));
        seven.put("overdueRate",sOverdueRate.divide(base));
        fourteen.put("interestRate",fInterestRate.divide(base));
        fourteen.put("serviceRate",fServiceRate.divide(base));
        fourteen.put("overdueRate",fOverdueRate.divide(base));
        //放入值
        Map loanTerm = new HashMap();
        loanTerm.put(smallDay, seven);
        loanTerm.put(bigDay, fourteen);
        String value=JSON.toJSONString(loanTerm);
        String littleAmount= (String) map.get("littleAmount");
        String bigAmount= (String) map.get("bigAmount");
        String value2=littleAmount+","+bigAmount;
        JsdResourceDo jsdResourceDo=new JsdResourceDo();
        jsdResourceDo.setRid(id);
        jsdResourceDo.setValue(value);
        jsdResourceDo.setValue1(defaultRate.toString());
        jsdResourceDo.setValue2(value2);
        jsdResourceDo.setGmtModified(new Date());
        jsdResourceService.updateById(jsdResourceDo);
        mgrOperateLogDao.addOperateLog(Sessions.getRealname(request),"产品配置:"+json);
        return Resp.succ(jsdResourceDo,"");
    }

    @RequestMapping(value = {"getLoanInfo.json"})
    public Resp<Map<String, Object>> getLoanInfo(HttpServletRequest request){
        Map<String, Object> data=new HashMap<String, Object>();
        JsdResourceDo jsdResourceDo=jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(), ResourceType.JSD_CONFIG_REVIEW_MODE.name());
        if(jsdResourceDo.getValue().equals(JsdBorrowCashReviewSwitch.AUTO.name())){
            data.put("pattern","AUTO");
        }else if(jsdResourceDo.getValue().equals(JsdBorrowCashReviewSwitch.MANUAL.name())){
            data.put("pattern","MANUAL");
        }else {
            data.put("pattern","SEMI_AUTO");
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
        data.setGmtModified(new Date());
        jsdResourceService.updateById(data);
        mgrOperateLogDao.addOperateLog(Sessions.getRealname(request),"设置："+JSON.toJSONString(resourceReq));
        return Resp.succ(data,"");
    }
}
