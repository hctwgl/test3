package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName ThirdGoodsBo.
 * @desc
 * 第三方自建商城商品BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 1:23
 */
public class ThirdGoodsBo implements Serializable {
    private static final long serialVersionUID = -3445488228597103538L;

    private String goodsName;   //商品名称

    private String saleCount;   //销量

    private String source;   //商品来源

    private BigDecimal rebateAmount;   //返利金额

    private BigDecimal monthAmout;   //月供金额

    private String freeNper;   //免息期数

    private BigDecimal saleAmount;   //商品销售价

    private BigDecimal realAmount;   //商品到手价

    private String businessName;    //商家名称

    private List<SpecificationBo> specificationList;   //规格参数

    private List<ThirdGoodsImgBo> goodsImgList;    //商品主图

    private List<DetailImgBo> detailImgList;    //商品详情图片

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public BigDecimal getMonthAmout() {
        return monthAmout;
    }

    public void setMonthAmout(BigDecimal monthAmout) {
        this.monthAmout = monthAmout;
    }

    public String getFreeNper() {
        return freeNper;
    }

    public void setFreeNper(String freeNper) {
        this.freeNper = freeNper;
    }

    public BigDecimal getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(BigDecimal saleAmount) {
        this.saleAmount = saleAmount;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<SpecificationBo> getSpecificationList() {
        return specificationList;
    }

    public void setSpecificationList(List<SpecificationBo> specificationList) {
        this.specificationList = specificationList;
    }

    public List<ThirdGoodsImgBo> getGoodsImgList() {
        return goodsImgList;
    }

    public void setGoodsImgList(List<ThirdGoodsImgBo> goodsImgList) {
        this.goodsImgList = goodsImgList;
    }

    public List<DetailImgBo> getDetailImgList() {
        return detailImgList;
    }

    public void setDetailImgList(List<DetailImgBo> detailImgList) {
        this.detailImgList = detailImgList;
    }
}
