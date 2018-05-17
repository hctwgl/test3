package com.ald.fanbei.api.web.api.borrow;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 *
 * @author suweili 2017年3月30日下午4:02:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCreditPromoteInfoV1Api")
public class GetCreditPromoteInfoV1Api implements ApiHandle {

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
		String scene = ObjectUtils.toString(requestDataVo.getParams().get("scene"));//场景
		if("".equals(scene)){
			scene="CASH";//如果前端所传为空,默认为现金贷
		}
		// 账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		//芝麻信息认证相关配置
		AfResourceDo zmConfigResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ZHIMA_VERIFY_CONFIG.getCode(), AfResourceSecType.ZHIMA_VERIFY_RULE_CONFIG.getCode());
		//旧版本继续走老的逻辑，直接按认证通过处理
		//新版本如果配置默认让用户芝麻认证通过，则走同样逻辑
		//芝麻信用重新启用的版本分界
		Integer zmVersionDivision = NumberUtil.objToIntDefault(zmConfigResourceDo.getValue3(), 412);
		if(!authDo.getZmStatus().equals("Y") && ( context.getAppVersion() < zmVersionDivision ||( context.getAppVersion() >= zmVersionDivision && YesNoStatus.NO.getCode().equals(zmConfigResourceDo.getValue2()))) ){
			authDo.setZmScore(0);
			authDo.setZmStatus("Y");
			authDo.setGmtZm(new Date());
			authDo.setIvsScore(0);
			authDo.setIvsStatus("Y");
			authDo.setGmtIvs(new Date());
			afUserAuthService.updateUserAuth(authDo);
		}
		
		Map<String, Object> data = afUserAuthService.getCreditPromoteInfo(userId, now, userDto, authDo,context.getAppVersion(),scene,zmConfigResourceDo);

		resp.setResponseData(data);

		return resp;
	}

}
