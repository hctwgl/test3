package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类现描述：通讯录授权（同步通讯录） 同步用户通讯录接口，客户端把用户通讯录中的数据同步到服务端，包括昵称和电话号码 客户端通过以下格式把数据传到服务端， 好友1昵称:好友1手机号1&好友1手机号2,好友昵称2:好友2手机号；即多个好友用逗号（英文逗号）","隔开， 单个好友的昵称和手机号用冒号":"隔开，单个好友多个手机号用&符号隔开，如： 陈金虎:15958686524&18857416845,小猪:07966898475,她娘:18656847587, 火火兔:07966898475,拨浪鼓:07966898475 为了防止通讯录中的昵称或手机号中有:,&这3个特殊字符，客户端需要先把通讯录中的这些特殊字符去除再传入服务端
 * 
 * @author chenjinhu 2017年2月16日 下午2:09:44
 * @version 1.1 huyang 2017年4月6日 09:58:04 通讯录不再保存在本地，直接同步通讯录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authContactsV1Api")
public class AuthContactsV1Api implements ApiHandle {

	// private static final int ADD_CONTRACT_PER_PAGE = 2;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	AfAuthContactsService afAuthContactsService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String contacts = (String) requestDataVo.getParams().get("contacts");

		contacts = contacts.replace((char)160, (char)32);
//		if (StringUtil.isBlank(contacts)) {
//			throw new FanbeiException("authContactsApi param error", FanbeiExceptionCode.PARAM_ERROR);
//		}

		bizCacheUtil.saveObject(Constants.CACHEKEY_USER_CONTACTS + context.getUserId(), contacts, Constants.SECOND_OF_ONE_DAY);
		
//		riskUtil.addressListPrimaries(context.getUserId() + "", contacts);
		
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);


		try {
			if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.NO.getCode()) || StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
				RiskRespBo riskResp = riskUtil.registerStrongRisk(userId + "", "DIRECTORY", null, null, "", "", null, "", "", "","");
				if (!riskResp.isSuccess()) {
					throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
				}
			}
		} catch (Exception e) {
			logger.error("更新风控通讯录信息失败：" + userId);
		}

		/**
		 * 更新 af_user_auth_status 用户认证信息表
		 */
		String scene = ObjectUtils.toString(requestDataVo.getParams().get("scene"));//场景
		if("".equals(scene)){
			scene="CASH";//如果前端所传为空,默认为现金贷
		}
		//已过期重新认证项
		String authItem = "directory";
		AfUserAuthStatusDo afUserAuthStatusDo= afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(context.getUserId(),scene);
		if(afUserAuthStatusDo!=null){
			String causeReason = afUserAuthStatusDo.getCauseReason();
			if(causeReason!=null&&!"".equals(causeReason)){
				JSONArray jsonArray = JSON.parseArray(causeReason);
				boolean judge=true;
				for(int i =0;i<jsonArray.size();i++){
					if(judge){
						JSONObject jsonObject =jsonArray.getJSONObject(i);
						String auth=jsonObject.getString("auth");
						if(authItem.equals(auth)){
							jsonObject.put("status","Y");
							jsonArray.remove(i);
							jsonArray.add(jsonObject);
							afUserAuthStatusDo.setCauseReason(jsonArray.toString());
							afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
							judge=false;
						}
					}
				}

			}
		}

		AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
		afUserAuthDo.setUserId(context.getUserId());
		afUserAuthDo.setTeldirStatus(YesNoStatus.YES.getCode());
		afUserAuthService.updateUserAuth(afUserAuthDo);
		resp.addResponseData("allowConsume", afUserAuthService.getConsumeStatus(context.getUserId(), context.getAppVersion()));
		return resp;
	}

//	private AfAuthContactsDo buildContractsDo(String contractsItem, Long userId) {
//		AfAuthContactsDo item = new AfAuthContactsDo();
//		String[] contractsItemArr = contractsItem.split(":");
//		item.setFriendNick(contractsItemArr[0]);
//		item.setFriendPhone(contractsItemArr[1]);
//		item.setUserId(userId);
//		return item;
//	}

}
