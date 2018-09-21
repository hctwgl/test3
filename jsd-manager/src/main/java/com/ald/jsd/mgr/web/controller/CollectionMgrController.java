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
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdCollectionBorrowService;
import com.ald.fanbei.api.biz.service.JsdCollectionRepaymentService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.CuiShouUtils;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.jsd.mgr.biz.service.MgrOfflineRepaymentService;
import com.ald.jsd.mgr.dal.dao.MgrCollectionBorrowDao;
import com.ald.jsd.mgr.dal.dao.MgrCollectionRepaymentDao;
import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;
import com.ald.jsd.mgr.enums.CollectionBorrowStatus;
import com.ald.jsd.mgr.enums.CommonReviewStatus;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.req.CollectionBorrowManualReq;
import com.ald.jsd.mgr.web.dto.req.CollectionBorrowReviewReq;
import com.ald.jsd.mgr.web.dto.req.CollectionRepaymentDetailReq;
import com.ald.jsd.mgr.web.dto.req.CollectionRepaymentReviewReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;

/**
 * @author ZJF
 */
@Controller
@ResponseBody
@RequestMapping("/api/collection")
public class CollectionMgrController extends BaseController{
	@Resource
	JsdUserService jsdUserService;
	@Resource
	JsdBorrowCashService jsdBorrowCashService;
	@Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;
	@Resource
    JsdCollectionRepaymentService jsdCollectionRepaymentService;
	@Resource
	JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
	
	@Resource
    MgrOfflineRepaymentService mgrOfflineRepaymentService;
	
    @Resource
    MgrCollectionBorrowDao mgrCollectionBorrowDao;
    @Resource
    MgrCollectionRepaymentDao mgrCollectionRepaymentDao;
    
    @Resource
    CuiShouUtils cuiShouUtils;
    
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
    	String reviewRemark = "管理员【" + operator + "】操作强制结清，原因：" + params.reviewRemark;
    	
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(params.tradeNoXgxy);
    	JsdBorrowLegalOrderDo legalOrderDo = jsdBorrowLegalOrderService.getLastOrderByBorrowId(cashDo.getRid());
    	JsdCollectionBorrowDo collBorrowDo = jsdCollectionBorrowService.selectByBorrowId(cashDo.getRid());
    	
    	if(CollectionBorrowStatus.WAIT_FINISH.name().equals(collBorrowDo.getStatus())) {
    		return Resp.fail("当笔借款为待审核平账中，不可强制结清，只可审核");
    	}
    	
    	JsdCollectionBorrowDo collBorrowDoForMod = this.buildCollectionBorrowDoForMod(collBorrowDo.getRid(),
    			CollectionBorrowStatus.MANUAL_FINISHED.name(), operator, CommonReviewStatus.PASS.name(), reviewRemark);
    	
    	JsdBorrowCashDo cashDoForMod = new JsdBorrowCashDo();
    	cashDoForMod.setRid(cashDo.getRid());
    	cashDoForMod.setStatus(JsdBorrowCashStatus.FINISHED.name());
    	cashDoForMod.setRemark(reviewRemark);
    	
    	transactionTemplate.execute(new TransactionCallback<Integer>() {
			public Integer doInTransaction(TransactionStatus status) {
				jsdBorrowCashService.updateById(cashDoForMod);
				jsdCollectionBorrowService.updateById(collBorrowDoForMod);
				cuiShouUtils.collectImport(legalOrderDo.getRid().toString());
				return 1;
			}
		});
    	
