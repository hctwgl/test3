package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.TradeOrderStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 沈铖 2017/7/14 下午3:14
 * @类描述: 商圈相关接口
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/app/")
public class AppH5TradeController extends BaseController {

    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserService afUserService;

    @RequestMapping(value = "initTradeInfo", method = RequestMethod.GET)
    public void initTradeInfo(HttpServletRequest request, ModelMap model) {
        model.put("isLogin", "no");
        String bid = request.getParameter("bid");
        if (StringUtil.isBlank(bid)) {
            return;
        }

        bid = AesUtil.decryptFromBase64(bid, Constants.TRADE_AES_DECRYPT_PASSWORD);
        AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(Long.parseLong(bid));
        if (afTradeBusinessInfoDo == null || afTradeBusinessInfoDo.getStatus().equals(2)) {
            return;
        }

        FanbeiWebContext context = null;
        try {
            context = doWebCheckNoAjax(request, true);
        } catch (Exception e) {
            return;
        }

        if (!context.isLogin()) {
            return;
        }

        //认证状态
        AfUserDo afUserDo = afUserService.getUserByUserName(context.getUserName());
        if (afUserDo == null) {
            return;
        }
        AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(afUserDo.getRid());
        AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
        Integer status = getAuthStatus(auth, account, context.getAppVersion());
        model.put("isShowMention", status);
        if (status.equals(3)) {
            model.put("realName", account.getRealName());
            model.put("idNumber", Base64.encodeString(account.getIdNumber()));
        }

        model.put("name", afTradeBusinessInfoDo.getName());
        model.put("id", afTradeBusinessInfoDo.getBusinessId());
        model.put("isLogin", "yes");
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
        BigDecimal auAmount = afUserAccountDo.getAuAmount()==null?BigDecimal.ZERO:afUserAccountDo.getAuAmount();
        BigDecimal usedAmount = afUserAccountDo.getUsedAmount()==null?BigDecimal.ZERO:afUserAccountDo.getUsedAmount();
        BigDecimal freezeAmount = afUserAccountDo.getFreezeAmount()==null?BigDecimal.ZERO:afUserAccountDo.getFreezeAmount();
        Double canUseAmount = BigDecimalUtil.subtract(auAmount, BigDecimalUtil.add(usedAmount, freezeAmount)).doubleValue();
        model.put("canUseAmount", canUseAmount);

    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        RequestDataVo reqVo = new RequestDataVo();
        JSONObject jsonObj = JSON.parseObject(requestData);
        reqVo.setId(jsonObj.getString("id"));
        reqVo.setMethod(request.getRequestURI());
        reqVo.setSystem(jsonObj);

        return reqVo;
    }

    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

    private Integer getAuthStatus(AfUserAuthDo auth, AfUserAccountDo account, Integer appVersion) {
        Integer status = 0;
        if (YesNoStatus.NO.getCode().equals(auth.getRealnameStatus())) { //判断实名认证
            status = 1;
        } else if (YesNoStatus.NO.getCode().equals(auth.getFacesStatus())) { //判断人脸识别
            status = 2;
        } else if (YesNoStatus.NO.getCode().equals(auth.getBankcardStatus())) { //判断绑卡状态
            status = 3;
        } else if (YesNoStatus.NO.getCode().equals(getConsumeStatus(auth, account, appVersion))) { //判断是否提额
            status = 4;
        } else if (!(YesNoStatus.YES.getCode().equals(auth.getJinpoStatus())
                && YesNoStatus.YES.getCode().equals(auth.getFundStatus())
                && YesNoStatus.YES.getCode().equals(auth.getCreditStatus())
                && YesNoStatus.YES.getCode().equals(auth.getAlipayStatus()))) { //公积金，行用卡，社保和支付宝认证状态
            status = 5;
        }

        return status;
    }

    private String getConsumeStatus(AfUserAuthDo auth, AfUserAccountDo account, Integer appVersion) {
        String status = YesNoStatus.NO.getCode();
        if (account.getAuAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (appVersion >= 340) {
                if (StringUtil.equals(YesNoStatus.YES.getCode(), auth.getIvsStatus())// 反欺诈分已验证
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getZmStatus())// 芝麻信用已验证
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getTeldirStatus())// 通讯录匹配状态
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getMobileStatus())// 手机运营商
//						&& (null != auth.getGmtMobile() && DateUtil.beforeDay(auth.getGmtMobile(), DateUtil.addMonths(new Date(), 2)))// 手机运营商认证时间小于两个月
//						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getContactorStatus())// 紧急联系人
//						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getLocationStatus())// 定位
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getRiskStatus())) { // 强风控状态
                    status = YesNoStatus.YES.getCode();
                }
            } else {
                if (StringUtil.equals(YesNoStatus.YES.getCode(), auth.getIvsStatus())// 反欺诈分已验证
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getZmStatus())// 芝麻信用已验证
                        && StringUtil.equals(YesNoStatus.YES.getCode(), auth.getTeldirStatus())// 通讯录匹配状态
                        ) {
                    status = YesNoStatus.YES.getCode();
                }
            }

        }
        return status;
    }

}
