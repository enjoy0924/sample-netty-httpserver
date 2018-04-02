package com.altas.gateway;

import com.altas.cache.redis.JedisTemplate;
import com.altas.gateway.core.HttpServer;
import com.altas.gateway.loader.GlobalConfig;
import com.altas.gateway.loader.HttpRequestHandlerLoader;
import com.altas.gateway.tars.TarsLoader;
import com.altas.gateway.utils.LoggerHelper;

import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {

        do {
            //1.加载配置文件
            boolean loadConfigSuccess = GlobalConfig.instance().loadConfig();
            if (!loadConfigSuccess) {
                LoggerHelper.error("load config [gateway.properties] failed!");
                break;
            }

            //2.加载redis集群配置
            JedisTemplate.instance().loadJedisConfig(GlobalConfig.instance().getJedisConfig());

            //3.加载tars
            boolean loadTars = TarsLoader.instance().loadTars();
            if(!loadTars){
                LoggerHelper.error("load tars failed! check if every object config is set at gateway.properties");
                break;
            }

            //4.初始化URL映射关系
            List<String> packageNames = Arrays.asList(GlobalConfig.instance().getPackageNames().split(";"));
            boolean loadUrlDictFromAnnotationSuccess = HttpRequestHandlerLoader.getInstance().loadUrlDictFromAnnotation(packageNames);
            if (!loadUrlDictFromAnnotationSuccess) {
                LoggerHelper.error("load url function mapping from annotation failed!");
                break;
            }

            //3.启动Http Server
            HttpServer server = new HttpServer(GlobalConfig.instance().getHttpServicePort(),
                    GlobalConfig.instance().getHttpServiceIoCount(),
                    GlobalConfig.instance().getHttpServiceWorkerCount(),
                    GlobalConfig.instance().getHttpServiceTcpBacklog(),
                    GlobalConfig.instance().getHttpServiceTcpLinger(),
                    GlobalConfig.instance().getHttpServiceTcpKeepAlive(),
                    GlobalConfig.instance().getHttpServiceTcpReuseAddress());
            boolean startSuccess =server.start();
            if (!startSuccess) {
                LoggerHelper.error("server start failed!");
                break;
            }

            LoggerHelper.info("server started on listen port " + GlobalConfig.instance().getHttpServicePort());

        }while (false);

    }
}
