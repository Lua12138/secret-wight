import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import z.c.s.wt.util.HosHttpFactory;
import z.c.s.wt.util.WcConfig;

import java.io.IOException;

public class AccessTokenTransmitter {
    private final static Logger logger = Logger.getLogger(AccessTokenTransmitter.class);
    /**
     * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
     */
    private static final String urlFormat = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    protected String transmit() {
        String url = String.format(urlFormat, WcConfig.getAppId(), WcConfig.getAppSecret());
        // place to modify the app parameters
        HttpGet httpGet = new HttpGet(url);

        logger.trace("executing request " + httpGet.getRequestLine());

        CloseableHttpResponse response;
        response = HosHttpFactory.execute(httpGet);
        if (response == null) {
            logger.error("执行http post提交微信网站时出错");
        }
        try {
            HttpEntity entity = response.getEntity();

            logger.info(String.format("执行微信api获取access token的http响应状态行: %s", response.getStatusLine()));
            String s = EntityUtils.toString(entity); //假设微信https访问总是可信的，返回http body不会撑爆服务器。
            logger.debug(String.format("返回消息: %s", s));
            JSONObject o = (JSONObject) JSON.parse(s);
            if (o != null && o.getBoolean("errcode") != null) {
                if (o.getBoolean("errcode")) {
                    logger.info("调用web api，menu/create创建微信公众号菜单成功");
                    return null;
                } else {
                    logger.error(String.format("使用menu/create的web api创建菜单时返回错误，错误代码: %d, 错误消息: %s", o.getInteger("errcode"), o.getString("errmsg")));
                    return null;
                }
            } else {
                String accessToken = o.getString("access_token");
                Integer expiresIn = o.getInteger("expires_in");
                if (accessToken != null && expiresIn != null) {
                    logger.info(String.format("请求获取了新的access token，长度为: %d，expires in: %d", accessToken.length(), expiresIn));
                    return accessToken;
                } else {
                    logger.error("调用menu/create的web api创建微信菜单式，返回json消息解读失败");
                    return null;
                }
            }
        } catch (IOException e) {
            logger.error("读取http返回entity时遇到错误", e);
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("关闭httpClient的response对象时遇到错误", e);
            }
        }
    }

    public static void main(String[] args) {
        AccessTokenTransmitter att = new AccessTokenTransmitter();
        System.out.println(att.transmit());
    }
}