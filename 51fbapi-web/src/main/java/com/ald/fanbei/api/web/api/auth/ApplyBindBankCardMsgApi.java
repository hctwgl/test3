package com.ald.fanbei.api.web.api.auth;


import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.service.DsedBankService;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedBankDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyBindBankcardParam;
import com.ald.fanbei.api.web.validator.bean.BankCardBindParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *@类现描述：申请绑卡银行短信
 *@author jilong
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBindBankcardApi")
@Validator("bankCardBindParam")
public class ApplyBindBankCardMsgApi implements ApiHandle {

    @Resource
    private DsedUserService dsedUserService;

    @Resource
    private DsedUserBankcardService dsedUserBankcardService;

    @Resource
    private DsedBankService dsedBankService;

    @Resource
    UpsUtil upsUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        BankCardBindParam param = (BankCardBindParam) requestDataVo.getParamObj();
        Long userId = context.getUserId();


       //判断是否已经被绑定
        if(dsedUserBankcardService.getUserBankByCardNo(param.bankNo)>0){
            throw new FanbeiException("user bankcard exist error", FanbeiExceptionCode.USER_BANKCARD_EXIST_ERROR);
        }


     //默认赋值为借记卡
     String cardType = "00";
     //获取用户身份信息
     DsedUserDo userDo=dsedUserService.getById(userId);

     DsedBankDo bank=dsedBankService.getBankByName(param.bankName);
     //调用ups
     UpsAuthSignRespBo upsResult = upsUtil.authSign(userId.toString(), userDo.getRealName(), param.bankMobile, userDo.getIdNumber(), param.bankNo, "02",
             bank.getBankCode(),cardType,param.validDate,param.safeCode);

     if(!upsResult.isSuccess()){
         return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_ERROR);
      }else if(!"10".equals(upsResult.getNeedCode())){
          return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_SMS_ERROR);
      }
     //是否是设主卡
     String isMain = YesNoStatus.NO.getCode();
      if(dsedUserBankcardService.getUserBankCardInfoByUserId(userId).size()==0){
          isMain=YesNoStatus.YES.getCode();
      }
      //创建用户银行卡新加状态
      DsedUserBankcardDo userBankcard= buildUserCard( bank.getBankCode(),param.bankName,param.bankNo,param.bankMobile,userId,isMain);
      dsedUserBankcardService.addUserBankcard(userBankcard);
      Map<String,Object> map = new HashMap<String,Object>();
      map.put("userBankcardId",userBankcard.getRid());
      resp.setResponseData(map);
      return resp;
    }

    private DsedUserBankcardDo buildUserCard(String bankCode, String bankName, String cardNumber, String mobile, Long userId, String isMain){
        DsedUserBankcardDo bank = new DsedUserBankcardDo();
        bank.setBankCode(bankCode);
        bank.setBankName(bankName);
        bank.setCardNumber(cardNumber);
        bank.setIsMain(isMain);
        bank.setMobile(mobile);
        bank.setStatus(BankcardStatus.NEW.getCode());
        bank.setUserId(userId);
        return bank;
    }
}
