package com.ald.fanbei.web.test.common;

public enum AccountOfTester {
	朱玲玲("13656648524", 18637963409L, "123456", 0L),
	田建成("15669066271", 0L, "123456", 0L),
	胡潮永("13958004662", 0L, "123456", 0L),
	车("18258023758", 0L, "123456", 0L),
	张飞凯("13460011555", 0L, "123456", 0L),
	秦继强("15293971826", 0L, "888888", 0L),
	王卿("13370127054", 0L, "123456", 0L),
	俞佳楠("13165995223", 18637963644L, "", 0L),
	代秋天("17612158083  ", 0L, "", 0L),
	夏枫("18519202996  ", 0L, "", 0L);

	public String mobile;
	public Long userId;
	public String payPwd;
	public Long cardId;
	
	AccountOfTester(String mobile, Long userId, String payPwd, Long cardId){
		this.mobile = mobile;
		this.userId = userId;
		this.payPwd = payPwd;
		this.cardId = cardId;
	}
}
