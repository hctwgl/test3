package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.query.JsdUserAuthQuery;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @类描述：认证管理
 *
 * @author yinxiangyu
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/api/auth")
public class JsdUserAuthController {

    @Resource
    JsdUserAuthService jsdUserAuthService;

    @RequestMapping("/getJsdUserAuthInfo")
    public String getJsdUserAuthInfo(@RequestBody JSONObject data, HttpServletRequest request,ModelMap model,JsdUserAuthQuery query){
        String riskStatus = data.getString("riskStatus");
        String searchContent = data.getString("searchContent");
        int pageIndex = data.getInteger("pageIndex");
        int pageSize = data.getInteger("pageSize");
        JsdUserAuthQuery jsdUserAuthQuery=new JsdUserAuthQuery();
        jsdUserAuthQuery.setPageIndex(pageIndex);
        jsdUserAuthQuery.setPageSize(pageSize);
        jsdUserAuthQuery.setRiskStatus(riskStatus);
        jsdUserAuthQuery.setSearchContent(searchContent);
        List<JsdUserAuthDo> list=jsdUserAuthService.getListJsdUserAuth(query);
        return null;
    }
}
