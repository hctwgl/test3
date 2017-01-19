/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:48:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("updateInfoApi")
public class UpdateInfoApi implements ApiHandle {
	
	@Resource
	AfUserService afUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid",FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		String nickName = ObjectUtils.toString(params.get("nickname"), "").toString() ;
		String avata = ObjectUtils.toString(params.get("avata"), "").toString() ;
		Long birthdayTime = NumberUtil.objToLongDefault(params.get("birthday"), null) ;
		Integer gender = NumberUtil.objToIntDefault(params.get("gender"), null);
		Integer isUndisturbed = NumberUtil.objToIntDefault(params.get("isUndisturbed"), null);
		String undisturbedStartTime = ObjectUtils.toString(params.get("undisturbedStartTime"), "").toString();
		String undisturbedEndTime = ObjectUtils.toString(params.get("undisturbedEndTime"), "").toString();
		String mobile = ObjectUtils.toString(params.get("mobile"), "").toString() ;

		AfUserDo afUserDo = new AfUserDo();
		afUserDo.setRid(userId);
		if (StringUtils.isEmpty(mobile) &&StringUtils.isEmpty(nickName) && StringUtils.isEmpty(avata) 
				&& birthdayTime == null && gender == null && isUndisturbed == null && StringUtils.isEmpty(undisturbedStartTime) && StringUtils.isEmpty(undisturbedEndTime)) {
			throw new FanbeiException("params is error",FanbeiExceptionCode.PARAM_ERROR);
		}
		
		afUserDo.setMobile(StringUtils.isNotBlank(mobile)?mobile:null);
		afUserDo.setNick(StringUtils.isNotBlank(nickName)?nickName:null);

		afUserDo.setAvata(StringUtils.isNotBlank(avata)?avata:null);
		
		if (afUserService.updateUser(afUserDo) > 0) {
			return resp;
		} else {
			throw new FanbeiException("update User info failed",FanbeiExceptionCode.SYSTEM_ERROR);
		}
	}

}
