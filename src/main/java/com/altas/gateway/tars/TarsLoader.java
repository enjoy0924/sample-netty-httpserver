package com.altas.gateway.tars;

import com.altas.exception.TarsNotInitializeException;
import com.altas.exception.TarsObjNotFoundException;
import com.altas.exception.TarsSliceException;
import com.altas.gateway.constant.CONST;
import com.altas.gateway.loader.GlobalConfig;
import com.altas.gateway.tars.umservants.UmPrx;
import com.qq.tars.client.Communicator;
import com.qq.tars.client.CommunicatorConfig;
import com.qq.tars.client.CommunicatorFactory;
import com.qq.tars.client.ServantProxyConfig;
import com.qq.tars.common.support.Holder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TarsLoader {

    private  static  TarsLoader tarsLoader=new TarsLoader();

    private Map<Class<?>, TarsServantName> prxCls2TarsObj = new ConcurrentHashMap<>();
    private Communicator communicator;

    private TarsLoader() {
    }

    public  static TarsLoader instance(){
        return  tarsLoader;
    }

    public synchronized boolean loadTars() {
        GlobalConfig.instance().loadConfig();
        registerTarsObj();

        return true;
    }

    private void registerTarsObj(){
        prxCls2TarsObj.put(UmPrx.class, GlobalConfig.instance().getTarsUmservantsName());
        //TODO more tars server register here
    }

    private String getTarsObjectByPrxCls(Class<?> cls){
        TarsServantName object = prxCls2TarsObj.get(cls);
        if(null == object || null == object.getName() || object.getName().trim().isEmpty())
            throw new TarsObjNotFoundException("tars " +cls.toString()+ " object not found");

        return object.getName();
    }

    public <T> T instanceOfPrx(Class<T> cls){
        Communicator communicator = getCommunicator();
        if(null == communicator)
            throw new TarsNotInitializeException("tars communicator not initialized");

        return communicator.stringToProxy(cls, getTarsObjectByPrxCls(cls));
    }

    private ServantProxyConfig getServantProxyConfig(String objName, String setDivision) {
        ServantProxyConfig servantProxyConfig = new ServantProxyConfig(objName);
        boolean enableSet = (null != setDivision && !setDivision.trim().isEmpty());
        if(enableSet){
            servantProxyConfig.setEnableSet(true);
            servantProxyConfig.setSetDivision(setDivision);
        }

        return servantProxyConfig;
    }

    private synchronized Communicator getCommunicator() {
        if (null == communicator) {
            communicator = CommunicatorFactory.getInstance().getCommunicator(GlobalConfig.instance().getTarsLocator());
            CommunicatorConfig communicatorConfig = communicator.getCommunicatorConfig();
            communicatorConfig.setSyncInvokeTimeout(GlobalConfig.instance().getTarsSyncInvokeTimeOut());
            communicatorConfig.setRefreshEndpointInterval(300000); //默认5分钟刷新一次
        }
        return communicator;
    }

//    private synchronized Communicator getCommunicatorByObjNameAndSet(String tarsObjName, String setDivision) {
//
//        String complexKey = tarsObjName;
//        boolean enableSet = (null != setDivision && !setDivision.trim().isEmpty());
//        if(enableSet)
//            complexKey= (complexKey+"@"+setDivision);
//        Communicator communicator = setDivision2Communicator.get(complexKey);
//        if(null == communicator){
//            communicator = CommunicatorFactory.getInstance().getCommunicator(GlobalConfig.instance().getTarsLocator());
//            CommunicatorConfig communicatorConfig = communicator.getCommunicatorConfig();
//            if(enableSet) {
//                communicatorConfig.setEnableSet(true);
//                communicatorConfig.setSetDivision(setDivision);
//            }
//            communicatorConfig.setRefreshEndpointInterval(300000); //默认5分钟刷新一次
//            setDivision2Communicator.put(complexKey, communicator);
//        }
//
//        return communicator;
//    }

    public static class TarsServantName{
        private String name;
        private String module;

        public TarsServantName(String name, String module) {
            this.name = name;
            this.module = module;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }
    }
}
