package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 游戏充值实体
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 16:00:31 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSupOrderDo extends AbstractSerial {

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
     * 关联af_order表的id
     */
    private String orderNo;

    /**
     * 关联af_sup_game表的id
     */
    private Long goodsId;

    /**
     * 充值商品编号
     */
    private String goodsCode;

    /**
     * 充值帐号类型
     */
    private String acctType;

    /**
     * 充值游戏名称
     */
    private String gameName;

    /**
     * 充值用户名
     */
    private String userName;

    /**
     * 充值数量
     */
    private Integer goodsNum;

    /**
     * 充值类型
     */
    private String gameType;

    /**
     * 充值游戏账号
     */
    private String gameAcct;

    /**
     * 充值游戏区
     */
    private String gameArea;

    /**
     * 充值游戏服
     */
    private String gameSrv;

    /**
     * 真实玩家充值请求ip
     */
    private String userIp;

    private Integer goodsCount;

    private String msg;

    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid() {
	return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid) {
	this.rid = rid;
    }

    public Integer getGoodsCount() {
	return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
	this.goodsCount = goodsCount;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(String msg) {
	this.msg = msg;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate() {
	return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate
     *            要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
	this.gmtCreate = gmtCreate;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified() {
	return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified
     *            要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified) {
	this.gmtModified = gmtModified;
    }

    /**
     * 获取关联af_order表的id
     *
     * @return 关联af_order表的id
     */
    public String getOrderNo() {
	return orderNo;
    }

    /**
     * 设置关联af_order表的id
     * 
     * @param orderNo
     *            要设置的关联af_order表的id
     */
    public void setOrderNo(String orderNo) {
	this.orderNo = orderNo;
    }

    /**
     * 获取关联af_sup_game表的id
     *
     * @return 关联af_sup_game表的id
     */
    public Long getGoodsId() {
	return goodsId;
    }

    /**
     * 设置关联af_sup_game表的id
     * 
     * @param goodsId
     *            要设置的关联af_sup_game表的id
     */
    public void setGoodsId(Long goodsId) {
	this.goodsId = goodsId;
    }

    /**
     * 获取充值商品编号
     *
     * @return 充值商品编号
     */
    public String getGoodsCode() {
	return goodsCode;
    }

    /**
     * 设置充值商品编号
     * 
     * @param goodsCode
     *            要设置的充值商品编号
     */
    public void setGoodsCode(String goodsCode) {
	this.goodsCode = goodsCode;
    }

    /**
     * 获取充值帐号类型
     *
     * @return 充值帐号类型
     */
    public String getAcctType() {
	return acctType;
    }

    /**
     * 设置充值帐号类型
     * 
     * @param acctType
     *            要设置的充值帐号类型
     */
    public void setAcctType(String acctType) {
	this.acctType = acctType;
    }

    /**
     * 获取充值游戏名称
     *
     * @return 充值游戏名称
     */
    public String getGameName() {
	return gameName;
    }

    /**
     * 设置充值游戏名称
     * 
     * @param gameName
     *            要设置的充值游戏名称
     */
    public void setGameName(String gameName) {
	this.gameName = gameName;
    }

    /**
     * 获取充值用户名
     *
     * @return 充值用户名
     */
    public String getUserName() {
	return userName;
    }

    /**
     * 设置充值用户名
     * 
     * @param userName
     *            要设置的充值用户名
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * 获取充值数量
     *
     * @return 充值数量
     */
    public Integer getGoodsNum() {
	return goodsNum;
    }

    /**
     * 设置充值数量
     * 
     * @param goodsNum
     *            要设置的充值数量
     */
    public void setGoodsNum(Integer goodsNum) {
	this.goodsNum = goodsNum;
    }

    /**
     * 获取充值类型
     *
     * @return 充值类型
     */
    public String getGameType() {
	return gameType;
    }

    /**
     * 设置充值类型
     * 
     * @param gameType
     *            要设置的充值类型
     */
    public void setGameType(String gameType) {
	this.gameType = gameType;
    }

    /**
     * 获取充值游戏账号
     *
     * @return 充值游戏账号
     */
    public String getGameAcct() {
	return gameAcct;
    }

    /**
     * 设置充值游戏账号
     * 
     * @param gameAcct
     *            要设置的充值游戏账号
     */
    public void setGameAcct(String gameAcct) {
	this.gameAcct = gameAcct;
    }

    /**
     * 获取充值游戏区
     *
     * @return 充值游戏区
     */
    public String getGameArea() {
	return gameArea;
    }

    /**
     * 设置充值游戏区
     * 
     * @param gameArea
     *            要设置的充值游戏区
     */
    public void setGameArea(String gameArea) {
	this.gameArea = gameArea;
    }

    /**
     * 获取充值游戏服
     *
     * @return 充值游戏服
     */
    public String getGameSrv() {
	return gameSrv;
    }

    /**
     * 设置充值游戏服
     * 
     * @param gameSrv
     *            要设置的充值游戏服
     */
    public void setGameSrv(String gameSrv) {
	this.gameSrv = gameSrv;
    }

    /**
     * 获取真实玩家充值请求ip
     *
     * @return 真实玩家充值请求ip
     */
    public String getUserIp() {
	return userIp;
    }

    /**
     * 设置真实玩家充值请求ip
     * 
     * @param userIp
     *            要设置的真实玩家充值请求ip
     */
    public void setUserIp(String userIp) {
	this.userIp = userIp;
    }

}