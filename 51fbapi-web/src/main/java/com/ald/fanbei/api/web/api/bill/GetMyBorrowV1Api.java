package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author yuyue
 * @ClassName: GetMyBorrowV1Api
 * @Description: 用户获取账单主页面的api——账单二期
 * @date 2017年11月13日 上午10:51:12
 */
@Component("getMyBorrowV1Api")
public class GetMyBorrowV1Api implements ApiHandle {

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        try {
            Long userId = context.getUserId();
            if (userId == null) {
                logger.info("getMyBorrowV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
                return resp;
            }
            AfUserDo afUserDo = afUserService.getUserById(userId);
            if (afUserDo == null || afUserDo.getRid() == null) {
                logger.info("getMyBorrowV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                return resp;
            }
            Map<String, Object> map = new HashMap<String, Object>();

            AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(userId);
            //加入临时额度
            AfInterimAuDo afInterimAuDo = afBorrowBillService.selectInterimAmountByUserId(userId);
            BigDecimal interimAmount = new BigDecimal(0);
            BigDecimal usableAmount = new BigDecimal(0);
            if (afInterimAuDo != null) {
                interimAmount = afInterimAuDo.getInterimAmount();
                usableAmount = interimAmount.subtract(afInterimAuDo.getInterimUsed());
                map.put("interimType", 1);//已获取临时额度
                map.put("interimAmount", afInterimAuDo.getInterimAmount());//临时额度
                map.put("interimUsed", afInterimAuDo.getInterimUsed());//已使用的额度
                int failureStatus = 0;//0未失效,1失效
                if (afInterimAuDo.getGmtFailuretime().getTime() < new Date().getTime()) {
                    failureStatus = 1;
                    interimAmount = new BigDecimal(0);
                    usableAmount = new BigDecimal(0);
                }
                map.put("failureStatus", failureStatus);
            } else {
                map.put("interimType", 0);//未获取临时额度
            }

            //加入漂浮窗信息
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndValue("SUSPENSION_FRAME_SETTING", "0");
            if (afResourceDo != null) {
                map.put("floatType", 1);//开启悬浮窗
                map.put("name", afResourceDo.getName());
                map.put("pic1", afResourceDo.getPic1());
                map.put("pic2", afResourceDo.getPic2());
            } else {
                map.put("floatType", 0);//未开启悬浮窗
            }
            if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.YES.getCode())) {
                // 获取用户额度
                AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
                if (userAccount == null || userAccount.getRid() == null) {
                    logger.error("getMyBorrowV1Api error ; userAccount is null and userId = " + userId);
                    resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                    return resp;
                }
                // 信用额度
                BigDecimal auAmount = userAccount.getAuAmount();
                // 可用额度
                BigDecimal amount = BigDecimalUtil.subtract(auAmount, userAccount.getUsedAmount());
                // 获取逾期账单月数量
                int overduedMonth = afBorrowBillService.getOverduedMonthByUserId(userId);
                AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
                query.setUserId(userId);
                int billCount = afBorrowBillService.countBillByQuery(query);
                if (billCount < 1) {
                    map.put("status", "noBill");
                } else {
                    map.put("status", "bill");
                }
                // 已出账单
                query.setIsOut(1);
                query.setStatus(BorrowBillStatus.NO.getCode());
                BigDecimal outMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
                // 未出账单
                query.setIsOut(0);
                BigDecimal notOutMoeny = afBorrowBillService.getUserBillMoneyByQuery(query);
                map.put("lastPayDay", null);
                if (outMoney.compareTo(new BigDecimal(0)) == 1) {
                    if (overduedMonth < 1) {
                        Date lastPayDay = afBorrowBillService.getLastPayDayByUserId(userId);
                        map.put("lastPayDay", DateUtil.formatMonthAndDay(lastPayDay));
                    }
                }


                map.put("auAmount", auAmount.add(interimAmount));
                map.put("amount", amount.add(usableAmount));
                map.put("overduedMonth", overduedMonth);
                map.put("outMoney", outMoney);
                map.put("notOutMoeny", notOutMoeny);
            }
            resp.setResponseData(map);
        } catch (Exception e) {
            logger.error("getMyBorrowV1Api error :", e);
            resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
            return resp;
        }
        return resp;
    }

}
