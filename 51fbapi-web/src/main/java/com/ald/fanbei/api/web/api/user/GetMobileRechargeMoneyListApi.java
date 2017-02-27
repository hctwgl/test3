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

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

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

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}

		List<AfResourceDo> resourceList = afResourceService
				.getResourceListByTypeOrderBy(AfResourceType.MobileRecharge.getCode());
		if (resourceList.size() == 0) {
			throw new FanbeiException("mobile recharge resource is invalid", FanbeiExceptionCode.PARAM_ERROR);

		}
		List<AfUserCouponDto> couponDtoList = afUserCouponService.getUserCouponByUserIdAndType(userId,
				CouponType.REPAYMENT.getCode(), new BigDecimal(resourceList.get(0).getValue()));
		if(couponDtoList==null){
			couponDtoList = new ArrayList<AfUserCouponDto>();
		}
		Map<String, Object> data = couponObjectWithAfUserCouponDtoList(couponDtoList,resourceList);

		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> couponObjectWithAfUserCouponDtoList(List<AfUserCouponDto> couponList,List<AfResourceDo> resourceList){
		Map<String, Object> returnData = new HashMap<String, Object>();

		Map<String, AfUserCouponDto> data = new HashMap<String, AfUserCouponDto>();
		AfUserCouponDto temCoupon = new AfUserCouponDto();
		for (AfResourceDo afResourceDo : resourceList) {
			data.put(afResourceDo.getValue(), temCoupon);
		}
		
		List<Object> list = new ArrayList<Object>();
	

		for (AfUserCouponDto afUserCouponDto : couponList) {
			
			//根据
			for (String key : data.keySet()) {  
				AfUserCouponDto coupon =  data.get(key);
				//充值金额
				BigDecimal rechargeMoney=new BigDecimal(key); 
				//充值金额大于优惠券限制金额
				if(rechargeMoney.compareTo(afUserCouponDto.getLimitAmount())==1&&(coupon.getAmount()==null||(coupon.getAmount() !=null&&afUserCouponDto.getAmount().compareTo(coupon.getAmount())==1))){
					data.put(key, afUserCouponDto);
				}
			  
			} 
			list.add(couponObjectWithAfUserCouponDto(afUserCouponDto));
			
		}
		List<Object> rechargeList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : resourceList) {
			Map<String, Object> couponData = new HashMap<String, Object>();
			
			couponData.put("amount", afResourceDo.getValue());
			couponData.put("rebate", afResourceDo.getValue1());
			AfUserCouponDto coupon =  data.get(afResourceDo.getValue());

			couponData.put("coupon", couponObjectWithAfUserCouponDto(coupon));
			rechargeList.add(couponData);

		}

		
		returnData.put("couponList", list);
		returnData.put("rechargeList", rechargeList);
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

		return returnData;

	}

}
