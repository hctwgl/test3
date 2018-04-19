package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.bo.GetBrandCouponCountRequestBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfOrderCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.MineHomeVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * App我的首页信息
 *
 * @author wangli
 * @date 2018/4/12 19:45
 */
@Controller("mineHomeApi")
public class MineHomeApi implements ApiHandle {

    private static final String DESC_ONLINEAMOUNT_NOAUTH = "购物最高额度";

    private static final String DESC_ONLINEAMOUNT_AUTH = "购物总额度";

    private static final String DESC_AMOUNT_NOAUTH = "借款最高额度";

    private static final String DESC_AMOUNT_AUTH = "借款总额度";

    @Autowired
    private AfUserAccountService afUserAccountService;

    @Autowired
    private AfSigninService afSigninService;

    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfUserAuthService afUserAuthService;

    @Autowired
    private AfUserCouponService afUserCouponService;

    @Autowired
    private AfBorrowBillService afBorrowBillService;

    @Autowired
    private AfUserAccountSenceService afUserAccountSenceService;

    @Autowired
    private AfUserAuthStatusService afUserAuthStatusService;

    @Autowired
    private AfOrderService afOrderService;

    @Autowired
    private AfBorrowCashService afBorrowCashService;

    @Autowired
    private AfLoanService afLoanService;

    @Autowired
    private AfLoanPeriodsService afLoanPeriodsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

        MineHomeVo data = new MineHomeVo();
        Long userId = context.getUserId();

        fillUserInfo(data, userId);
        fillCreditInfo(data, userId);
        fillAccountInfo(data, userId);
        fillOrderInfo(data, userId);
        fillBannerAndNavigationInfo(data, requestDataVo.getId());
        resp.setResponseData(data);

