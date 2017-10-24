package com.ald.fanbei.api.biz.service.de;

import java.util.List;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;

/**
 * 双十一砍价Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfDeUserCutInfoService extends ParentService<AfDeUserCutInfoDo, Long>{

    List<AfDeUserCutInfoDo> getAfDeUserCutInfoList(AfDeUserCutInfoQuery queryCutInfo);


}
