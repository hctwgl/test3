package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：支付前根据额度确定支付方式
 * @author fumeiai 2017年7月27日上午13:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBeforePayTypeApi")
public class GetBeforePayTypeApi implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"), null);

		if (orderId == null) {
			logger.error("orderId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		// TODO获取用户订单
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);

		BigDecimal actualAmount = orderInfo.getActualAmount();
		//是否是限额类目
		AfUserAccountSenceDo userAccountInfo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(),orderInfo.getUserId());
		Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
		// 判断使用额度
		BigDecimal leftAmount = afOrderService.checkUsedAmount(virtualMap, orderInfo, userAccountInfo);
		
		HashMap<String, Object> responseMap = new HashMap<String, Object>();
		if (leftAmount.compareTo(BigDecimal.ZERO) == 0) {
			responseMap.put("payType", PayType.OTHER.getCode());
		} else if (leftAmount.compareTo(actualAmount) >= 0) {
			responseMap.put("payType", PayType.AGENT_PAY.getCode());
		} else if (leftAmount.compareTo(actualAmount) < 0) {
			responseMap.put("payType", PayType.COMBINATION_PAY.getCode());
		}
		
		if (StringUtil.equals(orderInfo.getPayType(), PayType.WECHAT.getCode()) || StringUtil.equals(orderInfo.getPayType(), PayType.BANK.getCode()) || StringUtil.equals(orderInfo.getPayType(), StringUtil.EMPTY)) {
			responseMap.put("payType", PayType.OTHER.getCode());
		}
		
		resp.setResponseData(responseMap);
		return resp;
	}
}
