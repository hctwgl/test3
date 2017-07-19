package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author fumeiai 2017年4月24日下午19：08：56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCommitRecordDo extends AbstractSerial {

	private static final long serialVersionUID = -6566198924650170281L;

	private Long id;// 主键，自增
	private String type;// 提交类型，如verify风控认证
	private String relate_id;// 关联id，如borrow_id
	private String content;// 提交的报文
	private String url;// 提交的url
	private String commit_time;// 提交的时间，每次累加，以逗号隔开
	private int commit_num;// 累计提交次数

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelate_id() {
		return relate_id;
	}

	public void setRelate_id(String relate_id) {
		this.relate_id = relate_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCommit_time() {
		return commit_time;
	}

	public void setCommit_time(String commit_time) {
		this.commit_time = commit_time;
	}

	public int getCommit_num() {
		return commit_num;
	}

	public void setCommit_num(int commit_num) {
		this.commit_num = commit_num;
	}

}
