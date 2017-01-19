package com.ald.fanbei.api.biz.util;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.impl.AfSmsRecordServiceImpl;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.alibaba.druid.util.StringUtils;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午2:51:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("smsSendUtil")
public class SmsSendUtil extends AbstractThridUtil{
	
	@Resource
	AfSmsRecordServiceImpl afSmsRecordServiceImpl;
	
	private static final String SEND_SMS_SUCC_CODE = "000000";
	private static final int REGIST_CODE_TEMPLATEID       = 000000;  //注册验证码模板
	private static final int CHANGE_PASS_CODE_TEMPLATEID  = 000000;  //修改注册密码模板

	/**
	 * 发送短信
	 * @param type 短信类型 1：注册验证码
	 * @param toMobile 手机号
	 * @param templateId 模板号
	 * @param params 参数列表
	 * @return
	 */
	private boolean sendTemplateSms(int type,String toMobile,Integer templateId,String...params ){
		HashMap<String, Object> result = new HashMap<String, Object>();
		if(StringUtils.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_TEST)){
			result.put("statusCode", "000000");
			params = new String[]{"888888"};
		}else{
			//发送验证码
//			result = getSmsSdk().sendTemplateSMS(toMobile, templateId+"", params);
		}
		thirdLog.info("sendTemplateSms:toMobile" + toMobile + ",templateId=" + templateId + ",params" + StringUtil.turnArrayToStr(null,params)+";result="+result);
		AfSmsRecordDo smsDo = new AfSmsRecordDo();
		smsDo.setMobile(toMobile);
		smsDo.setParams(StringUtil.turnArrayToStr(null,params));
		smsDo.setTemplateId(templateId);
		smsDo.setType(type);
		smsDo.setUserId(0l);
		smsDo.setResult(result + "");
		afSmsRecordServiceImpl.addSmsRecord(smsDo);
		if(SEND_SMS_SUCC_CODE.equals(result.get("statusCode"))){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 发送注册短信验证码
	 * @param mobile
	 * @return
	 */
	public boolean sendVerifyCodeReg(String mobile,String vericationCode){
		return this.sendTemplateSms(1,mobile, REGIST_CODE_TEMPLATEID, vericationCode);
	}
	
	/**
	 * 忘记密码获取短信验证码
	 * @param mobile
	 * @return
	 */
	public boolean sendVerifyCodeFindPass(String mobile,String vericationCode){
		return this.sendTemplateSms(2,mobile, CHANGE_PASS_CODE_TEMPLATEID, vericationCode);
	}
}
