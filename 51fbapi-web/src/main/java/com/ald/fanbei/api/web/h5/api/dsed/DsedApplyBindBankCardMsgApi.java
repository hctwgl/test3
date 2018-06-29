package com.ald.fanbei.api.web.h5.api.dsed;


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
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedBankDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.validator.Validator;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *@类现描述：申请绑卡银行短信
 *@author jilong
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedApplyBindBankCardMsgApi")
@Validator("bankCardBindParam")
public class DsedApplyBindBankCardMsgApi implements DsedH5Handle {

    @Resource
    private DsedUserService dsedUserService;

    @Resource
    private DsedUserBankcardService dsedUserBankcardService;

    @Resource
    private DsedBankService dsedBankService;

    @Resource
    UpsUtil upsUtil;

    @Override
    public DsedH5HandleResponse process(Context context)  {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "请求成功");
        String bankNo = ObjectUtils.toString(context.getData("bankNo"), null);
        String bankName = ObjectUtils.toString(context.getData("bankName"), null);
        String bankMobile = ObjectUtils.toString(context.getData("bankMobile"), null);
        String validDate = ObjectUtils.toString(context.getData("validDate"), "");
        String safeCode = ObjectUtils.toString(context.getData("safeCode"), "");
        String openId = ObjectUtils.toString(context.getData("userId"), null);
        DsedUserDo user=dsedUserService.getByOpenId(openId);
       //判断是否已经被绑定
        if(dsedUserBankcardService.getUserBankByCardNo(bankNo)>0){
            return new DsedH5HandleResponse(1545, FanbeiExceptionCode.DSED_BANK_BINDED.getDesc());
        }


     //默认赋值为借记卡
     String cardType = "00";
     //获取用户身份信息
     DsedUserDo userDo=dsedUserService.getById(user.getRid());

     DsedBankDo bank=dsedBankService.getBankByName(bankName);
     //调用ups
     UpsAuthSignRespBo upsResult = upsUtil.authSign(user.getRid().toString(), userDo.getRealName(), bankMobile, userDo.getIdNumber(), bankNo, "02",
             bank.getBankCode(),cardType,validDate,safeCode);

     if(!upsResult.isSuccess()){
         return new DsedH5HandleResponse(1542, FanbeiExceptionCode.AUTH_BINDCARD_ERROR.getDesc());
      }else if(!"10".equals(upsResult.getNeedCode())){
          return new DsedH5HandleResponse(1567, FanbeiExceptionCode.AUTH_BINDCARD_SMS_ERROR.getErrorMsg());
      }
     //是否是设主卡
      String isMain = YesNoStatus.NO.getCode();
      if(dsedUserBankcardService.getUserBankCardInfoByUserId(user.getRid()).size()==0){
          isMain=YesNoStatus.YES.getCode();
      }
      //创建用户银行卡新加状态
      DsedUserBankcardDo userBankcard= buildUserCard( bank.getBankCode(),bankName,bankNo,bankMobile, user.getRid(),isMain,validDate,safeCode);
      dsedUserBankcardService.addUserBankcard(userBankcard);
      Map<String,Object> map = new HashMap<String,Object>();
      map.put("busiFlag",userBankcard.getRid());
      resp.setData(map);
      return resp;
    }

    private DsedUserBankcardDo buildUserCard(String bankCode, String bankName, String cardNumber, String mobile, Long userId, String isMain,String validDate,String safeCode){
        DsedUserBankcardDo bank = new DsedUserBankcardDo();
        bank.setBankCode(bankCode);
        bank.setBankName(bankName);
        bank.setCardNumber(cardNumber);
        bank.setIsMain(isMain);
        bank.setMobile(mobile);
        bank.setStatus(BankcardStatus.NEW.getCode());
        bank.setUserId(userId);
        bank.setValidDate(validDate);
        bank.setSafeCode(safeCode);
        return bank;
    }
}
