/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月16日下午3:55:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeLoginPwdApi")
public class ChangeLoginPwdApi implements ApiHandle {
	@Resource
	AfUserService afUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = context.getUserName();
       
		  String oldPwd = ObjectUtils.toString(requestDataVo.getParams().get("oldPwd"));
		  String newPwd = ObjectUtils.toString(requestDataVo.getParams().get("newPwd"));
  
	        if(StringUtils.isBlank(oldPwd)&&StringUtils.isBlank(newPwd)){
	        	logger.error("oldPwd or newPwd  can't be empty");
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	        }
	        
	        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
	        if(afUserDo == null){
	        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
	        }
	        
	      //check password
	        String inputOldPwd = UserUtil.getPassword(oldPwd, afUserDo.getSalt());
	        if(!StringUtils.equals(inputOldPwd, afUserDo.getPassword())){
	        	
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PASSWORD_OLD_ERROR);
	        }

	        String salt = UserUtil.getSalt();
	        String password = UserUtil.getPassword(newPwd, salt);

	        afUserDo.setSalt(salt);
	        afUserDo.setPassword(password);
	        afUserDo.setFailCount(0);
	        afUserService.updateUser(afUserDo);
	        return resp;
	}

}
