/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;

/**
 * @类描述：
 * @author suweili 2017年3月29日下午3:09:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getUserCounponListTypeApi")
public class GetUserCounponListTypeApi implements ApiHandle {

	@Resource
	AfUserCouponService afUserCouponService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
        if(CouponType.findRoleTypeByCode(type)==null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        List<AfUserCouponDto> couponList = afUserCouponService.getUserCouponByType(userId, type);
        List<AfUserCouponVo> couponVoList = new ArrayList<AfUserCouponVo>();
        for (AfUserCouponDto afUserCouponDto : couponList) {
        	AfUserCouponVo couponVo = getUserCouponVo(afUserCouponDto);
        	couponVoList.add(couponVo);
		}
        Map<String, Object> data = new HashMap<String, Object>();
		data.put("couponList", couponVoList);
		resp.setResponseData(data);

        return resp;
	}
	private AfUserCouponVo getUserCouponVo(AfUserCouponDto afUserCouponDto){
		AfUserCouponVo couponVo = new AfUserCouponVo();
		couponVo.setRid(afUserCouponDto.getRid());
		couponVo.setAmount(afUserCouponDto.getAmount());
		couponVo.setGmtEnd(afUserCouponDto.getGmtEnd());
		couponVo.setGmtStart(afUserCouponDto.getGmtStart());
		couponVo.setLimitAmount(afUserCouponDto.getLimitAmount());
		couponVo.setName(afUserCouponDto.getName());
		couponVo.setStatus(afUserCouponDto.getStatus());
		couponVo.setUseRule(afUserCouponDto.getUseRule());
		couponVo.setType(afUserCouponDto.getType());
		return couponVo;
	}
}
