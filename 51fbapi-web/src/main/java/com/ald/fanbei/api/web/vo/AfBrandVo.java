package com.ald.fanbei.api.web.vo;


import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 品牌VO对象
 * @author liutengyuan 
 * @date 2018年4月20日
 */
public class AfBrandVo extends AbstractSerial{

	private static final long serialVersionUID = 1L;
	/**
     * 主键Rid
     */
    private Long rid;
    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌logo
     */
    private String logo;

    /**
     * 品牌Banner
     */
    private String banner;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

    
    
}
