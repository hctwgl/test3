/**
 * 
 */
package com.ald.fanbei.api.biz.rule.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.dal.dao.AfActivityGoodsDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;

/**
 * @类描述：
 * @author 江荣波 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afActivityGoodsService")
public class AfActivityGoodsServiceImpl  implements AfActivityGoodsService {

	@Resource
	AfActivityGoodsDao afActivityGoodsDao;

	@Override
	public List<AfActivityGoodsDto> listActivityGoodsByActivityId(
			Long activityId) {
		return afActivityGoodsDao.listActivityGoodsByActivityId(activityId);
	}

	@Override
	public List<AfGoodsDo> listRecommendGoodsByActivityId(Long activityId) {
		return afActivityGoodsDao.listRecommendGoodsByActivityId(activityId);
	}
	
	
	
}
