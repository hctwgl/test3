package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfActivityDo;


/**
 * 
 * @类描述：
 * @author 江荣波 2017年6月20日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityService {

	int addActivity(AfActivityDo activityDo);

	AfActivityDo getActivityById(Long activityId);

	List<AfActivityDo> listAllActivity();

	int deleteActivityById(Long activityId);

	int updateActivity(AfActivityDo activityDo);


}
