/**
 * 
 */
package com.ald.fanbei.api.web.api.borrow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月30日下午4:02:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCreditPromoteInfoApi")
public class GetCreditPromoteInfoApi implements ApiHandle {

	@Resource
	private AfUserAuthService afUserAuthService;

	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfResourceService afResourceService;


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Date now = new Date();
		// 账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Map<String, Object> data = getCreditPromoteInfo(now, userDto, authDo);
		resp.setResponseData(data);
		
		return resp;
	}

	private Map<String, Object> getCreditPromoteInfo(Date now, AfUserAccountDto userDto, AfUserAuthDo authDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> creditModel = new HashMap<String, Object>();
		Map<String, Object> zmModel = new HashMap<String, Object>();
		Map<String, Object> locationModel = new HashMap<String, Object>();
		Map<String, Object> contactorModel = new HashMap<String, Object>();
//		AfResourceDo afResourceDo =afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.creditScoreAmount.getCode());
////		JSONObject json = JSONObject.parseObject(afResourceDo.getValue());
//		JSONArray arry = JSON.parseArray(afResourceDo.getValue());
//		authDo.get
//		BigDecimal creditAmount = BigDecimal.ZERO;
//		int min = Integer.parseInt(afResourceDo.getValue1());//最小分数
//		if(sorce<min){
//			resp.addResponseData("tooLow", 'Y');
//			resp.addResponseData("creditAmount", creditAmount);
//		}else{
//			for (int i = 0; i < arry.size(); i++) {
//				JSONObject obj = arry.getJSONObject(i);
//				int minScore = obj.getInteger("minScore");
//				int maxScore = obj.getInteger("maxScore");
//				BigDecimal amount = obj.getBigDecimal("amount");
//				if(minScore<=sorce&&maxScore>sorce){
//					creditAmount = amount;
//				}
//			}
//			
//		}
		
		
		creditModel.put("creditLevel", "较差");
		creditModel.put("creditAssessTime", authDo.getGmtModified());
		creditModel.put("allowConsume", afUserAuthService.getConsumeStatus(authDo.getUserId()));
		zmModel.put("zmStatus", authDo.getZmStatus());
		zmModel.put("zmScore", authDo.getZmScore());
		if (StringUtil.equals(authDo.getRealnameStatus(), YesNoStatus.YES.getCode())
				&& StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())) {
			String authParamUrl = ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
			zmModel.put("zmxyAuthUrl", authParamUrl);

		}

		locationModel.put("locationStatus", authDo.getLocationStatus());
		locationModel.put("locationAddress", authDo.getLocationAddress());
		contactorModel.put("contactorStatus", authDo.getContactorStatus());
		contactorModel.put("contactorName", authDo.getContactorName());
		contactorModel.put("contactorMobile", authDo.getContactorMobile());
		contactorModel.put("contactorType", authDo.getContactorType());

		data.put("creditModel", creditModel);
		data.put("rrCreditStatus", authDo.getRealnameStatus());
		data.put("mobileStatus", authDo.getMobileStatus());
		data.put("teldirStatus", authDo.getTeldirStatus());
		data.put("zmModel", zmModel);
		data.put("locationModel", locationModel);
		data.put("contactorModel", contactorModel);

		return data;
	}

}
