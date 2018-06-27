package com.ald.fanbei.api.web.h5.api.dsed;

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
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.SubmitBindBankcardParam;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *@类现描述：提交绑卡
 *@author
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedSubmitBindBankcardApi")
@Validator("submitBindBankcardParam")
public class DsedSubmitBindBankCardApi implements DsedH5Handle {

	@Resource
	private TransactionTemplate transactionTemplate;

	@Resource
	private DsedUserBankcardService dsedUserBankcardService;

	@Resource
	private DsedUserService dsedUserService;
	@Resource
	UpsUtil upsUtil;
	@Override
	public DsedH5HandleResponse process(Context context)  {
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "");
		Long bankCardId = Long.parseLong((String) context.getData("bankCardId"));
		String realname = ObjectUtils.toString(context.getData("realname"), null);
		String smsCode = ObjectUtils.toString(context.getData("smsCode"), null);

		final DsedUserBankcardDo userBankcardDo=dsedUserBankcardService.getById(bankCardId);
		int res =transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				DsedUserDo userDo=dsedUserService.getById(context.getUserId());
				DsedUserDo userUpdate=new DsedUserDo();
				if(StringUtil.isEmpty(userDo.getRealName())){
					userUpdate.setRid(context.getUserId());
					userUpdate.setRealName(realname);
					dsedUserService.updateUser(userUpdate);
				}
				userBankcardDo.setStatus(BankcardStatus.BIND.getCode());
				dsedUserBankcardService.updateUserBankcard(userBankcardDo);
				UpsAuthSignValidRespBo upsResult = upsUtil.authSignValid(context.getUserId()+"", userBankcardDo.getCardNumber(), smsCode, "02");
				if(!upsResult.isSuccess()){
					status.setRollbackOnly();
					return 1000; //UPS绑卡失败
				}
				return 1; //仅当返回1 才操作成功
			}
		});
		if(res == 1000) {
			userBankcardDo.setStatus(BankcardStatus.UNBIND.getCode());
			dsedUserBankcardService.updateUserBankcard(userBankcardDo);
			throw new FanbeiException(FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	    return resp;
	}


}
