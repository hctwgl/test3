
package com.ald.fanbei.api.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.context.Context;


/**
 * 
 *@类描述：H5处理接口
 *@author 江荣波 2017年1月16日 下午11:51:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface H5Handle {
	
	Logger logger = LoggerFactory.getLogger(H5Handle.class);

	H5HandleResponse process(Context context);
}
