package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.alibaba.fastjson.JSON;


/**
 * ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-28 15:43:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdNoticeRecordService")
public class JsdNoticeRecordServiceImpl extends ParentServiceImpl<JsdNoticeRecordDo, Long> implements JsdNoticeRecordService {
   
    @Resource
    private JsdNoticeRecordDao jsdNoticeRecordDao;

    @Resource
	private XgxyUtil xgxyUtil;

		@Override
	public BaseDao<JsdNoticeRecordDo, Long> getDao() {
		return jsdNoticeRecordDao;
	}

	@Override
	public int updateNoticeRecordStatus(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao. updateNoticeRecordStatus(noticeRecordDo);
	}

	@Override
	public int updateNoticeRecordTimes(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao.updateNoticeRecordTimes( noticeRecordDo);
	}

	@Override
	public int addNoticeRecord(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
	}

	@Override
	public List<JsdNoticeRecordDo> getAllFailNoticeRecord() {
		return jsdNoticeRecordDao.getAllFailNoticeRecord();
	}

	@Override
	public void dealBorrowNoticed(JsdBorrowCashDo cashDo, XgxyBorrowNoticeBo noticeBo) {
		JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
        noticeRecordDo.setUserId(cashDo.getUserId());
        noticeRecordDo.setRefId(String.valueOf(cashDo.getRid()));
        noticeRecordDo.setType(JsdNoticeType.DELEGATEPAY.code);
        noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
        noticeRecordDo.setParams(JSON.toJSONString(noticeBo));
		jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
		if(xgxyUtil.borrowNoticeRequest(noticeBo)){
			jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void dealRepayNoticed(JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, HashMap data){
		JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
		noticeRecordDo.setUserId(repaymentDo!=null?repaymentDo.getUserId():orderRepaymentDo.getUserId());
		noticeRecordDo.setRefId(String.valueOf(repaymentDo!=null?repaymentDo.getRid():orderRepaymentDo.getRid()));
		noticeRecordDo.setType(JsdNoticeType.REPAY.code);
		noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
		noticeRecordDo.setParams(JSON.toJSONString(data));
		jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
		if (xgxyUtil.repayNoticeRequest(data)) {
			jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
		}
	}

}