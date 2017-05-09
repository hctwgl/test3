/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年5月9日下午6:05:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitIdNumberInfoApi")
public class SubmitIdNumberInfoApi implements ApiHandle {

	@Resource
	AfIdNumberService afIdNumberService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		Map<String, Object> params =requestDataVo.getParams();
		String type = ObjectUtils.toString(params.get("type"), "") ;
		if(ApiCallType.findRoleTypeByCode(type)==null){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);
		}
		if(StringUtils.equals(type, ApiCallType.YITU_CARD.getCode())){
			String address = ObjectUtils.toString(params.get("address"), "") ;
			String citizenId = ObjectUtils.toString(params.get("citizenId"), "") ;
			String gender = ObjectUtils.toString(params.get("gender"), "") ;
			String nation = ObjectUtils.toString(params.get("nation"), "") ;
			String name = ObjectUtils.toString(params.get("name"), "") ;
			String validDateBegin = ObjectUtils.toString(params.get("validDateBegin"), "") ;
			String validDateEnd = ObjectUtils.toString(params.get("validDateEnd"), "") ;
			String birthday = ObjectUtils.toString(params.get("birthday"), "") ;
			String agency = ObjectUtils.toString(params.get("agency"), "") ;
			String idFrontUrl = ObjectUtils.toString(params.get("idFrontUrl"), "") ;
			String idBehindUrl = ObjectUtils.toString(params.get("idBehindUrl"), "") ;
			
			AfIdNumberDo afIdNumberDo = new AfIdNumberDo();
			afIdNumberDo.setUserId(userId);
			afIdNumberDo.setAddress(address);
			afIdNumberDo.setAgency(agency);
			afIdNumberDo.setBirthday(birthday);
			afIdNumberDo.setCitizenId(citizenId);
			afIdNumberDo.setGender(gender);
			afIdNumberDo.setIdBehindUrl(idBehindUrl);
			afIdNumberDo.setIdFrontUrl(idFrontUrl);
			afIdNumberDo.setName(name);
			afIdNumberDo.setNation(nation);
			afIdNumberDo.setValidDateBegin(validDateBegin);
			afIdNumberDo.setValidDateEnd(validDateEnd);
			
			AfIdNumberDo numberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
			Integer count =0;
			if(numberDo==null){
				count = afIdNumberService.addIdNumber(afIdNumberDo);
			}else{
				afIdNumberDo.setRid(numberDo.getRid());
				count = afIdNumberService.updateIdNumber(afIdNumberDo);
			}
			if(count>0){
				return resp;
			}
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

		}else if(StringUtils.equals(type, ApiCallType.YITU_FACE.getCode())){
			String faceUrl = ObjectUtils.toString(params.get("faceUrl"), "") ;

			AfIdNumberDo afIdNumberDo = new AfIdNumberDo();
			afIdNumberDo.setUserId(userId);
			afIdNumberDo.setFaceUrl(faceUrl);
			Integer count =0;
			count = afIdNumberService.updateIdNumber(afIdNumberDo);
			if(count>0){
				return resp;

			}
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);


		}else{
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);

		}
		
		
	}

}