    	return Resp.succ();
    }
    @RequestMapping(value = { "/borrow/review.json" })
    public Resp<Map<String, Long>> reviewBorrow(@RequestBody @Valid CollectionBorrowReviewReq params, HttpServletRequest request){
    	String operator = Sessions.getRealname(request);
    	
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(params.tradeNoXgxy);
    	JsdBorrowLegalOrderDo legalOrderDo = jsdBorrowLegalOrderService.getLastOrderByBorrowId(cashDo.getRid());
    	JsdCollectionBorrowDo collBorrowDo = jsdCollectionBorrowService.selectByBorrowId(cashDo.getRid());
    	
    	String reviewRemark = "管理员【" + operator + "】审核催收员【" + collBorrowDo.getRequester() + "】平账请求（平账原因：" + collBorrowDo.getRequestReason() + "），审核结果:" + params.reviewRemark;
    	
    	if(!CollectionBorrowStatus.WAIT_FINISH.name().equals(collBorrowDo.getStatus())) {
    		return Resp.fail("当笔借款非 待审核平账 状态，不可操作");
    	}
    	
    	JsdCollectionBorrowDo collBorrowDoForMod;
    	
    	if(CommonReviewStatus.REFUSE.name().equals(params.reviewStatus)) {
    		collBorrowDoForMod = this.buildCollectionBorrowDoForMod(collBorrowDo.getRid(), CollectionBorrowStatus.NOTICED.name(), 
    				operator, params.reviewStatus, reviewRemark);
    		jsdCollectionBorrowService.updateById(collBorrowDoForMod);
    	}else if(CommonReviewStatus.PASS.name().equals(params.reviewStatus)) {
    		collBorrowDoForMod = this.buildCollectionBorrowDoForMod(collBorrowDo.getRid(), CollectionBorrowStatus.COLLECT_FINISHED.name(), 
    				operator, params.reviewStatus, reviewRemark);
    		
    		JsdBorrowCashDo cashDoForMod = new JsdBorrowCashDo();
    		cashDoForMod.setRid(cashDo.getRid());
        	cashDoForMod.setStatus(JsdBorrowCashStatus.FINISHED.name());
        	cashDoForMod.setRemark(reviewRemark);
        	
        	transactionTemplate.execute(new TransactionCallback<Integer>() {
    			public Integer doInTransaction(TransactionStatus status) {
    				jsdBorrowCashService.updateById(cashDoForMod);
    				jsdCollectionBorrowService.updateById(collBorrowDoForMod);
    				cuiShouUtils.collectImport(legalOrderDo.getRid().toString());
    				return 1;
    			}
    		});
    	}
    	
    	return Resp.succ();
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
    @RequestMapping(value = { "/repayment/detail.json" })
    public Resp<Map<String, Object>> detailRepayment(@RequestBody @Valid CollectionRepaymentDetailReq params, HttpServletRequest request){
    	JsdCollectionRepaymentDo collRepayDo = jsdCollectionRepaymentService.getByRepayNo(params.tradeNo);
    	JsdUserDo userDo = jsdUserService.getById(collRepayDo.getUserId());
    	
    	Map<String, String> userInfo = new HashMap<>();
    	userInfo.put("realName", userDo.getRealName());
    	userInfo.put("account", userDo.getUserName());
    	userInfo.put("idNumber", userDo.getIdNumber());
    	
    	Map<String, Object> reviewInfo = new HashMap<>();
    	reviewInfo.put("reviewer", collRepayDo.getReviewer());
    	reviewInfo.put("reviewStatus", collRepayDo.getReviewStatus());
    	reviewInfo.put("reviewRemark", collRepayDo.getReviewRemark());
    	reviewInfo.put("gmtReview", collRepayDo.getGmtModified() != null? collRepayDo.getGmtModified().getTime():null);
    	
    	Map<String, Object> data = new HashMap<>();
    	data.put("userInfo", userInfo);
    	data.put("repayCert", collRepayDo.getRepayCert());
    	data.put("reviewInfo", reviewInfo);
    	
    	return Resp.succ(data, "");
    }
    @RequestMapping(value = { "/repayment/review.json" })
    public Resp<?> reviewRepayment(@RequestBody @Valid CollectionRepaymentReviewReq params, HttpServletRequest request){
    	JsdCollectionRepaymentDo collRepayDo = jsdCollectionRepaymentService.getByRepayNo(params.tradeNo);
    	JsdBorrowCashDo cashDo = jsdBorrowCashService.getById(collRepayDo.getBorrowId());
    	
    	Map<String, String> offlineData = new HashMap<>(8, 1);
    	
    	offlineData.put("borrowNo", cashDo.getBorrowNo());
        offlineData.put("repaymentDate", collRepayDo.getGmtRepay().getTime() + "");
        offlineData.put("channel", collRepayDo.getRepayWay());
        offlineData.put("tradeNo", collRepayDo.getTradeNo());
        offlineData.put("amount", collRepayDo.getRepayAmount().toString());
        offlineData.put("remark", collRepayDo.getReviewRemark());
        
        // TODO 修改collRepayDo 
        transactionTemplate.execute(new TransactionCallback<Integer>() {
			public Integer doInTransaction(TransactionStatus status) {
				mgrOfflineRepaymentService.dealOfflineRepayment(offlineData);
				return 1;
			}
		});
    	return Resp.succ();
    }
    
    private JsdCollectionBorrowDo buildCollectionBorrowDoForMod(Long id, String status, String reviewer, String reviewStatus, String reviewRemark) {
    	JsdCollectionBorrowDo collBorrowDoForMod = new JsdCollectionBorrowDo();
    	collBorrowDoForMod.setRid(id);
		collBorrowDoForMod.setStatus(status);
		collBorrowDoForMod.setReviewer(reviewer);
		collBorrowDoForMod.setReviewStatus(reviewStatus);
		collBorrowDoForMod.setReviewRemark(reviewRemark);
		return collBorrowDoForMod;
    }
}
