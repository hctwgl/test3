package com.ald.fanbei.web.test.api.borrowlegal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class RepayDoTest  extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "http://192.168.96.216:8080";
	String userName = "13656648524";
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	@Test
	public void  repayDo() {
		String url = urlBase + "/legalborrow/repayDo";
		Map<String,String> params = new HashMap<>();
		params.put("repaymentAmount", "100");
		params.put("userCouponId", "");
		params.put("rebateAmount", "");
		params.put("payPwd", DigestUtils.md5Hex("123456"));
		params.put("cardId", "3111464117");
		params.put("actualAmount", "100");
		params.put("borrowId", "1259619");
		params.put("from", "INDEX");
		
		testApi(url, params, userName ,true);
	}
	
}
