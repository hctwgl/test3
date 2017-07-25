package com.ald.fanbei.api.web.api.brand;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.VirtualGoodsCateogy;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author xiaotianjian 2017年3月27日上午12:58:25
 * @类描述：获取分期金额
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayAmountApi")
public class GetPayAmountApi implements ApiHandle {
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Map<String, Object> params = requestDataVo.getParams();

		Long orderId = NumberUtil.objToLongDefault(params.get("orderId"), null);

		if (orderId == null) {
			logger.error("orderId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo == null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		AfUserDo afUser = afUserService.getUserById(orderInfo.getUserId());
		
		BigDecimal amount = orderInfo.getSaleAmount();
		if (StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
			amount = orderInfo.getActualAmount();
		}
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		// 删除2分期
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		removeSecondNper(array);

		// 免息
		String interestFreeJson = orderInfo.getInterestFreeJson();// 免息规则JSON
		JSONArray interestFreeArray = null;
		if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
			interestFreeArray = JSON.parseArray(interestFreeJson);
		}

		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), amount, resource.getValue1(), resource.getValue2());

        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
        BigDecimal useableAmount = BigDecimalUtil.subtract(afUserAccountDo.getAuAmount(), afUserAccountDo.getUsedAmount());
		//是否是限额类目
		String isQuotaGoods = "N";
		RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(orderInfo.getUserId().toString(), "", orderInfo.getGoodsName());
		String data = quotaBo.getData();
		if (StringUtils.isNotBlank(data)&&!StringUtil.equals(data, "{}")) {
			JSONObject json = JSONObject.parseObject(data);
			isQuotaGoods = "Y";
			resp.addResponseData("goodsTotalAmount", json.getBigDecimal("amount"));
			String virtualCode = json.getString("virtualCode");
			BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(), virtualCode, json.getBigDecimal("amount"));
			resp.addResponseData("goodsUseableAmount", goodsUseableAmount);
			VirtualGoodsCateogy virtualGoodsCateogy = VirtualGoodsCateogy.findRoleTypeByCode(virtualCode);
			resp.addResponseData("categoryName", virtualGoodsCateogy.getName());
			if (goodsUseableAmount.compareTo(useableAmount) < 0) {
				useableAmount = goodsUseableAmount;
			}
		}
		
		BigDecimal bankPayAmount = BigDecimalUtil.subtract(amount, useableAmount);
		
		resp.addResponseData("nperList", nperList);
		resp.addResponseData("instalmentAmount", amount);
		resp.addResponseData("useableAmount", useableAmount);
		resp.addResponseData("bankPayAmount", bankPayAmount);
		resp.addResponseData("realName", afUser.getRealName());
		resp.addResponseData("isQuotaGoods", isQuotaGoods);
		
		if (bankPayAmount.compareTo(BigDecimal.ZERO) > 0) {
			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(orderInfo.getUserId());
			resp.addResponseData("rId", afUserBankcardDo.getRid());
			resp.addResponseData("bankCode", afUserBankcardDo.getBankCode());
			resp.addResponseData("bankName", afUserBankcardDo.getBankName());
			resp.addResponseData("bankIcon", afUserBankcardDo.getBankIcon());
			resp.addResponseData("cardNumber", afUserBankcardDo.getCardNumber());
			resp.addResponseData("isValid", afUserBankcardDo.getIsValid());			
		}
		
		return resp;
	}

	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
				it.remove();
				break;
			}
		}
	}

}
