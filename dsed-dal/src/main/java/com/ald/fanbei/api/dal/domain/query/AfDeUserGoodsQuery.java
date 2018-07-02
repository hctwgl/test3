package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;

/**
 * 双十一砍价实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfDeUserGoodsQuery extends Page<AfDeUserGoodsDo> {

    private static final long serialVersionUID = 1L;
    private long goodspriceid;
    public long getGoodspriceid() {
        return goodspriceid;
    }
    public void setGoodspriceid(long goodspriceid) {
        this.goodspriceid = goodspriceid;
    }
    
    

}