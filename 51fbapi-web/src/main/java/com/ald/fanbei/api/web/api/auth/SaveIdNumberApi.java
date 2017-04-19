/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：保存身份信息
 * 
 * @author suweili 2017年4月18日下午1:26:41
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("saveIdNumberApi")
public class SaveIdNumberApi implements ApiHandle {
	@Resource
	AfIdNumberService afIdNumberService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		String address = ObjectUtils.toString(requestDataVo.getParams().get("address"));
		String citizenId = ObjectUtils.toString(requestDataVo.getParams().get("idNumber"));
		String gender = ObjectUtils.toString(requestDataVo.getParams().get("gender"));
		String nation = ObjectUtils.toString(requestDataVo.getParams().get("nation"));
		String name = ObjectUtils.toString(requestDataVo.getParams().get("name"));
		String validDateBegin = ObjectUtils.toString(requestDataVo.getParams().get("validDateBegin"));
		String validDateEnd = ObjectUtils.toString(requestDataVo.getParams().get("validDateEnd"));
		String birthday = ObjectUtils.toString(requestDataVo.getParams().get("birthday"));
		String agency = ObjectUtils.toString(requestDataVo.getParams().get("agency"));
		String idFrontUrl = ObjectUtils.toString(requestDataVo.getParams().get("idFrontUrl"));
		String idBehindUrl = ObjectUtils.toString(requestDataVo.getParams().get("idBehindUrl"));

		if (StringUtils.isBlank(address) || StringUtils.isBlank(citizenId) || StringUtils.isBlank(gender)
				|| StringUtils.isBlank(nation) || StringUtils.isBlank(name) || StringUtils.isBlank(validDateBegin)
				|| StringUtils.isBlank(validDateEnd) || StringUtils.isBlank(birthday) || StringUtils.isBlank(agency)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
		if (idNumberDo == null) {
			idNumberDo = idNumberDoWithIdNumberInfo(address, citizenId, gender, nation, name, validDateBegin,
					validDateEnd, birthday, agency, idFrontUrl, idBehindUrl, idNumberDo);
			idNumberDo.setUserId(userId);
			if (afIdNumberService.addIdNumber(idNumberDo) > 0) {
				return resp;
			}
		} else {
			idNumberDo = idNumberDoWithIdNumberInfo(address, citizenId, gender, nation, name, validDateBegin,
					validDateEnd, birthday, agency, idFrontUrl, idBehindUrl, idNumberDo);
			if (afIdNumberService.updateIdNumber(idNumberDo) > 0) {
				return resp;
			}
		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

	}

	public AfIdNumberDo idNumberDoWithIdNumberInfo(String address, String citizenId, String gender, String nation,
			String name, String validDateBegin, String validDateEnd, String birthday, String agency, String idFrontUrl,
			String idBehindUrl, AfIdNumberDo idNumberDo) {
		if (idNumberDo == null) {
			idNumberDo = new AfIdNumberDo();
		}
		idNumberDo.setAddress(address);
		idNumberDo.setAgency(agency);
		idNumberDo.setBirthday(birthday);
		idNumberDo.setCitizenId(citizenId);
		idNumberDo.setGender(gender);
		idNumberDo.setIdBehindUrl(idBehindUrl);
		idNumberDo.setIdFrontUrl(idFrontUrl);
		idNumberDo.setName(name);
		idNumberDo.setNation(nation);
		idNumberDo.setValidDateBegin(validDateBegin);
		idNumberDo.setValidDateEnd(validDateEnd);

		return idNumberDo;
	}

}
