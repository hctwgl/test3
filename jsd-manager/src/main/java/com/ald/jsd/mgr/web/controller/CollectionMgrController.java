package com.ald.jsd.mgr.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.jsd.mgr.dal.dao.MgrOperatorDao;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;
import com.ald.jsd.mgr.web.dto.req.CollectionListBorrowReq;
import com.ald.jsd.mgr.web.dto.resp.CollectionListBorrowRespBody;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;

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
    
    @Resource
    MgrOperatorDao mgrOperatorDao;

    @RequestMapping(value = { "listBorrow.json" })
    public Resp<CollectionListBorrowRespBody> numProtocol(@RequestBody @Valid CollectionListBorrowReq params, HttpServletRequest request){
    	CollectionListBorrowRespBody body = new CollectionListBorrowRespBody();
    	
    	MgrOperatorDo operator = mgrOperatorDao.getById(1L);
    	JsdUserDo userDo = jsdUserService.getById(4L);
    	System.out.println(JSON.toJSONString(userDo));
    	
    	return Resp.succ(body, "");
    }
    
}
