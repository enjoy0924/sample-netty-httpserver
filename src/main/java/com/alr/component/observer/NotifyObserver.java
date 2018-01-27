package com.alr.component.observer;

import com.alr.component.mq.RocketMQAdapter;
import com.alr.core.utils.JsonHelper;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.tars.activityservants.LoginSuccessInfo;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * Created by G_dragon on 2017/7/19.
 */
public class NotifyObserver implements GateWayObserver {


    private static NotifyObserver notifyObserverInstance = new NotifyObserver();
    public static NotifyObserver instance(){
        if(null == notifyObserverInstance)
            notifyObserverInstance = new NotifyObserver();

        return notifyObserverInstance;
    }
    public NotifyObserver() {
    }

    @Override
    public void handleLoginSuccess(LoginSuccessInfo loginSuccessInfo) {

        Message message = _generatePaperGoldenCupRocketMessageByObject(loginSuccessInfo);

        if (null == message) return;

        RocketMQAdapter.Producer.AsyncSend(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                SendStatus sendStatus = sendResult.getSendStatus();
                if (sendStatus != SendStatus.SEND_OK) {
                    //消息发送状态不对
                }
            }

            @Override
            public void onException(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private Message _generatePaperGoldenCupRocketMessageByObject(LoginSuccessInfo loginSuccessInfo) {

        String complexKey = loginSuccessInfo.getId();
        String textMsg = JsonHelper.allToJson(loginSuccessInfo);

        try {
            return new Message(CONST.MQ_TOPIC_GATE_WAY, CONST.MQ_TAG_GATE_WAY_LOGIN_SUCCESS,
                    complexKey, textMsg.getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
