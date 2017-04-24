package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.GetBrandCouponCountRequestBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：getMineInfoApi
 * @author 何鑫 2017年1月20日 14:39:43
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMineInfoApi")
public class GetMineInfoApi implements ApiHandle {
	
	private static final String AVAILABLE_NUM = "availableNum";
	private static final String DATA = "data";

	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAuthService afUserAuthService;

	@Resource
	private AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Integer appVersion = context.getAppVersion();
		Long userId = context.getUserId();
		logger.info("userId=" + userId);

		AfUserDo afUserDo = afUserService.getUserById(userId);
		if (afUserDo == null) {
			throw new FanbeiException("afUserDo  is invalid", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);

		}
		
		// 可用红包数量
		int coupleCount = afUserCouponService.getUserCouponByUserNouse(userId);
		// 账户关联信息
		AfUserAccountDto userAccountInfo = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("avata", userAccountInfo.getAvatar());
		data.put("nick", userAccountInfo.getNick());
		data.put("userName", userAccountInfo.getUserName());
//		data.put("realName", userAccountInfo.getRealName());
		data.put("jfbAmount", userAccountInfo.getJfbAmount());
		data.put("mobile", afUserDo.getMobile());
		String isPay = YesNoStatus.NO.getCode();
		if (!StringUtil.isBlank(userAccountInfo.getPassword())) {
			isPay = YesNoStatus.YES.getCode();
		}
		data.put("isPayPwd", isPay);
		data.put("vipLevel", userAccountInfo.getVipLevel());
		data.put("rebateAmount", userAccountInfo.getRebateAmount());
		if (appVersion < 340) {
			data.put("couponCount", coupleCount);
		} else {
			dealWithVersionGT340(data, requestDataVo, context, coupleCount);
		}
		
		data.put("bankcardStatus", afUserAuthDo.getBankcardStatus());

		data.put("recommendCode", userAccountInfo.getRecommendCode());
		resp.setResponseData(data);
		return resp;
	}
	
	private void dealWithVersionGT340(Map<String, Object> resultData, RequestDataVo requestDataVo, FanbeiContext context, int coupleCount) {
		GetBrandCouponCountRequestBo bo = new GetBrandCouponCountRequestBo();
		bo.setUserId(context.getUserId()+StringUtils.EMPTY);
		String resultString = HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/api/promotion/get_coupon_num", JSONObject.toJSONString(bo));
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (resultJson == null || !"0".equals(resultJson.getString("code"))) {
			resultData.put("couponCount", coupleCount);
			resultData.put("brandCouponCount", 0);
		} else {
			JSONObject data = resultJson.getJSONObject(DATA);
			Integer brandCouponCount = data.getInteger(AVAILABLE_NUM);
			resultData.put("couponCount", coupleCount + brandCouponCount);
		}
		resultData.put("plantformCouponCount", coupleCount);
	}

}
