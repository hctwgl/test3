package com.ald.fanbei.api.common.page;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 *@类描述：分页类
 *@author 陈金虎 2017年1月16日 下午11:46:46
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Page<T> extends AbstractSerial implements Paginable<T> {

	private static final long serialVersionUID = -7087449214551867328L;

	public static final int DEFAULT_PAGE_SIZE = 10; // 默认每页记录数

	public static final int PAGE_COUNT = 10;

	private int pageIndex = 1; // 页码

	private int pageSize = DEFAULT_PAGE_SIZE; // 每页记录数

	private int totalCount = 0; // 总记录数

	private int totalPage = 0; // 总页数

	private String timestamp = null; // 查询时间戳

	private boolean full = true; // 是否需要感知全量数据 //false 不更新totalcount

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
		this.setTotalPage(totalPage);
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
