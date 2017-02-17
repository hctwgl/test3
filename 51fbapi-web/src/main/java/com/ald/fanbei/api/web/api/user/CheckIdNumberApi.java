/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月17日下午6:09:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkIdNumberApi")
public class CheckIdNumberApi implements ApiHandle {

	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		return null;
	}

}
