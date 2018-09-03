package com.ald.fanbei.api.web.validator.intercept;

import com.ald.fanbei.api.web.common.Context;
/**
 * 
 *@类描述：数据校验拦截器接口
 *@author 江荣波  2017年12月29日 下午11:51:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface Interceptor {
	void intercept(Context context);
}
