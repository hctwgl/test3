package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.timeUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdTotalInfoDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdTotalInfoDo;
import com.google.gson.Gson;

/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2019-01-03 13:49:13 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("jsdTotalInfoService")
public class JsdTotalInfoServiceImpl extends ParentServiceImpl<JsdTotalInfoDo, Long> implements JsdTotalInfoService {
	private static final Logger logger = LoggerFactory.getLogger(JsdTotalInfoServiceImpl.class);
	@Resource
	private JsdTotalInfoDao jsdTotalInfoDao;
	@Resource
	JsdBorrowCashRenewalDao jsdBorrowCashRenewalDao;
	@Resource
	JsdBorrowCashDao jsdBorrowCashDao;
	@Resource
	JsdBorrowCashService jsdBorrowCashService;
	@Resource
	JsdResourceService jsdResourceService;
	@Resource
	JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
	@Resource
	JsdBorrowLegalOrderService jsdBorrowLegalOrderService;

	@Override
	public BaseDao<JsdTotalInfoDo, Long> getDao() {
		return jsdTotalInfoDao;
	}

	@Override
	public void updateExtensionInfo(Date date, String term, JsdTotalInfoDo jsdTotalInfoDo) {
		// 查询展期数据
		JsdBorrowCashRenewalDo jsdBorrowCashRenewalDo = new JsdBorrowCashRenewalDo();
		jsdBorrowCashRenewalDo.setStatus("Y");
		jsdBorrowCashRenewalDo.setQueryDate(date);
		jsdBorrowCashRenewalDo.setType(term);
		List<JsdBorrowCashRenewalDo> list = jsdBorrowCashRenewalDao.getListByType(jsdBorrowCashRenewalDo);

		// 展期笔数
		long extensionNum = 0L;
		if (null != list && !list.isEmpty()) {
			extensionNum = (long) list.size();
		}
		jsdTotalInfoDo.setExtensionNum(extensionNum);
		// 展期还本
		BigDecimal extensionReturnPrincipal = new BigDecimal("0");
		for (JsdBorrowCashRenewalDo j : list) {
			extensionReturnPrincipal = extensionReturnPrincipal.add(j.getCapital());
		}
		jsdTotalInfoDo.setExtensionReturnPrincipal(extensionReturnPrincipal);
		// 展期费用
		BigDecimal extensionCost = new BigDecimal("0");
		for (JsdBorrowCashRenewalDo j : list) {
			extensionCost = extensionCost.add(
					j.getActualAmount().subtract(j.getCapital().add(j.getPriorPoundage().add(j.getPriorOverdue().add(j.getPriorInterest())))));
		}
		jsdTotalInfoDo.setExtensionCost(extensionCost);

		// 在展本金，数据太大，不适合便利
		

		BigDecimal renewalAmount = new BigDecimal("0");
		jsdBorrowCashRenewalDo = new JsdBorrowCashRenewalDo();
		jsdBorrowCashRenewalDo.setStatus("Y");
		jsdBorrowCashRenewalDo.setEndDate(date);
		jsdBorrowCashRenewalDo.setType(term);
		renewalAmount = jsdBorrowCashRenewalDao.getRenewalAmount(jsdBorrowCashRenewalDo);
		if(renewalAmount !=null){
		jsdTotalInfoDo.setInExhibitionCapital(renewalAmount);
		}else{
			jsdTotalInfoDo.setInExhibitionCapital(new BigDecimal("0"));
		}
		Gson gson = new Gson();
		System.out.println("更新数据为" + gson.toJson(jsdTotalInfoDo));

	}

