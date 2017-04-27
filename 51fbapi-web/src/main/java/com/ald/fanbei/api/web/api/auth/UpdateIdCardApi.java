package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.YituFaceCardRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.yitu.YituUtil;
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

@Component("updateIdCardApi")
public class UpdateIdCardApi implements ApiHandle {

	@Resource
	YituUtil yituUtil;
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

		AfUserApiCallLimitDo callLimitDo = afUserApiCallLimitService.selectByUserIdAndType(userId, ApiCallType.YITU_CARD.getCode());
		if (callLimitDo == null) {
			callLimitDo = new AfUserApiCallLimitDo();
			callLimitDo.setType(ApiCallType.YITU_CARD.getCode());
			callLimitDo.setUserId(userId);
			afUserApiCallLimitService.addUserApiCallLimit(callLimitDo);
		}
		if (callLimitDo.getDisableStatus().equals("Y")) {
			throw new FanbeiException(FanbeiExceptionCode.API_CALL_NUM_OVERFLOW);
		}
		YituFaceCardRespBo bo = null;
		try {
			bo = yituUtil.checkCard(front, back);
			afUserApiCallLimitService.addVisitNum(context.getUserId(), ApiCallType.YITU_CARD.getCode());
			Integer maxNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, ApiCallType.YITU_CARD.getCode()).getValue(), 0);
			if (maxNum - callLimitDo.getCallNum() <= 0) {
				callLimitDo.setDisableStatus("Y");
				afUserApiCallLimitService.updateUserApiCallLimit(callLimitDo);
			}

		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}
		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
		if (accountDo.getRealName().equals(bo.getIdcard_ocr_result().getName()) && accountDo.getIdNumber().equals(bo.getIdcard_ocr_result().getCitizen_id())) {
			// 初始化用户身份证表信息
			AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
			if (idNumberDo == null) {
				idNumberDo = idNumberDoWithIdNumberInfo(bo.getIdcard_ocr_result().getAddress(), bo.getIdcard_ocr_result().getCitizen_id(), bo.getIdcard_ocr_result().getGender(),
						bo.getIdcard_ocr_result().getNation(), bo.getIdcard_ocr_result().getName(), bo.getIdcard_ocr_result().getValid_date_begin(),
						bo.getIdcard_ocr_result().getValid_date_end(), bo.getIdcard_ocr_result().getBirthday(), bo.getIdcard_ocr_result().getAgency(), front, back, idNumberDo);
				idNumberDo.setUserId(userId);
				if (afIdNumberService.addIdNumber(idNumberDo) > 0) {
					return resp;
				}
			} else {
				idNumberDo = idNumberDoWithIdNumberInfo(bo.getIdcard_ocr_result().getAddress(), bo.getIdcard_ocr_result().getCitizen_id(), bo.getIdcard_ocr_result().getGender(),
						bo.getIdcard_ocr_result().getNation(), bo.getIdcard_ocr_result().getName(), bo.getIdcard_ocr_result().getValid_date_begin(),
						bo.getIdcard_ocr_result().getValid_date_end(), bo.getIdcard_ocr_result().getBirthday(), bo.getIdcard_ocr_result().getAgency(), front, back, idNumberDo);
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
