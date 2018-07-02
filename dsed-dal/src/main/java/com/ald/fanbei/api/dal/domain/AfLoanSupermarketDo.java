package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * 贷款超市Do
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLoanSupermarketDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String lsmNo;
	private String iconUrl;
	private Date gmtCreate;
	private Date gmtModified;
	private Long createUserId;
	private Long modifyUserId;
	private String lsmName;
	private String lsmIntro;
	private String linkUrl;
	private String label;
	private Integer orderNo;
	private Integer status;
	private Integer isDelete;
	private String marketPoint;
	private int isUnionLogin;
	private String lsmSecretKey;
	/**
	 * 额度范围下限
	 */
	private BigDecimal moneyMin;

	/**
	 * 额度范围上限
	 */
	private BigDecimal moneyMax;

	/**
	 * 期限范围下限
	 */
	private Integer timeMin;

	/**
	 * 期限范围上限
	 */
	private Integer timeMax;

	/**
	 * 还款方式：1.按月还款，2.按日还款
	 */
	private Integer payMethod;

	/**
	 * 申请流程
	 */
	private String applyProcess;

	/**
	 * 申请条件
	 */
	private String applyCondition;

	/**
	 * 所需材料
	 */
	private String needMaterial;

	/**
	 * 最快放款时间
	 */
	private String fastLendTime;

	/**
	 * 利率
	 */
	private BigDecimal lendRate;

	/**
	 * 贷款金额
	 */
	private BigDecimal money;

	/**
	 * 贷款时间
	 */
	private Integer time;

	/**
	 * 贷款时间单位：1.天，2.月
	 */
	private Integer timeUnit;

	/**
	 * 标语
	 */
	private String slogan;

	/**
	 * 第三方接口url
	 */
	private String registerUrl;


	public AfLoanSupermarketDo() {
		super();
	}

	public AfLoanSupermarketDo(String lsmNo, String iconUrl, String lsmName,
			String lsmIntro, String linkUrl, String label, String marketPoint) {
		super();
		this.lsmNo = lsmNo;
		this.iconUrl = iconUrl;
		this.lsmName = lsmName;
		this.lsmIntro = lsmIntro;
		this.linkUrl = linkUrl;
		this.label = label;
		this.marketPoint = marketPoint;
	}


	public String getMarketPoint() {
		return marketPoint;
	}

	public void setMarketPoint(String marketPoint) {
		this.marketPoint = marketPoint;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLsmNo() {
		return lsmNo;
	}
	public void setLsmNo(String lsmNo) {
		this.lsmNo = lsmNo;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public Long getModifyUserId() {
		return modifyUserId;
	}
	public void setModifyUserId(Long modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	public String getLsmName() {
		return lsmName;
	}
	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}
	public String getLsmIntro() {
		return lsmIntro;
	}
	public void setLsmIntro(String lsmIntro) {
		this.lsmIntro = lsmIntro;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public int getIsUnionLogin() {
		return isUnionLogin;
	}

	public void setIsUnionLogin(int isUnionLogin) {
		this.isUnionLogin = isUnionLogin;
	}

	public String getLsmSecretKey() {
		return lsmSecretKey;
	}

	public void setLsmSecretKey(String lsmSecretKey) {
		this.lsmSecretKey = lsmSecretKey;
	}

	/**
	 * 获取额度范围下限
	 *
	 * @return 额度范围下限
	 */
	public BigDecimal getMoneyMin(){
		return moneyMin;
	}

	/**
	 * 设置额度范围下限
	 *
	 * @param moneyMin 要设置的额度范围下限
	 */
	public void setMoneyMin(BigDecimal moneyMin){
		this.moneyMin = moneyMin;
	}

	/**
	 * 获取额度范围上限
	 *
	 * @return 额度范围上限
	 */
	public BigDecimal getMoneyMax(){
		return moneyMax;
	}

	/**
	 * 设置额度范围上限
	 *
	 * @param moneyMax 要设置的额度范围上限
	 */
	public void setMoneyMax(BigDecimal moneyMax){
		this.moneyMax = moneyMax;
	}

	/**
	 * 获取期限范围下限
	 *
	 * @return 期限范围下限
	 */
	public Integer getTimeMin(){
		return timeMin;
	}

	/**
	 * 设置期限范围下限
	 *
	 * @param timeMin 要设置的期限范围下限
	 */
	public void setTimeMin(Integer timeMin){
		this.timeMin = timeMin;
	}

	/**
	 * 获取期限范围上限
	 *
	 * @return 期限范围上限
	 */
	public Integer getTimeMax(){
		return timeMax;
	}

	/**
	 * 设置期限范围上限
	 *
	 * @param timeMax 要设置的期限范围上限
	 */
	public void setTimeMax(Integer timeMax){
		this.timeMax = timeMax;
	}

	/**
	 * 获取还款方式：1.按月还款，2.按日还款
	 *
	 * @return 还款方式：1.按月还款，2.按日还款
	 */
	public Integer getPayMethod(){
		return payMethod;
	}

	/**
	 * 设置还款方式：1.按月还款，2.按日还款
	 *
	 * @param payMethod 要设置的还款方式：1.按月还款，2.按日还款
	 */
	public void setPayMethod(Integer payMethod){
		this.payMethod = payMethod;
	}

	/**
	 * 获取申请流程
	 *
	 * @return 申请流程
	 */
	public String getApplyProcess(){
		return applyProcess;
	}

	/**
	 * 设置申请流程
	 *
	 * @param applyProcess 要设置的申请流程
	 */
	public void setApplyProcess(String applyProcess){
		this.applyProcess = applyProcess;
	}

	/**
	 * 获取申请条件
	 *
	 * @return 申请条件
	 */
	public String getApplyCondition(){
		return applyCondition;
	}

	/**
	 * 设置申请条件
	 *
	 * @param applyCondition 要设置的申请条件
	 */
	public void setApplyCondition(String applyCondition){
		this.applyCondition = applyCondition;
	}

	/**
	 * 获取所需材料
	 *
	 * @return 所需材料
	 */
	public String getNeedMaterial(){
		return needMaterial;
	}

	/**
	 * 设置所需材料
	 *
	 * @param needMaterial 要设置的所需材料
	 */
	public void setNeedMaterial(String needMaterial){
		this.needMaterial = needMaterial;
	}

	/**
	 * 获取最快放款时间
	 *
	 * @return 最快放款时间
	 */
	public String getFastLendTime(){
		return fastLendTime;
	}

	/**
	 * 设置最快放款时间
	 *
	 * @param fastLendTime 要设置的最快放款时间
	 */
	public void setFastLendTime(String fastLendTime){
		this.fastLendTime = fastLendTime;
	}

	/**
	 * 获取利率
	 *
	 * @return 利率
	 */
	public BigDecimal getLendRate(){
		return lendRate;
	}

	/**
	 * 设置利率
	 *
	 * @param lendRate 要设置的利率
	 */
	public void setLendRate(BigDecimal lendRate){
		this.lendRate = lendRate;
	}

	/**
	 * 获取贷款金额
	 *
	 * @return 贷款金额
	 */
	public BigDecimal getMoney(){
		return money;
	}

	/**
	 * 设置贷款金额
	 *
	 * @param money 要设置的贷款金额
	 */
	public void setMoney(BigDecimal money){
		this.money = money;
	}

	/**
	 * 获取贷款时间
	 *
	 * @return 贷款时间
	 */
	public Integer getTime(){
		return time;
	}

	/**
	 * 设置贷款时间
	 *
	 * @param time 要设置的贷款时间
	 */
	public void setTime(Integer time){
		this.time = time;
	}

	/**
	 * 获取贷款时间单位：1.天，2.月
	 *
	 * @return 贷款时间单位：1.天，2.月
	 */
	public Integer getTimeUnit(){
		return timeUnit;
	}

	/**
	 * 设置贷款时间单位：1.天，2.月
	 *
	 * @param timeUnit 要设置的贷款时间单位：1.天，2.月
	 */
	public void setTimeUnit(Integer timeUnit){
		this.timeUnit = timeUnit;
	}

	/**
	 * 获取标语
	 *
	 * @return 标语
	 */
	public String getSlogan(){
		return slogan;
	}

	/**
	 * 设置标语
	 *
	 * @param slogan 要设置的标语
	 */
	public void setSlogan(String slogan){
		this.slogan = slogan;
	}

	public String getRegisterUrl() {
		return registerUrl;
	}

	public void setRegisterUrl(String registerUrl) {
		this.registerUrl = registerUrl;
	}
}
