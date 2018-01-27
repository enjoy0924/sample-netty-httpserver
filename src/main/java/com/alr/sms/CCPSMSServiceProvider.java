package com.alr.sms;

import com.alr.gateway.loader.ConfigUtils;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;
import com.cloopen.rest.sdk.CCPRestSmsSDK;

import java.util.Map;

/**
 * Author: syd
 * Date:2017/7/16
 * Description:容联：云通信服务提供商
 */
public class CCPSMSServiceProvider{

    CCPRestSmsSDK ccpRestSmsSDK = null;
    String url = ConfigUtils.instance().getSMSCCPURL();
    String port = ConfigUtils.instance().getSMSCCPPort();
    String accountSid = ConfigUtils.instance().getSMSCCPSID();
    String accountToken = ConfigUtils.instance().getSMSCCPToken();

    public CCPSMSServiceProvider() {
        ccpRestSmsSDK = new CCPRestSmsSDK();
        ccpRestSmsSDK.init(url, port);
        ccpRestSmsSDK.setAccount(accountSid, accountToken);
    }



    public int sendMsg(String telephone, String appId, String templateId, String[] datas){

        ccpRestSmsSDK.setAppId(appId);
        Map<String,Object> result = ccpRestSmsSDK.sendTemplateSMS(telephone,templateId,datas);
        if(result.get("statusCode").equals("000000"))
            return GlobalServantsConst.ERROR_CODE_OK;

        return GlobalServantsConst.ERROR_CODE_UNKNOWN;
    }





}
