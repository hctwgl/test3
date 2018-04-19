package com.ald.fanbei.api.web.third.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.ArbitrationRespBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.ArbitrationService;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：在线仲裁系统接口
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 下午4:50:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Controller
@RequestMapping("/third/arbitration")
public class ArbitrationController {

    @Resource
    ArbitrationService arbitrationService;
    @Resource
    AfBorrowCashDao afBorrowCashDao;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserAccountService afUserAccountService;

    /**
     * 该API接口由互仲平台向客户平台发起该请求，客户平台回应并返回相应结果信息。通过该接口客户返回案件订单相关的信息。
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getOrderInfo"}, method = RequestMethod.POST)
    public String getOrderInfo(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));

	return arbitrationService.getOrderInfo(loanBillNo);
    }

    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户平台回应并返回相应结果信息。通过该接口客户返回案件订单相关的金额信息
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getFundInfo"}, method = RequestMethod.POST)
    public String getFundInfo(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));

	return arbitrationService.getFundInfo(loanBillNo);
    }

    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户平台回应并返回相应结果信息
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getLitiGants"}, method = RequestMethod.POST)
    public String getLitiGants(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));
	String ltype = ObjectUtils.toString(request
		.getParameter("ltype"));
	

	return arbitrationService.getLitiGants(loanBillNo,ltype);
    }
    
    
    
    
    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户平台回应并返回相应结果信息。通过该接口，客户返回相应案件订单的借款协议列表
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getCreditAgreement"}, method = RequestMethod.POST)
    public String getCreditAgreement(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));

	return arbitrationService.getCreditAgreement(loanBillNo);
    }

    
    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户平台回应并返回相应结果信息。通过该接口客户返回案件订单相关的借款信息列表
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getCreditInfo"}, method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
    public String getCreditInfo(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));

	return arbitrationService.getCreditInfo(loanBillNo);
    }
    
    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户平台回应并返回相应结果信息。通过该接口客户返回案件订单相关的金额信息
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getRefundInfo"}, method = RequestMethod.POST)
    public String getRefundInfo(HttpServletRequest request,
	    HttpServletResponse response) {
	String loanBillNo = ObjectUtils.toString(request
		.getParameter("loanBillNo"));

	return arbitrationService.getRefundInfo(loanBillNo);
    }
}
