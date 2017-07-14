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
import com.ald.fanbei.api.common.enums.RiskStatus;
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
 *@类描述：获取分呗主页信息api
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
		Map<String,Integer> map = afBorrowService.getCurrentTermYearAndMonth("",now);
		AfBorrowHomeVo data = getBorrowHomeInfo(now,afBorrowBillService.getMonthlyBillByStatus(userId, map.get(Constants.DEFAULT_YEAR), map.get(Constants.DEFAULT_MONTH), YesNoStatus.NO.getCode()),userDto, authDo,context);
		resp.setResponseData(data);
		return resp;
	}

	private AfBorrowHomeVo getBorrowHomeInfo(Date now,BigDecimal repaymentAmount,AfUserAccountDto userDto,AfUserAuthDo authDo,FanbeiContext context){
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
		if(null == authDo.getGmtMobile()){
			vo.setMobileStatus(authDo.getMobileStatus());
		}else{
//			if(StringUtil.equals(MobileStatus.YES.getCode(), authDo.getMobileStatus())&&DateUtil.afterDay(authDo.getGmtMobile(), DateUtil.addMonths(new Date(), 2))){//超过两个月
//				vo.setMobileStatus(MobileStatus.NO.getCode());
//			}else{
				vo.setMobileStatus(authDo.getMobileStatus());
//			}
		}
		vo.setRealNameStatus(authDo.getRealnameStatus());
		vo.setRepayLimitTime(afBorrowService.getReyLimitDate("",now));
		vo.setTeldirStatus(authDo.getTeldirStatus());
		vo.setTotalAmount(userDto.getAuAmount());
		BigDecimal usableAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount());
		if (usableAmount.compareTo(BigDecimal.ZERO)<0) {
			usableAmount = BigDecimal.ZERO;
		}
		vo.setUsableAmount(usableAmount);
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
		vo.setZmScore(authDo.getZmScore());
		vo.setIvsScore(authDo.getIvsScore());
		vo.setRealNameScore(authDo.getRealnameScore());
		vo.setContactorStatus(authDo.getContactorStatus());
		vo.setContactorName(authDo.getContactorName());
		vo.setContactorType(authDo.getContactorType());
		vo.setContactorMobile(authDo.getContactorMobile());
		vo.setLocationAddress(authDo.getLocationAddress());
		vo.setLocationStatus(authDo.getLocationStatus());
		vo.setAllowConsume(afUserAuthService.getConsumeStatus(authDo.getUserId(),context.getAppVersion()));
		vo.setFaceStatus(authDo.getFacesStatus());
		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			vo.setRiskStatus(RiskStatus.A.getCode());
		} else {
			vo.setRiskStatus(authDo.getRiskStatus());
		}
		return vo;
	}
}
