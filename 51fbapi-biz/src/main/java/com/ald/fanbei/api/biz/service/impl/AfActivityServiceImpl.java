/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.dal.dao.AfActivityDao;
import com.ald.fanbei.api.dal.domain.AfActivityDo;

/**
 * @类描述：
 * @author 江荣波 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afActivityService")
public class AfActivityServiceImpl  implements AfActivityService {

	@Resource
	AfActivityDao afActivityDao;
	
	@Override
	public int addActivity(AfActivityDo activityDo) {
		return afActivityDao.addActivity(activityDo);
	}

	@Override
	public AfActivityDo getActivityById(Long activityId) {
		return afActivityDao.getActivityById(activityId);
	}

	@Override
	public List<AfActivityDo> listAllActivity() {
		return afActivityDao.listAllActivity();
	}

	@Override
	public int deleteActivityById(Long activityId) {
		return afActivityDao.deleteActivityById(activityId);
	}

	@Override
	public int updateActivity(AfActivityDo activityDo) {
		return afActivityDao.updateActivity(activityDo);
	}
	
	
}
