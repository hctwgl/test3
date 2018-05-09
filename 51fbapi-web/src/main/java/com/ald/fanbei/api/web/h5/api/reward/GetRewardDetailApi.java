package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfSignRewardService;
import com.ald.fanbei.api.biz.service.AfSignRewardWithdrawService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 展示签到明细
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getRewardDetailApi")
public class GetRewardDetailApi implements H5Handle {

    @Resource
    AfSignRewardService afSignRewardService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Integer pageNo = NumberUtil.objToPageIntDefault(context.getData("pageNo").toString(), 1);
        Integer pageSize = NumberUtil.objToPageIntDefault(context.getData("pageSize").toString(), 5);
        AfSignRewardQuery signQuery = new AfSignRewardQuery();
        signQuery.setUserId(context.getUserId());
        signQuery.setPageNo(pageNo);
        signQuery.setPageSize(pageSize);
        List<AfSignRewardDo> signList = afSignRewardService.getRewardDetailList(signQuery);
        resp.addResponseData("signList",signList);
        return resp;
    }
}
