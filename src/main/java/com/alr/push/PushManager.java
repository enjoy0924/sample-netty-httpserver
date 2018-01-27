package com.alr.push;

import com.alr.core.utils.LoggerHelper;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.loader.ConfigUtils;
import com.alr.gateway.tars.umservants.Account;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

import java.util.List;

/**
 * 推送信息到APP的管理类
 *
 */
public class PushManager {

    private static PushManager pushManager;

    private XingeApp androidDev;
    private XingeApp androidProd;
    private XingeApp iosDev;
    private XingeApp iosProd;


    public synchronized static PushManager instance(){
        if(null == pushManager)
            pushManager = new PushManager();

        return pushManager;
    }

    public synchronized void initialize(){

        if(!ConfigUtils.instance().isProdEnv()) {
            if(null == androidDev) {
                androidDev = new XingeApp(
                        Long.valueOf(ConfigUtils.instance().getXingePushAccessId(CONST.XINGE_ENV_ANDROID_DEV)),
                        ConfigUtils.instance().getXingePushSecretKey(CONST.XINGE_ENV_ANDROID_DEV)
                );
            }
            if(null == iosDev){
                iosDev = new XingeApp(
                        Long.valueOf(ConfigUtils.instance().getXingePushAccessId(CONST.XINGE_ENV_IOS_DEV)),
                        ConfigUtils.instance().getXingePushSecretKey(CONST.XINGE_ENV_IOS_DEV)
                );
            }
        }else {
            if(null == androidProd) {
                androidProd = new XingeApp(
                        Long.valueOf(ConfigUtils.instance().getXingePushAccessId(CONST.XINGE_ENV_ANDROID_PROD)),
                        ConfigUtils.instance().getXingePushSecretKey(CONST.XINGE_ENV_ANDROID_PROD)
                );
            }
            if(null == iosProd){
                iosProd = new XingeApp(
                        Long.valueOf(ConfigUtils.instance().getXingePushAccessId(CONST.XINGE_ENV_IOS_PROD)),
                        ConfigUtils.instance().getXingePushSecretKey(CONST.XINGE_ENV_IOS_PROD)
                );
            }
        }
    }

    public void notifyNewPaperByStuAccountsAndGroupIdAndPaperName(List<Account> accounts, String groupId, String paperName) {

        if(ConfigUtils.instance().getXingePushEnable()) {
            try {
                boolean isProdEnv = ConfigUtils.instance().isProdEnv();
                String title = "你的老师布置了新作业";
                String messageTemplate = "亲爱的[%s]同学，你的老师在[%s]班级，布置了作业[%s],抓紧时间完成吧!";
                for (Account account : accounts) {

                    String content = String.format(messageTemplate, account.getRealname(), groupId, paperName);
                    String lowerCaseAccount = account.getUsername().toLowerCase();
                    if (!isProdEnv) {   //发送正式环境通知
                        androidDev.pushSingleAccount(0, lowerCaseAccount, getAndroidTeacherDeliverMessage(title, content));
                        iosDev.pushSingleAccount(0, lowerCaseAccount, getIosTeacherDeliverMessage(content), XingeApp.IOSENV_DEV);
                    } else {
                        androidProd.pushSingleAccount(0, lowerCaseAccount, getAndroidTeacherDeliverMessage(title, content));
                        iosProd.pushSingleAccount(0, lowerCaseAccount, getIosTeacherDeliverMessage(content), XingeApp.IOSENV_PROD);
                    }
                }
            }catch (Exception ex) {
                LoggerHelper.error(ex.getMessage(), ex);
            }
        }
    }

    private MessageIOS getIosTeacherDeliverMessage(String content) {

        MessageIOS message = new MessageIOS();
        message.setAlert(content);
        message.setBadge(1);
        message.setSound("beep.wav");

        return message;
    }

    private Message getAndroidTeacherDeliverMessage(String title, String content) {
        Message message = new Message();
        message.setExpireTime(86400);
        message.setTitle(title);
        message.setContent(content);
        message.setType(Message.TYPE_MESSAGE);
        return  message;
    }
}
