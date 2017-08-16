package com.ald.fanbei.api.biz.bo;

import java.util.Arrays;

/**
 * 
 * 
 * @类描述：依图活体识别返回数据
 * 
 * @author huyang 2017年4月25日下午2:35:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YituFaceLivingRespBo extends YituFaceRespBo {
	
	public static Integer RESULT_TRUE = 0;
	public static Integer RESULT_FALSE = 1;

	private String global_request_id;// 请求的流水号
	private Integer pair_verify_result;// 0 表示认为是同一个人 1 表示认为不是同一个人
	private Double pair_verify_similarity;// 相似值， 值越大越相似 分数取值范围 0 -100
	private PackageResult query_image_package_result;

	public class PackageResult {
		private String customer_defined_content;// 用户在大礼包中预先加入的自定义字段
		private String[] query_image_contents;// 大礼包中解码的图片列表, 若有正脸图片.则正脸图片为第一张.
		private Boolean is_same_person;// 大礼包同人验证结果
		private Boolean is_anti_screen_check_passed;// 防屏幕翻拍的验证结果，是否通过了测试
		private Double is_anti_screen_check_score;// 防屏幕翻拍的验证分数
		private Boolean is_dark_illumination_check_passed;// 光线状况判断的结果, 是否通过测试
		private Double is_dark_illumination_check_score;// 光线状况判断的分数
		private Boolean is_anti_picture_check_valid;// 防照片的判断结果是否有效
		private Boolean is_anti_picture_check_passed;// 防照片的判断结果，是否通过测试
		private Double is_anti_picture_check_score;// 防照片的判断分数
		private Boolean is_anti_eye_blockage_check_valid;// 防眼部遮挡的结果是否有效
		private Boolean is_anti_eye_blockage_check_passed;// 防眼部遮挡的结果，是否通过测试
		private Double is_anti_eye_blockage_check_score;// 防眼部遮挡的判断分数
		private Boolean is_anti_hole_check_valid;// 防孔洞检测结果是否有效
		private Boolean is_anti_hole_check_passed;// 防孔洞检测的结果，是否通过测试
		private Double is_anti_hole_check_score;// 防孔洞的判断分数

		public String getCustomer_defined_content() {
			return customer_defined_content;
		}

		public void setCustomer_defined_content(String customer_defined_content) {
			this.customer_defined_content = customer_defined_content;
		}

		public String[] getQuery_image_contents() {
			return query_image_contents;
		}

		public void setQuery_image_contents(String[] query_image_contents) {
			this.query_image_contents = query_image_contents;
		}

		public Boolean getIs_same_person() {
			return is_same_person;
		}

		public void setIs_same_person(Boolean is_same_person) {
			this.is_same_person = is_same_person;
		}

		public Boolean getIs_anti_screen_check_passed() {
			return is_anti_screen_check_passed;
		}

		public void setIs_anti_screen_check_passed(Boolean is_anti_screen_check_passed) {
			this.is_anti_screen_check_passed = is_anti_screen_check_passed;
		}

		public Double getIs_anti_screen_check_score() {
			return is_anti_screen_check_score;
		}

		public void setIs_anti_screen_check_score(Double is_anti_screen_check_score) {
			this.is_anti_screen_check_score = is_anti_screen_check_score;
		}

		public Boolean getIs_dark_illumination_check_passed() {
			return is_dark_illumination_check_passed;
		}

		public void setIs_dark_illumination_check_passed(Boolean is_dark_illumination_check_passed) {
			this.is_dark_illumination_check_passed = is_dark_illumination_check_passed;
		}

		public Double getIs_dark_illumination_check_score() {
			return is_dark_illumination_check_score;
		}

		public void setIs_dark_illumination_check_score(Double is_dark_illumination_check_score) {
			this.is_dark_illumination_check_score = is_dark_illumination_check_score;
		}

		public Boolean getIs_anti_picture_check_valid() {
			return is_anti_picture_check_valid;
		}

		public void setIs_anti_picture_check_valid(Boolean is_anti_picture_check_valid) {
			this.is_anti_picture_check_valid = is_anti_picture_check_valid;
		}

		public Boolean getIs_anti_picture_check_passed() {
			return is_anti_picture_check_passed;
		}

		public void setIs_anti_picture_check_passed(Boolean is_anti_picture_check_passed) {
			this.is_anti_picture_check_passed = is_anti_picture_check_passed;
		}

		public Double getIs_anti_picture_check_score() {
			return is_anti_picture_check_score;
		}

		public void setIs_anti_picture_check_score(Double is_anti_picture_check_score) {
			this.is_anti_picture_check_score = is_anti_picture_check_score;
		}

		public Boolean getIs_anti_eye_blockage_check_valid() {
			return is_anti_eye_blockage_check_valid;
		}

		public void setIs_anti_eye_blockage_check_valid(Boolean is_anti_eye_blockage_check_valid) {
			this.is_anti_eye_blockage_check_valid = is_anti_eye_blockage_check_valid;
		}

		public Boolean getIs_anti_eye_blockage_check_passed() {
			return is_anti_eye_blockage_check_passed;
		}

		public void setIs_anti_eye_blockage_check_passed(Boolean is_anti_eye_blockage_check_passed) {
			this.is_anti_eye_blockage_check_passed = is_anti_eye_blockage_check_passed;
		}

		public Double getIs_anti_eye_blockage_check_score() {
			return is_anti_eye_blockage_check_score;
		}

		public void setIs_anti_eye_blockage_check_score(Double is_anti_eye_blockage_check_score) {
			this.is_anti_eye_blockage_check_score = is_anti_eye_blockage_check_score;
		}

		public Boolean getIs_anti_hole_check_valid() {
			return is_anti_hole_check_valid;
		}

		public void setIs_anti_hole_check_valid(Boolean is_anti_hole_check_valid) {
			this.is_anti_hole_check_valid = is_anti_hole_check_valid;
		}

		public Boolean getIs_anti_hole_check_passed() {
			return is_anti_hole_check_passed;
		}

		public void setIs_anti_hole_check_passed(Boolean is_anti_hole_check_passed) {
			this.is_anti_hole_check_passed = is_anti_hole_check_passed;
		}

		public Double getIs_anti_hole_check_score() {
			return is_anti_hole_check_score;
		}

		public void setIs_anti_hole_check_score(Double is_anti_hole_check_score) {
			this.is_anti_hole_check_score = is_anti_hole_check_score;
		}

		@Override
		public String toString() {
			return "{customer_defined_content:" + customer_defined_content + ", query_image_contents:" + Arrays.toString(query_image_contents) + ", is_same_person:"
					+ is_same_person + ", is_anti_screen_check_passed:" + is_anti_screen_check_passed + ", is_anti_screen_check_score:" + is_anti_screen_check_score
					+ ", is_dark_illumination_check_passed:" + is_dark_illumination_check_passed + ", is_dark_illumination_check_score:" + is_dark_illumination_check_score
					+ ", is_anti_picture_check_valid:" + is_anti_picture_check_valid + ", is_anti_picture_check_passed:" + is_anti_picture_check_passed
					+ ", is_anti_picture_check_score:" + is_anti_picture_check_score + ", is_anti_eye_blockage_check_valid:" + is_anti_eye_blockage_check_valid
					+ ", is_anti_eye_blockage_check_passed:" + is_anti_eye_blockage_check_passed + ", is_anti_eye_blockage_check_score:" + is_anti_eye_blockage_check_score
					+ ", is_anti_hole_check_valid:" + is_anti_hole_check_valid + ", is_anti_hole_check_passed:" + is_anti_hole_check_passed + ", is_anti_hole_check_score:"
					+ is_anti_hole_check_score + "}";
		}

	}

	public String getGlobal_request_id() {
		return global_request_id;
	}

	public void setGlobal_request_id(String global_request_id) {
		this.global_request_id = global_request_id;
	}

	public Integer getPair_verify_result() {
		return pair_verify_result;
	}

	public void setPair_verify_result(Integer pair_verify_result) {
		this.pair_verify_result = pair_verify_result;
	}

	public Double getPair_verify_similarity() {
		return pair_verify_similarity;
	}

	public void setPair_verify_similarity(Double pair_verify_similarity) {
		this.pair_verify_similarity = pair_verify_similarity;
	}

	public PackageResult getQuery_image_package_result() {
		return query_image_package_result;
	}

	public void setQuery_image_package_result(PackageResult query_image_package_result) {
		this.query_image_package_result = query_image_package_result;
	}

	@Override
	public String toString() {
		return "{global_request_id:" + global_request_id + ", pair_verify_result:" + pair_verify_result + ", pair_verify_similarity:" + pair_verify_similarity
				+ ", query_image_package_result:" + query_image_package_result + "}";
	}

}
