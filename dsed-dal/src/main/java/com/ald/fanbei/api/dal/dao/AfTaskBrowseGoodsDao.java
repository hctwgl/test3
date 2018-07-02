package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDo;
import org.apache.ibatis.annotations.Param;

/**
 * 每日浏览商品数量的任务Dao
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 20:02:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskBrowseGoodsDao{
    AfTaskBrowseGoodsDo isExisted(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    int countBrowseGoodsToday(@Param("userId") Long userId);

    int addBrowseGoodsTaskUser(AfTaskBrowseGoodsDo afTaskBrowseGoodsDo);

}
