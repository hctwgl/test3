package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.sun.jdi.PrimitiveType;

/**  
 * @Title: AfRebateDo.java
 * @Package com.ald.fanbei.api.dal.domain
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年11月17日 下午2:37:03
 * @version V1.0  
 */
public class AfRebateDo{

	private String consumeScene;
	private String consumeTime;
	private BigDecimal sceneRebate;
	private BigDecimal surpriseRebate;
	
	

	public String getConsumeTime() {
		return consumeTime;
	}
	public void setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
	}
	public String getConsumeScene() {
		return consumeScene;
	}
	public void setConsumeScene(String consumeScene) {
		this.consumeScene = consumeScene;
	}
	
	public BigDecimal getSceneRebate() {
		return sceneRebate;
	}
	
	public void setSceneRebate(BigDecimal sceneRebate) {
		this.sceneRebate = sceneRebate;
	}
	public BigDecimal getSurpriseRebate() {
		return surpriseRebate;
	}
	public void setSurpriseRebate(BigDecimal surpriseRebate) {
		this.surpriseRebate = surpriseRebate;
	}
	@Override
	public String toString() {
		return "AfRebateDo [consumeScene=" + consumeScene + ", consumeTime=" + consumeTime + ", sceneRebate="
				+ sceneRebate + ", surpriseRebate=" + surpriseRebate + "]";
	}
	
	
	
	
}
