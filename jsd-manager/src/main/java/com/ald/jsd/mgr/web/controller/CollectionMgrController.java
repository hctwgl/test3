package com.ald.jsd.mgr.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.dal.dao.JsdCollectionBorrowDao;
import com.ald.fanbei.api.dal.dao.JsdCollectionRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
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
    @Resource
    JsdCollectionRepaymentDao jsdCollectionRepaymentDao;
    
    @RequestMapping(value = { "/borrow/list.json" })
    public Resp<MgrCommonQuery<MgrCollectionBorrowDto>> listBorrow(@RequestBody @Valid MgrCommonQuery<MgrCollectionBorrowDto> query, HttpServletRequest request){
    	query.list = jsdCollectionBorrowDao.mgrListCollectionBorrow(query);
    	return Resp.succ(query, "");
    }
    
    @RequestMapping(value = { "/repayment/list.json" })
    public Resp<MgrCommonQuery<JsdCollectionRepaymentDo>> listRepayment(@RequestBody @Valid MgrCommonQuery<JsdCollectionRepaymentDo> query, HttpServletRequest request){
    	query.list = jsdCollectionRepaymentDao.mgrListCollectionRepayment(query);
    	return Resp.succ(query, "");
    }
    
}
