package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfActivityModelDo;


/**
 * @类描述：
 * @author 江荣波 2017年3月21日下午5:35:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityModelDao {

	int deleteActivityGoodsByActivityId(Long activityId);

	int addActivityModel(AfActivityModelDo activityModel);
}
