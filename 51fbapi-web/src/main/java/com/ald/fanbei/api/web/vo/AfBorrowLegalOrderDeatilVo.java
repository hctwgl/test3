package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午14:57:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowLegalOrderDeatilVo extends AbstractSerial{

	private static final long serialVersionUID = 1L;

	/**
	 * 主键Rid
	 */
	private Long rid;


	/**
	 *
	 */
	private Long userId;

	/**
	 * 对应借款附带订单id
	 */
	private Long borrowLegalOrderId;

	/**
	 * 借款流水号
	 */
	private String cashNo;

	/**
	 * 借款类型【SEVEN-7天，FOURTEEN-14天】
	 */
	private String type;


	/**
	 * 借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
	 */
	private String status;

	/**
	 *
	 */
	private String remark;

	/**
	 * 逾期天数
	 */
	private short overdueDay;

	/**
	 * 逾期状态：Y：逾期；N：未逾期
	 */
	private String overdueStatus;

	/**
	 * 逾期产生的逾期额
	 */
	private BigDecimal overdueAmount;

	/**
	 * 已还款金额
	 */
	private BigDecimal repaidAmount;

	/**
	 * 手续费
	 */
	private BigDecimal poundageAmount;

	/**
	 * 利息费
	 */
	private BigDecimal interestAmount;

	/**
	 * 手续费费率
	 */
	private BigDecimal poundageRate;

	/**
	 * 利率
	 */
	private BigDecimal interestRate;

	/**
	 *
	 */
	private Integer planRepayDays;

	/**
	 * 应还日期
	 */
	private Date gmtPlanRepay;

	/**
	 * 创建时间
	 */
	private Date gmtCreate;

	/**
	 * 关闭时间
	 */
	private Date gmtClose;

	/**
	 *
	 */
	private Date gmtModifed;

	/**
	 * 最近一次还款的时间
	 */
	private Date gmtLastRepayment;

	/**
	 * 借款结清时间
	 */
	private Date gmtFinish;

	/**
	 * 借款用途
	 */
	private String borrowRemark;

	/**
	 * 还款来源
	 */
	private String refundRemark;

	/**
	 * 原借款Id
	 */
	private Long borrowId;

	private Date gmtModified;

	/**
	 * '订单序列号'
	 */
	private String orderNo;

	/**
	 * 对应商品订单
	 */
	private Long goodsId;

	/**
	 * 订单价格
	 */
	private BigDecimal priceAmount;

	/**
	 *
	 */
	private Date gmtDeliver;

	/**
	 *
	 */
	private Date gmtConfirmReceived;

	/**
	 *
	 */
	private Date gmtClosed;

	/**
	 * 订单关闭原因
	 */
	private String closedDetail;

	/**
	 * 收货地址
	 */
	private String address;

	/**
	 * 商品名称
	 */
	private String goodsName;

	private String logisticsInfo;

	private String logisticsCompany;

	private String logisticsNo;

	private String deliveryUser;

	private String deliveryPhone;
	
	private String goodsIcon;

	private String borrowStatus;
	
	private BigDecimal userAmount;
	
	private BigDecimal returnAmount;
	
	private String existRepayingMoney;

	private String edition;

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getExistRepayingMoney() {
		return existRepayingMoney;
	}

	public void setExistRepayingMoney(String existRepayingMoney) {
		this.existRepayingMoney = existRepayingMoney;
	}

	public BigDecimal getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(BigDecimal returnAmount) {
		this.returnAmount = returnAmount;
	}

	public BigDecimal getUserAmount() {
		return userAmount;
	}

	public void setUserAmount(BigDecimal userAmount) {
		this.userAmount = userAmount;
	}

	public String getBorrowStatus() {
		return borrowStatus;
	}

	public void setBorrowStatus(String borrowStatus) {
		this.borrowStatus = borrowStatus;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public Date getGmtDeliver() {
		return gmtDeliver;
	}

	public void setGmtDeliver(Date gmtDeliver) {
		this.gmtDeliver = gmtDeliver;
	}

	public Date getGmtConfirmReceived() {
		return gmtConfirmReceived;
	}

	public void setGmtConfirmReceived(Date gmtConfirmReceived) {
		this.gmtConfirmReceived = gmtConfirmReceived;
	}

	public Date getGmtClosed() {
		return gmtClosed;
	}

	public void setGmtClosed(Date gmtClosed) {
		this.gmtClosed = gmtClosed;
	}

	public String getClosedDetail() {
		return closedDetail;
	}

	public void setClosedDetail(String closedDetail) {
		this.closedDetail = closedDetail;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getLogisticsInfo() {
		return logisticsInfo;
	}

	public void setLogisticsInfo(String logisticsInfo) {
		this.logisticsInfo = logisticsInfo;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

	public String getDeliveryUser() {
		return deliveryUser;
	}

	public void setDeliveryUser(String deliveryUser) {
		this.deliveryUser = deliveryUser;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
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
	 * 获取
	 *
	 * @return
	 */
	public Long getUserId(){
		return userId;
	}

	/**
	 * 设置
	 *
	 * @param userId 要设置的
	 */
	public void setUserId(Long userId){
		this.userId = userId;
	}

	/**
	 * 获取对应借款附带订单id
	 *
	 * @return 对应借款附带订单id
	 */
	public Long getBorrowLegalOrderId(){
		return borrowLegalOrderId;
	}

	/**
	 * 设置对应借款附带订单id
	 *
	 * @param borrowLegalOrderId 要设置的对应借款附带订单id
	 */
	public void setBorrowLegalOrderId(Long borrowLegalOrderId){
		this.borrowLegalOrderId = borrowLegalOrderId;
	}

	/**
	 * 获取借款流水号
	 *
	 * @return 借款流水号
	 */
	public String getCashNo(){
		return cashNo;
	}

	/**
	 * 设置借款流水号
	 *
	 * @param cashNo 要设置的借款流水号
	 */
	public void setCashNo(String cashNo){
		this.cashNo = cashNo;
	}

	/**
	 * 获取借款类型【SEVEN-7天，FOURTEEN-14天】
	 *
	 * @return 借款类型【SEVEN-7天，FOURTEEN-14天】
	 */
	public String getType(){
		return type;
	}

	/**
	 * 设置借款类型【SEVEN-7天，FOURTEEN-14天】
	 *
	 * @param type 要设置的借款类型【SEVEN-7天，FOURTEEN-14天】
	 */
	public void setType(String type){
		this.type = type;
	}

	

	/**
	 * 获取借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
	 *
	 * @return 借款状态【APPLYING:审核中，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
	 */
	public String getStatus(){
		return status;
	}

	/**
	 * 设置借款状态【APPLYING:审核中，AWAIT_POST:待入账，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
	 *
	 * @param status 要设置的借款状态【APPLYING:审核中，AWAIT_POST:待入账，AWAIT_REPAY:待还款,PART_REPAID: 部分还款, FINISHED:已经结清,CLOSED:借款关闭】
	 */
	public void setStatus(String status){
		this.status = status;
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
	 * 获取逾期天数
	 *
	 * @return 逾期天数
	 */
	public short getOverdueDay(){
		return overdueDay;
	}

	/**
	 * 设置逾期天数
	 *
	 * @param overdueDay 要设置的逾期天数
	 */
	public void setOverdueDay(short overdueDay){
		this.overdueDay = overdueDay;
	}

	/**
	 * 获取逾期状态：Y：逾期；N：未逾期
	 *
	 * @return 逾期状态：Y：逾期；N：未逾期
	 */
	public String getOverdueStatus(){
		return overdueStatus;
	}

	/**
	 * 设置逾期状态：Y：逾期；N：未逾期
	 *
	 * @param overdueStatus 要设置的逾期状态：Y：逾期；N：未逾期
	 */
	public void setOverdueStatus(String overdueStatus){
		this.overdueStatus = overdueStatus;
	}

	/**
	 * 获取逾期产生的逾期额
	 *
	 * @return 逾期产生的逾期额
	 */
	public BigDecimal getOverdueAmount(){
		return overdueAmount;
	}

	/**
	 * 设置逾期产生的逾期额
	 *
	 * @param overdueAmount 要设置的逾期产生的逾期额
	 */
	public void setOverdueAmount(BigDecimal overdueAmount){
		this.overdueAmount = overdueAmount;
	}

	/**
	 * 获取已还款金额
	 *
	 * @return 已还款金额
	 */
	public BigDecimal getRepaidAmount(){
		return repaidAmount;
	}

	/**
	 * 设置已还款金额
	 *
	 * @param repaidAmount 要设置的已还款金额
	 */
	public void setRepaidAmount(BigDecimal repaidAmount){
		this.repaidAmount = repaidAmount;
	}

	/**
	 * 获取手续费
	 *
	 * @return 手续费
	 */
	public BigDecimal getPoundageAmount(){
		return poundageAmount;
	}

	/**
	 * 设置手续费
	 *
	 * @param poundageAmount 要设置的手续费
	 */
	public void setPoundageAmount(BigDecimal poundageAmount){
		this.poundageAmount = poundageAmount;
	}

	/**
	 * 获取利息费
	 *
	 * @return 利息费
	 */
	public BigDecimal getInterestAmount(){
		return interestAmount;
	}

	/**
	 * 设置利息费
	 *
	 * @param interestAmount 要设置的利息费
	 */
	public void setInterestAmount(BigDecimal interestAmount){
		this.interestAmount = interestAmount;
	}

	/**
	 * 获取手续费费率
	 *
	 * @return 手续费费率
	 */
	public BigDecimal getPoundageRate(){
		return poundageRate;
	}

	/**
	 * 设置手续费费率
	 *
	 * @param poundageRate 要设置的手续费费率
	 */
	public void setPoundageRate(BigDecimal poundageRate){
		this.poundageRate = poundageRate;
	}

	/**
	 * 获取利率
	 *
	 * @return 利率
	 */
	public BigDecimal getInterestRate(){
		return interestRate;
	}

	/**
	 * 设置利率
	 *
	 * @param interestRate 要设置的利率
	 */
	public void setInterestRate(BigDecimal interestRate){
		this.interestRate = interestRate;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Integer getPlanRepayDays(){
		return planRepayDays;
	}

	/**
	 * 设置
	 *
	 * @param planRepayDays 要设置的
	 */
	public void setPlanRepayDays(Integer planRepayDays){
		this.planRepayDays = planRepayDays;
	}

	/**
	 * 获取应还日期
	 *
	 * @return 应还日期
	 */
	public Date getGmtPlanRepay(){
		return gmtPlanRepay;
	}

	/**
	 * 设置应还日期
	 *
	 * @param gmtPlanRepay 要设置的应还日期
	 */
	public void setGmtPlanRepay(Date gmtPlanRepay){
		this.gmtPlanRepay = gmtPlanRepay;
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
	 * 获取关闭时间
	 *
	 * @return 关闭时间
	 */
	public Date getGmtClose(){
		return gmtClose;
	}

	/**
	 * 设置关闭时间
	 *
	 * @param gmtClose 要设置的关闭时间
	 */
	public void setGmtClose(Date gmtClose){
		this.gmtClose = gmtClose;
	}

	/**
	 * 获取
	 *
	 * @return
	 */
	public Date getGmtModifed(){
		return gmtModifed;
	}

	/**
	 * 设置
	 *
	 * @param gmtModifed 要设置的
	 */
	public void setGmtModifed(Date gmtModifed){
		this.gmtModifed = gmtModifed;
	}

	/**
	 * 获取最近一次还款的时间
	 *
	 * @return 最近一次还款的时间
	 */
	public Date getGmtLastRepayment(){
		return gmtLastRepayment;
	}

	/**
	 * 设置最近一次还款的时间
	 *
	 * @param gmtLastRepayment 要设置的最近一次还款的时间
	 */
	public void setGmtLastRepayment(Date gmtLastRepayment){
		this.gmtLastRepayment = gmtLastRepayment;
	}

	/**
	 * 获取借款结清时间
	 *
	 * @return 借款结清时间
	 */
	public Date getGmtFinish(){
		return gmtFinish;
	}

	/**
	 * 设置借款结清时间
	 *
	 * @param gmtFinish 要设置的借款结清时间
	 */
	public void setGmtFinish(Date gmtFinish){
		this.gmtFinish = gmtFinish;
	}

	/**
	 * 获取借款用途
	 *
	 * @return 借款用途
	 */
	public String getBorrowRemark(){
		return borrowRemark;
	}

	/**
	 * 设置借款用途
	 *
	 * @param borrowRemark 要设置的借款用途
	 */
	public void setBorrowRemark(String borrowRemark){
		this.borrowRemark = borrowRemark;
	}

	/**
	 * 获取还款来源
	 *
	 * @return 还款来源
	 */
	public String getRefundRemark(){
		return refundRemark;
	}

	/**
	 * 设置还款来源
	 *
	 * @param refundRemark 要设置的还款来源
	 */
	public void setRefundRemark(String refundRemark){
		this.refundRemark = refundRemark;
	}

	/**
	 * 获取原借款Id
	 *
	 * @return 原借款Id
	 */
	public Long getBorrowId(){
		return borrowId;
	}

	/**
	 * 设置原借款Id
	 *
	 * @param borrowId 要设置的原借款Id
	 */
	public void setBorrowId(Long borrowId){
		this.borrowId = borrowId;
	}

	public String getGoodsIcon() {
		return goodsIcon;
	}

	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	
	
}
