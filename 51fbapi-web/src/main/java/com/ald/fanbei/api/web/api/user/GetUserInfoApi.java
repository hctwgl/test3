package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:48:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getUserInfoApi")
public class GetUserInfoApi implements ApiHandle {
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfUserDo userDo = afUserService.getUserById(userId);
		AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserVo vo = parseUserInfoToUserVo(userDo, userAccountDo, userAuthDo);
		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
		if(idNumberDo == null){
			vo.setIsUploadImage("N");
		}else if (StringUtils.isNotBlank(idNumberDo.getIdFrontUrl()) && StringUtils.isNotBlank(idNumberDo.getIdBehindUrl()) ) {
			vo.setIsUploadImage("Y");
		}else {
			vo.setIsUploadImage("N");
		}
		
	 int bankCardBind = afUserBankcardService.getUserBankcardCountByUserId(userId);
		vo.setBindCard(bankCardBind+"");
		resp.setResponseData(vo);
		return resp;
	}
	
	private AfUserVo parseUserInfoToUserVo(AfUserDo userDo, AfUserAccountDo userAccountDo, AfUserAuthDo userAuthDo) {
		AfUserVo userVo = new AfUserVo();
		userVo.setAvatar(userDo.getAvatar());
		userVo.setNick(userDo.getNick());
		userVo.setMobile(userDo.getMobile());
		userVo.setEmail(userDo.getEmail());
		userVo.setProvince(userDo.getProvince());
		userVo.setCity(userDo.getCity());
		userVo.setCounty(userDo.getCounty());
		userVo.setAddress(userDo.getAddress());
		
		String idNumber = Base64.encodeString(userAccountDo.getIdNumber());
		String realName = userDo.getRealName();
		userVo.setRealnameStatus(userAuthDo.getRealnameStatus());
		userVo.setIdNumber(StringUtils.isBlank(idNumber) ? "" : idNumber);
		userVo.setRealName(StringUtils.isBlank(realName) ? "" : realName);
		userVo.setBankCardStatus(userAuthDo.getBankcardStatus());
		userVo.setFaceStatus(userAuthDo.getFacesStatus());
		userVo.setRiskStatus(userAuthDo.getRiskStatus());
		
		return userVo;
	}
	
}
