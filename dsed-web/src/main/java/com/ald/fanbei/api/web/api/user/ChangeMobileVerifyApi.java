package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.order.BuySelfGoodsApi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfValidationLogDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfValidationLogDo;
import com.ald.fanbei.api.dal.domain.AfValidationLogDo.AfValidationLogType;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：更改手机——验证支付密码/验证原手机/验证身份证
 * @author zjf 2017年9月22日下午16:19:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeMobileVerifyApi")
public class ChangeMobileVerifyApi implements ApiHandle {
	Logger logger = LoggerFactory.getLogger(ChangeMobileVerifyApi.class);
	public static final int PAY_PWD_FAIL_THRESHOLD = 5;
	public static final int ID_CARD_FAIL_THRESHOLD = 5;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfValidationLogDao afValidationLogDao;
	@Resource
	private AfResourceService afResourceService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("enabled_change_mobile");
		if (afResourceDo != null && afResourceDo.getValue().equals("Y")) {
			throw new FanbeiException(afResourceDo.getValue2(), true);
		}
        Map<String, Object> params = requestDataVo.getParams();
        String behaviorStr = (String)params.get("behavior");
        String newMobile = (String)params.get("newMobile");
        if(behaviorStr == null || newMobile == null) {
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
        
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        VerifyBehaviorType behavior = VerifyBehaviorType.valueOf(behaviorStr);
        
        if(VerifyBehaviorType.PAY_PWD.equals(behavior)) {	//验证支付密码
        	AfValidationLogDo tmpDo = new AfValidationLogDo(userId, String.valueOf(AfValidationLogType.PAY_PWD.ordinal()), "false");
        	int num = afValidationLogDao.countFailNumWithin24H(tmpDo);
        	if(num >= PAY_PWD_FAIL_THRESHOLD) {
        		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANGE_MOBILE_PASSWORD_ERROR_EXCEED_THRESHOLD);
        	}
        	
        	AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
        	String payPwd = params.get("payPwd").toString();
        	String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
			if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
				afValidationLogDao.addValidationLog(tmpDo);
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
			tmpDo.setResult("true");
			afValidationLogDao.addValidationLog(tmpDo);
        	
        } else if(VerifyBehaviorType.OLD_MOBILE.equals(behavior)) { //验证原手机
        	String smsCode = params.get("verifyCode").toString();
        	smsUtil.checkSmsByMobileAndType(context.getMobile(), smsCode, SmsType.MOBILE_BIND);
        	
        } else {//验证身份证
        	String idCard = params.get("idCard").toString();
        	AfValidationLogDo tmpDo = new AfValidationLogDo(userId, String.valueOf(AfValidationLogType.ID_CARD.ordinal()), "false");
        	int num = afValidationLogDao.countFailNumWithin24H(tmpDo);
        	if(num >= ID_CARD_FAIL_THRESHOLD) {
        		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANGE_MOBILE_IDENTITY_CARD_ERROR_EXCEED_THRESHOLD);
        	}
        	
        	AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
        	if(!idCard.trim().toUpperCase().equals(afUserAccountDo.getIdNumber())) {
        		afValidationLogDao.addValidationLog(tmpDo);
        		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_IDNUMBER_INVALID_ERROR);
        	}
        	tmpDo.setResult("true");
        	afValidationLogDao.addValidationLog(tmpDo);
        }
        
        bizCacheUtil.hset(Constants.CACHEKEY_CHANGE_MOBILE, userId.toString(), newMobile, 1*60*60);
        return resp;
    }
	
	/**
	 * 用户请求验证的行为类型
	 */
	private enum VerifyBehaviorType{
		PAY_PWD,
		OLD_MOBILE,
		ID_CARD;
	}

}
