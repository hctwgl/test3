package com.ald.fanbei.web.test.api.loan;

import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.web.test.common.BaseTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class signTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
//	String urlBase = "https://testapi.51fanbei.com";
	String urlBase = "http://localhost:8082";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
//	String userName = "15669066271";	//田建成 cardId:3111464125 支付密码123456
//	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
//	String userName = "13370127054";	//王卿 	支付密码123456
//	String userName = "13656648524";	//朱玲玲 支付密码123456
//	String userName = "13510301615";	//王绪武 支付密码123456
	String userName = "18258023758";	//新账号 支付密码123456

	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	/**
	 * 获取借钱首页详情
	 */
	@Test
	public void getHomeInfo() {
		Map<String,String> params = new HashMap<>();
		String url = urlBase + "/h5/reward/getRewardHomeInfo";
		params.put("userName","18237147019");
		params.put("push","Y");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 获取所有借钱记录，包含白领贷和小额贷记录
	 */
	@Test
	public void getSignReward() {
		String url = urlBase + "/SignRewardInfo/supplementSign";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", "18659115681");
		params.put("verifyCode", "888888");
		params.put("push", "Y");
		params.put("rewardUserId","18258023758");
		params.put("time","9");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 明细
	 */
	@Test
	public void confirmLoan() {
		String url = urlBase + "/taskUser/addBrowseTaskUser";
		Map<String,String> params = new HashMap<>();
		params.put("activityUrl", "http://testapp.51fanbei.com/app/goods/goodsListModel?modelId=194");
		testApi(url, params, userName, true);
	}
	
	/**
	 * 发起贷款申请
	 */
	@Test
	public void applyLoan() {
		String url = urlBase + "/h5/reward/doFinishTask";
		Map<String,String> params = new HashMap<>();
		params.put("taskType", "browse");
		params.put("taskSecType", "category");
		params.put("taskCondition", "279");
		params.put("pageSize","10");
		params.put("pageNo","1");
		params.put("userName","18258023758");
		testH5(url, params, userName, true);
	}

	/**
	 * 贷款申请成功后，模拟 UPS 回调 返呗API
	 */
	@Test
	public void delegatePay() {
		String url = urlBase + "/h5/reward/doShareTask";
		Map<String,String> params = new HashMap<>();
		params.put("taskCondition","https://testh5.51fanbei.com/h5/activity/201805/openRed.html?refreshUrl=false");
		params.put("userName","18258023758");
		testH5(url, params, userName ,true);
	}

	/**
	 * 获取白领贷协议
	 */
	@Test
	public void getWhiteLoanProtocol() {
		String url = urlBase + "/h5/reward/getExtractMoney";
		Map<String,String> params = new HashMap<>();
		params.put("withdrawType", "0");//1038.66
		params.put("userName", "18258023758");
		testH5(url, params, userName, true);
	}

	/**
	 * 按期还款
	 */
	@Test
	public void repayDo() {
		String url = urlBase + "/SignRewardInfo/friendSign";
		Map<String,String> params = new HashMap<>();
		params.put("mobile", "18237147025");
		params.put("verifyCode", "888888");
		params.put("push", "Y");
		params.put("rewardUserId","15293971826");
		params.put("wxCode","2");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 提前结清还款
	 */
	@Test
	public void allRepayDo() {
		String url = urlBase + "/h5/reward/getReceiveReward";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "18258023758");
		params.put("taskId", "93");
		params.put("isDailyUpdate", "0");
		params.put("taskName","所有用户-分享");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 获取贷款详情
	 */
	@Test
	public void getLoanInfo() {
		String url = urlBase + "/mySignInfo/mySign";
		Map<String,String> params = new HashMap<>();
		params.put("userId", "17098711611");
		params.put("push","N");
		testH5(url, params, userName, true);
	}
	
	/**
	 * 获取贷款还款计划
	 */
	@Test
	public void loanRepayPlan() {
		String url = urlBase + "/h5/reward/doPushTask";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "18258023758");
		testH5(url, params, userName, true);
	}
	
	@Test
	public void getLoanRepayments() {
		String url = urlBase + "/h5/reward/getWithdrawDetail";
		Map<String,String> params = new HashMap<>();
		params.put("userName", "18258023758");
		params.put("pageSize", "54");
		params.put("pageNo", "1");
		testH5(url, params, userName, true);
	}
	
	@Test
	public void  collect() {
		String url = urlBase + "/user/addRecommendShared";
		Map<String,String> params = new HashMap<>();
		params.put("shareUrl","https://testh5.51fanbei.com/h5/activity/201805/openRed.html?refreshUrl=false");
		params.put("userName","18258023758");
		testApi(url, params, userName, true);
	}
	
	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		
	}
	
}
