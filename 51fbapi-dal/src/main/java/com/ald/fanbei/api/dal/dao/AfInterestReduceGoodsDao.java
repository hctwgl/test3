package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfInterestReduceGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceRulesDo;

/**
 * 降息Dao
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-03-29 13:41:22
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfInterestReduceGoodsDao extends BaseDao<AfInterestReduceGoodsDo, Long> {

	AfInterestReduceRulesDo checkIfReduce(@Param("ruleType")int i, @Param("goodsId")Long goodsId);

    

}
