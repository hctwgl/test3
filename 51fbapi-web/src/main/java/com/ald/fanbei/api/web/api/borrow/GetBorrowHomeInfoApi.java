package com.ald.fanbei.api.web.api.borrow;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBorrowHomeVo;

/**
 * 
 *@类描述：GetBorrowHomeInfoApi
 *@author 何鑫 2017年2月18日  14:46:35
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowHomeInfoApi")
public class GetBorrowHomeInfoApi implements ApiHandle{

	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfBorrowService afBorrowService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		//账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfBorrowHomeVo data = getBorrowHomeInfo(userDto, authDo);
		resp.setResponseData(data);
		return resp;
	}

	private AfBorrowHomeVo getBorrowHomeInfo(AfUserAccountDto userDto,AfUserAuthDo authDo){
		AfBorrowHomeVo vo = new AfBorrowHomeVo();
		vo.setBankcardStatus(authDo.getBankcardStatus());
		vo.setCurrentAmount(userDto.getRepaymentAmount());
		vo.setIvsStatus(authDo.getIvsStatus());
		vo.setMobileStatus(authDo.getMobileStatus());
		vo.setRealnameStatus(authDo.getRealnameStatus());
		vo.setRepayLimitTime(afBorrowService.getReyLimitDate(new Date()));
		vo.setTeldirStatus(authDo.getTeldirStatus());
		vo.setTotalAmount(userDto.getAuAmount());
		vo.setUsableAmount(userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount()));
		vo.setZmStatus(authDo.getZmStatus());
		return vo;
	}
}
