package com.ald.fanbei.api.biz.third.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.ApacheHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component("jpushUtilV2")
public class JpushUtilV2 extends AbstractThird {

    private JPushClient jpushClient = null;
    private static final String MESSAGE_CONTENT_TYPE = "text";
    private static final String EXTRAS_KEY_TITLE = "title";
    private static final String EXTRAS_KEY_CONTENT = "content";
    private static final String IOS_SOUND = "default";
    private static boolean on_line = false;


    public JpushUtilV2() {
        String appKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_JPUSH_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        String appSecret = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_JPUSH_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setConnectionTimeout(3000);
        clientConfig.setReadTimeout(5000);
        clientConfig.setConnectionRequestTimeout(3000);
        jpushClient = new JPushClient(appSecret, appKey, null, clientConfig);
        String authCode = ServiceHelper.getBasicAuthorization(appKey, appSecret);
        ApacheHttpClient httpClient = new ApacheHttpClient(authCode, null, clientConfig);
        jpushClient.getPushClient().setHttpClient(httpClient);
    }

    /**
     * 向用户推送消息，通过别名
     *
     * @param title
     * @param msgContent
     * @param extras
     * @param alias
     * @param isAll
     */
    public void pushMessageByAlias(String title, String msgContent, Map<String, String> extras, String[] alias, boolean isAll) {
        PushResult result = null;
        try {
            extras.put(EXTRAS_KEY_TITLE, title);
            extras.put(EXTRAS_KEY_CONTENT, msgContent);
            cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
            Message.Builder message = Message.newBuilder();
            message.setTitle(title);
            message.setMsgContent(msgContent);
            message.addExtras(extras);
            message.setContentType(MESSAGE_CONTENT_TYPE);

            pushBuilder.setAudience(isAll ? Audience.all() : Audience.alias(alias));
            pushBuilder.setMessage(message.build());
            pushBuilder.setOptions(getOptions());
            pushBuilder.setPlatform(Platform.all());
            PushPayload ppl = pushBuilder.build();
            result = jpushClient.sendPush(ppl);
            thirdLog.info(StringUtil.appendStrs("pushMessageByAlias title=", title, ",msgContent=", msgContent, ",extras=", extras, "alias=", StringUtil.turnArrayToStr(null, alias), ";result=", result));
        } catch (APIConnectionException | APIRequestException e) {
            e.printStackTrace();
            thirdLog.error(StringUtil.appendStrs("pushMessageByAlias error title=", title, ",msgContent=", msgContent, ",extras=", extras, "alias=", StringUtil.turnArrayToStr(null, alias), ";result=", result), e);
            throw new FanbeiException("pushMessageByAlias error", FanbeiExceptionCode.JPUSH_ERROR);
        }
    }

    /**
     * 向用户推送消息，通过别名
     *
     * @param title
     * @param msgContent
     * @param extras
     * @param alias
     */
    public void pushMessageByRegistIds(String title, String msgContent, Map<String, String> extras, String[] registIds) {
        PushResult result = null;
        try {
            extras.put(EXTRAS_KEY_TITLE, title);
            extras.put(EXTRAS_KEY_CONTENT, msgContent);
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
            result = jpushClient.sendPush(ppl);
            thirdLog.info(StringUtil.appendStrs("pushMessageByRegistIds title=", title, ",msgContent=", msgContent, ",extras=", extras, "registIds=", StringUtil.turnArrayToStr(null, registIds), ";result=", result));
        } catch (APIConnectionException | APIRequestException e) {
            e.printStackTrace();
            thirdLog.error(StringUtil.appendStrs("pushMessageByRegistIds error title=", title, ",msgContent=", msgContent, ",extras=", extras, "registIds=", StringUtil.turnArrayToStr(null, registIds), ";result=", result), e);
            throw new FanbeiException("pushMessageByRegistIds error", FanbeiExceptionCode.JPUSH_ERROR);
        }
    }

    /**
     * 向用户推送通知，通过别名
     *
     * @param title
     * @param msgContent
     * @param extras
     * @param alias
     */
    public void pushNotifyByAlias(String title, String msgContent, Map<String, String> extras, String[] alias) {
        PushResult result = null;
        try {
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
            result = jpushClient.sendPush(ppl);
            thirdLog.info(StringUtil.appendStrs("pushNotifyByAlias title=", title, ",msgContent=", msgContent, ",extras=", extras, "alias=", StringUtil.turnArrayToStr(null, alias), ";result=", result));
        } catch (APIConnectionException | APIRequestException e) {
            e.printStackTrace();
            thirdLog.info(StringUtil.appendStrs("pushNotifyByRegistIds error title=", title, ",msgContent=", msgContent, ",extras=", extras, "alias=", StringUtil.turnArrayToStr(null, alias), ";result=", result), e);
            throw new FanbeiException("pushNotifyByRegistIds error", FanbeiExceptionCode.JPUSH_ERROR);
        }
    }

