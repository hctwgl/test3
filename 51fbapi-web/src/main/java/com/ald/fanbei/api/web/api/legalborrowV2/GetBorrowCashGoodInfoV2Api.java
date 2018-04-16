package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowLegalCashCouponDto;
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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

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
	private AfResourceService afResourceService;

	@Resource
	AfBorrowCashService afBorrowCashService;

	@Resource
	AfBorrowLegalCashCouponService legalCashCouponService;
	@Resource
	NumberWordFormat numberWordFormat;

	private Logger logger = LoggerFactory.getLogger(GetBorrowCashGoodInfoV2Api.class);

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> respData = Maps.newHashMap();
		// 判断用户是否登录
		Long userId = context.getUserId();
		
		GetBorrowCashGoodInfoParam param = (GetBorrowCashGoodInfoParam) requestDataVo.getParamObj();
		BigDecimal borrowAmount = param.getBorrowAmount();
		String borrowType = String.valueOf(numberWordFormat.borrowTime(param.getBorrowType()));
		BigDecimal borrowDay = new BigDecimal(borrowType);
		
		// 查询新利率配置
				AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
						Constants.BORROW_CASH_INFO_LEGAL_NEW);
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
				logger.info("newRate = > {}, borrowAmount = > {}",newRate,borrowAmount);
				logger.info("borrowDay = > {}",borrowDay);
				// 计算服务费和手续费
				BigDecimal serviceFee = new BigDecimal(newServiceRate / 100).multiply(borrowAmount).multiply(borrowDay)
						.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
				BigDecimal interestFee = new BigDecimal(newInterestRate / 100).multiply(borrowAmount).multiply(borrowDay)
						.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
		
				BigDecimal repayAmount = BigDecimalUtil.add(serviceFee,interestFee,borrowAmount);
		
		List<AfBorrowLegalCashCouponDto> cashCouponDoList = legalCashCouponService.getCouponIdByBorrowAmout(borrowAmount);
		List<Map<String,Object>> goodsInfoList = Lists.newArrayList();
		for (AfBorrowLegalCashCouponDto cashCoupon:cashCouponDoList) {
			Map<String, Object> goodsInfoMap = Maps.newHashMap();
			goodsInfoMap.put("saleAmount", 0);
			goodsInfoMap.put("goodsId", cashCoupon.getCouponId());
			goodsInfoMap.put("goodsName", cashCoupon.getCouponName());
			goodsInfoMap.put("goodsIcon", "");
			goodsInfoMap.put("goodsServiceFee", "");
			goodsInfoMap.put("goodsInterestFee", "");
			goodsInfoMap.put("serviceFee", serviceFee);
			goodsInfoMap.put("interestFee", interestFee);
			goodsInfoMap.put("repayAmount", repayAmount);
			goodsInfoMap.put("goodsProperty", "");
			goodsInfoList.add(goodsInfoMap);
		}
		respData.put("goodsInfoList", goodsInfoList);
		if(afResourceService.getBorrowCashCLosed()){
			respData.put("goodsInfoList", Lists.newArrayList());
		}
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
				} else if(StringUtils.equals(twoDay, borrowType)){
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
