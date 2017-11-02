/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月17日下午5:19:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("setPayPwdApi")
public class SetPayPwdApi implements ApiHandle {
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		String oldPwd = ObjectUtils.toString(params.get("oldPwd"), "").toString();
		String idNumber1 = ObjectUtils.toString(params.get("idNumber"), "").toString();
		String type = ObjectUtils.toString(params.get("type"), "").toString();
		String newPwd = ObjectUtils.toString(params.get("newPwd"), "").toString();
		if ((StringUtils.isEmpty(oldPwd) && StringUtils.isEmpty(idNumber1)) || StringUtils.isEmpty(type)
				|| StringUtils.isEmpty(newPwd)) {
			throw new FanbeiException("(newPwd or type or( oldPwd and idNumber) ) is  empty",
					FanbeiExceptionCode.PARAM_ERROR);
		}
		String idNumber = Base64.decodeToString(idNumber1);

		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (afUserAccountDo == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);

		}
		if(!StringUtils.equals(type, "S")&&!StringUtils.equals(type, "C")){
			throw new FanbeiException("type is error", FanbeiExceptionCode.PARAM_ERROR);
		}
		
		switch (type) {
		case "S":
			if(!StringUtils.equals(idNumber, afUserAccountDo.getIdNumber())){
				throw new FanbeiException("idNumber id is invalid",
						FanbeiExceptionCode.USER_ACCOUNT_IDNUMBER_INVALID_ERROR);
			}
			
			break;
		case "C":
			String inputOldPwd = UserUtil.getPassword(oldPwd, afUserAccountDo.getSalt());
			if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(),
						FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
			break;

		default:
			

			break;
		}
		
		String salt = UserUtil.getSalt();
		String password = UserUtil.getPassword(newPwd, salt);
		AfUserAccountDo nAccountDo = new AfUserAccountDo();
		nAccountDo.setRid(afUserAccountDo.getRid());
		nAccountDo.setSalt(salt);
		nAccountDo.setUserId(userId);
		nAccountDo.setPassword(password);

		if (afUserAccountService.updateUserAccount(nAccountDo) > 0) {
			return resp;
		}
		throw new FanbeiException("set password is failed", FanbeiExceptionCode.FAILED);

	}

}
