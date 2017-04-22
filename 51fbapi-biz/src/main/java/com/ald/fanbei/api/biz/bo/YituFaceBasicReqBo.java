package com.ald.fanbei.api.biz.bo;

import com.alibaba.fastjson.JSON;
import com.taobao.api.request.AlibabaBaichuanAppeventBatchuploadRequest.Json;

/**
 * 
 * 
 * @类描述：依图人脸识别传入参数
 * 
 * @author huyang 2017年4月22日上午10:33:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YituFaceBasicReqBo {

	private Integer mode;// 1 上传图片进行查询 2 上传特征进行查询
	private UserInfo user_info;
	// private

	class Options{
		private Integer ocr_type;// 1 翻拍身份证照 2 驾驶证  3 行驶证
		private boolean auto_rotate = false; // 是否开启自动旋转矫正 
		private Integer ocr_mode;// 
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

	/**
	 * 
	 * @方法说明：TODO
	 * @author huyang
	 * @param imageContent
	 * @param featureContent
	 */
	public void genUserInfo(String imageContent, String featureContent) {
		UserInfo u = new UserInfo();
		u.setImage_content(imageContent);
		u.setFeature_content(featureContent);
		this.user_info = u;
	}

	public static void main(String[] args) {
		YituFaceBasicReqBo bo = new YituFaceBasicReqBo();
		bo.setMode(1);

		bo.genUserInfo("test", "");
		System.out.println(JSON.toJSONString(bo));
	}
}
