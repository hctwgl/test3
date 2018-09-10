/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.jsd;

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

import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

/**
 * 
 * @类描述：快捷支付确认支付
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("confirmSmsApi")
public class ConfirmSmsApi implements JsdH5Handle {


	@Autowired
	@Qualifier("jsdBorrowCashRepaymentService")
	JsdUpsPayKuaijieServiceAbstract jsdBorrowCashRepaymentService;
	@Autowired
	@Qualifier("jsdBorrowCashRenewalService")
	JsdUpsPayKuaijieServiceAbstract jsdBorrowCashRenewalService;

	@Resource
	private JsdUserBankcardService jsdUserBankcardService;
	@Resource
	private JsdBorrowCashRenewalService renewalService;

	@Resource
	private JsdUserService jsdUserService;

	@Resource
	private TransactionTemplate transactionTemplate;

	@Resource
	private JsdBorrowCashRepaymentService repaymentService;

	@Resource
	private JsdBorrowLegalOrderRepaymentService jsdBorrowLegalOrderRepaymentService;

	@Resource
	private UpsUtil upsUtil;

    @Autowired
    BizCacheUtil bizCacheUtil;

    @Resource
	XgxyUtil xgxyUtil;

    @Override
    public JsdH5HandleResponse process(Context context) {
		JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");

		String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
		String smsCode = ObjectUtils.toString(context.getData("code"), null);
		String type = ObjectUtils.toString(context.getData("type"), null);
		Long userId=context.getUserId();
		JsdBorrowCashRepaymentDo repaymentDo=repaymentService.getByTradeNoXgxy(busiFlag);
		JsdBorrowLegalOrderRepaymentDo legalOrderRepaymentDo=jsdBorrowLegalOrderRepaymentService.getByTradeNoXgxy(busiFlag);
		
		if (StringUtils.isBlank(busiFlag) || StringUtils.isBlank(smsCode)) {
			return new JsdH5HandleResponse(3001, FanbeiExceptionCode.JSD_PARAMS_ERROR.getErrorMsg());
		}

		Map<String, Object> map = new HashMap<String, Object>();
 		if(SmsCodeType.REPAY.getCode().equals(type)){
 			if(repaymentDo!=null){
 				busiFlag=repaymentDo.getTradeNo();
 			}else {
 				busiFlag=legalOrderRepaymentDo.getTradeNo();
 			}
			Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + busiFlag);
			if (beanName == null) {
				// 未获取到缓存数据，支付订单过期
				throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
			}

			switch (beanName.toString()) {
				case "jsdBorrowCashRepaymentService":
					map = jsdBorrowCashRepaymentService.doUpsPay(busiFlag, smsCode);
					break;
				default:
					throw new FanbeiException("ups kuaijie not support", FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
			}
			
		}else if(SmsCodeType.DELAY.getCode().equals(type)){
			JsdBorrowCashRenewalDo renewalDo = renewalService.getByTradeNoXgxy(busiFlag);
 			Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + renewalDo.getTradeNo());
 			if (beanName == null) {
 				// 未获取到缓存数据，支付订单过期
 				throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
 			}
 			
 			switch (beanName.toString()) {
	 			case "jsdBorrowCashRenewalService":
	 				map = jsdBorrowCashRenewalService.doUpsPay(renewalDo.getTradeNo(), smsCode);
	 				break;
	 			default:
	 				throw new FanbeiException("ups kuaijie not support", FanbeiExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
	 		}
 		}else if(SmsCodeType.BIND.getCode().equals(type)){
			final JsdUserBankcardDo userBankcardDo=jsdUserBankcardService.getByBindNo(busiFlag);
			final JsdUserDo userDo=jsdUserService.getById(userId);
			int res =transactionTemplate.execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus status) {
					JsdUserDo userUpdate=new JsdUserDo();
					if(StringUtil.isEmpty(userDo.getRealName())){
						userUpdate.setRid(userDo.getRid());
						userUpdate.setRealName(userDo.getRealName());
						jsdUserService.updateUser(userUpdate);
					}
					userBankcardDo.setStatus(BankcardStatus.BIND.getCode());
					jsdUserBankcardService.updateUserBankcard(userBankcardDo);
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
				jsdUserBankcardService.updateUserBankcard(userBankcardDo);
				return new JsdH5HandleResponse(1556, FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR.getErrorMsg());
			}
			HashMap<String,String> cardMap=new HashMap<>();
			cardMap.put("openId", String.valueOf(userId));
			cardMap.put("bindNo", busiFlag);
			if(res==1){
				cardMap.put("status", "Y");
			}else {
				cardMap.put("status", "N");
				cardMap.put("reason", FanbeiExceptionCode.UPS_AUTH_SIGN_ERROR.getErrorMsg());
			}
			cardMap.put("timestamp",System.currentTimeMillis()+"");
			xgxyUtil.bindBackNoticeRequest(cardMap);
		}
		resp.setData(map);
		return resp;
    }
}
