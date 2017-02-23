/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午4:37:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCouponListApi")
public class GetCouponListApi implements ApiHandle {

	@Resource
	AfCouponService afCouponService;
	
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
//		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ResourceHomeTypeCoupon.getCode(), "COUPON_R");
//		Map<String, Object> data = new HashMap<String, Object>();
//		List<Object> couponList = new ArrayList<Object>();
//
//		if(afResourceDo !=null ){
//			List<AfCouponDo> list = afCouponService.selectCouponByCouponIds(afResourceDo.getValue1());
//			for (AfCouponDo afCouponDo : list) {
//				Map<String, Object> couponData = couponWithAfCouponList(afCouponDo);
//		   		couponList.add(couponData);
//			}
//		}
//		data.put("couponList", couponList);
//		resp.setResponseData(data);

		return resp;
	}
	
	public Map<String, Object> couponWithAfCouponList(AfCouponDo afCouponDo){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("couponId",afCouponDo.getRid() );
		data.put("name", afCouponDo.getName());
		data.put("limitAmount",afCouponDo.getLimitAmount() );
		data.put("amount",afCouponDo.getAmount() );
		data.put("useRule",afCouponDo.getUseRule() );
		data.put("totalCount",afCouponDo.getTotalCount() );
		data.put("gmtStart",afCouponDo.getGmtStart() );
		data.put("gmtEnd",afCouponDo.getGmtEnd());
		data.put("validDays",afCouponDo.getValidDays() );
		data.put("limitCount",afCouponDo.getLimitCount() );

		return data;
	}

}
