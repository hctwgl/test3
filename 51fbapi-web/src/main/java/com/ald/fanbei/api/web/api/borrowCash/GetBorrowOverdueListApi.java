/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午2:15:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowOverdueListApi")
public class GetBorrowOverdueListApi implements ApiHandle {

	/* (non-Javadoc)
	 * @see com.ald.fanbei.api.web.common.ApiHandle#process(com.ald.fanbei.api.web.common.RequestDataVo, com.ald.fanbei.api.common.FanbeiContext, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
