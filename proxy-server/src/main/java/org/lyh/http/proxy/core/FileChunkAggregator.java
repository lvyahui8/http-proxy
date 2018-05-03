package org.lyh.http.proxy.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
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
public class FileChunkAggregator extends MessageToMessageDecoder<HttpObject> {

    public static final Logger logger = LoggerFactory.getLogger(FileChunkAggregator.class);

    private FileOutputStream fileOutputStream ;

    private File file;

    private Boolean isFileUpload = null;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, HttpObject httpObject, List<Object> list) throws Exception {
        logger.debug("xxxx {}",httpObject.toString());
//        if (isFileUpload != null && !isFileUpload) {
//            list.add(httpObject);
//        }
//        if (httpObject instanceof HttpRequest) {
//            /* 第一个HttpObject一定是HttpRequest的对象，代表着http的请求头 */
//            HttpRequest request = (HttpRequest) httpObject;
//            String contentType = request.headers().get(HttpHeaderNames.CONTENT_LENGTH);
//            isFileUpload = contentType != null &&
//                    contentType.contains(HttpHeaderValues.MULTIPART_FORM_DATA.toString());
//            if (!isFileUpload) {
//                list.add(httpObject);
//            }
//            // 确定是文件上传，将后续的HttpObjectAggregator移除掉，自己封装FullHttpRequest
//            channelHandlerContext.pipeline().remove("http-aggregator");
//        } else if (httpObject instanceof HttpContent) {
//
//        } else {
//            logger.error("recv faild: {}", httpObject);
//        }
        list.add(httpObject);
    }
}
