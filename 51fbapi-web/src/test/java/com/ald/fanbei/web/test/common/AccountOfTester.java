package com.ald.fanbei.web.test.common;

public enum AccountOfTester {
	朱玲玲("13656648524", "123456", ""),
	田建成("15669066271", "123456", "3111464125"),
	胡潮永("13958004662", "123456", ""),
	张飞凯("13460011555", "123456", ""),
	秦继强("15293971826", "888888", ""),
	王卿("13370127054", "123456", "");
	
	public String mobile;
	public String payPwd;
	
	AccountOfTester(String mobile, String payPwd, String cardId){
		this.mobile = mobile;
		this.payPwd = payPwd;
	}
}
