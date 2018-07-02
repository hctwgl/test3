package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfUserGoodsSmsDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @类描述：
 * @author chefeipeng  2017年11月20日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserGoodsSmsDao {

	AfUserGoodsSmsDo selectByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);

	int insertByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);

	AfUserGoodsSmsDo selectBookingByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);

}
