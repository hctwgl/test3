package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.SubmitBindBankcardParam;

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
				AfUserAccountDo userAccForUpdate = new AfUserAccountDo();
				
				if(userAccDB.getPassword() == null) { //支付密码为空，则此次请求需设置支付密码
					if(StringUtils.isEmpty(param.payPwd)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_PAY_PWD_MISS);
					} else {
						String salt = UserUtil.getSalt();
						String newPwd = UserUtil.getPassword(param.payPwd, salt);
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setSalt(salt);
						userAccForUpdate.setPassword(newPwd);
					}
				}
				
				if(userAccDB.getRealName() == null) { //真实姓名为空，则此次请求需存入身份证信息
					if(StringUtils.isEmpty(param.realName)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_REALINFO_MISS);
					} else {
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setRealName(param.realName);
						userAccForUpdate.setIdNumber(param.idNumber);
					}
				}
				
				if(userAccForUpdate.getUserId() != null) { // 可选更新用户账户信息
					afUserAccountService.updateUserAccount(userAccForUpdate);
				}
				
				// 设置卡状态为可用
				bank.setStatus(BankcardStatus.BIND.getCode());
				afUserBankcardService.updateUserBankcard(bank);
				
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