	public void updateFateInfo(Date date, String term, JsdTotalInfoDo jsdTotalInfoDo) {

		// 首逾率
		// 逾期一天的数据
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date tdate = calendar.getTime();

		JsdBorrowCashDo jsdBorrowCashDo = new JsdBorrowCashDo();
		if (!term.equals("all")) {
			jsdBorrowCashDo.setType(term);
		}
		jsdBorrowCashDo.setQueryDate(tdate);
		jsdBorrowCashDo.setOrstatus("1");
		List<JsdBorrowCashDo> listAll = jsdBorrowCashDao.getListByCommonCondition(jsdBorrowCashDo);
		jsdBorrowCashDo.setOrstatus(null);
		jsdBorrowCashDo.setStatus("TRANSFERRED");
		List<JsdBorrowCashDo> listY = jsdBorrowCashDao.getListByCommonCondition(jsdBorrowCashDo);
		BigDecimal rirstRate = new BigDecimal("0.00");
		if (listAll != null && !listAll.isEmpty()) {
			rirstRate = new BigDecimal(listY.size() / Double.valueOf(listAll.size())).setScale(4, BigDecimal.ROUND_DOWN);
		}

		jsdTotalInfoDo.setRirstRate(rirstRate);

		// 逾期率
		BigDecimal overdueRate = new BigDecimal("0.00");
		jsdBorrowCashDo = new JsdBorrowCashDo();
		jsdBorrowCashDo.setEndDate(tdate);
		jsdBorrowCashDo.setOrstatus("1");
		if (!term.equals("all")) {
			jsdBorrowCashDo.setType(term);
		}
		int ALL = jsdBorrowCashDao.getcount(jsdBorrowCashDo);
		jsdBorrowCashDo.setOrstatus(null);
		jsdBorrowCashDo.setStatus("TRANSFERRED");
		int Y = jsdBorrowCashDao.getcount(jsdBorrowCashDo);
		if (ALL != 0) {
			overdueRate = new BigDecimal(Y / Double.valueOf(ALL)).setScale(4, BigDecimal.ROUND_DOWN);
		}
		jsdTotalInfoDo.setOverdueRate(overdueRate);
		// 未回收率
		BigDecimal listYBig = new BigDecimal("0.00");
		
		
		 jsdBorrowCashDo = new JsdBorrowCashDo();
		 jsdBorrowCashDo.setType(term);
		jsdBorrowCashDo.setQueryDate(date);
		listYBig = jsdBorrowCashDao.getlistY(jsdBorrowCashDo);
		BigDecimal listAllBig = jsdBorrowCashDao.getlistAll(jsdBorrowCashDo);
		
		
		
		
		
		if ( listYBig!=null&& null!=listAllBig&&!listAllBig.equals(new BigDecimal("0.00"))) {
			jsdTotalInfoDo.setUnrecoveredRate(listYBig.divide(listAllBig,4,BigDecimal.ROUND_DOWN));
		}else{
			jsdTotalInfoDo.setUnrecoveredRate(new BigDecimal("0.00"));

		}
		// 坏账金额率
		BigDecimal result = new BigDecimal("0");
		calendar.add(Calendar.DAY_OF_MONTH, -31);
		tdate = calendar.getTime();

		jsdBorrowCashDo = new JsdBorrowCashDo();
		jsdBorrowCashDo.setEndDate(tdate);
		if (!term.equals("all")) {
			jsdBorrowCashDo.setType(term);
		}
		// 坏账金额
		jsdBorrowCashDo.setOrstatus("1");
		jsdBorrowCashDo.setEndDate(date);
		BigDecimal all = jsdBorrowCashDao.getReplayAllAmount(jsdBorrowCashDo);
		jsdBorrowCashDo.setEndDate(tdate);
		jsdBorrowCashDo.setOrstatus(null);
		jsdBorrowCashDo.setStatus("TRANSFERRED");
		BigDecimal bad = jsdBorrowCashDao.getReplayAmount(jsdBorrowCashDo);
		if (bad!=null&&all != null && !all.equals(BigDecimal.ZERO)) {
			result = bad.divide(all,4,BigDecimal.ROUND_DOWN);
		}
		jsdTotalInfoDo.setBadDebtAmount(result);

		// 盈利率
		BigDecimal profitability = new BigDecimal("0");
		
		JsdBorrowCashRenewalDo jsdBorrowCashRenewalDo=new JsdBorrowCashRenewalDo();
		if (!term.equals("all")) {
			jsdBorrowCashRenewalDo.setType(term);
		}
		jsdBorrowCashRenewalDo.setStatus("Y");
		jsdBorrowCashRenewalDo.setEndDate(date);
		
		jsdBorrowCashDo = new JsdBorrowCashDo();
		if (!term.equals("all")) {
			jsdBorrowCashDo.setType(term);
		}
		jsdBorrowCashDo.setEndDate(date);
		
		
		BigDecimal replay = jsdBorrowCashRenewalDao.getALLReplayAmount(jsdBorrowCashRenewalDo);
		jsdBorrowCashDo.setUnstatus("1");
		BigDecimal arriva = jsdBorrowCashDao.getArrivalAmount(jsdBorrowCashDo);
		if (null!=replay&&null != arriva && !BigDecimal.ZERO.equals(arriva)) {
			profitability = (replay.subtract(arriva)).divide(arriva,4,BigDecimal.ROUND_DOWN);
		}
		jsdTotalInfoDo.setProfitabilityRate(profitability);
		Gson gson = new Gson();
		System.out.println("更新数据为" + gson.toJson(jsdTotalInfoDo));

	}

