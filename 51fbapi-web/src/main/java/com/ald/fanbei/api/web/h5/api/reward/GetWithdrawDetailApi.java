package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfSignRewardWithdrawService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfSignRewardWithdrawDo;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardWithdrawQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 展示提现明细
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getWithdrawDetailApi")
public class GetWithdrawDetailApi implements H5Handle {

    @Resource
    AfSignRewardWithdrawService afSignRewardWithdrawService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Integer pageNo = NumberUtil.objToPageIntDefault(context.getData("pageNo").toString(), 1);
        Integer pageSize = NumberUtil.objToPageIntDefault(context.getData("pageSize").toString(), 5);
        AfSignRewardWithdrawQuery withdrawQuery = new AfSignRewardWithdrawQuery();
        withdrawQuery.setUserId(context.getUserId());
        withdrawQuery.setPageNo(pageNo);
        withdrawQuery.setPageSize(pageSize);
        List<AfSignRewardWithdrawDo> withdrawList = afSignRewardWithdrawService.getWithdrawList(withdrawQuery);
        resp.addResponseData("withdrawList",withdrawList);
        return resp;
    }
}
