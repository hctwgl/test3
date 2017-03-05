package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：ApplyConsumeApi
 *@author 何鑫 2017年2月07日  11:01:26
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyConsumeApi")
public class ApplyConsumeApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Resource
	private AfBorrowService afBorrowService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("amount")), BigDecimal.ZERO);
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
		String openId = ObjectUtils.toString(requestDataVo.getParams().get("openId"));
		String numId = ObjectUtils.toString(requestDataVo.getParams().get("numId"));
		String name = ObjectUtils.toString(requestDataVo.getParams().get("name"));
		int nper = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get(Constants.DEFAULT_NPER)), 2);
		Long userId = context.getUserId();
		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
		if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
		BigDecimal useableAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount());
		
		if(useableAmount.compareTo(amount)==-1){
			throw new FanbeiException("borrow consume money error", FanbeiExceptionCode.BORROW_CONSUME_MONEY_ERROR);
		}
		
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		if(null == card){
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
		//TODO 转账处理 待调试
		/*UpsDelegatePayRespBo upsResult = UpsUtil.delegatePay(amount, userDto.getRealName(), card.getCardNumber(), userId+"", card.getMobile(), card.getBankName(),
				card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02");
		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}*/
		long result = afBorrowService.dealConsumeApply(userDto, amount, card.getRid(), goodsId, openId,numId, name, nper);
		if(result>0){//TODO 以下4行代码可换成  
			resp.addResponseData("refId", result); 
			resp.addResponseData("type", UserAccountLogType.CASH.getCode());
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
