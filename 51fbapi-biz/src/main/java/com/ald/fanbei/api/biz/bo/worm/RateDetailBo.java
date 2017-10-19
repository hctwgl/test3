package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;

/**
 * @ClassName RateDetailBo.
 * @desc
 * 第三方自建商品评论详情BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 0:47
 */
public class RateDetailBo implements Serializable {
    private static final long serialVersionUID = 8296617140990439162L;

    private String picUrl;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
