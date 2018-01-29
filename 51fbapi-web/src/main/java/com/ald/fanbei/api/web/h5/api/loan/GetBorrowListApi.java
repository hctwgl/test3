package com.ald.fanbei.api.web.h5.api.loan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanProductDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * H5借钱首页-右上角获取借钱记录
 * @author ZJF
 */
@Component("getBorrowListApi")
public class GetBorrowListApi implements H5Handle {
	public static final int PAGE_COUNT = 20;
	
	@Resource
	private AfBorrowCashDao afBorrowCashDao;
	@Resource
	private AfLoanDao afLoanDao;
	@Resource
	private AfLoanProductDao afLoanProductDao;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		
		Integer pageNo = NumberUtil.objToIntDefault(context.getData("pageNo"), 1);
		Long userId = context.getUserId();
		
		List<Object> dealingList = new ArrayList<Object>();
		List<Object> doneList = new ArrayList<Object>();
		//小借
		List<AfBorrowCashDo> doneCashs = afBorrowCashDao.listDoneCashsByUserId(userId, (pageNo - 1) * PAGE_COUNT);
		for(AfBorrowCashDo cashDo : doneCashs) {
			doneList.add(this.transformBorrowCash(cashDo));
		}
		AfBorrowCashDo dealingCash = afBorrowCashDao.getDealingCashByUserId(userId);
		if(dealingCash != null) {
			dealingList.add(this.transformBorrowCash(dealingCash));
		}
		
		//贷款
		List<AfLoanDo> doneLoans = afLoanDao.listDoneLoansByUserId(userId, (pageNo - 1) * PAGE_COUNT);
		for(AfLoanDo loanDo : doneLoans) {
			doneList.add(this.transformLoan(loanDo));
		}
		List<AfLoanDo> dealingLoans = afLoanDao.listDealingLoansByUserId(userId);
		for(AfLoanDo loanDo : dealingLoans) {
			dealingList.add(this.transformLoan(loanDo));
		}
		
		this.sort(dealingList);
		this.sort(doneList);
		
		if(doneList.size() > PAGE_COUNT) { //分页每次只显示20条
			doneList = doneList.subList(0, 20);
		}
		
		resp.addResponseData("dealingList", dealingList);
		resp.addResponseData("doneList", doneList);
		return resp;
	}
	
	private Map<String, Object> transformBorrowCash(AfBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());
		
		data.put("prdType", "BORROW_CASH");
		data.put("prdName", "小额贷");
		return data;
	}
	
	private Map<String, Object> transformLoan(AfLoanDo afLoanDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afLoanDo.getRid());
		data.put("amount", afLoanDo.getAmount());
		data.put("gmtCreate", afLoanDo.getGmtCreate());
		data.put("status", afLoanDo.getStatus());
		
		AfLoanProductDo prd = afLoanProductDao.getByPrdType(afLoanDo.getPrdType());
		data.put("prdType", prd.getPrdType());
		data.put("prdName", prd.getName());
		return data;
	}
	/**
	 * 根据申请时间降序排序
	 * @param objs
	 */
	private void sort(List<Object> objs) {
		objs.sort(new Comparator<Object>() {//根据申请时间降序排序
			public int compare(Object o1, Object o2) {
				Date o1GmtCreate = extractGmtCreate(o1);
				Date o2GmtCreate = extractGmtCreate(o2);
				if(o1GmtCreate.after(o2GmtCreate)) {
					return -1;
				}else {
					return 1;
				}
			}
		});
	}
	@SuppressWarnings("unchecked")
	private Date extractGmtCreate(Object o) {
		Map<String, Object> data = (Map<String, Object>)o;
		return (Date)data.get("gmtCreate");
	}
	
}
