package com.ald.fanbei.api.biz.third.util.yitu;

public class FaceSaasDataStructure {

	static public class UserInfo {
		// 用户登记照的内容。 照片要求为JPEG格式。
		private String imageContent;
		// 图片的签名， 从Base64解码得到
		private String imageSignatrue;
		// 用户姓名
		private String name;
		// 用户身份证号
		private String citizenId;

		// 用户类型标记
		// 1: 黑名单用户
		// 2: 用户注册用户
		private int label;
		// 用户名
		private String userId;

		// 查询人脸的活体比对数据包
		private String queryImagePackage;

		public String getQueryImagePackage() {
			return queryImagePackage;
		}

		public void setQueryImagePackage(String queryImagePackage) {
			this.queryImagePackage = queryImagePackage;
		}

		public UserInfo() {
			imageContent = "";
			name = "";
			imageSignatrue = "";
			userId = "";
			citizenId = "";
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public int getLabel() {
			return label;
		}

		public void setLabel(int label) {
			this.label = label;
		}

		public String getImageContent() {
			return imageContent;
		}

		public void setImageContent(String imageContent) {
			this.imageContent = imageContent;
		}

		public String getImageSignatrue() {
			return imageSignatrue;
		}

		public void setImageSignatrue(String imageSignatrue) {
			this.imageSignatrue = imageSignatrue;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCitizenId() {
			return citizenId;
		}

		public void setCitizenId(String citizenId) {
			this.citizenId = citizenId;
		}
	}

	static public class UploadOptions {
		// 上传的图像类型
		private int imageType;
		// 开启自动旋转矫正 仅在上传翻拍或证件照时有意义
		private boolean autoRotate;
		// 是否开启镜像检
		private boolean autoFlip;
		// 将检查用户是否在人像库中
		private boolean checkRepo;
		// 开启有效身份证翻拍照效验 仅在上传翻拍身份证照时有意义
		private boolean idcardConfirm;
		// 开启身份证识别，将返回身份证信息
		private boolean idcardOcr;
		// 1: 身份证正面识别
		// 2: 身份证背面识别
		// 3: auto 自动区分正面背面
		private int idcardOcrMode;
		// 开启去水印，将返回去水印后的图片
		private boolean removeWatermark;
		// 开启照片客户端签名验证
		private boolean enableClientImageSignatureCheck;

		public UploadOptions() {
			autoRotate = false;
			autoFlip = false;
			checkRepo = false;
			idcardConfirm = false;
			idcardOcr = false;
			removeWatermark = false;
			enableClientImageSignatureCheck = false;
			idcardOcrMode = 1;
		}

		public int getImageType() {
			return imageType;
		}

		public void setImageType(int imageType) {
			this.imageType = imageType;
		}

		public boolean isAutoRotate() {
			return autoRotate;
		}

		public void setAutoRotate(boolean autoRotate) {
			this.autoRotate = autoRotate;
		}

		public boolean isAutoFlip() {
			return autoFlip;
		}

		public void setAutoFlip(boolean autoFlip) {
			this.autoFlip = autoFlip;
		}

		public boolean isCheckRepo() {
			return checkRepo;
		}

		public void setCheckRepo(boolean checkRepo) {
			this.checkRepo = checkRepo;
		}

		public boolean isIdcardConfirm() {
			return idcardConfirm;
		}

		public void setIdcardConfirm(boolean idcardConfirm) {
			this.idcardConfirm = idcardConfirm;
		}

		public boolean isIdcardOcr() {
			return idcardOcr;
		}

		public void setIdcardOcr(boolean idcardOcr) {
			this.idcardOcr = idcardOcr;
		}

		public void setIdcardOcrMode(int idcardOcrMode) {
			this.idcardOcrMode = idcardOcrMode;
		}

		public int getIdcardOcrMode() {
			return idcardOcrMode;
		}

		public boolean isRemoveWatermark() {
			return removeWatermark;
		}

		public void setRemoveWatermark(boolean removeWatermark) {
			this.removeWatermark = removeWatermark;
		}

		public boolean isEnableClientImageSignatureCheck() {
			return enableClientImageSignatureCheck;
		}

		public void setEnableClientImageSignatureCheck(
				boolean enableClientImageSignatureCheck) {
			this.enableClientImageSignatureCheck = enableClientImageSignatureCheck;
		}
	}

	static public class VerifyOptions {
		/*
		 * 比对类型： 1: 单张登记照比对 2: 身份证翻拍照辅助比对(三件套)
		 */
		private int verifyType;
		// 图像类型
		private int imageType;
		// 开启自动旋转矫正 仅在上传翻拍或证件照时有意义
		private boolean autoRotate;
		// 开启镜像检查
		private boolean autoFlip;
		// 期望误报率，用来确定分数阈值
		private String trueNegativeRate;

		public boolean isQueryImagePackageCheckSamePerson() {
			return queryImagePackageCheckSamePerson;
		}

		public void setQueryImagePackageCheckSamePerson(
				boolean queryImagePackageCheckSamePerson) {
			this.queryImagePackageCheckSamePerson = queryImagePackageCheckSamePerson;
		}

		public boolean isQueryImagePackageReturnImageList() {
			return queryImagePackageReturnImageList;
		}

		public void setQueryImagePackageReturnImageList(
				boolean queryImagePackageReturnImageList) {
			this.queryImagePackageReturnImageList = queryImagePackageReturnImageList;
		}

		// 检查用户是否在人像库中
		private boolean checkRepo;
		//
		private boolean enablReturnFaceImage;
		// 是否检查活体比对数据包中查询照片是同一个人
		private boolean queryImagePackageCheckSamePerson;
		// 是否返回活体比对数据包中解出的图片列表
		private boolean queryImagePackageReturnImageList;

		public boolean isEnablReturnFaceImage() {
			return enablReturnFaceImage;
		}

		public void setEnablReturnFaceImage(boolean enablReturnFaceImage) {
			this.enablReturnFaceImage = enablReturnFaceImage;
		}

		public VerifyOptions() {
			verifyType = 0;
			imageType = 0;
			autoRotate = false;
			autoFlip = false;
			trueNegativeRate = "99.99";
			checkRepo = false;
			enablReturnFaceImage = false;
		}

		public int getVerifyType() {
			return verifyType;
		}

		public void setVerifyType(int verifyType) {
			this.verifyType = verifyType;
		}

		public int getImageType() {
			return imageType;
		}

		public void setImageType(int imageType) {
			this.imageType = imageType;
		}

		public boolean isAutoRotate() {
			return autoRotate;
		}

		public void setAutoRotate(boolean autoRotate) {
			this.autoRotate = autoRotate;
		}

		public String getTrueNegativeRate() {
			return trueNegativeRate;
		}

		public void setTrueNegativeRate(String trueNegativeRate) {
			this.trueNegativeRate = trueNegativeRate;
		}

		public boolean isCheckRepo() {
			return checkRepo;
		}

		public void setCheckRepo(boolean checkRepo) {
			this.checkRepo = checkRepo;
		}

		public boolean isAutoFlip() {
			return autoFlip;
		}

		public void setAutoFlip(boolean autoFlip) {
			this.autoFlip = autoFlip;
		}

	}

	static public class ImagePackageOptions {
		public String getImagePackage() {
			return imagePackage;
		}

		public void setImagePackage(String imagePackage) {
			this.imagePackage = imagePackage;
		}

		public boolean isReturnImageList() {
			return returnImageList;
		}

		public void setReturnImageList(boolean returnImageList) {
			this.returnImageList = returnImageList;
		}

		public boolean isCheckSamePerson() {
			return checkSamePerson;
		}

		public void setCheckSamePerson(boolean checkSamePerson) {
			this.checkSamePerson = checkSamePerson;
		}

		public boolean isReturnFeatureString() {
			return returnFeatureString;
		}

		public void setReturnFeatureString(boolean returnFeatureString) {
			this.returnFeatureString = returnFeatureString;
		}

		private String imagePackage; // 查询人脸的大礼包
		private boolean returnImageList;// 是否返回大礼包中解出的图片列表
		private boolean checkSamePerson;// 是否检查活体比对数据包中查询照片是同一个人
		private boolean returnFeatureString;// 是否返回正脸特征图片
	}

	static public class FaceAttributeParameter {
		public String getImageContent() {
			return imageContent;
		}

		public void setImageContent(String imageContent) {
			this.imageContent = imageContent;
		}

		public boolean isAutoRotate() {
			return autoRotate;
		}

		public void setAutoRotate(boolean autoRotate) {
			this.autoRotate = autoRotate;
		}

		public boolean isReturnLandmarkImageContent() {
			return returnLandmarkImageContent;
		}

		public void setReturnLandmarkImageContent(
				boolean returnLandmarkImageContent) {
			this.returnLandmarkImageContent = returnLandmarkImageContent;
		}

		FaceAttributeParameter() {
			imageContent = "";
			autoRotate = false;
			returnLandmarkImageContent = false;
		}

		private String imageContent;
		private boolean autoRotate;
		private boolean returnLandmarkImageContent;
	}
}
