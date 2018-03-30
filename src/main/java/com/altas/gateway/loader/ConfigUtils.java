package com.altas.gateway.loader;


import com.altas.gateway.constant.CONST;
import com.altas.gateway.tars.TarsLoader;
import org.apache.log4j.PropertyConfigurator;
import com.altas.cache.redis.JedisConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by zhangy on 2017/7/7.
 */
public class ConfigUtils {

    private static ConfigUtils instance = new ConfigUtils();

    private boolean inited = false;
    private long idleTimeout;

    private ConfigUtils() {
    }

    private Properties properties = new Properties();

    public boolean loadConfig() {
        if(inited)
            return true;

        try {
            loadLog4j();
            String path = System.getProperty("user.dir");
            path += (File.separator + "etc" + File.separator + "gateway.properties");
            properties.load(new FileInputStream(path));
            inited = true;
            return true;
        } catch (Exception e) {
            inited = false;
            return false;
        }
    }

    public static ConfigUtils instance() {
        return instance;
    }

    public String getPackageNames() {
        return properties.getProperty("api.scan.packages");
    }

    public int getHttpServicePort() {
        return Integer.valueOf(properties.getProperty("server.http.port"));
    }

    public int getHttpServiceIoCount() {
        return Integer.valueOf(properties.getProperty("server.threads.io"));
    }

    public int getHttpServiceWorkerCount() {
        return Integer.valueOf(properties.getProperty("server.threads.worker"));
    }

    public int getHttpServiceTcpBacklog() {
        return Integer.valueOf(properties.getProperty("server.tcp.backlog"));
    }

    public int getHttpServiceTcpLinger() {
        return Integer.valueOf(properties.getProperty("server.tcp.linger"));
    }

    public boolean getHttpServiceTcpKeepAlive() {
        return Boolean.valueOf(properties.getProperty("server.tcp.keepAlive"));
    }

    public boolean getHttpServiceTcpReuseAddress() {
        return Boolean.valueOf(properties.getProperty("server.tcp.reuseAddr"));
    }

