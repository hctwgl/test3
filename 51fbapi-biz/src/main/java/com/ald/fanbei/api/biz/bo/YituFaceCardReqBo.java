package com.ald.fanbei.api.biz.bo;

/**
 * 
 * 
 * @类描述：依图身份证识别传入参数
 * 
 * @author huyang 2017年4月22日上午10:33:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YituFaceCardReqBo {

	public static final Integer OCR_MODE_FRONT = 1;
	public static final Integer OCR_MODE_BACK = 2;

	private Integer mode = 1;// 1 上传图片进行查询 2 上传特征进行查询
	private UserInfo user_info = new UserInfo();
	private Options options = new Options();

	class Options {
		private Integer ocr_type = 1; // 1 翻拍身份证照 2 驾驶证 3 行驶证
		private boolean auto_rotate = true; // 是否开启自动旋转矫正
		private Integer ocr_mode = 1;// 1 身份证正面 2 身份证背面 3 自动 4 双面

		public Integer getOcr_type() {
			return ocr_type;
		}

		public void setOcr_type(Integer ocr_type) {
			this.ocr_type = ocr_type;
		}

		public boolean isAuto_rotate() {
			return auto_rotate;
		}

		public void setAuto_rotate(boolean auto_rotate) {
			this.auto_rotate = auto_rotate;
		}

		public Integer getOcr_mode() {
			return ocr_mode;
		}

		public void setOcr_mode(Integer ocr_mode) {
			this.ocr_mode = ocr_mode;
		}

		@Override
		public String toString() {
			return " {ocr_type:" + ocr_type + ", auto_rotate:" + auto_rotate + ", ocr_mode:" + ocr_mode + "}";
		}

	}

	/**
	 * 
	 * 
	 * @类描述：用户信息（图片）
	 * 
	 * 				@author huyang 2017年4月22日上午11:04:20
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	class UserInfo {
		private String image_content;// mode 为1时必填
		private String feature_content;// mode 为2时必填

		public String getImage_content() {
			return image_content;
		}

		public void setImage_content(String image_content) {
			this.image_content = image_content;
		}

		public String getFeature_content() {
			return feature_content;
		}

		public void setFeature_content(String feature_content) {
			this.feature_content = feature_content;
		}

		@Override
		public String toString() {
			return " {image_content:" + image_content + "}";
		}

	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public UserInfo getUser_info() {
		return user_info;
	}

	public void setUser_info(UserInfo user_info) {
		this.user_info = user_info;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public void genUserInfo(String imageContent) {
		this.user_info.image_content = imageContent;
	}

	public void genOptions(Integer ocrMode) {
		this.options.ocr_mode = ocrMode;
	}

	@Override
	public String toString() {
		return " {mode:" + mode + ", user_info:" + user_info + ", options:" + options + "}";
	}

}
