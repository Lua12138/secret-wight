import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import z.c.s.wt.util.HosHttpFactory;

import java.io.*;
import java.net.URISyntaxException;

public class MenuTransmitter {
    final private static Logger logger = Logger.getLogger(MenuTransmitter.class);
    private final static String menuFilename = "/wcmenu-aliyun.json";

    /**
     * https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN
     */
    private static final String urlFormat = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

    public boolean process(String accessToken) {
        String url = String.format(urlFormat, accessToken);
        return transmit(readMenuJson(), url);
    }

    public boolean process(String menuJson, String accessToken) {
        String url = String.format(urlFormat, accessToken);
        return transmit(menuJson, url);
    }

    protected String readMenuJson() {
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(new FileInputStream(new File(MenuTransmitter.class.getResource(menuFilename).toURI())), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("无法用UTF-8处理json menu文件的编码格式", e);
            return null;
        } catch (FileNotFoundException e) {
            logger.error("wcmenu.json文件没找到", e);
            return null;
        } catch (URISyntaxException e) {
            logger.error("类加载json菜单文件资源转换URL到URI时失败");
            return null;
        }
		// no good method to veridate the json format of the wechat menu file.
        JSONReader jr = new JSONReader(isr);
        JSONObject o = (JSONObject) jr.readObject();
        try {
            isr.close();
        } catch (IOException e) {
            logger.error("关闭JsonReader遇到错误", e);
        }
        return o.toString();
    }

    protected boolean transmit(String jsonBody, String url) {
		// a connection pool could be used instead of this.
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
        logger.trace("executing request " + httpPost.getRequestLine());

        CloseableHttpResponse response;
        response = HosHttpFactory.execute(httpPost);
        if (response == null) {
            logger.error("执行http post提交微信网站时出错");
        }
        try {
            HttpEntity entity = response.getEntity();

            String s = EntityUtils.toString(entity); //假设微信https连接返回内容不会撑爆服务器
            logger.debug(String.format("微信api，创建菜单，返回消息: %s", s));
            JSONObject o = (JSONObject) JSON.parse(s);
            Integer errCode = o.getInteger("errcode");
            if (o != null && errCode != null) {
                if (errCode == 0) {
                    logger.info("调用web api，menu/create创建微信公众号菜单成功");
                    return true;
                } else {
                    logger.error(String.format("使用menu/create的web api创建菜单时返回错误，错误代码: %d, 错误消息: %s", errCode, o.getString("errmsg")));
                    return false;
                }
            } else {
                logger.error("调用menu/create的web api创建微信菜单式，返回json消息解读失败");
                return false;
            }
        } catch (IOException e) {
            logger.error("读取http返回entity时遇到错误", e);
            return false;
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
        MenuTransmitter mt = new MenuTransmitter();
        System.out.println(mt.process(att.transmit()));
        System.out.println("提交文件: " + menuFilename);
        logger.info("下午好亚洲");
    }
}