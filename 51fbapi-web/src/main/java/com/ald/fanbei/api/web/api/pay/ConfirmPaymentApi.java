/**
 * 
 */
package com.ald.fanbei.api.web.api.pay;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsQuickPayConfirmRespBo;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：快捷支付确认支付
 * @author chenqiwei 2018年3月29日下午15:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("confirmPaymentApi")
public class ConfirmPaymentApi implements ApiHandle {
    
	@Resource
	AfUserBankcardDao afUserBankcardDao;
	@Resource
	UpsUtil upsUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	
	//String clientType = ObjectUtils.toString(requestDataVo.getParams().get("clientType"),null);
   //String orderNo = ObjectUtils.toString(requestDataVo.getParams().get("orderNo"),null);
	String smsCode = ObjectUtils.toString(requestDataVo.getParams().get("smsCode"),null);
	String tradeNo = ObjectUtils.toString(requestDataVo.getParams().get("tradeNo"),null);
    Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
    
    
  //  Map<String, Object> params = requestDataVo.getParams();
    
    if (cardId == null  || tradeNo == null || smsCode == null) {
        logger.error("cardId is empty or smsCode is empty or tradeNo is empty");
        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
    }
//    if(payId <= 0 ){
//    	  logger.info("choose bank card pay orderNo = "+orderNo);
//          return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHOOSE_BANK_CARD_PAY);
//    }
    AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
    if(bank == null ){
    	 return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST);
    }
   // UpsQuickPayConfirmRespBo quickPayConfirm(String tradeNo,String orderNo,String userNo,String smsCode,String cardNo,String bankCode,String clientType, String merPriv){
    	UpsQuickPayConfirmRespBo respBo = upsUtil.quickPayConfirm(tradeNo, context.getUserId().toString(),smsCode, bank.getCardNumber(),bank.getBankCode(),"02" ,"QUICK_PAY_CONFIRM");


		if (!respBo.isSuccess()) {
		    throw new FanbeiException(respBo.getRespDesc());
		}
		resp.addResponseData("data", respBo);

	return resp;
    }


}
