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
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：修改用户相关信息接口
 * @author Xiaotianjian 2017年1月19日下午1:48:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("updateUserInfoApi")
public class UpdateUserInfoApi implements ApiHandle {

	@Resource
	AfUserService afUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		String nick = ObjectUtils.toString(params.get("nick"), "").toString();
		String avatar = ObjectUtils.toString(params.get("avatar"), "").toString();
		String province = ObjectUtils.toString(params.get("province"), "").toString();
		String city = ObjectUtils.toString(params.get("city"), "").toString();
		String county = ObjectUtils.toString(params.get("county"), "").toString();

		if (StringUtils.isEmpty(nick) && StringUtils.isEmpty(avatar) && StringUtils.isEmpty(province)
				&& StringUtils.isEmpty(city) && StringUtils.isEmpty(county)) {
			throw new FanbeiException("(nick or avata or province or city or county) is  empty", FanbeiExceptionCode.PARAM_ERROR);
		}

		AfUserDo afUserDo = new AfUserDo();
		afUserDo.setRid(userId);

		afUserDo.setNick(StringUtils.isNotBlank(nick) ? nick : null);

		afUserDo.setAvatar(StringUtils.isNotBlank(avatar) ? avatar : null);
		afUserDo.setProvince(StringUtils.isNotBlank(province) ? province : null);
		afUserDo.setCity(StringUtils.isNotBlank(city) ? city : null);
		afUserDo.setCounty(StringUtils.isNotBlank(county)?county:null);
		if (afUserService.updateUser(afUserDo) > 0) {
			return resp;
		} else {
			throw new FanbeiException("update User info failed", FanbeiExceptionCode.SYSTEM_ERROR);
		}
	}

}