    /**
     * 向用户推送通知,极光注册id
     *
     * @param title
     * @param msgContent
     * @param extras
     * @param registIds
     */
    public void pushNotifyByRegistIds(String title, String msgContent, Map<String, String> extras, String[] registIds) {
        PushResult result = null;
        try {
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
            result = jpushClient.sendPush(ppl);
            thirdLog.info(StringUtil.appendStrs("pushNotifyByRegistIds title=", title, ",msgContent=", msgContent, ",extras=", extras, "registIds=", StringUtil.turnArrayToStr(null, registIds), ";result=", result));
        } catch (APIConnectionException | APIRequestException e) {
            e.printStackTrace();
            thirdLog.error(StringUtil.appendStrs("pushNotifyByRegistIds error title=", title, ",msgContent=", msgContent, ",extras=", extras, "registIds=", StringUtil.turnArrayToStr(null, registIds), ";result=", result), e);
            throw new FanbeiException("pushNotifyByRegistIds error", FanbeiExceptionCode.JPUSH_ERROR);
        }
    }

    private cn.jpush.api.push.model.notification.Notification.Builder getNotifycationBuilder(String title, String msgContent, Map<String, String> extras) {
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

    private Options getOptions() {
        Options.Builder build = Options.newBuilder();
        build.setSendno(Options.sendno().getSendno());
        String invelmentType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        if (!StringUtils.isBlank(invelmentType) && (Constants.INVELOMENT_TYPE_ONLINE.equals(invelmentType) || Constants.INVELOMENT_TYPE_PRE_ENV.equals(invelmentType))) {
            on_line = true;
        }
        build.setApnsProduction(on_line);
        return build.build();
    }

    /**
     * 向用户推送通知，通过别名
     *
     * @param title
     * @param msgContent
     * @param extras
     * @param alias
     * @param type       1-指定用户 2-全部用户
     */
    public PushResult pushNotifyByAlias(String title, String msgContent, Map<String, String> extras, String[] alias, String type, String system, String pushType) {
        PushResult result;
        try {
            extras.put("title", title);
            extras.put("content", msgContent);
            cn.jpush.api.push.model.PushPayload.Builder pushBuilder = PushPayload.newBuilder();
            cn.jpush.api.push.model.notification.Notification.Builder notifyBuilder = this.getNotifycationBuilder(title, msgContent, extras, system);
            if ("2".equals(type)) {
                pushBuilder.setAudience(Audience.all());
            } else if ("1".equals(type) || "3".equals(type) || "4".equals(type)) {
                pushBuilder.setAudience(Audience.alias(alias));
            }
            if ("1".equals(pushType)) {
                pushBuilder.setMessage(Message.newBuilder().setMsgContent(msgContent).setTitle(title).addExtras(extras).setContentType(MESSAGE_CONTENT_TYPE).build());
            } else if ("2".equals(pushType)) {
                pushBuilder.setNotification(notifyBuilder.build());
            } else if ("3".equals(pushType)) {
                pushBuilder.setMessage(Message.newBuilder().setMsgContent(msgContent).setTitle(title).addExtras(extras).setContentType(MESSAGE_CONTENT_TYPE).build());
                pushBuilder.setNotification(notifyBuilder.build());
            }
            pushBuilder.setOptions(getOptions());
            if (null != system && "ios".equals(system)) {
                pushBuilder.setPlatform(Platform.ios());
            } else if (null != system && "android".equals(system)) {
                pushBuilder.setPlatform(Platform.android());
            } else {
                pushBuilder.setPlatform(Platform.all());
            }
//			pushBuilder.setPlatform(Platform.all());
            PushPayload ppl = pushBuilder.build();

            result = jpushClient.sendPush(ppl);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    private cn.jpush.api.push.model.notification.Notification.Builder getNotifycationBuilder(String title, String msgContent, Map<String, String> extras, String system) {
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
        if (null != system && "ios".equals(system)) {
            notifyBuilder.addPlatformNotification(iosBuilder.build());
        } else if (null != system && "android".equals(system)) {
            notifyBuilder.addPlatformNotification(androidBuilder.build());
        } else {
            notifyBuilder.addPlatformNotification(androidBuilder.build());
            notifyBuilder.addPlatformNotification(iosBuilder.build());
        }
        return notifyBuilder;
    }
}
