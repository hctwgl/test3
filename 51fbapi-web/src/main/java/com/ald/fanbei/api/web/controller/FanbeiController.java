package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.intercept.ValidationInterceptor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**
 *
 *@类描述：FanbeiController
 *@author 陈金虎 2017年1月17日 下午6:15:06
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class FanbeiController extends BaseController {

	@Resource
	AfResourceService afResourceService;
	
	@Resource
	ValidationInterceptor validationInterceptor;

    @RequestMapping(value ={
    	    	"/goods/getFootMarkList","/goods/getGoodsInfoByNumId","/good/getGoodsTkRate","/goods/getThirdShopsList",
	"/goods/addFootMark","/goods/getHomeInfo","/goods/getHomeInfoV1","/goods/getHomeInfoV2",
	"/goods/getThirdGoodsList","/goods/getCategoryList","/good/getSearchHome",
    	"/goods/getCateGoodsList","/good/getRecommendGoods","/good/getBrandShopList","/good/getGoodsDetailInfo","/good/getPayTypeList","/goods/getGoodsSpec",
            "/goods/getGoodsRateInfo","/goods/getThirdGoodsAuction","/goods/getHomeGoodsList","/goods/getThirdGoodsDetailInfo","/cashier/start","/cashier/pre" ,"/cashier/getNperList",
	"/category/getAllGoodsCategory","/category/getGoodsList", "/goods/getGoodsRateInfo","/goods/getThirdGoodsAuction","/goods/getThirdGoodsDetailInfo"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String goodsRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    @RequestMapping(value ={
    	"/address/addressList","/address/addAddress","/address/updateInfo","/address/deleteAddress","/mine/commitCode",
    	"/coupon/couponList","/mine/getInviteInfo","/mine/getOrderListCount","/mine/getSettingInfo","/order/payResultOfAlipay","/order/getOrderLogistics"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String mineRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }
    //代买相关
    @RequestMapping(value ={
    		"/agencyBuy/addUserAddress","/agencyBuy/getAgencyCouponList","/agencyBuy/getAgencyNperInfo","/agencyBuy/payAgencyOrder","/agencyBuy/changeUserAddress","/agencyBuy/getDefaultUserAddress","/agencyBuy/deleteUserAddress","/agencyBuy/getUserAddressList","/agencyBuy/submitAgencyBuyOrder"
        ,"/agencyBuy/getAgencyBuyOrderDetail","/agencyBuy/cancelAgencyBuyOrder","/agencyBuy/confirmationCompletedAgencyBuyOrder","/agencyBuy/payAgencyOrderV1","/agencyBuy/buySelfGoods","/agencyBuy/getTradeNperInfo"},method = RequestMethod.POST,produces="application/json;charset=utf-8")
        @ResponseBody
        public String agencyBuyRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
            request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
            response.setContentType("application/json;charset=utf-8");
            return this.processRequest(body, request, false);
    }

    @RequestMapping(value ={
    	"/order/confirmReceipt","/order/buySelfGoods","/order/mobileCharge","/order/getOrderDetailInfo","/order/getOrderList","/order/getOrderNoWithUser","/order/refundOrderApply","/order/deleteOrderInfo","/order/cancelAfterSaleApply","/order/afterSaleLogisticSupply","/order/getOrderAfterSaleInfo","/order/insufficientBalance"
        ,"/order/tradeOrder","/order/getTradeOrderDetailInfo","/order/combinationPay","/order/getBeforePayType","/order/dmNperEnabled"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String orderRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    @RequestMapping(value ={
    	"/system/appUpgrade","/system/commitFeedBack","/system/getSettingInfo","/system/checkVersion","/system/AppLaunchImage","/system/appPopImage",
    	"/system/getTabbarInfor","/resource/getResourceConfig","/system/maidian","/clickAmount/clickAmountNum","/resource/getRedRainRounds" ,"/pushClickAmout/clickPushAmountNum","/resource/getConfigurableNumDay"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String sysRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    @RequestMapping(value = {
    		"/user/userLogin","/user/getVerifyCode","/user/checkVerifyCode","/user/setRegisterPwd","/user/quickRegisterPwd","/user/login","/user/quickLoginOrRegister","/user/resetPwd","/user/setQuickFirstPwd","/user/getUserInfo",
    		"/user/logout","/user/updateUserInfo","/user/getSysMsgList","/user/getMineInfo","/user/getMineCouponCount","/user/getMineCouponList","/user/getCallCenterInfo",
    		"/user/commitFeedback","/user/getCouponList","/user/pickCoupon","/user/getUserCounponListType","/user/getMobileRechargeMoneyList",
    		"/user/withdrawCash","/user/deleteCollection","/user/addCollection","/user/getCollectionList","/user/deleteBankCard","/user/changeEmail",
    		"/user/getBankCardList","/user/getEmailVerifyCode","/user/checkPayPwd","/user/getSigninInfo","/user/setPayPwd","/user/getPayPwdVerifyCode",
    		"/user/checkPayPwdVerifyCode","/user/checkIdNumber","/user/changeLoginPwd","/user/setQuickLoginPwd","/user/getInvitationInfo","/user/signin","/user/changeMobile",
    		"/user/submitShareAction","/user/checkMobileRegistered","/user/checkQuickRegisteredPwd","/user/getImageCode","/user/getRecommedData","/user/getRecommendListByUserId","/user/getActivieResourceByType",
            "/user/getRecommendListSort","/user/getPrizeUser","/user/addRecommendShared","/user/getUserRecommed","/user/checkLoginVerifyCode",
            "/user/changeMobileIfAble", "/user/changeMobileCheckVerifyCode", "/user/changeMobileVerify", "/user/changeMobileSyncConacts","user/getBorrowCashProtocol","/user/getContractPdfUrl",
            "/user/accountAppealCheckSms","/user/accountAppealDo"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String userRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    @RequestMapping(value = {
    		"/borrow/getCashConfirmInfo","/borrow/applyCash","/borrow/getBorrowHomeInfo","/borrow/getConsumeConfirmInfo","/borrow/applyConsume",
    		"/bill/getMyBillHomeInfo","/bill/getMyBillList","/bill/getBillDetailList","/bill/getBillDetailInfo","/repay/getRepaymentConfirmInfo",
            "/repay/submitRepayment","/auth/authYdInfo","/bill/getLimitDetailList","/bill/getLimitDetailInfo","/borrow/getCreditPromoteInfo","/borrow/getOtherPayWay"
            ,"/borrow/getRepaymentInfo","/borrow/submitRepaymentByYiBao","/bill/getRepayMentAmountByMonth","/repay/getRepaymentConfirmInfoV1"
            ,"/bill/getBillListByStatus","/borrow/getCreditPromoteInfoV1"
            ,"/borrow/getBillListByMonthAndYear","/borrow/getBorrowDetailV1","/borrow/getMyBorrowListV1","/borrow/getMyBorrowV1"
            ,"/borrow/getMyHistoryBorrowV1","/borrow/getMyRepaymentDetailV1","/borrow/getMyRepaymentHistoryV1","/borrow/getMyRepaymentV1","/borrow/getRepaymentDetailV1"
            ,"/borrow/getRepaymentCalendarV1","/borrow/getMyHistoryBorrowDetailV1"
            ,"/borrow/getDataView","/borrow/submitClear"
    },method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String fenbeiRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    /**
     * 认证相关
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
	@RequestMapping(value = { "/auth/authRealname", "/auth/authContacts", "/auth/authCredit", "/auth/authZhima", "/auth/authBankcard", "/auth/checkBankcard", "/auth/getBankList",
			"/auth/checkBankcardPay", "/auth/authFace", "/auth/authMobile", "/auth/authContactor","/auth/authChsi","/auth/authZhengxin","/auth/authOnlinebank", "/auth/authLocation", "/auth/authMobileBack", "/auth/getAllowConsume",
			"/auth/getDailyRate", "/auth/saveIdNumber", "/auth/checkIdCardApi", "/auth/updateIdCardApi","/auth/bindingBankcard","/auth/checkMessages",
			"/auth/checkFaceApi","/auth/getYiTuInfo" ,"/auth/uploadYiTuCount","/auth/submitIdNumberInfo","/auth/authStrongRisk","/auth/authStrongRiskV1",
			"/auth/authContactorV1","/auth/authContactsV1","/auth/authCreditV1","/auth/authFaceV1","/auth/authRealnameV1","/auth/submitIdNumberInfoV1","/auth/updateIdCardV1",
			"/auth/authSupplyCertify","/auth/authFund","/auth/authFundNew","/auth/authFundNew/GiveBack","/auth/authSocialSecurity","/auth/authCreditCard","/auth/authSupplyVerifying","/auth/authAlipay",
			"/auth/submitIdNumberInfoForFacePlus","/auth/getFaceType","/auth/getFaceTypeFree","/auth/updateIdCardForFacePlus","/auth/updateRealnameManual","/auth/updateRealnameManualFree","/auth/lookAllQuota"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String authRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		return this.processRequest(body, request, false);
	}


    /**
     * 品牌商城相关
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/brand/getBrandUrl","/brand/getOrderDetailUrl","/brand/getConfirmOrder","/brand/getBrandList"
    		,"/brand/getPayAmount","/brand/payOrder","/brand/getBrandCouponList","/brand/pickBrandCoupon","brand/payOrderV1","/drainage/getBannerList"},method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String brandShopRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    /**
     * 借钱相关
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/borrowCash/getBorrowCashHomeInfo","/borrowCash/getBowCashLogInInfo","/borrowCash/getConfirmBorrowInfo","/borrowCash/applyBorrowCash",
    		"/borrowCash/getBorrowCashDetail","/borrowCash/getBorrowCashList","/borrowCash/getBorrowOverdueList","/repayCash/getConfirmRepayInfo","/good/deleteHistory",
    		"/repayCash/getRepayCashList","/repayCash/getRepayCashInfo","/borrowCash/applyRenewal","/borrowCash/confirmRenewalPay","/borrowCash/getRenewalList",
    		"/borrowCash/getRenewalDetail","/borrowCash/getLoanSupermarketList","/loanMarket/accessLoanSupermarket","borrowCash/applyBorrowCashV1",
            "/repayCash/getConfirmRepayInfoV1","/borrowCash/confirmRenewalPayV1","/repayCash/getRepayCashByOrderId",
            "/borrowCash/getLoanSupermarketTabList","/borrowCash/getLoanSupermarketListByTab","/borrowCash/tearPacket","/instalments/getInstalmentsAd",
            "/borrowCash/getAllBorrowList", "legalborrow/*", "legalborrowV2/*", "loan/*"},method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String borrowCashRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

    /**
     * 运营相关接口相关
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/operateApp/getAgencyBuyOrderList","/operateApp/getOrderSyncOrderInfo","/operateApp/getAgencyBuyOrderInfo"},method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String operateAppRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }
    @Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		if (StringUtils.isBlank(reqData)) {
			throw new FanbeiException("param is null",FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		return reqData;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();
            String method = request.getRequestURI();
            reqVo.setMethod(method);
            reqVo.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
            String appVersion = request.getHeader(Constants.REQ_SYS_NODE_VERSION);
            String netType = request.getHeader(Constants.REQ_SYS_NODE_NETTYPE);
            String userName = request.getHeader(Constants.REQ_SYS_NODE_USERNAME);
            String sign = request.getHeader(Constants.REQ_SYS_NODE_SIGN);
            String time = request.getHeader(Constants.REQ_SYS_NODE_TIME);

            Map<String,Object> system = new HashMap<String,Object>();
            
            system.put(Constants.REQ_SYS_NODE_VERSION, appVersion);
            system.put(Constants.REQ_SYS_NODE_NETTYPE, netType);
            system.put(Constants.REQ_SYS_NODE_USERNAME, userName);
            system.put(Constants.REQ_SYS_NODE_SIGN, sign);
            system.put(Constants.REQ_SYS_NODE_TIME, time);
            
            reqVo.setSystem(system);
            
            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setParams((jsonObj == null || jsonObj.isEmpty()) ? new HashMap<String,Object>() : jsonObj);

            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        ApiHandle methodHandel = apiHandleFactory.getApiHandle(requestDataVo.getMethod());
        // 数据校验拦截器
        validationInterceptor.intercept(requestDataVo, context, httpServletRequest);
        
        ApiHandleResponse handelResult;
        try {
            handelResult = methodHandel.process(requestDataVo,context, httpServletRequest);
            int resultCode = handelResult.getResult().getCode();
            if(resultCode != 1000){
                logger.info(requestDataVo.getId() + " err,Code=" + resultCode);
            }
            return handelResult;
        }catch(FanbeiException e){
        	logger.error("app exception",e);
        	throw e;
		} catch (Exception e) {
            logger.error("sys exception",e);
            throw new FanbeiException("sys exception",FanbeiExceptionCode.SYSTEM_ERROR);
        }
	}

	@RequestMapping(value = { "re1fla5shLoc3alReesC8a1sh" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public void reflashLocalResCash(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String randomParam1 = request.getParameter("926d4e62fe23c77e");
        String randomParam2 = request.getParameter("21603C8898E194C5");
        String value1= "4Sr221Obo03cM89Q";
        String value2= "9pF20aF49221b603Ce8s8";
        if(StringUtil.equals(randomParam1, value1)&&StringUtil.equals(randomParam2, value2)){
            afResourceService.cleanLocalCache();
        }
	}

	   /**
     * 代扣相关
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/Withhold/updateWithholdSwitch","/Withhold/getWithholdInfo","/Withhold/updateWithholdCard","/Withhold/showUserBankCard","/homePage/homeScrollBar","/Withhold/WithholdCheck"},method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String withHoldRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(body, request, false);
    }

}