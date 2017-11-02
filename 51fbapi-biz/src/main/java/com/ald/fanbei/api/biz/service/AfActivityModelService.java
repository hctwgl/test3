/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfActivityModelDo;


/**
 * @类描述：
 * @author 江荣波 2017年6月21日下午4:44:33
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityModelService {

	int deleteActivityGoodsByActivityId(Long activityId);

	int addActivityModel(AfActivityModelDo activityModel);

	List<AfActivityModelDo> getActivityModelByGoodsId(Long goodsId);

}