        return resp;
    }

    // 填充用户信息
    private void fillUserInfo(MineHomeVo data, Long userId) {
        data.setIsSign(YesNoStatus.NO.getCode());
        data.setCustomerPhone(randomPhone());

        if (userId != null) {
            AfUserAccountDto userAccountInfo = afUserAccountService.getUserAndAccountByUserId(userId);
            if (userAccountInfo != null) {
                data.setIsLogin(YesNoStatus.YES.getCode());
                data.setAvata(userAccountInfo.getAvatar());
                data.setNick(userAccountInfo.getNick());
                data.setUserName(userAccountInfo.getUserName());
                data.setRealName(userAccountInfo.getRealName());
                data.setIdNumber(userAccountInfo.getIdNumber());
                data.setMobile(userAccountInfo.getMobile());
                data.setRecommendCode(userAccountInfo.getRecommendCode());
                data.setRebateAmount(userAccountInfo.getRebateAmount()
                        .setScale(2, BigDecimal.ROUND_HALF_UP).toString());


                if (!StringUtil.isBlank(userAccountInfo.getPassword())) {
                    data.setIsPayPwd(YesNoStatus.YES.getCode());
                }

                AfSigninDo signinDo = afSigninService.selectSigninByUserId(userId);
                if (signinDo != null && signinDo.getGmtSeries() != null
                        && DateUtil.isSameDay(new Date(), signinDo.getGmtSeries())) {
                    data.setIsSign(YesNoStatus.YES.getCode());
                }

                AfResourceDo resourceDo  = afResourceService
                        .getConfigByTypesAndSecType("PERSONAL_CENTER", "SIGIN_URL");
                if (resourceDo != null) {
                    data.setSignUrl(resourceDo.getValue());
                }

                AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
                if (userAuthDo != null) {
                    data.setFaceStatus(userAuthDo.getFacesStatus());
                    data.setBankcardStatus(userAuthDo.getBankcardStatus());
                }
            }
        }
    }

    // 填充信用信息
    private void fillCreditInfo(MineHomeVo data, Long userId) {
        AfResourceDo creditResource = afResourceService.getSingleResourceBytype("CREDIT_AUTH_STATUS");
        List<String> cashDescs = getAuthDesc(creditResource.getValue3(), "one");// 借款最高额度
        List<String> consumeDescs = getAuthDesc(creditResource.getValue4(), "one");// 购物最高额度

        // 未登录
        if (userId == null) {
            data.setShowAmount(cashDescs.get(0));
            data.setDesc(DESC_AMOUNT_NOAUTH);
            data.setOnlineShowAmount(consumeDescs.get(0));
            data.setOnlineDesc(DESC_ONLINEAMOUNT_NOAUTH);
            return;
        }

        // 购物额度认证状态
        AfUserAuthStatusDo consumeAuthStatus = afUserAuthStatusService
                .getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        if (consumeAuthStatus == null) {
            // 未认证
            data.setOnlineShowAmount(consumeDescs.get(0));
            data.setOnlineDesc(DESC_ONLINEAMOUNT_NOAUTH);
        } else if (consumeAuthStatus.getStatus().equals(UserAuthSceneStatus.FAILED.getCode())) {
            // 已认证，未通过强风控
            data.setOnlineShowAmount("0.00");
            data.setOnlineDesc(DESC_ONLINEAMOUNT_AUTH);
        } else if (consumeAuthStatus.getStatus().equals(UserAuthSceneStatus.YES.getCode())) {
            // 通过强风控审核
            // 授予的额度
            AfUserAccountSenceDo onlineScene = afUserAccountSenceService
                    .getByUserIdAndScene("ONLINE",userId);
            BigDecimal onlineAuAmount = onlineAuAmount = onlineScene.getAuAmount();
            // 临时额度
            AfInterimAuDo interimAuDo = afBorrowBillService.selectInterimAmountByUserId(userId);
            if (interimAuDo != null
                    && interimAuDo.getGmtFailuretime().getTime() > new Date().getTime()) {
                onlineAuAmount = onlineAuAmount.add(interimAuDo.getInterimAmount());
            }
            data.setOnlineShowAmount(onlineAuAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            data.setOnlineDesc(DESC_ONLINEAMOUNT_AUTH);
        }

        // 借款额度认证状态
        AfUserAuthDo cashAuthStatus = afUserAuthService.getUserAuthInfoByUserId(userId);
        if (StringUtil.equals(cashAuthStatus.getBankcardStatus(), "N")
                && StringUtil.equals(cashAuthStatus.getZmStatus(), "N")
                && StringUtil.equals(cashAuthStatus.getMobileStatus(), "N")
                && StringUtil.equals(cashAuthStatus.getTeldirStatus(),"N")
                && StringUtil.equals(cashAuthStatus.getFacesStatus(),"N")) {
            // 未认证
            data.setShowAmount(cashDescs.get(0));
            data.setDesc(DESC_AMOUNT_NOAUTH);
        } else if (StringUtil.equals(cashAuthStatus.getBankcardStatus(), "N")
                || StringUtil.equals(cashAuthStatus.getZmStatus(),"N")
                || StringUtil.equals(cashAuthStatus.getMobileStatus(),"N")
                || StringUtil.equals(cashAuthStatus.getTeldirStatus(),"N")
                || StringUtil.equals(cashAuthStatus.getFacesStatus(),"N")) {
            // 认证中
            data.setShowAmount(cashDescs.get(0));
            data.setDesc(DESC_AMOUNT_NOAUTH);
        } else if (StringUtil.equals(cashAuthStatus.getRiskStatus(), RiskStatus.NO.getCode())) {
            // 已认证，未通过强风控
            data.setShowAmount("0.00");
            data.setDesc(DESC_AMOUNT_AUTH);
        } else if (StringUtil.equals(cashAuthStatus.getRiskStatus(), RiskStatus.YES.getCode())) {
            // 已认证，通过强风控
            AfUserAccountSenceDo loanTotalSenceDo = afUserAccountSenceService
                    .getByUserIdAndScene(SceneType.LOAN_TOTAL.getName(), userId);
            if (loanTotalSenceDo != null) {
                data.setShowAmount(
                        loanTotalSenceDo.getAuAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                data.setDesc(DESC_AMOUNT_AUTH);
            }
        }

    }

    // 填充账户信息
    private void fillAccountInfo(MineHomeVo data, Long userId) {
        if (userId == null) return;

        // 优惠券
        int coupleCount = afUserCouponService.getUserCouponByUserNouse(userId);
        GetBrandCouponCountRequestBo bo = new GetBrandCouponCountRequestBo();
        bo.setUserId(userId.toString());
        String resultString = HttpUtil.doHttpPost(
                ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/api/promotion/get_coupon_num",
                JSONObject.toJSONString(bo));
        JSONObject resultJson = JSONObject.parseObject(resultString);
        if (resultJson == null || !"0".equals(resultJson.getString("code"))) {
            data.setCouponCount(coupleCount);
            data.setBrandCouponCount(0);
        } else {
            JSONObject resultData = resultJson.getJSONObject("data");
            Integer brandCouponCount = resultData.getInteger("availableNum");
            data.setCouponCount(coupleCount + brandCouponCount);
        }
        data.setPlantformCouponCount(coupleCount);

        // 本月待还
        // 购物账单待还金额
        BigDecimal borrowBillAmount = getCurrMonthBorrowBillWaitRepaymentAmount(userId);
        // 极速贷账单待还金额
        BigDecimal borrowCashAmount = getBorrowCashWaitRepaymentAmount(userId);
        // 白领贷账单待还
        BigDecimal loanAmount = getLoanWaitRepaymentAmount(userId);
        BigDecimal outMoney = borrowBillAmount.add(borrowCashAmount).add(loanAmount);
        data.setOutMoney(outMoney.setScale(2, RoundingMode.HALF_UP).toEngineeringString());
    }

    // 填充订单信息
    private void fillOrderInfo(MineHomeVo data, Long userId) {
        if (userId == null) return;

        AfOrderCountDto orderCountDto = afOrderService.countStatusNum(userId);
        data.setNewOrderNum(orderCountDto.getNewOrderNum());
        data.setPaidOrderNum(orderCountDto.getPaidOrderNum());
        data.setDeliveredOrderNum(orderCountDto.getDeliveredOrderNum());
        data.setFinishedOrderNum(orderCountDto.getFinishedOrderNum());
    }

    // 填充banner和快速导航信息
    private void fillBannerAndNavigationInfo(MineHomeVo data, String appModel) {
        // banner
        String invelomentType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        logger.info("getDrainageBannerListApi and type = {}", invelomentType);

        List<AfResourceDo> bannerResources = null;
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(invelomentType)
                || Constants.INVELOMENT_TYPE_TEST.equals(invelomentType)) {
            bannerResources = afResourceService.getResourceHomeListByTypeOrderBy(Constants.PERSONAL_CENTER_BANNER);
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(invelomentType) ){
            bannerResources = afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(Constants.PERSONAL_CENTER_BANNER);
        }

        if (CollectionUtil.isNotEmpty(bannerResources)) {
          List<Map<String, Object>> bannerList = CollectionConverterUtil
                  .convertToListFromList(bannerResources, new Converter<AfResourceDo, Map<String, Object>>() {
              @Override
              public Map<String, Object> convert(AfResourceDo source) {
                  Map<String, Object> map = new HashMap<String, Object>();
                  map.put("imageUrl", source.getValue());
                  map.put("type", source.getValue1());
                  map.put("content", source.getValue2());
                  return map;
              }
          });
          data.setBannerList(bannerList);
        }

        // navigation
        List<AfResourceDo> navigationResources = afResourceService
                .getHomeIndexListByOrderby(AfResourceType.PERSONAL_CENTER_NAVIGATION.getCode());
        int nrSize = navigationResources.size();
        for (int i =0; i < nrSize; i++) {
            // 如果配置大于4个，小于8个，则只显示4个
            if (nrSize >= 4 && nrSize < 8) {
                if (i >= 4) {
                    break;
                }
            } else if (nrSize >=  8) {
                // 如果配置大于等于8个，则只显示8个
                if (i >= 8) {
                    break;
                }
            }else if(nrSize < 4 ){
                break;
            }

            AfResourceDo nr = navigationResources.get(i);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("imageUrl", nr.getValue());
            dataMap.put("type", nr.getSecType());
            if (nr.getSecType().equals("NAVIGATION_NATIVE")) {
                String model =  appModel.substring(0, 1);
                if (model.equalsIgnoreCase("a")) {
                    dataMap.put("content", nr.getPic1());
                } else  if (model.equalsIgnoreCase("i")) {
                    dataMap.put("content", nr.getPic2());
                }
            } else {
                dataMap.put("content", nr.getValue2());
            }

            data.addNavigation(dataMap);
        }
    }

    private String randomPhone() {
        String targetPhone = "0571-86803811";
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(AfResourceType.APPCONSUMERPHONE.getCode());
        if(resourceInfo != null && StringUtil.isNotBlank(resourceInfo.getValue())){
            String[] phoneArry = resourceInfo.getValue().split(",");
            if(phoneArry!= null && phoneArry.length > 0){
                targetPhone = RandomUtil.getRandomElement(phoneArry);
                if(StringUtil.isNotBlank(targetPhone)){
                    return targetPhone;
                }
            }
        }
        return targetPhone;
    }

    private List<String> getAuthDesc(String value,String status){
        List<String> listString = new ArrayList();
        JSONArray jsonArray = JSON.parseArray(value);
        boolean judge=true;
        for(int i =0;i<jsonArray.size();i++){
            if(judge){
                JSONObject jsonObject =jsonArray.getJSONObject(i);
                String jsonStatus=jsonObject.getString("status");
                if(status.equals(jsonStatus)){
                    listString.add(jsonObject.getString("quotaSection"));
                    listString.add(jsonObject.getString("desc"));
                    judge=false;
                }
            }
        }
        return listString;
    }

    private BigDecimal getCurrMonthBorrowBillWaitRepaymentAmount(Long userId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        // 本月账单所属年
        int currBillYear = cal.get(Calendar.YEAR);
        // 本月账单所属月
        int currBillMonth = cal.get(Calendar.MONTH) + 1;  
        // 已出账待还款金额
        BigDecimal waitRepaymentOutMoney = afBorrowBillService.getMonthlyBillByStatusNew(userId, currBillYear,
                currBillMonth, BorrowBillStatus.NO.getCode());
        // 逾期金额
        BigDecimal overdueAmount = afBorrowBillService
                .getMonthlyBillByStatusNewV1(userId, BorrowBillStatus.OVERDUE.getCode());
        return waitRepaymentOutMoney.add(overdueAmount);
    }

    private BigDecimal getBorrowCashWaitRepaymentAmount(Long userId) {
        AfBorrowCashDo borrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
        if (borrowCashDo == null) {
            // 没有最早的待还，查询最后一笔借款信息
            borrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
        }
        if (borrowCashDo != null && !borrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
            BigDecimal allAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumOverdue(),
                    borrowCashDo.getOverdueAmount(), borrowCashDo.getRateAmount(), borrowCashDo.getSumRate(),
                    borrowCashDo.getPoundage(), borrowCashDo.getSumRenewalPoundage());
            return BigDecimalUtil.subtract(allAmount, borrowCashDo.getRepayAmount());
        }
        return new BigDecimal(0);
    }

    private BigDecimal getLoanWaitRepaymentAmount(Long userId) {
        AfLoanDo lastLoanDo = afLoanService.getLastByUserIdAndPrdType(userId, LoanType.BLD_LOAN.getCode());
        if (lastLoanDo != null) {
            // 本月待还  TODO:确认本月待还怎么取
            AfLoanPeriodsDo currMonthPeriodsDo = afLoanPeriodsService.getCurrMonthPeriod(lastLoanDo.getRid());
            if (currMonthPeriodsDo != null
                    && !currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.FINISHED.getCode())) {
                return afLoanPeriodsService.calcuRestAmount(currMonthPeriodsDo);
            }
        }
        return new BigDecimal(0);
    }
}
