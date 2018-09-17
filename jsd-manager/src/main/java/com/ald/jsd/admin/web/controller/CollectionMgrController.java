package com.ald.jsd.admin.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.jsd.admin.web.dto.req.BaseReq;
import com.ald.jsd.admin.web.dto.resp.CollectionListBorrowRespBody;
import com.ald.jsd.admin.web.dto.resp.Resp;

/**
 * @author ZJF
 */
@Controller
@ResponseBody
@RequestMapping("/api/collection/")
public class CollectionMgrController extends BaseController{
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;

    @RequestMapping(value = { "listBorrow.json" }, method = RequestMethod.POST)
    public Resp<CollectionListBorrowRespBody> numProtocol(@RequestBody @Valid BaseReq params, HttpServletRequest request){
    	CollectionListBorrowRespBody body = new CollectionListBorrowRespBody();
    	
    	return Resp.succ(body, "");
    }
    
}
