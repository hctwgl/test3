package com.ald.fanbei.api.web.api.tradeWeiXin;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        
        AfTradeTenementInfoDo afTradeTenementInfoDo = new AfTradeTenementInfoDo();
        String id = getId();
        //String applyTime = ObjectUtils.toString(requestDataVo.getParams().get("applyTime"), "").toString();
        //String auditTime = ObjectUtils.toString(requestDataVo.getParams().get("auditTime"), "").toString();
        String userName = ObjectUtils.toString(requestDataVo.getParams().get("userName"), "").toString();
        String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"), "").toString();
        String idNumber = ObjectUtils.toString(requestDataVo.getParams().get("idNumber"), "").toString();
        String homeAddress = ObjectUtils.toString(requestDataVo.getParams().get("homeAddress"), "").toString();
        String contractImageUrl = ObjectUtils.toString(requestDataVo.getParams().get("contractImageUrl"), "").toString();
        String rentType = ObjectUtils.toString(requestDataVo.getParams().get("rentType"), "").toString();
        BigDecimal rentOnePrice = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("rentOnePrice"),null);
        BigDecimal rentSumPrice = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("rentSumPrice"),null);
        String beginTime = ObjectUtils.toString(requestDataVo.getParams().get("beginTime"), "").toString();
        String endTime = ObjectUtils.toString(requestDataVo.getParams().get("endTime"), "").toString();
        Integer auditState = NumberUtil.objToIntDefault(requestDataVo.getParams().get("auditState"), null);
        String auditFaildReason = ObjectUtils.toString(requestDataVo.getParams().get("auditFaildReason"), "").toString();
        Integer rentStatus = NumberUtil.objToIntDefault(requestDataVo.getParams().get("rentStatus"), null);
        
        afTradeTenementInfoDo.setId(id);
        //Date applyTimeDate = DateUtil.parseDate(applyTime,"yyyy-MM-dd");
        //Date auditTimeDate = DateUtil.parseDate(auditTime,"yyyy-MM-dd");
        //afTradeTenementInfoDo.setApplyTime(applyTimeDate);
        //afTradeTenementInfoDo.setAuditTime(auditTimeDate);
        afTradeTenementInfoDo.setUserName(userName);
        afTradeTenementInfoDo.setMobile(mobile);
        afTradeTenementInfoDo.setIdNumber(idNumber);
        afTradeTenementInfoDo.setHomeAddress(homeAddress);
        afTradeTenementInfoDo.setContractImageUrl(contractImageUrl);
        afTradeTenementInfoDo.setRentType(rentType);
        afTradeTenementInfoDo.setRentOnePrice(rentOnePrice);
        afTradeTenementInfoDo.setRentSumPrice(rentSumPrice);
        Date beginTimeDate = DateUtil.parseDate(beginTime,"yyyy-MM-dd");
        Date endTimeDate = DateUtil.parseDate(endTime,"yyyy-MM-dd");
        afTradeTenementInfoDo.setBeginTime(beginTimeDate);
        afTradeTenementInfoDo.setEndTime(endTimeDate);
        afTradeTenementInfoDo.setBusinessId(businessId);
        
        AfTradeTenementInfoDo tenementInfoDo = tradeTenementService.getTenementInfoDoBymobile(mobile);
        //Integer state = tenementInfoDo.getAuditState();//得到审核状态
        
        if(tenementInfoDo==null){
        	tradeTenementService.addTenementInfoDo(afTradeTenementInfoDo);	
        }else if(tenementInfoDo.getAuditState()==0){
        	tradeTenementService.updateTenementInfo(afTradeTenementInfoDo);
        }else{
        	resp.addResponseData("message", "对不起，您的审核已将完成，无法更改");
        }
        
		return resp;
	}

	
	public String getId() {
		String now = DateUtil.now();
        int i=(int)(Math.random()*900)+100;
        String random = String.valueOf(i);
        String id = "51"+now+random;
        return id;
	}
}
