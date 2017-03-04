package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
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
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Date now =new Date();
		//账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Map<String,Integer> map = afBorrowService.getCurrentYearAndMonth("",now);
		AfBorrowHomeVo data = getBorrowHomeInfo(now,afBorrowBillService.getMonthlyBillByStatus(userId, map.get("year"), map.get("month"), YesNoStatus.NO.getCode()),userDto, authDo);
		resp.setResponseData(data);
		return resp;
	}

	private AfBorrowHomeVo getBorrowHomeInfo(Date now,BigDecimal repaymentAmount,AfUserAccountDto userDto,AfUserAuthDo authDo){
		AfBorrowHomeVo vo = new AfBorrowHomeVo();
		vo.setBankcardStatus(authDo.getBankcardStatus());
    	vo.setRealName(userDto.getRealName());
        if(StringUtil.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())){
        	String publicKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        	vo.setYdKey(publicKey);
        	vo.setYdUrl(ConfigProperties.get(Constants.CONFKEY_YOUDUN_NOTIFY));
        	vo.setIdNumber(Base64.encodeString(userDto.getIdNumber()));
        }
		vo.setCurrentAmount(repaymentAmount);
		vo.setIvsStatus(authDo.getIvsStatus());
		vo.setMobileStatus(authDo.getMobileStatus());
		vo.setRealnameStatus(authDo.getRealnameStatus());
		vo.setRepayLimitTime(afBorrowService.getReyLimitDate(now));
		vo.setTeldirStatus(authDo.getTeldirStatus());
		vo.setTotalAmount(userDto.getAuAmount());
		vo.setUsableAmount(userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount()));
		vo.setZmStatus(authDo.getZmStatus());
		vo.setGmtZm(authDo.getGmtZm());
		if(StringUtil.equals(authDo.getRealnameStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())){
			String authParamUrl =  ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
			vo.setZmxyAuthUrl(authParamUrl);
		}
		if(repaymentAmount.compareTo(BigDecimal.ZERO)==0){
			vo.setStatus(BorrowBillStatus.YES.getCode());
		}else{
			if(now.after(vo.getRepayLimitTime())){
				vo.setStatus(BorrowBillStatus.OVERDUE.getCode());
			}else{
				vo.setStatus(BorrowBillStatus.NO.getCode());
			}
		}
		
		return vo;
	}
}
