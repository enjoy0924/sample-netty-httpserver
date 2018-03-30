package com.altas.gateway.session;

import com.alr.api.ResultDictionary;
import com.alr.core.utils.JsonHelper;
import com.alr.core.utils.SerializeHelper;
import com.alr.dto.task.TeacherRecommendDetailsItem;
import org.junit.Test;

import java.util.*;

/**
 * Created by zhangy on 2017/7/13.
 */
public class SerializeUtilsTest {
    @Test
    public void deserialize() throws Exception {
    }

    @Test
    public void serialize() throws Exception {
        try {
            TeacherRecommendDetailsItem teacherRecommendDetailsItem =new TeacherRecommendDetailsItem();
            teacherRecommendDetailsItem.setAwardId("123");
            teacherRecommendDetailsItem.setJoinStudentCount(12);
            teacherRecommendDetailsItem.setTargetStudentCount(20);
            teacherRecommendDetailsItem.setName("zaa");
            teacherRecommendDetailsItem.setPhone("135****5655");

            List<TeacherRecommendDetailsItem> list=new ArrayList<>();
            list.add(teacherRecommendDetailsItem);

            ResultDictionary resultDictionary=ResultDictionary.OK();
            resultDictionary.insertElem("result",list);
            String j= JsonHelper.allToJson(resultDictionary);

//            TeacherInviteDetails details=new TeacherInviteDetails();
//            details.setAwardId("abcsdsd");
//            details.setAwardCredits(200);
//
//            List<TeacherInviteDetailsItem> list=new ArrayList<>();
//
//            TeacherInviteDetailsItem teacherInviteDetailsItem=new TeacherInviteDetailsItem();
//            teacherInviteDetailsItem.setClassId("336541");
//            teacherInviteDetailsItem.setClassName("三年二班");
//            teacherInviteDetailsItem.setCount(13);
//            list.add(teacherInviteDetailsItem);
//            details.setClassInfo(list);
//
//            String j= JsonHelper.allToJson(details);

            Session session = new Session("sdasd");
            String abc = SerializeHelper.serialize(session);

            Session session1 = (Session) SerializeHelper.deserialize(abc);

            System.out.print("");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}