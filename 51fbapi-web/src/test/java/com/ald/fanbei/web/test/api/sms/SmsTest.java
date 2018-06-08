package com.ald.fanbei.web.test.api.sms;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.biz.third.util.YSSmsUtil;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.web.test.common.BaseTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SmsTest extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
//	String urlBase = "https://testapi.51fanbei.com";
	String urlBase = "http://localhost:8080";
//	String userName = "13638668564";	//田建成 cardId:3111464419 支付密码123456
//	String userName = "15669066271";	//田建成 cardId:3111464125 支付密码123456
	String userName = "13958004662";	//胡朝永 支付密码123456
//	String userName = "13460011555";	//张飞凯 支付密码123456
//	String userName = "15293971826";	//秦继强 支付密码888888
//	String userName = "13370127054";	//王卿 	支付密码123456
//	String userName = "13656648524";	//朱玲玲 支付密码123456
//	String userName = "13510301615";	//王绪武 支付密码123456
//	String userName = "17756648524";	//新账号 支付密码123456
	
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
	public void SmsTest() {
		YSSmsUtil.send("13018933980","测试","1");
//		String url = urlBase + "/h5/loan/getLoanHomeInfo";
//		testH5(url, null, userName, true);
	}

	@Test
	public void MD5(){
		String pwd = "jkcryy0529";
		String newPwd = MD5.md5(pwd);

		System.out.println(newPwd);
	}
}
