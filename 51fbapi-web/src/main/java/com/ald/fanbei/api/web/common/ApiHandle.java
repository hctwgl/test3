
package com.ald.fanbei.api.web.common;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.FanbeiContext;


/**
 * 
 *@类描述：api处理接口
 *@author 陈金虎 2017年1月16日 下午11:51:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface ApiHandle {
	Logger logger = LoggerFactory.getLogger(ApiHandle.class);
    ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request);
}
