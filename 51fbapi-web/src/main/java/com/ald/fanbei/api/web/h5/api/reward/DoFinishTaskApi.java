package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfTaskService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.enums.TaskSecType;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 去完成任务
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("doFinishTaskApi")
public class DoFinishTaskApi implements H5Handle {


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        String taskType = ObjectUtils.toString(context.getData("taskType"),null);
        String taskSecType = ObjectUtils.toString(context.getData("taskSecType"),null);
        if(StringUtil.equals(taskType, TaskType.browse.getCode()) || StringUtil.equals(taskType, TaskType.shopping.getCode()) ){
            if(StringUtil.equals(taskSecType, TaskSecType.brand.getCode())){

            }else if(StringUtil.equals(taskSecType, TaskSecType.category.getCode())){

            }
        }


        return resp;
    }



}
