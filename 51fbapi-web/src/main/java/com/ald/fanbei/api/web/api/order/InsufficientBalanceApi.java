package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.VirtualGoodsCateogy;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：余额不足提示页面
 * @author fmai 2017年7月10日 14:10:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("insufficientBalanceApi")
public class InsufficientBalanceApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		HashMap<String, Object> responseMap = new HashMap<String, Object>();
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0l);
		
		AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);
		
		BigDecimal needPayAmount = NumberUtil.objToBigDecimalDefault(afOrderDo.getActualAmount(), BigDecimal.ZERO);

		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);

		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		String isSupplyCertify = "N";
		if (StringUtil.equals(authDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getJinpoStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getCreditStatus(), YesNoStatus.YES.getCode())) {
			isSupplyCertify = "Y";
		}
		
		String isRepayment = "A";
		if (accountDo.getUsedAmount().compareTo(BigDecimal.ZERO)>0) {
			isRepayment = "N";
		}
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
		if (afBorrowCashDo != null) {
			isRepayment = "Y";
			AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);
			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
			BigDecimal returnAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			responseMap.put("borrowID", afBorrowCashDo.getRid());
			responseMap.put("borrowAmount", returnAmount);//应还金额
			responseMap.put("balanceAmount", account.getRebateAmount());//账户余额
			responseMap.put("jiFenBaoCount", account.getJfbAmount());
		}
		//是否是限额类目
		String isQuotaGoods = "N";
		RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", afOrderDo.getGoodsName());
		String data = quotaBo.getData();
		if (StringUtils.isNotBlank(data)) {
			isQuotaGoods = "Y";
			JSONObject json = JSONObject.parseObject(data);
			responseMap.put("goodsTotalAmount", json.getBigDecimal("amount"));
			String virtualCode = json.getString("virtualCode");
			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
			responseMap.put("goodsUseableAmount", goodsUseableAmount);
			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
			responseMap.put("categoryName", virtualGoodsCateogy.getName());
		}
				
		responseMap.put("needPayAmount", needPayAmount);
		responseMap.put("totalAmount", accountDo.getAuAmount());
		responseMap.put("useableAmount", BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount()));
		responseMap.put("isSupplyCertify", isSupplyCertify);
		responseMap.put("isRepayment", isRepayment);
		responseMap.put("isQuotaGoods", isQuotaGoods);
		resp.setResponseData(responseMap);
		return resp;
	}

}
