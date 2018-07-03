package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.third.util.JpushUtilV2;
import com.ald.fanbei.api.common.exception.FanbeiException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.JpushUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * 
 * @类描述：
 * @author 何鑫 2017年2月25日 下午15:50:38
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("jPushService")
public class JpushServiceimpl extends BaseService implements JpushService {
	@Autowired
	JpushUtilV2 jpushUtil;
        @Resource 
        AfResourceService afResourceService;
	@Resource
	private AfResourceDao afResourceDao;
	private static final String PID = "pid";
	private static final String PUSH_JUMP_TYPE = "pushJumpType";
	private static final String DATA = "data";
	private static final String TIMESTAMP = "time";

	
	@Override
	public void userInviteSuccess(String userName, String mobile) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("userInviteSuccess,pid=", pid, ",mobile=", mobile));
			String msgContext = "您已成功邀请好友【{mobile}】完成注册，提醒好友完成实名认证，您即可获得现金奖励及优惠红包";
			
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "200");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4));

			jpushUtil.pushNotifyByAlias("邀请好友注册成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("userInviteSuccess error:", e);
		}
	}

	@Override
	public void chargeMobileError(String userName, String mobile, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("chargeMobileError,pid=", pid, ",mobile=", mobile));
			String msgContext = "您好，抱歉告诉您，您于{date}为【{mobile}的话费充值不成功，话费将原路返还您的账户。您可再次尝试充值！";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "201");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4));
			msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_CHINESE_FULL_PATTERN));
			jpushUtil.pushNotifyByAlias("手机充值失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("chargeMobileError error", e);
		}
	}

	@Override
	public void chargeMobileSucc(String userName, String mobile, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("chargeMobileSucc,pid=", pid, ",mobile=", mobile));
			String msgContext = "您好，您于{date}为【{mobile}】的话费充值成功，感谢您使用爱上街";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "202");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{mobile}", StringUtil.strToSecret(mobile, 3, 4));
			msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_CHINESE_FULL_PATTERN));
			jpushUtil.pushNotifyByAlias("手机充值成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("chargeMobileSucc error:", e);
		}

	}

	@Override
	public void repayBillSuccess(String userName, String year, String month) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("repayBillSuccess,pid=", pid));
			String msgContext = "您好，您{year}年{month}月账单已还清；";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "205");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{year}", year).replace("{month}", month);
			jpushUtil.pushNotifyByAlias("账单已还清", msgContext, extras, new String[] { userName });
		}
		catch (FanbeiException e){
			
		}
		catch (Exception e) {
			logger.info("repayBillSuccess error", e);
		}

	}

	@Override
	public void getCashExtracting(String userName) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("getCashExtracting,pid=", pid));
			String msgContext = "您好，您申请的余额提现已打款，请等待到账！";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "206");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("余额提现打款中", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("getCashExtracting error", e);
		}

	}

	@Override
	public void getSignCycle(String userName) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("getSignCycle,pid=", pid));
			String msgContext = "恭喜您已成功完成一个周期的签到，获得多张优惠券奖励，请再接再厉>";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "207");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("签到奖励", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("getSignCycle error", e);
		}
	}


	@Override
	public void refundMobileError(String userName, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("refundMobileError,pid=", pid));
			String msgContext = "抱歉告诉您，您于{date}的手机充值失败，请联系{phone}进行人工退款";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "208");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_PATTERN_WITH_HYPHEN));
			msgContext = msgContext.replace("{phone}", Constants.DEFAULT_SERVICE_PHONE);
			jpushUtil.pushNotifyByAlias("手机充值失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("refundMobileError error", e);
		}
	}

	@Override
	public void dealBorrowConsumeTransfer(String userName, String name) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowConsumeTransfer,pid=", pid, ",name=", name));
			String msgContext = "您好，您申请购买【{name}】的消费借款，已经打款，请耐心等待资金到账";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "203");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{name}", name);
			jpushUtil.pushNotifyByAlias("消费借款打款中", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("dealBorrowConsumeTransfer error:", e);
		}
	}

	@Override
	public void dealBorrowCashTransfer(String userName, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowCashTransfer,pid=", pid, ",date=", date));
			String msgContext = "您好，您于{date}申请的现金借款，已打款；请耐心等待资金到账";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, "");
			msgContext = msgContext.replace("{date}", DateUtil.formatDate(date, DateUtil.DEFAULT_PATTERN_WITH_DOT));
			jpushUtil.pushNotifyByAlias("现金打款中", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("dealBorrowCashTransfer error", e);
		}
	}

	@Override
	public void dealBorrowCashApplySuccss(String userName, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowCashApplySuccss,pid=", pid, ",date=", date));
			String msgContext = "您的借款申请已经通过，银行系统已经开始处理您的打款需求，预计3—5分钟左右到账，请注意查看您的银行卡。";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "209");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("借钱申请成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("dealBorrowCashTransfer error", e);
		}
	}

	@Override
	public void dealBorrowCashApplyFail(String userName, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowCashTransfer,pid=", pid, ",date=", date));
			String msgContext = "亲，您的信用还需努力，本次借款申请没有通过。继续保持好的信用生活习惯，离下次成功借款只差一步了。";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "210");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("借钱申请失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("dealBorrowCashTransfer error", e);
		}
	}

    @Override
    public void dealBorrowCashApplyFailForKoudai(String userName, Date date, String content) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowCashTransfer,pid=", pid, ",date=", date));
			String msgContext = content;
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "210");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("借钱申请失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("dealBorrowCashTransfer error", e);
		}
    }

    @Override
	public void repayRenewalSuccess(String userName) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("repayRenewalSuccess,pid=", pid));
			String msgContext = "恭喜，您的续期申请已通过！";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "212");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("续期申请成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("repayRenewalSuccess error:", e);
		}
	}
	
	@Override
	public void repayRenewalFail(String userName) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("repayRenewalFail,pid=", pid));
			String msgContext = "亲，您的续期申请失败，请重新申请！";
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "213");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("续期申请失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("repayRenewalFail error", e);
		}
	}
	@Override
	public void dealBorrowApplyFail(String userName, Date date) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("dealBorrowApplyFail,pid=",pid,",date=",date));
			String msgContext = "亲，您的信用还需努力，本次分期购买申请没有通过。继续保持好的信用生活习惯，离下次成功只差一步了。";
			Map<String,String> extras = new HashMap<String,String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis()+"");
			extras.put(PUSH_JUMP_TYPE, "210");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("分期购买申请审核失败", msgContext, extras, new String[]{userName});
		} catch (Exception e) {
			logger.info("dealBorrowApplyFail error",e);
		}
		
	}

	@Override
	public void gameShareSuccess(String userName) {
		try {
			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes("APP_POP_IMAGE");
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("gameShareSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, afResourceDo.getValue() + "," + afResourceDo.getName());
			jpushUtil.pushMessageByAlias("恭喜","你获得一次抽现金机会分享越多机会越多",extras,new String[]{userName}, false);
		} catch (Exception e) {
			logger.info("gameShareSuccess error", e);
		}

	}
	
	@Override
	public void strongRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您的信用认证已经通过审核。可以立即去分期和借钱啦~";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("strongRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "200");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("信用认证通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("strongRiskSuccess error", e);
		}

	}

	@Override
	public void strongRiskFail(String userName) {
		try {
			String msgContext = "VIP特权，给您挑选几个额度高，要求低，下款快的借钱平台，50000额度等你提取！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("strongRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "200");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("额度不够用？爱上街借贷超市，帮你解决", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("strongRiskFail error", e);
		}
	}

	@Override
	public void mobileRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过运营商认证！马上去下一步认证吧。您离获得信用额度越来越近了哦。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("mobileRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "200");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("运营商认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("mobileRiskSuccess error", e);
		}
	}

	@Override
	public void mobileRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次认证未通过！您可要核对身份信息后,重新尝试认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("mobileRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "200");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("运营商认证失败", msgContext, extras, new String[] { userName });

		} catch (Exception e) {
			logger.info("mobileRiskFail error", e);
		}
	}

	@Override
	public void pushHeaderImage(String userName) {
		try {
			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes("APP_POP_IMAGE");
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("gameShareSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, afResourceDo.getValue() + "," + afResourceDo.getValue2());
			jpushUtil.pushMessageByAlias("恭喜","你获得一次抽现金机会分享越多机会越多",extras,new String[]{userName}, false);
		} catch (Exception e) {
			logger.info("gameShareSuccess error", e);
		}
	}

	@Override
	public void pushAllHeaderImage() {
		try {
			String userName = StringUtils.EMPTY;
			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes("APP_POP_IMAGE");
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("gameShareSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, afResourceDo.getValue() + "," + afResourceDo.getValue2());
			jpushUtil.pushMessageByAlias("恭喜","你获得一次抽现金机会分享越多机会越多",extras,new String[]{userName}, true);
		} catch (Exception e) {
			logger.info("gameShareSuccess error", e);
		}
		
	}

	
	
	@Override
	public void fundRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过公积金认证！马上去下一步认证吧。可以获得更高额度哦！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("fundRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "222");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("公积金认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("fundRiskSuccess error", e);
		}		
	}

	@Override
	public void fundRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次公积金认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("fundRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "223");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("公积金认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("fundRiskFail error", e);
		}		
	}

	@Override
	public void fundRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次公积金认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("fundRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "223");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("公积金认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("fundRiskFail error", e);
		}		
	}
	
	@Override
	public void socialSecurityRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过社保认证！马上去下一步认证吧。可以获得更高额度哦！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("socialSecurityRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "224");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("社保认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("socialSecurityRiskSuccess error", e);
		}	
	}

	@Override
	public void socialSecurityRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次社保认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("socialSecurityRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "225");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("社保认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("socialSecurityRiskFail error", e);
		}
	}

	@Override
	public void socialSecurityRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次社保认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("socialSecurityRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "225");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("社保认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("socialSecurityRiskFail error", e);
		}
	}
	
	@Override
	public void creditCardRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过信用卡认证！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("creditCardRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "226");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("信用卡认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("creditCardRiskSuccess error", e);
		}			
	}

	@Override
	public void creditCardRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次信用卡认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("creditCardRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "227");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("信用卡认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("creditCardRiskFail error", e);
		}		
	}

	@Override
	public void creditCardRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次信用卡认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("creditCardRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "227");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("信用卡认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("creditCardRiskFail error", e);
		}		
	}
	
	@Override
	public void alipayRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过支付宝认证！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("alipayRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "228");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("支付宝认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("alipayRiskSuccess error", e);
		}	
	}

	@Override
	public void alipayRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次支付宝认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("alipayRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("支付宝认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("alipayRiskFail error", e);
		}	
	}
	@Override
	public void alipayRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次支付宝认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("alipayRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("支付宝认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("alipayRiskFail error", e);
		}	
	}	


	public  void jPushByType(int jumpType, String type,String userName){
		try {

			if(userName == null && userName.equals("")){
				userName = StringUtils.EMPTY;
			}

			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes("APP_POP_IMAGE_"+type);
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("gameShareSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE,  String.valueOf(jumpType));
			extras.put(DATA, afResourceDo.getValue() + "," + type);

			if(userName.equals("") ){
				jpushUtil.pushMessageByAlias(afResourceDo.getValue1(),afResourceDo.getValue2(),extras,new String[]{userName}, true);
			}
			else{
				jpushUtil.pushMessageByAlias(afResourceDo.getValue1(),afResourceDo.getValue2(),extras,new String[]{userName}, false);
			}

		} catch (Exception e) {
			logger.info("gameShareSuccess error", e);
		}
	}

	public  void jPushCoupon(String type,String userName){
		
		try {
			logger.info("jPushCoupon type=>" + type + " userName=>" + userName);
			//String userName = StringUtils.EMPTY;
			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes("COUPON_POPUPS");
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("gameShareSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, afResourceDo.getValue() + "," + afResourceDo.getValue2());
			jpushUtil.pushMessageByAlias("恭喜","你获得一次抽现金机会分享越多机会越多",extras,new String[]{userName}, false);
		} catch (Exception e) {
			logger.info("gameShareSuccess error", e);
		}
	}

	public void jPushPopupWnd(String type,String userName) {
		try {
			logger.info("jPushCoupon type=>" + type + " userName=>" + userName);
			//String userName = StringUtils.EMPTY;
			List<AfResourceDo> resourceDoList = afResourceDao.getConfigByTypes(type);
			AfResourceDo afResourceDo = resourceDoList.get(0);
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("jPushPopupWnd,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "204");
			extras.put(DATA, afResourceDo.getValue() + "," + afResourceDo.getValue2());
			jpushUtil.pushMessageByAlias("弹框","弹框",extras,new String[]{userName}, false);
		} catch (Exception e) {
			logger.info("push wnd  error", e);
		}
	}

	@Override
	  public void pushBorrowCashActivitys(String userName,String money,String type) {
	    try {
	      String pid = userName + "_" + System.currentTimeMillis();
	      logger.info(StringUtil.appendStrs("pushBorrowCashActivitys,pid=", pid));
	      String msgContext = null;
	      String title=null;
	      if(type.equals("One")){
	        title="头号“金”喜！";
	        msgContext ="恭喜成为最幸运“破十五亿”用户，10000元现金红包已发放，立即前往";
	      } else if (type.equals("Win")){
	        title="头号“金”喜！";
	        msgContext = "哇！幸运值爆棚的你在“破十五亿”活动中获得"+money+"元现金红包，快去看看吧";
	      } else {
	        title="破十五亿 有“金”喜";
	        msgContext = "又有10位幸运用户已获得"+money+"元现金红包，快来看看有你吗";
	      }
	      Map<String, String> extras = new HashMap<String, String>();
	      extras.put(PID, pid);
	      extras.put(TIMESTAMP, System.currentTimeMillis() + "");
	      extras.put(PUSH_JUMP_TYPE, "231");
	      extras.put(DATA, "");
	      jpushUtil.pushNotifyByAlias(title, msgContext, extras, new String[] { userName });
	    } catch (Exception e) {
	      logger.info("pushBorrowCashActivitys error", e);
	    }

	  }

	@Override
	public void reservationActivity(String userName,String msgContext) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("getSignCycle,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "231");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("iPhoneX预约", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("getSignCycle error", e);
		}
	}
	@Override
	public void pushSharedTips(String userName,String msgContext) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("pushSharedTips,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "231");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("邀请使用二维码", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("getSignCycle error", e);
		}
	}

	@Override
	public void chsiRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次学信网认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("chsiRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("学信网认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("chsiRiskFail error", e);
		}	
	}

	@Override
	public void chsiRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次学信网认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("chsiRiskFault,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("学信网认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("chsiRiskFault error", e);
		}	
	}

	@Override
	public void chsiRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过学信网认证！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("chsiRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "228");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("学信网认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("chsiRiskSuccess error", e);
		}	
	}

	@Override
	public void zhengxinRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过人行征信认证！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("zhengxinRiskSuccess,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "228");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("人行征信认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("zhengxinRiskSuccess error", e);
		}	
		
	}

	@Override
	public void zhengxinRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次人行征信认证未通过。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("zhengxinRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("人行征信认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("zhengxinRiskFail error", e);
		}	
		
	}

	@Override
	public void zhengxinRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次人行征信认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("zhengxinRiskFault,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("人行征信认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("zhengxinRiskFault error", e);
		}	
		
	}
	
	@Override
	public void ecommerceRiskSuccess(String userName) {
		try {
			String msgContext = "恭喜，您已通过电商认证！马上去下一步认证吧。可以获得更高额度哦！";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("ecommerceRisk Success,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "232");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("电商认证成功", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("ecommerceRisk error", e);
		}		
	}

	@Override
	public void ecommerceRiskFail(String userName) {
		try {
			String msgContext = "您好，您本次电商认证未通过";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("ecommerceRiskFail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "233");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("电商认证未通过", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("ecommerceRiskFail error", e);
		}		
	}

	@Override
	public void ecommerceRiskFault(String userName) {
		try {
			String msgContext = "您好，您本次电商认证失败，稍后请重新进行认证。";
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("ecommerceRisk fail,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "234");
			extras.put(DATA, "");
			jpushUtil.pushNotifyByAlias("电商认证失败", msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("ecommerceRiskFault error", e);
		}		
	}
	@Override
	public void boluomeActivityMsg(String userName,String type,String secType) {
		try {
		    
		        //从af_resource 取得配置信息
		    	AfResourceDo resourceInfo = afResourceService.getConfigByTypesAndSecType(type, secType);
		    	if(resourceInfo!= null){
        			String msgContext = resourceInfo.getValue();
        			String pid = userName + "_" + System.currentTimeMillis();
        			logger.info(StringUtil.appendStrs("boluomeActivityMsg,pid=", pid));
        			Map<String, String> extras = new HashMap<String, String>();
        			extras.put(PID, pid);
        			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
        			extras.put(PUSH_JUMP_TYPE, "229");
        			extras.put(DATA, "");
        			jpushUtil.pushNotifyByAlias(resourceInfo.getValue1(), msgContext, extras, new String[] { userName });
		    }
		} catch (Exception e) {
			logger.info("boluomeActivityMsg error", e);
		}	
		
	}

	@Override
	public void send15Coupon(String userName) {
		try {
			String msgContext = "恭喜您邀请好友成功！5元无门槛外卖券已发放到您的账户，请记得查收并使用！";
			String title = "恭喜您邀请好友成功";
			logger.info("send15Coupon params ：　userName = {} , middle business params : msgContext = {} ,  title = {}",userName,msgContext,title);
			commonMethod(userName,msgContext,title);
		} catch (Exception e) {
			logger.info("send15Coupon error", e);
		}	
		
		
	}
	/**
	 * 
	* @Title: commonMethod
	* @author qiao
	* @date 2017年11月17日 下午4:40:31
	* @Description: 一般发短信的接口可以调用的通用接口
	* @param userName
	* @param msgContext    
	* @return void   
	* @throws
	 */
	public void commonMethod(String userName,String msgContext,String title) {
		try {
			String pid = userName + "_" + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("commonMethod,pid=", pid));
			Map<String, String> extras = new HashMap<String, String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "229");
			extras.put(DATA, "");
			logger.info("commonMethod params : userName = {} , msgContext = {} , title = {} , pushNotifyByAlias params : extras = {}" ,new Object[]{userName,msgContext,title,extras});
			jpushUtil.pushNotifyByAlias(title, msgContext, extras, new String[] { userName });
		} catch (Exception e) {
			logger.info("zhengxinRiskFault error", e);
		}	
		
	}

	@Override
	public void sendRebateMsg(String userName, String scence, BigDecimal rebateAmount) {
		try {
			logger.info("sendRebateMsg params : userName = {} , scence = {} , rebateAmount = {}" ,new Object[]{userName,scence,rebateAmount});
			String msgContext = "您已参与“" + scence + "”场景获得" + rebateAmount + "元惊喜返利金！参与越多，奖励越多，最高领取188元>>>";
			String errorMsg = "恭喜您赢得返利";
			commonMethod(userName,msgContext,errorMsg);
		} catch (Exception e) {
			logger.info("send15Coupon error", e);
		}
		
	}

	/**
	 * 新版借款流程消息和通知推送
	 */
	@Override
	public void pushUtil(String title,String msgContent,String userName){
		try {
			String pid =userName + System.currentTimeMillis();
			logger.info(StringUtil.appendStrs("pushUtil,pid=", pid, "userName=", userName));
			logger.info("msgcontent="+msgContent+",userName="+userName+",title="+title);
			Map<String,String> extras = new HashMap<String,String>();
			extras.put(PID, pid);
			extras.put(TIMESTAMP, System.currentTimeMillis() + "");
			extras.put(PUSH_JUMP_TYPE, "1");
			jpushUtil.pushNotifyByAlias(title,msgContent,extras,new String[]{userName},"1","","3");
		}catch (Exception e){
			logger.info("pushUtil error", e);
		}

	}

}