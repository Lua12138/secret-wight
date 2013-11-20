package com.baidu.maven.servlet;

import com.baidu.bae.api.counter.BaeCounter;
import com.baidu.bae.api.factory.BaeFactory;
import com.baidu.bae.api.memcache.BaeMemcachedClient;
import com.baidu.bae.api.util.BaeEnv;
import com.mongodb.*;
import com.mysql.jdbc.Driver;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class TestServlet extends HttpServlet {
    private final String mongoDbName = "TnrFPHRKxetAWrbPjZRd";
    private final String redisDbName = "hVrCgFmJfogEMijdYvwT";
    private final String sqlDbName = "RbYlHZrIcQPTSVmVzXJP";
    private String user;
    private String pass;
    private String hostMg;
    private int portMg;
    private String hostRedis;
    private int portRedis;
    private String hostSql;
    private String portSql;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        user = req.getHeader(BaeEnv.BAE_ENV_AK);
        pass = req.getHeader(BaeEnv.BAE_ENV_SK);
        hostMg = req.getHeader(BaeEnv.BAE_ENV_ADDR_MONGO_IP);
        portMg = Integer.parseInt(req.getHeader(BaeEnv.BAE_ENV_ADDR_MONGO_PORT));

        hostRedis = req.getHeader(BaeEnv.BAE_ENV_ADDR_REDIS_IP);
        portRedis = Integer.parseInt(req.getHeader(BaeEnv.BAE_ENV_ADDR_REDIS_PORT));

        hostSql = req.getHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
        portSql = req.getHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);

        String p = req.getParameter("s");
        if (p != null && p.equals("mongo")) {
            resp.getWriter().append(user + '\n' + hostMg + '\n' + portMg + '\n');
            try {
                resp.getWriter().append(mongoDbT());
            } catch (UnknownHostException e) {
                resp.getWriter().append("unknownhostexception");
                e.printStackTrace(resp.getWriter());
            } catch (RuntimeException e) {
                resp.getWriter().append("runtime exception");
                e.printStackTrace(resp.getWriter());
            }
        } else if (p != null && p.equals("redis")) {
            resp.getWriter().append(hostRedis + '\n' + portRedis + '\n');
            try {
                resp.getWriter().append(redisT());
            } catch (RuntimeException e) {
                resp.getWriter().append("runtime exception");
                e.printStackTrace(resp.getWriter());
            }
        } else if (p != null && p.equals("cache")) {
            try {
                resp.getWriter().append(memT());
            } catch (RuntimeException e) {
                resp.getWriter().append("runtime exception");
                e.printStackTrace(resp.getWriter());
            }
        } else if (p != null && p.equals("sql")) {
            try {
                resp.getWriter().append(sqlT());
            } catch (RuntimeException e) {
                resp.getWriter().append("runtime exception");
                e.printStackTrace(resp.getWriter());
            }
        } else if (p != null && p.equals("counter")) {
            try {
                resp.getWriter().append(counterT());
            } catch (RuntimeException e) {
                resp.getWriter().append("runtime exception");
                e.printStackTrace(resp.getWriter());
            }
        }
        resp.getWriter().close();
    }

    protected String mongoDbT() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new ServerAddress(hostMg, portMg), Arrays.asList(MongoCredential.createMongoCRCredential(user, mongoDbName, pass.toCharArray())), new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build());
        DB db = mongoClient.getDB(mongoDbName);
        db.authenticate(user, pass.toCharArray());
        return "" + db.getCollectionNames();
    }

    protected String redisT() {
        Jedis jedis = new Jedis(hostRedis, portRedis);
        jedis.connect();
        jedis.auth(user + '-' + pass + '-' + redisDbName);
        jedis.del("times");
        jedis.incr("temp");
        return "" + jedis.dbSize();
    }

    protected String memT() {
        BaeMemcachedClient cache = new BaeMemcachedClient();
        long a, b;
        cache.set("temp", 0);
        a = cache.incr("temp");
        b = cache.incr("temp", 5);
        return String.format("%04d,%04d\n", a, b);
    }

    protected String sqlT() {
        String server = String.format("jdbc:mysql://%s:%s/%s", hostSql, portSql, sqlDbName);
        StringBuilder sb = new StringBuilder();
        try {
            new Driver();
        } catch (SQLException e) {
            sb.append(e.getMessage());
            return sb.toString();
        }
        Connection connection;
        try {
            connection = DriverManager.getConnection(server, user, pass);
        } catch (SQLException e) {
            sb.append(e.getMessage());
            return sb.toString();
        }
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("select now();");
            if (resultSet.first()) {
                sb.append(resultSet.getObject(1));
            }
        } catch (SQLException e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

    protected String counterT() {
        BaeCounter baeCounter = BaeFactory.getBaeCounter();
        return "" + baeCounter.increase("sum");
    }

    protected String urlT() {

        return "";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}