package com.ald.fanbei.api.biz.service.worm;

import com.ald.fanbei.api.biz.bo.worm.RateBo;
import com.ald.fanbei.api.biz.bo.worm.ThirdGoodsBo;

/**
 * @ClassName AfWormGoodsService.
 * @desc
 * 第三方自建商城爬虫数据处理类
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/13 11:17
 */

public interface AfWormGoodsService {


    public RateBo getWormGoodsRate(String url,String source);

    public ThirdGoodsBo getWormGoodsGoodsInfo();


}
