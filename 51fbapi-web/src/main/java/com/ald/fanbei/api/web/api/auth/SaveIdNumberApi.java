/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
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
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	RiskUtil riskUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();

		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
		if (idNumberDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
		} else {
			AfUserDo afUserDo = afUserService.getUserById(userId);


			AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
			

			if (StringUtil.isBlank(accountDo.getOpenId())) {
				RiskRespBo riskResp = riskUtil.register(idNumberDo.getUserId() + "", idNumberDo.getName(), accountDo.getMobile(), idNumberDo.getCitizenId(), accountDo.getEmail(),
						accountDo.getAlipayAccount(), accountDo.getAddress());
				if(!riskResp.isSuccess()){
          			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
          		}
			} else {
				RiskRespBo riskResp = riskUtil.modify(idNumberDo.getUserId() + "", idNumberDo.getName(), accountDo.getMobile(), idNumberDo.getCitizenId(), accountDo.getEmail(),
						accountDo.getAlipayAccount(), accountDo.getAddress(), accountDo.getOpenId());
				if(!riskResp.isSuccess()){
          			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
          		}
			}

			
			afUserDo.setRealName(idNumberDo.getName());
			afUserService.updateUser(afUserDo);
			
			accountDo.setRealName(idNumberDo.getName());
			accountDo.setIdNumber(idNumberDo.getCitizenId());
			afUserAccountService.updateUserAccount(accountDo);


			return resp;
		}

	}

	public AfIdNumberDo idNumberDoWithIdNumberInfo(String address, String citizenId, String gender, String nation, String name, String validDateBegin, String validDateEnd,
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
