package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOpenRedPacketHomeVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.util.ArrayList;
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
                data.setWithdrawList(findWithdrawList(userId, 2));
                if (data.getRedPacket() != null) {
                    Long redPacketTotalId = Long.valueOf(data.getRedPacket().get(AfOpenRedPacketHomeVo.KEY_REDPACKET_ID));
                    data.setOpenList(findOpenList(redPacketTotalId, 2));
                }
            }
            return H5CommonResponse.getNewInstance(true, "", "", data).toString();
        } /*catch (FanbeiException e) {
            logger.error("/redPacket/getHomeInfoInSite, error={}", e);
            if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
                return H5CommonResponse.getNewInstance(false, "没有登录", "", null).toString();
            }
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        } */catch (Exception e) {
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
        result.put("withdrawLimitAmount", redPacketConfig.getString("threshold"));
        result.put("gmtOverdue",
                DateUtil.formatDateTime(DateUtil.addHoures(redPacketTotalDo.getGmtCreate(), overdueIntervalHour)));
        return result;
    }

    // 查找用户提现记录
    private List<Map<String,String>> findWithdrawList(Long userId, Integer queryNum) {
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
    private List<Map<String,String>> findOpenList(Long redPacketTotalId, Integer queryNum) {
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
}
