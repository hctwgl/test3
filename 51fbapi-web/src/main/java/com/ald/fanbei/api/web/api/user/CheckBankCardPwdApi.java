package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAppealLogDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo.AfUserAppealLogStatusEnum;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 验证用户在绑定银行卡时是否需要设置支付密码
 * @author chefeipeng
 */

@Component("checkBankCardPwdApi")
public class CheckBankCardPwdApi implements ApiHandle {

	@Resource
	SmsUtil smsUtil;
	
	@Resource
	AfUserAppealLogDao afUserAppealLogDao;
	
	@Resource
	AfUserService afUserService;

	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Long userId = context.getUserId();
		if(StringUtil.isEmpty(userId.toString())){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if(null != afUserAccountDo){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
		}else if(StringUtil.isEmpty(afUserAccountDo.getPassword())){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}else{
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		}
	}

}
