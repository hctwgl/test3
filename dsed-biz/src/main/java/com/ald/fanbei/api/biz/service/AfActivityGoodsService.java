package com.ald.fanbei.api.biz.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;

/**
 * 
 * @类描述：类目service
 * @author 江荣波 2017年2月16日下午5:24:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityGoodsService {

	List<AfActivityGoodsDto> listActivityGoodsByActivityId(Long activityId);

	List<AfGoodsDo> listRecommendGoodsByActivityId(Long activityId);
	
	AfActivityGoodsDo getActivityGoodsByGoodsId(Long goodsId);

	List<AfEncoreGoodsDto> listNewEncoreGoodsByActivityId(Long activityId);

	List<AfEncoreGoodsDto> listHomeActivityGoodsByActivityId(Long activityId);

	AfActivityGoodsDo getActivityGoodsByGoodsIdAndType(Long goodsId);

	List<AfActivityGoodsDo> getActivityGoodsByGoodsIdAndTypeMap(List<AfEncoreGoodsDto> list);

	HashMap getVisualActivityGoodsByGoodsId(Long goodsId);
}