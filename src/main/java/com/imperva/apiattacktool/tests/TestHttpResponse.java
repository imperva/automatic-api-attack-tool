package com.imperva.apiattacktool.tests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestHttpResponse {
    private String incidentId;
    @JsonProperty("hostName")
    private String hostname;
    private String errorCode;
    @JsonProperty("description")
    private String responseDescription;
    private String timeUtc;
    private String clientIp;
    private String proxyId;
    private String proxyIp;

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getTimeUtc() {
        return timeUtc;
    }

    public void setTimeUtc(String timeUtc) {
        this.timeUtc = timeUtc;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getProxyId() {
        return proxyId;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    @Override
    public String toString() {
        return "TestHttpResponse{\n"
            + "\tincidentId :'" + incidentId + "\'\n"
            + "\thostname :'" + hostname + "\'\n"
            + "\terrorCode :'" + errorCode + "\'\n"
            + "\tresponseDescription :'" + responseDescription + "\'\n"
            + "\ttimeUtc :'" + timeUtc + "\'\n"
            + "\tclientIp :'" + clientIp + "\'\n"
            + "\tproxyId :'" + proxyId + "'\'\n"
            + "\tproxyIp :'" + proxyIp + "\'\n"
            + '}';
    }
}
