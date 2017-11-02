/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfValidationLogDao;
import com.ald.fanbei.api.dal.domain.AfValidationLogDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：查询用户是否有资格更换手机号
 * @author chefeipeng 2017年2月16日下午7:09:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeMobileIfAbleApi")
public class ChangeMobileIfAbleApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfValidationLogDao afValidationLogDao;

	/**
	 * 点击更换手机号码，先检查用户是否有资格(24小时之内支付密码或者身份证验证超过5次错误)
	 * @param requestDataVo
	 * @param context
	 * @param request
	 * @return
	 */
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		//先检查用户是否有资格(24小时之内支付密码或者身份证验证超过5次错误)
		AfValidationLogDo afValidationLogDo = new AfValidationLogDo();
		//检查密码支付错误是否有连续5次
		afValidationLogDo.setType("1");
		afValidationLogDo.setUserId(userId);
		List<AfValidationLogDo> passwordList = afValidationLogDao.selectByUserId(afValidationLogDo);
		if(passwordList != null && passwordList.size()>0){
			int count = 0;
			for(int i=0;passwordList.size()<i;i++){
				String result = passwordList.get(i).getResult();
				if("false".equals(result)){
					count += 1;
					if(count>=ChangeMobileVerifyApi.PAY_PWD_FAIL_THRESHOLD){
						return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANGE_MOBILE_PASSWORD_ERROR_EXCEED_THRESHOLD);
					}
				}else{
					count = 0;
				}
			}
		}
		//检查身份证错误是否有连续5次
		afValidationLogDo.setType("2");
		List<AfValidationLogDo> idList = afValidationLogDao.selectByUserId(afValidationLogDo);
		if(idList != null && idList.size()>0){
			int count = 0;
			for(int i=0;idList.size()<i;i++){
				String result = idList.get(i).getResult();
				if("false".equals(result)){
					count += 1;
					if(count>=ChangeMobileVerifyApi.ID_CARD_FAIL_THRESHOLD){
						return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANGE_MOBILE_IDENTITY_CARD_ERROR_EXCEED_THRESHOLD);
					}
				}else{
					count = 0;
				}
			}
		}

		//跳转下个页面
		return resp;
	}




}
