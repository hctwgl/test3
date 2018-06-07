package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfSignRewardExtService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 签到提醒
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getSignRemindApi")
public class GetSignRemindApi implements H5Handle {

    @Resource
    AfSignRewardExtService afSignRewardExtService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Integer isOpenRemind = NumberUtil.objToIntDefault(context.getData("isOpenRemind"),null);
        AfSignRewardExtDo afSignRewardExtDo = new AfSignRewardExtDo();
        if(isOpenRemind != null){
            if(isOpenRemind == 1 || isOpenRemind == 0){
                afSignRewardExtDo.setGmtModified(new Date());
                afSignRewardExtDo.setUserId(context.getUserId());
                afSignRewardExtDo.setIsOpenRemind(isOpenRemind);
                if(afSignRewardExtService.updateSignRemind(afSignRewardExtDo) < 1){
                    resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SIGN_REMIND_FAIL);
                }
            }else{
                resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
            }
        }else{
            resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }

        return resp;
    }
}
