/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月22日下午8:57:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteBankCardApi")
public class DeleteBankCardApi implements ApiHandle {


	@Resource
	AfUserBankcardService afUserBankcardService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
        Map<String, Object> params = requestDataVo.getParams();
        Long bankId = NumberUtil.objToLongDefault(ObjectUtils.toString(params.get("bankId")), 0);
        AfUserBankcardDo afUserBankcardDo = new AfUserBankcardDo();
        afUserBankcardDo.setRid(bankId);
       afUserBankcardDo.setUserId(userId);
       afUserBankcardDo.setStatus("U");
        if(afUserBankcardService.updateUserBankcard(afUserBankcardDo)>0){
       		return resp;
          }
        
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
