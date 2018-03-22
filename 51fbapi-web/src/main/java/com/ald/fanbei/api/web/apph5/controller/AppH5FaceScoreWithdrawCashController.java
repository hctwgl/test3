package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfFacescoreRedService;
import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAndRedRelationDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 颜值测试游戏提现功能接口
 * @author liutengyuan 
 * @date 2018年3月22日
 */
@Controller
@RequestMapping("/fanbei_api/faceScore")
public class AppH5FaceScoreWithdrawCashController extends BaseController {
	
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	private AfFacescoreRedService faceScoreRedService;
	@Resource
	private AfFacescoreShareCountService faceScoreShareCountService;
	@Resource
	private AfResourceService afResourceService;
	String opennative = "/fanbei-web/opennative?name=";
	
	@ResponseBody
	@RequestMapping(value = "withdrawCash", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String withdrawCash(final HttpServletRequest request, HttpServletResponse response){
		return transactionTemplate.execute(new TransactionCallback<String>() {

			@Override
			public String doInTransaction(TransactionStatus status) {
				String resultStr = H5CommonResponse.getNewInstance(false, "红包提现失败!", "", null).toString();
				FanbeiWebContext context = new FanbeiWebContext();
				try {
				 // 1查询红包记录是否存在
				long redId = NumberUtil.objToLongDefault(request.getParameter("rid"),0L);
				AfFacescoreRedDo redDo = faceScoreRedService.getById(redId);
				if (redDo == null){
					return H5CommonResponse.getNewInstance(false, "红包不存在,参数有误!", "", null).toString();
				}
					Long userId = -1l;
					AfUserDo afUser = null;
					// 和登录有关的
					context = doWebCheck(request, true);
					// 2判断用户是否处于登陆状态
					if (context.isLogin()) {
						afUser = afUserService.getUserByUserName(context.getUserName());
						if (afUser != null) {
							userId = afUser.getRid();
							/*AfFacescoreShareCountDo shareCountDo = faceScoreShareCountService.getById(userId);
							Integer sharedCount = shareCountDo.getCount();
							// 1先查询有没有测试过，是否对红包进行了提现
							int count = faceScoreRedService.findUserAndRedRelationRecordByUserId(userId);
							List<AfResourceDo> configList = afResourceService.getConfigByTypes("USER_FACETEST");
							if (CollectionUtil.isEmpty(configList)){
								 return H5CommonResponse.getNewInstance(false, "该活动已经结束！", "", null).toString();
							}
							Integer totalAllowedCount = Integer.valueOf(configList.get(0).getValue1());
							if (count == totalAllowedCount){
								return H5CommonResponse.getNewInstance(false, "领取红包的次数已经用完！", "", null).toString();
							}else if (count == 1){
								// 已经领取过一次，进行分享次数的判断
								
								// 获取需要分享的次数
								int needSharedCount = Integer.valueOf(configList.get(0).getValue());
								if (sharedCount < needSharedCount ){
								   return H5CommonResponse.getNewInstance(false, "分享五个群可再得一次拆红包的机会！", "", null).toString();
								}
							}*/
							int count = faceScoreRedService.findUserAndRedRelationRecordByRedId(redId);
							if (count > 0){
								return H5CommonResponse.getNewInstance(false, "该红包已经被提现了！", "", null).toString();
							}
							AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
					        if (userAccountDo == null) {
					            throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
					        }
					        // 2.1更新用户账户额度
					        BigDecimal oldAmount = userAccountDo.getRebateAmount();
					        BigDecimal newAmount = oldAmount.add(redDo.getAmount());
					        userAccountDo.setRebateAmount(newAmount);
					        afUserAccountService.updateUserAccount(userAccountDo);
					        // 记录数据到账户记录表
					        AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
					        afUserAccountLogDo.setGmtCreate(new Date());
					        afUserAccountLogDo.setUserId(userId);
					        afUserAccountLogDo.setAmount(redDo.getAmount());
					        afUserAccountLogDo.setType("CASH");
					        afUserAccountLogDo.setRefId("");
					        afUserAccountService.addUserAccountLog(afUserAccountLogDo);
					        // 记录数据到用户——红包记录表
					        faceScoreRedService.addUserAndRedRecord(new AfUserAndRedRelationDo(userId,redId));
							} else {
							// 用户未登陆的情况
							return H5CommonResponse.getNewInstance(false, "用户不存在！", "", null).toString();
							}
					}else{
						return H5CommonResponse.getNewInstance(false, "请先登陆或者注册！", "", null).toString();
					}
				} catch (FanbeiException e) {
					if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
						Map<String, Object> data = new HashMap<>();
						String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
						data.put("loginUrl", loginUrl);
						logger.error("/activity/de/share" + context + "login error ");
						resultStr =  H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
					    }else{
					    resultStr = H5CommonResponse.getNewInstance(false, "颜值红包提现失败", "", e.getMessage()).toString();
					    }
				} catch (Exception e) {
					status.setRollbackOnly();
					resultStr = H5CommonResponse.getNewInstance(false, "颜值红包提现失败", "", "").toString();
					logger.error("颜值红包提现失败" + context, e);
				}
				return resultStr;
			}
		});
	}
	/**
	 * H5 的接口
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/H5withdrawCash", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String withdrawCashH5(final HttpServletRequest request, HttpServletResponse response){
		return transactionTemplate.execute(new TransactionCallback<String>() {

			@Override
			public String doInTransaction(TransactionStatus status) {
				String resultStr = H5CommonResponse.getNewInstance(false, "红包提现失败!", "", null).toString();
				FanbeiH5Context context = new FanbeiH5Context();
				try {
				 // 1查询红包记录是否存在
				long redId = NumberUtil.objToLongDefault(request.getParameter("rid"),0L);
				AfFacescoreRedDo redDo = faceScoreRedService.getById(redId);
				if (redDo == null){
					return H5CommonResponse.getNewInstance(false, "红包不存在,参数有误!", "", null).toString();
				}
					Long userId = -1l;
					AfUserDo afUser = null;
					// 和登录有关的
					context = doH5Check(request, true);
					// 2判断用户是否处于登陆状态
					if (context.isLogin()) {
						afUser = afUserService.getUserByUserName(context.getUserName());
						if (afUser != null) {
							userId = afUser.getRid();
							/*AfFacescoreShareCountDo shareCountDo = faceScoreShareCountService.getById(userId);
							Integer sharedCount = shareCountDo.getCount();
							// 1先查询有没有测试过，是否对红包进行了提现
							int count = faceScoreRedService.findUserAndRedRelationRecordByUserId(userId);
							List<AfResourceDo> configList = afResourceService.getConfigByTypes("USER_FACETEST");
							if (CollectionUtil.isEmpty(configList)){
								 return H5CommonResponse.getNewInstance(false, "该活动已经结束！", "", null).toString();
							}
							Integer totalAllowedCount = Integer.valueOf(configList.get(0).getValue1());
							if (count == totalAllowedCount){
								return H5CommonResponse.getNewInstance(false, "领取红包的次数已经用完！", "", null).toString();
							}else if (count == 1){
								// 已经领取过一次，进行分享次数的判断
								
								// 获取需要分享的次数
								int needSharedCount = Integer.valueOf(configList.get(0).getValue());
								if (sharedCount < needSharedCount ){
								   return H5CommonResponse.getNewInstance(false, "分享五个群可再得一次拆红包的机会！", "", null).toString();
								}
							}*/
							int count = faceScoreRedService.findUserAndRedRelationRecordByRedId(redId);
							if (count > 0){
								return H5CommonResponse.getNewInstance(false, "该红包已经被提现了！", "", null).toString();
							}
							AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
					        if (userAccountDo == null) {
					            throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
					        }
					        // 2.1更新用户账户额度
					        BigDecimal oldAmount = userAccountDo.getRebateAmount();
					        BigDecimal newAmount = oldAmount.add(redDo.getAmount());
					        userAccountDo.setRebateAmount(newAmount);
					        afUserAccountService.updateUserAccount(userAccountDo);
					        // 记录数据到账户记录表
					        AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
					        afUserAccountLogDo.setGmtCreate(new Date());
					        afUserAccountLogDo.setUserId(userId);
					        afUserAccountLogDo.setAmount(redDo.getAmount());
					        afUserAccountLogDo.setType("CASH");
					        afUserAccountLogDo.setRefId("");
					        afUserAccountService.addUserAccountLog(afUserAccountLogDo);
					        // 记录数据到用户——红包记录表
					        faceScoreRedService.addUserAndRedRecord(new AfUserAndRedRelationDo(userId,redId));
							} else {
								// 用户未登陆的情况
								return H5CommonResponse.getNewInstance(false, "用户不存在！", "", null).toString();
							}
					}else{
						return H5CommonResponse.getNewInstance(false, "请先登陆或者注册！", "", null).toString();
					}
				} catch (FanbeiException e) {
					if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
						Map<String, Object> data = new HashMap<>();
						String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
						data.put("loginUrl", loginUrl);
						logger.error("/activity/de/share" + context + "login error ");
						resultStr =  H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
					    }else{
					    resultStr = H5CommonResponse.getNewInstance(false, "颜值红包提现失败", "", e.getMessage()).toString();
					    }
				} catch (Exception e) {
					status.setRollbackOnly();
					resultStr = H5CommonResponse.getNewInstance(false, "颜值红包提现失败", "", "").toString();
					logger.error("颜值红包提现失败" + context, e);
				}
				return resultStr;
			}
		});
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(),
					FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
