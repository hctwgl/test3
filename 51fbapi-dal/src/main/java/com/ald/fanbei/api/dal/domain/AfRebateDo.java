package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**  
 * @Title: AfRebateDo.java
 * @Package com.ald.fanbei.api.dal.domain
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年11月17日 下午2:37:03
 * @version V1.0  
 */
public class AfRebateDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 751687871525979238L;

	private String consumeScene;
	private Date consumeTime;
	private BigDecimal sceneRebate;
	private BigDecimal surpriseRebate;
	public String getConsumeScene() {
		return consumeScene;
	}
	public void setConsumeScene(String consumeScene) {
		this.consumeScene = consumeScene;
	}
	public Date getConsumeTime() {
		return consumeTime;
	}
	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
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
