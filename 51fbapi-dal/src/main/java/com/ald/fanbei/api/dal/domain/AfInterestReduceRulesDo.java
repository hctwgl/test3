package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 降息
 *
 * @author xieqiang
 * @create 2018-04-12 12:17
 **/
public class AfInterestReduceRulesDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
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
     * 免息规则名称
     */
    private String name;
    /**
     * 降息比例
     */
    private Double nper1;
    /**
     * 降息比例
     */
    private Double nper2;

    /**
     * 降息比例
     */
    private Double nper3;

    /**
     * 降息比例
     */
    private Double nper6;

    /**
     * 降息比例
     */
    private Double nper9;

    /**
     * 降息比例
     */
    private Double nper12;


    /**
     * 获取主键Id
     *
     * @return id
     */



    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
        return gmtCreate;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
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
     * 获取免息规则名称
     *
     * @return 免息规则名称
     */
    public String getName(){
        return name;
    }

    /**
     * 设置免息规则名称
     *
     * @param name 要设置的免息规则名称
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * 获取降息比例
     *
     * @return 降息比例
     */
    public Double getNper2(){
        return nper2;
    }

    /**
     * 设置降息比例
     *
     * @param nper2 要设置的降息比例
     */
    public void setNper2(Double nper2){
        this.nper2 = nper2;
    }

    /**
     * 获取降息比例
     *
     * @return 降息比例
     */
    public Double getNper3(){
        return nper3;
    }

    /**
     * 设置降息比例
     *
     * @param nper3 要设置的降息比例
     */
    public void setNper3(Double nper3){
        this.nper3 = nper3;
    }

    /**
     * 获取降息比例
     *
     * @return 降息比例
     */
    public Double getNper6(){
        return nper6;
    }

    /**
     * 设置降息比例
     *
     * @param nper6 要设置的降息比例
     */
    public void setNper6(Double nper6){
        this.nper6 = nper6;
    }

    /**
     * 获取降息比例
     *
     * @return 降息比例
     */
    public Double getNper9(){
        return nper9;
    }

    /**
     * 设置降息比例
     *
     * @param nper9 要设置的降息比例
     */
    public void setNper9(Double nper9){
        this.nper9 = nper9;
    }

    /**
     * 获取降息比例
     *
     * @return 降息比例
     */
    public Double getNper12(){
        return nper12;
    }

    /**
     * 设置降息比例
     *
     * @param nper12 要设置的降息比例
     */
    public void setNper12(Double nper12){
        this.nper12 = nper12;
    }

    public Double getNper1() {
        return nper1;
    }

    public void setNper1(Double nper1) {
        this.nper1 = nper1;
    }
}