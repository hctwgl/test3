package com.ald.fanbei.api.biz.util;


import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author 郭帅强 2017年1月16日 下午11:44:10
 * @类描述：用户工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class ProtocolUtil {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(ProtocolUtil.class);

    @Resource
    AfResourceService afResourceService;
    @Resource
    private AfBorrowLegalGoodsService afBorrowLegalGoodsService;
    @Resource
    private AfGoodsService afGoodsService;
    @Resource
    private RiskUtil riskUtil;
    @Resource
    private AfUserService afUserService;

    public List<AfResourceDo> getProtocolList(String type, Map map) {
        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("AGREEMENT");
        List<AfResourceDo> resourceDoList = new ArrayList<>();
        String userName = ObjectUtils.toString(map.get("userName"), "").toString();
        String dayType = ObjectUtils.toString(map.get("type"), "0").toString();
        String appName = ObjectUtils.toString(map.get("appName"), "").toString();
        String ipAddress = ObjectUtils.toString(map.get("ipAddress"), "").toString();
        Long borrowId = NumberUtil.objToLongDefault(map.get("borrowId"), 0l);
        Long renewalId = NumberUtil.objToLongDefault(map.get("renewalId"), 0l);
        int renewalDay = NumberUtil.objToIntDefault(map.get("renewalDay"), 0);
        int nper = NumberUtil.objToIntDefault(map.get("nper"), 0);
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(map.get("renewalAmount"), BigDecimal.ZERO);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(map.get("borrowAmount"), BigDecimal.ZERO);
        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(map.get("poundage"), BigDecimal.ZERO);
        if (afResourceDoList.size() == 0) {
            return resourceDoList;
        }
        AfUserDo userDo = afUserService.getUserByUserName(userName);
        BigDecimal goodsAmount = getGoodsAmount(borrowAmount,dayType,userDo.getRid(),appName,ipAddress);//续借金额
        BigDecimal arriveAmount = borrowAmount.subtract(goodsAmount);//到账金额
        for (AfResourceDo afResourceDo : afResourceDoList) {
            if (afResourceDo.getValue1().contains(type)) {
                if ("RENEWAL_CONTRACT".equals(afResourceDo.getSecType())) {//续借协议
                    afResourceDo.setValue("/fanbei-web/app/protocolLegalRenewalV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&renewalId=" + renewalId + "&renewalDay=" + renewalDay +
                            "&renewalAmount=" + renewalAmount);
                } else if ("INSTALMENT_CONTRACT".equals(afResourceDo.getSecType())) {//分期协议
                    afResourceDo.setValue("/fanbei-web/app/protocolLegalInstalmentV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&nper=" + nper + "&amount=" + borrowAmount +
                            "&poundage=" + poundage);
                } else if ("LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//借款协议
                    afResourceDo.setValue("/fanbei-web/app/protocolLegalCashLoanV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&borrowAmount=" + arriveAmount);
                } else if ("PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//平台服务协议
                    afResourceDo.setValue("/fanbei-web/app/platformServiceProtocol?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&poundage=" + poundage + "&borrowAmount=" + arriveAmount);
                } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                    afResourceDo.setValue("/fanbei-web/app/numProtocol?userName=" + userName);
                } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                    afResourceDo.setValue("/app/sys/riskWarning");
                } else if ("BUYING_RELATED_AGREEMENTS".equals(afResourceDo.getSecType())) {//代买协议
                    afResourceDo.setValue("/fanbei-web/app/protocolAgentBuyService?userName=" + userName);
                } else if ("GOODS_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//搭售商品协议
                    afResourceDo.setValue("/fanbei-web/app/protocolLegalGoodsCashLoanV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&borrowAmount=" + goodsAmount);
                }
                resourceDoList.add(afResourceDo);
            }
        }
        /*AfResourceDo afResourceDo = new AfResourceDo();
        afResourceDo.setValue("/fanbei-web/app/protocolLegalGoodsCashLoanV2?userName=" + userName +
                "&type=" + dayType + "&borrowId=" + borrowId + "&borrowAmount=" + goodsAmount);
        resourceDoList.add(afResourceDo);*/

        return resourceDoList;
    }

    private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
        String oneDay = "";
        String twoDay = "";
        if (null != afResourceDo) {
            oneDay = afResourceDo.getTypeDesc().split(",")[0];
            twoDay = afResourceDo.getTypeDesc().split(",")[1];
        }
        Map<String, Object> rateInfo = Maps.newHashMap();
        double serviceRate = 0;
        double interestRate = 0;
        JSONArray array = JSONObject.parseArray(borrowRate);
        double totalRate = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String borrowTag = info.getString(tag + "Tag");
            if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    interestRate = info.getDouble(tag + "FirstType");
                    totalRate += interestRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    interestRate = info.getDouble(tag + "SecondType");
                    totalRate += interestRate;
                }
            }
            if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "FirstType");
                    totalRate += serviceRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "SecondType");
                    totalRate += serviceRate;
                }
            }

        }
        rateInfo.put("serviceRate", serviceRate);
        rateInfo.put("interestRate", interestRate);
        rateInfo.put("totalRate", totalRate);
        return rateInfo;
    }

    private BigDecimal getGoodsAmount(BigDecimal borrowAmount, String borrowType,Long userId,String appName,String ipAddress) {

        BigDecimal oriRate = BigDecimal.ZERO;
        BigDecimal borrowDay = new BigDecimal(borrowType);
        // 查询新利率配置
        AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
                Constants.BORROW_CASH_INFO_LEGAL_NEW);
        BigDecimal newRate = null;
        JSONObject params = new JSONObject();
        params.put("ipAddress", ipAddress);
        params.put("appName",appName);
        oriRate = riskUtil.getRiskOriRate(userId,params,borrowType);
        double newServiceRate = 0;
        double newInterestRate = 0;
        if (rateInfoDo != null) {
            String borrowRate = rateInfoDo.getValue2();
            Map<String, Object> rateInfo = getRateInfo(borrowRate, borrowType, "borrow");
            newServiceRate = (double) rateInfo.get("serviceRate");
            newInterestRate = (double) rateInfo.get("interestRate");
            double totalRate = (double) rateInfo.get("totalRate");
            newRate = BigDecimal.valueOf(totalRate / 100);
        } else {
            newRate = BigDecimal.valueOf(0.36);
        }


        newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        logger.info("newRate = > {}, borrowAmount = > {}", newRate, borrowAmount);
        logger.info("borrowDay = > {}，oriRate = > {}", borrowDay, oriRate);
        BigDecimal profitAmount = oriRate.subtract(newRate).multiply(borrowAmount).multiply(borrowDay);
        if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            profitAmount = BigDecimal.ZERO;
        }
        logger.info("GetBorrowCashGoodInfoV2Api profitAmount =>{}", profitAmount);
        List<Long> allGoodsId = afBorrowLegalGoodsService.getGoodsIdByProfitAmoutForV2(profitAmount);
        for (Long goodsId : allGoodsId) {
            // 应还金额 = 借款金额 + 手续费 + 利息
            AfGoodsDo goodsInfo = afGoodsService.getGoodsById(goodsId);
            if (goodsInfo != null && goodsInfo.getSaleAmount() != null) {
                return goodsInfo.getSaleAmount();
            }
        }
        return null;
    }

}
