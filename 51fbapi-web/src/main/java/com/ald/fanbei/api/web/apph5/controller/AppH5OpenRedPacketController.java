package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.bo.OpenRedPacketHomeBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.SelfOpenRedPacketSourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.OpenRedPacketParamVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拆红包controller
 *
 * @author wangli
 * @date 2018/4/11 14:24
 */
@Controller
@RequestMapping("/fanbei-web/redPacket")
public class AppH5OpenRedPacketController extends BaseController {

    @Autowired
    private AfUserService afUserService;

    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfRedPacketTotalService afRedPacketTotalService;

    @Autowired
    private AfRedPacketSelfOpenService afRedPacketSelfOpenService;

    @Autowired
    private AfRedPacketHelpOpenService afRedPacketHelpOpenService;

    @Autowired
    private AfUserThirdInfoService afUserThirdInfoService;

    @Autowired
    private AfSmsRecordService afSmsRecordService;

    @Autowired
    private TongdunUtil tongdunUtil;

    @Autowired
    private BaiQiShiUtils baiQiShiUtils;

    /**
     * 获取红包主页信息（站内）
     * 
     * @author wangli
     * @date 2018/5/4 11:12
     */
    @RequestMapping(value = "/getHomeInfoInSite", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getHomeInfoInSite(HttpServletRequest request) {
        try {
            FanbeiWebContext context = doWebCheck(request, false);
            OpenRedPacketHomeBo data = afRedPacketTotalService.getHomeInfoInSite(context);
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 获取红包主页信息（站外）
     *
     * @author wangli
     * @date 2018/5/4 11:13
     */
    @RequestMapping(value = "/getHomeInfoOutSite", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getHomeInfoOutSite(OpenRedPacketParamVo param) {
        try {
            OpenRedPacketHomeBo data = afRedPacketTotalService.getHomeInfoOutSite(param.getCode(), param.getShareId());
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
           return handleException(e);
        }
    }

    /**
     * 获取所有拆红包记录
     * 
     * @author wangli
     * @date 2018/5/4 17:27
     */
    @RequestMapping(value = "/findOpenList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findOpenList(OpenRedPacketParamVo param) {
        Map<String, Object> data = new HashMap<>();

        Map<String, String> redPacket = new HashMap<>();
        AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getById(param.getId());
        redPacket.put("id", redPacketTotalDo.getRid().toString());
        redPacket.put("amount", redPacketTotalDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());

        AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
        JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
        BigDecimal restAmount = afRedPacketTotalService
                .calcWithdrawRestAmount(redPacketTotalDo, redPacketConfig.getBigDecimal("thresholdAmount"));
        redPacket.put("restAmount", restAmount.setScale(2, RoundingMode.HALF_UP).toString());

        data.put("redPacket", redPacket);
        data.put("openList", afRedPacketTotalService.findOpenListOfHome(param.getId(), null));

        return H5CommonResponse.getNewInstance(true, "", "", data).toString();
    }

    /**
     * 获取用户所有提现记录
     *
     * @author wangli
     * @date 2018/5/4 18:11
     */
    @RequestMapping(value = "/findWithdrawList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findWithdrawList(HttpServletRequest request, OpenRedPacketParamVo param) {
        try {
            AfUserDo userDo = getUserInfo(param.getCode(), request);
            List<Map<String, String>> data = afRedPacketTotalService
                    .findWithdrawListOfHome(userDo.getRid(), null);

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 个人拆红包
     *
     * @author wangli
     * @date 2018/5/7 9:37
     */
    @RequestMapping(value = "/open", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String selfOpen(HttpServletRequest request, OpenRedPacketParamVo param) {
        try {
            AfUserDo userDo = getUserInfo(param.getCode(), request);
            AfRedPacketSelfOpenDo selfOpenDo = afRedPacketSelfOpenService
                    .open(userDo.getRid(), userDo.getUserName(), param.getSourceType());
            Map<String, String> data = buildSelfOpenResult(selfOpenDo);

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 帮好友拆红包
     *
     * @author wangli
     * @date 2018/5/4 16:21
     */
    @RequestMapping(value = "/helpOpen", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String helpOpen(OpenRedPacketParamVo param) {
        try {
            AfRedPacketHelpOpenDo helpOpenDo = afRedPacketHelpOpenService.open(param.getCode(), param.getShareId());

            Map<String, String> data = new HashMap<>();
            UserWxInfoDto userWxInfoDto = afUserThirdInfoService.getWxOrLocalUserInfo(helpOpenDo.getUserId());
            data.put("nick", userWxInfoDto.getNick());
            data.put("amount", helpOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 绑定手机号并拆红包
     *
     * @author wangli
     * @date 2018/5/10 11:53
     */
    @RequestMapping(value = "/bindPhoneAndOpen", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String bindPhoneAndOpen(HttpServletRequest request, OpenRedPacketParamVo param) {
        try {
            AfUserDo userDo = getOrRegisterUser(request, param.getVerifyCode(), param.getToken(),
                    param.getBsqToken(), param.getMobile());
            AfRedPacketSelfOpenDo selfOpenDo = afRedPacketSelfOpenService.bindPhoneAndOpen(userDo.getRid(),
                    userDo.getUserName(), param.getCode(), SelfOpenRedPacketSourceType.OPEN_SELF.getCode());

            Map<String, String> data = buildSelfOpenResult(selfOpenDo);

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 红包提现
     *
     * @author wangli
     * @date 2018/5/6 10:25
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String withdraw(HttpServletRequest request, OpenRedPacketParamVo param) {
        try {
            AfUserDo userDo = getUserInfo(param.getCode(), request);
            afRedPacketTotalService.withdraw(param.getId(), userDo.getUserName());

            return H5CommonResponse.getNewInstance(true, "").toString();
        } catch (FanbeiException e) {
            return handleFanbeiException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

    // 获取或注册用户
    private AfUserDo getOrRegisterUser(HttpServletRequest request, String verifyCode, String token,
                                       String bsqToken, String mobile) {
        try {
            tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request),
                    mobile, mobile, "");
        } catch (Exception e) {
            throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR);
        }

        validateVerifyCode(verifyCode, mobile);

        AfUserDo userDo = afUserService.getUserByUserName(mobile);
        if (userDo == null) {
            userDo = registerUser(request, mobile, bsqToken);
        }

        return userDo;
    }

    // 验证验证码
    private void validateVerifyCode(String verifyCode, String mobile) {
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
        if (smsDo == null) {
            logger.error("/redPacket/bindPhoneAndOpen, error=sms record is empty");
            throw new FanbeiException("手机号与验证码不匹配");
        }
        String realCode = smsDo.getVerifyCode();
        if (!StringUtils.equals(verifyCode, realCode)) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is invalid");
            throw new FanbeiException(FanbeiExceptionCode.USER_REGIST_SMS_ERROR);
        }
        if (smsDo.getIsCheck() == 1) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is already invalid");
            throw new FanbeiException(FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR);
        }
        // 判断验证码是否过期
        if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), 5))) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is overdue");
            throw new FanbeiException(FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
        }
        afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
    }

    // 注册用户
    private AfUserDo registerUser(HttpServletRequest request, String mobile, String bsqToken) {
        try {
            baiQiShiUtils.getRegistResult("h5", bsqToken, CommonUtil.getIpAddr(request), mobile,
                    "","","","");
        } catch (Exception e){
            logger.error("/redPacket/bindPhoneAndOpen baiQiShiUtils getRegistResult error => {}",e.getMessage());
        }

        AfUserDo userDo = new AfUserDo();
        userDo.setSalt("");
        userDo.setUserName(mobile);
        userDo.setMobile(mobile);
        userDo.setNick("");
        userDo.setPassword("");
        userDo.setRecommendId(0L);
        long userId = afUserService.addUser(userDo);
        Long invteLong = Constants.INVITE_START_VALUE + userId;
        String inviteCode = Long.toString(invteLong, 36);
        userDo.setRecommendCode(inviteCode);
        afUserService.updateUser(userDo);
        return userDo;
    }

    // 处理FanbeiException
    private String handleFanbeiException(FanbeiException e) {
        logger.error("/redPacket/getHomeInfoInSite, error={}", e);
        if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
            return H5CommonResponse.getNewInstance(false, "没有登录").toString();
        }
        return H5CommonResponse.getNewInstance(false, e.getMessage()).toString();
    }

    // 处理异常
    private String handleException(Exception e) {
        logger.error("/redPacket/getHomeInfoInSite, error:", e);
        return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SYSTEM_ERROR.getDesc()).toString();
    }

    // 获取用户信息，如果传code了，就利用绑定的openId获取用户信息，否则从登录信息中获取
    private AfUserDo getUserInfo(String code, HttpServletRequest request) {
        if (StringUtil.isBlank(code)) {
            FanbeiWebContext context = doWebCheck(request, true);
            return afUserService.getUserByUserName(context.getUserName());
        } else {
            AfResourceDo afResourceDo = afResourceService.getWechatConfig();
            String appid = afResourceDo.getValue();
            String secret = afResourceDo.getValue1();
            JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, code);
            UserWxInfoDto localUserInfo = afUserThirdInfoService
                    .getLocalUserInfoByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
            if (localUserInfo != null) {
                AfUserDo userDo = new AfUserDo();
                userDo.setRid(localUserInfo.getUserId());
                userDo.setUserName(localUserInfo.getUserName());
                return userDo;
            } else {
                throw new FanbeiException("没有绑定手机号");
            }
        }
    }

    // 构建自己拆红包结果
    private Map<String, String> buildSelfOpenResult(AfRedPacketSelfOpenDo selfOpenDo) {
        Map<String, String> result = new HashMap<>();
        result.put("id", selfOpenDo.getRedPacketTotalId().toString());
        result.put("amount", selfOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());

        AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
        JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
        BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
        BigDecimal restAmount = afRedPacketTotalService
                .calcWithdrawRestAmount(selfOpenDo.getRedPacketTotalId(), thresholdAmount);
        result.put("restAmount", restAmount.setScale(2, RoundingMode.HALF_UP).toString());

        boolean isCanGainOne = afRedPacketTotalService
                .isCanGainOne(selfOpenDo.getRedPacketTotalId(), redPacketConfig.getInteger("shareTime"));
        result.put("isCanGainOne", isCanGainOne ? YesNoStatus.YES.getCode() : YesNoStatus.NO.getCode());
        return result;
    }
}
