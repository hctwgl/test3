package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.VirtualGoodsCateogy;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
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
	AfUserService afUserService;
	@Resource
	AfOrderService afOrderService;
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
		
		Long orderId = NumberUtil.objToLongDefault(params.get("orderId"), null);
		String goodsName = ObjectUtils.toString(params.get("goodsName"));
		
		if (orderId == null) {
			logger.error("orderId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo == null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal useableAmount = BigDecimalUtil.subtract(afUserAccountDo.getAuAmount(), afUserAccountDo.getUsedAmount());
		//是否是限额类目
        Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
		if (afOrderService.isVirtualGoods(virtualMap)) {
			String virtualCode = afOrderService.getVirtualCode(virtualMap);
			BigDecimal totalVirtualAmount = afOrderService.getVirtualAmount(virtualMap);
			resp.addResponseData("goodsTotalAmount", totalVirtualAmount);
			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(), virtualCode, totalVirtualAmount);
			resp.addResponseData("goodsUseableAmount", goodsUseableAmount);
			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
			resp.addResponseData("categoryName", virtualGoodsCateogy.getName());
			useableAmount = goodsUseableAmount.compareTo(useableAmount) < 0 ? goodsUseableAmount : useableAmount;
		}
//		// 是否是限额类目
//		String isQuotaGoods = "N";
//		RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", goodsName);
//		String quotaData = quotaBo.getData();
//		if (StringUtils.isNotBlank(quotaData) && !StringUtil.equals(quotaData, "{}")) {
//			JSONObject json = JSONObject.parseObject(quotaData);
//			isQuotaGoods = "Y";
//			resp.addResponseData("goodsTotalAmount", json.getBigDecimal("amount"));
//			String virtualCode = json.getString("virtualCode");
//			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
//			resp.addResponseData("goodsUseableAmount", goodsUseableAmount);
//			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
//			resp.addResponseData("categoryName", virtualGoodsCateogy.getName());
//			if (goodsUseableAmount.compareTo(useableAmount) < 0) {
//				useableAmount = goodsUseableAmount;
//			}
//		}
		
		AfUserDo afUserDo = afUserService.getUserById(userId);
		
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		String isSupplyCertify = "N";
		if (StringUtil.equals(authDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getJinpoStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getCreditStatus(), YesNoStatus.YES.getCode())) {
			isSupplyCertify = "Y";
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("quotaAmount", useableAmount);
		data.put("realName", afUserDo.getRealName());
		data.put("isSupplyCertify", isSupplyCertify);
		
		BigDecimal bankPayAmount = BigDecimal.ZERO;
		if (orderInfo.getActualAmount().compareTo(useableAmount) > 0) {
			bankPayAmount =  BigDecimalUtil.subtract(orderInfo.getActualAmount(), useableAmount);
		}
		data.put("bankPayAmount", bankPayAmount);
		
		if (bankPayAmount.compareTo(BigDecimal.ZERO) > 0) {
			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(orderInfo.getUserId());
			data.put("rId", afUserBankcardDo.getRid());
			data.put("bankCode", afUserBankcardDo.getBankCode());
			data.put("bankName", afUserBankcardDo.getBankName());
			data.put("bankIcon", afUserBankcardDo.getBankIcon());
			data.put("cardNumber", afUserBankcardDo.getCardNumber());
			data.put("isValid", afUserBankcardDo.getIsValid());			
		}
		
		resp.setResponseData(data);
		
		return resp;
	}

}
