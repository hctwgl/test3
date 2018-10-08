package com.ald.fanbei.web.test.common;

import com.ald.fanbei.api.biz.bo.TokenBo;



/**
 * @类描述：单元测试基类
 * @author zjf 2017年9月26日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class BaseTest {
	
	private TokenBo tokenBo;
	
	/**
	 * 自动向服务端注入登陆令牌
	 */
	public TokenBo init(String userName) {
		tokenBo = new TokenBo();
		return tokenBo;
	}
    
}
