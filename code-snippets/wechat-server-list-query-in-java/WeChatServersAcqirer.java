import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import z.c.s.wt.util.HosHttpFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeChatServersAcqirer {
    private static final Logger logger = Logger.getLogger(WeChatServersAcqirer.class);

    public static List<String> get(String token) {
        HttpGet hg = new HttpGet(String.format("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=%s", token));
        CloseableHttpResponse resp = HosHttpFactory.execute(hg);
        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            System.out.println();
            String s = null;
            try {
                s = EntityUtils.toString(resp.getEntity());
            } catch (IOException e) {
                logger.error("在获取微信服务器IP地址列表时，读取http get遇到IO异常", e);
                return null;
            }
//            System.out.println(s);
            JSONObject jo = JSON.parseObject(s);
            if (jo.get("errcode") != null) {
                logger.warn(String.format("", jo.get("errcode"), jo.get("errmsg")));
                return null;
            } else {
                if (jo.get("ip_list") != null) {
                    JSONArray jArr = (JSONArray) jo.get("ip_list");
                    List<String> list = new ArrayList<>(jArr.size());
                    for (Object ss : jArr.toArray()) {
                        list.add((String) ss);
                    }
                    return list;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        AccessTokenTransmitter att = new AccessTokenTransmitter();
        System.out.println(JSON.toJSONString(get(att.transmit()), true));
    }
}