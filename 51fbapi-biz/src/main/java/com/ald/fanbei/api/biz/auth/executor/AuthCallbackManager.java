package com.ald.fanbei.api.biz.auth.executor;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.AuthType;
import com.google.common.collect.Maps;

/**
 * 认证回调管理类
 * 
 * @author rongbo
 *
 */
@Component("authCallbackManager")
public class AuthCallbackManager implements Executor, ApplicationContextAware {

    private Map<String, Executor> executors = Maps.newConcurrentMap();

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
	register(AuthType.ALIPAY.getCode(), getExecutor("alipayAuthCallbackExecutor"));
	register(AuthType.BANK.getCode(), getExecutor("bankAuthCallbackExecutor"));
	register(AuthType.CARDEMAIL.getCode(), getExecutor("cardEmailAuthCallbackExecutor"));
	register(AuthType.FUND.getCode(), getExecutor("fundAuthCallbackExecutor"));
	register(AuthType.INSURANCE.getCode(), getExecutor("insuranceAuthCallbackExecutor"));
	register(AuthType.ZHENGXIN.getCode(), getExecutor("zhengxinAuthCallbackExecutor"));
    register(AuthType.BUBBLE.getCode(), getExecutor("bubbleAuthCallbackExecutor"));
    }

    @Override
    public void execute(AuthCallbackBo authCallbackBo) {
	String authItem = authCallbackBo.getAuthItem();
	Executor executor = this.lookup(authItem);

	executor.execute(authCallbackBo);
    }

    private Executor getExecutor(String excutorName) {
	return (Executor) applicationContext.getBean(excutorName);
    }

    public Map<String, Executor> getExecutors() {
	return executors;
    }

    public void register(String name, Executor executor) {
	executors.put(name, executor);
    }

    public void unregister(String name) {
	executors.remove(name);
    }

    public Executor lookup(String name) {
	return executors.get(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	this.applicationContext = applicationContext;
    }
}
