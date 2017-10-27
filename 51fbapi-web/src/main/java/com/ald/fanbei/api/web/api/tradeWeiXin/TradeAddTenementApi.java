package com.ald.fanbei.api.web.api.tradeWeiXin;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.TradeTenementService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("tradeAddTenementApi")
public class TradeAddTenementApi implements ApiHandle {

    @Resource
    private TradeTenementService tradeTenementService;
    @Resource
    private AfUserService afUserService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,
                                     FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("id"), 0);

        String userName = ObjectUtils.toString(requestDataVo.getParams().get("userName"), "").toString();
        String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"), "").toString();
        String idNumber = ObjectUtils.toString(requestDataVo.getParams().get("idNumber"), "").toString();
        String homeAddress = ObjectUtils.toString(requestDataVo.getParams().get("homeAddress"), "").toString();
        String contractImageUrl = ObjectUtils.toString(requestDataVo.getParams().get("contractImageUrl"), "").toString();
        String rentType = ObjectUtils.toString(requestDataVo.getParams().get("rentType"), "").toString();
        BigDecimal rentOnePrice = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("rentOnePrice"), null);
        BigDecimal rentSumPrice = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("rentSumPrice"), null);
        String beginTime = ObjectUtils.toString(requestDataVo.getParams().get("beginTime"), "").toString();
        String endTime = ObjectUtils.toString(requestDataVo.getParams().get("endTime"), "").toString();
<<<<<<< HEAD
     
        
        
       
=======
        AfIdNumberDo afIdNumberDo= tradeTenementService.getUserIdentityUrl(mobile);
        if(afIdNumberDo==null){
            resp.addResponseData("message", "对不起，该用户尚未进行注册/身份证认证");
            return resp;
        }
        AfTradeTenementInfoDo afTradeTenementInfoDo = null;
        if (id == 0) {
            afTradeTenementInfoDo = new AfTradeTenementInfoDo();
        } else {
            afTradeTenementInfoDo = tradeTenementService.getTenementInfoDoById(id);
            if (afTradeTenementInfoDo.getAuditState() == 1) {
                resp.addResponseData("message", "对不起，您的审核已将完成，无法更改");
                return resp;
            }
        }

>>>>>>> b54f292b942e290726a81dab4f5c26497c55eb28
        afTradeTenementInfoDo.setUserName(userName);
        afTradeTenementInfoDo.setMobile(mobile);
        afTradeTenementInfoDo.setIdNumber(idNumber);
        afTradeTenementInfoDo.setHomeAddress(homeAddress);
        afTradeTenementInfoDo.setContractImageUrl(contractImageUrl);
        afTradeTenementInfoDo.setRentType(rentType);
        afTradeTenementInfoDo.setRentOnePrice(rentOnePrice);
        afTradeTenementInfoDo.setRentSumPrice(rentSumPrice);
        Date beginTimeDate = DateUtil.parseDate(beginTime, "yyyy-MM-dd");
        Date endTimeDate = DateUtil.parseDate(endTime, "yyyy-MM-dd");
        afTradeTenementInfoDo.setBeginTime(beginTimeDate);
        afTradeTenementInfoDo.setEndTime(endTimeDate);
        afTradeTenementInfoDo.setBusinessId(businessId);
        afTradeTenementInfoDo.setAuditState(0);

        if (id == 0) {
            tradeTenementService.addTenementInfoDo(afTradeTenementInfoDo);
        } else {
            tradeTenementService.updateTenementInfo(afTradeTenementInfoDo);
        }

        return resp;
    }

}
