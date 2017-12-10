package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;

/**
 *
 * @类描述：GetBorrowCashGoodInfoApi
 * @author 江荣波 2017年3月3日 11:41:32
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashGoodInfoApi")
public class GetBorrowCashGoodInfoApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	private RiskUtil riskUtil;

	@Resource
	private BizCacheUtil bizCacheUtil;

	@Resource
	private AfResourceService afResourceService;

	@Resource
	private AfBorrowLegalGoodsService afBorrowLegalGoodsService;

	@Resource
	private AfGoodsService afGoodsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> respData = Maps.newHashMap();
		// 判断用户是否登录
		Long userId = context.getUserId();

		String borrowAmount = ObjectUtils.toString(requestDataVo.getParams().get("borrowAmount"));
		String borrowType = ObjectUtils.toString(requestDataVo.getParams().get("borrowType"));
		if (StringUtils.isEmpty(borrowAmount)) {
			throw new FanbeiException("borrowAmount can't be empty.");
		}
		if (StringUtils.isEmpty(borrowType)) {
			throw new FanbeiException("borrowType can't be empty.");
		}
		BigDecimal borrowDay = BigDecimal.ZERO;
		if (StringUtils.equals(AfBorrowCashType.SEVEN.getCode(), borrowType)) {
			borrowDay = BigDecimal.valueOf(7);
		} else {
			borrowDay = BigDecimal.valueOf(14);
		}
		List<AfResourceDo> borrowConfigList = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(borrowConfigList);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal serviceRate = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
		BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

		if (userId != null) {
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			} else {
				try {
					RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString());
					String poundage = riskResp.getPoundageRate();
					if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
						logger.info("comfirmBorrowCash get user poundage rate from risk: consumerNo="
								+ riskResp.getConsumerNo() + ",poundageRate=" + poundage);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundage,
								Constants.SECOND_OF_ONE_MONTH);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId,
								new Date(System.currentTimeMillis()), Constants.SECOND_OF_ONE_MONTH);
					}
				} catch (Exception e) {
					logger.info(userId + "从风控获取分层用户额度失败：" + e);
				}
			}
		}
		// 计算原始利率
		BigDecimal oriRate = serviceRate.add(poundageRate);

		// 查询新利率
		BigDecimal newRate = BigDecimal.valueOf(0.36);

		BigDecimal profitAmount = oriRate.subtract(newRate).multiply(new BigDecimal(borrowAmount)).multiply(borrowDay)
				.divide(BigDecimal.valueOf(360));
		Long goodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmout(profitAmount);
		if (goodsId != null) {
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(goodsId);
			if (goodsInfo != null) {
				respData.put("saleAmout", goodsInfo.getSaleAmount());
				respData.put("goodsId", goodsId);
				respData.put("goodsName", goodsInfo.getName());
			}
		}
		resp.setResponseData(respData);
		return resp;
	}

}
