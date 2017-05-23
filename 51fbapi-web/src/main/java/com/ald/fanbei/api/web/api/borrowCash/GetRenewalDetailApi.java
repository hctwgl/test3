package com.ald.fanbei.api.web.api.borrowCash;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author fumeiai 2017年05月20日下午1:56:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRenewalDetailApi")
public class GetRenewalDetailApi implements ApiHandle {

	@Resource
	AfRenewalDetailService afRenewalDetailService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("renewalId"), 0l);
		
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByRenewalId(rid);

		if (afRenewalDetailDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		
		Map<String, Object> data = objectWithAfRenewalDetailDo(afRenewalDetailDo);

		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> objectWithAfRenewalDetailDo(AfRenewalDetailDo afRenewalDetailDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afRenewalDetailDo.getRid());
		data.put("renewalAmount", afRenewalDetailDo.getRenewalAmount());//续期本金
		data.put("priorInterest", afRenewalDetailDo.getPriorInterest());//上期利息
		data.put("priorOverdue", afRenewalDetailDo.getPriorOverdue());//上期滞纳金
		data.put("nextPoundage", afRenewalDetailDo.getNextPoundage());//下期手续费
		data.put("cardName", afRenewalDetailDo.getCardName());//支付方式（卡名称）
		data.put("tradeNo", afRenewalDetailDo.getTradeNo());//支付编号
		data.put("gmtCreate", afRenewalDetailDo.getGmtCreate().getTime());//创建时间
		data.put("renewalNo", afRenewalDetailDo.getPayTradeNo());//续借编号
		
		return data;

	}

}
