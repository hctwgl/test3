package com.ald.fanbei.api.web.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.internal.toplink.LinkException;

/**
 * 
 *@类描述：阿里百川消息推送监听
 *@author 何鑫 2017年2月4日 下午3:02:01
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class MessageListener extends ContextLoaderListener{

	private static String appId = null;
	private static String secret = null;
	
	private static String getBcAppId(){
		if(appId != null){
			return appId;
		}
		appId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_APPID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return appId;
	}
	
	private static String getBcSecret(){
		if(secret != null){
			return secret;
		}
		secret = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_TAOBAO_BCDS_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return secret;
	}
	
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		TmcClient client = new TmcClient(getBcAppId(),getBcSecret(), "default");  
	    client.setMessageHandler(new MessageHandler() {
			@Override
			public void onMessage(Message message, MessageStatus status) throws Exception {
				try {  
		            // 默认不抛出异常则认为消息处理成功  
					AfOrderService afOrderService = (AfOrderService)SpringBeanContextUtil.getBean("afOrderService");
		            if(StringUtil.isNotEmpty(message.getTopic())){
		            	switch(message.getTopic()){
		            		case "taobao_tae_BaichuanTradeCreated":
		            			afOrderService.createOrderTrade(message.getContent());
		            			break;
		            		case "taobao_tae_BaichuanTradeSuccess":
		            			afOrderService.updateOrderTradeSuccess(message.getContent());
		            			break;
		            		case "taobao_tae_BaichuanTradeRefundCreated":
		            			afOrderService.updateOrderTradeRefundCreated(message.getContent());
		            			break;
		            		case "taobao_tae_BaichuanTradeRefundSuccess":
		            			afOrderService.updateOrderTradeRefundSuccess(message.getContent());
		            			break;
		            		case "taobao_tae_BaichuanTradePaidDone":
		            			afOrderService.updateOrderTradePaidDone(message.getContent());
		            			break;
		            		case "taobao_tae_BaichuanTradeClosed":
		            			afOrderService.updateOrderTradeClosed(message.getContent());
		            			break;
		            		default:break;
		            	} 
		            }
		        } catch (Exception e) {  
		            e.printStackTrace();  
		            status.fail();// 消息处理失败回滚，服务端需要重发  
		        }  
			}
		});  
	    try {
			client.connect();
		} catch (LinkException e) {
			e.printStackTrace();
		}  
	}
}
