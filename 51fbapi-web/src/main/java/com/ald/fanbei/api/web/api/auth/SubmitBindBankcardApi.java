package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankCardType;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.SubmitBindBankcardParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *@类现描述：提交绑卡
 *@author ZJF 2018年4月09日
 *@since 4.1.2
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitBindBankcardApi")
@Validator("submitBindBankcardParam")
public class SubmitBindBankcardApi implements ApiHandle {

	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Resource
	private UpsUtil upsUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, final FanbeiContext context, final HttpServletRequest request) {
		final SubmitBindBankcardParam param = (SubmitBindBankcardParam) requestDataVo.getParamObj();

		final AfUserBankcardDo bank = afUserBankcardService.getUserBankcardById(param.bankCardId);
		int res = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				AfUserAccountDo userAccDB = afUserAccountService.getUserAccountByUserId(context.getUserId());
				AfUserDo userDo=afUserService.getUserById(context.getUserId());
				AfUserAccountDo userAccForUpdate = new AfUserAccountDo();
                AfUserAuthDo userAuthUpdate=new AfUserAuthDo();
                AfUserDo userUpdate=new AfUserDo();
				if(StringUtil.isEmpty(userAccDB.getPassword())) { //支付密码为空，则此次请求需设置支付密码
					if(StringUtils.isEmpty(param.newPassword)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_PAY_PWD_MISS);
					} else {
						String salt = UserUtil.getSalt();
						String newPwd = UserUtil.getPassword(param.newPassword, salt);
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setSalt(salt);
						userAccForUpdate.setPassword(newPwd);
					}
				}
				if(StringUtil.isEmpty(userDo.getRealName())){
					userUpdate.setRid(context.getUserId());
					userUpdate.setRealName(param.realname);
					afUserService.updateUser(userUpdate);
				}
				if(StringUtil.isEmpty(userAccDB.getIdNumber())) { //真实姓名为空，则此次请求需存入身份证信息
					if(StringUtils.isEmpty(param.realname)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_REALINFO_MISS);
					} else {
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setRealName(param.realname);
						userAccForUpdate.setIdNumber(param.idNumber);
					}
				}
				userAccForUpdate.setBindCard(YesNoStatus.YES.getCode());
				if(userAccForUpdate.getUserId() != null) { // 可选更新用户账户信息
					afUserAccountService.updateUserAccount(userAccForUpdate);
				}

				// 设置卡状态为可用
				bank.setStatus(BankcardStatus.BIND.getCode());
				afUserBankcardService.updateUserBankcard(bank);

				//设置用户绑卡状态
				if(!BankCardType.CREDIT.getCode().equals(bank.getCardType())) {
					userAuthUpdate.setUserId(userAccDB.getUserId());
					userAuthUpdate.setBankcardStatus(YesNoStatus.YES.getCode());
					afUserAuthService.updateUserAuth(userAuthUpdate);
				}
				UpsAuthSignValidRespBo upsResult = upsUtil.authSignValid(context.getUserId()+"", bank.getCardNumber(), param.smsCode, "02");
				if(!upsResult.isSuccess()){
					status.setRollbackOnly();
					return 1000; //UPS绑卡失败
				}
				
				return 1; //仅当返回1 才操作成功
			}
		});
        
        if(res == 1000) {
        	bank.setStatus(BankcardStatus.UNBIND.getCode());
			afUserBankcardService.updateUserBankcard(bank);
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
        }
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		return resp;
	}

}
