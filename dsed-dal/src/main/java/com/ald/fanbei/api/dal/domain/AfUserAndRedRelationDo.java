package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 
 * @类描述: 颜值测试红包和用户关系实体类
 * @author :liutengyuan
 * @version :2018年3月14日 上午11:22:43 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserAndRedRelationDo extends AbstractSerial{

	private static final long serialVersionUID = 1L;
	private Long userId;
	private Long redId;
	
	public AfUserAndRedRelationDo(Long userId, Long redId) {
		super();
		this.userId = userId;
		this.redId = redId;
	}
	
	public AfUserAndRedRelationDo() {
		super();
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRedId() {
		return redId;
	}
	public void setRedId(Long redId) {
		this.redId = redId;
	}
	
}