    public TarsLoader.TarsServantName getTarsUmservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.um"),"um");
    }

    public TarsLoader.TarsServantName getTarsLocservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.loc"),"loc");
    }

    public TarsLoader.TarsServantName getTarsGmservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.gm"),"gm");
    }

    public TarsLoader.TarsServantName getTarsGameQuestionServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.gameQuestion"),"gameQuestion");
    }

    public TarsLoader.TarsServantName getTarsGameCatalogServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.gameCatalog"),"gameCatalog");
    }

    public TarsLoader.TarsServantName getTarsGameServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.game"),"game");
    }

    public TarsLoader.TarsServantName getTarsLrservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.lr"),"lr");
    }

    public TarsLoader.TarsServantName getTarsGradeservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.grade"),"grade");
    }

    public TarsLoader.TarsServantName getTarsRcservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.rc"),"rc");
    }

    public TarsLoader.TarsServantName getTarsPqbservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.pqb"),"pqb");
    }

    public TarsLoader.TarsServantName getTarsQbuservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.qbu"),"qbu");
    }

    public TarsLoader.TarsServantName getTarsQusservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.qus"),"qus");
    }


    public TarsLoader.TarsServantName getTarsApprCommentServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.apprComment"),"apprComment");
    }

    public TarsLoader.TarsServantName getTarsConfServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.conf"),"conf");
    }

    public TarsLoader.TarsServantName getTarsFbServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.fb"),"fb");
    }

    public TarsLoader.TarsServantName getTarsRCQServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.rcq"),"rcq");
    }

    public TarsLoader.TarsServantName getTarsRqbservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.rqb"),"rqb");
    }

    public TarsLoader.TarsServantName getTarsShardservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.shard"),"shard");
    }

    public TarsLoader.TarsServantName getTarsMallservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.mall"),"mall");
    }

    public TarsLoader.TarsServantName getTarsTaskservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.task"),"task");
    }

    public TarsLoader.TarsServantName getTarsCreditsservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.credits"),"credits");
    }

    public TarsLoader.TarsServantName getTarsMtcExtendservantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.mtcExtend"),"mtcExtend");
    }

    public TarsLoader.TarsServantName getTarsMsServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.ms"),"ms");
    }

    public TarsLoader.TarsServantName getTarsPetServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.pet"),"pet");
    }

    public TarsLoader.TarsServantName getTarsPetCatalogServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.petCatalog"),"petCatalog");
    }

    public TarsLoader.TarsServantName getTarsTeachingResearchServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.teachingResearch"),"teachingResearch");
    }

    public TarsLoader.TarsServantName getTarsActivityServantsName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.activity"),"activity");
    }

    public TarsLoader.TarsServantName getTarsHandWritingNodeJSName() {
        return new TarsLoader.TarsServantName(properties.getProperty("server.tars.handWritingNodeJS"),"handWriting");
    }


    public String getKeyCertChainFileName() {
        return properties.getProperty("server.https.keyCertChainFile");
    }

    public String getKeyFile() {
        return properties.getProperty("server.https.keyFile");
    }

    public boolean getTcpServerOptionKeepAlive() {
        return Boolean.parseBoolean(properties.getProperty("tcpServer.ChannelOption.keepAlive"));
    }


    public Map<String, String> getAllPermissionMap() {
        Map<String, String> retMap = new ConcurrentHashMap<>();
        Pattern namePattern = Pattern.compile("permission.*name");
        String properNameStr, properClassStr;
        for (Map.Entry<Object, Object> properKey : properties.entrySet()) {
            properNameStr = properKey.getKey().toString();
            properClassStr = properNameStr.replace("name", "class");
            //找到权限名称将其的值作为key，权限class的值作为value
            if (namePattern.matcher(properNameStr).find() && null != properties.getProperty(properClassStr)) {
                retMap.put(properKey.getValue().toString(), properties.getProperty(properClassStr));
            }
        }
        return retMap;
    }

    private void loadLog4j() {
        String path = System.getProperty("user.dir");
        path += (File.separator + "etc" + File.separator + "log4j.properties");
        PropertyConfigurator.configure(path);
    }

    public JedisConfig getJedisConfig() {

        JedisConfig jedisConfig = new JedisConfig();
        jedisConfig.setNodes(properties.getProperty("server.cache.redis.nodes"));

        String value = properties.getProperty("server.cache.redis.connectionTimeout");
        if (null != value && value.matches("[0-9]+"))
            jedisConfig.setConnectionTimeout(Integer.valueOf(value));

        value = properties.getProperty("server.cache.redis.expireSeconds");
        if (null != value && value.matches("[0-9]+"))
            jedisConfig.setExpireSeconds(Integer.valueOf(value));

        value = properties.getProperty("server.cache.redis.maxAttempts");
        if (null != value && value.matches("[0-9]+"))
            jedisConfig.setMaxAttempts(Integer.valueOf(value));

        value = properties.getProperty("server.cache.redis.soTimeout");
        jedisConfig.setSoTimeout(Integer.valueOf(value));

        jedisConfig.setPassword(properties.getProperty("server.cache.redis.password"));

        return jedisConfig;
    }

    public boolean isProdEnv(){
        String mode = properties.getProperty("server.mode");
        if(null == mode || !mode.equalsIgnoreCase("prod")){
            return false;
        }

        if(mode.equalsIgnoreCase("prod"))
            return true;
        else
            return false;
    }

    public String getXingePushAccessId(int env){
        if(env == CONST.XINGE_ENV_IOS_DEV){
            return properties.getProperty("push.xinge.ios.dev.accessId");
        }else if(env == CONST.XINGE_ENV_IOS_PROD){
            return properties.getProperty("push.xinge.ios.prod.accessId");
        }else if(env == CONST.XINGE_ENV_ANDROID_DEV){
            return properties.getProperty("push.xinge.android.dev.accessId");
        }else if(env == CONST.XINGE_ENV_ANDROID_PROD){
            return properties.getProperty("push.xinge.android.prod.accessId");
        }else {
            return "";
        }
    }

    public String getXingePushSecretKey(int env){
        if(env == CONST.XINGE_ENV_IOS_DEV){
            return properties.getProperty("push.xinge.ios.dev.secretKey");
        }else if(env == CONST.XINGE_ENV_IOS_PROD){
            return properties.getProperty("push.xinge.ios.prod.secretKey");
        }else if(env == CONST.XINGE_ENV_ANDROID_DEV){
            return properties.getProperty("push.xinge.android.dev.secretKey");
        }else if(env == CONST.XINGE_ENV_ANDROID_PROD){
            return properties.getProperty("push.xinge.android.prod.secretKey");
        }else {
            return "";
        }
    }

    public boolean getXingePushEnable() {

        String fakeSmsEnable = properties.getProperty("push.xinge.enable");
        if (fakeSmsEnable == null || fakeSmsEnable.isEmpty()) {
            return false;
        }
        Boolean isFakeSmsEnable = Boolean.valueOf(fakeSmsEnable);
        return isFakeSmsEnable;
    }


    public String getSMSCCPURL() {
        return properties.getProperty("sms.ccp.url");
    }

    public String getSMSCCPPort() {
        return properties.getProperty("sms.ccp.port");
    }


    public String getSMSCCPSID() {
        return properties.getProperty("sms.ccp.sid");
    }


    public String getSMSCCPToken() {
        return properties.getProperty("sms.ccp.token");
    }


    public String getSMSCCPValAppId() {
        return properties.getProperty("sms.ccp.validate.appId");
    }


    public String getSMSCCPValRegTemplateId() {
        return properties.getProperty("sms.ccp.validate.register.templateId");
    }


    public String getSMSCCPValExpTime() {
        return properties.getProperty("sms.ccp.validate.expiredTime");
    }

    public int getDefaultPageSize() {
        return 10;
    }

    public boolean fakeSmsEnable() {

        String fakeSmsEnable = properties.getProperty("sms.fake.enable");
        Boolean isFakeSmsEnable = Boolean.valueOf(fakeSmsEnable);
        return isFakeSmsEnable;
    }

    public int getMaxNormalGroupCount() {

        String maxClass = properties.getProperty("gm.nromalClass.max");
        return Integer.parseInt(maxClass);
    }

    public String getTarsLocator() {
        return properties.getProperty("server.tars.locator");
    }

    public String getRocketMqConsumerGroup() {
        return properties.getProperty("rocketmq.consumer.group");
    }

    public String getRocketMqNameSrvAddr(){
        return properties.getProperty("rocketmq.namesrv.address");
    }

    public  int getTarsSyncInvokeTimeOut() {
        String timeout = properties.getProperty("tars.sync.timeout");
        if (null == timeout || !timeout.matches("[0-9]+"))
            return 5000;
        return Integer.valueOf(timeout.trim());
    }

    public long getIdleTimeout() {

        String timeout = properties.getProperty("server.tcp.idleTimeout");
        if (null == timeout || !timeout.matches("[0-9]+"))
            return 30;
        return Integer.valueOf(timeout.trim());
    }
}
