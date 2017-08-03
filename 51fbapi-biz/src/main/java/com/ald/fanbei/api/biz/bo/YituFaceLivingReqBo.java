package com.ald.fanbei.api.biz.bo;

/**
 * 
 * 
 * @类描述：依图活体采集请求类
 * 
 * @author huyang 2017年4月24日下午6:26:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YituFaceLivingReqBo {

	private String query_image_package;
	private boolean query_image_package_check_same_person = true;// 是否检查大礼包中查询照片是同一个人
	private boolean query_image_package_check_anti_screen = true; // 是否开启防屏幕翻拍检测
	private boolean query_image_package_check_anti_picture = true;// 是否开启防照片检测
	private boolean query_image_package_check_anti_eye_blockage = true;// 是否开启眼部遮挡检测
	private boolean query_image_package_check_anti_hole = true;// 是否开启防孔洞检测
	private boolean query_image_package_return_image_list = true;// 是否返回大礼包中解出的图片列表
	private Integer query_image_type = 301;// 查询图片的类型 类证件照 = 301 类证件照(手持身份证场景)=
											// 302 类证件照(VTM场景)= 303 芯片照 = 4
											// 身份证翻拍照 = 2
	private String database_image_content;// 登记照片(JPG)的Base64编码
	private Integer database_image_type = 2;// 登记照片的图片的类型 证件照 = 1 身份证翻拍照 = 2
											// 类证件照 = 3 身份证芯片照 = 4 金融行业水印的证件照
											// (不带横纹的老版网纹照) = 5 公安行业水印证件照(
											// 带横纹的老版网纹照 ) = 7 铁丝网水印的证件照(新版网纹照)
											// = 9

	public String getQuery_image_package() {
		return query_image_package;
	}

	public void setQuery_image_package(String string) {
		this.query_image_package = string;
	}

	public boolean isQuery_image_package_check_same_person() {
		return query_image_package_check_same_person;
	}

	public void setQuery_image_package_check_same_person(boolean query_image_package_check_same_person) {
		this.query_image_package_check_same_person = query_image_package_check_same_person;
	}

	public boolean isQuery_image_package_check_anti_screen() {
		return query_image_package_check_anti_screen;
	}

	public void setQuery_image_package_check_anti_screen(boolean query_image_package_check_anti_screen) {
		this.query_image_package_check_anti_screen = query_image_package_check_anti_screen;
	}

	public boolean isQuery_image_package_check_anti_picture() {
		return query_image_package_check_anti_picture;
	}

	public void setQuery_image_package_check_anti_picture(boolean query_image_package_check_anti_picture) {
		this.query_image_package_check_anti_picture = query_image_package_check_anti_picture;
	}

	public boolean isQuery_image_package_check_anti_eye_blockage() {
		return query_image_package_check_anti_eye_blockage;
	}

	public void setQuery_image_package_check_anti_eye_blockage(boolean query_image_package_check_anti_eye_blockage) {
		this.query_image_package_check_anti_eye_blockage = query_image_package_check_anti_eye_blockage;
	}

	public boolean isQuery_image_package_check_anti_hole() {
		return query_image_package_check_anti_hole;
	}

	public void setQuery_image_package_check_anti_hole(boolean query_image_package_check_anti_hole) {
		this.query_image_package_check_anti_hole = query_image_package_check_anti_hole;
	}

	public boolean isQuery_image_package_return_image_list() {
		return query_image_package_return_image_list;
	}

	public void setQuery_image_package_return_image_list(boolean query_image_package_return_image_list) {
		this.query_image_package_return_image_list = query_image_package_return_image_list;
	}

	public Integer getQuery_image_type() {
		return query_image_type;
	}

	public void setQuery_image_type(Integer query_image_type) {
		this.query_image_type = query_image_type;
	}

	public String getDatabase_image_content() {
		return database_image_content;
	}

	public void setDatabase_image_content(String database_image_content) {
		this.database_image_content = database_image_content;
	}

	public Integer getDatabase_image_type() {
		return database_image_type;
	}

	public void setDatabase_image_type(Integer database_image_type) {
		this.database_image_type = database_image_type;
	}

	@Override
	public String toString() {
		return "{query_image_package:" + query_image_package + ", query_image_package_check_same_person:" + query_image_package_check_same_person
				+ ", query_image_package_check_anti_screen:" + query_image_package_check_anti_screen + ", query_image_package_check_anti_picture:"
				+ query_image_package_check_anti_picture + ", query_image_package_check_anti_eye_blockage:" + query_image_package_check_anti_eye_blockage
				+ ", query_image_package_check_anti_hole:" + query_image_package_check_anti_hole + ", query_image_package_return_image_list:"
				+ query_image_package_return_image_list + "}";
	}

}
