package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @类描述：返现金额 兑换成 满减券
 * @date 2018年3月2日上午11:33:38
 * @author weiqingeng
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("exchangeApi")
public class ExchangeApi implements ApiHandle {

    @Autowired
    private AfUserAccountService afUserAccountService;
    @Autowired
    private AfRecycleService afRecycleService;


    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long uid = context.getUserId();
        Integer amount = NumberUtil.objToInteger(requestDataVo.getParams().get("amount"));
        try {
            if (null == uid || null == amount) {
                throw new FanbeiException(FanbeiExceptionCode.FAILED);
            }
            if (amount < 50) {
                throw new FanbeiException("兑换金额不能小于50", true);
            }
            AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(uid);
            if (null == afUserAccountDo || (null != afUserAccountDo && afUserAccountDo.getRebateAmount().intValue() < 50)) {
                throw new FanbeiException("账户余额不足50元,无法兑换", true);
            }
            if (amount.compareTo(afUserAccountDo.getRebateAmount().intValue()) > 0) {
                throw new FanbeiException("账户余额小于兑换金额", true);
            }
            afRecycleService.addExchange(uid, amount, afUserAccountDo.getRebateAmount());
        } catch (Exception e) {
            logger.error("exchangeApi,error=",e);
            throw new FanbeiException(FanbeiExceptionCode.FAILED);
        }
        return resp;
    }
}
