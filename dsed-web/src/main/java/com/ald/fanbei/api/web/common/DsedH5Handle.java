
package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 *@类描述：H5处理接口
 *@author 江荣波 2017年1月16日 下午11:51:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedH5Handle {
	
	Logger logger = LoggerFactory.getLogger(DsedH5Handle.class);

	DsedH5HandleResponse process(Context context);
}
