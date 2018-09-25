package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdCollectionService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayDealBo;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.enums.JsdRepayCollectionType;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.alibaba.fastjson.JSON;

@Service("jsdCollectionService")
public class JsdCollectionServiceImpl implements JsdCollectionService{
	private final Logger logger = LoggerFactory.getLogger(JsdBorrowCashRepaymentServiceImpl.class);
	
	private final String salt = "jsdcuishou";
	
	@Resource
	JsdNoticeRecordDao jsdNoticeRecordDao;
	@Resource
	JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
	
	@Resource
	JsdNoticeRecordService jsdNoticeRecordService;
	@Resource
	CollectionSystemUtil collectionSystemUtil;
	
	public void nofityRisk(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo, 
				JsdBorrowLegalOrderRepaymentDo orderRepaymentDo,JsdRepayType type,String dataId) {
		try{
			List<HashMap<String,String>> list = new ArrayList<>();
			JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
			Long userId = 0l;
			Long borrowId = 0l;
			BigDecimal repayAmount = BigDecimal.ZERO;
			if(repaymentDo != null){
				borrowId = repaymentDo.getBorrowId();
				repayAmount = repaymentDo.getRepaymentAmount();
				userId = repaymentDo.getUserId();
				noticeRecordDo.setRefId(String.valueOf(repaymentDo.getRid()));
			}else if(orderRepaymentDo != null) {
				borrowId = orderRepaymentDo.getBorrowId();
				repayAmount = orderRepaymentDo.getRepayAmount().add(repayAmount);
				userId = orderRepaymentDo.getUserId();
				noticeRecordDo.setRefId(String.valueOf(orderRepaymentDo.getRid()));
			}
			//--------------------start  催收还款接口需要参数---------------------------

			Map<String, String> repayData = new HashMap<String, String>();
			HashMap<String, String> data = new HashMap<String, String>();
			//("还款流水")
			repayData.put("repaymentNo", repayDealBo.curOutTradeNo);
			//("还款时间")
			repayData.put("repayTime", DateUtil.formatDateTime(new Date()));
			//("订单编号")
			repayData.put("orderNo", repayDealBo.borrowNo);
			//根据场景不同，推送催收不同还款类型
			if(StringUtil.equals(type.getCode(),JsdRepayType.COLLECTION.getCode()) || StringUtil.equals(type.getCode(),JsdRepayType.REVIEW_COLLECTION.getCode())){
				repayData.put("type", JsdRepayCollectionType.APP.getCode());
				noticeRecordDo.setType(JsdNoticeType.OVERDUEREPAY.code);
				if(repaymentDo != null){
					JsdBorrowLegalOrderDo jsdBorrowLegalOrder = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(borrowId);
					dataId = String.valueOf(jsdBorrowLegalOrder.getRid());
				}else if(orderRepaymentDo != null){
					dataId = String.valueOf(repayDealBo.orderCashDo.getBorrowLegalOrderId());
				}
				data.put("dataId",dataId);//源数据id
			}else if(StringUtil.equals(type.getCode(),JsdRepayType.OFFLINE.getCode())){
				data.put("dataId",dataId);//源数据id
				repayData.put("type", JsdRepayCollectionType.OFFLINE.getCode());
				noticeRecordDo.setType(JsdNoticeType.COLLECT.code);
			}
			repayData.put("repaymentAcc", repayDealBo.userId+"");//还款账户

			data.put("amount",repayAmount+"");
			repayData.put("companyId", ConfigProperties.get(Constants.CONFKEY_COLLECTION_COMPANYID));
			repayData.put("totalAmount", repayAmount+"");
			byte[] pd = DigestUtil.digestString(repayDealBo.curOutTradeNo.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			String sign = DigestUtil.encodeHex(pd);
			repayData.put("sign",sign);
			list.add(data);
			//("还款详情, 格式: [{'dataId':'数据编号', 'amount':'还款金额(元, 精确到分)'},...]")
			repayData.put("details", JSON.toJSONString(list));
			//--------------------end  催收还款接口需要参数---------------------------

			noticeRecordDo.setUserId(userId);
			noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
			noticeRecordDo.setParams(JSON.toJSONString(repayData));
			jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
			if(collectionSystemUtil.consumerRepayment(repayData)){
				noticeRecordDo.setRid(noticeRecordDo.getRid());
				noticeRecordDo.setGmtModified(new Date());
				jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
			}
		}catch (Exception e) {
			logger.error("向催收平台同步还款信息失败", e);
		}

	}
}
