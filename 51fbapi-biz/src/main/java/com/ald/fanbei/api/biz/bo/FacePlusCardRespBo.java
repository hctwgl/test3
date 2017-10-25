package com.ald.fanbei.api.biz.bo;




/**
 * 
 * @类描述：face++返回值
 * @author xiaotianjian 2017年7月23日下午10:53:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FacePlusCardRespBo{

	private String request_id;//用于区分每一次请求的唯一的字符串。
	private Float time_used;//整个请求所花费的时间，单位为毫秒。
	private String address;//住址
	private FacePlusCardBirthDayResp birthday;//生日，下分年月日，都是一个字符串
	private String gender;//性别（男/女）
	private String id_card_number;//身份证号
	private String name;//姓名
	private String race;//民族(汉字)
	private String side;//front：表示身份证的人像面  back：表示身份证的国徽面
	private String issued_by;//签发机关
	private String valid_date;//有效日期
	private String head_rect;//身份证中人脸框的位置，分别包含左上、右上、右下、左下四个角点。可能会超过图本身。
	private String legality;//如果用户调用时设置可选参数legality为“1”，则返回身份证照片的合法性检查结果，否则不返回该字段。结果为五种分类的概率值（取［0，1］区间实数，取3位有效数字，总和等于1.0）
	private String valid_date_begin;//开始有效日期
	private String valid_date_end;//截止有效日期
	
	public class FacePlusCardBirthDayResp{
		private String year;
		private String month;
		private String day;
		/**
		 * @return the year
		 */
		public String getYear() {
			return year;
		}
		/**
		 * @param year the year to set
		 */
		public void setYear(String year) {
			this.year = year;
		}
		/**
		 * @return the month
		 */
		public String getMonth() {
			return month;
		}
		/**
		 * @param month the month to set
		 */
		public void setMonth(String month) {
			this.month = month;
		}
		/**
		 * @return the day
		 */
		public String getDay() {
			return day;
		}
		/**
		 * @param day the day to set
		 */
		public void setDay(String day) {
			this.day = day;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "{year=" + year + ", month=" + month + ", day=" + day + "}";
		}
	}
	/**
	 * @return the request_id
	 */
	public String getRequest_id() {
		return request_id;
	}
	/**
	 * @param request_id the request_id to set
	 */
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	/**
	 * @return the time_used
	 */
	public Float getTime_used() {
		return time_used;
	}
	/**
	 * @param time_used the time_used to set
	 */
	public void setTime_used(Float time_used) {
		this.time_used = time_used;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the id_card_number
	 */
	public String getId_card_number() {
		return id_card_number;
	}
	/**
	 * @param id_card_number the id_card_number to set
	 */
	public void setId_card_number(String id_card_number) {
		this.id_card_number = id_card_number;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the side
	 */
	public String getSide() {
		return side;
	}
	/**
	 * @param side the side to set
	 */
	public void setSide(String side) {
		this.side = side;
	}
	/**
	 * @return the issued_by
	 */
	public String getIssued_by() {
		return issued_by;
	}
	/**
	 * @param issued_by the issued_by to set
	 */
	public void setIssued_by(String issued_by) {
		this.issued_by = issued_by;
	}
	/**
	 * @return the valid_date
	 */
	public String getValid_date() {
		return valid_date;
	}
	/**
	 * @param valid_date the valid_date to set
	 */
	public void setValid_date(String valid_date) {
		this.valid_date = valid_date;
	}
	/**
	 * @return the head_rect
	 */
	public String getHead_rect() {
		return head_rect;
	}
	/**
	 * @param head_rect the head_rect to set
	 */
	public void setHead_rect(String head_rect) {
		this.head_rect = head_rect;
	}
	/**
	 * @return the legality
	 */
	public String getLegality() {
		return legality;
	}
	/**
	 * @param legality the legality to set
	 */
	public void setLegality(String legality) {
		this.legality = legality;
	}
	/**
	 * @return the race
	 */
	public String getRace() {
		return race;
	}
	/**
	 * @param race the race to set
	 */
	public void setRace(String race) {
		this.race = race;
	}
	/**
	 * @return the valid_date_begin
	 */
	public String getValid_date_begin() {
		return valid_date_begin;
	}
	/**
	 * @param valid_date_begin the valid_date_begin to set
	 */
	public void setValid_date_begin(String valid_date_begin) {
		this.valid_date_begin = valid_date_begin;
	}
	/**
	 * @return the valid_date_end
	 */
	public String getValid_date_end() {
		return valid_date_end;
	}
	/**
	 * @param valid_date_end the valid_date_end to set
	 */
	public void setValid_date_end(String valid_date_end) {
		this.valid_date_end = valid_date_end;
	}
	/**
	 * @return the birthday
	 */
	public FacePlusCardBirthDayResp getBirthday() {
		return birthday;
	}
	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(FacePlusCardBirthDayResp birthday) {
		this.birthday = birthday;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{request_id=" + request_id + ", time_used=" + time_used + ", address=" + address + ", birthday=" + birthday + ", gender=" + gender + ", id_card_number=" + id_card_number + ", name=" + name + ", race=" + race + ", side=" + side + ", issued_by=" + issued_by + ", valid_date=" + valid_date + ", head_rect=" + head_rect + ", legality=" + legality + ", valid_date_begin=" + valid_date_begin + ", valid_date_end=" + valid_date_end + "}";
	}
	
	public String getRealBirthDay() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBirthday().getYear()).append(".")
		.append(getBirthday().getMonth()).append(".")
		.append(getBirthday().getDay());
		return sb.toString();
	}

	
}
