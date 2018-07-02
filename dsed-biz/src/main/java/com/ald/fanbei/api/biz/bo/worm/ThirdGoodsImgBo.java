package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;

/**
 * @ClassName ThirdGoodsImgBo.
 * @desc
 * 第三方自建商城商品主图BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 1:29
 */
public class ThirdGoodsImgBo implements Serializable {
    private static final long serialVersionUID = 8740939717128907573L;

    private String goodsMainImg;    //产品主图

    private String goodsDeputyImg;  //产品主图缩略图

    public String getGoodsMainImg() {
        return goodsMainImg;
    }

    public void setGoodsMainImg(String goodsMainImg) {
        this.goodsMainImg = goodsMainImg;
    }

    public String getGoodsDeputyImg() {
        return goodsDeputyImg;
    }

    public void setGoodsDeputyImg(String goodsDeputyImg) {
        this.goodsDeputyImg = goodsDeputyImg;
    }
}
