/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfMobileChargeService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfMoblieChargeDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月25日下午5:41:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMobileRechargeMoneyListApi")
public class GetMobileRechargeMoneyListApi implements ApiHandle {
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfMobileChargeService afMobileChargeService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		
        String province = ObjectUtils.toString(requestDataVo.getParams().get("province"));
        String company = ObjectUtils.toString(requestDataVo.getParams().get("company"));

		AfMoblieChargeDo afMoblieChargeDo = afMobileChargeService.getMoblieChargeByTypeAndCompany(province,company);
		if(afMoblieChargeDo==null){
			afMoblieChargeDo = afMobileChargeService.getMoblieChargeByTypeAndCompany("未知","未知");
		}

		//500是充值金额里面最大充值金额
		List<AfUserCouponDto> couponDtoList = afUserCouponService.getUserCouponByUserIdAndType(userId,
				CouponType.MOBILE.getCode(), new BigDecimal(500));
		if(couponDtoList==null){
			couponDtoList = new ArrayList<AfUserCouponDto>();
		}
		Map<String, Object> data = couponObjectWithAfUserCouponDtoList(couponDtoList,afMoblieChargeDo);

		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> couponObjectWithAfUserCouponDtoList(List<AfUserCouponDto> couponList,AfMoblieChargeDo afMoblieChargeDo){
		Map<String, Object> returnData = new HashMap<String, Object>();
	

		List<Object> list = new ArrayList<Object>();
		String priceJson = afMoblieChargeDo.getPriceJson();
		
		JSONArray array = JSONArray.parseArray(priceJson);
		
		for (AfUserCouponDto afUserCouponDto : couponList) {
		
			list.add(couponObjectWithAfUserCouponDto(afUserCouponDto));
			
		}

		returnData.put("couponList", list);
		returnData.put("rechargeList", array);
		return returnData;
		
	}
	
	public Map<String, Object> couponObjectWithAfUserCouponDto(AfUserCouponDto afUserCouponDto){
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("rid", afUserCouponDto.getRid());
		returnData.put("useRule", afUserCouponDto.getUseRule());
		returnData.put("limitAmount", afUserCouponDto.getLimitAmount());
		returnData.put("name", afUserCouponDto.getName());
		returnData.put("gmtStart", afUserCouponDto.getGmtStart());
		returnData.put("gmtEnd", afUserCouponDto.getGmtEnd());
		returnData.put("amount", afUserCouponDto.getAmount());

		return returnData;

	}

}
