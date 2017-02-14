package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:48:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("registerSetPwdApi")
public class RegisterSetPwdApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	AfUserAccountService afUserAccountService;
	
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = context.getUserName();
        String passwordSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        String nick = ObjectUtils.toString(requestDataVo.getParams().get("nick"), null);
        String recommendCode = ObjectUtils.toString(requestDataVo.getParams().get("recommendCode"), null);
        if(StringUtil.isBlank(passwordSrc)){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(context.getUserName(), SmsType.REGIST.getCode());
        if(smsDo == null){
        	logger.error("sms record is empty");
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getParams().split(",")[0];
        if(!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 0){
        	logger.error("verifyCode is invalid");
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        
        String salt = UserUtil.getSalt();
        String password = UserUtil.getPassword(passwordSrc, salt);
        
        AfUserDo userDo = new AfUserDo();
        userDo.setSalt(salt);
        userDo.setUserName(userName);
        userDo.setMobile(userName);
        userDo.setPassword(password);
        userDo.setNick(userName);
        userDo.setRecommendCode(recommendCode);
        userDo.setNick(nick);
        
        afUserService.addUser(userDo);
        
        AfUserAccountDo account = new AfUserAccountDo();
        account.setUserId(userDo.getRid());
        account.setUserName(userDo.getUserName());
        afUserAccountService.addUserAccount(account);
        return resp;
    }
    
//    private void userInvite(Long userId,String code,AppContext context){
//		AfUserDo hoaUserDo = afUserService.getUserById(userId);
//		Long invitorId =  hoaUserDo.getInvitor();
//		AfUserDo invitor = hoaUserService.getUserByInviteCode(code);
//		if(invitor == null){
//			throw new FanbeiException("code not exist", FanbeiExceptionCode.CODE_NOT_EXIST);
//		}
//		//是在注册二十四小时之后输入的邀请码
////		if (new Date().getTime() - hoaUserDo.getGmtCreate().getTime() > 1000 * 60 * 60 * 72) {
////			throw new FanbeiException("commit invite code expire time", FanbeiExceptionCode.COMMIT_INVITE_CODE_EXPIRE_TIME);
////		}
//		//邀请码只能输入一次,不能重复输入
//		if (invitorId != null && invitorId != 0) {
//			throw new FanbeiException("user duplicate commit invite code", FanbeiExceptionCode.USER_DUPLICATE_INVITE_CODE);
//		}
//
//		if((CommonUtil.isMobile(hoaUserDo.getUserName()) && !CommonUtil.isMobile(invitor.getUserName()))
//				||(!CommonUtil.isMobile(hoaUserDo.getUserName()) && CommonUtil.isMobile(invitor.getUserName()))){
//			throw new FanbeiException("app and wx invite error", FanbeiExceptionCode.APP_WX_INVITE_ERROR); 
//		}
//		//不能互相邀请
//		if (invitor != null && invitor.getInvitor().equals(userId)) {
//			throw new FanbeiException("limit invite each other", FanbeiExceptionCode.LIMIT_INVITE_EACH_OTHER);
//		}
//		//不能邀请自己
//		if (invitor != null && invitor.getRid().equals(userId)) {
//			throw new FanbeiException("can't invite self", FanbeiExceptionCode.LIMIT_INVITE_SELF);
//		}
//		hoaUserDo.setInvitor(invitor.getRid());
//		hoaUserDo.setGmtInvite(new Date());
//		
//		if(hoaUserService.updateUser(hoaUserDo) <= 0){
//			throw new FanbeiException("commit invite code failed", FanbeiExceptionCode.SYSTEM_ERROR);
//		}
//		
//		//增加统计信息
//		//userId位被邀请的人，invitor为第一邀请人(invitor直接邀请人+1，间接邀请人+userId所邀请的所有人),invitor的invitor为第二邀请人（+1）
//		
//		//设置直接邀请人的邀请数量增加1，直接邀请人间接邀请的数量需要增加邀请人所邀请的一级邀请人的数量
//		Integer inviteCount = hoaUserService.getInvitedCountByUserId(userId);
//		int updateCount = hoaUserInviteStatisticsService.updateInviteStatistics(new Date(), invitor.getRid(), null,null, 1, inviteCount==0?null:inviteCount,null,null);
//		if(updateCount <1){
//			hoaUserInviteStatisticsService.addInviteStatistics(new Date(), invitor.getRid() , 0 , 0 , 1 , inviteCount , new BigDecimal(0),new BigDecimal(0));
//		}
//		//设置间接邀请人
//		if(invitor.getInvitor().longValue() > 0){
//			int updateSedCount = hoaUserInviteStatisticsService.updateInviteStatistics(new Date(), invitor.getInvitor(), null,null, null, 1,null,null);
//			if(updateSedCount <1){
//				hoaUserInviteStatisticsService.addInviteStatistics(new Date(), invitor.getInvitor() , 0 , 0 , 0 , 1 , new BigDecimal(0),new BigDecimal(0));
//			}
//		}
//		activeRuleEngineUtil.invite(userId, invitor.getRid(),context.getSource());
//	}
    
}
