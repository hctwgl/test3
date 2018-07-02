package com.ald.fanbei.api.dal.domain.dto;

public class AppMaidianDto {
    public String getMaidianInfo() {
        return maidianInfo;
    }

    public void setMaidianInfo(String maidianInfo) {
        this.maidianInfo = maidianInfo;
    }

    public String getMaidianInfo1() {
        return maidianInfo1;
    }

    public void setMaidianInfo1(String maidianInfo1) {
        this.maidianInfo1 = maidianInfo1;
    }

    public String getMaidianInfo2() {
        return maidianInfo2;
    }

    public void setMaidianInfo2(String maidianInfo2) {
        this.maidianInfo2 = maidianInfo2;
    }

    public String getMaidianInfo3() {
        return maidianInfo3;
    }

    public void setMaidianInfo3(String maidianInfo3) {
        this.maidianInfo3 = maidianInfo3;
    }

    private String maidianInfo;
    private String maidianInfo1;
    private String maidianInfo2;
    private String maidianInfo3;

    @Override
    public String toString() {
        return "AppMaidianDto{" +
                "maidianInfo='" + maidianInfo + '\'' +
                ", maidianInfo1='" + maidianInfo1 + '\'' +
                ", maidianInfo2='" + maidianInfo2 + '\'' +
                ", maidianInfo3='" + maidianInfo3 + '\'' +
                '}';
    }
}
