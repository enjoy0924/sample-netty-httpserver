package com.altas.sms;

/**
 * Created by chengang on 2017/7/16.
 */
public class SMSFactory {


    static CCPSMSServiceProvider  ccpsmsServiceProvider = new CCPSMSServiceProvider();

    public static CCPSMSServiceProvider getSMSSP(){

        //目前只有容联-云通信
        return  ccpsmsServiceProvider;

    }


}
