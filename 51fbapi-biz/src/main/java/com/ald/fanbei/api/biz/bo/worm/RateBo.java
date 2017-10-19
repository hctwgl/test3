package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RateBo.
 * @desc
 * 第三方自建商品评论BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/13 0:29
 */
public class RateBo implements Serializable {

    private static final long serialVersionUID = -7480903407382175651L;

    private String content; //评论内容

    private String userName;//用户名

    private String type;    //评论类型	0 普通评论 1 图文评论 2 追评评论

    private String reply;    //店主评论内容

    private String rateTime;  //评论时间

    private String auctionSku;    //评论商品规格参数

    private List<RateDetailBo> rateDetail;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getRateTime() {
        return rateTime;
    }

    public void setRateTime(String rateTime) {
        this.rateTime = rateTime;
    }

    public String getAuctionSku() {
        return auctionSku;
    }

    public void setAuctionSku(String auctionSku) {
        this.auctionSku = auctionSku;
    }

    public List<RateDetailBo> getRateDetail() {
        return rateDetail;
    }

    public void setRateDetail(List<RateDetailBo> rateDetail) {
        this.rateDetail = rateDetail;
    }
}

