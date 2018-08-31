package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**  
 * @Description: 极速贷获取续期详情
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月28日
 */
@Component("getRenewalDetailApi")
public class GetRenewalDetailApi implements DsedH5Handle {

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    private JsdResourceService jsdResourceService;

	@Override
	public DsedH5HandleResponse process(Context context) {
		// 借款borrowNo
		String borrowNo = ObjectUtils.toString(context.getDataMap().get("borrowNo"), "");
		// 请求时间戳
		String timestamp = ObjectUtils.toString(context.getDataMap().get("timestamp"), "");
		
		// 借款记录
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByBorrowNo(borrowNo);
		if (borrowCashDo == null) {
			throw new FanbeiException("borrowCash is not exist", FanbeiExceptionCode.SYSTEM_ERROR);
		}
		logger.info("getRenewalDetail jsdBorrowCash record = {} " , borrowCashDo);

		jsdBorrowCashRenewalService.checkCanRenewal(borrowCashDo);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("delayInfo", this.getRenewalDetail(borrowCashDo));
		data.put("timestamp", timestamp);
		
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功", data);

		return resp;
	}
	

	private JSONArray getRenewalDetail(JsdBorrowCashDo borrowCashDo) {
		JSONArray delayArray = new JSONArray();
		Map<String, Object> delayInfo = new HashMap<String, Object>();
		
		//上一笔订单记录
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());
		if(orderCashDo == null)	throw new FanbeiException(FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		
		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderService.getById(orderCashDo.getBorrowLegalOrderId());
		if(orderDo==null) throw new FanbeiException(FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		logger.info("last orderCash record = {} " , orderCashDo);
		
		JsdResourceDo renewalResource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(renewalResource==null) throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);

		// 允许续期天数
		BigDecimal allowRenewalDay = new BigDecimal(renewalResource.getValue());
		
		// 续借需还本金比例
		BigDecimal renewalCapitalRate = new BigDecimal(renewalResource.getValue1());
		//续借需要支付本金 = 借款金额 * 续借需还本金比例
		BigDecimal capital = borrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
		
		// 上期订单待还本金
		BigDecimal waitOrderAmount = BigDecimalUtil.add(orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),
				orderCashDo.getSumRepaidPoundage()).subtract(orderCashDo.getRepaidAmount());
		
		// 上期总利息
		BigDecimal rateAmount = BigDecimalUtil.add(borrowCashDo.getRateAmount(),orderCashDo.getInterestAmount());
		// 上期总手续费
		BigDecimal poundage = BigDecimalUtil.add(borrowCashDo.getPoundage(),orderCashDo.getPoundageAmount());
		// 上期总逾期费
		BigDecimal overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(),orderCashDo.getOverdueAmount());
		
		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +上期待还订单)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital, waitOrderAmount);
		
		String deferRemark = "上期利息"+rateAmount+
				"元,上期手续费"+poundage+
				"元,上期逾期费"+overdueAmount+
				"元,本金还款部分"+capital+
				"元,本期商品价格"+waitOrderAmount+"元";
		
		BigDecimal principalAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumOverdue(), 
				borrowCashDo.getSumRate(), borrowCashDo.getSumRenewalPoundage())
				.subtract(borrowCashDo.getRepayAmount().add(capital));
		
		delayInfo.put("principalAmount", principalAmount+"");	// 展期后剩余借款本金
		delayInfo.put("deferAmount", renewalPayAmount+"");	// 需支付总金额
		delayInfo.put("deferDay", allowRenewalDay+"");	// 续期天数
		delayInfo.put("deferRemark", deferRemark);	// 费用明细	展期金额的相关具体描述（多条说明用英文逗号,用间隔）
		delayInfo.put("totalDiffFee", "");	// 展期后的利润差，西瓜会根据此金额匹配搭售商品 TODO
		this.getRenewalRate(delayInfo);
		
		delayArray.add(delayInfo);
		
		return delayArray;
	}
	
	
	private void getRenewalRate(Map<String, Object> delayInfo) {
		JsdResourceDo rateResource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RATE_INFO.getCode());
		if(rateResource==null) throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);
		
		//借款手续费率
		BigDecimal poundageRate = null;
		//借款利率
		BigDecimal baseBankRate = null;
		
		String rateStr = rateResource.getValue();
		JSONArray array = JSONObject.parseArray(rateStr);
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString("borrowTag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				baseBankRate = info.getBigDecimal("borrowFirstType");
			}
			if(StringUtils.equals("SERVICE_RATE", borrowTag)){
				poundageRate = info.getBigDecimal("borrowFirstType");
			}
		}
		delayInfo.put("interestRate", baseBankRate.divide(new BigDecimal(100)));
		delayInfo.put("serviceRate", poundageRate.divide(new BigDecimal(100)));
	}
}

