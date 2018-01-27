package com.alr.component;


import com.alr.component.observer.GateWayObserver;
import com.alr.gateway.tars.activityservants.LoginSuccessInfo;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by G_dragon on 2017/7/19.
 */
public class GateWayObserverManager {
    private static GateWayObserverManager gateWayObserverManager = new GateWayObserverManager();

    public static GateWayObserverManager instance(){
        return gateWayObserverManager;
    }

    ConcurrentLinkedQueue<GateWayObserver> gateWayObservers = new ConcurrentLinkedQueue<>();

    public void register(GateWayObserver observer){
        gateWayObservers.add(observer);
    }


    public void notifyLoginSuccess(LoginSuccessInfo loginSuccessInfo) {
        for(GateWayObserver observer : gateWayObservers){
            observer.handleLoginSuccess(loginSuccessInfo);
        }
    }
}
