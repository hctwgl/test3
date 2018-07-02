package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 菠萝觅订单详情实体
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeJipiaoFlightDo extends AbstractSerial {

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
     * 第三方订单号
     */
    private String thirdOrderNo;

    /**
     * 到达机场
     */
    private String arrivalAirport;

    /**
     * 到达城市
     */
    private String arrivalCity;

    /**
     * 到达日期
     */
    private String arrivalDate;

    /**
     * 到达时间
     */
    private String arrivalTime;

    /**
     * 舱位类型
     */
    private String cabinType;

    /**
     * 航空公司名称
     */
    private String carrierName;

    /**
     * 正点率
     */
    private String correct;

    /**
     * 起飞机场
     */
    private String departureAirport;

    /**
     * 起飞城市
     */
    private String departureCity;

    /**
     * 起飞日期
     */
    private String departureDate;

    /**
     * 起飞航站楼
     */
    private String departureTerminal;

    /**
     * 起飞时间
     */
    private String departureTime;

    /**
     * 航班时常
     */
    private String duration;

    /**
     * 航班号
     */
    private String flightNum;

    /**
     * 执勤飞机类型
     */
    private String flightTypeFullName;


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
     * 获取第三方订单号
     *
     * @return 第三方订单号
     */
    public String getThirdOrderNo(){
      return thirdOrderNo;
    }

    /**
     * 设置第三方订单号
     * 
     * @param thirdOrderNo 要设置的第三方订单号
     */
    public void setThirdOrderNo(String thirdOrderNo){
      this.thirdOrderNo = thirdOrderNo;
    }

    /**
     * 获取到达机场
     *
     * @return 到达机场
     */
    public String getArrivalAirport(){
      return arrivalAirport;
    }

    /**
     * 设置到达机场
     * 
     * @param arrivalAirport 要设置的到达机场
     */
    public void setArrivalAirport(String arrivalAirport){
      this.arrivalAirport = arrivalAirport;
    }

    /**
     * 获取到达城市
     *
     * @return 到达城市
     */
    public String getArrivalCity(){
      return arrivalCity;
    }

    /**
     * 设置到达城市
     * 
     * @param arrivalCity 要设置的到达城市
     */
    public void setArrivalCity(String arrivalCity){
      this.arrivalCity = arrivalCity;
    }

    /**
     * 获取到达日期
     *
     * @return 到达日期
     */
    public String getArrivalDate(){
      return arrivalDate;
    }

    /**
     * 设置到达日期
     * 
     * @param arrivalDate 要设置的到达日期
     */
    public void setArrivalDate(String arrivalDate){
      this.arrivalDate = arrivalDate;
    }

    /**
     * 获取到达时间
     *
     * @return 到达时间
     */
    public String getArrivalTime(){
      return arrivalTime;
    }

    /**
     * 设置到达时间
     * 
     * @param arrivalTime 要设置的到达时间
     */
    public void setArrivalTime(String arrivalTime){
      this.arrivalTime = arrivalTime;
    }

    /**
     * 获取舱位类型
     *
     * @return 舱位类型
     */
    public String getCabinType(){
      return cabinType;
    }

    /**
     * 设置舱位类型
     * 
     * @param cabinType 要设置的舱位类型
     */
    public void setCabinType(String cabinType){
      this.cabinType = cabinType;
    }

    /**
     * 获取航空公司名称
     *
     * @return 航空公司名称
     */
    public String getCarrierName(){
      return carrierName;
    }

    /**
     * 设置航空公司名称
     * 
     * @param carrierName 要设置的航空公司名称
     */
    public void setCarrierName(String carrierName){
      this.carrierName = carrierName;
    }

    /**
     * 获取正点率
     *
     * @return 正点率
     */
    public String getCorrect(){
      return correct;
    }

    /**
     * 设置正点率
     * 
     * @param correct 要设置的正点率
     */
    public void setCorrect(String correct){
      this.correct = correct;
    }

    /**
     * 获取起飞机场
     *
     * @return 起飞机场
     */
    public String getDepartureAirport(){
      return departureAirport;
    }

    /**
     * 设置起飞机场
     * 
     * @param departureAirport 要设置的起飞机场
     */
    public void setDepartureAirport(String departureAirport){
      this.departureAirport = departureAirport;
    }

    /**
     * 获取起飞城市
     *
     * @return 起飞城市
     */
    public String getDepartureCity(){
      return departureCity;
    }

    /**
     * 设置起飞城市
     * 
     * @param departureCity 要设置的起飞城市
     */
    public void setDepartureCity(String departureCity){
      this.departureCity = departureCity;
    }

    /**
     * 获取起飞日期
     *
     * @return 起飞日期
     */
    public String getDepartureDate(){
      return departureDate;
    }

    /**
     * 设置起飞日期
     * 
     * @param departureDate 要设置的起飞日期
     */
    public void setDepartureDate(String departureDate){
      this.departureDate = departureDate;
    }

    /**
     * 获取起飞航站楼
     *
     * @return 起飞航站楼
     */
    public String getDepartureTerminal(){
      return departureTerminal;
    }

    /**
     * 设置起飞航站楼
     * 
     * @param departureTerminal 要设置的起飞航站楼
     */
    public void setDepartureTerminal(String departureTerminal){
      this.departureTerminal = departureTerminal;
    }

    /**
     * 获取起飞时间
     *
     * @return 起飞时间
     */
    public String getDepartureTime(){
      return departureTime;
    }

    /**
     * 设置起飞时间
     * 
     * @param departureTime 要设置的起飞时间
     */
    public void setDepartureTime(String departureTime){
      this.departureTime = departureTime;
    }

    /**
     * 获取航班时常
     *
     * @return 航班时常
     */
    public String getDuration(){
      return duration;
    }

    /**
     * 设置航班时常
     * 
     * @param duration 要设置的航班时常
     */
    public void setDuration(String duration){
      this.duration = duration;
    }

    /**
     * 获取航班号
     *
     * @return 航班号
     */
    public String getFlightNum(){
      return flightNum;
    }

    /**
     * 设置航班号
     * 
     * @param flightNum 要设置的航班号
     */
    public void setFlightNum(String flightNum){
      this.flightNum = flightNum;
    }

    /**
     * 获取执勤飞机类型
     *
     * @return 执勤飞机类型
     */
    public String getFlightTypeFullName(){
      return flightTypeFullName;
    }

    /**
     * 设置执勤飞机类型
     * 
     * @param flightTypeFullName 要设置的执勤飞机类型
     */
    public void setFlightTypeFullName(String flightTypeFullName){
      this.flightTypeFullName = flightTypeFullName;
    }

}