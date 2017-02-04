package com.ald.fanbei.api.biz.third.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.Options.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

/**
 * 
 *@类描述：极光推送服务
 *@author 陈金虎 2017年1月19日 下午3:57:45
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("jpushUtil")
public class JpushUtil extends AbstractThird{
	
	private static JPushClient jpushClient= null;
	
	private static final String MESSAGE_CONTENT_TYPE          = "text";
	private static final String EXTRAS_KEY_TITLE              = "title";
	private static final String EXTRAS_KEY_CONTENT            = "content";
	private static final String IOS_SOUND                     = "default";
	private static boolean on_line = false;
	
	private synchronized static JPushClient getPushClient(){
		if	(jpushClient == null){
			String appKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_JPUSH_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
			String appSecret = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_JPUSH_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
			jpushClient = new JPushClient(appSecret,appKey);  
			return jpushClient;
		}
		return jpushClient;
	}
	
	/**
	 * 向用户推送消息，通过别名
	 *@param title
	 *@param msgContent
	 *@param extras
	 *@param alias
	 */
	public void pushMessageByAlias(String title,String msgContent,Map<String,String> extras,String[] alias){
		PushResult result = null;
		try{
			extras.put(EXTRAS_KEY_TITLE, title);
			extras.put(EXTRAS_KEY_CONTENT, msgContent);
			thirdLog.info(StringUtil.appendStrs("pushMessageByAlias title=",title,",msgContent=",msgContent,",extras=",extras,"alias=",StringUtil.turnArrayToStr(null,alias),";result=",result));
			cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
			Message.Builder message = Message.newBuilder();
			message.setTitle(title);
			message.setMsgContent(msgContent);
			message.addExtras(extras);
			message.setContentType(MESSAGE_CONTENT_TYPE);
			
			pushBuilder.setAudience(Audience.alias(alias));
			pushBuilder.setMessage(message.build());
			pushBuilder.setOptions(getOptions());
			pushBuilder.setPlatform(Platform.all());
			PushPayload ppl = pushBuilder.build();
			result = getPushClient().sendPush(ppl);
		}catch(APIConnectionException | APIRequestException e){
			e.printStackTrace();
			thirdLog.error(StringUtil.appendStrs("pushMessageByAlias error title=",title,",msgContent=",msgContent,",extras=",extras,"alias=",StringUtil.turnArrayToStr(null,alias),";result=",result),e);
			throw new FanbeiException("pushMessageByAlias error",FanbeiExceptionCode.JPUSH_ERROR);
		}
	}
	
	/**
	 * 向用户推送消息，通过别名
	 *@param title
	 *@param msgContent
	 *@param extras
	 *@param alias
	 */
	public void pushMessageByRegistIds(String title,String msgContent,Map<String,String> extras,String[] registIds){
		PushResult result = null;
		try{
			extras.put(EXTRAS_KEY_TITLE, title);
			extras.put(EXTRAS_KEY_CONTENT, msgContent);
			thirdLog.info(StringUtil.appendStrs("pushMessageByRegistIds title=",title,",msgContent=",msgContent,",extras=",extras,"registIds=",StringUtil.turnArrayToStr(null,registIds),";result=",result));
			cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
			Message.Builder message = Message.newBuilder();
			message.setTitle(title);
			message.setMsgContent(msgContent);
			extras.put(EXTRAS_KEY_TITLE, title);
			message.addExtras(extras);
			message.setContentType(MESSAGE_CONTENT_TYPE);
			
			pushBuilder.setAudience(Audience.registrationId(registIds));
			pushBuilder.setMessage(message.build());
			pushBuilder.setOptions(getOptions());
			pushBuilder.setPlatform(Platform.all());
			PushPayload ppl = pushBuilder.build();
			result = getPushClient().sendPush(ppl);
		}catch(APIConnectionException | APIRequestException e){
			e.printStackTrace();
			thirdLog.error(StringUtil.appendStrs("pushMessageByRegistIds error title=",title,",msgContent=",msgContent,",extras=",extras,"registIds=",StringUtil.turnArrayToStr(null,registIds),";result=",result),e);
			throw new FanbeiException("pushMessageByRegistIds error",FanbeiExceptionCode.JPUSH_ERROR);
		}
	}

	/**
	 * 向用户推送通知，通过别名
	 *@param title
	 *@param msgContent
	 *@param extras
	 *@param alias
	 */
	public void pushNotifyByAlias(String title,String msgContent,Map<String,String> extras,String[] alias){
		PushResult result = null;
		thirdLog.info(StringUtil.appendStrs("pushNotifyByAlias title=",title,",msgContent=",msgContent,",extras=",extras,"alias=",StringUtil.turnArrayToStr(null,alias),";result=",result));
		try{
			extras.put(EXTRAS_KEY_TITLE, title);
			extras.put(EXTRAS_KEY_CONTENT, msgContent);
			cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
			cn.jpush.api.push.model.notification.Notification.Builder notifyBuilder = this.getNotifycationBuilder(title, msgContent, extras);
			
			pushBuilder.setAudience(Audience.alias(alias));
			pushBuilder.setNotification(notifyBuilder.build());
			pushBuilder.setOptions(getOptions());
			pushBuilder.setMessage(Message.content(msgContent));
			pushBuilder.setPlatform(Platform.all());
			PushPayload ppl = pushBuilder.build();
			result = getPushClient().sendPush(ppl);
		}catch(APIConnectionException | APIRequestException e){
			e.printStackTrace();
			thirdLog.error(StringUtil.appendStrs("pushNotifyByRegistIds error title=",title,",msgContent=",msgContent,",extras=",extras,"alias=",StringUtil.turnArrayToStr(null,alias),";result=",result),e);
			throw new FanbeiException("pushNotifyByRegistIds error",FanbeiExceptionCode.JPUSH_ERROR);
		}
	}
	
	/**
	 * 向用户推送通知,极光注册id
	 *@param title
	 *@param msgContent
	 *@param extras
	 *@param registIds
	 */
	public void pushNotifyByRegistIds(String title,String msgContent,Map<String,String> extras,String[] registIds){
		PushResult result = null;
		thirdLog.info(StringUtil.appendStrs("pushNotifyByRegistIds title=",title,",msgContent=",msgContent,",extras=",extras,"registIds=",StringUtil.turnArrayToStr(null,registIds),";result=",result));
		try{
			extras.put(EXTRAS_KEY_TITLE, title);
			extras.put(EXTRAS_KEY_CONTENT, msgContent);
			cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
			cn.jpush.api.push.model.notification.Notification.Builder notifyBuilder = this.getNotifycationBuilder(title, msgContent, extras);
			
			pushBuilder.setAudience(Audience.registrationId(registIds));
			pushBuilder.setNotification(notifyBuilder.build());
			pushBuilder.setOptions(getOptions());
			pushBuilder.setMessage(Message.content(msgContent));
			pushBuilder.setPlatform(Platform.all());
			PushPayload ppl = pushBuilder.build();
			result = getPushClient().sendPush(ppl);
		}catch(APIConnectionException | APIRequestException e){
			e.printStackTrace();
			thirdLog.error(StringUtil.appendStrs("pushNotifyByRegistIds error title=",title,",msgContent=",msgContent,",extras=",extras,"registIds=",StringUtil.turnArrayToStr(null,registIds),";result=",result),e);
			throw new FanbeiException("pushNotifyByRegistIds error",FanbeiExceptionCode.JPUSH_ERROR);
		}
	}
	
	private cn.jpush.api.push.model.notification.Notification.Builder getNotifycationBuilder(String title,String msgContent,Map<String,String> extras){
		cn.jpush.api.push.model.notification.Notification.Builder notifyBuilder = Notification.newBuilder();
		cn.jpush.api.push.model.notification.AndroidNotification.Builder androidBuilder = AndroidNotification.newBuilder();
		androidBuilder.setTitle(title);
		androidBuilder.setAlert(msgContent);
		androidBuilder.addExtras(extras);
		cn.jpush.api.push.model.notification.IosNotification.Builder iosBuilder = IosNotification.newBuilder();
		iosBuilder.setAlert(msgContent);
		iosBuilder.setSound(IOS_SOUND);
		iosBuilder.autoBadge();
		iosBuilder.addExtras(extras);
		
		notifyBuilder.setAlert(title);
		notifyBuilder.addPlatformNotification(androidBuilder.build());
		notifyBuilder.addPlatformNotification(iosBuilder.build());
		return notifyBuilder;
	}
	
	private Options getOptions(){
		Builder build = Options.newBuilder();
		build.setSendno(Options.sendno().getSendno());
		String invelmentType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		if(!StringUtils.isBlank(invelmentType)&&Constants.INVELOMENT_TYPE_ONLINE.equals(invelmentType)){
			on_line= true;
		}
		build.setApnsProduction(on_line);
		return build.build();
	}
	
}
