package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetBorrowCashGoodInfoParam;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *
 * @类描述：GetBorrowCashGoodInfoV2Api
 * @author 江荣波 2017年3月3日 11:41:32
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashGoodInfoV2Api")
@Validator("getBorrowCashGoodInfoParam")
public class GetBorrowCashGoodInfoV2Api extends GetBorrowCashBase implements ApiHandle {

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

	@Resource
	private AfGoodsPriceService afGoodsPriceService;

	private Logger logger = LoggerFactory.getLogger(GetBorrowCashGoodInfoV2Api.class);

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> respData = Maps.newHashMap();
		// 判断用户是否登录
		Long userId = context.getUserId();

		GetBorrowCashGoodInfoParam param = (GetBorrowCashGoodInfoParam) requestDataVo.getParamObj();
		BigDecimal borrowAmount = param.getBorrowAmount();
		String borrowType = param.getBorrowType();
		BigDecimal borrowDay = new BigDecimal(borrowType);

		BigDecimal oriRate = BigDecimal.ZERO;
		if (userId != null) {
			oriRate = riskUtil.getRiskOriRate(userId);
		}

		// 查询新利率配置
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL);
		BigDecimal newRate = null;

		double newServiceRate = 0;
		double newInterestRate = 0;
		if (rateInfoDo != null) {
			String borrowRate = rateInfoDo.getValue2();
			Map<String, Object> rateInfo = getRateInfo(borrowRate, borrowType, "borrow");
			newServiceRate = (double) rateInfo.get("serviceRate");
			newInterestRate = (double) rateInfo.get("interestRate");
			double totalRate = (double) rateInfo.get("totalRate");
			newRate = BigDecimal.valueOf(totalRate / 100);
		} else {
			newRate = BigDecimal.valueOf(0.36);
		}

		newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		BigDecimal profitAmount = oriRate.subtract(newRate).multiply(borrowAmount).multiply(borrowDay);
		if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
			profitAmount = BigDecimal.ZERO;
		}
		// 计算服务费和手续费
		BigDecimal serviceFee = new BigDecimal(newServiceRate / 100).multiply(borrowAmount).multiply(borrowDay)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		BigDecimal interestFee = new BigDecimal(newInterestRate / 100).multiply(borrowAmount).multiply(borrowDay)
				.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		// 如果用户未登录，则利润空间为0
		if (userId == null) {
			profitAmount = BigDecimal.ZERO;
		}
		List<Long> allGoodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmoutForV2(profitAmount);
		List<Map<String,Object>> goodsInfoList = Lists.newArrayList();
		for (Long goodsId : allGoodsId) {
			// 应还金额 = 借款金额 + 手续费 + 利息 + 商品借款金额 + 商品手续费 + 商品利息
			BigDecimal repayAmount = new BigDecimal(0);
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(goodsId);
			if (goodsInfo != null) {
				BigDecimal saleAmount = goodsInfo.getSaleAmount();
				Map<String, Object> goodsInfoMap = Maps.newHashMap();
				goodsInfoMap.put("saleAmount", saleAmount);
				goodsInfoMap.put("goodsId", goodsId);
				goodsInfoMap.put("goodsName", goodsInfo.getName());
				goodsInfoMap.put("goodsIcon", goodsInfo.getGoodsIcon());

				String borrowRate = rateInfoDo.getValue3();
				Map<String, Object> rateInfo = getRateInfo(borrowRate, borrowType, "consume");
				double goodsServiceRate = (double) rateInfo.get("serviceRate");
				double goodsInterestRate = (double) rateInfo.get("interestRate");

				BigDecimal goodsServiceFee = new BigDecimal(goodsServiceRate / 100).multiply(saleAmount)
						.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
				BigDecimal goodsInterestFee = new BigDecimal(goodsInterestRate / 100).multiply(saleAmount)
						.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

				serviceFee = serviceFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				interestFee = interestFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				goodsServiceFee = goodsServiceFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				goodsInterestFee = goodsInterestFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				goodsInfoMap.put("goodsServiceFee", goodsServiceFee);
				goodsInfoMap.put("goodsInterestFee", goodsInterestFee);

				goodsInfoMap.put("serviceFee", serviceFee);
				goodsInfoMap.put("interestFee", interestFee);
				repayAmount = BigDecimalUtil.add(serviceFee, interestFee, borrowAmount);
				repayAmount = repayAmount.add(goodsServiceFee).add(goodsInterestFee).add(saleAmount);
				goodsInfoMap.put("repayAmount", repayAmount);
				// 查询商品默认规格
				AfGoodsPriceDo afGoodsProperty = afGoodsPriceService.getGoodsPriceByGoodsId(goodsId);
				if (afGoodsProperty != null) {
					String props = afGoodsProperty.getPropertyValueNames();
					if (StringUtils.isNotEmpty(props)) {
						String[] propsArray = props.split(";");
						goodsInfoMap.put("goodsProperty", propsArray[0]);
					}
				}
				
				goodsInfoList.add(goodsInfoMap);
			}
			
			
		}

		logger.info("getBorrowCashGoodInfoApi process, userid => {} , resp data => {}", userId,
				JSONObject.toJSONString(respData));
		resp.setResponseData(respData);
		return resp;
	}

	private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
		Map<String, Object> rateInfo = Maps.newHashMap();
		double serviceRate = 0;
		double interestRate = 0;
		JSONArray array = JSONObject.parseArray(borrowRate);
		double totalRate = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString(tag + "Tag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), borrowType)) {
					interestRate = info.getDouble(tag + "SevenDay");
					totalRate += interestRate;
				} else {
					interestRate = info.getDouble(tag + "FourteenDay");
					totalRate += interestRate;
				}
			}
			if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(AfBorrowCashType.SEVEN.getName(), borrowType)) {
					serviceRate = info.getDouble(tag + "SevenDay");
					totalRate += serviceRate;
				} else {
					serviceRate = info.getDouble(tag + "FourteenDay");
					totalRate += serviceRate;
				}
			}

		}
		rateInfo.put("serviceRate", serviceRate);
		rateInfo.put("interestRate", interestRate);
		rateInfo.put("totalRate", totalRate);
		return rateInfo;
	}

}