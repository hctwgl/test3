package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;


/**
 * @类描述：
 * @author 江荣波  2017年6月20日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityGoodsDao {
	
	//小于371
	List<AfActivityGoodsDto> listActivityGoodsByActivityIdLT371(@Param("activityId")Long activityId);

	List<AfActivityGoodsDto> listActivityGoodsByActivityId(@Param("activityId")Long activityId);

	//小于371
	List<AfGoodsDo> listRecommendGoodsByActivityIdLT371(@Param("activityId")Long activityId);
	
	List<AfGoodsDo> listRecommendGoodsByActivityId(@Param("activityId")Long activityId);
	
	// 根据goodsId查询商品信息
	AfActivityGoodsDo  getActivityGoodsByGoodsId(@Param("goodsId") Long goodsId);

	

}
