package org.lyh.http.proxy.client;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * Hello world!
 *
 */
public class TestClientApp
{
    private static final AsyncHttpClient asyncHttpClient = asyncHttpClient();
    /*
     * svr_cost_time    svr_thread  thread   request     cost_time  sucess_rate
     * ~10ms            8           5        1000        105206 ms  100
     * ~100ms           8           5        1000        105206 ms  100
     * ~100ms           8           100      100         126783 ms  100
     * ~100ms           200         100      100         17397 ms   100
     * ~100ms           200         1000     100         103885 ms  100
     */
    private static final String TEST_URL = "http://127.0.0.1:7987/app/test/get";
    /*
     * svr_cost_time    svr_thread  thread   request     cost_time  sucess_rate
     * ~10ms            8           5        1000        105206 ms  100
     * ~100ms           8           5        1000        102799 ms  100
     * ~100ms           8           100      100         125928 ms  100
     * ~100ms           200         100      100         13388 ms   100
     * ~100ms           200         1000     100         53158 ms   100
     */
//    private static final String TEST_URL = "http://127.0.0.1:10024/test/get";
    private static final Logger  logger = LoggerFactory.getLogger(TestClientApp.class);

    public static void main( String[] args ) throws InterruptedException, IOException {
        final int nThread = 2000, nRequestInThread = 10000;
        final AtomicInteger cnt = new AtomicInteger(0);
        final ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        final CountDownLatch threadLatch = new CountDownLatch(nThread * nRequestInThread);
        long startTime =  System.currentTimeMillis();
        for (int i = 0 ; i < nThread; i ++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (int k = 0 ; k < nRequestInThread; k ++){
                        syncRequest();
                    }
                }

                private void syncRequest() {
                    try {
                        CompletableFuture<Response> completableFuture = asyncHttpClient.prepareGet(TEST_URL).execute().toCompletableFuture();
                        completableFuture.exceptionally(e -> {
                            logger.error("request exception",e);
                            return null;
                        }).thenApply(response -> {
                            if(response != null && response.hasResponseStatus()
                                    && response.getStatusCode() == 200){
                                cnt.incrementAndGet();
                                //logger.info(response.getResponseBody());
                            } else {
                                logger.error("request failed. repsonse : {}",response);
                            }
                            return null;
                        });

                        completableFuture.join();
                    } finally {
                        threadLatch.countDown();
                    }

                }

                private void asyncRequest() {
                    final ListenableFuture<Response> future = asyncHttpClient.prepareGet(TEST_URL).execute();
                    future.addListener(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                Response response = null;
                                try {
                                    response = future.get();
                                } catch (InterruptedException | ExecutionException e) {
                                    // logger.error("request failed.",e);
                                    return;
                                }
                                if(response != null && response.hasResponseStatus()
                                        && response.getStatusCode() == 200){
                                    cnt.incrementAndGet();
                                    // logger.info(response.getResponseBody());
                                } else {
                                   logger.error("request failed. repsonse : {}",response);
                                }
                            } finally {
                                threadLatch.countDown();
                            }
                        }
                    },executorService);
                }
            });
        }
        threadLatch.await();
        System.out.println(String.format("total request : %d, success request : %d, cost time %d ms",
                nThread * nRequestInThread,cnt.get(),System.currentTimeMillis() - startTime));
        executorService.shutdown();
        asyncHttpClient.close();
    }
}
