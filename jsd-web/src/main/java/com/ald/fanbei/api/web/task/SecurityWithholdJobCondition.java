package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SecurityWithholdJobCondition implements Condition {


    private static String SECURITY_WITHHOLD_TASK = ConfigProperties.get(Constants.CONFKEY_SECURITY_WITHHOLD_TASK);


    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        if (SECURITY_WITHHOLD_TASK != null) {
            return SECURITY_WITHHOLD_TASK.equals("on");
        } else {
            return false;
        }
    }
}