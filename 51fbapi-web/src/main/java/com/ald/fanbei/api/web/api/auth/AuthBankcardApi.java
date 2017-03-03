package com.ald.fanbei.api.web.api.auth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
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
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：银行卡认证（添加银行卡）,用户在完成人脸识别之后可以绑定银行卡，绑定银行卡时需要调用第三方认证，直接调用支付通道（融都开发那套）相关接口
 *@author chenjinhu 2017年2月17日 下午4:16:29
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authBankcardApi")
public class AuthBankcardApi implements ApiHandle {

	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfAuthYdService afAuthYdService;
	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String cardNumber = ObjectUtils.toString(requestDataVo.getParams().get("cardNumber"));
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		String isMain = ObjectUtils.toString(requestDataVo.getParams().get("isMain"),"N");
		String bankCode = ObjectUtils.toString(requestDataVo.getParams().get("bankCode"));
		
		AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
		if(null ==auth||YesNoStatus.NO.getCode().equals(auth.getFacesStatus())){
			throw new FanbeiException("user face auth error", FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
		}
		AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(context.getUserId());
		UpsAuthSignRespBo upsResult = UpsUtil.authSign(context.getUserId()+"",userAccount.getRealName(), mobile, userAccount.getIdNumber(), cardNumber, "02",bankCode);
		
		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_ERROR);
		}
		
		//TODO 新建卡
		AfUserBankcardDo bankDo = getUserBankcardDo(upsResult.getBankCode(),"", cardNumber, mobile, context.getUserId(),isMain);
		afUserBankcardDao.addUserBankcard(bankDo);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("bankId", bankDo.getRid());
		map.put("tradeNo", upsResult.getOrderNo());
		resp.setResponseData(map);
		return resp;
	}

	private AfUserBankcardDo getUserBankcardDo(String bankCode,String bankName,String cardNumber,String mobile,Long userId,String isMain){
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
