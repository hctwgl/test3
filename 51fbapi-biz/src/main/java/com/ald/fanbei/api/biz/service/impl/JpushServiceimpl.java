package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.JpushUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * 
 *@类描述：
 *@author 何鑫 2017年2月25日  下午15:50:38
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("jPushService")
public class JpushServiceimpl extends BaseService implements JpushService{

	@Resource
	JpushUtil jpushUtil;
	
	private static final String PID               = "pid";
	private static final String PUSH_JUMP_TYPE    = "pushJumpType";
	private static final String DATA              = "data";
	private static final String TIMESTAMP         = "time";
	
	@Override
	public void userInviteSuccess(String userName, String mobile) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("userInviteSuccess,pid=",pid,",mobile=",mobile));
		String msgContext = "您已成功邀请好友【{mobile}】完成注册，提醒好友完成实名认证，您即可获得现金奖励及优惠红包";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "200");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4)); 
		jpushUtil.pushNotifyByAlias("邀请好友注册成功", msgContext, extras, new String[]{userName});
	}

	@Override
	public void chargeMobileError(String userName, String mobile,Date date) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("chargeMobileError,pid=",pid,",mobile=",mobile));
		String msgContext = "您好，抱歉告诉您，您与{date}为【{mobile}的话费充值不成功，话费将原路返还您的账户。您可再次尝试充值！";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "201");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4)); 
		msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_CHINESE_FULL_PATTERN)); 
		jpushUtil.pushNotifyByAlias("手机充值失败", msgContext, extras, new String[]{userName});
	}

	@Override
	public void chargeMobileSucc(String userName, String mobile,Date date) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("chargeMobileSucc,pid=",pid,",mobile=",mobile));
		String msgContext = "您好，您与{date}为【{mobile}】的话费充值成功，感谢您使用51返呗";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "202");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4)); 
		msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_CHINESE_FULL_PATTERN));
		jpushUtil.pushNotifyByAlias("手机充值成功", msgContext, extras, new String[]{userName});
	}

	@Override
	public void dealBorrowConsumeTransfer(String userName, String name) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("dealBorrowConsumeTransfer,pid=",pid,",name=",name));
		String msgContext = "您好，您申请购买【{name}】的消费借款，已经打款，请耐心等待资金到账";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "203");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{name}", name); 
		jpushUtil.pushNotifyByAlias("消费借款打款中", msgContext, extras, new String[]{userName});
	}

	@Override
	public void dealBorrowCashTransfer(String userName, Date date) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("dealBorrowCashTransfer,pid=",pid,",date=",date));
		String msgContext = "您好，您与{date}申请的现金借款，已打款；请耐心等待资金到账";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "204");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_PATTERN_WITH_DOT)); 
		jpushUtil.pushNotifyByAlias("现金打款中", msgContext, extras, new String[]{userName});
	}

	@Override
	public void repayBillSuccess(String userName, String year, String month) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("repayBillSuccess,pid=",pid));
		String msgContext = "您好，您{year}年{month}月账单已还清；";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "205");
		extras.put(DATA, "");
		msgContext = msgContext.replace("{year}", year).replace("{month}", month); 
		jpushUtil.pushNotifyByAlias("账单已还清", msgContext, extras, new String[]{userName});
	}

	@Override
	public void getCashExtracting(String userName) {
		String pid = userName + "_" + System.currentTimeMillis();
		logger.info(StringUtil.appendStrs("getCashExtracting,pid=",pid));
		String msgContext = "您好，您申请的余额提现已打款，请等待到账！";
		Map<String,String> extras = new HashMap<String,String>();
		extras.put(PID, pid);
		extras.put(TIMESTAMP, System.currentTimeMillis()+"");
		extras.put(PUSH_JUMP_TYPE, "206");
		extras.put(DATA, "");
		jpushUtil.pushNotifyByAlias("余额提现打款中", msgContext, extras, new String[]{userName});
	}
	
}
