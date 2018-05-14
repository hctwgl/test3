
package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.common.FanbeiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 *@类描述：h5处理接口
 *@author cfp 2018年5月14日 下午11:51:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface H5ApiHandle {
	//log
	Logger logger = LoggerFactory.getLogger(H5ApiHandle.class);
	
	/**
	 * api处理方法
	 * @param requestDataVo
	 * @param context
	 * @param request
	 * @return
	 */

	ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request,HttpServletResponse response);
}
