/**
 * 
 */
package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.enums.SmsCodeType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSON;

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
	@Autowired
	@Qualifier("beheadBorrowCashRenewalService")
	JsdUpsPayKuaijieServiceAbstract beheadBorrowCashRenewalService;

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
	private JsdNoticeRecordService jsdNoticeRecordService;

	@Resource
	private UpsUtil upsUtil;

    @Autowired
    BizCacheUtil bizCacheUtil;

    @Resource
	XgxyUtil xgxyUtil;


	@Resource
	private RedisTemplate<String, ?> redisTemplate;

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
			return new JsdH5HandleResponse(3001, BizExceptionCode.JSD_PARAMS_ERROR.getErrorMsg());
		}

		Map<String, Object> map = new HashMap<String, Object>();
 		if(SmsCodeType.REPAY.getCode().equals(type)){
			//校验借款是否被代扣锁住
			repaymentService.checkBorrowIsLock(userId);
			try{
				if(repaymentDo!=null){
					busiFlag=repaymentDo.getTradeNo();
				}else {
					busiFlag=legalOrderRepaymentDo.getTradeNo();
				}
				Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + busiFlag);
				if (beanName == null) {
					// 未获取到缓存数据，支付订单过期
					throw new BizException(BizExceptionCode.UPS_CACHE_EXPIRE);
				}



			switch (beanName.toString()) {
				case "jsdBorrowCashRepaymentService":
					map = jsdBorrowCashRepaymentService.doUpsPay(busiFlag, smsCode);
					break;
				default:
					throw new BizException("ups kuaijie not support", BizExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);}
			}catch (Exception e){
				throw new BizException("ups kuaijie fail  case:", e);
			}finally {
				repaymentService.unLockBorrow(userId);
			}
			
		}else if(SmsCodeType.DELAY.getCode().equals(type)){
			JsdBorrowCashRenewalDo renewalDo = renewalService.getByTradeNoXgxy(busiFlag);
 			Object beanName = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + renewalDo.getTradeNo());
 			if (beanName == null) {
 				// 未获取到缓存数据，支付订单过期
 				throw new BizException(BizExceptionCode.UPS_CACHE_EXPIRE);
 			}
 			
 			switch (beanName.toString()) {
	 			case "jsdBorrowCashRenewalService":
	 				map = jsdBorrowCashRenewalService.doUpsPay(renewalDo.getTradeNo(), smsCode);
	 				break;
	 			case "beheadBorrowCashRenewalService":
	 				map = beheadBorrowCashRenewalService.doUpsPay(renewalDo.getTradeNo(), smsCode);
	 				break;
	 			default:
	 				throw new BizException("ups kuaijie not support", BizExceptionCode.UPS_KUAIJIE_NOT_SUPPORT);
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
				return new JsdH5HandleResponse(1556, BizExceptionCode.UPS_AUTH_SIGN_ERROR.getErrorMsg());
			}

			HashMap<String,String> cardMap=new HashMap<>();
			cardMap.put("openId", String.valueOf(userId));
			cardMap.put("bindNo", busiFlag);
			if(res==1){
				cardMap.put("status", "Y");
			}else {
				cardMap.put("status", "N");
				cardMap.put("reason", BizExceptionCode.UPS_AUTH_SIGN_ERROR.getErrorMsg());
			}
			cardMap.put("timestamp",System.currentTimeMillis()+"");
			JsdNoticeRecordDo jsdNoticeRecordDo=new JsdNoticeRecordDo();
			jsdNoticeRecordDo.setParams(JSON.toJSONString(cardMap));
			jsdNoticeRecordDo.setType(JsdNoticeType.BIND.code);
			jsdNoticeRecordDo.setUserId(userDo.getRid());
			jsdNoticeRecordService.addNoticeRecord(jsdNoticeRecordDo);
			if(xgxyUtil.bindBackNoticeRequest(cardMap)){
				jsdNoticeRecordDo.setRid(jsdNoticeRecordDo.getRid());
				jsdNoticeRecordDo.setGmtModified(new Date());
				jsdNoticeRecordService.updateNoticeRecordStatus(jsdNoticeRecordDo);
			}
		}
		resp.setData(map);
		return resp;
    }
    /*
     * 锁住还款
	 */
	private void lockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 300, TimeUnit.SECONDS);
		if (count != 1) {
			throw new BizException(BizExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
	}
	private void unLockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
		redisTemplate.delete(key);
	}
}
