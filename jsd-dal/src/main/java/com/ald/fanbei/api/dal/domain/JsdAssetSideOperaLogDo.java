package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 15:12:35
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdAssetSideOperaLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 资产方id
     */
    private Long assetSideId;

    /**
     * 操作时间
     */
    private Date gmtCreate;

    /**
     * 操作类型(getasset:获取债权资产  giveback:退回债权资产 pushpackage:推送资产包 changepackage:变更资产债权 )
     */
    private String changeType;

    /**
     * 操作涉及金额,默认为0
     */
    private BigDecimal amount;

    /**
     * 关联的资产包记录id集合,多个以英文,区分
     */
    private String refPackageId;

    /**
     * 关联资产包与债权明细记录id集合,多个以英文,区分
     */
    private String refDetailIds;

    /**
     * 备注说明
     */
    private String remarks;


    public JsdAssetSideOperaLogDo(Long assetSideId, Date gmtCreate,
                                 String changeType, BigDecimal amount, String refPackageId,
                                 String refDetailIds, String remarks) {
        super();
        this.assetSideId = assetSideId;
        this.gmtCreate = gmtCreate;
        this.changeType = changeType;
        this.amount = amount;
        this.refPackageId = refPackageId;
        this.refDetailIds = refDetailIds;
        this.remarks = remarks;
    }

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
     * 获取资产方id
     *
     * @return 资产方id
     */
    public Long getAssetSideId(){
      return assetSideId;
    }

    /**
     * 设置资产方id
     * 
     * @param assetSideId 要设置的资产方id
     */
    public void setAssetSideId(Long assetSideId){
      this.assetSideId = assetSideId;
    }

    /**
     * 获取操作时间
     *
     * @return 操作时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置操作时间
     * 
     * @param gmtCreate 要设置的操作时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取操作类型(getasset:获取债权资产  giveback:退回债权资产 pushpackage:推送资产包 changepackage:变更资产债权 )
     *
     * @return 操作类型(getasset:获取债权资产  giveback:退回债权资产 pushpackage:推送资产包 changepackage:变更资产债权 )
     */
    public String getChangeType(){
      return changeType;
    }

    /**
     * 设置操作类型(getasset:获取债权资产  giveback:退回债权资产 pushpackage:推送资产包 changepackage:变更资产债权 )
     * 
     * @param changeType 要设置的操作类型(getasset:获取债权资产  giveback:退回债权资产 pushpackage:推送资产包 changepackage:变更资产债权 )
     */
    public void setChangeType(String changeType){
      this.changeType = changeType;
    }

    /**
     * 获取操作涉及金额,默认为0
     *
     * @return 操作涉及金额,默认为0
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置操作涉及金额,默认为0
     * 
     * @param amount 要设置的操作涉及金额,默认为0
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取关联的资产包记录id集合,多个以英文,区分
     *
     * @return 关联的资产包记录id集合,多个以英文,区分
     */
    public String getRefPackageId(){
      return refPackageId;
    }

    /**
     * 设置关联的资产包记录id集合,多个以英文,区分
     * 
     * @param refPackageId 要设置的关联的资产包记录id集合,多个以英文,区分
     */
    public void setRefPackageId(String refPackageId){
      this.refPackageId = refPackageId;
    }

    /**
     * 获取关联资产包与债权明细记录id集合,多个以英文,区分
     *
     * @return 关联资产包与债权明细记录id集合,多个以英文,区分
     */
    public String getRefDetailIds(){
      return refDetailIds;
    }

    /**
     * 设置关联资产包与债权明细记录id集合,多个以英文,区分
     * 
     * @param refDetailIds 要设置的关联资产包与债权明细记录id集合,多个以英文,区分
     */
    public void setRefDetailIds(String refDetailIds){
      this.refDetailIds = refDetailIds;
    }

    /**
     * 获取备注说明
     *
     * @return 备注说明
     */
    public String getRemarks(){
      return remarks;
    }

    /**
     * 设置备注说明
     * 
     * @param remarks 要设置的备注说明
     */
    public void setRemarks(String remarks){
      this.remarks = remarks;
    }

}