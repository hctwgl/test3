/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @类描述：
 * @author suweili 2017年2月16日下午3:55:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("setQuickFirstPwdApi")
public class SetQuickFirstPwdApi implements ApiHandle {
	@Resource
	AfUserService afUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = context.getUserName();
       
		String pwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"));
		String recommendCode = ObjectUtils.toString(requestDataVo.getParams().get("recommendCode"), null);//邀请码
	        if(StringUtils.isBlank(pwd)){
	        	logger.error("pwd  can't be empty");
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	        }
	        
	        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
	        if(afUserDo == null){
	        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
	        }
			if (StringUtil.isNotEmpty(recommendCode)) {
				AfUserDo recommendUserDo = afUserService.getUserByRecommendCode(recommendCode);
				if (recommendUserDo == null) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CODE_NOT_EXIST);
				} else {
					// 写入邀请人邀请码
					afUserDo.setRecommendId(recommendUserDo.getRid());
				}

			}
	        String salt = UserUtil.getSalt();
	        String password = UserUtil.getPassword(pwd, salt);

	        afUserDo.setSalt(salt);
	        afUserDo.setPassword(password);
	        afUserDo.setFailCount(0);
	        afUserService.updateUser(afUserDo);
	        return resp;
	}

}
