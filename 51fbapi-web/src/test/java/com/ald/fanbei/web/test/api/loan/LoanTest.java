package com.ald.fanbei.web.test.api.loan;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import com.ald.fanbei.web.test.common.BaseTest;

public class LoanTest  extends BaseTest{
	/**
	 * 自测根据自己的业务修改下列属性 TODO
	 */
	String urlBase = "https://btestapp.51fanbei.com";
	String userName = "15669066271";
	
	/**
	 * 自动注入登陆令牌，当needLogin为true时，不得注释此方法
	 */
	@Before
	public void init(){
		super.init(userName);
	}
	
	@Test
	public void getHomeInfo() {
		
	}
	
//	@Test
	public void confirmLoan() {
		
	}
	
//	@Test
	public void applyLoan() {
		
	}

//	@Test
	public void repayDo() {
		
	}
	
//	@Test
	public void  collect() {
		
	}
	
//	@Test
	public void  offlineRepayment() throws UnsupportedEncodingException {
		
	}
	
}
