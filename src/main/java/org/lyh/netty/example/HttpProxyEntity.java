package org.lyh.netty.example;

/**
 * 一个标准的http协议地址格式：
 * http://user:pass@host:port/path?query#fragment
 * 代理实体只需要知道下列四个属性，其余的部分以及HTTP请求header和body全部转发
 * host
 * port
 * path
 * 一个HttpProxy的唯一标识是inboudPath 和 method的二元组
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/29 16:44
 */
public class HttpProxyEntity {

    /**
     * 代理方法
     */
    private String method;

    /**
     * 入站路径
     */
    private String inboundPath;

    /**
     * 目标主机
     */
    private String targetUri;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getInboundPath() {
        return inboundPath;
    }

    public void setInboundPath(String inboundPath) {
        this.inboundPath = inboundPath;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpProxyEntity that = (HttpProxyEntity) o;

        return method != null ? method.equals(that.method)
                : that.method == null
                && (inboundPath != null ?
                    inboundPath.equals(that.inboundPath) : that.inboundPath == null);

    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (inboundPath != null ? inboundPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "method='" + method + '\'' +
                ", inboundPath='" + inboundPath + '\'' +
                ", targetUri='" + targetUri + '\'' +
                '}';
    }
}
