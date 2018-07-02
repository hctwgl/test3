package com.ald.fanbei.api.web.api.auth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyBindBankcardParam;

/**
 *@类现描述：申请绑卡银行短信
 *@author ZJF 2018-04-11
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applySuperBindBankcardApi")
@Validator("applySuperBindBankcardParam")
public class ApplySuperBindBankcardApi implements ApiHandle {

	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfAuthYdService afAuthYdService;
	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Resource
	UpsUtil upsUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		ApplyBindBankcardParam param = (ApplyBindBankcardParam) requestDataVo.getParamObj();
		
		Long userId = context.getUserId();

		if(afUserBankcardDao.getUserBankByCardNo(param.cardNumber)>0){// 判断该卡是否已被绑定
			throw new FanbeiException("user bankcard exist error", FanbeiExceptionCode.USER_BANKCARD_EXIST_ERROR);
		}
		
		if(param.realname == null) {
			AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
			param.realname = userAccount.getRealName();
			param.idNumber = userAccount.getIdNumber();
		}
		
		// TODO 调用  PayOrderV1Api
		
		String isMain = YesNoStatus.NO.getCode();
		AfUserBankcardDo bank = afUserBankcardDao.getUserMainBankcardByUserId(userId);
		if(null == bank){ isMain = YesNoStatus.YES.getCode(); }
		
		AfUserBankcardDo bankDo = genUserBankcardDo(param.bankCode, param.bankName, param.cardNumber, param.mobile, userId, isMain);
		afUserBankcardDao.addUserBankcard(bankDo);

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("bankId", bankDo.getRid());
		resp.setResponseData(map);
		return resp;
	}

	private AfUserBankcardDo genUserBankcardDo(String bankCode,String bankName,String cardNumber,String mobile,Long userId,String isMain){
		AfUserBankcardDo bank = new AfUserBankcardDo();
		bank.setBankCode(bankCode);
		bank.setBankName(bankName);
		bank.setCardNumber(cardNumber);
		bank.setIsMain(isMain);
		bank.setMobile(mobile);
		bank.setStatus(BankcardStatus.NEW.getCode());
		bank.setUserId(userId);
		return bank;
	}





}
