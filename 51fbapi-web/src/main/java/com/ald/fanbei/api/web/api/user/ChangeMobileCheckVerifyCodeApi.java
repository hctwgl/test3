package com.ald.fanbei.api.web.api.user;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author chefeipeng 2017年9月25日下午1:49:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeMobileCheckVerifyCodeApi")
public class ChangeMobileCheckVerifyCodeApi implements ApiHandle {
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	SmsUtil smsUtil;
	@Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
        String newMobile = ObjectUtils.toString(requestDataVo.getParams().get("newMobile"));

        if(StringUtil.isBlank(verifyCode) || StringUtil.isBlank(type) || SmsType.findByCode(type) == null){
        	logger.error("verifyCode or type is empty verifyCode = " + verifyCode + " type = " + type);
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
        }
        
        AfSmsRecordDo afSmsRecordDo = afSmsRecordService.getLatestByMobileCode(newMobile,verifyCode);
        if (afSmsRecordDo == null) {
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_ERROR);
        }
        
        //验证图片验证码
        String imageCode = ObjectUtils.toString(requestDataVo.getParams().get("imageCode"));
        if(StringUtil.isNotEmpty(imageCode)) {
            String id = requestDataVo.getId().split("_")[1];
            Object value = bizCacheUtil.getObject(id);
            if(value != null) {
                bizCacheUtil.delCache(id);
                String realCode = value.toString();
                if(!realCode.equalsIgnoreCase(imageCode)) {
                    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_IMAGE_ERROR);
                }
            }
            else {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_IMAGE_NOTEXIST);
            }
        }

        //验证手机验证码
        smsUtil.checkSmsByMobileAndType(newMobile, verifyCode, SmsType.findByCode(type));
        
        //是否实名
        Long userId = context.getUserId();
        Map<String, Object> data = new HashMap<String, Object>();
        AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        if(null != afUserAuthDo){
            if("Y".equals(afUserAuthDo.getRealnameStatus())){
                data.put("realnameStatus", "Y");
            }else{
                data.put("realnameStatus", "N");
            }
        }else{
            data.put("realnameStatus", "N");
        }
        //是否有支付密码
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        if(null != afUserAccountDo){
            if(!StringUtil.isEmpty(afUserAccountDo.getPassword())){
                data.put("passwordStatus", "Y");
            }else{
                data.put("passwordStatus", "N");
            }
        }else{
            data.put("passwordStatus", "N");
        }
        //新手机是否被注册
        AfUserDo afUserDo = afUserService.getUserByUserName(newMobile);
        if(null != afUserDo){
            data.put("isMember", "Y");
        }else{
            data.put("isMember", "N");
        }
        resp.setResponseData(data);
        return resp;
    }
    

}
