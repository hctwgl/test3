package com.ald.fanbei.api.biz.service.de;

import java.util.List;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;

/**
 * 双十一砍价Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:19 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfDeGoodsService extends ParentService<AfDeGoodsDo, Long> {
    List<UserDeGoods> getUserDeGoodsList(Long userId);
    long  getActivityEndTime();
    long  getActivityTotalCount();
    AfDeUserGoodsInfoDto getGoodsInfo(AfDeGoodsDo afDeGoodsDo);
    int getIniNum();
}
