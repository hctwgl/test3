/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.risk.RiskAuthFactory.RiskEventType;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：更改手机——新手机号入库&同步新手机号和通讯录到风控，原子操作
 * @author zjf 2017年9月22日下午16:19:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeMobileSyncConactsApi")
public class ChangeMobileSyncConactsApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfAuthContactsService afAuthContactsService;
	
	@Resource
	TransactionTemplate transactionTemplate;
	
	@Resource
	SmsUtil smsUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	@Resource
	TokenCacheUtil tokenCacheUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, final FanbeiContext context, HttpServletRequest request) {
		final String contacts = (String) requestDataVo.getParams().get("contacts");
		if(contacts == null) {
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
		
		final ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		final Long uid = context.getUserId();
		final String uidStr = context.getUserId().toString();
		Object o = bizCacheUtil.hget(Constants.CACHEKEY_CHANGE_MOBILE, uidStr);
		if(o == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANGE_MOBILE_TARGET_LOST);
		}
		final String newMobile = o.toString(); // 获取上一步缓存的手机号
		
		// 同步风控数据
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				AfUserDo userDo = afUserService.getUserById(uid);
				AfUserAccountDo userAccountDo = afUserAccountService.getUserAndAccountByUserId(uid);
				
				userDo.setUserName(newMobile);
				userDo.setMobile(newMobile);
				userAccountDo.setUserName(newMobile);
				
				AfUserDo userDoForMod = new AfUserDo();
				userDoForMod.setRid(userDo.getRid());
				userDoForMod.setUserName(newMobile);
				userDoForMod.setMobile(newMobile);
				afUserService.updateUser(userDoForMod);
				
				AfUserAccountDo userAccountDoForMod = new AfUserAccountDo();
				userAccountDoForMod.setUserId(userDo.getRid());
				userAccountDoForMod.setUserName(newMobile);
				afUserAccountService.updateUserAccount(userAccountDoForMod);
				
				AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(uid);
				if(authDo == null || RiskStatus.A.getCode().equals(authDo.getRiskStatus())) { // 用户还未风控初始化，跳过
					logger.info("don't init risk,skip sync user");
				}else {
					// 更新通讯录 DIRECTORY
					bizCacheUtil.saveObject(Constants.CACHEKEY_USER_CONTACTS + uidStr, contacts, Constants.SECOND_OF_ONE_DAY);
					RiskRespBo riskResp = riskUtil.registerStrongRisk(uidStr, RiskEventType.DIRECTORY.name(), null, null, "", "", null, "", "", "","");
					if (!riskResp.isSuccess()) {
						throw new FanbeiException(FanbeiExceptionCode.RISK_SYNC_CONTACTS_ERROR);
					}
					
					// 更新用户信息 USER
					riskResp = riskUtil.registerStrongRisk(uidStr, RiskEventType.USER.name(), userDo, null, "", "", (AfUserAccountDto)userAccountDo, "", "", "","");
					if (!riskResp.isSuccess()) {
						throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
					}
					
					//状态更新失败不影响核心业务，日志打点即可
					try {
						AfUserAuthDo authDoForMod = new AfUserAuthDo();
						authDoForMod.setUserId(uid);
						authDoForMod.setTeldirStatus(YesNoStatus.YES.getCode());
						afUserAuthService.updateUserAuth(authDoForMod);
					}catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				
				return 1;
			}
		});
		
		bizCacheUtil.delCache(Constants.CACHEKEY_USER_NAME + context.getUserName());//操作成功，删除原用户缓存
		bizCacheUtil.hdel(Constants.CACHEKEY_CHANGE_MOBILE, uidStr);
		return resp;
	}

}
