package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.LeaseGoods;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhourui on 2018年03月08日 15:47
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("leaseOrderApi")
public class LeaseOrderApi implements ApiHandle {
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfGoodsPriceService afGoodsPriceService;

    @Resource
    AfOrderService afOrderService;

    @Resource
    AfUserAddressService afUserAddressService;

    @Resource
    GeneratorClusterNo generatorClusterNo;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        final Long userId = context.getUserId();
        final String userName = context.getUserName();
        //商品ID
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId"), ""),
                0l);
        //商品价格ID
        Long goodsPriceId = NumberUtil
                .objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsPriceId"), ""), 0l);
        //地址ID
        Long addressId = NumberUtil
                .objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("addressId"), ""), 0l);

        Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 12);

        String lc = ObjectUtils.toString(requestDataVo.getParams().get("lc"));//订单来源地址

        logger.info("add lease order 1,lc=" + lc);
        if(StringUtils.isBlank(lc)){
            lc = ObjectUtils.toString(request.getAttribute("lc"));
        }
        logger.info("add lease order 2,lc=" + lc);
        String key = Constants.CACHKEY_CUT_PRICE_LOCK + ":" + userId + ":" + goodsId;
        boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
        if(isNotLock) {
            HashMap checkResult = afOrderService.checkLeaseOrder(userId, goodsId);
            if (checkResult != null) {
                throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_LEASE_ERROR);
            }
            Date currTime = new Date();
            int order_pay_time_limit = Constants.ORDER_PAY_TIME_LIMIT;
            try {
                AfResourceDo resourceDo = afResourceService.getSingleResourceBytype("lease_order_pay_time_limit");
                if (resourceDo != null) {
                    order_pay_time_limit = Integer.valueOf(resourceDo.getValue());
                    if (order_pay_time_limit == 0) {
                        order_pay_time_limit = Constants.ORDER_PAY_TIME_LIMIT;
                    }
                }
            } catch (Exception e) {
                logger.error("resource config error:", e);
            }
            Date gmtPayEnd = DateUtil.addMins(currTime, order_pay_time_limit);

            final AfGoodsPriceDo priceDo = afGoodsPriceService.getById(goodsPriceId);
            final LeaseGoods goodsDo = afGoodsService.getLeaseGoodsByGoodsId(goodsId);
            if (goodsDo == null || priceDo == null) {
                throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
            }
            if (priceDo.getLeaseSale().equals("N")) {
                throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_CANCEL);
            }
            AfUserAddressDo addressDo = afUserAddressService.selectUserAddressByrid(addressId);
            if (addressDo == null) {
                throw new FanbeiException(FanbeiExceptionCode.USER_ADDRESS_NOT_EXIST);
            }
            BigDecimal richieAmount = new BigDecimal(0);
            BigDecimal recoverRate = new BigDecimal(0);
            BigDecimal monthlyRent = new BigDecimal(0);
            JSONArray leaseParamArray = JSON.parseArray(priceDo.getLeaseParam());
            for (int i = 0; i < leaseParamArray.size(); i++) {
                JSONObject obj = leaseParamArray.getJSONObject(i);
                if (obj.getInteger("nper") == nper) {
                    richieAmount = obj.getBigDecimal("richieAmount");
                    recoverRate = obj.getBigDecimal("recoverRate");
                    monthlyRent = obj.getBigDecimal("monthlyRent");
                }
            }
            if (monthlyRent.compareTo(new BigDecimal(0)) == 0) {
                throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_CANCEL);
            }
            final AfOrderDo afOrder = orderDoWithGoodsAndAddressDo(addressDo, goodsDo, priceDo);
            afOrder.setUserId(userId);
            afOrder.setGoodsPriceId(goodsPriceId);
            afOrder.setActualAmount(BigDecimal.ZERO);

            //新增下单时，记录ip和同盾设备指纹锁 cxk
            afOrder.setIp(CommonUtil.getIpAddr(request));//用户ip地址
            afOrder.setBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("blackBox")));//加入同盾设备指纹
            afOrder.setBqsBlackBox(ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox")));//加入白骑士设备指纹

            afOrder.setCount(1);
            afOrder.setNper(nper - 1);

            afOrder.setGmtCreate(currTime);
            afOrder.setGmtPayEnd(gmtPayEnd);
            afOrder.setLc(lc);

            afOrder.setGoodsPriceName(priceDo.getPropertyValueNames());

            // 保存手续费信息
            BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper, userName,afOrder.getGoodsId());
            afOrder.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));

            //下单时所有场景额度使用情况
            final List<AfOrderSceneAmountDo> listSceneAmount = new ArrayList<AfOrderSceneAmountDo>();
            //线上使用情况
            AfOrderSceneAmountDo onlineSceneAmount = new AfOrderSceneAmountDo();
            //培训使用情况
            AfOrderSceneAmountDo trainSceneAmount = new AfOrderSceneAmountDo();
            //获取所有场景额度
            List<AfUserAccountSenceDo> list = afUserAccountSenceService.getByUserId(userId);
            //当前场景额度
            AfUserAccountSenceDo afUserAccountSenceDo = null;

            for (AfUserAccountSenceDo item : list) {
                if (item.getScene().equals(UserAccountSceneType.ONLINE.getCode())) {
                    afUserAccountSenceDo = item;
                    onlineSceneAmount.setAuAmount(item.getAuAmount());
                    onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
                    onlineSceneAmount.setUsedAmount(item.getUsedAmount());
                    onlineSceneAmount.setUserId(userId);
                }
                if (item.getScene().equals(UserAccountSceneType.TRAIN.getCode())) {
                    trainSceneAmount.setAuAmount(item.getAuAmount());
                    trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
                    trainSceneAmount.setUsedAmount(item.getUsedAmount());
                    trainSceneAmount.setUserId(userId);
                }
            }
            if (afUserAccountSenceDo == null) {
                afUserAccountSenceDo = new AfUserAccountSenceDo();
                afUserAccountSenceDo.setAuAmount(new BigDecimal(0));
                afUserAccountSenceDo.setFreezeAmount(new BigDecimal(0));
                afUserAccountSenceDo.setUsedAmount(new BigDecimal(0));
            }
            BigDecimal useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount()).subtract(afUserAccountSenceDo.getFreezeAmount());
            afOrder.setAuAmount(afUserAccountSenceDo.getAuAmount());
            afOrder.setUsedAmount(afUserAccountSenceDo.getUsedAmount());

            //获取现金贷额度
            AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
            //现金贷使用情况
            AfOrderSceneAmountDo cashSceneAmount = new AfOrderSceneAmountDo();
            cashSceneAmount.setOrderId(afOrder.getRid());
            cashSceneAmount.setAuAmount(userAccountInfo.getAuAmount());
            cashSceneAmount.setScene(UserAccountSceneType.CASH.getCode());
            cashSceneAmount.setUsedAmount(userAccountInfo.getUsedAmount());
            cashSceneAmount.setUserId(userId);
            if (onlineSceneAmount.getUserId() == null) {
                onlineSceneAmount.setAuAmount(new BigDecimal(0));
                onlineSceneAmount.setScene(UserAccountSceneType.ONLINE.getCode());
                onlineSceneAmount.setUsedAmount(new BigDecimal(0));
                onlineSceneAmount.setUserId(userId);
            }
            if (trainSceneAmount.getUserId() == null) {
                trainSceneAmount.setAuAmount(new BigDecimal(0));
                trainSceneAmount.setScene(UserAccountSceneType.TRAIN.getCode());
                trainSceneAmount.setUsedAmount(new BigDecimal(0));
                trainSceneAmount.setUserId(userId);
            }
            onlineSceneAmount.setOrderId(afOrder.getRid());
            trainSceneAmount.setOrderId(afOrder.getRid());
            listSceneAmount.add(cashSceneAmount);
            listSceneAmount.add(onlineSceneAmount);
            listSceneAmount.add(trainSceneAmount);

            Map<String, Object> dataLeaseFreeze = new HashMap<String, Object>();
            dataLeaseFreeze.put("ipAddress", CommonUtil.getIpAddr(request));
            String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
            dataLeaseFreeze.put("appName", appName);
            dataLeaseFreeze.put("blackBox", afOrder.getBlackBox());
            JSONObject dataObj = afOrderService.getLeaseFreeze(dataLeaseFreeze, priceDo.getLeaseAmount(), userId);

            final AfOrderLeaseDo afOrderLeaseDo = new AfOrderLeaseDo();
            afOrderLeaseDo.setMonthlyRent(monthlyRent);
            afOrderLeaseDo.setRecoverRate(recoverRate);
            afOrderLeaseDo.setRichieAmount(richieAmount);
            afOrderLeaseDo.setRealName(userAccountInfo.getRealName());
            afOrderLeaseDo.setScore(dataObj.getInteger("score"));
            afOrderLeaseDo.setOrderNo(afOrder.getOrderNo());
            BigDecimal freezeAmount = dataObj.getBigDecimal("freezeAmount");
            // -1 只能现金支付（配置规则没匹配）
            if (dataObj.getInteger("freeze") == -1) {
                afOrder.setActualAmount(priceDo.getLeaseAmount().add(monthlyRent).add(richieAmount));
                afOrderLeaseDo.setFreezeAmount(priceDo.getLeaseAmount());
                afOrderLeaseDo.setCashDeposit(priceDo.getLeaseAmount());
                afOrderLeaseDo.setQuotaDeposit(BigDecimal.ZERO);
            } else {
                afOrderLeaseDo.setFreezeAmount(freezeAmount);
                afOrderLeaseDo.setCashDeposit(BigDecimal.ZERO);
                afOrderLeaseDo.setQuotaDeposit(BigDecimal.ZERO);
            }
            Integer result = transactionTemplate
                    .execute(new TransactionCallback<Integer>() {

                        @Override
                        public Integer doInTransaction(TransactionStatus transactionStatus) {
                            try {
                                afOrderService.createOrder(afOrder);
                                afOrderLeaseDo.setBuyout(goodsDo.getBuyout());
                                afOrderLeaseDo.setUserId(userId);
                                afOrderLeaseDo.setOrderId(afOrder.getRid());
                                afOrderLeaseDo.setUserName(userName);
                                afOrderService.addOrderLease(afOrderLeaseDo);
                                //添加下单时所有场景额度使用情况
                                afOrderService.addSceneAmount(listSceneAmount);

                                afGoodsPriceService.updateNewStockAndSaleByPriceId(afOrder.getGoodsPriceId(), 1, true);

                                afGoodsService.updateSelfSupportGoods(afOrder.getGoodsId(), 1);
                                return 1;
                            } catch (Exception e) {
                                logger.info("LeaseOrderApi error:" + e);
                                transactionStatus.setRollbackOnly();
                                throw e;
                            }
                        }
                    });

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("orderId", afOrder.getRid());
            String isEnoughAmount = "Y";
            if (useableAmount.compareTo(afOrderLeaseDo.getFreezeAmount()) < 0) {
                isEnoughAmount = "N";
            }
            data.put("isEnoughAmount", isEnoughAmount);
            resp.setResponseData(data);
            return resp;
        }
        else {
            throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_LEASE_ERROR);
        }
    }

    private AfOrderDo orderDoWithGoodsAndAddressDo(AfUserAddressDo addressDo, AfGoodsDo goodsDo,AfGoodsPriceDo priceDo) {
        AfOrderDo afOrder = new AfOrderDo();
        afOrder.setConsignee(addressDo.getConsignee());
        afOrder.setConsigneeMobile(addressDo.getMobile());
        afOrder.setSaleAmount(priceDo.getLeaseAmount());// TODO:售价改成从规格中取得。

        afOrder.setPriceAmount(priceDo.getLeaseAmount());
        afOrder.setGoodsIcon(goodsDo.getGoodsIcon());
        afOrder.setGoodsName(goodsDo.getName());

        //新增下单时记录 省、 市、 区 、详细地址 、IP 、设备指纹 2017年12月12日11:17:51 cxk
        String province = addressDo.getProvince() !=null?addressDo.getProvince():"";
        String city = addressDo.getCity() !=null?addressDo.getCity():"";
        String district = addressDo.getCounty() !=null?addressDo.getCounty():"";
        String address = addressDo.getAddress() !=null?addressDo.getAddress():"";
        afOrder.setProvince(province);//省
        afOrder.setCity(city);//市
        afOrder.setDistrict(district);//区
        afOrder.setAddress(address);//详细地址
        afOrder.setGoodsId(goodsDo.getRid());
        afOrder.setOpenId(goodsDo.getOpenId());
        afOrder.setNumId(goodsDo.getNumId());
        afOrder.setShopName(goodsDo.getShopName());
        afOrder.setRebateAmount(goodsDo.getRebateAmount().multiply(new BigDecimal(1)));
        afOrder.setMobile("");

        afOrder.setBankId(0L);
        afOrder.setOrderType(OrderType.LEASE.getCode());
        String orderNo = generatorClusterNo.getOrderNo(OrderType.LEASE);
        afOrder.setOrderNo(orderNo);
        return afOrder;
    }
}
