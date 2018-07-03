/**
 * 
 */
package com.ald.fanbei.api.biz.rule.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.dal.dao.AfActivityGoodsDao;
import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;

/**
 * @类描述：
 * @author 江荣波 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afActivityGoodsService")
public class AfActivityGoodsServiceImpl  implements AfActivityGoodsService {

	@Resource
	AfActivityGoodsDao afActivityGoodsDao;
	
	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public List<AfActivityGoodsDto> listActivityGoodsByActivityId(
			Long activityId) {
		return afActivityGoodsDao.listActivityGoodsByActivityId(activityId);
	}

	@Override
	public List<AfGoodsDo> listRecommendGoodsByActivityId(Long activityId) {
		return afActivityGoodsDao.listRecommendGoodsByActivityId(activityId);
	}

	@Override
	public AfActivityGoodsDo getActivityGoodsByGoodsId(Long goodsId) {
		
		return afActivityGoodsDao.getActivityGoodsByGoodsId(goodsId);
	}

	@Override
	public List<AfEncoreGoodsDto> listNewEncoreGoodsByActivityId(Long activityId) {
		return afActivityGoodsDao.listNewEncoreGoodsByActivityId(activityId);
		
	}

	@Override
	public List<AfEncoreGoodsDto> listHomeActivityGoodsByActivityId(Long activityId) {
		String cacheKey = "LIST_HOME_ACTIVITY_GOODS_BY_ACTIVITY_ID" + activityId;
		List<AfEncoreGoodsDto> goodsDtoList = bizCacheUtil.getObjectList(cacheKey);
		if(goodsDtoList == null) {
			goodsDtoList = afActivityGoodsDao.listHomeActivityGoodsByActivityId(activityId);
			bizCacheUtil.saveObjectList(cacheKey, goodsDtoList);
		}
		return goodsDtoList;
	}
	
	@Override
	public AfActivityGoodsDo getActivityGoodsByGoodsIdAndType(Long goodsId){
		return afActivityGoodsDao.getActivityGoodsByGoodsIdAndType(goodsId);
	}

	@Override
	public List<AfActivityGoodsDo> getActivityGoodsByGoodsIdAndTypeMap(List<AfEncoreGoodsDto> list){
		return afActivityGoodsDao.getActivityGoodsByGoodsIdAndTypeMap(list);
	}

	@Override
	public HashMap getVisualActivityGoodsByGoodsId(Long goodsId) {
		return afActivityGoodsDao.getVisualActivityGoodsByGoodsId(goodsId);
	}
}