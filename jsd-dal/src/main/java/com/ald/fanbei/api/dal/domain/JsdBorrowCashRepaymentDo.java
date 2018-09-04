package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowCashRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 还款名称
     */
    private String name;

    /**
     * 还款编号 hkYYYYMMDDHHMMSSXXXX
     */
    private String repayNo;

    /**
     * 还款金额
     */
    private BigDecimal repaymentAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 借钱id
     */
    private Long borrowId;

    /**
     * 第三方的交易流水号
     */
    private String tradeNo;

    /**
     * 还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    private String status;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 卡号
     */
    private String cardNumber;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 失败码
     */
    private String failCode;

    /**
     * 
     */
    private String remark;


    /**
     * 失败原因
     */

    private String failMsg;

    /**
     * 还款类型
     */
    private String type;


    private String jsdRepayNo;


    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
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
     * 获取还款名称
     *
     * @return 还款名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置还款名称
     * 
     * @param name 要设置的还款名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取还款编号 hkYYYYMMDDHHMMSSXXXX
     *
     * @return 还款编号 hkYYYYMMDDHHMMSSXXXX
     */
    public String getRepayNo(){
      return repayNo;
    }

    /**
     * 设置还款编号 hkYYYYMMDDHHMMSSXXXX
     * 
     * @param repayNo 要设置的还款编号 hkYYYYMMDDHHMMSSXXXX
     */
    public void setRepayNo(String repayNo){
      this.repayNo = repayNo;
    }

    /**
     * 获取还款金额
     *
     * @return 还款金额
     */
    public BigDecimal getRepaymentAmount(){
      return repaymentAmount;
    }

    /**
     * 设置还款金额
     * 
     * @param repaymentAmount 要设置的还款金额
     */
    public void setRepaymentAmount(BigDecimal repaymentAmount){
      this.repaymentAmount = repaymentAmount;
    }

    /**
     * 获取实际支付金额
     *
     * @return 实际支付金额
     */
    public BigDecimal getActualAmount(){
      return actualAmount;
    }

    /**
     * 设置实际支付金额
     * 
     * @param actualAmount 要设置的实际支付金额
     */
    public void setActualAmount(BigDecimal actualAmount){
      this.actualAmount = actualAmount;
    }

    /**
     * 获取借钱id
     *
     * @return 借钱id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置借钱id
     * 
     * @param borrowId 要设置的借钱id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取第三方的交易流水号
     *
     * @return 第三方的交易流水号
     */
    public String getTradeNo(){
      return tradeNo;
    }

    /**
     * 设置第三方的交易流水号
     * 
     * @param tradeNo 要设置的第三方的交易流水号
     */
    public void setTradeNo(String tradeNo){
      this.tradeNo = tradeNo;
    }

    /**
     * 获取还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     *
     * @return 还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     * 
     * @param status 要设置的还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    public void setStatus(String status){
      this.status = status;
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
     * 获取卡号
     *
     * @return 卡号
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置卡号
     * 
     * @param cardNumber 要设置的卡号
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取卡名称
     *
     * @return 卡名称
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置卡名称
     * 
     * @param cardName 要设置的卡名称
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取失败码
     *
     * @return 失败码
     */
    public String getFailCode(){
      return failCode;
    }

    /**
     * 设置失败码
     * 
     * @param failCode 要设置的失败码
     */
    public void setFailCode(String failCode){
      this.failCode = failCode;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置
     * 
     * @param remark 要设置的
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取失败原因
     *
     * @return 失败原因
     */
    public String getFailMsg(){
      return failMsg;
    }

    /**
     * 设置失败原因
     * 
     * @param failMsg 要设置的失败原因
     */
    public void setFailMsg(String failMsg){
      this.failMsg = failMsg;
    }

    /**
     * 获取还款类型
     *
     * @return 还款类型
     */
    public String getType(){
      return type;
    }

    /**
     * 设置还款类型
     * 
     * @param type 要设置的还款类型
     */
    public void setType(String type){
      this.type = type;
    }

    public String getJsdRepayNo() {
        return jsdRepayNo;
    }

    public void setJsdRepayNo(String jsdRepayNo) {
        this.jsdRepayNo = jsdRepayNo;
    }



}