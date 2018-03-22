package com.ald.fanbei.api.web.third.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.Auth51FundRespBo;
import com.ald.fanbei.api.biz.bo.assetside.AssetSideRespBo;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.Auth51FundUtil;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 51公积金回调
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年1月9日下午3:53:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/51fund")
public class Auth51FundController {
	@Resource
	Auth51FundUtil auth51FundUtil;
	@Resource
	AfUserAuthService afUserAuthService;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = { "/giveBack" }, method = RequestMethod.GET)
    @ResponseBody
    public String giveBack(@RequestParam("orderSn") String orderSn,@RequestParam("userId") String userId,HttpServletRequest request,HttpServletResponse response) throws IOException{
		try {
			int result = auth51FundUtil.giveBack(orderSn,userId+"");
			if (result == 1) {
				//保存认证的状态为认证中
				AfUserAuthDo authDo = new AfUserAuthDo();
				authDo.setUserId(NumberUtil.objToLongDefault(userId, 0l));
				authDo.setGmtFund(new Date(System.currentTimeMillis()));
				authDo.setFundStatus(SupplyCertifyStatus.WAIT.getCode());
				afUserAuthService.updateUserAuth(authDo);
			}
        } catch (Exception e) {
        	logger.error("51fund giveBack error", e);
        	return "FAIL";
        }
		return "SUCCESS";
	}
	
}
