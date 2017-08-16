package com.ald.fanbei.api.biz.bo;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 
 * @类描述：依图身份证识别返回
 * 
 * @author huyang 2017年4月22日下午12:45:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YituFaceCardRespBo extends YituFaceRespBo {

	private CardResult idcard_ocr_result;

	public class CardResult {
		private String name; // 姓名
		private String address;// 资质
		private String citizen_id;// 身份证号
		private String valid_date_begin;// 有效开始时间
		private String valid_date_end;// 有效结束时间
		private String gender;// 性别
		private String nation;// 民族
		private String birthday;// 出生年月日
		private String agency;// 签发机关
		private Integer idcard_type;// 识别出的身份证图片类型

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCitizen_id() {
			return citizen_id;
		}

		public void setCitizen_id(String citizen_id) {
			this.citizen_id = citizen_id;
		}

		public String getValid_date_begin() {
			return valid_date_begin;
		}

		public void setValid_date_begin(String valid_date_begin) {
			this.valid_date_begin = valid_date_begin;
		}

		public String getValid_date_end() {
			return valid_date_end;
		}

		public void setValid_date_end(String valid_date_end) {
			this.valid_date_end = valid_date_end;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getNation() {
			return nation;
		}

		public void setNation(String nation) {
			this.nation = nation;
		}

		public String getBirthday() {
			return birthday;
		}

		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}

		public String getAgency() {
			return agency;
		}

		public void setAgency(String agency) {
			this.agency = agency;
		}

		public Integer getIdcard_type() {
			return idcard_type;
		}

		public void setIdcard_type(Integer idcard_type) {
			this.idcard_type = idcard_type;
		}

		@Override
		public String toString() {
			return "{name:" + name + ", address:" + address + ", citizen_id:" + citizen_id + ", valid_date_begin:" + valid_date_begin + ", valid_date_end:" + valid_date_end
					+ ", gender:" + gender + ",nation:" + nation + ",birthday:" + birthday + ",agency:" + agency + ",idcard_type" + idcard_type + "}";
		}

	}

	public CardResult getIdcard_ocr_result() {
		return idcard_ocr_result;
	}

	public void setIdcard_ocr_result(CardResult idcard_ocr_result) {
		this.idcard_ocr_result = idcard_ocr_result;
	}

	@Override
	public String toString() {
		return "{rtn:" + super.getRtn() + ", message:" + super.getMessage() + ",idcard_ocr_result=" + idcard_ocr_result + "}";
	}

	//

	public static void main(String[] args) {
		String s = "{\"idcard_ocr_result\": {name:\"123\"}, \"message\": \"OK\", \"rtn\": 0}";
		YituFaceCardRespBo bo = JSONObject.parseObject(s, YituFaceCardRespBo.class);
		System.out.println(bo.getIdcard_ocr_result().getName());
	}

}
