package com.ald.jsd.mgr.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdCollectionBorrowService;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.jsd.mgr.dal.dao.MgrCollectionBorrowDao;
import com.ald.jsd.mgr.dal.dao.MgrCollectionRepaymentDao;
import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;
import com.ald.jsd.mgr.enums.CollectionBorrowStatus;
import com.ald.jsd.mgr.enums.CommonReviewStatus;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.req.CollectionBorrowManualReq;
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
    JsdCollectionBorrowService jsdCollectionBorrowService;
	
    @Resource
    MgrCollectionBorrowDao mgrCollectionBorrowDao;
    @Resource
    MgrCollectionRepaymentDao mgrCollectionRepaymentDao;
    
    @Resource
    TransactionTemplate transactionTemplate;
    
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
    public Resp<Map<String, Long>> manualBorrow(@RequestBody @Valid CollectionBorrowManualReq params, HttpServletRequest request){
    	String operator = Sessions.getRealname(request);
    	String reviewRemark = "管理员" + operator + "操作强制结清，原因：" + params.reviewRemark;
    	
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(params.tradeNoXgxy);
    	JsdCollectionBorrowDo collBorrowDo = jsdCollectionBorrowService.selectByBorrowId(cashDo.getRid());
    	
    	if(CollectionBorrowStatus.WAIT_FINISH.name().equals(collBorrowDo.getStatus())) {
    		return Resp.fail("当笔借款为待审核平账中，不可强制结清，只可审核");
    	}
    	
    	JsdBorrowCashDo cashDoForMod = new JsdBorrowCashDo();
    	JsdCollectionBorrowDo collBorrowDoForMod = new JsdCollectionBorrowDo();
    	
    	collBorrowDoForMod.setRid(collBorrowDo.getRid());
    	collBorrowDoForMod.setStatus(CollectionBorrowStatus.MANUAL_FINISHED.name());
    	collBorrowDoForMod.setReviewer(operator);
    	collBorrowDoForMod.setReviewStatus(CommonReviewStatus.PASS.name());
    	collBorrowDoForMod.setReviewRemark(reviewRemark);
    	
    	cashDoForMod.setRid(cashDo.getRid());
    	cashDoForMod.setStatus(JsdBorrowCashStatus.FINISHED.name());
    	cashDoForMod.setRemark(reviewRemark);
    	
    	transactionTemplate.execute(new TransactionCallback<Integer>() {
			public Integer doInTransaction(TransactionStatus status) {
				jsdBorrowCashService.updateById(cashDoForMod);
				jsdCollectionBorrowService.updateById(collBorrowDoForMod);
				return 1;
			}
		});
    	
    	return Resp.succ();
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
