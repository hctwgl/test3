package com.ald.fanbei.api.web.api.user;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.BizCacheUtil;

import com.ald.fanbei.api.dal.domain.AfUserDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:49:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkVerifyCodeApi")
public class CheckVerifyCodeApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	SmsUtil smsUtil;
	@Resource
    BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        String userName = context.getUserName();
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
        
        if(StringUtil.isBlank(verifyCode) || StringUtil.isBlank(type) || SmsType.findByCode(type) == null){
        	logger.error("verifyCode or type is empty verifyCode = " + verifyCode + " type = " + type);
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
        }
        AfSmsRecordDo afSmsRecordDo = afSmsRecordService.getLatestByMobileCode(userName,verifyCode);

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

        smsUtil.checkSmsByMobileAndType(userName, verifyCode,SmsType.findByCode(type));

        /**
         * 判断推荐人
         */
        String recommendCode = ObjectUtils.toString(requestDataVo.getParams().get("recommendCode"));
        // 判断邀请码是否为空
        if (StringUtil.isNotEmpty(recommendCode)) {
            AfUserDo recommendUserDo = afUserService.getUserByRecommendCode(recommendCode);
            if (recommendUserDo == null) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CODE_NOT_EXIST);
            }
        }
        return resp;
    }
    

}
