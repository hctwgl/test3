package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.FacePlusCardRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.yitu.FacePlusUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年7月24日上午11:42:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("updateIdCardForFacePlusApi")
public class UpdateIdCardForFacePlusApi implements ApiHandle {

	@Resource
	FacePlusUtil facePlusUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserApiCallLimitService afUserApiCallLimitService;
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		String front = (String) requestDataVo.getParams().get("front");
		String back = (String) requestDataVo.getParams().get("back");
		if (StringUtils.isBlank(front) || StringUtils.isBlank(back)) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}


		AfUserApiCallLimitDo callLimitDo = afUserApiCallLimitService.selectByUserIdAndType(userId, ApiCallType.FACE_PLUS_CARD.getCode());
		if (callLimitDo == null) {
			callLimitDo = new AfUserApiCallLimitDo();
			callLimitDo.setType(ApiCallType.YITU_CARD.getCode());
			callLimitDo.setUserId(userId);
			afUserApiCallLimitService.addUserApiCallLimit(callLimitDo);
		}
		if (callLimitDo.getDisableStatus().equals("Y")) {
			throw new FanbeiException(FanbeiExceptionCode.API_CALL_NUM_OVERFLOW);
		}
		FacePlusCardRespBo bo = null;
		try {
			bo = facePlusUtil.checkCard(front, back);
			afUserApiCallLimitService.addVisitNum(context.getUserId(), ApiCallType.FACE_PLUS_CARD.getCode());
			Integer maxNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, ApiCallType.FACE_PLUS_CARD.getCode()).getValue(), 0);
			if (maxNum - callLimitDo.getCallNum() <= 0) {
				callLimitDo.setDisableStatus("Y");
				afUserApiCallLimitService.updateUserApiCallLimit(callLimitDo);
			}

		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}
		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
		if (accountDo.getRealName().equals(bo.getName()) && accountDo.getIdNumber().equals(bo.getId_card_number())) {
			// 初始化用户身份证表信息
			AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
			if (idNumberDo == null) {
				idNumberDo = idNumberDoWithIdNumberInfo(bo.getAddress(), bo.getId_card_number(), bo.getGender(),
						bo.getRace(), bo.getName(), bo.getValid_date_begin(),
						bo.getValid_date_end(), bo.getRealBirthDay(), bo.getIssued_by(), front, back, idNumberDo);
				idNumberDo.setUserId(userId);
				if (afIdNumberService.addIdNumber(idNumberDo) > 0) {
					return resp;
				}
			} else {
				idNumberDo = idNumberDoWithIdNumberInfo(bo.getAddress(), bo.getId_card_number(), bo.getGender(),
						bo.getRace(), bo.getName(), bo.getValid_date_begin(),
						bo.getValid_date_end(), bo.getRealBirthDay(), bo.getIssued_by(), front, back, idNumberDo);
				if (afIdNumberService.updateIdNumber(idNumberDo) > 0) {
					return resp;
				}
			}

			riskUtil.modify(idNumberDo.getUserId() + "", idNumberDo.getName(), accountDo.getMobile(), idNumberDo.getCitizenId(), accountDo.getEmail(), accountDo.getAlipayAccount(),
					accountDo.getAddress(), accountDo.getOpenId());
		} else {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_INFO_ATYPISM_ERROR);
		}

		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

	private AfIdNumberDo idNumberDoWithIdNumberInfo(String address, String citizenId, String gender, String nation, String name, String validDateBegin, String validDateEnd,
			String birthday, String agency, String idFrontUrl, String idBehindUrl, AfIdNumberDo idNumberDo) {
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
