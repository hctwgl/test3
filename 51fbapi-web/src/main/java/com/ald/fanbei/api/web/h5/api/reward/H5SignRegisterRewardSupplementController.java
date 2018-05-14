package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @类描述：
 * 
 * @author :cfp
 * @version ：2017年10月30日 下午16:00:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/signRewardSupplement")
public class H5SignRegisterRewardSupplementController extends BaseController {

    @Resource
    AfUserService afUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
	@Resource
	BaiQiShiUtils baiQiShiUtils;
	@Resource
	NumberWordFormat numberWordFormat;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfSignRewardService afSignRewardService;
	@Resource
	AfSignRewardExtService afSignRewardExtService;



    // 提交活动注册并登陆
    @ResponseBody
    @RequestMapping(value = "/commitRegisterLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String commitActivityRegisterLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	 	String resultStr = "";
	 	try {
			String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
			String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
			final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
			AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
			if (eUserDo != null) {
				AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
				afSignRewardDo.setIsDelete(0);
				afSignRewardDo.setUserId(rewardUserId);
				afSignRewardDo.setGmtCreate(new Date());
				afSignRewardDo.setGmtModified(new Date());
				afSignRewardDo.setType(3);
				afSignRewardDo.setStatus(0);
				afSignRewardDo.setFriendUserId(eUserDo.getRid());
				afSignRewardDo.setAmount(BigDecimal.ZERO);
				afSignRewardService.saveRecord(afSignRewardDo);
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();
			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				return H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "", null).toString();
			}
			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
			}
			try {
				tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
			} catch (Exception e) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
			}
			try {
				baiQiShiUtils.getRegistResult("h5",bsqToken,CommonUtil.getIpAddr(request),moblie,"","","","");
			}catch (Exception e){
				logger.error("h5Common commitRegisterLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
			}
			// 更新为已经验证
			afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
			String salt = UserUtil.getSalt();
			// modify by luoxiao 避免passwordSrc 为空出现NullPointerException异常
			String password = "";
			if(StringUtils.isNotEmpty(passwordSrc)){
				password = UserUtil.getPassword(passwordSrc, salt);
			}
			// end by luoxiao
			AfUserDo userDo = new AfUserDo();
			userDo.setSalt(salt);
			userDo.setUserName(moblie);
			userDo.setMobile(moblie);
			userDo.setNick("");
			userDo.setPassword(password);
			userDo.setRecommendId(0l);
			final long userId = afUserService.addUser(userDo);
			//绑定微信的唯一标识open_id

			Long invteLong = Constants.INVITE_START_VALUE + userId;
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			afUserService.updateUser(userDo);
			// 获取邀请分享地址
			String appDownLoadUrl = "";
			resultStr = H5CommonResponse.getNewInstance(true, "补签成功", appDownLoadUrl, null).toString();
			// save token to cache 记住登录状态
			String  newtoken = UserUtil.generateToken(moblie);
			String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + moblie;
			CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
			CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
			bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_HOUR);
			if(!signReward(request,userId)){
				return  H5CommonResponse.getNewInstance(true, "补签失败").toString();
			}
			return resultStr;
		} catch (FanbeiException e) {
			logger.error("commitRegister fanbei exception" + e.getMessage());
			return H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
		} catch (Exception e) {
			logger.error("commitRegister exception", e);
			return H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
		}

    }

    private boolean signReward(HttpServletRequest request,final Long userId){
    	boolean result ;
    	final Long rewardUserId = NumberUtil.objToLongDefault(request.getParameter("rewardUserId"),null);
		final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("NEW_FRIEND_USER_SIGN");
		if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
			throw new FanbeiException("param error", FanbeiExceptionCode.PARAM_ERROR);
		}
		final BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
		String status = transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				try{
					AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
					afSignRewardDo.setIsDelete(0);
					afSignRewardDo.setUserId(rewardUserId);
					afSignRewardDo.setGmtCreate(new Date());
					afSignRewardDo.setGmtModified(new Date());
					afSignRewardDo.setType(2);
					afSignRewardDo.setStatus(0);
					afSignRewardDo.setFriendUserId(userId);
					afSignRewardDo.setAmount(rewardAmount);
					AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(rewardUserId);
					afSignRewardService.saveRecord(afSignRewardDo);
					afSignRewardExtDo.setAmount(rewardAmount);
					afSignRewardExtService.increaseMoney(afSignRewardExtDo);
					return "success";
				}catch (Exception e){
					status.setRollbackOnly();
					return "fail";
				}
			}
		});
		if(StringUtil.equals(status,"success")){
			result =true;
		}else {
			result =false;
		}
    	return result;

	}

	/**
	 * 随机获取min 与 max 之间的值
	 * @param min
	 * @param max
	 * @return
	 */
	private BigDecimal randomNum(String min,String max){
		BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(max) - Double.parseDouble(min)) + min).setScale(2, RoundingMode.HALF_EVEN);
		return rewardAmount;

	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		return null;
    }


	
	

}
