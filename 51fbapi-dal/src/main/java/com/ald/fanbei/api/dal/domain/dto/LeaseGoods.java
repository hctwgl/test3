package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;

import java.util.List;

/**
 * @author zhourui on 2018年03月05日 13:45
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class LeaseGoods extends AfGoodsDo {
    private List<AfGoodsPriceDo> goodsPrice;

    public List<AfGoodsPriceDo> getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(List<AfGoodsPriceDo> goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
}
