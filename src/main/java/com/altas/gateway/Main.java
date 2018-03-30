package com.altas.gateway;

import com.altas.cache.redis.JedisTemplate;
import com.altas.component.GateWayObserverManager;
import com.altas.component.mq.RocketMQAdapter;
import com.altas.component.observer.NotifyObserver;
import com.alr.core.utils.LoggerHelper;
import com.alr.dto.DefaultSystemPaper;
import com.alr.dto.rc.generateQuestion.ResolveErrorQuestionLevel;
import com.altas.gateway.core.HttpServer;
import com.altas.gateway.loader.ConfigUtils;
import com.altas.gateway.loader.HttpRequestHandlerLoader;
import com.altas.gateway.tars.TarsLoader;
import com.altas.push.PushManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangy on 2017/6/30.
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {

        do {
            //1.加载配置文件
            boolean loadConfigSuccess = ConfigUtils.instance().loadConfig();
            if (!loadConfigSuccess) {
                LoggerHelper.error("load config [gateway.properties] failed!");
                break;
            }

            PushManager.instance().initialize();

            //2.加载redis集群配置
            JedisTemplate.instance().loadJedisConfig(ConfigUtils.instance().getJedisConfig());

            //5.初始化RocketMq
            GateWayObserverManager.instance().register(NotifyObserver.instance());

            RocketMQAdapter.initializeAdapter("", ConfigUtils.instance().getRocketMqConsumerGroup(), ConfigUtils.instance().getRocketMqNameSrvAddr());

            //3.加载tars
            boolean loadTars = TarsLoader.instance().loadTars();
            if(!loadTars){
                LoggerHelper.error("load tars failed! check if every object config is set at gateway.properties");
                break;
            }

            //4.初始化URL映射关系
            List<String> packageNames = Arrays.asList(ConfigUtils.instance().getPackageNames().split(";"));
            boolean loadUrlDictFromAnnotationSuccess = HttpRequestHandlerLoader.getInstance().loadUrlDictFromAnnotation(packageNames);
            if (!loadUrlDictFromAnnotationSuccess) {
                LoggerHelper.error("load url function mapping from annotation failed!");
                break;
            }

            boolean loadDefaultRapidCalcConf = ResolveErrorQuestionLevel.instance().initializeMeta();
            if(!loadDefaultRapidCalcConf){
                LoggerHelper.error("load default rapid calculate configure failed!");
                break;
            }

            boolean loadDefaultSystemPaper = DefaultSystemPaper.instance().initializeMeta();
            if(!loadDefaultSystemPaper){
                LoggerHelper.error("load default system paper failed!");
                break;
            }

            //3.启动Http Server
            HttpServer server = new HttpServer(ConfigUtils.instance().getHttpServicePort(),
                    ConfigUtils.instance().getHttpServiceIoCount(),
                    ConfigUtils.instance().getHttpServiceWorkerCount(),
                    ConfigUtils.instance().getHttpServiceTcpBacklog(),
                    ConfigUtils.instance().getHttpServiceTcpLinger(),
                    ConfigUtils.instance().getHttpServiceTcpKeepAlive(),
                    ConfigUtils.instance().getHttpServiceTcpReuseAddress());
            boolean startSuccess =server.start();
            if (!startSuccess) {
                LoggerHelper.error("server start failed!");
                break;
            }

            LoggerHelper.info("server started on listen port " + ConfigUtils.instance().getHttpServicePort());

        }while (false);

    }
}
