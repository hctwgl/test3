package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.domain.XItem;

/**
 * 
 * @类描述：获取能否分期状态
 * @author xiaotianjian 2017年3月31日下午8:39:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAllowConsumeApi")
public class GetAllowConsumeApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> params = requestDataVo.getParams();
		Long userId = context.getUserId();
		String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"), null);
		params.put("numIid", numId);

		AfUserAuthDo autDo = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
		if (autDo == null) {
			throw new FanbeiException("authDo id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}

		String isSupplyCertify = "N";
		if (StringUtil.equals(autDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(autDo.getJinpoStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(autDo.getCreditStatus(), YesNoStatus.YES.getCode())) {
			isSupplyCertify = "Y";
		}

		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		BigDecimal leftAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
		String isNoneQuota = "N";// 可用额度是否为0
		
		if (numId != null) {
			try {
				List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
				if (null != nTbkItemList && nTbkItemList.size() > 0) {
					XItem item = nTbkItemList.get(0);
					String title = item.getTitle();
					RiskVirtualProductQuotaRespBo quotaBo = riskUtil.virtualProductQuota(userId.toString(), "", title);
					String quotaData = quotaBo.getData();
					if (StringUtils.isNotBlank(quotaData) && !StringUtil.equals(quotaData, "{}")) {
						JSONObject json = JSONObject.parseObject(quotaData);
						String virtualCode = json.getString("virtualCode");
						BigDecimal goodsUseableAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(userId, virtualCode, json.getBigDecimal("amount"));
						if (goodsUseableAmount.compareTo(leftAmount) < 0) {
							leftAmount = goodsUseableAmount;
						}
					}
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		
		if (leftAmount.compareTo(BigDecimal.ZERO) == 0) {
			isNoneQuota = "Y";
		}
		resp.addResponseData("isNoneQuota", isNoneQuota);
//		BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
//		if (StringUtil.equals(autDo.getRiskStatus(), RiskStatus.YES.getCode()) && usableAmount.compareTo(BigDecimal.ZERO) <= 0) {
//			throw new FanbeiException("available credit not enough", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH);
//		}

		resp.addResponseData("allowConsume", afUserAuthService.getConsumeStatus(context.getUserId(), context.getAppVersion()));
		resp.addResponseData("bindCardStatus", autDo.getBankcardStatus());
		resp.addResponseData("realNameStatus", autDo.getRealnameStatus());
		if (StringUtil.equals(autDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			resp.addResponseData("riskStatus", RiskStatus.A.getCode());
		} else {
			resp.addResponseData("riskStatus", autDo.getRiskStatus());
		}
		resp.addResponseData("faceStatus", autDo.getFacesStatus());

		resp.addResponseData("idNumber", Base64.encodeString(accountDo.getIdNumber()));
		resp.addResponseData("realName", accountDo.getRealName());
		resp.addResponseData("isSupplyCertify", isSupplyCertify);

		return resp;
	}
}
