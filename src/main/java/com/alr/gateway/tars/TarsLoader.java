package com.alr.gateway.tars;

import com.alr.exception.TarsNotInitializeException;
import com.alr.exception.TarsObjNotFoundException;
import com.alr.exception.TarsSliceException;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.loader.ConfigUtils;
import com.alr.gateway.tars.activityservants.ActivityPrx;
import com.alr.gateway.tars.apprcommentservants.ApprCommentPrx;
import com.alr.gateway.tars.confservants.ConfPrx;
import com.alr.gateway.tars.creditsservants.CreditsPrx;
import com.alr.gateway.tars.fbservants.FbPrx;
import com.alr.gateway.tars.gamecatalogservants.GameCatalogPrx;
import com.alr.gateway.tars.gamequestionservant.GameQuestionPrx;
import com.alr.gateway.tars.gameservants.GamePrx;
import com.alr.gateway.tars.gmservants.GmPrx;
import com.alr.gateway.tars.handwritingnodejsservant.HandWritingNodeJSPrx;
import com.alr.gateway.tars.locservants.LocationPrx;
import com.alr.gateway.tars.lrservants.LifeRecordPrx;
import com.alr.gateway.tars.mallservants.MallPrx;
import com.alr.gateway.tars.msservants.MsPrx;
import com.alr.gateway.tars.petcatalogservants.PetCatalogPrx;
import com.alr.gateway.tars.petservants.PetPrx;
import com.alr.gateway.tars.pqbservants.PqbPrx;
import com.alr.gateway.tars.qbuservants.QbuPrx;
import com.alr.gateway.tars.qusservants.QusPrx;
import com.alr.gateway.tars.rcquestionservant.RCQuestionPrx;
import com.alr.gateway.tars.rcservants.RcPrx;
import com.alr.gateway.tars.rqbservants.RqbPrx;
import com.alr.gateway.tars.shardservants.ShardAddress;
import com.alr.gateway.tars.shardservants.ShardPrx;
import com.alr.gateway.tars.shardservants.ShardServantsConst;
import com.alr.gateway.tars.taskservants.TaskPrx;
import com.alr.gateway.tars.teachingresearchservants.TeachingResearchPrx;
import com.alr.gateway.tars.umservants.UmPrx;
import com.qq.tars.client.Communicator;
import com.qq.tars.client.CommunicatorConfig;
import com.qq.tars.client.CommunicatorFactory;
import com.qq.tars.client.ServantProxyConfig;
import com.qq.tars.common.support.Holder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangy on 2017/7/12.
 */
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
        ConfigUtils.instance().loadConfig();
        registerTarsObj();

        return true;
    }

    private void registerTarsObj(){
        prxCls2TarsObj.put(UmPrx.class, ConfigUtils.instance().getTarsUmservantsName());
        prxCls2TarsObj.put(GamePrx.class, ConfigUtils.instance().getTarsGameServantsName());
        prxCls2TarsObj.put(GameCatalogPrx.class, ConfigUtils.instance().getTarsGameCatalogServantsName());
        prxCls2TarsObj.put(RcPrx.class, ConfigUtils.instance().getTarsRcservantsName());
        prxCls2TarsObj.put(RqbPrx.class, ConfigUtils.instance().getTarsRqbservantsName());
        prxCls2TarsObj.put(LocationPrx.class, ConfigUtils.instance().getTarsLocservantsName());
        prxCls2TarsObj.put(ShardPrx.class, ConfigUtils.instance().getTarsShardservantsName());
        prxCls2TarsObj.put(LifeRecordPrx.class, ConfigUtils.instance().getTarsLrservantsName());
        prxCls2TarsObj.put(PqbPrx.class, ConfigUtils.instance().getTarsPqbservantsName());
        prxCls2TarsObj.put(QbuPrx.class, ConfigUtils.instance().getTarsQbuservantsName());
        prxCls2TarsObj.put(QusPrx.class, ConfigUtils.instance().getTarsQusservantsName());
        prxCls2TarsObj.put(MallPrx.class, ConfigUtils.instance().getTarsMallservantsName());
        prxCls2TarsObj.put(TaskPrx.class, ConfigUtils.instance().getTarsTaskservantsName());
        prxCls2TarsObj.put(CreditsPrx.class, ConfigUtils.instance().getTarsCreditsservantsName());
        prxCls2TarsObj.put(GmPrx.class, ConfigUtils.instance().getTarsGmservantsName());
        prxCls2TarsObj.put(ApprCommentPrx.class, ConfigUtils.instance().getTarsApprCommentServantsName());
        prxCls2TarsObj.put(GameQuestionPrx.class, ConfigUtils.instance().getTarsGameQuestionServantsName());
        prxCls2TarsObj.put(ConfPrx.class, ConfigUtils.instance().getTarsConfServantsName());
        prxCls2TarsObj.put(FbPrx.class, ConfigUtils.instance().getTarsFbServantsName());
        prxCls2TarsObj.put(RCQuestionPrx.class, ConfigUtils.instance().getTarsRCQServantsName());
        prxCls2TarsObj.put(MsPrx.class, ConfigUtils.instance().getTarsMsServantsName());
        prxCls2TarsObj.put(PetPrx.class, ConfigUtils.instance().getTarsPetServantsName());
        prxCls2TarsObj.put(PetCatalogPrx.class, ConfigUtils.instance().getTarsPetCatalogServantsName());
        prxCls2TarsObj.put(TeachingResearchPrx.class, ConfigUtils.instance().getTarsTeachingResearchServantsName());
        prxCls2TarsObj.put(ActivityPrx.class, ConfigUtils.instance().getTarsActivityServantsName());
        prxCls2TarsObj.put(HandWritingNodeJSPrx.class, ConfigUtils.instance().getTarsHandWritingNodeJSName());
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

    public <T> T instanceOfSlicePrx(Class<T> cls, String shardingId){
        return instanceOfSlicePrxWithType(cls, shardingId, null);
    }

    public <T> T instanceOfSlicePrxWithType(Class<T> cls, String shardingId, Holder<Integer> typeHolder){
        TarsServantName tarsServantName = prxCls2TarsObj.get(cls);
        if(null == tarsServantName)
            return null;

        String tarsObjName = tarsServantName.getName();
        if(null == tarsObjName || tarsObjName.trim().isEmpty()) {
            Holder<ShardAddress> shardAddressHolder = new Holder<>();
            int error = instanceOfPrx(ShardPrx.class).getShardingRoute(shardingId, tarsServantName.getModule(), shardAddressHolder);
            if (error != ShardServantsConst.ERROR_CODE_OK || null == shardAddressHolder.getValue()) {
                throw new TarsSliceException("get " + tarsServantName.getModule() + " slice error occour !");
            }

            String router = shardAddressHolder.getValue().getHost();
            if (null == router || router.trim().isEmpty())
                return null;


            String[] routers = router.split("@");
            String set = "";
            if (routers.length > 1)
                set = routers[1];
            String objName = routers[0];
            if(null != typeHolder){
                if(objName.contains("QbuBDServer")){
                    typeHolder.setValue(CONST.PROXY_QBU_FOR_BIG_DATA);
                }else {
                    typeHolder.setValue(CONST.PROXY_QBU_FOR_MYSQL);
                }
            }

            ServantProxyConfig servantProxyConfig = getServantProxyConfig(objName, set);
            return getCommunicator().stringToProxy(cls, servantProxyConfig);
        }else {
            return getCommunicator().stringToProxy(cls, tarsObjName);
        }
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
            communicator = CommunicatorFactory.getInstance().getCommunicator(ConfigUtils.instance().getTarsLocator());
            CommunicatorConfig communicatorConfig = communicator.getCommunicatorConfig();
            communicatorConfig.setSyncInvokeTimeout(ConfigUtils.instance().getTarsSyncInvokeTimeOut());
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
//            communicator = CommunicatorFactory.getInstance().getCommunicator(ConfigUtils.instance().getTarsLocator());
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
