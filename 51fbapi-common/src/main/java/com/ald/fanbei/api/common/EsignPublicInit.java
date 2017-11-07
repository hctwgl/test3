package com.ald.fanbei.api.common;

public class EsignPublicInit {

    private String projectId;
    private String projectSecret;
    private String apisUrl;
    private String proxyIp;
    private String proxyPort;
    private String httpType;
    private String retry;
    private String esignPublicKey;
    private String privateKey;
    private String algorithm;
    private String eviUrl;

    public String getEviUrl() {
        return eviUrl;
    }

    public void setEviUrl(String eviUrl) {
        this.eviUrl = eviUrl;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectSecret() {
        return projectSecret;
    }

    public void setProjectSecret(String projectSecret) {
        this.projectSecret = projectSecret;
    }

    public String getApisUrl() {
        return apisUrl;
    }

    public void setApisUrl(String apisUrl) {
        this.apisUrl = apisUrl;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getHttpType() {
        return httpType;
    }

    public void setHttpType(String httpType) {
        this.httpType = httpType;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getEsignPublicKey() {
        return esignPublicKey;
    }

    public void setEsignPublicKey(String esignPublicKey) {
        this.esignPublicKey = esignPublicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
