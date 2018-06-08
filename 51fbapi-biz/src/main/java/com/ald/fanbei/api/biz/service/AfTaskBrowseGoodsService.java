package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;

/**
 * 每日浏览商品数量的任务Service
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 20:02:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskBrowseGoodsService{
    AfTaskBrowseGoodsDo isExisted(Long userId, Long goodsId);

    int countBrowseGoodsToday(Long userId);

    int addBrowseGoodsTaskUser(Long userId, Long goodsId);

    AfTaskUserDo addBrowseGoodsTaskUserRecord(Long userId, Long goodsId);
}
