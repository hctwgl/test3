package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAuthYdDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 *@类现描述：人脸识别认证结果，app端做完人脸识别之后把结果发给服务端
 *
 *@author chenjinhu 2017年2月15日 下午3:34:46
 *@version 2.0 重构版本
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authFaceApi")
public class AuthFaceApi implements ApiHandle {
	
	private final static String  RESULT_AUTH_TRUE = "T";
	
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfAuthYdService afAuthYdService;
	@Resource
	private AfAuthTdService afAuthTdService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private RiskUtil riskUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String result = (String)requestDataVo.getParams().get("result");
		String resultAuth = (String)requestDataVo.getParams().get("resultAuth");
		String idNumber = (String)requestDataVo.getParams().get("idNumber");
		String realName = (String)requestDataVo.getParams().get("realName");
		
		if(StringUtil.isBlank(idNumber) || StringUtil.isBlank(realName)){
			throw new FanbeiException("authRealnameApi param error",FanbeiExceptionCode.PARAM_ERROR);
		}
		
		idNumber = new String(Base64.decode(idNumber));
		
        AfUserAccountDto oldAccount = afUserAccountService.getUserAndAccountByUserId(context.getUserId());
		
		//实名信息，如果与之前的实名信息不一致时报错
        if(StringUtil.isNotBlank(oldAccount.getIdNumber())&&StringUtil.isNotBlank(oldAccount.getRealName())){
        	if(!StringUtil.equals(idNumber, oldAccount.getIdNumber())||!StringUtil.equals(realName, oldAccount.getRealName())){
                throw new FanbeiException("user realname auth error",FanbeiExceptionCode.USER_REALNAME_AUTH_ERROR);
            }
        }
        
        //第一次实名认证
      	if(StringUtil.isBlank(oldAccount.getIdNumber())&&StringUtil.isBlank(oldAccount.getRealName())){
      		//同步实名信息到融都
      		try {
      			RiskRespBo riskResp = riskUtil.register(oldAccount.getUserId()+"", realName, oldAccount.getMobile(), idNumber, 
          				oldAccount.getEmail(), oldAccount.getAlipayAccount(), oldAccount.getAddress());
          		if(!riskResp.isSuccess()){
          			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
          		}
			} catch (Exception e) {
				//执行修改操作
				RiskRespBo riskResp = riskUtil.modify(oldAccount.getUserId()+"", realName, oldAccount.getMobile(), idNumber,
						oldAccount.getEmail(), oldAccount.getAlipayAccount(), oldAccount.getAddress(), "");
				if(!riskResp.isSuccess()){
          			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
          		}
			}
      		
      	}
        
		//TODO 更新user_account中身份证号和真实姓名
		AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
		afUserAccountDo.setUserId(context.getUserId());
		afUserAccountDo.setRealName(realName);
		afUserAccountDo.setIdNumber(idNumber);
		afUserAccountService.updateUserAccount(afUserAccountDo);
		
		AfUserDo afUserDo = new AfUserDo();
		afUserDo.setRid(context.getUserId());
		afUserDo.setRealName(realName);
		afUserService.updateUser(afUserDo);
		
		//人脸识别
		//新增有盾数据
		AfAuthYdDo afAuthYdDo = new AfAuthYdDo(); 
		afAuthYdDo.setType("FACE_APP");
		afAuthYdDo.setUserId(context.getUserId());
		afAuthYdDo.setAuthParam("");
		afAuthYdDo.setAuthResult(result);
		afAuthYdService.addAuthYd(afAuthYdDo);
		if(StringUtil.equals(resultAuth, RESULT_AUTH_TRUE)){
			AfUserAuthDo userAuth = new AfUserAuthDo();
			userAuth.setUserId(context.getUserId());
			userAuth.setYdStatus(YesNoStatus.YES.getCode());
			userAuth.setFacesStatus(YesNoStatus.YES.getCode());
			userAuth.setSimilarDegree(new BigDecimal((String)JSONObject.parseObject(result).get("be_idcard")));
			afUserAuthService.updateUserAuth(userAuth);
		}
		
		return resp;
	}

}
