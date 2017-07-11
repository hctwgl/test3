package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 售后申请实体
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-08 16:15:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAftersaleApplyDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 最新的售后申请时间
     */
    private Date gmtApply;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 售后申请编号
     */
    private String applyNo;

    /**
     * 状态【NEW:新建 CLOSE:用户关闭售后申请 NOTPASS：审核不通过 WAIT_GOODS_BACK:审核通过待回寄商品 GOODS_BACKIING：商品回寄中 WAIT_REFUND:等待退款 FINISH:退款完成】
     */
    private String status;

    /**
     * 后台审核说明
     */
    private String verifyRemark;

    /**
     * 用户申请退款原因
     */
    private String userReason;

    /**
     * 用户申请退款图片凭证
     */
    private String picVouchers;

    /**
     * 货品回寄地址
     */
    private String goodsBackAddress;

    /**
     * 回寄物流公司
     */
    private String logisticsCompany;

    /**
     * 回寄物流单号
     */
    private String logisticsNo;

    /**
     * 备注说明
     */
    private String remark;
    
    public AfAftersaleApplyDo() {
		super();
	}

	public AfAftersaleApplyDo(Date gmtApply, Long userId, Long orderId,
			String applyNo, String status, String userReason, String picVouchers) {
		super();
		this.gmtApply = gmtApply;
		this.userId = userId;
		this.orderId = orderId;
		this.applyNo = applyNo;
		this.status = status;
		this.userReason = userReason;
		this.picVouchers = picVouchers;
	}



	/**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
    }
    

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取最新的售后申请时间
     *
     * @return 最新的售后申请时间
     */
    public Date getGmtApply(){
      return gmtApply;
    }

    /**
     * 设置最新的售后申请时间
     * 
     * @param gmtApply 要设置的最新的售后申请时间
     */
    public void setGmtApply(Date gmtApply){
      this.gmtApply = gmtApply;
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取订单id
     *
     * @return 订单id
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单id
     * 
     * @param orderId 要设置的订单id
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

    /**
     * 获取售后申请编号
     *
     * @return 售后申请编号
     */
    public String getApplyNo(){
      return applyNo;
    }

    /**
     * 设置售后申请编号
     * 
     * @param applyNo 要设置的售后申请编号
     */
    public void setApplyNo(String applyNo){
      this.applyNo = applyNo;
    }

    /**
     * 获取状态【NEW:新建 CLOSE:用户关闭售后申请 NOTPASS：审核不通过 WAIT_GOODS_BACK:审核通过待回寄商品 GOODS_BACKIING：商品回寄中 WAIT_REFUND:等待退款 FINISH:退款完成】
     *
     * @return 状态【NEW:新建 CLOSE:用户关闭售后申请 NOTPASS：审核不通过 WAIT_GOODS_BACK:审核通过待回寄商品 GOODS_BACKIING：商品回寄中 WAIT_REFUND:等待退款 FINISH:退款完成】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【NEW:新建 CLOSE:用户关闭售后申请 NOTPASS：审核不通过 WAIT_GOODS_BACK:审核通过待回寄商品 GOODS_BACKIING：商品回寄中 WAIT_REFUND:等待退款 FINISH:退款完成】
     * 
     * @param status 要设置的状态【NEW:新建 CLOSE:用户关闭售后申请 NOTPASS：审核不通过 WAIT_GOODS_BACK:审核通过待回寄商品 GOODS_BACKIING：商品回寄中 WAIT_REFUND:等待退款 FINISH:退款完成】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取后台审核说明
     *
     * @return 后台审核说明
     */
    public String getVerifyRemark(){
      return verifyRemark;
    }

    /**
     * 设置后台审核说明
     * 
     * @param verifyRemark 要设置的后台审核说明
     */
    public void setVerifyRemark(String verifyRemark){
      this.verifyRemark = verifyRemark;
    }

    /**
     * 获取用户申请退款原因
     *
     * @return 用户申请退款原因
     */
    public String getUserReason(){
      return userReason;
    }

    /**
     * 设置用户申请退款原因
     * 
     * @param userReason 要设置的用户申请退款原因
     */
    public void setUserReason(String userReason){
      this.userReason = userReason;
    }

    /**
     * 获取用户申请退款图片凭证
     *
     * @return 用户申请退款图片凭证
     */
    public String getPicVouchers(){
      return picVouchers;
    }

    /**
     * 设置用户申请退款图片凭证
     * 
     * @param picVouchers 要设置的用户申请退款图片凭证
     */
    public void setPicVouchers(String picVouchers){
      this.picVouchers = picVouchers;
    }

    /**
     * 获取货品回寄地址
     *
     * @return 货品回寄地址
     */
    public String getGoodsBackAddress(){
      return goodsBackAddress;
    }

    /**
     * 设置货品回寄地址
     * 
     * @param goodsBackAddress 要设置的货品回寄地址
     */
    public void setGoodsBackAddress(String goodsBackAddress){
      this.goodsBackAddress = goodsBackAddress;
    }

    /**
     * 获取回寄物流公司
     *
     * @return 回寄物流公司
     */
    public String getLogisticsCompany(){
      return logisticsCompany;
    }

    /**
     * 设置回寄物流公司
     * 
     * @param logisticsCompany 要设置的回寄物流公司
     */
    public void setLogisticsCompany(String logisticsCompany){
      this.logisticsCompany = logisticsCompany;
    }

    /**
     * 获取回寄物流单号
     *
     * @return 回寄物流单号
     */
    public String getLogisticsNo(){
      return logisticsNo;
    }

    /**
     * 设置回寄物流单号
     * 
     * @param logisticsNo 要设置的回寄物流单号
     */
    public void setLogisticsNo(String logisticsNo){
      this.logisticsNo = logisticsNo;
    }

    /**
     * 获取备注说明
     *
     * @return 备注说明
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注说明
     * 
     * @param remark 要设置的备注说明
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

}