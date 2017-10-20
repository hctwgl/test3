package com.ald.fanbei.api.dal.domain.dto;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserDo;


/**
 * 
* @ClassName: AfOverdueUserDto 
* @Description: 用于消费分期向风控推送数据的dome
* @author yuyue 
* @date 2017年9月5日 上午10:30:52 
*
 */
public class AfOverdueUserDto extends AfUserDo{
    private static final long serialVersionUID = -3238354383678920782L;
    // user_id
    private Long consumerNo;
    // real_name
    private String realName;

    private String userName;

    private String idNumber;
    // address
    private String registerAddress;
    // 没有户籍地址标识  Y: 没有N: 有
    private String noAddress = "N";
    
    private List<AfOverdueOrderDto> orders;

    public Long getConsumerNo() {
        return consumerNo;
    }

    public void setConsumerNo(Long consumerNo) {
        this.consumerNo = consumerNo;
    }

    @Override
    public String getRealName() {
        return realName;
    }

    @Override
    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getNoAddress() {
        return noAddress;
    }

    public void setNoAddress(String noAddress) {
        this.noAddress = noAddress;
    }

	public List<AfOverdueOrderDto> getOrders() {
		return orders;
	}

	public void setOrders(List<AfOverdueOrderDto> orders) {
		this.orders = orders;
	}

    
}
