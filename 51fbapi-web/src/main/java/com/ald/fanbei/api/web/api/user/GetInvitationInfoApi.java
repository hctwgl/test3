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
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月17日上午10:48:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getInvitationInfoApi")
public class GetInvitationInfoApi implements ApiHandle {
	@Resource
	AfUserService afUserService;
	@Resource
	AfResourceService afResourceService;

	@Resource
	AfUserCouponService afUserCouponService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		List<Object> recommendList = new ArrayList<Object>();
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(UserCouponSource.INVITE.getCode());
		if (afResourceDo == null) {
			throw new FanbeiException("resource id is invalid", FanbeiExceptionCode.PARAM_ERROR);

		}
		
		AfUserDo userDo = afUserService.getUserById(userId);

		BigDecimal  amount = afUserCouponService.getUserCouponByInvite(userId);

		List<AfUserInvitationDto> list = afUserService.getRecommendUserByRecommendId(userId);
		Map<String, Object> invitationInfo = new HashMap<String, Object>();
		invitationInfo.put("rule", afResourceDo.getDescription());
		invitationInfo.put("invitationNum", list.size());
		invitationInfo.put("invitationCode", userDo.getRecommendCode());
		invitationInfo.put("amount", amount);
		
		invitationInfo.put("shareUrl", "http://");

		for (AfUserInvitationDto invitationDto : list) {
			recommendList.add(mapWithInvitationDto(invitationDto));
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("recommendList", recommendList);
		data.put("invitationInfo", invitationInfo);
		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> mapWithInvitationDto(AfUserInvitationDto invitationDto) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", invitationDto.getRid());
		data.put("userName", invitationDto.getUserName());
		data.put("timeStamp", invitationDto.getGmtCreate());
		data.put("reward", invitationDto.getAmount());
		data.put("staus", invitationDto.getRealName().length()>0?"T":"F");
		return data;

	}

}
