package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
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
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOpenRedPacketHomeVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    private BizCacheUtil bizCacheUtil;

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
    public String getHomeInfoInSite(HttpServletRequest request,
                                    @RequestParam(value = "broadcastSize", required = false) Integer broadcastSize) {
        try {
            AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
            // 判断活动开关是否打开
            if (config.getValue().trim().equals(YesNoStatus.NO)) {
                return H5CommonResponse.getNewInstance(false, "活动已结束", "", null).toString();
            }

            AfOpenRedPacketHomeVo data = new AfOpenRedPacketHomeVo();
            FanbeiWebContext context = doWebCheck(request, false);
            JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());

            data.setWithdrawTotalNum(getWithdrawTotalNum(redPacketConfig));
            if (context.isLogin()) {
                Long userId = afUserService.getUserByUserName(context.getUserName()).getRid();
                data.setRedPacket(getRedPacket(userId, redPacketConfig));
                data.setWithdrawList(doFindWithdrawList(userId, 2));
                if (data.getRedPacket() != null) {
                    Long redPacketTotalId = Long.valueOf(data.getRedPacket().get(AfOpenRedPacketHomeVo.KEY_REDPACKET_ID));
                    data.setOpenList(doFindOpenList(redPacketTotalId, 2));
                }
            }
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
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
    public String getHomeInfoOutSite(@RequestParam("code") String code,@RequestParam("shareId") Long shareId) {
        try {
            AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
            // 判断活动开关是否打开
            if (config.getValue().trim().equals(YesNoStatus.NO)) {
                return H5CommonResponse.getNewInstance(false, "活动已结束", "", null).toString();
            }

            AfOpenRedPacketHomeVo data = new AfOpenRedPacketHomeVo();
            JSONObject userWxInfo = getUserWxInfoByCode(code);
            JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());

            data.setWithdrawTotalNum(getWithdrawTotalNum(redPacketConfig));

            Long userId = afUserThirdInfoService.getUserIdByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
            if (userId != null) {
                data.setUserId(userId.toString());
                data.setRedPacket(getRedPacket(userId, redPacketConfig));
                data.setWithdrawList(doFindWithdrawList(userId, 2));
                if (data.getRedPacket() != null) {
                    Long redPacketTotalId = Long.valueOf(data.getRedPacket().get(AfOpenRedPacketHomeVo.KEY_REDPACKET_ID));
                    data.setOpenList(doFindOpenList(redPacketTotalId, 2));
                }
            }

            AfRedPacketTotalDo shareRedPacket = afRedPacketTotalService.getById(shareId);
            AfRedPacketHelpOpenDo helpOpenDo = afRedPacketHelpOpenService
                    .getByOpenIdAndUserId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID), shareRedPacket.getUserId());
            if (helpOpenDo == null) {
                data.setIsCanOpen(YesNoStatus.YES.getCode());
            } else {
                data.setIsCanOpen(YesNoStatus.NO.getCode());
            }

            Map<String, String> helpOpenInfo = new HashMap<>();
            helpOpenInfo.put("isOverdue", YesNoStatus.NO.getCode());
            Integer overdueIntervalHour = redPacketConfig.getInteger("overdueIntervalHour");
            if (DateUtil.addHoures(shareRedPacket.getGmtCreate(), overdueIntervalHour).before(new Date())) {
                helpOpenInfo.put("isOverdue", YesNoStatus.YES.getCode());
            }
            if (data.getIsCanOpen().equals(YesNoStatus.YES.getCode())) {
                UserWxInfoDto shareUserWxInfo = afUserThirdInfoService.getWxOrLocalUserInfo(shareRedPacket.getUserId());
                helpOpenInfo.put("avatar", shareUserWxInfo.getAvatar());
                helpOpenInfo.put("nick", shareUserWxInfo.getNick());
            } else if (helpOpenDo.getRedPacketTotalId().equals(shareRedPacket.getRid())) {
                UserWxInfoDto shareUserWxInfo = afUserThirdInfoService.getWxOrLocalUserInfo(shareRedPacket.getUserId());
                helpOpenInfo.put("avatar", shareUserWxInfo.getAvatar());
                helpOpenInfo.put("nick", shareUserWxInfo.getNick());
                helpOpenInfo.put("amount", helpOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
            }

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoOutSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "页面失效，请刷新页面重试").toString();
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
    public String findOpenList(@RequestParam("id") Long id) {
        Map<String, Object> data = new HashMap<>();

        AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getById(id);
        Map<String, String> redPacket = new HashMap<>();
        redPacket.put("id", redPacketTotalDo.getRid().toString());
        redPacket.put("amount", redPacketTotalDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
        JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
        BigDecimal restAmount = redPacketConfig.getBigDecimal("thresholdAmount")
                .subtract(redPacketTotalDo.getAmount());
        redPacket.put("restAmount", restAmount.compareTo(BigDecimal.ZERO) < 0 ? "0.00" :
                restAmount.setScale(2, RoundingMode.HALF_UP).toString());
        data.put("redPacket", redPacket);

        data.put("openList", doFindOpenList(id, null));

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
    public String findWithdrawList(HttpServletRequest request,
                                   @RequestParam(value = "code", required = false) String code) {
        try {
            Long userId = null;
            if (StringUtil.isBlank(code)) {
                FanbeiWebContext context = doWebCheck(request, true);
                AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
                userId = userDo.getRid();
            } else {
                JSONObject userWxInfo = getUserWxInfoByCode(code);
                userId = afUserThirdInfoService.getUserIdByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
            }
            if (userId != null) {
                List<Map<String, String>> data = doFindWithdrawList(userId, null);
                return H5CommonResponse.getNewInstance(true, "", "", data).toString();
            } else {
                return H5CommonResponse.getNewInstance(false, "没有绑定手机号", "", null).toString();
            }
        } catch (FanbeiException e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
                return H5CommonResponse.getNewInstance(false, "没有登录", "", null).toString();
            }
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        }
    }

    /**
     * 拆红包
     *
     * @author wangli
     * @date 2018/5/7 9:37
     */
    @RequestMapping(value = "/open", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String open(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
                       @RequestParam("sourceType") String sourceType) {
        try {
            Long userId = null;
            String userName = null;
            if (StringUtil.isBlank(code)) {
                FanbeiWebContext context = doWebCheck(request, true);
                AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
                userId = userDo.getRid();
                userName = userDo.getUserName();
            } else {
                JSONObject userWxInfo = getUserWxInfoByCode(code);
                UserWxInfoDto localUserInfo = afUserThirdInfoService
                        .getLocalUserInfoByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
                if (localUserInfo != null) {
                    userId = localUserInfo.getUserId();
                    userName = localUserInfo.getUserName();
                } else {
                    return H5CommonResponse.getNewInstance(false, "没有绑定手机号", "", null).toString();
                }
            }

            AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
            JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());

            if (!isCanOpen(userId, redPacketConfig)) {
                return H5CommonResponse.getNewInstance(false, "您已不能再拆红包了", "", null).toString();
            }
            AfRedPacketSelfOpenDo selfOpenDo = afRedPacketSelfOpenService.open(userId, userName, sourceType);
            Map<String, String> data = new HashMap<>();
            data.put("id", selfOpenDo.getRedPacketTotalId().toString());
            data.put("amount", selfOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
            BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
            AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getById(selfOpenDo.getRedPacketTotalId());
            BigDecimal restAmount = thresholdAmount.subtract(redPacketTotalDo.getAmount());
            data.put("restAmount", restAmount.compareTo(BigDecimal.ZERO) < 0 ? "0.00"
                    : restAmount.setScale(2, RoundingMode.HALF_UP).toString());

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (FanbeiException e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
                return H5CommonResponse.getNewInstance(false, "没有登录", "", null).toString();
            }
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
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
    public String helpOpen(@RequestParam("code") String code, @RequestParam("shareId") Long shareId) {
        try {
            JSONObject userWxInfo = getUserWxInfoByCode(code);
            AfRedPacketHelpOpenDo helpOpenDo = new AfRedPacketHelpOpenDo();
            helpOpenDo.setOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
            helpOpenDo.setFriendNick(userWxInfo.getString(UserWxInfoDto.KEY_NICK));
            helpOpenDo.setFriendAvatar(userWxInfo.getString(UserWxInfoDto.KEY_AVATAR));;
            helpOpenDo.setRedPacketTotalId(shareId);
            helpOpenDo = afRedPacketHelpOpenService.open(helpOpenDo);

            Map<String, String> data = new HashMap<>();
            UserWxInfoDto userWxInfoDto = afUserThirdInfoService.getWxOrLocalUserInfo(helpOpenDo.getUserId());
            data.put("nick", userWxInfoDto.getNick());
            data.put("amount", helpOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        }
    }

    @RequestMapping(value = "/bindPhoneAndOpen", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String bindPhoneAndOpen(HttpServletRequest request, @RequestParam("code") String code,
                                   @RequestParam("verifyCode") String verifyCode, @RequestParam("token") String token,
                                   @RequestParam("bsqToken") String bsqToken, @RequestParam("mobile") String mobile) {
        try {
            tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request),
                    mobile, mobile, "");
        } catch (Exception e) {
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR.getDesc()).toString();
        }

        H5CommonResponse response = validateVerifyCode(verifyCode, mobile);
        if (!response.getSuccess()) {
            return response.toString();
        }

        AfUserDo userDo = afUserService.getUserByUserName(mobile);
        if (userDo == null) {
            userDo = registerUser(request, mobile, bsqToken);
        }

        try {
            JSONObject userWxInfo = getUserWxInfoByCode(code);
            afUserThirdInfoService.bindUserWxInfo(userWxInfo, userDo.getRid(), userDo.getUserName());

            Map<String, String> data = null;
            AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
            JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
            if (isCanOpen(userDo.getRid(), redPacketConfig)) {
                data = new HashMap<>();
                AfRedPacketSelfOpenDo selfOpenDo = afRedPacketSelfOpenService.open(userDo.getRid(),
                        userDo.getUserName(), SelfOpenRedPacketSourceType.OPEN_SELF.getCode());
                data.put("id", selfOpenDo.getRedPacketTotalId().toString());
                data.put("amount", selfOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
                BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
                AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getById(selfOpenDo.getRedPacketTotalId());
                BigDecimal restAmount = thresholdAmount.subtract(redPacketTotalDo.getAmount());
                data.put("restAmount", restAmount.compareTo(BigDecimal.ZERO) < 0 ? "0.00"
                        : restAmount.setScale(2, RoundingMode.HALF_UP).toString());
            }

            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } catch (Exception e) {
            logger.error("/redPacket/bindPhoneAndOpen, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
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
    public String withdraw(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
                           @RequestParam("id") Long id) {
        try {
            String userName = null;
            if (StringUtil.isBlank(code)) {
                FanbeiWebContext context = doWebCheck(request, true);
                userName = context.getUserName();
            } else {
                JSONObject userWxInfo = getUserWxInfoByCode(code);
                UserWxInfoDto localUserInfo = afUserThirdInfoService
                        .getLocalUserInfoByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
                userName = localUserInfo.getUserName();
            }

            afRedPacketTotalService.withdraw(id, userName);
            return H5CommonResponse.getNewInstance(true, "").toString();
        } catch (FanbeiException e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
                return H5CommonResponse.getNewInstance(false, "没有登录", "", null).toString();
            }
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        } catch (Exception e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
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

    // 获取提现总人数
    private int getWithdrawTotalNum(JSONObject redPacketConfig) {
        int withdrawTotalNum = afRedPacketTotalService.getWithdrawTotalNum();
        Integer configWithdrawTotalNum = redPacketConfig.getInteger("withdrawTotalNum");
        configWithdrawTotalNum = configWithdrawTotalNum == null ? 0 : configWithdrawTotalNum;
        return withdrawTotalNum + configWithdrawTotalNum;
    }

    // 获取红包信息
    private Map<String,String> getRedPacket(Long userId, JSONObject redPacketConfig) {
        Integer overdueIntervalHour = redPacketConfig.getInteger("overdueIntervalHour");
        AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getTheOpening(userId, overdueIntervalHour);
        if (redPacketConfig == null) return null;

        Map<String, String> result = new HashMap<>();
        result.put(AfOpenRedPacketHomeVo.KEY_REDPACKET_ID, redPacketTotalDo.getRid().toString());
        result.put("amount", redPacketTotalDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        result.put("withdrawLimitAmount", redPacketConfig.getString("thresholdAmount"));
        result.put("gmtOverdue",
                DateUtil.formatDateTime(DateUtil.addHoures(redPacketTotalDo.getGmtCreate(), overdueIntervalHour)));
        return result;
    }

    // 查找用户提现记录
    private List<Map<String,String>> doFindWithdrawList(Long userId, Integer queryNum) {
        List<Map<String, String>> result = new ArrayList<>();
        List<AfRedPacketTotalDo> withdrawList = afRedPacketTotalService.findWithdrawList(userId, queryNum);
        if (CollectionUtil.isNotEmpty(withdrawList)) {
            result = CollectionConverterUtil.convertToListFromList(withdrawList,
                    new Converter<AfRedPacketTotalDo, Map<String, String>>() {
                @Override
                public Map<String, String> convert(AfRedPacketTotalDo source) {
                    Map<String, String> e = new HashMap<>();
                    e.put("gmtWithdraw", DateUtil.formatDateTime(source.getGmtWithdraw()));
                    e.put("desc", "成功提现" + source.getAmount().setScale(2, RoundingMode.HALF_UP).toString() + "元");
                    return e;
                }
            });
            return result;
        }
        return result;
    }

    // 查找拆红包记录
    private List<Map<String,String>> doFindOpenList(Long redPacketTotalId, Integer queryNum) {
        List<Map<String, String>> result = new ArrayList<>();

        List<AfRedPacketHelpOpenDo> helpOpenList = afRedPacketHelpOpenService
                .findOpenRecordList(redPacketTotalId, queryNum);
        List<AfRedPacketSelfOpenDto> selfOpenList = null;
        if (queryNum == null || queryNum == 0) {
            selfOpenList = afRedPacketSelfOpenService.findOpenRecordList(redPacketTotalId);
        }

        if (CollectionUtil.isNotEmpty(helpOpenList)) {
            result.addAll(CollectionConverterUtil.convertToListFromList(helpOpenList,
                    new Converter<AfRedPacketHelpOpenDo, Map<String, String>>() {
                        @Override
                        public Map<String, String> convert(AfRedPacketHelpOpenDo source) {
                            Map<String, String> e = new HashMap<>();
                            e.put("avatar", source.getFriendAvatar());
                            e.put("nick", source.getFriendNick());
                            e.put("desc", "帮你拆了" +
                                    source.getAmount().setScale(2, RoundingMode.HALF_UP).toString() + "元");
                            return e;
                        }
                    }));
        }
        if (CollectionUtil.isNotEmpty(selfOpenList)) {
            Map<String, String> e = new HashMap<>();
            e.put("avatar", selfOpenList.get(0).getUserAvatar());
            e.put("nick", selfOpenList.get(0).getUserNick());
            StringBuilder sb = new StringBuilder("你拆了");
            for (int i = 0; i < selfOpenList.size(); i++) {
                if (i < selfOpenList.size() - 1) {
                    sb.append(selfOpenList.get(i).getAmount().setScale(2, RoundingMode.HALF_UP).toString()
                            + "元+");
                } else {
                    sb.append(selfOpenList.get(i).getAmount().setScale(2, RoundingMode.HALF_UP).toString()
                            + "元");
                }
            }
            e.put("desc", sb.toString());
            result.add(e);
        }
        return result;
    }

    // 根据code获取用户微信信息
    private JSONObject getUserWxInfoByCode(String code) {
        String key = "openRedPacket:userWxInfo:" + code;
        JSONObject userWxInfo = (JSONObject) bizCacheUtil.getObject(key);
        if (userWxInfo == null) {
            userWxInfo = WxUtil.getUserInfo(WxUtil.getWxAppId(), WxUtil.getWxSecret(), code);
            if (userWxInfo != null && userWxInfo.getInteger("errcode") == null) {
                bizCacheUtil.saveObject(key, userWxInfo, Constants.MINITS_OF_FIVE);
            } else {
                String errmsg = userWxInfo.getString("errmsg");
                throw new RuntimeException(errmsg == null ? "用户微信信息为空" : errmsg);
            }
        }
        return userWxInfo;
    }

    // 判断用户是否可以再拆红包
    private boolean isCanOpen(Long userId, JSONObject redPacketConfig) {
        AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService
                .getTheOpening(userId, redPacketConfig.getInteger("overdueIntervalHour"));
        if (redPacketTotalDo == null) return true;

        int num = afRedPacketSelfOpenService.getOpenedNum(redPacketTotalDo.getRid());
        Integer shareTime = redPacketConfig.getInteger("shareTime");
        if (num < (shareTime + 1)) {
            return true;
        }

        return false;
    }

    // 验证验证码
    private H5CommonResponse validateVerifyCode(String verifyCode, String mobile) {
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
        if (smsDo == null) {
            logger.error("/redPacket/bindPhoneAndOpen, error=sms record is empty");
            return H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配");
        }
        String realCode = smsDo.getVerifyCode();
        if (!StringUtils.equals(verifyCode, realCode)) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is invalid");
            return H5CommonResponse.getNewInstance(false,  FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc());
        }
        if (smsDo.getIsCheck() == 1) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is already invalid");
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc());
        }
        // 判断验证码是否过期
        if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_SIXTY))) {
            logger.error("/redPacket/bindPhoneAndOpen, error=verifyCode is overdue");
            return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc());
        }
        afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
        return H5CommonResponse.getNewInstance(true, "");
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
        userDo.setRecommendId(0l);
        long userId = afUserService.addUser(userDo);
        Long invteLong = Constants.INVITE_START_VALUE + userId;
        String inviteCode = Long.toString(invteLong, 36);
        userDo.setRecommendCode(inviteCode);
        afUserService.updateUser(userDo);
        return userDo;
    }
}
