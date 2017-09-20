package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：IP转换实体
 * @author xiaotianjian 2017年8月15日上午11:46:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class IPTransferBo extends AbstractSerial {
	
	private static final long serialVersionUID = 7261726855615445924L;
	
	private String countryCode;//国家类型
	private String countryName;//国家名称呢给
	private String region;//区域
	private BigDecimal latitude;//纬度
	private BigDecimal longitude;//经度
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}
	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}
	/**
	 * @return the latitude
	 */
	public BigDecimal getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public BigDecimal getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	
	
	
}
