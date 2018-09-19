package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.jsd.mgr.spring.NotNeedLogin;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.yeepay.g3.utils.common.json.JSONUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/configureModify/")
@NotNeedLogin
public class ResourceController {

    @Resource
    JsdResourceService jsdResourceService;

    @RequestMapping(value = {"getProductConfigureList.json"})
    public Resp<Map<String, Object>> getProductConfigureList(){
        Map<String, Object> data=new HashMap<String, Object>();
        JsdResourceDo jsdResourceDo=jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),ResourceType.JSD_RATE_INFO.name());
        Map map=JSONUtils.jsonToBean(jsdResourceDo.getValue(),Map.class);
        Map seven= (Map) map.get("7");
        Map fourteen = (Map) map.get("14");
        int sInterestRate=NumberUtil.objToIntDefault((String) seven.get("interestRate"),0)*100;
        int sServiceRate= NumberUtil.objToIntDefault((String) seven.get("serviceRate"),0)*100;
        int sOverdueRate=NumberUtil.objToIntDefault((String) seven.get("overdueRate"),0)*100;
        int fInterestRate=NumberUtil.objToIntDefault((String) fourteen.get("interestRate"),0)*100;
        int fServiceRate=NumberUtil.objToIntDefault((String) fourteen.get("serviceRate"),0)*100;
        int fOverdueRate=NumberUtil.objToIntDefault((String) fourteen.get("overdueRate"),0)*100;
        data.put("sInterestRate",sInterestRate);
        data.put("sServiceRate",sServiceRate);
        data.put("sOverdueRate",sOverdueRate);
        data.put("fInterestRate",fInterestRate);
        data.put("fServiceRate",fServiceRate);
        data.put("fOverdueRate",fOverdueRate);
        data.put("defaultRate",jsdResourceDo.getValue1());
        data.put("loanSection",jsdResourceDo.getValue2());
        return Resp.succ(data,"");
    }
}
