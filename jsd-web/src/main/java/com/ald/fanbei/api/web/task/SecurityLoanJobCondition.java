package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SecurityLoanJobCondition implements Condition {


    private static String SECURITY_OVERDUE_TASK = ConfigProperties.get(Constants.SECURITY_OVERDUE_TASK);


    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        if(SECURITY_OVERDUE_TASK!=null){
            return SECURITY_OVERDUE_TASK.equals("on");
        }else {
            return false;
        }
    }

}
