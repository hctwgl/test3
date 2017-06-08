/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年5月9日下午6:05:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitIdNumberInfoApi")
public class SubmitIdNumberInfoApi implements ApiHandle {
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		logger.info("进入");
		Map<String, Object> params = requestDataVo.getParams();
		String type = ObjectUtils.toString(params.get("type"), "");
		if (ApiCallType.findRoleTypeByCode(type) == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		if (StringUtils.equals(type, ApiCallType.YITU_CARD.getCode())) {
			String address = ObjectUtils.toString(params.get("address"), "");
			String citizenId = ObjectUtils.toString(params.get("citizenId"), "");
			String gender = ObjectUtils.toString(params.get("gender"), "");
			String nation = ObjectUtils.toString(params.get("nation"), "");
			String name = ObjectUtils.toString(params.get("name"), "");
			String validDateBegin = ObjectUtils.toString(params.get("validDateBegin"), "");
			String validDateEnd = ObjectUtils.toString(params.get("validDateEnd"), "");
			String birthday = ObjectUtils.toString(params.get("birthday"), "");
			String agency = ObjectUtils.toString(params.get("agency"), "");
			String idFrontUrl = ObjectUtils.toString(params.get("idFrontUrl"), "");
			String idBehindUrl = ObjectUtils.toString(params.get("idBehindUrl"), "");

			logger.info("准备查询");
			Integer c = afUserAccountService.getCountByIdNumer(citizenId, context.getUserId());
			logger.info("进入验证信息："+citizenId+";条数："+c);
			if (c > 0) {
				logger.error(FanbeiExceptionCode.USER_CARD_IS_EXIST.getErrorMsg());
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_CARD_IS_EXIST);
			}

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
			AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
			if (StringUtils.equals(auth.getBankcardStatus(), YesNoStatus.YES.getCode())) {
				AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
				if (accountDo != null) {
					if (StringUtils.isNotBlank(accountDo.getRealName())
							&& StringUtils.isNotBlank(accountDo.getIdNumber())) {
						if (!StringUtils.equals(accountDo.getRealName(), name)
								|| !StringUtils.equals(accountDo.getIdNumber(), citizenId)) {
							logger.error(FanbeiExceptionCode.USER_CARD_INFO_ATYPISM_ERROR.getErrorMsg());
							return new ApiHandleResponse(requestDataVo.getId(),
									FanbeiExceptionCode.USER_CARD_INFO_ATYPISM_ERROR);
						}
					}
				}
			}
			AfIdNumberDo numberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
			Integer count = 0;
			if (numberDo == null) {
				count = afIdNumberService.addIdNumber(afIdNumberDo);
			} else {
				afIdNumberDo.setRid(numberDo.getRid());
				count = afIdNumberService.updateIdNumber(afIdNumberDo);
			}

			if (count > 0) {

				return resp;
			}
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

		} else if (StringUtils.equals(type, ApiCallType.YITU_FACE.getCode())) {
			String faceUrl = ObjectUtils.toString(params.get("faceUrl"), "");
			AfIdNumberDo numberDo = afIdNumberService.selectUserIdNumberByUserId(userId);

			AfIdNumberDo afIdNumberDo = new AfIdNumberDo();
			afIdNumberDo.setFaceUrl(faceUrl);
			afIdNumberDo.setRid(numberDo.getRid());
			Integer count = 0;
			count = afIdNumberService.updateIdNumber(afIdNumberDo);
			logger.info("id number change" + count);
			if (count > 0) {
				AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
				auth.setFacesStatus(YesNoStatus.YES.getCode());
				auth.setYdStatus(YesNoStatus.YES.getCode());

				Double similarity = (Double) bizCacheUtil.getObject(Constants.CACHEKEY_YITU_FACE_SIMILARITY);
				if (similarity != null) {
					auth.setSimilarDegree(BigDecimal.valueOf(similarity));
					bizCacheUtil.delCache(Constants.CACHEKEY_YITU_FACE_SIMILARITY);
				}

				afUserAuthService.updateUserAuth(auth);

				AfUserDo afUserDo = new AfUserDo();
				afUserDo.setRid(userId);
				afUserDo.setRealName(numberDo.getName());
				afUserService.updateUser(afUserDo);

				logger.info("id number account realname=" + numberDo.getName());

				AfUserAccountDo account = new AfUserAccountDo();
				account.setRealName(numberDo.getName());
				account.setUserId(userId);
				account.setIdNumber(numberDo.getCitizenId());
				afUserAccountService.updateUserAccount(account);

				AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
				try {
					RiskRespBo riskResp = riskUtil.register(userId + "", numberDo.getName(), accountDo.getMobile(),
							numberDo.getCitizenId(), accountDo.getEmail(), accountDo.getAlipayAccount(),
							numberDo.getAddress());
					if (!riskResp.isSuccess()) {
						throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
					}
				} catch (Exception e) {
					RiskRespBo riskResp = riskUtil.modify(userId + "", numberDo.getName(), accountDo.getMobile(),
							numberDo.getCitizenId(), accountDo.getEmail(), accountDo.getAlipayAccount(),
							numberDo.getAddress(), accountDo.getOpenId());
					if (!riskResp.isSuccess()) {
						throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
					}
					logger.error("更新风控用户失败：" + accountDo.getUserId());
				}

				return resp;

			}
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

		} else {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

		}

	}

}
