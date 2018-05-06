package org.lyh.http.proxy.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import org.lyh.http.proxy.conf.ProxyConstant;
import org.lyh.http.proxy.conf.ProxyHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * see HttpObjectDecoder
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/5/3 22:23
 */
public class FileCodec extends MessageToMessageDecoder<HttpObject> {

    public static final Logger logger = LoggerFactory.getLogger(FileCodec.class);

    private FileOutputStream fileOutputStream ;

    private File file;

    private Boolean isFileUpload = null;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, HttpObject httpObject, List<Object> list) throws Exception {
        logger.debug("xxxx {}",httpObject.toString());
        if (isFileUpload != null && !isFileUpload) {
            list.add(httpObject);
            return ;
        }
        if (httpObject instanceof HttpRequest) {
            /* 第一个HttpObject一定是HttpRequest的对象，代表着http的请求头 */
            HttpRequest request = (HttpRequest) httpObject;
            String contentType = request.headers().get(HttpHeaderNames.CONTENT_LENGTH);
            isFileUpload = contentType != null &&
                    contentType.contains(HttpHeaderValues.MULTIPART_FORM_DATA.toString());
            if (!isFileUpload) {
                list.add(httpObject);
                return ;
            }
            /* 确定是文件上传，改造HttpRequest */
            request.headers().set(ProxyHeaderNames.IS_FILE_UPLOAD,ProxyConstant.STR_TRUE);
            list.add(request);
        } else if (httpObject instanceof HttpContent) {
            /* 一定是文件内容，将内容追加到磁盘文件 */

        } else {
            logger.error("recv faild: {}", httpObject);
        }
    }
}
