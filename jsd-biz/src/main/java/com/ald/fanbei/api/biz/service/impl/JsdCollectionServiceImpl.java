package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdCollectionService;
import com.ald.fanbei.api.biz.third.util.CollectionNoticeUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.enums.JsdRepayCollectionType;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.alibaba.fastjson.JSON;

@Service("jsdCollectionService")
public class JsdCollectionServiceImpl implements JsdCollectionService{
	private final String salt = "jsdcuishou";
	
	@Resource
	JsdNoticeRecordDao jsdNoticeRecordDao;
	@Resource
	CollectionNoticeUtil collectionNoticeUtil;
	
	public void nofityRepayment(BigDecimal repayAmount, String curOutTradeNo, String borrowNo, Long orderId, Long uid, JsdRepayType repayType, String reviewReuslt) {
		List<HashMap<String,String>> list = new ArrayList<>();
		JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
		
		//--------------------start  催收还款接口需要参数---------------------------
		Map<String, String> repayData = new HashMap<String, String>();
		HashMap<String, String> data = new HashMap<String, String>();
		//("还款流水")
		repayData.put("repaymentNo", curOutTradeNo);
		//("还款时间")
		repayData.put("repayTime", DateUtil.formatDateTime(new Date()));
		//("订单编号")
		repayData.put("orderNo", borrowNo);
		
		//根据场景不同，推送催收不同还款类型
		if(JsdRepayType.COLLECTION.equals(repayType) || JsdRepayType.REVIEW_COLLECTION.equals(repayType)){
			data.put("dataId", orderId.toString());//源数据id
			repayData.put("type", JsdRepayCollectionType.OFFLINE.getCode());
			noticeRecordDo.setType(JsdNoticeType.COLLECT.code);
		}else {
			repayData.put("type", JsdRepayCollectionType.APP.getCode());
			noticeRecordDo.setType(JsdNoticeType.OVERDUEREPAY.code);
			data.put("dataId", orderId.toString());//源数据id
		}
		repayData.put("repaymentAcc", uid.toString());//还款账户
		repayData.put("token",ConfigProperties.get(Constants.CONFKEY_COLLECTION_TOKEN));
		data.put("amount",repayAmount+"");
		repayData.put("companyId", ConfigProperties.get(Constants.CONFKEY_COLLECTION_COMPANYID));
		repayData.put("totalAmount", repayAmount+"");
		byte[] pd = DigestUtil.digestString(curOutTradeNo.getBytes(Charset.forName("UTF-8")), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
		String sign = DigestUtil.encodeHex(pd);
		repayData.put("sign",sign);
		list.add(data);
		//("还款详情, 格式: [{'dataId':'数据编号', 'amount':'还款金额(元, 精确到分)'},...]")
		repayData.put("details", JSON.toJSONString(list));
		//--------------------end  催收还款接口需要参数---------------------------


			//--------------------start 兼容审批模式还款通知 --------------------------
			if(JsdRepayType.REVIEW_COLLECTION.equals(repayType)) {
				repayData.put("reviewReuslt", reviewReuslt);
			}
			//--------------------end  兼容审批模式还款通知 ---------------------------

			noticeRecordDo.setUserId(uid);
			noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
			noticeRecordDo.setParams(JSON.toJSONString(repayData));
			jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
			if(collectionNoticeUtil.consumerRepayment(repayData)){
				noticeRecordDo.setRid(noticeRecordDo.getRid());
				noticeRecordDo.setGmtModified(new Date());
			jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
		}else {
			throw new BizException("nofityRepayment error!");
		}
	}
}
