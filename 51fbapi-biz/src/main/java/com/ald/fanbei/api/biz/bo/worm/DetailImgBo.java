package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;

/**
 * @ClassName DetailImgBo.
 * @desc
 * 第三方自建商城商品详情图片BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 1:13
 */
public class DetailImgBo implements Serializable {
    private static final long serialVersionUID = 2140517581661616619L;

    private String detailImg;   //详情图片地址

    private String width;       //详情图片宽度

    private String height;      //详情图片高度

    public String getDetailImg() {
        return detailImg;
    }

    public void setDetailImg(String detailImg) {
        this.detailImg = detailImg;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
