package com.ald.fanbei.api.web.h5.api.recycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;

/**
 * @author yanghailong
 * @类描述：获取银行卡
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Component("getUserBankListApi")
public class GetUserBankListApi implements H5Handle  {

	@Resource
	AfUserBankcardService afUserBankcardService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		
		List<AfUserBankcardDo> bankList = afUserBankcardService.getAfUserBankcardDoList(context.getUserId());
		if(bankList.size()<=0){
			throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
		}
		
		for (AfUserBankcardDo bankcardDo : bankList) {
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put("cardId", bankcardDo.getRid());
			resMap.put("bankCode", bankcardDo.getBankCode());
			resMap.put("bankName", bankcardDo.getBankName());
			resMap.put("isMain", bankcardDo.getIsMain());
			resMap.put("cardNumber", bankcardDo.getCardNumber());
			resMap.put("status", bankcardDo.getStatus());
			resMap.put("bankIcon", bankcardDo.getBankIcon());
			resMap.put("bankChannel", bankcardDo.getBankChannel());
			resMap.put("mobile", bankcardDo.getMobile());
			
			UpsBankStatusDto upsBankStatus = afUserBankcardService.getUpsBankStatus(bankcardDo.getBankCode(), bankcardDo.getBankChannel());
			resMap.put("limitUp", upsBankStatus.getLimitUp());
			resMap.put("dailyLimit", upsBankStatus.getDailyLimit());
			resMap.put("isMaintain", upsBankStatus.getIsMaintain());
			
			returnList.add(resMap);
		}
		
		resp.setResponseData(returnList);
		return resp;
	}

}
