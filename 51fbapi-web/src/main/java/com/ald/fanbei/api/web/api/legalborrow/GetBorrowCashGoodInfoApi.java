package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import org.apache.commons.lang.ObjectUtils;
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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

	@Resource
	private AfGoodsPriceService afGoodsPriceService;

	@Resource
	NumberWordFormat numberWordFormat;
	private Logger logger = LoggerFactory.getLogger(GetBorrowCashGoodInfoApi.class);

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
		borrowDay = BigDecimal.valueOf(numberWordFormat.borrowTime(borrowType));
		List<AfResourceDo> borrowConfigList = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(borrowConfigList);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal serviceRate = bankRate.multiply(bankDouble).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6,
				RoundingMode.HALF_UP);
		BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

		if (userId != null) {
			Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
			if (poundageRateCash != null) {
				poundageRate = new BigDecimal(poundageRateCash.toString());
			} else {
				try {
					JSONObject params = new JSONObject();
					String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
					params.put("ipAddress", CommonUtil.getIpAddr(request));
					params.put("appName",appName);
					params.put("bqsBlackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("bqsBlackBox"));
					params.put("blackBox",requestDataVo.getParams()==null?"":requestDataVo.getParams().get("blackBox"));
					RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(),params);
					String poundage = riskResp.getPoundageRate();
					if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
						logger.info("comfirmBorrowCash get user poundage rate from risk: consumerNo="
								+ riskResp.getConsumerNo() + ",poundageRate=" + poundage);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundage,
								Constants.SECOND_OF_ONE_MONTH);
						bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId, new Date(),
								Constants.SECOND_OF_ONE_MONTH);
					}
				} catch (Exception e) {
					logger.info(userId + "从风控获取分层用户额度失败：" + e);
				}
			}
		}
		// 计算原始利率
		BigDecimal oriRate = serviceRate.add(poundageRate);
		// 查询新利率配置
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL_NEW);
		BigDecimal newRate = null;

		double newServiceRate = 0;
		double newInterestRate = 0;
		if (rateInfoDo != null) {
			String borrowRate = rateInfoDo.getValue2();
			Map<String, Object> rateInfo = getRateInfo(borrowRate, String.valueOf(numberWordFormat.borrowTime(borrowType)), "borrow");
			newServiceRate = (double) rateInfo.get("serviceRate");
			newInterestRate = (double) rateInfo.get("interestRate");
			double totalRate = (double) rateInfo.get("totalRate");
			newRate = BigDecimal.valueOf(totalRate / 100);
		} else {
			newRate = BigDecimal.valueOf(0.36);
		}

		newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		BigDecimal profitAmount = oriRate.subtract(newRate).multiply(new BigDecimal(borrowAmount)).multiply(borrowDay);
		if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
			profitAmount = BigDecimal.ZERO;
		}
		// 计算服务费和手续费
		BigDecimal serviceFee = new BigDecimal(newServiceRate / 100).multiply(new BigDecimal(borrowAmount))
				.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		BigDecimal interestFee = new BigDecimal(newInterestRate / 100).multiply(new BigDecimal(borrowAmount))
				.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

		// 应还金额 = 借款金额 + 手续费 + 利息 + 商品借款金额 + 商品手续费 + 商品利息
		BigDecimal repayAmount = BigDecimal.ZERO;

		// 如果用户未登录，则利润空间为0
		if (userId == null) {
			profitAmount = BigDecimal.ZERO;
		}
		Long goodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmout(profitAmount);
		if (goodsId != null) {
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(goodsId);
			if (goodsInfo != null) {
				BigDecimal saleAmount = goodsInfo.getSaleAmount();
				respData.put("saleAmount", saleAmount);
				respData.put("goodsId", goodsId);
				respData.put("goodsName", goodsInfo.getName());
				respData.put("goodsIcon", goodsInfo.getGoodsIcon());

				String borrowRate = rateInfoDo.getValue3();
				Map<String, Object> rateInfo = getRateInfo(borrowRate, String.valueOf(numberWordFormat.borrowTime(borrowType)), "consume");
				double totalRate = (double) rateInfo.get("totalRate");
				double goodsServiceRate = (double) rateInfo.get("serviceRate");
				double goodsInterestRate = (double) rateInfo.get("interestRate");
				// 计算商品借款总费用
				BigDecimal goodsRate = BigDecimal.valueOf(totalRate / 100);

				BigDecimal goodsFee = goodsRate.multiply(saleAmount).multiply(borrowDay)
						.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

				BigDecimal goodsServiceFee = new BigDecimal(goodsServiceRate / 100).multiply(saleAmount)
						.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
				BigDecimal goodsInterestFee = new BigDecimal(goodsInterestRate / 100).multiply(saleAmount)
						.multiply(borrowDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
				
				serviceFee = serviceFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				interestFee = interestFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				goodsServiceFee = goodsServiceFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				goodsInterestFee = goodsInterestFee.setScale(2, BigDecimal.ROUND_HALF_UP);
				respData.put("goodsServiceFee", goodsServiceFee);
				respData.put("goodsInterestFee", goodsInterestFee);
				
				respData.put("serviceFee", serviceFee);
				respData.put("interestFee", interestFee);
				repayAmount = BigDecimalUtil.add(serviceFee, interestFee, new BigDecimal(borrowAmount));
				repayAmount = repayAmount.add(goodsServiceFee).add(goodsInterestFee).add(saleAmount);
			}
			// 查询商品默认规格
			AfGoodsPriceDo afGoodsProperty = afGoodsPriceService.getGoodsPriceByGoodsId(goodsId);
			if (afGoodsProperty != null) {
				String props = afGoodsProperty.getPropertyValueNames();
				if (StringUtils.isNotEmpty(props)) {
					String[] propsArray = props.split(";");
					respData.put("goodsProperty", propsArray[0]);
				}
			}
		}
		respData.put("repayAmount", repayAmount);
		logger.info("getBorrowCashGoodInfoApi process, userid => {} , resp data => {}", userId,
				JSONObject.toJSONString(respData));
		resp.setResponseData(respData);
		return resp;
	}

	private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
		String oneDay = "";
		String twoDay = "";
		if(null != afResourceDo){
			oneDay = afResourceDo.getTypeDesc().split(",")[0];
			twoDay = afResourceDo.getTypeDesc().split(",")[1];
		}
		Map<String, Object> rateInfo = Maps.newHashMap();
		double serviceRate = 0;
		double interestRate = 0;
		JSONArray array = JSONObject.parseArray(borrowRate);
		double totalRate = 0;
		for (int i = 0; i < array.size(); i++) {
			JSONObject info = array.getJSONObject(i);
			String borrowTag = info.getString(tag + "Tag");
			if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, borrowType)) {
					interestRate = info.getDouble(tag + "FirstType");
					totalRate += interestRate;
				} else if(StringUtils.equals(twoDay, borrowType)) {
					interestRate = info.getDouble(tag + "SecondType");
					totalRate += interestRate;
				}
			}
			if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				if (StringUtils.equals(oneDay, borrowType)) {
					serviceRate = info.getDouble(tag + "FirstType");
					totalRate += serviceRate;
				} else if(StringUtils.equals(twoDay, borrowType)){
					serviceRate = info.getDouble(tag + "SecondType");
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
