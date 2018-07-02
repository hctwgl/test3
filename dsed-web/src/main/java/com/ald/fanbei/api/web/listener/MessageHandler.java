package com.ald.fanbei.api.web.listener;

import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageStatus;

/**
 * 
 *@类描述：阿里百川消息推送接口
 *@author 何鑫 2017年2月4日 下午3:03:35
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MessageHandler {

	/**
	*消息服务客户端收到消息后，会回调该方法处理具体的业务，处理结果可以通过以下两种方式来表述:
	<li>抛出异常或设置status.fail()表明消息处理失败，需要消息服务端重发
	<li>不抛出异常，也没有设置status信息，则表明消息处理成功，消息服务端不会再投递此消息
	@param message消息内容
	@param status处理结果，如果调用status.fail()，消息服务将会择机重发消息；否则，消息服务认为消息处理成功
	@throws Exception消息处理失败，消息服务将会择机重发消息
	*/
	public void onMessage(Message message,MessageStatus status)throws Exception;
}
