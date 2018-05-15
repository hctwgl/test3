package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankCardType;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardTypeDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardTypeDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyBindBankcardParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *@类现描述：申请绑卡银行短信
 *@author ZJF 2018-04-11
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBindBankcardApi")
@Validator("applyBindBankcardParam")
public class ApplyBindBankcardApi implements ApiHandle {

	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfAuthYdService afAuthYdService;
	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfUserAuthService afUserAuthService;

	@Autowired
	private AfUserBankcardTypeDao afUserBankcardTypeDao;

	@Resource
	UpsUtil upsUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		ApplyBindBankcardParam param = (ApplyBindBankcardParam) requestDataVo.getParamObj();
		
		Long userId = context.getUserId();

		if(context.getAppVersion()>=415){
			if(param.cardType== null)
			{
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
		}

		if(afUserBankcardDao.getUserBankByCardNo(param.cardNumber)>0){// 判断该卡是否已被绑定
			throw new FanbeiException("user bankcard exist error", FanbeiExceptionCode.USER_BANKCARD_EXIST_ERROR);
		}
		
		if(param.idNumber == null) {
			AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
			param.realname = userAccount.getRealName();
			param.idNumber = userAccount.getIdNumber();
		}

		if(afUserAccountService.getCountByIdNumer(param.idNumber,userId)>0){
			return  new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_CARD_IS_EXIST);
		}

		//默认赋值为借记卡
		String cardType = "00";
		if(BankCardType.CREDIT.getCode().equals(param.cardType)) {//验证信用卡有效期格式
			String express = "\\d{2}/\\d{2}";
				Pattern pattern = Pattern.compile(express);
			Matcher matcher = pattern.matcher(param.validDate);
			if(!matcher.matches())
			{
				throw new FanbeiException("信用卡有效期格式错误，正确格式为MM/YY");
			}
			cardType = "01";
		}

		UpsAuthSignRespBo upsResult = upsUtil.authSign(userId.toString(), param.realname, param.mobile, param.idNumber, param.cardNumber, "02",
				param.bankCode,cardType,param.validDate,param.safeCode);
		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_ERROR);
		}else if(!"10".equals(upsResult.getNeedCode())){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_SMS_ERROR);
		}
		
		String isMain = YesNoStatus.NO.getCode();
		AfUserBankcardDo bank = afUserBankcardDao.getUserMainBankcardByUserId(userId);
		if(null == bank && cardType.equals("00")){ isMain = YesNoStatus.YES.getCode(); }
		
		AfUserBankcardDo bankDo = genUserBankcardDo(param.bankCode, param.bankName, param.cardNumber, param.mobile, userId, isMain);
		afUserBankcardDao.addUserBankcard(bankDo);

		if(context.getAppVersion()>=415) {
			//添加银行卡补充信息
			AfUserBankcardTypeDo afUserBankcardTypeDo = new AfUserBankcardTypeDo();
			afUserBankcardTypeDo.setType(param.cardType);
			if(BankCardType.CREDIT.getCode().equals(param.cardType)){
				afUserBankcardTypeDo.setSafeCode(param.safeCode);
				afUserBankcardTypeDo.setValidDate(param.validDate);
				afUserBankcardTypeDo.setUserBankcardId(bankDo.getRid());
			}
			afUserBankcardTypeDao.saveRecord(afUserBankcardTypeDo);
		}

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
