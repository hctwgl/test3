package com.ald.fanbei.api.biz.bo;

public class RiskQuotaRespBo extends RiskRespBo {

	private String code;

	private Data data;

	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static class Data {
		private String consumerNo;
		private Result[] results;
		private String amount;
		private Result[] bldResults;
		private String bldAmount;
		private String totalAmount;
		public String getConsumerNo() {
			return consumerNo;
		}
		public void setConsumerNo(String consumerNo) {
			this.consumerNo = consumerNo;
		}
		public Result[] getResults() {
			return results;
		}
		public void setResults(Result[] results) {
			this.results = results;
		}
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public Result[] getBldResults() {
			return bldResults;
		}
		public void setBldResults(Result[] bldResults) {
			this.bldResults = bldResults;
		}
		public String getBldAmount() {
			return bldAmount;
		}
		public void setBldAmount(String bldAmount) {
			this.bldAmount = bldAmount;
		}
		public String getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(String totalAmount) {
			this.totalAmount = totalAmount;
		}
		
		
	}
	
	public static class Result {
		private String scene ;
		private String result;
		public String getScene() {
			return scene;
		}
		public void setScene(String scene) {
			this.scene = scene;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		
	}
}
