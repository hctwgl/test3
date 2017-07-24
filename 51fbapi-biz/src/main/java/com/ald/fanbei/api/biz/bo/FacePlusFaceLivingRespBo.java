package com.ald.fanbei.api.biz.bo;


/**
 * 
 * @类描述：face ++ 活体验证返回数据
 * @author xiaotianjian 2017年7月24日上午1:37:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FacePlusFaceLivingRespBo {

	private String request_id;// 用于区分每一次请求的唯一的字符串。此字符串可以用于后续数据反查。除非发生404（API_NOT_FOUND )错误，此字段必定返回。
	private Integer time_used;//整个请求所花费的时间，单位为毫秒。除非发生404（API_NOT_FOUND )错误，此字段必定返回。
	private FacePlusResultFaceId result_faceid;//有源比对时，数据源人脸照片与待验证人脸照的比对结果。此字段只在接口被成功调用时返回。
	private String error_message;//当请求失败时才会返回此字符串，具体返回内容见后续错误信息章节。否则此字段不存在。
	
	public class FacePlusResultFaceId{
		private Double confidence;//比对结果的置信度，Float类型，取值［0，100］，数字越大表示两张照片越可能是同一个人。
		private Double thresholds;//一组用于参考的置信度阈值，Object类型，包含三个字段，均为Float类型、取值［0，100］：
		/**
		 * @return the confidence
		 */
		public Double getConfidence() {
			return confidence;
		}
		/**
		 * @param confidence the confidence to set
		 */
		public void setConfidence(Double confidence) {
			this.confidence = confidence;
		}
		/**
		 * @return the thresholds
		 */
		public Double getThresholds() {
			return thresholds;
		}
		/**
		 * @param thresholds the thresholds to set
		 */
		public void setThresholds(Double thresholds) {
			this.thresholds = thresholds;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "{confidence=" + confidence + ", thresholds=" + thresholds + "}";
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
	public Integer getTime_used() {
		return time_used;
	}

	/**
	 * @param time_used the time_used to set
	 */
	public void setTime_used(Integer time_used) {
		this.time_used = time_used;
	}

	/**
	 * @return the result_faceid
	 */
	public FacePlusResultFaceId getResult_faceid() {
		return result_faceid;
	}

	/**
	 * @param result_faceid the result_faceid to set
	 */
	public void setResult_faceid(FacePlusResultFaceId result_faceid) {
		this.result_faceid = result_faceid;
	}

	/**
	 * @return the error_message
	 */
	public String getError_message() {
		return error_message;
	}

	/**
	 * @param error_message the error_message to set
	 */
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{request_id=" + request_id + ", time_used=" + time_used + ", result_faceid=" + result_faceid + ", error_message=" + error_message + "}";
	}
}
