package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 沈铖 2017/7/14 下午3:14
 * @类描述: 商圈相关接口
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5TradeController extends BaseController {

    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;

    @RequestMapping(value = "initTradeInfo", method = RequestMethod.GET)
    public void initTradeInfo(HttpServletRequest request, ModelMap model) {
        model.put("isLogin", "no");
        String bid = request.getParameter("bid");
        if (StringUtil.isBlank(bid)) {
            return;
        }

        bid = AesUtil.decryptFromBase64(bid, Constants.TRADE_AES_DECRYPT_PASSWORD);
        AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getById(Long.parseLong(bid));
        if (afTradeBusinessInfoDo == null) {
            return;
        }

        FanbeiWebContext context = null;
        try {
            context = doWebCheck(request, true);
        } catch (Exception e) {
            return;
        }

        if (!context.isLogin()) {
            return;
        }
        model.put("name", afTradeBusinessInfoDo.getName());
        model.put("id", afTradeBusinessInfoDo.getId());
        model.put("isLogin", "yes");
        String userName = context.getUserName();
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountInfoByUserName(userName);
        Double canUseAmount = BigDecimalUtil.subtract(afUserAccountDo.getAuAmount(), BigDecimalUtil.add(afUserAccountDo.getUsedAmount(), afUserAccountDo.getFreezeAmount())).doubleValue();
        model.put("canUseAmount", canUseAmount);
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

}
