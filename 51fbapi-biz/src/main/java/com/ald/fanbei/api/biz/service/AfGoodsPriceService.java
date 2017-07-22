package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:34:26 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsPriceService extends ParentService<AfGoodsPriceDo, Long> {
	int updateStockAndSaleByPriceId(Long priceId, boolean isSold);
}