	public int saveAll(List<JsdTotalInfoDo> list) {
		return jsdTotalInfoDao.saveAll(list);
	}

	@Override
	public void updateTotalInfo(Date tdate,String date,JsdResourceDo resourceDo) {

			List<JsdTotalInfoDo> list = new ArrayList<>();
			String[] arr = resourceDo.getTypeDesc().split(",");
			for (int i = 0; arr.length > i; i++) {

				JsdTotalInfoDo infoDo = new JsdTotalInfoDo();
				infoDo.setCountDate(tdate);
				infoDo.setNper(arr[i]);
				// 放款笔数
				Integer loanNum = jsdBorrowCashService.getLoanNum(arr[i], date);
				infoDo.setLoanNum(null == loanNum ? 0l : loanNum.longValue());
				// 借款申请金额
				BigDecimal appleAmount = jsdBorrowCashService.getAppleAmount(arr[i], date);
				infoDo.setApplyAmount(appleAmount == null ? BigDecimal.ZERO : appleAmount);
				// 实际出款金额
				BigDecimal loanAmount = jsdBorrowCashService.getLoanAmount(arr[i], date);
				infoDo.setLoanAmount(loanAmount == null ? BigDecimal.ZERO : loanAmount);
				// 商品搭售金额
				BigDecimal tyingAmount = jsdBorrowLegalOrderService.getTyingAmount(arr[i], date);
				infoDo.setTyingAmount(tyingAmount == null ? BigDecimal.ZERO : tyingAmount);
				// 应还款金额
				BigDecimal repaymentAmount = jsdBorrowCashService.getRepaymentAmount(arr[i], date);
				infoDo.setRepaymentAmount(repaymentAmount == null ? BigDecimal.ZERO : repaymentAmount);
				// 正常还款金额
				BigDecimal normalAmount = jsdBorrowCashService.getNormalAmount(arr[i], date);
				infoDo.setNormalAmount(normalAmount == null ? BigDecimal.ZERO : normalAmount);
				// 总还款金额
				BigDecimal sumRepaymentAmount = jsdBorrowCashRepaymentService.getSumRepaymentAmount(arr[i], date);
				infoDo.setCountRepaymentAmount(sumRepaymentAmount == null ? BigDecimal.ZERO : sumRepaymentAmount);
				// 应还款笔数
				Integer repaymentNum = jsdBorrowCashService.getRepaymentNum(arr[i], date);
				infoDo.setRepaymentNum(repaymentNum == null ? 0l : repaymentNum.longValue());
				// 正常还款笔数
				Integer normalNum = jsdBorrowCashService.getNormalNum(arr[i], date);
				infoDo.setNormalNum(normalNum == null ? 0l : normalNum.longValue());
				// 总还款笔数
				Integer sumRepaymentNum = jsdBorrowCashService.getSumRepaymentNum(arr[i], date);
				infoDo.setCountRepaymentNum(sumRepaymentNum == null ? 0l : sumRepaymentNum.longValue());
				// 展期笔数、展期还本、展期费用、在展本金
				this.updateExtensionInfo(tdate, arr[i], infoDo);
				// 首逾率、逾期率、未回收率、坏账金额、盈利率
				this.updateFateInfo(tdate, "all", infoDo);
				list.add(infoDo);
			}
			if (list.size() > 0) {
				this.saveAll(list);
			}
		

	}
	
	
}
