package com.alr.component.observer;


import com.alr.gateway.tars.activityservants.LoginSuccessInfo;

/**
 * Created by G_dragon on 2017/7/19.
 */
public interface  GateWayObserver {

    void handleLoginSuccess(LoginSuccessInfo loginSuccessInfo);
}
