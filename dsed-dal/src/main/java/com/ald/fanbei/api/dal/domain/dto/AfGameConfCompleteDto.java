package com.ald.fanbei.api.dal.domain.dto;
import java.util.List;

/**
 * 借贷超市签到完成奖
 * @类描述:
 *
 * @auther caihuan 2017年9月4日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGameConfCompleteDto {
	
	/**
	 * 载体位置
	 */
	private Integer item;
	
	/**
	 * 载体名称
	 */
	private String name;
	
	/**
	 * 签到天数
	 */
	private Integer days;
	
	/**
	 * 奖品名称
	 */
	private String couponNames;
	
	/**
	 * 类型
	 */
	private String typeString;
	
	private String ids;
	
	private List<AfGameConifPrize> prize;
	
	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<AfGameConifPrize> getPrize() {
		return prize;
	}

	public void setPrize(List<AfGameConifPrize> prize) {
		this.prize = prize;
	}


	public String getCouponNames() {
		return couponNames;
	}

	public void setCouponNames(String couponNames) {
		this.couponNames = couponNames;
	}


	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}


	public static class AfGameConifPrize{
		private String type; //类型
		private String prizeId; //优惠id
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getPrizeId() {
			return prizeId;
		}
		public void setPrizeId(String prizeId) {
			this.prizeId = prizeId;
		}
	}

}