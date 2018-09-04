package com.ald.fanbei.api.web.h5.api.dsed;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.JsdUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;

/**
 *@类现描述：提交绑卡
 *@author
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedsmsCodeSubmitApi")
public class DsedSmsCodeSubmitApi implements DsedH5Handle {

	@Resource
	private TransactionTemplate transactionTemplate;

	@Resource
	private DsedUserBankcardService dsedUserBankcardService;


	@Resource
	private DsedUserService dsedUserService;

	@Autowired
	@Qualifier("dsedLoanRepaymentService")
    JsdUpsPayKuaijieServiceAbstract dsedLoanRepaymentService;

	@Autowired
	BizCacheUtil bizCacheUtil;

	@Resource
	UpsUtil upsUtil;
	@Override
	public DsedH5HandleResponse process(Context context)  {
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
		String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
		String openId = ObjectUtils.toString(context.getData("userId"), null);
		String smsCode = ObjectUtils.toString(context.getData("code"), null);
		String type = ObjectUtils.toString(context.getData("type"), null);
		if (StringUtils.isBlank(busiFlag) || StringUtils.isBlank(smsCode) || StringUtils.isBlank(openId)) {
			return new DsedH5HandleResponse(9999, "参数错误");
		}
		if(StringUtil.equals(SmsCodeType.BIND.getCode(),type)){
			final DsedUserBankcardDo userBankcardDo=dsedUserBankcardService.getById(Long.valueOf(busiFlag));
			final DsedUserDo userDo=dsedUserService.getByOpenId(openId);
			int res =transactionTemplate.execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus status) {
					DsedUserDo userUpdate=new DsedUserDo();
					if(StringUtil.isEmpty(userDo.getRealName())){
						userUpdate.setRid(userDo.getRid());
						userUpdate.setRealName(userDo.getRealName());
						dsedUserService.updateUser(userUpdate);
					}
					userBankcardDo.setStatus(BankcardStatus.BIND.getCode());
					dsedUserBankcardService.updateUserBankcard(userBankcardDo);
					UpsAuthSignValidRespBo upsResult = upsUtil.authSignValid(userDo.getRid()+"", userBankcardDo.getBankCardNumber(), smsCode, "02");
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
				return new DsedH5HandleResponse(1556, FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR.getErrorMsg());
			}
		}else if(StringUtil.equals(SmsCodeType.REPAY.getCode(),type)) {
			Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + busiFlag);
			if (beanName == null) {
				// 未获取到缓存数据，支付订单过期
				throw new FanbeiException("ups cache expire",FanbeiExceptionCode.UPS_CACHE_EXPIRE);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			switch (beanName.toString()) {
				case "dsedLoanRepaymentService":
					map = dsedLoanRepaymentService.doUpsPay(busiFlag, smsCode);
					break;
				default:
					throw new FanbeiException("ups kuaijie not support", FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
			}
			resp.setData(map);
		}
	    return resp;
	}


}
