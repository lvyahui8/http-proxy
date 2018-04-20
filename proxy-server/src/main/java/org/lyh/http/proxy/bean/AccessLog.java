package org.lyh.http.proxy.bean;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import org.lyh.http.proxy.core.HttpProxyEntity;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/1 11:08
 */
public class AccessLog {
    private String guid;

    private String requestGroupId;

    private HttpProxyEntity httpProxyEntity;

    private HttpHeaders reqeustHeader;

    private HttpContent requestContent;

    private HttpHeaders responseHeader;

    private HttpContent responseContent;

    private int costTime;

    private long time;

    private boolean success;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getRequestGroupId() {
        return requestGroupId;
    }

    public void setRequestGroupId(String requestGroupId) {
        this.requestGroupId = requestGroupId;
    }

    public HttpProxyEntity getHttpProxyEntity() {
        return httpProxyEntity;
    }

    public void setHttpProxyEntity(HttpProxyEntity httpProxyEntity) {
        this.httpProxyEntity = httpProxyEntity;
    }

    public HttpHeaders getReqeustHeader() {
        return reqeustHeader;
    }

    public void setReqeustHeader(HttpHeaders reqeustHeader) {
        this.reqeustHeader = reqeustHeader;
    }

    public HttpContent getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(HttpContent requestContent) {
        this.requestContent = requestContent;
    }

    public HttpHeaders getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(HttpHeaders responseHeader) {
        this.responseHeader = responseHeader;
    }

    public HttpContent getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(HttpContent responseContent) {
        this.responseContent = responseContent;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
