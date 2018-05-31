package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSignRewardWithdrawService;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardWithdrawDo;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardDto;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardWithdrawDto;
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
    AfResourceService afResourceService;
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
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_PRIZE");
        List<AfSignRewardWithdrawDto> withdrawList = afSignRewardWithdrawService.getWithdrawList(withdrawQuery);
        for(AfSignRewardWithdrawDto afSignRewardWithdrawDto : withdrawList){
            switch (afSignRewardWithdrawDto.getWithdrawType()) {
                case 0:
                    afSignRewardWithdrawDto.setName("兑换"+ afResourceDo.getValue1() +"元优惠券");
                    continue;
                case 1:
                    afSignRewardWithdrawDto.setName("提现"+ afResourceDo.getValue2() +"元至返利账户");
                    continue;
                case 2:
                    afSignRewardWithdrawDto.setName("提现"+ afResourceDo.getValue3() +"元至返利账户");
                    continue;
                case 3:
                    afSignRewardWithdrawDto.setName("提现"+ afResourceDo.getValue4() +"元至返利账户");
                    continue;
            }
        }
        resp.addResponseData("withdrawList",withdrawList);
        return resp;
    }
}
