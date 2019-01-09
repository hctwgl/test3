package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdTotalInfoService;
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

	@Override
	public BaseDao<JsdTotalInfoDo, Long> getDao() {
		return jsdTotalInfoDao;
	}

	@Override
	public void updateExtensionInfo(Date date, String term, JsdTotalInfoDo jsdTotalInfoDo) {
		// 查询展期数据
		JsdBorrowCashRenewalDo jsdBorrowCashRenewalDo = new JsdBorrowCashRenewalDo();
		if (!term.equals("all")) {
			jsdBorrowCashRenewalDo.setRenewalDay(Long.valueOf(term));
		}
		jsdBorrowCashRenewalDo.setStatus("Y");
		jsdBorrowCashRenewalDo.setQueryDate(date);
		List<JsdBorrowCashRenewalDo> list = jsdBorrowCashRenewalDao.getListByCommonCondition(jsdBorrowCashRenewalDo);

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
					j.getCapital().subtract((j.getPriorPoundage().add(j.getPriorOverdue()).add(j.getPriorInterest()))));
		}
		jsdTotalInfoDo.setExtensionCost(extensionCost);

		// 在展本金，数据太大，不适合便利
		BigDecimal inExhibitionCapital = new BigDecimal("0");
		for (JsdBorrowCashRenewalDo j : list) {
			inExhibitionCapital = inExhibitionCapital.add(j.getRenewalAmount());
		}

		BigDecimal renewalAmount = new BigDecimal("0");
		jsdBorrowCashRenewalDo = new JsdBorrowCashRenewalDo();
		if (!"all".equals(term)) {
			jsdBorrowCashRenewalDo.setRenewalDay(Long.valueOf(term));
		}
		jsdBorrowCashRenewalDo.setStatus("Y");
		jsdBorrowCashRenewalDo.setEndDate(date);
		renewalAmount = jsdBorrowCashRenewalDao.getRenewalAmount(jsdBorrowCashRenewalDo);
		jsdTotalInfoDo.setInExhibitionCapital(renewalAmount);
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
		jsdBorrowCashDo.setQueryDate(tdate);
		List<JsdBorrowCashDo> listAll = jsdBorrowCashDao.getListByCommonCondition(jsdBorrowCashDo);

		jsdBorrowCashDo.setStatus("TRANSFERRED");
		List<JsdBorrowCashDo> listY = jsdBorrowCashDao.getListByCommonCondition(jsdBorrowCashDo);
		BigDecimal rirstRate = new BigDecimal("0.00");
		if (listAll != null && !listAll.isEmpty()) {
			rirstRate = new BigDecimal(listY.size() / Double.valueOf(listAll.size()));
		}

		jsdTotalInfoDo.setRirstRate(rirstRate);

		// 逾期率
		BigDecimal overdueRate = new BigDecimal("0.00");
		jsdBorrowCashDo = new JsdBorrowCashDo();
		jsdBorrowCashDo.setEndDate(date);
		int ALL = jsdBorrowCashDao.getcount(jsdBorrowCashDo);
		jsdBorrowCashDo.setStatus("TRANSFERRED");
		int Y = jsdBorrowCashDao.getcount(jsdBorrowCashDo);
		if (ALL != 0) {
			overdueRate = new BigDecimal(Y / Double.valueOf(ALL));
		}
		jsdTotalInfoDo.setOverdueRate(overdueRate);
		// 未回收率
		BigDecimal listYBig = new BigDecimal("0.00");
		for (JsdBorrowCashDo j : listY) {
			listYBig = BigDecimalUtil
					.add(listYBig, j.getAmount(), j.getInterestAmount(), j.getPoundageAmount(), j.getOverdueAmount(),
							j.getSumRepaidInterest(), j.getSumRepaidPoundage(), j.getSumRepaidOverdue())
					.subtract(j.getRepayAmount());
		}

		BigDecimal listAllBig = new BigDecimal("0.00");
		for (JsdBorrowCashDo j : listAll) {
			listAllBig = BigDecimalUtil
					.add(listAllBig, j.getAmount(), j.getInterestAmount(), j.getPoundageAmount(), j.getOverdueAmount(),
							j.getSumRepaidInterest(), j.getSumRepaidPoundage(), j.getSumRepaidOverdue())
					.subtract(j.getRepayAmount());
		}
		if (!listAllBig.equals(new BigDecimal("0.00"))) {
			jsdTotalInfoDo.setUnrecoveredRate(listYBig.divide(listAllBig,2));
		}else{
			jsdTotalInfoDo.setUnrecoveredRate(new BigDecimal("0.00"));

		}
		// 坏账金额率
		BigDecimal result = new BigDecimal("0");
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		tdate = calendar.getTime();

		jsdBorrowCashDo = new JsdBorrowCashDo();
		jsdBorrowCashDo.setEndDate(tdate);

		// 坏账金额
		jsdBorrowCashDo.setQueryDate(date);
		BigDecimal all = jsdBorrowCashDao.getReplayAmount(jsdBorrowCashDo);
		jsdBorrowCashDo.setQueryDate(tdate);
		jsdBorrowCashDo.setStatus("TRANSFERRED");
		BigDecimal bad = jsdBorrowCashDao.getReplayAmount(jsdBorrowCashDo);
		if (all != null && !all.equals(BigDecimal.ZERO)) {
			result = bad.divide(all);
		}
		jsdTotalInfoDo.setBadDebtAmount(result);

		// 盈利率
		BigDecimal profitability = new BigDecimal("0");

		BigDecimal replay = jsdBorrowCashDao.getALLReplayAmount(jsdBorrowCashDo);
		BigDecimal arriva = jsdBorrowCashDao.getArrivalAmount(jsdBorrowCashDo);
		if (null != arriva && !BigDecimal.ZERO.equals(arriva)) {
			profitability = replay.divide(arriva);
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
				BigDecimal tyingAmount = jsdBorrowCashService.getTyingAmount(arr[i], date);
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
				this.updateFateInfo(tdate, arr[i], infoDo);
				list.add(infoDo);
			}
			if (list.size() > 0) {
				this.saveAll(list);
			}
		

	}
}
