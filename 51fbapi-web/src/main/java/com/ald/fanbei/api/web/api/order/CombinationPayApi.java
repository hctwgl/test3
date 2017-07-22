package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.VirtualGoodsCateogy;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @author fumeiai 2017年7月19日 21:26
 * @类描述：组合支付页面
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("combinationPayApi")
public class CombinationPayApi implements ApiHandle {
	@Resource
	RiskUtil riskUtil;

	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource
	AfUserBankcardService afUserBankcardService;	

	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Map<String, Object> params = requestDataVo.getParams();
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(params.get("amount"), BigDecimal.ZERO);
		String goodsName = ObjectUtils.toString(params.get("goodsName"));
		String numId = ObjectUtils.toString(params.get("numId"));
		String type = ObjectUtils.toString(params.get("type"), OrderType.TAOBAO.getCode());
		BigDecimal eachAmount = NumberUtil.objToBigDecimalDefault(params.get("eachAmount"), BigDecimal.ZERO);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);

		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = BigDecimalUtil.subtract(afUserAccountDo.getAuAmount(), afUserAccountDo.getUsedAmount());
		// 是否是限额类目
		String isQuotaGoods = "N";
		RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", goodsName);
		String quotaData = quotaBo.getData();
		if (StringUtils.isNotBlank(quotaData) && !StringUtil.equals(quotaData, "{}")) {
			JSONObject json = JSONObject.parseObject(quotaData);
			isQuotaGoods = "Y";
			resp.addResponseData("goodsTotalAmount", json.getBigDecimal("amount"));
			String virtualCode = json.getString("virtualCode");
			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
			resp.addResponseData("goodsUseableAmount", goodsUseableAmount);
			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
			resp.addResponseData("categoryName", virtualGoodsCateogy.getName());
			if (goodsUseableAmount.compareTo(useableAmount) < 0) {
				useableAmount = goodsUseableAmount;
			}
		}

		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		String isSupplyCertify = "N";
		if (StringUtil.equals(authDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getJinpoStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getCreditStatus(), YesNoStatus.YES.getCode())) {
			isSupplyCertify = "Y";
		}

		List<AfBankUserBankDto> userBankList = afUserBankcardService.getUserBankcardByUserId(userId);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("amount", amount);
		data.put("numId", numId);
		data.put("type", type);
		data.put("quotaAmount", useableAmount);
		data.put("bankAmount", BigDecimalUtil.subtract(amount, useableAmount));
		data.put("isSupplyCertify", isSupplyCertify);
		data.put("goodsName", goodsName);
		data.put("eachAmount", eachAmount);
		data.put("nper", nper);
		
		resp.setResponseData(data);
		resp.addResponseData("bankList", userBankList);
		
		return resp;
	}

}
