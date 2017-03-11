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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
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
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}

		List<Object> recommendList = new ArrayList<Object>();
		Map<String, Object> params = requestDataVo.getParams();
		Integer pageNum = NumberUtil.objToIntDefault(ObjectUtils.toString(params.get("pageNum"), "").toString(), 1);

		// 获取邀请规则链接地址
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(AfResourceType.INVITE.getCode());
		if (afResourceDo == null) {
			throw new FanbeiException("resource id is invalid", FanbeiExceptionCode.PARAM_ERROR);

		}
		// 获取邀请分享地址
		AfResourceDo resourceCodeDo = afResourceService
				.getSingleResourceBytype(AfResourceType.ShareInviteCode.getCode());

		AfUserDo userDo = afUserService.getUserById(userId);
		AfUserAccountLogDo userAccountLogDo = new AfUserAccountLogDo();
		userAccountLogDo.setUserId(userId);
		userAccountLogDo.setType(UserAccountLogType.AUTHNAME.getCode());
		BigDecimal amount = new BigDecimal(0);
		if(userAccountLogDo !=null ){
			BigDecimal amountTem= afUserAccountLogDao.getUserAmountByType(userAccountLogDo);
			if( amountTem !=null){
				amount = amountTem;
			}
		
		}
		
		
		Map<String, Object> data = new HashMap<String, Object>();

		List<AfUserInvitationDto> list = afUserService.getRecommendUserByRecommendId(userId, (pageNum - 1) * 20,
				pageNum * 20);
		if (pageNum == 1) {
			Map<String, Object> invitationInfo = new HashMap<String, Object>();
			invitationInfo.put("rulesUrl",
					ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + afResourceDo.getValue());
			invitationInfo.put("invitationNum", list.size());
			invitationInfo.put("invitationCode", userDo.getRecommendCode() == null ? "" : userDo.getRecommendCode());
			invitationInfo.put("amount", amount);
			// 邀请分享地址为配置地址+“?userName=”+注册手机号
			invitationInfo.put("shareUrl",ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + resourceCodeDo.getValue() + "?userName=" + context.getUserName());
			data.put("invitationInfo", invitationInfo);
		}
		for (AfUserInvitationDto invitationDto : list) {
			recommendList.add(mapWithInvitationDto(invitationDto));
		}

		data.put("recommendList", recommendList);
		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> mapWithInvitationDto(AfUserInvitationDto invitationDto) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", invitationDto.getRid());
		data.put("userName", invitationDto.getUserName());
		data.put("timeStamp", invitationDto.getGmtCreate());
		data.put("reward", invitationDto.getAmount());
		data.put("staus", StringUtils.equals(invitationDto.getRealnameStatus(), YesNoStatus.YES.getCode())?"T":"F" );
		return data;

	}

}
