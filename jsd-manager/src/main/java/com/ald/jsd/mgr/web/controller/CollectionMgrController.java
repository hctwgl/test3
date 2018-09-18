package com.ald.jsd.mgr.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.dal.dao.JsdCollectionBorrowDao;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;

/**
 * @author ZJF
 */
@Controller
@ResponseBody
@RequestMapping("/api/collection")
public class CollectionMgrController extends BaseController{
    @Resource
    JsdCollectionBorrowDao jsdCollectionBorrowDao;

    @RequestMapping(value = { "/listBorrow.json" })
    public Resp<MgrCommonQuery> numProtocol(@RequestBody @Valid MgrCommonQuery query, HttpServletRequest request){
    	
    	return Resp.succ(null, "");
    }
    
}
