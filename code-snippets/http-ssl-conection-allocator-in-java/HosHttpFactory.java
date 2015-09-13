import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
* referenced the Apache HttpClient Example of SSL linking. got running both in Tomcat and Wildfly web app server. Should there be a connection pool, for a better use.
*/
public class HosHttpFactory {
    final private static Logger logger = Logger.getLogger(HosHttpFactory.class);
    private static CloseableHttpClient httpClient = null;
    private static final int RETRY_COUNT = 4;

    private static boolean init() {
        URI uri;

        SSLContext sslcontext;
        try {
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(HosHttpFactory.class.getResource("/RootCertKeyStore.jks"), "...[TheKeyStorePassword]...".toCharArray(),
                            new TrustSelfSignedStrategy())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("找不到所需算法", e);
            return false;
        } catch (KeyManagementException e) {
            logger.error("根证书文件密钥管理异常", e);
            return false;
        } catch (KeyStoreException e) {
            logger.error("根证书keystore文件异常", e);
            return false;
        } catch (CertificateException e) {
            logger.error("根证书文件内证书异常", e);
            return false;
        } catch (IOException e) {
            logger.error("读取访问https所用根证书文件是出错", e);
            return false;
        }

        RequestConfig rc = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(
                    IOException exception,
                    int executionCount,
                    HttpContext context) {
                if (executionCount >= RETRY_COUNT) {
                    // Do not retry if over max retry count
                    logger.debug("重复请求次数超过限制");
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    logger.debug("超时异常");
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    logger.debug("未知主机异常");
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    logger.debug("建立连接超时");
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    logger.warn("SSL握手异常");
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    logger.trace(String.format("不重发不幂等的请求: %s", request.getRequestLine()));
                    return true;
                }
                return false;
            }

        };
        httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setDefaultRequestConfig(rc)
                .setRetryHandler(myRetryHandler)
                .build();
        return true;
    }

    public static CloseableHttpResponse execute(HttpUriRequest request) {
        if (httpClient == null) {
            synchronized (HosHttpFactory.class) {
                if (httpClient == null) {
                    init();
                }
            }
        }
        if (httpClient != null) {
            try {
                return httpClient.execute(request);
            } catch (IOException e) {
                logger.error("执行http request时出错", e);
                return null;
            }
        } else {
            return null;
        }
    }
}