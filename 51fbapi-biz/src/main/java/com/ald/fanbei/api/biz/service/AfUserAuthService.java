package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.bo.risk.ReqFromRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromSecondaryRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromStrongRiskBo;
import com.ald.fanbei.api.biz.bo.risk.RespSecAuthInfoToRiskBo;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.alibaba.fastjson.JSONObject;

/**
 * @类现描述：
 * @author chenjinhu 2017年2月15日 下午3:03:56
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthService {
	
	/**
	 * 增加记录
	 * @param afUserAuthDo
	 * @return
	 */
	int addUserAuth(AfUserAuthDo afUserAuthDo);

	/**
	 * 更新记录
	 * @param afUserAuthDo
	 * @return
	 */
	int updateUserAuth(AfUserAuthDo afUserAuthDo);
	
	/**
	 * 更新运营商认证状态为认证中-此更新比较特殊单独方法处理
	 * @param afUserAuthDo
	 * @return
	 */
	int updateUserAuthMobileStatusWait(AfUserAuthDo afUserAuthDo);
	
	/**
	 * 获取用户认证信息
	 * @param userId
	 * @return
	 */
	AfUserAuthDo getUserAuthInfoByUserId(Long userId);
	
	/**
	 * 判断是否可以分期
	 * @param userId
	 * @return
	 */
	String getConsumeStatus(Long userId,Integer appVersion);
	
	/**
	 * 获取Ivs_status is Y 的数量
	 * @return
	 */
	int getUserAuthCountWithIvs_statusIsY();
	
	/**
	 * 获取Ivs_status is Y 的数据
	 * @return
	 */
	List<AfUserAuthDo> getUserAuthListWithIvs_statusIsY(AfUserAuthQuery query);

	Map<String, Object> getCreditPromoteInfo(Long userId, Date now, AfUserAccountDto userDto, AfUserAuthDo authDo, Integer appVersion, String scene,AfResourceDo zhimaConfigResource);

	boolean allBasicAuthPassed(Long userId);
	boolean allBasicAuthPassed(AfUserAuthDo authInfo);

	boolean allSupplementAuthPassed(Long userId);

	boolean allSupplementAuthPassed(AfUserAuthDo authInfo);
	
	/**
	 * 判断用户是否是白名单用户
	 * @return
	 */
	boolean passWhiteList(String userName);
	
	boolean getAuthRaiseStatus(AfAuthRaiseStatusDo afAuthRaiseStatusDo, String scene, String auth_type, Date authDate);
	
	/**
	 * 处理来自风控其主动发出的强风控回调请求
	 * @return
	 */
	void dealFromStrongRiskForcePush(ReqFromStrongRiskBo reqBo);
	
	/**
	 * 处理来自风控其主动发出的补充认证回调请求
	 * @return
	 */
	void dealFromSecondaryRiskForcePush(ReqFromSecondaryRiskBo reqBo);
	
	/**
	 * 查询补充认证的相关状态
	 * @return
	 */
	RespSecAuthInfoToRiskBo getSecondaryAuthInfo(ReqFromRiskBo reqBo);
	
	/**
	 * 处理主动还款提额后 对 认证状态的处理
	 * @param afUserAuthDo
	 * @param dataObj
	 */
	void dealRaiseQuota(AfUserAuthDo afUserAuthDo, JSONObject dataObj);
	/**
	 * 查询用户的权限包状态
	 * @param userId
	 */
	String getOrderWeakRiskStatus(Long userId);

	/**
	 * 签到领金币 获取用户层级
	 * @param userId
	 * @return
	 */
	List<Integer>  signRewardUserLevel(Long userId,HashMap<String,Object> hashMap);


	HashMap<String,Object> getUserAuthInfo(Long userId);
}
