package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfResourceH5Do;


/**
 * @author Jingru
 * @version 创建时间：2018年3月22日 下午1:19:53
 * @Description 类描述
 */
public class AfResourceH5Query extends Page<AfResourceH5Do> {
    private static final long serialVersionUID = 7119156778882198076L;

	private String           	  name;

    private String                url;

    private Byte                 isDelete;

	public Byte getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Byte isDelete) {
		this.isDelete = isDelete;
	}

	public AfResourceH5Query() {
        super();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


}
