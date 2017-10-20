package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.worm.RateBo;
import com.ald.fanbei.api.biz.bo.worm.ThirdGoodsBo;
import com.ald.fanbei.api.biz.service.worm.AfWormGoodsService;
import com.ald.fanbei.api.common.enums.GoodsSourceEnum;
import org.springframework.stereotype.Service;

/**
 * @ClassName AfWormGoodsServiceImpl.
 * @desc
 * 第三方自建商城爬虫数据处理实现类
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/17 14:43
 */
@Service("afWormGoodsService")
public class AfWormGoodsServiceImpl implements AfWormGoodsService {

    @Override
    public RateBo getWormGoodsRate(String goodsId,String source) {

        if(GoodsSourceEnum.GOODS_SOURCE_JD.getCode().equals(source)){
            String jdUrl = "https://sclub.jd.com/comment/productPageComments.action?productId=1373345235&score=0&sortType=3&page=1&pageSize=10&callback=fetchJSON_comment98vv37464";
        }
        return null;
    }

    @Override
    public ThirdGoodsBo getWormGoodsGoodsInfo() {
        return null;
    }
}
