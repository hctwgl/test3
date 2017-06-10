/**
 * 
 */
package com.ald.fanbei.api.web.vo;

import java.util.List;

/**
 *@类现描述：游戏初始化返回结果vo
 *@author chenjinhu 2017年6月3日 下午6:00:46
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGameInitVo {
	private int clientRate;//客户端中奖概率
	private String title;
	private String rule;
	private String isLogin;
	private int chanceCount;
	private String chanceCodes;
	private String isFinish;
	private String isAward;
	private Long gmtCurrent;
	private Long gmtOpen;
	private String recommendCode;
	private int item1Count;
	private int item2Count;
	private int item3Count;
	private int item4Count;
	private int item5Count;
	private List<AwardUserVo> awardList;//最近20条抓娃娃中奖名单
	private List<AwardUserVo> entityAwardList;//最近20条发奖中奖名单
	private AwardInfoVo awardInfo;//中奖信息
	
	public int getClientRate() {
		return clientRate;
	}
	public void setClientRate(int clientRate) {
		this.clientRate = clientRate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getIsLogin() {
		return isLogin;
	}
	public void setIsLogin(String isLogin) {
		this.isLogin = isLogin;
	}
	public int getChanceCount() {
		return chanceCount;
	}
	public void setChanceCount(int chanceCount) {
		this.chanceCount = chanceCount;
	}
	public String getChanceCodes() {
		return chanceCodes;
	}
	public void setChanceCodes(String chanceCodes) {
		this.chanceCodes = chanceCodes;
	}
	public String getIsFinish() {
		return isFinish;
	}
	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}
	public String getIsAward() {
		return isAward;
	}
	public void setIsAward(String isAward) {
		this.isAward = isAward;
	}
	public Long getGmtCurrent() {
		return gmtCurrent;
	}
	public void setGmtCurrent(Long gmtCurrent) {
		this.gmtCurrent = gmtCurrent;
	}
	public Long getGmtOpen() {
		return gmtOpen;
	}
	public void setGmtOpen(Long gmtOpen) {
		this.gmtOpen = gmtOpen;
	}
	public int getItem1Count() {
		return item1Count;
	}
	public void setItem1Count(int item1Count) {
		this.item1Count = item1Count;
	}
	public int getItem2Count() {
		return item2Count;
	}
	public void setItem2Count(int item2Count) {
		this.item2Count = item2Count;
	}
	public int getItem3Count() {
		return item3Count;
	}
	public void setItem3Count(int item3Count) {
		this.item3Count = item3Count;
	}
	public int getItem4Count() {
		return item4Count;
	}
	public void setItem4Count(int item4Count) {
		this.item4Count = item4Count;
	}
	public int getItem5Count() {
		return item5Count;
	}
	public void setItem5Count(int item5Count) {
		this.item5Count = item5Count;
	}
	public List<AwardUserVo> getAwardList() {
		return awardList;
	}
	public void setAwardList(List<AwardUserVo> awardList) {
		this.awardList = awardList;
	}
	public List<AwardUserVo> getEntityAwardList() {
		return entityAwardList;
	}
	public void setEntityAwardList(List<AwardUserVo> entityAwardList) {
		this.entityAwardList = entityAwardList;
	}
	public AwardInfoVo getAwardInfo() {
		return awardInfo;
	}
	public void setAwardInfo(AwardInfoVo awardInfo) {
		this.awardInfo = awardInfo;
	}
	public String getRecommendCode() {
		return recommendCode;
	}
	public void setRecommendCode(String recommendCode) {
		this.recommendCode = recommendCode;
	}


	public class AwardInfoVo{
		private String type;
		private String awardName;
		private Long awardId;
		private String awardIcon;
		private String isSubmitContacts;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAwardName() {
			return awardName;
		}
		public void setAwardName(String awardName) {
			this.awardName = awardName;
		}
		public Long getAwardId() {
			return awardId;
		}
		public void setAwardId(Long awardId) {
			this.awardId = awardId;
		}
		public String getAwardIcon() {
			return awardIcon;
		}
		public void setAwardIcon(String awardIcon) {
			this.awardIcon = awardIcon;
		}
		public String getIsSubmitContacts() {
			return isSubmitContacts;
		}
		public void setIsSubmitContacts(String isSubmitContacts) {
			this.isSubmitContacts = isSubmitContacts;
		}
	}
	
	public class AwardUserVo{
		private String avatar;
		private String userName;
		private String msg;
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}



