package com.ald.jsd.mgr.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.JsdCollectionBorrowDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.jsd.mgr.dal.dao.MgrCollectionBorrowDao;
import com.ald.jsd.mgr.dal.dao.MgrCollectionRepaymentDao;
import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;
import com.ald.jsd.mgr.enums.CollectionBorrowStatus;
import com.ald.jsd.mgr.web.dto.req.CollectionBorrowReviewReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;

/**
 * @author ZJF
 */
@Controller
@ResponseBody
@RequestMapping("/api/collection")
public class CollectionMgrController extends BaseController{
	@Resource
	JsdBorrowCashService jsdBorrowCashService;
	@Resource
    JsdCollectionBorrowDao jsdCollectionBorrowDao;
	
    @Resource
    MgrCollectionBorrowDao mgrCollectionBorrowDao;
    @Resource
    MgrCollectionRepaymentDao mgrCollectionRepaymentDao;
    
    /* --------- 借款 ------- */
    @RequestMapping(value = { "/borrow/list.json" })
    public Resp<MgrCommonQuery<MgrCollectionBorrowDto>> listBorrow(@RequestBody @Valid MgrCommonQuery<MgrCollectionBorrowDto> query){
    	query.list = mgrCollectionBorrowDao.listCollectionBorrow(query);
    	return Resp.succ(query, "");
    }
    @RequestMapping(value = { "/borrow/stats.json" })
    public Resp<Map<String, Long>> statsBorrow(){
    	Map<String, Long> data = new HashMap<String, Long>(4);
    	Date now = new Date();
    	data.put("totalAmtToday", mgrCollectionBorrowDao.countTotalAmtBetweenGmtCreate(DateUtil.getStartOfDate(now), DateUtil.getEndOfDate(now)));
    	data.put("totalWaitFinish", mgrCollectionBorrowDao.countTotalWaitFinish());
    	return Resp.succ(data, "");
    }
    @RequestMapping(value = { "/borrow/manual.json" })
    public Resp<Map<String, Long>> manualBorrow(@RequestBody @Valid CollectionBorrowReviewReq params){
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(params.tradeNoXgxy);
    	JsdCollectionBorrowDo collBorrowDo = jsdCollectionBorrowDao.selectByBorrowId(cashDo.getRid());
    	collBorrowDo.setStatus(CollectionBorrowStatus.MANUAL_FINISHED.name());
    	
    	return Resp.succ(null, "");
    }
    @RequestMapping(value = { "/borrow/review.json" })
    public Resp<Map<String, Long>> reviewBorrow(@RequestBody @Valid CollectionBorrowReviewReq params){
    	
    	return Resp.succ(null, "");
    }
    
    /* --------- 还款 ------- */
    @RequestMapping(value = { "/repayment/list.json" })
    public Resp<MgrCommonQuery<JsdCollectionRepaymentDo>> listRepayment(@RequestBody @Valid MgrCommonQuery<JsdCollectionRepaymentDo> query, HttpServletRequest request){
    	query.list = mgrCollectionRepaymentDao.listCollectionRepayment(query);
    	return Resp.succ(query, "");
    }
    @RequestMapping(value = { "/repayment/stats.json" })
    public Resp<Map<String, Long>> statsRepayment(HttpServletRequest request){
    	Map<String, Long> data = new HashMap<String, Long>(4);
    	Date now = new Date();
    	data.put("totalAmtToday", mgrCollectionRepaymentDao.countTotalAmtBetweenGmtCreate(DateUtil.getStartOfDate(now), DateUtil.getEndOfDate(now)));
    	data.put("totalWaitReview", mgrCollectionRepaymentDao.countTotalWaitReview());
    	return Resp.succ(data, "");
    }
}
