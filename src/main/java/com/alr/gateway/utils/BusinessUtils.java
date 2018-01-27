package com.alr.gateway.utils;

import com.allere.qbu.grade.CONSTANT;
import com.allere.qbu.grade.GradeConvertUtil;
import com.allere.qbu.grade.GradeUtil;
import com.allere.qbu.grade.pojo.GradeScorePointAnswer;
import com.allere.qbu.grade.pojo.ScorePoint;
import com.allere.qbu.grade.pojo.commonQue.ScorePointAnswer;
import com.alr.core.utils.*;
import com.alr.dto.*;
import com.alr.dto.PaperAnalyzed;
import com.alr.dto.credits.TeacherCreditsRecord;
import com.alr.dto.mall.RefundDesc;
import com.alr.dto.ms.ExcellentAnswer;
import com.alr.dto.pqb.FavoritePaperItemDto;
import com.alr.dto.rqb.PageDto;
import com.alr.dto.rqb.QuestionDto;
import com.alr.dto.rqb.QuestionForQusDto;
import com.alr.dto.rqb.SimpleTagDto;
import com.alr.dto.task.*;
import com.alr.dto.teachingResearch.*;
import com.alr.dto.um.ParentMessageDTO;
import com.alr.dto.um.StudentMessageDTO;
import com.alr.dto.um.TeacherDto;
import com.alr.dto.um.TeacherMessageDTO;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.tars.activityservants.LoginSuccessInfo;
import com.alr.gateway.tars.activityservants.TrgPromotion;
import com.alr.gateway.tars.creditsservants.CreditsHistory;
import com.alr.gateway.tars.globalservants.*;
import com.alr.gateway.tars.globalservants.PaperAnalyzedKnowledgeQuestion;
import com.alr.gateway.tars.globalservants.PaperAnalyzedQuestion;
import com.alr.gateway.tars.gmservants.*;
import com.alr.gateway.tars.locservants.*;
import com.alr.gateway.tars.mallservants.RefundItem;
import com.alr.gateway.tars.pqbservants.FavoritePaperItem;
import com.alr.gateway.tars.qbuservants.*;
import com.alr.gateway.tars.rqbservants.*;
import com.alr.gateway.tars.rqbservants.QuestionPageItem;
import com.alr.gateway.tars.taskservants.*;
import com.alr.gateway.tars.umservants.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Dengxl on 2017/8/2.
 */
public class BusinessUtils {

    private static Map<Integer, String> code2MessageDictionary = new ConcurrentHashMap<>();

    public static boolean isTrSupperUser = false;


    public static boolean validateTelephone(String telNum) {
        if (telNum == null) {
            return false;
        }

        boolean matched = telNum.matches("1[0-9]{10}");

        return matched;
    }

    public static String findRegularRoleByCharRole(String charRole) {

        if (charRole.equalsIgnoreCase("s")) {
            return UmServantsConst.ROLE_STUDENT;
        } else if (charRole.equalsIgnoreCase("t")) {
            return UmServantsConst.ROLE_TEACHER;
        } else if (charRole.equalsIgnoreCase("p") || charRole.equalsIgnoreCase("g")) {
            return UmServantsConst.ROLE_FIRST_GUARDIAN;
        } else {
            return "";
        }
    }

    public static Character getLvlByStuId(String sid, Map<String, List<String>> lvl2StuIds) {
        for (Map.Entry<String, List<String>> entry : lvl2StuIds.entrySet()) {
            List<String> sids = entry.getValue();
            if (sids.contains(sid))
                return entry.getKey().charAt(0);
        }

        return 'A';
    }

    public static List<String> getSidByLvl2StuAndGrpRel(Map<String, List<StudentIdAndGrpRelId>> lvl2StuAndGrpRel) {
        if (null == lvl2StuAndGrpRel || lvl2StuAndGrpRel.isEmpty())
            return Collections.emptyList();

        List<String> sids = new ArrayList<>();
        for (Map.Entry<String, List<StudentIdAndGrpRelId>> entry : lvl2StuAndGrpRel.entrySet()) {
            sids.addAll(entry.getValue().stream().map(StudentIdAndGrpRelId::getStudentId).collect(Collectors.toList()));
        }
        return sids;
    }

    public static List<String> getSidByLvl2Stu(Map<String, List<String>> lvl2StuId) {
        if (null == lvl2StuId || lvl2StuId.isEmpty())
            return Collections.emptyList();

        List<String> sids = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : lvl2StuId.entrySet()) {
            sids.addAll(entry.getValue());
        }
        return sids;
    }

    private static SimpleUser _convertToSimpleUserByAccount(Account account) {

        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setGender(account.getGender());
        simpleUser.setLoginName(account.getUsername());
        simpleUser.setName(account.getRealname());

        return simpleUser;
    }

    public static List<SimpleUser> convertListOfSimpleUserByAccounts(List<Account> accounts) {
        if (null == accounts || accounts.isEmpty())
            return Collections.emptyList();

        List<SimpleUser> simpleUsers = new ArrayList<>();
        for (Account account : accounts) {
            SimpleUser simpleUser = _convertToSimpleUserByAccount(account);
            simpleUsers.add(simpleUser);
        }

        return simpleUsers;
    }

    private static synchronized void _initialCodeMessage() {
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_UNKNOWN, GlobalServantsConst.MSG_UNKNOWN);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_OK, GlobalServantsConst.MSG_OK);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PARAMETERS_NULL, GlobalServantsConst.MSG_PARAMETERS_NULL);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PARAMETERS_ERROR, GlobalServantsConst.MSG_PARAMETERS_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NO_RESULT, GlobalServantsConst.MSG_NO_RESULT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_VALIDATE_IMG_CODE, GlobalServantsConst.MSG_VALIDATE_IMG_CODE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_VALIDATE_SMS_CODE, GlobalServantsConst.MSG_VALIDATE_SMS_CODE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_SEND_SHORT_MESSAGE_ERROR, GlobalServantsConst.MSG_SEND_SHORT_MESSAGE_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NEED_VALIDATE_TEL, GlobalServantsConst.MSG_NEED_VALIDATE_TEL);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TELEPHONE_FORMAT_ERROR, GlobalServantsConst.MSG_TELEPHONE_FORMAT_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_SEND_MESSAGE_TOO_FREQUENT, GlobalServantsConst.MSG_SEND_MESSAGE_TOO_FREQUENT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NEED_AUTHORIZATION, GlobalServantsConst.MSG_NEED_AUTHORIZATION);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_FIRST_LOGIN, GlobalServantsConst.MSG_FIRST_LOGIN);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_USERNAME_OR_PASSWORD_ERROR, GlobalServantsConst.MSG_USERNAME_OR_PASSWORD_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_USER_SESSION_HAS_LOST, GlobalServantsConst.MSG_USER_SESSION_HAS_LOST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_LOGIN_NAME_NOT_EXIST, GlobalServantsConst.MSG_LOGIN_NAME_NOT_EXIST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PASSWORD_PROTECTION_ANSWER_ERROR, GlobalServantsConst.MSG_PASSWORD_PROTECTION_ANSWER_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_CHANGING_DEVICE, GlobalServantsConst.MSG_CHANGING_DEVICE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_REFUSE_LOGIN, GlobalServantsConst.MSG_REFUSE_LOGIN);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_WAIT, GlobalServantsConst.MSG_WAIT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NEED_COMPLETE_INFO, GlobalServantsConst.MSG_NEED_COMPLETE_INFO);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_USER_LOGIN_IN_OTHER_PLACE, GlobalServantsConst.MSG_USER_LOGIN_IN_OTHER_PLACE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_HAS_NO_PROXY_USER, GlobalServantsConst.MSG_HAS_NO_PROXY_USER);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_INVALID_URL, GlobalServantsConst.MSG_INVALID_URL);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_IS_NOT_SUPER_USER, GlobalServantsConst.MSG_IS_NOT_SUPER_USER);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_STUDENT_NUM_LESS_THAN_10, GlobalServantsConst.MSG_STUDENT_NUM_LESS_THAN_10);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NO_PERMISSION, GlobalServantsConst.MSG_NO_PERMISSION);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_GET_GROUPS, GlobalServantsConst.MSG_GET_GROUPS);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PUBLISH_FAILED, GlobalServantsConst.MSG_PUBLISH_FAILED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_SEND_SMS, GlobalServantsConst.MSG_SEND_SMS);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_ACCOUNT_INFO, GlobalServantsConst.MSG_CREATE_ACCOUNT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_ACCOUNT_EXIST, GlobalServantsConst.MSG_ACCOUNT_EXIST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PUBLISH_TOO_MORE, GlobalServantsConst.MSG_PUBLISH_TOO_MORE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_MORE_THAN_TWO_KIDS, GlobalServantsConst.MSG_MORE_THAN_TWO_KIDS);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_JOIN_GROUP, GlobalServantsConst.MSG_JOIN_GROUP);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_STU_DUPLICATE_NAME, GlobalServantsConst.MSG_STU_DUPLICATE_NAME);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_OPEN_BOX_FAILED, GlobalServantsConst.MSG_OPEN_BOX_FAILED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_DELETE_PUBLISHED, GlobalServantsConst.MSG_DELETE_PUBLISHED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_APPRAISE_COMMENT, GlobalServantsConst.MSG_APPRAISE_COMMENT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_ERROR_QUESTION_LIST_EMPTY, GlobalServantsConst.MSG_ERROR_QUESTION_LIST_EMPTY);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PHONE_REGISTERED, GlobalServantsConst.MSG_PHONE_REGISTERED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_GROUP_NOT_EXIST, GlobalServantsConst.MSG_GROUP_NOT_EXIST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_PAY_FAIL, GlobalServantsConst.MSG_PAY_FAIL);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_GROUP_REPEAT_JOIN, GlobalServantsConst.MSG_GROUP_REPEAT_JOIN);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NORMAL_GROUP_TOO_MANY, GlobalServantsConst.MSG_NORMAL_GROUP_TOO_MANY);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_SELF_STUDY_GROUP_TOO_MANY, GlobalServantsConst.MSG_SELF_STUDY_GROUP_TOO_MANY);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_AS_STUDY_GROUP_TOO_MANY, GlobalServantsConst.MSG_AS_STUDY_GROUP_TOO_MANY);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_NORMAL_CLASS_FULL, GlobalServantsConst.MSG_NORMAL_CLASS_FULL);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_GROUP_SELF_STUDY_ERROR, GlobalServantsConst.MSG_GROUP_SELF_STUDY_ERROR);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_CHILDREN_DUPLICATE_NAME, GlobalServantsConst.MSG_CHILDREN_DUPLICATE_NAME);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_CHAPTER_KNOWLEDGE, GlobalServantsConst.MSG_CHAPTER_KNOWLEDGE);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_QUESTION_SUBMITTED, GlobalServantsConst.MSG_QUESTION_SUBMITTED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_SG_PHONE_EXIST, GlobalServantsConst.MSG_SG_PHONE_EXIST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_ACCOUNT_NOT_EXIST, GlobalServantsConst.MSG_LOGIN_NAME_NOT_EXIST);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_WECHAT_AWARD_REPEAT, GlobalServantsConst.MSG_WECHAT_AWARD_REPEAT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TASK_AWARD_REPEAT, GlobalServantsConst.MSG_TASK_AWARD_REPEAT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TASK_RECOMMEND_SELF, GlobalServantsConst.MSG_TASK_RECOMMEND_SELF);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TASK_RECOMMEND_REGISTERED, GlobalServantsConst.MSG_TASK_RECOMMEND_REGISTERED);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TASK_RECOMMEND_REPEAT, GlobalServantsConst.MSG_TASK_RECOMMEND_REPEAT);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_VIP_TREASURE_LACK, GlobalServantsConst.MSG_VIP_TREASURE_LACK);
        code2MessageDictionary.put(GlobalServantsConst.ERROR_CODE_TARS_GRADE_GROUPID, GlobalServantsConst.MSG_GRADE_GROUP);
    }

    public static String messageByCode(int error) {

        if (code2MessageDictionary.isEmpty())
            _initialCodeMessage();

        //TODO 这个方法暂时这样写，后面可以进行更改
        String msg = code2MessageDictionary.get(error);
        if (null == msg) {
            msg = "找不到code对应的消息";
        }

        return msg;
    }

    private static GroupItemObj convertTeacherGroupItemByGroupAndTeacherName(Group group, String teacherName) {
        //教师查看班级  状态取的是group.getActiveState()
        GroupItemObj groupItemObj = new GroupItemObj();

        groupItemObj.setId(group.getId());
        groupItemObj.setName(group.getName());
        groupItemObj.setCityId(group.getCity().getId());
        groupItemObj.setCityName(group.getCity().getName());
        groupItemObj.setSchoolId(group.getSchool().getId());
        groupItemObj.setSchoolName(group.getSchool().getName());
        groupItemObj.setSchoolSimpleName(group.getSchool().getSim());
        groupItemObj.setDistrictId(group.getDistrict().getId());
        groupItemObj.setDistrictName(group.getDistrict().getName());
        groupItemObj.setProvinceId(group.getProvince().getId());
        groupItemObj.setProvinceName(group.getProvince().getName());
        groupItemObj.setClazz(group.getCls());

        if (group.getGroupType() == GmServantsConst.GROUP_TYPE_SELF_LEARN) {
            groupItemObj.setClassName(group.getName());
        } else {
            groupItemObj.setClassName(chineseNameOfClass(group.getCls()));
        }
        groupItemObj.setGrade(group.getGrade());
        groupItemObj.setGradeName(chineseNameOfGroup(group.getPeriod(), group.getGrade()));
        groupItemObj.setTeachingMaterial(group.getTextbook());
        groupItemObj.setStudentCount(group.getQuantityOfStu());
        groupItemObj.setCheckedNum(group.getQuantityOfStu());
        groupItemObj.setType(group.getGroupType());
        groupItemObj.setCreatedTime(group.getCreateTime());
        groupItemObj.setStatus(group.getActiveState());
//            groupItemObj.setStatus(GmServantsConst.STU_APPLY_STATUS_PASSED);//解决bug：由于自学班满45会重新创建自学班，并将以前的自学班修改2不活跃，但是前端只认1
        if (null != teacherName) {
            groupItemObj.setTeacher(teacherName);
        }
        groupItemObj.setAuditStatusName(convertChineseApplyStateByStateCode(group.getStudentJoinStatus()));

        return groupItemObj;
    }


    public static GroupItemObj convertStudentGroupItemByGroupAndTeacherName(Group group, String teacherName) {
        //学生查看班级  状态取的是group.getStudentJoinStatus()
        GroupItemObj groupItemObj = new GroupItemObj();

        groupItemObj.setId(group.getId());
        groupItemObj.setName(group.getName());
        groupItemObj.setCityId(group.getCity().getId());
        groupItemObj.setCityName(group.getCity().getName());
        groupItemObj.setSchoolId(group.getSchool().getId());
        groupItemObj.setSchoolName(group.getSchool().getName());
        groupItemObj.setSchoolSimpleName(group.getSchool().getSim());
        groupItemObj.setDistrictId(group.getDistrict().getId());
        groupItemObj.setDistrictName(group.getDistrict().getName());
        groupItemObj.setProvinceId(group.getProvince().getId());
        groupItemObj.setProvinceName(group.getProvince().getName());
        groupItemObj.setClazz(group.getCls());
        groupItemObj.setClassName(chineseNameOfClass(group.getCls()));
        groupItemObj.setGrade(group.getGrade());
        groupItemObj.setGradeName(chineseNameOfGroup(group.getPeriod(), group.getGrade()));
        groupItemObj.setTeachingMaterial(group.getTextbook());
        groupItemObj.setStudentCount(group.getQuantityOfStu());
        groupItemObj.setCheckedNum(group.getQuantityOfStu());
        groupItemObj.setType(group.getGroupType());
        groupItemObj.setCreatedTime(group.getCreateTime());
        groupItemObj.setTeacher(teacherName);
        groupItemObj.setStatus(group.getStudentJoinStatus());
        groupItemObj.setAuditStatusName(convertChineseApplyStateByStateCode(group.getStudentJoinStatus()));

        return groupItemObj;

    }

    private static String chineseNameOfGroup(int period, int grade) {
        return String.valueOf(period) + "届(" + convertToChineseNumber(grade) + "年级)";
    }

    private static String chineseNameOfClass(Integer classNum) {
        return convertToChineseNumber(classNum) + "班";
    }

    private static String[] chineseNumber = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private static String convertToChineseNumber(Integer number) {

        if (null == number || number <= 0) {
            return "";
        } else if (number <= 10) {
            return chineseNumber[number - 1];
        } else {

            if (number > 100)
                number = 99;

            String c = chineseNumber[number % 10 - 1];
            //超过二十，需要把
            if (number / 10 > 1) {
                c = chineseNumber[number / 10 - 1] + "十" + c;
            } else { //介于10-20之间则不需要第一个字符，如11为十一而不是一十一
                c = "十" + c;
            }
            return c;
        }
    }


    public static List<GroupItemObj> convertTeacherGroupItemsByGroupsAndTeacherNames(List<Group> groups, Map<String, String> id2name) {
        //教师查询班级状态
        if (null == groups || groups.isEmpty())
            return Collections.emptyList();

        List<GroupItemObj> groupItemObjs = new ArrayList<>();
        for (Group group : groups) {
            if (null == id2name || id2name.isEmpty())
                groupItemObjs.add(convertTeacherGroupItemByGroupAndTeacherName(group, null));
            else {
                String teacherId = group.getCreatorId();
                if (id2name.containsKey(teacherId)) {
                    groupItemObjs.add(convertTeacherGroupItemByGroupAndTeacherName(group, id2name.get(teacherId)));
                } else {
                    groupItemObjs.add(convertTeacherGroupItemByGroupAndTeacherName(group, ""));
                }

            }

        }

        return groupItemObjs;
    }

    public static List<GroupItemObj> convertStudentGroupItemsByGroupsAndTeacherNames(List<Group> groups, Map<String, String> id2name) {
        //家长和学生查询班级状态
        if (null == groups || groups.isEmpty())
            return Collections.emptyList();

        List<GroupItemObj> groupItemObjs = new ArrayList<>();
        for (Group group : groups) {
            if (null == id2name || id2name.isEmpty())
                groupItemObjs.add(convertStudentGroupItemByGroupAndTeacherName(group, null));
            else {
                String teacherId = group.getCreatorId();
                if (id2name.containsKey(teacherId)) {
                    groupItemObjs.add(convertStudentGroupItemByGroupAndTeacherName(group, id2name.get(teacherId)));
                } else {
                    groupItemObjs.add(convertStudentGroupItemByGroupAndTeacherName(group, ""));
                }

            }

        }

        return groupItemObjs;
    }

    public static Map<String, List<LocAddressItem>> sortLocationByLetter(Map<String, List<LocAddressItem>> locatonMap) {

        Map<String, List<LocAddressItem>> letterMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        letterMap.putAll(locatonMap);
        return letterMap;

    }

    public static Map<String, String> convertAccountId2RealName(List<Account> accounts) {
        if (null == accounts || accounts.isEmpty())
            return Collections.emptyMap();

        Map<String, String> id2RealName = new HashMap<>();
        for (Account account : accounts) {
            id2RealName.put(account.getId(), account.getRealname());
        }

        return id2RealName;
    }

    public static SimpleTagDto convertLightTagItemToSimpleTagDto(LightTagItem lightTagItem) {
        if (null == lightTagItem) {
            return null;
        }
        SimpleTagDto simpleTagDto = new SimpleTagDto();
        simpleTagDto.setId(lightTagItem.getId());
        simpleTagDto.setCode(lightTagItem.getCode());
        simpleTagDto.setName(null == lightTagItem.getText() ? lightTagItem.getCode() : lightTagItem.getText());
        simpleTagDto.setContent(lightTagItem.getContent());
        simpleTagDto.setProperty(lightTagItem.getProperty());
        return simpleTagDto;
    }

    public static QuestionDto convertQuestionMapItemToQuestionDto(QuestionMapItem questionMapItem) {
        if (null == questionMapItem) {
            return null;
        }
        QuestionDto questionDto = new QuestionDto();
        IntegrateQuestionItem integrateQuestionItem = questionMapItem.getIntegrateQuestionItem();
        questionDto.setId(integrateQuestionItem.getId());
        questionDto.setType(integrateQuestionItem.getType());
        questionDto.setDifficulty(integrateQuestionItem.getDifficulty());
        questionDto.setAnswerKey(questionMapItem.getAnswerKey());
        questionDto.setReferAns(questionMapItem.getReferAnswer());
        questionDto.setqContext(integrateQuestionItem.getQuestionBody());
        questionDto.setTagContext(integrateQuestionItem.getTagContext());
        questionDto.setQuestionTypeKey(questionMapItem.getQuestionTypeKey());
        Map<String, List<LightTagItem>> capabilityMap = questionMapItem.getQCapabilityMap();
        String key;
        List<LightTagItem> lightTagItems;
        List<SimpleTagDto> simpleTagDtos;
        for (Map.Entry<String, List<LightTagItem>> capabilityItem : capabilityMap.entrySet()) {
            key = capabilityItem.getKey();
            lightTagItems = capabilityItem.getValue();
            simpleTagDtos = new ArrayList<>();
            simpleTagDtos.addAll(lightTagItems.stream().map(BusinessUtils::convertLightTagItemToSimpleTagDto).collect(Collectors.toList()));
            if (RqbServantsConst.KEY_FOR_CAPABILITY.equals(key)) {
                questionDto.setCapability(simpleTagDtos);
            } else if (RqbServantsConst.KEY_FOR_COGNITION.equals(key)) {
                questionDto.setCognition(simpleTagDtos);
            } else if (RqbServantsConst.KEY_FOR_KNOWLEDGE.equals(key)) {
                questionDto.setKnowledge(simpleTagDtos);
            }
        }

        return questionDto;

    }

    public static PageDto convertPageItemToPageTo(QuestionPageItem questionPageItem) {
        if (null == questionPageItem) {
            return new PageDto();
        }
        PageDto pageDto = new PageDto();
        pageDto.setCurrentPage(questionPageItem.getCurrentPage());
        pageDto.setPages(questionPageItem.getPages());
        pageDto.setPerPageSize(questionPageItem.getPerPageSize());
        pageDto.setTotal(questionPageItem.getTotal());
        pageDto.setSort(questionPageItem.getSort());
        pageDto.setOrder(questionPageItem.getOrder());
        return pageDto;
    }

    public static  List convertStuPaperHistoriesByListOfReceivedPaperDescAndEncourageDictionary(List<ReceivedPaperDesc> receivedPaperDescs, Map<String, Map<String, List<Integer>>> instanceId2Encourages, String stuId) {

        List<StudentPaperHistory> studentPaperHistories = new ArrayList<>();
        for (ReceivedPaperDesc receivedPaperDesc : receivedPaperDescs) {
            String instanceId = receivedPaperDesc.getInstanceId();

            List<Integer> encourages = new ArrayList<>();
            if (null != instanceId2Encourages && !instanceId2Encourages.isEmpty()) {
                encourages.addAll(_convertRole2EncouragesToEncourage(instanceId2Encourages.get(instanceId)));
            }

            Map<String, List<Integer>> sid2Encourages = new HashMap<>();
            sid2Encourages.put(stuId, encourages);

            studentPaperHistories.add(
                    convertStudentPaperHistoryByReceivedPaperDescAndEncourages(receivedPaperDesc, sid2Encourages)
            );
        }

        return studentPaperHistories;
    }

    private static List<Integer> _convertRole2EncouragesToEncourage(Map<String, List<Integer>> role2Encourage) {

        if (null == role2Encourage || role2Encourage.isEmpty())
            return Collections.emptyList();

        List<Integer> encourages = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : role2Encourage.entrySet()) {
            encourages.addAll(entry.getValue());
        }

        return encourages;
    }

    private static StudentPaperHistory convertStudentPaperHistoryByReceivedPaperDescAndEncourages(ReceivedPaperDesc receivedPaperDesc, Map<String, List<Integer>> encourages) {
        StudentPaperHistory studentPaperHistory = new StudentPaperHistory();
        studentPaperHistory.setId(receivedPaperDesc.getId());
        studentPaperHistory.setGroupId(receivedPaperDesc.getGroupId());
        studentPaperHistory.setInstanceId(receivedPaperDesc.getInstanceId());
        studentPaperHistory.setPublishType(receivedPaperDesc.getPublishType());
        studentPaperHistory.setPublishTime(_convertLongToDate(receivedPaperDesc.getPublishTime()));
        studentPaperHistory.setPublishWeek(_convertChineseWeekByDate(studentPaperHistory.getPublishTime()));

        List<StudentPaperHistory.PaperHistory> paperHistories = new ArrayList<>();
        StudentPaperHistory.PaperHistory paperHistory = new StudentPaperHistory.PaperHistory();

        SingleMaster singleMaster = receivedPaperDesc.getSingleMaster();
        if (null != singleMaster) {
            paperHistory.setMasterStatus(singleMaster.getMasterState());
            paperHistory.setMasterNum(singleMaster.getMasterNum());
        }

        paperHistory.setEncourage(encourages);
        paperHistory.setLatestScore(receivedPaperDesc.getLatestScore());
        paperHistory.setPaperId(receivedPaperDesc.getPaperId());
        paperHistory.setPaperTitle(receivedPaperDesc.getPaperName());
        paperHistory.setWorthScore(receivedPaperDesc.getWorthScore());
        paperHistory.setStatus(receivedPaperDesc.getStatus());
        paperHistory.setType(receivedPaperDesc.getPublishType());
        paperHistories.add(paperHistory);

        studentPaperHistory.setPaperHistories(paperHistories);

        return studentPaperHistory;
    }

    private static String _convertChineseWeekByDate(Date date) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    public static Long convertStringDateToLong(String dateTime) {
        if (null == dateTime || dateTime.trim().isEmpty()) {
            return 0L;
        }
        Date date = DateTimeHelper.dateFromFormatString(dateTime, CONST.DATE_TIME_FORMAT_STRING);
        if (null == date)
            return 0L;
        return date.getTime();
    }


    public static Map<String, QuestionGrp> convertAnswerQuestionListToQuestionGrpMap(
            List<com.alr.gateway.tars.globalservants.AnswerQuestionParse> answerQuestionParseList) {
        if (null == answerQuestionParseList || answerQuestionParseList.isEmpty()) {
            return null;
        }
        Map<String, List<com.alr.gateway.tars.globalservants.AnswerQuestionParse>> ansQParseMapGroupByGrpId = StreamHelper.convertListToMapList(answerQuestionParseList, com.alr.gateway.tars.globalservants.AnswerQuestionParse::getQgId);
        Map<String, QuestionGrp> questionGrpMap = new ConcurrentHashMap<>();
        QuestionGrp questionGrp;
        String qGrpId;
        for (Map.Entry<String, List<com.alr.gateway.tars.globalservants.AnswerQuestionParse>> qGrpMap : ansQParseMapGroupByGrpId.entrySet()) {
            qGrpId = qGrpMap.getKey();
            List<com.alr.gateway.tars.globalservants.AnswerQuestionParse> qList = qGrpMap.getValue();
            double qGrpScore = 0;
            Map<String, Question> qMap = new ConcurrentHashMap<>();
            for (com.alr.gateway.tars.globalservants.AnswerQuestionParse ansQParse : qList) {

                qGrpScore += ansQParse.getQScore();

                Question question = new Question();
                question.setScore(ansQParse.getQScore());

                QuestionProperties questionProperties = convertQuestionPropsToQuestionProperties(ansQParse.getProps());
                questionProperties.setQuestionId(ansQParse.getQId());
                question.setProperties(questionProperties);

                String answerKey = ansQParse.getAnswerKey();

                Map<String, Double> spid2Score = generateSpId2ScoreByAnswerKeyAndQuestionScore(answerKey, question.getScore());
                question.setScorePointDict(spid2Score);//todo:需要将ansQParse.getAnswerKey()转换成Map<String,Double>
                qMap.put(ansQParse.getQId(), question);
            }
            questionGrp = new QuestionGrp();
            questionGrp.setScore(qGrpScore);
            questionGrp.setQuestionDict(qMap);
            questionGrpMap.put(qGrpId, questionGrp);
        }
        return questionGrpMap;
    }

    public static Map<String, Double> generateSpId2ScoreByAnswerKeyAndQuestionScore(String answerKey, double score) {

        Map<String, Double> spId2Score = new HashMap<>();

        List<ScorePoint> scorePoints = com.allere.qbu.grade.pojo.answer.AnswerQuestionParse.getScorePointsFromAnswerKey(answerKey, score);
        for (ScorePoint scorePoint : scorePoints) {
            spId2Score.put(scorePoint.getScorePointId(), scorePoint.getWorthScore());
        }

        return spId2Score;
    }

    public static QuestionProperties convertQuestionPropsToQuestionProperties(com.alr.gateway.tars.globalservants.QuestionProps props) {
        if (null == props) {
            return null;
        }
        QuestionProperties questionProperties = new QuestionProperties();
        questionProperties.setDifficulty(props.getDifficulty());
        questionProperties.setCapability(StringUtils.join(props.getCapability(), CONST.COMMON_SEPERATOR_COMMA));
        questionProperties.setCognition(StringUtils.join(props.getCognition(), CONST.COMMON_SEPERATOR_COMMA));

        //TODO 这里只需要二级知识点，这个获取方式有点问题。很容易出错
        questionProperties.setKnowledgeId(props.getKnowledge().get(0));

        return questionProperties;
    }

    public static DeliveredPaperQuestion convertDeliveredPaperDescByDeliveredPaperDescAndGroupName(DeliveredPaperDesc deliveredPaperDesc, Group group) {

        DeliveredPaperQuestion deliveredPaperQuestion = new DeliveredPaperQuestion();
        deliveredPaperQuestion.setAssigneeDisplaySet(_generateDisplayAssigneeByAssignRanges(deliveredPaperDesc.getAssignRanges()));
        deliveredPaperQuestion.setCreateTime(new Date(deliveredPaperDesc.getPublishTime()));
        deliveredPaperQuestion.setDelivererId(deliveredPaperDesc.getDelivererId());
        deliveredPaperQuestion.setInstanceId(deliveredPaperDesc.getInstanceId());
        deliveredPaperQuestion.setPublishType(deliveredPaperDesc.getPublishType());
        deliveredPaperQuestion.setPublishWeek(ConvertUtils.getWeekOfDate(deliveredPaperQuestion.getCreateTime()));
        deliveredPaperQuestion.setId(deliveredPaperDesc.getId());
        long startTime = deliveredPaperDesc.getTimeRange().getStartTime();
        if (startTime > 0) {
            deliveredPaperQuestion.setStartTime(new Date(startTime));
        }
        deliveredPaperQuestion.setIsNew(0);
        if (null != group) {
            deliveredPaperQuestion.setGroupId(group.getId());
            deliveredPaperQuestion.setGroupName(group.getName());
        }
        deliveredPaperQuestion.setSubjects(convertSubjectsBySubjectDesc(deliveredPaperDesc.getSubject()));

        return deliveredPaperQuestion;
    }

    private static Set<String> _generateDisplayAssigneeByAssignRanges(List<String> assignRanges) {
        Set<String> set = new HashSet<>();
        if (assignRanges.contains("all")) {
            set.add("全班");
        } else {
            set.add("" + assignRanges.get(0) + "层");
        }

        return set;
    }

    private static List<DeliveredPaperQuestion.Subject> convertSubjectsBySubjectDesc(SubjectDesc subjectDesc) {
        List<DeliveredPaperQuestion.Subject> subjects = new ArrayList<>();

        DeliveredPaperQuestion.Subject subject = new DeliveredPaperQuestion.Subject();
        subject.setType(subjectDesc.getType());
        subject.setScore(subjectDesc.getScore());
        subject.setSubjectId(subjectDesc.getSubjectId());
        subject.setSubjectSymbol(subjectDesc.getSubjectName());
        subject.setSubmitNum(subjectDesc.getSubmitNum());
        subject.setTotalNum(subjectDesc.getTotalNum());

        subjects.add(subject);

        return subjects;
    }

    public static List<ChapterMasterState> convertChapterMasterStatesByChapterId2QuantityOfKnowledgeMasAndChapterId2KnowledgeDesc(
            Map<String, QuantityOfKnowledgeMas> chapterId2QuantityOfKnowledgeMas, Map<String, ChapterSecKnowledgeDesc> chapterId2KnowledgeDesc) {

        List<ChapterMasterState> chapterMasterStates = new ArrayList<>();
        for (Map.Entry<String, QuantityOfKnowledgeMas> entry : chapterId2QuantityOfKnowledgeMas.entrySet()) {

            String chapterId = entry.getKey();
            QuantityOfKnowledgeMas quantityOfKnowledgeMas = entry.getValue();

            ChapterSecKnowledgeDesc chapterSecKnowledgeDesc = chapterId2KnowledgeDesc.get(chapterId);
            if (null == chapterId2KnowledgeDesc) {
                continue;
            }

            ChapterMasterState chapterMasterState = new ChapterMasterState();
            chapterMasterState.setId(chapterId);
            chapterMasterState.setContent(chapterSecKnowledgeDesc.getName());
            chapterMasterState.setSeqNum(Integer.valueOf(chapterSecKnowledgeDesc.getSeq()));
            chapterMasterState.setMasterNumber(quantityOfKnowledgeMas.getQuantityOfMasterful());
            chapterMasterState.setNoQuestionRecNumber(quantityOfKnowledgeMas.getQuantityOfNoneSample());
            chapterMasterState.setNotFirmNumber(quantityOfKnowledgeMas.getQuantityOfUnfirm());
            chapterMasterState.setNotMasterNumber(quantityOfKnowledgeMas.getQuantityOfUnversed());

            //TODO 一共需要掌握的知识点数量, 这个名字命得有歧义，理解的时候注意
            if (null != chapterSecKnowledgeDesc.getListOfSecKnowledgeDesc()) {
                chapterMasterState.setTotalMasterNumber(chapterSecKnowledgeDesc.getListOfSecKnowledgeDesc().size());
            } else {
                chapterMasterState.setTotalMasterNumber(0);
            }

            if (null == chapterSecKnowledgeDesc.getListOfSecKnowledgeDesc() || chapterSecKnowledgeDesc.getListOfSecKnowledgeDesc().isEmpty()) {
                chapterMasterState.setHasKnowledgePoint(false);
            } else {
                chapterMasterState.setHasKnowledgePoint(true);
            }

            chapterMasterStates.add(chapterMasterState);
        }

        return chapterMasterStates;
    }

    public static List<GroupLvlState> convertGroupLvlStateByListOfLvlQuantityGroupAndGroupType(List<LvlQuantityGroup> listOfLvlQuantityGroup, int groupType) {
        if (null == listOfLvlQuantityGroup || listOfLvlQuantityGroup.isEmpty())
            return Collections.emptyList();

        List<GroupLvlState> groupLvlStates = new ArrayList<>();
        for (LvlQuantityGroup lvlQuantityGroup : listOfLvlQuantityGroup) {

            GroupLvlState groupLvlState = new GroupLvlState();
            groupLvlState.setId(lvlQuantityGroup.getGroupId());
            groupLvlState.setName(lvlQuantityGroup.getName());
            groupLvlState.setLevels(lvlQuantityGroup.getLvl2QuantityOfStu());
            groupLvlState.setType(groupType);
            groupLvlState.setClassName(
                    BusinessUtils.chineseNameOfGroup(lvlQuantityGroup.getPeriod(), lvlQuantityGroup.getGrade()) + BusinessUtils.chineseNameOfClass(lvlQuantityGroup.getCls())
            );
            groupLvlState.setStudentCount(BusinessUtils.calcQuantityOfStuByLvl2QuantityOfStu(lvlQuantityGroup.getLvl2QuantityOfStu()));

            groupLvlStates.add(groupLvlState);
        }

        return groupLvlStates;
    }

    private static int calcQuantityOfStuByLvl2QuantityOfStu(Map<String, Integer> lvl2QuantityOfStu) {

        if (null == lvl2QuantityOfStu || lvl2QuantityOfStu.isEmpty())
            return 0;
        int quantityOfStu = 0;
        for (Map.Entry<String, Integer> entry : lvl2QuantityOfStu.entrySet()) {
            quantityOfStu += entry.getValue();
        }

        return quantityOfStu;
    }

    public static List<String> getGroupIdsNeedToGetAllStuIdByAssignees(final List<Assignee> assignees) {

        List<String> groupIds = new ArrayList<>();
        assignees.stream().filter(assignee -> null == assignee.getStuIds()).forEach(assignee -> {
            groupIds.add(assignee.getGroupId());
        });

        return groupIds;
    }


    public static SetupPaper generateSetupPaperByPaperIdAndTypeAndNameAndScoreAndQuestionGrpInfo(String deliverPaperId, int deliverPaperType, String paperName, double pScore, Map<String, QuestionGrp> questionGrpMap) {

        SetupPaper setupPaper = new SetupPaper();
        setupPaper.setId(deliverPaperId);
        setupPaper.setPaperType(deliverPaperType);
        setupPaper.setName(paperName);
        setupPaper.setQuestionGrpDict(questionGrpMap);
        setupPaper.setScore(pScore);

        return setupPaper;
    }

    public static DeliverConf generateDeliverConfByPubTypeAndAssigneesAndTimeRange(Integer publishType, List<Assignee> assignees, Long startLongTime, Long endLongTime) {
        DeliverConf deliverConf = new DeliverConf();

        deliverConf.setPublishType(publishType);
        deliverConf.setDescription("");
        deliverConf.setStartTime(startLongTime);
        deliverConf.setEndTime(endLongTime);
        deliverConf.setAssignees(assignees);

        return deliverConf;
    }

    public static String getPaperStateDescByStateCode(Integer stateCode) {
        if (stateCode == QbuServantsConst.STUDENT_WORK_STATUS_NOTSTARTED)
            return "未开始";
        else if (stateCode == QbuServantsConst.STUDENT_WORK_STATUS_INPROGRESS)
            return "进行中";
        else if (stateCode == QbuServantsConst.STUDENT_WORK_STATUS_COMPLETED)
            return "已提交";
        else if (stateCode == QbuServantsConst.STUDENT_WORK_STATUS_GRADINGCOMPLETED)
            return "已批改";
        else if (stateCode == QbuServantsConst.STUDENT_WORK_STATUS_REWORK)
            return "改错中";

        return "";
    }


    public static Map<String, PaperWorkHistory> generateSid2PaperWorkHistory(Map<String, Map<Integer, QuestionRecordItem>> sId2QuestionRecord,
                                                                             String questionGroupId, String questionId, String paperId) {

        Map<String, PaperWorkHistory> sId2PaperWorkHistory = new HashMap<>();
        for (Map.Entry<String, Map<Integer, QuestionRecordItem>> entry : sId2QuestionRecord.entrySet()) {

            String sId = entry.getKey();
            Map<String, Map<Integer, PaperWorkHistory.QuestionScore>> questionScoreDict = new HashMap<>();
            questionScoreDict.put(questionId, convertQuestionHistoryByRepeat2QuestionRecordDict(sId2QuestionRecord.get(sId)));

            PaperWorkHistory.QuestionGroupScore questionGroupScore = new PaperWorkHistory.QuestionGroupScore();
            questionGroupScore.setId2QuestionScore(questionScoreDict);

            Map<String, PaperWorkHistory.QuestionGroupScore> questionGroupDict = new HashMap<>();
            questionGroupDict.put(questionGroupId, questionGroupScore);

            PaperWorkHistory paperWorkHistory = new PaperWorkHistory();
            paperWorkHistory.setPaperId(paperId);
            paperWorkHistory.setId2QuestionGroupScore(questionGroupDict);

            sId2PaperWorkHistory.put(sId, paperWorkHistory);

        }

        return sId2PaperWorkHistory;
    }


    public static PaperWorkHistory convertStuPaperWorkHistoryByStuPaperAndInstanceId(StuPaper stuPaper, String paperInstanceId) {

        Map<Integer, Double> times2ScoreOfPaper = new HashMap<>();
        PaperWorkHistory paperWorkHistory = new PaperWorkHistory();
        paperWorkHistory.setPaperId(stuPaper.getPaperId());
        paperWorkHistory.setPaperInstanceId(paperInstanceId);
        paperWorkHistory.setStatus(new WorkStateKVPair(stuPaper.getState()));
        paperWorkHistory.setWasteTime(convertWasteTimeToElapseSecond(stuPaper.getWasteTime()).toString());
        paperWorkHistory.setFirstScore(BigDecimalUtils.roundDoubleNum(1, stuPaper.getFirstScore()));
        paperWorkHistory.setFirstSubmitTime(_convertLongToDate(stuPaper.getFirstSubmitTime()));
        paperWorkHistory.setLatestScore(BigDecimalUtils.roundDoubleNum(1,stuPaper.getLatestScore()));
        paperWorkHistory.setLatestSubmitTime(_convertLongToDate(stuPaper.getLatestSubmitTime()));

        paperWorkHistory.setId2QuestionGroupScore(_convertPaperWorkHistoryDictionaryByPaperStateDict(stuPaper.getPaperState(), times2ScoreOfPaper));

        paperWorkHistory.setScores(times2ScoreOfPaper);
        paperWorkHistory.setReworkTimes(times2ScoreOfPaper.size() - 1);

        return paperWorkHistory;
    }

    private static Map<String, PaperWorkHistory.QuestionGroupScore> _convertPaperWorkHistoryDictionaryByPaperStateDict(Map<String, QuestionGroupState> paperState, Map<Integer, Double> times2ScoreOfPaper) {

        Map<String, PaperWorkHistory.QuestionGroupScore> questionGroupScoreDict = new HashMap<>();
        for (Map.Entry<String, QuestionGroupState> qgEntry : paperState.entrySet()) {

            Map<Integer, Double> time2ScoreOfQuestionGroup = new HashMap<>();

            String questionGroupId = qgEntry.getKey();
            QuestionGroupState questionGroupState = qgEntry.getValue();

            PaperWorkHistory.QuestionGroupScore questionGroupScore = questionGroupScoreDict.get(questionGroupId);
            if (null == questionGroupScore) {
                questionGroupScore = new PaperWorkHistory.QuestionGroupScore();
                questionGroupScoreDict.put(questionGroupId, questionGroupScore);
            }

            questionGroupScore.setId2QuestionScore(_convertPaperWorkQuestionHistoryByQuestionScoreState(questionGroupState.getQuestionScoreDict(), time2ScoreOfQuestionGroup));
            questionGroupScore.setScores(time2ScoreOfQuestionGroup);

            _enrichTimesScore(times2ScoreOfPaper, time2ScoreOfQuestionGroup);
        }

        return questionGroupScoreDict;
    }

    private static Map<String, Map<Integer, PaperWorkHistory.QuestionScore>> _convertPaperWorkQuestionHistoryByQuestionScoreState(Map<String, List<QuestionScoreState>> questionId2ScoreDict, Map<Integer, Double> time2ScoreOfQuestionGroup) {

        Map<String, Map<Integer, PaperWorkHistory.QuestionScore>> questionScoreDict = new HashMap<>();
        for (Map.Entry<String, List<QuestionScoreState>> qEntry : questionId2ScoreDict.entrySet()) {
            String questionId = qEntry.getKey();
            List<QuestionScoreState> questionScoreStates = qEntry.getValue();

            Map<Integer, PaperWorkHistory.QuestionScore> times2QuestionScore = questionScoreDict.get(questionId);
            if (times2QuestionScore == null) {
                times2QuestionScore = new HashMap<>();
                questionScoreDict.put(questionId, times2QuestionScore);
            }


            Map<Integer, Double> time2ScoreOfQuestion = new HashMap<>();
            for (QuestionScoreState questionScoreState : questionScoreStates) {

                Integer times = questionScoreState.getRepeatSeq();

                PaperWorkHistory.QuestionScore questionScore = times2QuestionScore.get(times);
                if (null == questionScore) {
                    questionScore = new PaperWorkHistory.QuestionScore();
                    times2QuestionScore.put(times, questionScore);
                }

                questionScore.setVersion(questionScoreState.getVersion());
                questionScore.setScore(questionScoreState.getScore());
                questionScore.setTotalScore(questionScoreState.getWorthScore());
//              questionScore.setIgnore();
//              questionScore.set(questionScoreState.getWorkState());
                time2ScoreOfQuestion.put(times, questionScore.getScore());

                questionScore.setId2ScorePointScore(_convertPaperWorkScorePointHistoryByScorePointState(questionScoreState.getScorePointDict()));
            }

            _enrichTimesScore(time2ScoreOfQuestionGroup, time2ScoreOfQuestion);
        }

        return questionScoreDict;
    }

    private static Map<String, PaperWorkHistory.ScorePointScore> _convertPaperWorkScorePointHistoryByScorePointState(Map<String, ScorePointState> scorePointDict) {
        Map<String, PaperWorkHistory.ScorePointScore> spId2ScorePointScore = new HashMap<>();

        for (Map.Entry<String, ScorePointState> spEntry : scorePointDict.entrySet()) {
            String spId = spEntry.getKey();
            ScorePointState scorePointState = spEntry.getValue();

            PaperWorkHistory.ScorePointScore scorePointScore = spId2ScorePointScore.get(spId);
            if (null == scorePointScore) {
                scorePointScore = new PaperWorkHistory.ScorePointScore();
                spId2ScorePointScore.put(spId, scorePointScore);
            }

            scorePointScore.setId(scorePointState.getRecordId());
            scorePointScore.setScore(scorePointState.getScore());
            scorePointScore.setAnswer(scorePointState.getAnswer());
            scorePointScore.setCorrectness(scorePointState.getState());
            String reverse = scorePointState.getReverse();
            if (null != reverse) {
                if (reverse.contains("{") && reverse.contains("}")) { //此方式简单判断是不是Json
                    scorePointScore.setApplication(JsonHelper.valueOf(scorePointState.getReverse(), Object.class));
                } else {
                    scorePointScore.setApplication(reverse);
                }
            }
        }

        return spId2ScorePointScore;
    }

    private static void _enrichTimesScore(Map<Integer, Double> times2ScoreOfDst, Map<Integer, Double> time2ScoreOfSrc) {
        for (Map.Entry<Integer, Double> entry : time2ScoreOfSrc.entrySet()) {
            Integer times = entry.getKey();
            Double srcScore = entry.getValue();
            if (null == srcScore)
                srcScore = 0d;

            Double dstScore = times2ScoreOfDst.get(times);
            if (null == dstScore)
                dstScore = 0d;
            dstScore += srcScore;
            times2ScoreOfDst.put(times, dstScore);
        }
    }

    private static Date _convertLongToDate(long time) {
        return new Date(time);
    }

    public static BigDecimal setScaleRoundUp(int newScale, BigDecimal b) {
        return b.setScale(newScale, BigDecimal.ROUND_UP);
    }

    private static String _convertWasteTime(int wasteTime) {

        String wasteString = "";
        if (wasteTime / 1000d <= 0) {
            return "0秒";
        }

        BigDecimal bigDecimalMin = new BigDecimal(Double.valueOf(wasteTime) / 1000d);
        Integer wasteHour = setScaleRoundUp(0, bigDecimalMin).intValue();

        if (wasteHour / 3600 > 0) {
            wasteString += (wasteHour / 3600) + "时";
        }
        Integer wasteMinute = wasteHour % 3600;
        if (wasteMinute / 60 > 0) {
            wasteString += (wasteMinute / 60) + "分";
        }
        Integer wasteSecond = wasteMinute % 60;
        if (wasteSecond > 0) {
            wasteString += wasteSecond + "秒";
        }
        return wasteString;
    }

    public static Integer convertWasteTimeToElapseSecond(int wasteTime) {

        if (wasteTime == 0)
            return 0;

        if (wasteTime > 0 && wasteTime < 1000d)
            return 1;

        BigDecimal bigDecimalMin = new BigDecimal(Double.valueOf(wasteTime) / 1000d);
        Integer wasteSecond = setScaleRoundUp(0, bigDecimalMin).intValue();

        return wasteSecond;
    }

    public static Integer convertWasteTimeToElapseMinute(int wasteTime) {

        if (wasteTime == 0)
            return 0;

        if (wasteTime > 0 && wasteTime < 60000d)
            return 1;

        BigDecimal bigDecimalMin = new BigDecimal(Double.valueOf(wasteTime) / 60000d);
        Integer wasteMinute = setScaleRoundUp(0, bigDecimalMin).intValue();

        return wasteMinute;
    }


    public static PaperWork convertStuPaperWorkByStuPaperAndInstanceId(StuPaper stuPaper, String paperInstanceId) {

        PaperWork paperWork = new PaperWork();
        paperWork.setPaperId(stuPaper.getPaperId());
        paperWork.setPaperInstanceId(paperInstanceId);
//        paperWorkHistory.setScore();
        paperWork.setStatus(new WorkStateKVPair(stuPaper.getState()));
        paperWork.setWasteTime(stuPaper.getWasteTime());

        paperWork.setId2QuestionGroupScore(_convertPaperWorkDictionaryByPaperStateDict(stuPaper.getPaperState()));

        return paperWork;
    }

    private static Map<String, PaperWork.QuestionGroupScore> _convertPaperWorkDictionaryByPaperStateDict(Map<String, QuestionGroupState> paperState) {

        Map<String, PaperWork.QuestionGroupScore> questionGroupScoreDict = new HashMap<>();
        for (Map.Entry<String, QuestionGroupState> qgEntry : paperState.entrySet()) {
            String questionGroupId = qgEntry.getKey();
            QuestionGroupState questionGroupState = qgEntry.getValue();

            PaperWork.QuestionGroupScore questionGroupScore = questionGroupScoreDict.get(questionGroupId);
            if (null == questionGroupScore) {
                questionGroupScore = new PaperWork.QuestionGroupScore();
                questionGroupScoreDict.put(questionGroupId, questionGroupScore);
            }

            questionGroupScore.setScore(0);//TODO 题组的得分关系不大，这里都传零

            questionGroupScore.setId2QuestionScore(_convertPaperWorkQuestionByQuestionScoreState(questionGroupState.getQuestionScoreDict()));
        }

        return questionGroupScoreDict;
    }

    private static Map<String, PaperWork.QuestionScore> _convertPaperWorkQuestionByQuestionScoreState(Map<String, List<QuestionScoreState>> questionScoreStateDict) {

        Map<String, PaperWork.QuestionScore> questionScoreDict = new HashMap<>();
        for (Map.Entry<String, List<QuestionScoreState>> qEntry : questionScoreStateDict.entrySet()) {
            String questionId = qEntry.getKey();
            List<QuestionScoreState> questionScoreStates = qEntry.getValue();

            PaperWork.QuestionScore questionScore = questionScoreDict.get(questionId);
            if (questionScore == null) {
                questionScore = new PaperWork.QuestionScore();
                questionScoreDict.put(questionId, questionScore);
            }

            QuestionScoreState questionScoreState = questionScoreStates.get(0);

            questionScore.setVersion(questionScoreState.getVersion());
            questionScore.setScore(questionScoreState.getScore());
            questionScore.setId(questionId);
//            questionScore.setIgnore();
            questionScore.setWorkStatus(questionScoreState.getWorkState());

            questionScore.setId2ScorePointScore(_convertPaperWorkScorePointByScorePointState(questionScoreState.getScorePointDict()));
        }

        return questionScoreDict;
    }

    private static Map<String, PaperWork.ScorePointScore> _convertPaperWorkScorePointByScorePointState(Map<String, ScorePointState> scorePointDict) {

        Map<String, PaperWork.ScorePointScore> spId2ScorePointScore = new HashMap<>();

        for (Map.Entry<String, ScorePointState> spEntry : scorePointDict.entrySet()) {
            String spId = spEntry.getKey();
            ScorePointState scorePointState = spEntry.getValue();

            PaperWork.ScorePointScore scorePointScore = spId2ScorePointScore.get(spId);
            if (null == scorePointScore) {
                scorePointScore = new PaperWork.ScorePointScore();
                spId2ScorePointScore.put(spId, scorePointScore);
            }

            scorePointScore.setId(scorePointState.getRecordId());
            scorePointScore.setScore(scorePointState.getScore());
            scorePointScore.setAnswer(scorePointState.getAnswer());
            scorePointScore.setCorrectness(scorePointState.getState());
        }

        return spId2ScorePointScore;
    }

    public static ComplexStuAnswer generateComplexStuAnswerByJsonOfAnswerAndInstanceId(String jsonOfAnswer, String paperInstanceId) {

        ComplexStuAnswer complexStuAnswer = new ComplexStuAnswer();
        complexStuAnswer.setInstanceId(paperInstanceId);

        if (null != jsonOfAnswer && !jsonOfAnswer.trim().isEmpty()) {
            List<ComplexStuQAnswer> stuQAnswers = JsonHelper.valueOfList(jsonOfAnswer, ComplexStuQAnswer.class);
            complexStuAnswer.setAnswers(_convertQuestionAnswersByListOfComplexStuQAnswer(stuQAnswers));
        }
        return complexStuAnswer;
    }

    private static List<QuestionAnswer> _convertQuestionAnswersByListOfComplexStuQAnswer(List<ComplexStuQAnswer> stuQAnswers) {
        return stuQAnswers.stream().map(BusinessUtils::_convertQuestionAnswerByComplexStuQAnswer).collect(Collectors.toList());
    }

    private static QuestionAnswer _convertQuestionAnswerByComplexStuQAnswer(ComplexStuQAnswer complexStuQAnswer) {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setQuestionId(complexStuQAnswer.getQuestionId());
        questionAnswer.setWorkStatus(complexStuQAnswer.getWorkStatus());
        questionAnswer.setSpRecId2AnsDict(_convertSpRecId2AnswerByListOfSimpleAns(complexStuQAnswer.getAnswers()));
        return questionAnswer;
    }

    private static Map<String, String> _convertSpRecId2AnswerByListOfSimpleAns(List<ComplexStuQAnswer.SimpleStudentAnswer> answers) {

        Map<String, String> spRecId2Answer = new HashMap<>();
        for (ComplexStuQAnswer.SimpleStudentAnswer answer : answers) {
            spRecId2Answer.put(answer.getUuid(), answer.getAnswer());
        }

        return spRecId2Answer;
    }

    public static Map<Integer, PaperWorkHistory.QuestionScore> convertQuestionHistoryByRepeat2QuestionRecordDict(Map<Integer, QuestionRecordItem> repeat2QuestionRecordDict) {
        if (null == repeat2QuestionRecordDict || repeat2QuestionRecordDict.isEmpty())
            return Collections.emptyMap();

        Map<Integer, PaperWorkHistory.QuestionScore> repeatSeq2QuestionScore = new HashMap<>();
        for (Map.Entry<Integer, QuestionRecordItem> entry : repeat2QuestionRecordDict.entrySet()) {
            Integer repeatSeq = entry.getKey();
            QuestionRecordItem questionRecordItem = entry.getValue();
            PaperWorkHistory.QuestionScore questionScore = convertQuestionScoreByQuestionRecordItem(questionRecordItem);
            repeatSeq2QuestionScore.put(repeatSeq, questionScore);
        }

        return repeatSeq2QuestionScore;
    }

    public static PaperWorkHistory.QuestionScore convertQuestionScoreByQuestionRecordItem(QuestionRecordItem questionRecordItem) {

        PaperWorkHistory.QuestionScore questionScore = new PaperWorkHistory.QuestionScore();

        questionScore.setTotalScore(questionRecordItem.getTotalScore());
        questionScore.setScore(questionRecordItem.getScore());

        double totalScore = questionRecordItem.getTotalScore();
        double score = questionRecordItem.getScore();
        if (score >= totalScore)
            questionScore.setPassFlag(PaperWorkHistory.QuestionScore.PASS_FLAG_RIGHT);
        else if (score == 0)
            questionScore.setPassFlag(PaperWorkHistory.QuestionScore.PASS_FLAG_WRONG);
        else
            questionScore.setPassFlag(PaperWorkHistory.QuestionScore.PASS_FLAG_HALF);

        Integer questionType = questionRecordItem.getType();
        if (null != questionType && questionType == QbuServantsConst.QUESTION_TYPE_FOR_PAPER_QUESTION) {

            questionScore.setKeyValuePairs(new CommKvPair(questionType));
        }

        questionScore.setCreateTime(_convertLongToDate(questionRecordItem.getCreateTime()));
        questionScore.setId2ScorePointScore(
                _convertPaperWorkScorePointHistoryByScorePointState(questionRecordItem.getScorePointDict())
        );

        return questionScore;
    }

    public static Map<String, List<String>> convertChapterId2KnowledgeIdsByChapterId2ChapterSecKnowledgeDesc(Map<String, ChapterSecKnowledgeDesc> chapterId2SecKnowledgeDesc) {
        if (null == chapterId2SecKnowledgeDesc || chapterId2SecKnowledgeDesc.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> chapterId2SecKnowledgeIds = new HashMap<>();
        for (Map.Entry<String, ChapterSecKnowledgeDesc> entry : chapterId2SecKnowledgeDesc.entrySet()) {
            String chapterId = entry.getKey();

            List<String> knowledgeIds = chapterId2SecKnowledgeIds.get(chapterId);
            if (null == knowledgeIds) {
                knowledgeIds = new ArrayList<>();
                chapterId2SecKnowledgeIds.put(chapterId, knowledgeIds);
            }

            List<SecKnowledgeDesc> secKnowledgeDescs = entry.getValue().getListOfSecKnowledgeDesc();

            knowledgeIds.addAll(DSHelper.convertListFromCollections(secKnowledgeDescs, SecKnowledgeDesc::getId));
        }

        return chapterId2SecKnowledgeIds;
    }

    public static List<String> convertKnowledgeIdsByPeriodId2KnowledgeDesc(Map<String, PeriodKnowledgeDesc> periodId2PeriodKnowledgeDesc) {
        if (null == periodId2PeriodKnowledgeDesc || periodId2PeriodKnowledgeDesc.isEmpty())
            return Collections.emptyList();

        List<String> knowledgeIds = new ArrayList<>();
        for (Map.Entry<String, PeriodKnowledgeDesc> entry : periodId2PeriodKnowledgeDesc.entrySet()) {

            PeriodKnowledgeDesc periodKnowledgeDesc = entry.getValue();
            List<FirKnowledgeDesc> listOfFirKnowledgeDesc = periodKnowledgeDesc.getListOfKnowledgeDesc();
            if (null == listOfFirKnowledgeDesc || listOfFirKnowledgeDesc.isEmpty())
                continue;

            for (FirKnowledgeDesc firKnowledgeDesc : listOfFirKnowledgeDesc) {
                List<SecKnowledgeDesc> listOfSecKnowledgeDesc = firKnowledgeDesc.getListOfSecKnowledgeDesc();
                if (null == listOfSecKnowledgeDesc || listOfSecKnowledgeDesc.isEmpty())
                    continue;

                knowledgeIds.addAll(listOfSecKnowledgeDesc.stream().map(SecKnowledgeDesc::getId).collect(Collectors.toList()));
            }
        }

        return knowledgeIds;
    }

    public static List<PeriodMasterState> convertPeriodMasterStatesByClassifiedKnowledgeMasAndPeriodId2KnowledgeDesc(
            ClassifiedKnowledgeMas classifiedKnowledgeMas, Map<String, PeriodKnowledgeDesc> periodId2PeriodKnowledgeDesc,Map<String,Boolean> knowledgeId2Open) {

        List<PeriodMasterState> listOfPeriodMasterState = new ArrayList<>();
        for (Map.Entry<String, PeriodKnowledgeDesc> entry : periodId2PeriodKnowledgeDesc.entrySet()) {

            PeriodKnowledgeDesc periodKnowledgeDesc = entry.getValue();

            PeriodMasterState periodMasterState = new PeriodMasterState();
            periodMasterState.setContent(periodKnowledgeDesc.getName());
            periodMasterState.setId(entry.getKey());
            periodMasterState.setSeqNum(Integer.valueOf(periodKnowledgeDesc.getSeq()));
            periodMasterState.setFatherCustomizationDTOList(
                    _convertMasterKnowledgeStateByListOfKnowledgeDescAndClassifiedKnowledgeMas(
                            periodKnowledgeDesc.getListOfKnowledgeDesc(), classifiedKnowledgeMas,knowledgeId2Open
                    )
            );

            List<String> knowledgeIds = _getKnowledgeIdsByPeriodKnowledgeDesc(entry.getValue());
            QuantityOfKnowledgeMas quantityOfKnowledgeMas = _generateQuantityOfKnowledgeMas(classifiedKnowledgeMas, knowledgeIds);

            periodMasterState.setKeyValuePairsListRate(_calcCommKvPairsByQuantityOfKnowledgeMas(quantityOfKnowledgeMas));

            listOfPeriodMasterState.add(periodMasterState);
        }

        return listOfPeriodMasterState;
    }

    private static QuantityOfKnowledgeMas _generateQuantityOfKnowledgeMas(ClassifiedKnowledgeMas classifiedKnowledgeMas, List<String> knowledgeIds) {
        QuantityOfKnowledgeMas quantityOfKnowledgeMas = new QuantityOfKnowledgeMas();
        if (null == classifiedKnowledgeMas || null == knowledgeIds || knowledgeIds.isEmpty())
            return quantityOfKnowledgeMas;

        int masterful = 0;
        int unversed = 0;
        int unfirm = 0;
        int nonesample = 0;
        for (String knowledgeId : knowledgeIds) {
            if (classifiedKnowledgeMas.getNoneSampleKnowledgeIds().contains(knowledgeId)) {
                nonesample++;
            } else if (classifiedKnowledgeMas.getUnfirmKnowledgeIds().contains(knowledgeId)) {
                unfirm++;
            } else if (classifiedKnowledgeMas.getUnversedKnowledgeIds().contains(knowledgeId)) {
                unversed++;
            } else if (classifiedKnowledgeMas.getMasterfulKnowledgeIds().contains(knowledgeId)) {
                masterful++;
            }
        }

        quantityOfKnowledgeMas.setQuantityOfMasterful(masterful);
        quantityOfKnowledgeMas.setQuantityOfNoneSample(nonesample);
        quantityOfKnowledgeMas.setQuantityOfUnfirm(unfirm);
        quantityOfKnowledgeMas.setQuantityOfUnversed(unversed);

        return quantityOfKnowledgeMas;
    }

    private static List<String> _getKnowledgeIdsByPeriodKnowledgeDesc(PeriodKnowledgeDesc periodKnowledgeDesc) {
        if (null == periodKnowledgeDesc)
            return Collections.emptyList();

        List<FirKnowledgeDesc> firKnowledgeDescs = periodKnowledgeDesc.getListOfKnowledgeDesc();
        if (null == firKnowledgeDescs || firKnowledgeDescs.isEmpty())
            return Collections.emptyList();

        List<String> knowledgeIds = new ArrayList<>();
        for (FirKnowledgeDesc firKnowledgeDesc : firKnowledgeDescs) {
            List<SecKnowledgeDesc> secKnowledgeDescs = firKnowledgeDesc.getListOfSecKnowledgeDesc();
            if (null == secKnowledgeDescs || secKnowledgeDescs.isEmpty())
                continue;

            secKnowledgeDescs.forEach(secKnowledgeDesc -> knowledgeIds.add(secKnowledgeDesc.getId()));
        }

        return knowledgeIds;
    }

    private static List<CommKvPair> _calcCommKvPairsByQuantityOfKnowledgeMas(QuantityOfKnowledgeMas quantityOfKnowledgeMas) {

        List<CommKvPair> commKvPairs = new ArrayList<>();

        int totalNumber = _calcTotalQuantityOfKnowledgeByQuantityOfKnowledgeMas(quantityOfKnowledgeMas);

        commKvPairs.add(_calcCommKvPair(quantityOfKnowledgeMas.getQuantityOfMasterful(), totalNumber, QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL));
        commKvPairs.add(_calcCommKvPair(quantityOfKnowledgeMas.getQuantityOfUnversed(), totalNumber, QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED));
        commKvPairs.add(_calcCommKvPair(quantityOfKnowledgeMas.getQuantityOfUnfirm(), totalNumber, QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM));
        commKvPairs.add(_calcCommKvPair(quantityOfKnowledgeMas.getQuantityOfNoneSample(), totalNumber, QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE));

        return commKvPairs;
    }

    private static CommKvPair _calcCommKvPair(int size, int totalNumber, int masterKnowledgeLevelMasterful) {

        CommKvPair commKvPair = new CommKvPair();
        commKvPair.setKey(findChineseMasterLevelByMasterLvlCode(masterKnowledgeLevelMasterful));
        if (0 == totalNumber)
            commKvPair.setValue("0.00");
        else
            commKvPair.setValue(GradeConvertUtil.roundDoubleNum(2, String.valueOf(size / (double) totalNumber)).toString());
        commKvPair.setSort(sortOfMasterLvl(masterKnowledgeLevelMasterful));

        return commKvPair;
    }

    private static Integer sortOfMasterLvl(int masterLvlCode) {
        if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL)
            return 4;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM)
            return 3;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED)
            return 2;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE)
            return 1;
        else
            return 0;
    }

    private static int _calcTotalQuantityOfKnowledgeByQuantityOfKnowledgeMas(QuantityOfKnowledgeMas quantityOfKnowledgeMas) {
        if (null == quantityOfKnowledgeMas)
            return 0;

        return quantityOfKnowledgeMas.getQuantityOfMasterful() + quantityOfKnowledgeMas.getQuantityOfNoneSample() +
                quantityOfKnowledgeMas.getQuantityOfUnfirm() + quantityOfKnowledgeMas.getQuantityOfUnversed();
    }

    private static List<PeriodMasterState.FirKnowledge> _convertMasterKnowledgeStateByListOfKnowledgeDescAndClassifiedKnowledgeMas(
            List<FirKnowledgeDesc> listOfKnowledgeDesc, ClassifiedKnowledgeMas classifiedKnowledgeMas,Map<String,Boolean> knowledgeId2Open) {
        if (null == listOfKnowledgeDesc || listOfKnowledgeDesc.isEmpty())
            return Collections.emptyList();

        List<PeriodMasterState.FirKnowledge> firKnowledges = new ArrayList<>();
        for (FirKnowledgeDesc firKnowledgeDesc : listOfKnowledgeDesc) {

            PeriodMasterState.FirKnowledge firKnowledge = new PeriodMasterState.FirKnowledge();
            firKnowledge.setId(firKnowledgeDesc.getId());
            firKnowledge.setSeqNum(firKnowledgeDesc.getSeq());
            firKnowledge.setContent(firKnowledgeDesc.getName());
            firKnowledge.setCustomizationDTOList(
                    _convertMasterSecKnowledgeStateByListOfSecKnowledgeDescAndClassifiedKnowledgeMas(
                            firKnowledgeDesc.getListOfSecKnowledgeDesc(), classifiedKnowledgeMas,knowledgeId2Open
                    )
            );

            firKnowledges.add(firKnowledge);
        }

        return firKnowledges;
    }

    private static List<PeriodMasterState.SecKnowledge> _convertMasterSecKnowledgeStateByListOfSecKnowledgeDescAndClassifiedKnowledgeMas(
            List<SecKnowledgeDesc> listOfSecKnowledgeDesc, ClassifiedKnowledgeMas classifiedKnowledgeMas,Map<String,Boolean> knowledgeId2Open) {

        if (null == listOfSecKnowledgeDesc || listOfSecKnowledgeDesc.isEmpty())
            return Collections.emptyList();

        List<PeriodMasterState.SecKnowledge> secKnowledges = new ArrayList<>();
        for (SecKnowledgeDesc secKnowledgeDesc : listOfSecKnowledgeDesc) {

            PeriodMasterState.SecKnowledge secKnowledge = new PeriodMasterState.SecKnowledge();

            String knowledgeId = secKnowledgeDesc.getId();

            secKnowledge.setSeqNum(secKnowledgeDesc.getSeq());
            secKnowledge.setKnowledgeId(knowledgeId);
            secKnowledge.setKnowledgeName(secKnowledgeDesc.getName());
            secKnowledge.setLevel(_findMasterLevelByKnowledgeIdAndClassifiedKnowledgeMas(knowledgeId, classifiedKnowledgeMas));
            secKnowledge.setMasterDegree(findChineseMasterLevelByMasterLvlCode(secKnowledge.getLevel()));
            secKnowledge.setMasterDegreeUrl(_findMasterDisplayUrlByLvlCode(secKnowledge.getLevel()));

            if (knowledgeId2Open.containsKey(knowledgeId)) {
                secKnowledge.setOpen(knowledgeId2Open.get(knowledgeId));
            } else {
                secKnowledge.setOpen(false);
            }

            secKnowledges.add(secKnowledge);
        }

        return secKnowledges;
    }

    private static String _findMasterDisplayUrlByLvlCode(Integer masterLvlCode) {
        if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL)
            return CONST.DIAGNOSE_DISPLAY_IMG_URI_PREFIX_MASTERFUL;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM)
            return CONST.DIAGNOSE_DISPLAY_IMG_URI_PREFIX_UNFIRM;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED)
            return CONST.DIAGNOSE_DISPLAY_IMG_URI_PREFIX_UNVERSED;
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE)
            return CONST.DIAGNOSE_DISPLAY_IMG_URI_PREFIX_NONE_SAMPLE;
        else
            return CONST.DIAGNOSE_DISPLAY_IMG_URI_PREFIX_DEFAULT;
    }

    public static String findChineseMasterLevelByMasterLvlCode(Integer masterLvlCode) {
        if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL)
            return "掌握";
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM)
            return "不牢固";
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED)
            return "未掌握";
        else if (masterLvlCode == QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE)
            return "未做题";
        else
            return "未知";
    }


    private static Integer _findMasterLevelByKnowledgeIdAndClassifiedKnowledgeMas(String knowledgeId, ClassifiedKnowledgeMas classifiedKnowledgeMas) {

        List<String> masterfulKnowledgeIds = classifiedKnowledgeMas.getMasterfulKnowledgeIds();  //掌握的知识点
        List<String> unfirmKnowledgeIds = classifiedKnowledgeMas.getUnfirmKnowledgeIds();     //不牢固的知识点
        List<String> unversedKnowledgeIds = classifiedKnowledgeMas.getUnversedKnowledgeIds();   //未掌握的知识点
        List<String> noneSampleKnowledgeIds = classifiedKnowledgeMas.getNoneSampleKnowledgeIds(); //没有做题记录的知识点

        if (masterfulKnowledgeIds.contains(knowledgeId))
            return QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL;
        else if (unfirmKnowledgeIds.contains(knowledgeId))
            return QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM;
        else if (unversedKnowledgeIds.contains(knowledgeId))
            return QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED;
        else if (noneSampleKnowledgeIds.contains(knowledgeId))
            return QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE;
        else
            return 0;
    }

    public static List<SingleMasterLvlState> calcSingleMasterLvlStatesByClassifiedKnowledgeMasAndTotalSize(ClassifiedKnowledgeMas classifiedKnowledgeMas, int total) {

        List<SingleMasterLvlState> singleMasterLvlStates = new ArrayList<>();

        singleMasterLvlStates.add(_convertSingleMasterLvlStateByLvlCodeAndQuantityAndTotal(
                QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL, classifiedKnowledgeMas.getMasterfulKnowledgeIds().size(), total
                )
        );

        singleMasterLvlStates.add(_convertSingleMasterLvlStateByLvlCodeAndQuantityAndTotal(
                QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM, classifiedKnowledgeMas.getUnfirmKnowledgeIds().size(), total
                )
        );

        singleMasterLvlStates.add(_convertSingleMasterLvlStateByLvlCodeAndQuantityAndTotal(
                QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED, classifiedKnowledgeMas.getUnversedKnowledgeIds().size(), total
                )
        );

        singleMasterLvlStates.add(_convertSingleMasterLvlStateByLvlCodeAndQuantityAndTotal(
                QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE, classifiedKnowledgeMas.getNoneSampleKnowledgeIds().size(), total
                )
        );

        return singleMasterLvlStates;
    }

    private static SingleMasterLvlState _convertSingleMasterLvlStateByLvlCodeAndQuantityAndTotal(int masterLvlCode, int quantity, int total) {

        SingleMasterLvlState singleMasterLvlState = new SingleMasterLvlState();
        singleMasterLvlState.setLevel(masterLvlCode);
        singleMasterLvlState.setMasterNumber(Double.valueOf(quantity));
        singleMasterLvlState.setKey(findChineseMasterLevelByMasterLvlCode(masterLvlCode));
//        singleMasterLvlState.setMasterRate(_calcRateForDisplay(Double.valueOf(quantity), Double.valueOf(total)));

        //TODO 注意：这里的命名方式有问题，这个字段是说需要传入"2/45"这种形式的字符串
//        singleMasterLvlState.setMasterScoreRate(String.valueOf(quantity)+"/"+String.valueOf(total));

        return singleMasterLvlState;
    }

//    private static String _calcRateForDisplay(Double aDouble, Double aDouble1) {
//        String stringRate = GradeConvertUtil.roundDoubleNum(2, String.valueOf(aDouble / aDouble1)).toString();
//        if(aDouble>0 && stringRate.equalsIgnoreCase("0.0")){
//            return "0.01";
//        }else{
//            return stringRate;
//        }
//    }

    public static String selectQuestionIdsAndPlaceHolderQIdByListOfQuestionStatistics(List<QuestionStatisticsDigest> listOfQuestionStatistics, List<String> exclusiveQuestionIds) {
        if (null == listOfQuestionStatistics || listOfQuestionStatistics.isEmpty()) {
            return "";
        }

        String placeholderQId = "";
        Long latestTime = null;
        for (QuestionStatisticsDigest questionStatisticsDigest : listOfQuestionStatistics) {
            String questionId = questionStatisticsDigest.getQuestionId();
            if (null == latestTime || latestTime > questionStatisticsDigest.getLatestTime()) {
                placeholderQId = questionId;
                latestTime = questionStatisticsDigest.getLatestTime();
            }
            exclusiveQuestionIds.add(questionId);
        }

        return placeholderQId;
    }

    public static Question convertQbuQuestionByQbQuestion(QuestionMapItem questionItem) {

        Question question = new Question();

        question.setScore(10);   //默认分数为10分
        question.setProperties(BusinessUtils.generateQuestionPropertiesByQuestionItem(questionItem));
        question.setScorePointDict(BusinessUtils.generateSpId2ScoreByAnswerKeyAndQuestionScore(questionItem.getAnswerKey(), question.getScore()));

        return question;
    }

    public static QuestionProperties generateQuestionPropertiesByQuestionItem(QuestionMapItem questionItem) {
        if (null == questionItem) {
            return null;
        }

        IntegrateQuestionItem integrateQuestionItem = questionItem.getIntegrateQuestionItem();

        QuestionProperties questionProperties = new QuestionProperties();
        Map<String, List<LightTagItem>> propertyKey2Items = questionItem.getQCapabilityMap();
        questionProperties.setQuestionId(questionItem.getIntegrateQuestionItem().getId());
        questionProperties.setDifficulty(integrateQuestionItem.getDifficulty());
        questionProperties.setCapability(
                _convertTagIdByListOfLightTagItem(propertyKey2Items.get(RqbServantsConst.KEY_FOR_CAPABILITY))
        );
        questionProperties.setCognition(
                _convertTagIdByListOfLightTagItem(propertyKey2Items.get(RqbServantsConst.KEY_FOR_COGNITION))
        );
        questionProperties.setKnowledgeId(
                _convertKnowledgeIdByListOfLightTagItem(propertyKey2Items.get(RqbServantsConst.KEY_FOR_KNOWLEDGE))
        );

        return questionProperties;
    }

    private static String _convertKnowledgeIdByListOfLightTagItem(List<LightTagItem> listOfLightTagItem) {

        for (LightTagItem lightTagItem : listOfLightTagItem) {
            String code = lightTagItem.getCode();
            if (null == code)
                continue;
            //根据code的规则来判断是否是二级知识点
            if (code.matches(".*[A-Z]{1,3}-[A-Z]{1,3}[0-9]+"))
                return lightTagItem.getId();
        }

        return "";
    }

    private static String _convertTagIdByListOfLightTagItem(List<LightTagItem> listOfLightTagItem) {

        String ids = "";
        for (LightTagItem lightTagItem : listOfLightTagItem) {
            ids += (lightTagItem.getId() + ",");
        }

        if (!ids.trim().isEmpty()) {
            ids = ids.substring(0, ids.length() - 1);
        }

        return ids;
    }

    public static SingleQuestion generateSingleQuestionToByQbuQuestionItemAndInstanceId(QuestionMapItem questionMapItem, String instanceId) {

        SingleQuestion singleQuestionToDo = new SingleQuestion();
        singleQuestionToDo.setAnalysisImgUrl(questionMapItem.getIntegrateQuestionItem().getSolutionUrl());
        singleQuestionToDo.setAnswerKey(questionMapItem.getAnswerKey());
        singleQuestionToDo.setQuestion(questionMapItem.getIntegrateQuestionItem().getQuestionBody());
        singleQuestionToDo.setPaperInstanceId(instanceId);
        if (null != questionMapItem.getReferAnswer() && !questionMapItem.getReferAnswer().trim().isEmpty())
            singleQuestionToDo.setReferAnswers(JsonHelper.valueOfMap(questionMapItem.getReferAnswer()));
        singleQuestionToDo.setPaperInstanceId(instanceId);
        singleQuestionToDo.setId(questionMapItem.getIntegrateQuestionItem().getId());

        singleQuestionToDo.setQuestionTypeKey(questionMapItem.getQuestionTypeKey());

        return singleQuestionToDo;
    }

    public static QuestionAnswer generateQuestionAnswerByJsonOfAnswerAndInstanceId(String jsonOfAnswer, String instanceId) {
        ComplexStuAnswer complexStuAnswer = BusinessUtils.generateComplexStuAnswerByJsonOfAnswerAndInstanceId(jsonOfAnswer, instanceId);
        if (null == complexStuAnswer)
            return null;

        if (null == complexStuAnswer.getAnswers() || complexStuAnswer.getAnswers().isEmpty())
            return null;

        return complexStuAnswer.getAnswers().get(0);
    }

    public static Map<String, Map<String, Map<String, Map<Integer, PaperWorkHistory.QuestionScore>>>>
    generateStuIdQId2InsId2RepeatSeq2HistoryQuestionScoreDictByStuIdAndInsId2RepeatSeq2QuestionRecItem(String stuId, Map<String, Map<Integer, QuestionRecordItem>> insId2RepeatSeq2QuestionRecItem, List<String> questionIds) {

        Map<String, Map<String, Map<String, Map<Integer, PaperWorkHistory.QuestionScore>>>> studentId2QuestionScore = new HashMap<>();

        Map<String, Map<String, Map<Integer, PaperWorkHistory.QuestionScore>>> qId2InsId2RepeatSeq2HistoryQuestionScoreDict
                = generateQId2InsId2RepeatSeq2HistoryQuestionScoreDictByInsId2RepeatSeq2QuestionRecItem(insId2RepeatSeq2QuestionRecItem);

        studentId2QuestionScore.put(stuId, qId2InsId2RepeatSeq2HistoryQuestionScoreDict);

        if (null != questionIds)
            questionIds.addAll(qId2InsId2RepeatSeq2HistoryQuestionScoreDict.keySet());

        return studentId2QuestionScore;
    }

    public static Map<String, Map<String, Map<Integer, PaperWorkHistory.QuestionScore>>> generateQId2InsId2RepeatSeq2HistoryQuestionScoreDictByInsId2RepeatSeq2QuestionRecItem(
            Map<String, Map<Integer, QuestionRecordItem>> insId2RepeatSeq2QuestionRecItem) {

        Map<String, Map<String, Map<Integer, PaperWorkHistory.QuestionScore>>> qId2InsId2RepeatSeq2HistoryQuestionScoreDict = new HashMap<>();
        for (Map.Entry<String, Map<Integer, QuestionRecordItem>> entry : insId2RepeatSeq2QuestionRecItem.entrySet()) {
            String insId = entry.getKey();

            Map<Integer, QuestionRecordItem> repeatSeq2QuestionRecordItem = entry.getValue();
            for (Map.Entry<Integer, QuestionRecordItem> entry1 : repeatSeq2QuestionRecordItem.entrySet()) {
                Integer repeatSeq = entry1.getKey();
                QuestionRecordItem questionRecordItem = entry1.getValue();
                String questionId = questionRecordItem.getQuestionId();

                Map<String, Map<Integer, PaperWorkHistory.QuestionScore>> insId2RepeatSeq2HistoryQuestionScoreDict = qId2InsId2RepeatSeq2HistoryQuestionScoreDict.get(questionId);
                if (null == insId2RepeatSeq2HistoryQuestionScoreDict) {
                    insId2RepeatSeq2HistoryQuestionScoreDict = new HashMap<>();
                    qId2InsId2RepeatSeq2HistoryQuestionScoreDict.put(questionId, insId2RepeatSeq2HistoryQuestionScoreDict);
                }

                Map<Integer, PaperWorkHistory.QuestionScore> repeatSeq2HistoryQuestionScoreDict = insId2RepeatSeq2HistoryQuestionScoreDict.get(insId);
                if (null == repeatSeq2HistoryQuestionScoreDict) {
                    repeatSeq2HistoryQuestionScoreDict = new HashMap<>();
                    insId2RepeatSeq2HistoryQuestionScoreDict.put(insId, repeatSeq2HistoryQuestionScoreDict);
                }

                PaperWorkHistory.QuestionScore questionScore = repeatSeq2HistoryQuestionScoreDict.get(repeatSeq);
                if (null == questionScore) {
                    questionScore = convertQuestionScoreByQuestionRecordItem(questionRecordItem);
                    repeatSeq2HistoryQuestionScoreDict.put(repeatSeq, questionScore);
                }
            }
        }


        return qId2InsId2RepeatSeq2HistoryQuestionScoreDict;
    }

    public static Map<String, String> getKnowledgeId2NameFromRqbQuestions(List<QuestionMapItem> questionMapItems) {
        if (null == questionMapItems || questionMapItems.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> knowledgeId2Name = new HashMap<>();
        for (QuestionMapItem question : questionMapItems) {
            List<LightTagItem> lightTagItems = question.getQCapabilityMap().get(RqbServantsConst.KEY_FOR_KNOWLEDGE);
            if (null != lightTagItems && lightTagItems.size() > 0) {
                LightTagItem lightTagItem = lightTagItems.get(0);
                if (null != lightTagItem) {
                    knowledgeId2Name.put(lightTagItem.getId(), lightTagItem.getContent());
                }
            }
        }

        return knowledgeId2Name;
    }

    public static List<SingleQuestion> convertListOfSingleQuestionByQuestionItems(Collection<QuestionMapItem> questionMapItems) {
        List<SingleQuestion> listOfSingleQuestion = new ArrayList<>();
        if (null == questionMapItems || questionMapItems.isEmpty())
            return listOfSingleQuestion;

        for (QuestionMapItem questionMapItem : questionMapItems) {

            SingleQuestion singleQuestion = new SingleQuestion();
            singleQuestion.setAnswerKey(questionMapItem.getAnswerKey());
            singleQuestion.setId(questionMapItem.getIntegrateQuestionItem().getId());
            singleQuestion.setAnswerKey(questionMapItem.getAnswerKey());
            singleQuestion.setQuestion(questionMapItem.getIntegrateQuestionItem().getQuestionBody());
            singleQuestion.setDifficulty(questionMapItem.getIntegrateQuestionItem().getDifficulty());

            listOfSingleQuestion.add(singleQuestion);
        }

        return listOfSingleQuestion;
    }

    public static PaperAnalyzed generatePaperAnalyzedByKnowledgeId2GlobalPaperAnalyzedKnowledgeQuestionAndSId2ClassifiedKnowledgeMas(
            Map<String, PaperAnalyzedKnowledgeQuestion> knowledgeId2GlobalPaperAnalyzedKnowledgeQuestion,
            Map<String, ClassifiedKnowledgeMas> sid2ClassifiedKnowledgeMas, List<Account> stuAccounts) {

        PaperAnalyzed paperAnalyzed = new PaperAnalyzed();

        paperAnalyzed.setKnowledgeQuestionDict(_generatePaperAnalyzedKnowledgeQuestion(knowledgeId2GlobalPaperAnalyzedKnowledgeQuestion));
        paperAnalyzed.setStuId2KnowledgeMasterDict(_generatePaperAnalyzedKnowledgeMaster(sid2ClassifiedKnowledgeMas, stuAccounts));

        return paperAnalyzed;
    }

    private static Map<String, PaperAnalyzed.KnowledgeMaster> _generatePaperAnalyzedKnowledgeMaster(Map<String, ClassifiedKnowledgeMas> sid2ClassifiedKnowledgeMas, List<Account> stuAccounts) {
        Map<String, PaperAnalyzed.KnowledgeMaster> sid2PaperAnalyzedKnowledgeMaster = new HashMap<>();
        Map<String, Account> sid2Account = DSHelper.convertListToDictionary(stuAccounts, Account::getId);
        for (Map.Entry<String, ClassifiedKnowledgeMas> entry : sid2ClassifiedKnowledgeMas.entrySet()) {
            String sid = entry.getKey();
            ClassifiedKnowledgeMas classifiedKnowledgeMas = entry.getValue();

            PaperAnalyzed.KnowledgeMaster knowledgeMaster = new PaperAnalyzed.KnowledgeMaster();
            knowledgeMaster.putQuantityOfMasterLevel(QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_MASTERFUL, classifiedKnowledgeMas.getMasterfulKnowledgeIds());
            knowledgeMaster.putQuantityOfMasterLevel(QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNFIRM, classifiedKnowledgeMas.getUnfirmKnowledgeIds());
            knowledgeMaster.putQuantityOfMasterLevel(QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_UNVERSED, classifiedKnowledgeMas.getUnversedKnowledgeIds());
            knowledgeMaster.putQuantityOfMasterLevel(QbuServantsConst.MASTER_KNOWLEDGE_LEVEL_NONE_SAMPLE, classifiedKnowledgeMas.getNoneSampleKnowledgeIds());

            knowledgeMaster.setProfile(_generateProfileByAccount(sid2Account.get(sid)));

            sid2PaperAnalyzedKnowledgeMaster.put(sid, knowledgeMaster);
        }

        return sid2PaperAnalyzedKnowledgeMaster;
    }

    private static PersonProfile _generateProfileByAccount(Account account) {
        if (null == account)
            return null;

        PersonProfile personProfile = new PersonProfile();
        personProfile.setId(account.getId());
        personProfile.setGender(String.valueOf(account.getGender()));
        personProfile.setName(account.getRealname());

        return personProfile;
    }

    private static Map<String, PaperAnalyzed.KnowledgeQuestion> _generatePaperAnalyzedKnowledgeQuestion(Map<String, PaperAnalyzedKnowledgeQuestion> knowledgeId2GlobalPaperAnalyzedKnowledgeQuestion) {

        Map<String, PaperAnalyzed.KnowledgeQuestion> knowledgeId2PaperAnalyzedKnowledgeQuestion = new HashMap<>();
        for (Map.Entry<String, PaperAnalyzedKnowledgeQuestion> entry : knowledgeId2GlobalPaperAnalyzedKnowledgeQuestion.entrySet()) {
            String knowledgeId = entry.getKey();

            knowledgeId2PaperAnalyzedKnowledgeQuestion.put(knowledgeId, _convertPaperAnalyzedKnowledgeQuestion(entry.getValue()));
        }

        return knowledgeId2PaperAnalyzedKnowledgeQuestion;
    }

    private static PaperAnalyzed.KnowledgeQuestion _convertPaperAnalyzedKnowledgeQuestion(PaperAnalyzedKnowledgeQuestion paperAnalyzedKnowledgeQuestion) {

        PaperAnalyzed.KnowledgeQuestion knowledgeQuestion = new PaperAnalyzed.KnowledgeQuestion();
        knowledgeQuestion.setKnowledgeId(paperAnalyzedKnowledgeQuestion.getKnowledgeId());
        knowledgeQuestion.setKnowledgeTxt(paperAnalyzedKnowledgeQuestion.getKnowledgeTxt());
        knowledgeQuestion.setNum(paperAnalyzedKnowledgeQuestion.getNum());
        knowledgeQuestion.setUnitId(paperAnalyzedKnowledgeQuestion.getUnitId());
        knowledgeQuestion.setQuestions(_convertPaperAnalyzedQId2QuestionDict(paperAnalyzedKnowledgeQuestion.getQuestionDict()));

        return knowledgeQuestion;
    }

    private static Map<String, PaperAnalyzed.Question> _convertPaperAnalyzedQId2QuestionDict(Map<String, PaperAnalyzedQuestion> questionDict) {
        Map<String, PaperAnalyzed.Question> qId2Question = new HashMap<>();
        for (Map.Entry<String, PaperAnalyzedQuestion> entry : questionDict.entrySet()) {
            String qId = entry.getKey();

            PaperAnalyzedQuestion paperAnalyzedQuestion = entry.getValue();

            PaperAnalyzed.Question question = new PaperAnalyzed.Question();
            question.setId(paperAnalyzedQuestion.getId());
            question.setQgSeq(paperAnalyzedQuestion.getQgSeq());
            question.setqSeq(paperAnalyzedQuestion.getQSeq());

            qId2Question.put(qId, question);
        }

        return qId2Question;
    }

    public static List<StudentPaperHistory> convertStuPaperHistoriesByListOfReceivedPaperDesc(List<ReceivedPaperDesc> receivedPaperDescs) {
        return receivedPaperDescs.stream().map(receivedPaperDesc -> convertStudentPaperHistoryByReceivedPaperDescAndEncourages(receivedPaperDesc, null)).collect(Collectors.toList());
    }

    public static String convertChineseApplyStateByStateCode(int studentJoinStatus) {
        String chineseState = "未知";
        switch (studentJoinStatus) {

            case GmServantsConst.STU_APPLY_STATUS_IN_PROGRESS:
                chineseState = "审核中";
                break;

            case GmServantsConst.STU_APPLY_STATUS_PASSED:
                chineseState = "审核通过";
                break;

            case GmServantsConst.STU_APPLY_STATUS_REFUSED:
                chineseState = "审核拒绝";
                break;

            case GmServantsConst.STU_APPLY_STATUS_IGNORED:
                chineseState = "审核忽略";
                break;
        }

        return chineseState;
    }

    public static List<FavoritePaperItemDto> convertFavoritePaperItemList2FavoritePaperItemDtoList(List<FavoritePaperItem> favoritePaperItemList) {
        List<FavoritePaperItemDto> favoritePaperItemDtoList = new ArrayList<>();
        for (FavoritePaperItem favoritePaperItem : favoritePaperItemList) {
            FavoritePaperItemDto favoritePaperItemDto = new FavoritePaperItemDto();
            favoritePaperItemDto.setCreateTime(new Date(favoritePaperItem.getCreateTime()));
            favoritePaperItemDto.setId(favoritePaperItem.getId());
            favoritePaperItemDto.setName(favoritePaperItem.getName());
            favoritePaperItemDto.setType(favoritePaperItem.getType());
            favoritePaperItemDtoList.add(favoritePaperItemDto);
        }
        return favoritePaperItemDtoList;
    }


    public static Map<String, String> getSingleExcellentAnswerRefMapFromSp(String answerKey, Map<String, Map<String, String>> studentAnswersMap, Map<String, Account> accountMap) {

        Map<String, String> excellentRefAnswerMap = new HashMap<>();

        Map<String, List<String>> spId2refBoxMap = new HashMap<>();

        boolean ignoreTxtNotice = false;
        try {
            List<GradeScorePointAnswer> gradeScorePointAnswerList = GradeUtil.ConvertAnswerKey2GradeFormat(answerKey);

            for (GradeScorePointAnswer gradeScorePointAnswer : gradeScorePointAnswerList) {

                List<ScorePointAnswer> scorePointAnswers = gradeScorePointAnswer.getScorePoints();

                //1.应用题,列式解答
                if (gradeScorePointAnswer.getType().equalsIgnoreCase(CONSTANT.SCORE_RULE_APPLICATION) ||
                        //2.解方程
                        gradeScorePointAnswer.getType().equalsIgnoreCase(CONSTANT.SCORE_RULE_VARIABLE) ||
                        //3.脱式计算
                        gradeScorePointAnswer.getType().equalsIgnoreCase(CONSTANT.SCORE_RULE_PULL_EXP)) {


                    for (ScorePointAnswer scorePointAnswer : scorePointAnswers) {
                        if (null == scorePointAnswer)
                            continue;

                        List<String> referInputboxIds = new ArrayList<>();
                        //这三种题型的得分点和输入框是一一对应的，所以可以这样写
                        String referInputboxId = scorePointAnswer.getReferInputBoxes().get(0).getUuid();
                        referInputboxIds.add(referInputboxId);

                        spId2refBoxMap.put(scorePointAnswer.getUuid(), referInputboxIds);
                    }
                } else {
                    ignoreTxtNotice = true;
                    for (ScorePointAnswer scorePointAnswer : scorePointAnswers) {
                        if (null == scorePointAnswer)
                            continue;
                        List<String> referInputboxIds = new ArrayList<>();

                        List<ScorePointAnswer.ReferInputBox> referInputBoxes = scorePointAnswer.getReferInputBoxes();
                        for (ScorePointAnswer.ReferInputBox referInputBox : referInputBoxes) {
                            String referInputboxId = referInputBox.getUuid();
                            referInputboxIds.add(referInputboxId);
                        }
                        spId2refBoxMap.put(scorePointAnswer.getUuid(), referInputboxIds);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (spId2refBoxMap.isEmpty())
            return null;

        ExcellentAnswer excellentAnswer = new ExcellentAnswer();
        for (Map.Entry<String, Map<String, String>> entry : studentAnswersMap.entrySet()) {

            String assignee = entry.getKey();
            Account account = accountMap.get(assignee);
            if (null != account) {
                excellentAnswer.setStuId(account.getId());
                excellentAnswer.setStuName(account.getRealname());
                excellentAnswer.setGender(account.getGender() == 1 ? "男" : "女");
            }

            Map<String, String> sp2Ans = entry.getValue();

            for (Map.Entry<String, String> answerEntry : sp2Ans.entrySet()) {
                String spId = answerEntry.getKey();
                List<String> refBoxIds = spId2refBoxMap.get(spId);

                String answer = sp2Ans.get(spId);

                if (!ignoreTxtNotice) {
                    StringBuilder name = new StringBuilder(" " + excellentAnswer.getStuName() + " 的解答如下:");
                    name.replace(1, 2, "*");
                    if (null != answer) {
                        name.append("\\n\\n-----------------------------\\n\\n").append(answer);
                    }
                    for (String refBoxId : refBoxIds) {
                        excellentAnswer.addAnsToInputbox(refBoxId, name.toString());
                    }
                } else {
                    int i = 0;
                    String[] answerArray;
                    if (answer.contains("matrix")) { //不进行分割
                        answerArray = answer.split("##@@@@@@##");
                    } else {
                        answerArray = answer.split("#");
                    }
                    for (String refBoxId : refBoxIds) {
                        String newAnswer = "";
                        if (answerArray.length > i) {
                            newAnswer = answerArray[i];
                        }
                        excellentAnswer.addAnsToInputbox(refBoxId, newAnswer);
                        i++;
                    }
                }
            }
        }
        return excellentAnswer.getRef2Answer();
    }

    public static QuestionForQusDto convertQuestionMapItemToQuestionForQusDto(QuestionMapItem questionMapItem) {
        if (null == questionMapItem) {
            return null;
        }
        IntegrateQuestionItem integrateQuestionItem = questionMapItem.getIntegrateQuestionItem();
        QuestionForQusDto questionForQusDto = new QuestionForQusDto();
        questionForQusDto.setAnswerKey(questionMapItem.getAnswerKey());
        questionForQusDto.setDoc(questionMapItem.getDoc());
        questionForQusDto.setQuestion(integrateQuestionItem.getQuestionBody());
        questionForQusDto.setReferAnswer(questionMapItem.getReferAnswer());
        questionForQusDto.setSource(questionMapItem.getSource());
        QuestionForQusDto.Basic basic = new QuestionForQusDto.Basic();
        basic.setId(integrateQuestionItem.getId());
        basic.setDoc(integrateQuestionItem.getDoc());
        basic.setDifficulty(integrateQuestionItem.getDifficulty());
        basic.setDifficultyName(integrateQuestionItem.getDifficultyName());
        basic.setIndex(integrateQuestionItem.getIndex());
        basic.setSolutionUrl(integrateQuestionItem.getSolutionUrl());
        basic.setTagContext(integrateQuestionItem.getTagContext());
        basic.setType(integrateQuestionItem.getType());
        basic.setTypeName(integrateQuestionItem.getTypeName());
        basic.setCreatedTime(integrateQuestionItem.getCreatedTime());
        basic.setUpdatedTime(integrateQuestionItem.getUpdatedTime());
        basic.setTags(integrateQuestionItem.getTags());
        questionForQusDto.setBasic(basic);

        return questionForQusDto;
    }

    public static List<ViewDistrictDto> generateDistrictViewDistrictDtos(List<AreaSchoolsItem> areaSchoolsItems,
                                                                         List<LocSchoolSimpleItem> allLocSchoolSimpleItems,
                                                                         List<TrgPromotion> trgPromotions,
                                                                         List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts) {
        List<ViewDistrictDto> viewDistrictDtos = new ArrayList<>();
        Map<String, List<LocSchoolSimpleItem>> dist2SchoolMap = StreamHelper.convertListToMapList(allLocSchoolSimpleItems, LocSchoolSimpleItem::getFkDistrictId);
        for (AreaSchoolsItem areaSchoolsItem : areaSchoolsItems) {
            String distId = areaSchoolsItem.getId();
            ViewDistrictDto childViewDistrictDto = getOneDistrictStat(distId, areaSchoolsItem.getName(), dist2SchoolMap.get(distId), trgPromotions, schoolAndTeacherCntAndStuCnts);
            viewDistrictDtos.add(childViewDistrictDto);
        }
        return viewDistrictDtos;
    }

    public static List<ViewDistrictDto> generateProvinceViewDistrictDtos(List<AreaSchoolsItem> areaSchoolsItems,
                                                                         List<LocSchoolSimpleItem> allLocSchoolSimpleItems,
                                                                         List<TrgPromotion> trgPromotions,
                                                                         List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts) {
        List<ViewDistrictDto> viewDistrictDtos = new ArrayList<>();
        Map<String, List<LocSchoolSimpleItem>> dist2SchoolMap = StreamHelper.convertListToMapList(allLocSchoolSimpleItems, LocSchoolSimpleItem::getFkCityId);
        for (AreaSchoolsItem areaSchoolsItem : areaSchoolsItems) {
            ViewDistrictDto viewDistrictDto = _generateViewDistrictDtoByProvinceOrDistrict(areaSchoolsItem, dist2SchoolMap, trgPromotions, schoolAndTeacherCntAndStuCnts, CONST.AREA_TYPE_PROVINCE, CONST.AREA_TYPE_CITY);
            viewDistrictDtos.add(viewDistrictDto);
        }
        return viewDistrictDtos;
    }


    public static List<ViewDistrictDto> generateCityViewDistrictDtos(List<AreaSchoolsItem> areaSchoolsItems,
                                                                     List<LocSchoolSimpleItem> allLocSchoolSimpleItems,
                                                                     List<TrgPromotion> trgPromotions,
                                                                     List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts) {
        List<ViewDistrictDto> viewDistrictDtos = new ArrayList<>();
        Map<String, List<LocSchoolSimpleItem>> dist2SchoolMap = StreamHelper.convertListToMapList(allLocSchoolSimpleItems, LocSchoolSimpleItem::getFkDistrictId);
        for (AreaSchoolsItem areaSchoolsItem : areaSchoolsItems) {
            ViewDistrictDto viewDistrictDto = _generateViewDistrictDtoByProvinceOrDistrict(areaSchoolsItem, dist2SchoolMap, trgPromotions, schoolAndTeacherCntAndStuCnts, CONST.AREA_TYPE_CITY, CONST.AREA_TYPE_DISTRICT);
            viewDistrictDtos.add(viewDistrictDto);
        }
        return viewDistrictDtos;
    }


    private static ViewDistrictDto _generateViewDistrictDtoByProvinceOrDistrict(AreaSchoolsItem areaSchoolsItem,
                                                                                Map<String, List<LocSchoolSimpleItem>> dist2SchoolMap,
                                                                                List<TrgPromotion> trgPromotions,
                                                                                List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts,
                                                                                int areaType, int childAreaType) {
        List<ChildAddressItem> childAddressItems = areaSchoolsItem.getChildAddressItems();
        ViewDistrictDto viewDistrictDto = new ViewDistrictDto();
        viewDistrictDto.setAreaType(areaType);
        viewDistrictDto.setId(areaSchoolsItem.getId());
        viewDistrictDto.setName(areaSchoolsItem.getName());
        int studentCount = 0, teacherCount = 0, aliveCount = 0;

        if (null == childAddressItems || childAddressItems.isEmpty()) {
            _generateNullStat(viewDistrictDto);
        } else {
            List<ViewDistrictDto> childViewDistrictDtos = new ArrayList<>();
            for (ChildAddressItem childAddressItem : childAddressItems) {
                String distId = childAddressItem.getId();
                ViewDistrictDto childViewDistrictDto = getOneDistrictStat(distId, childAddressItem.getName(), dist2SchoolMap.get(distId), trgPromotions, schoolAndTeacherCntAndStuCnts);
                childViewDistrictDto.setAreaType(childAreaType);
                childViewDistrictDtos.add(childViewDistrictDto);
                studentCount += childViewDistrictDto.getStudentCount();
                teacherCount += childViewDistrictDto.getTeacherCount();
                aliveCount += childViewDistrictDto.getActiveCount();
            }
            viewDistrictDto.setChildViewDistricts(childViewDistrictDtos);
            viewDistrictDto.setStudentCount(studentCount);
            viewDistrictDto.setTeacherCount(teacherCount);
            viewDistrictDto.setActiveCount(aliveCount);
        }
        return viewDistrictDto;
    }


    private static void _generateNullStat(ViewDistrictDto viewDistrictDto) {
        viewDistrictDto.setTeacherCount(0);
        viewDistrictDto.setStudentCount(0);
        viewDistrictDto.setActiveCount(0);
        viewDistrictDto.setChildViewDistricts(Collections.emptyList());
    }


    public static ViewDistrictDto getOneDistrictStat(String distId, String distName,
                                                     List<LocSchoolSimpleItem> distSchoolList,
                                                     List<TrgPromotion> trgPromotions,
                                                     List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts) {

        ViewDistrictDto viewDistrictDto = new ViewDistrictDto();
        viewDistrictDto.setId(distId);
        viewDistrictDto.setName(distName);
        viewDistrictDto.setAreaType(CONST.AREA_TYPE_DISTRICT);
        if (null == distSchoolList || distSchoolList.isEmpty()) {//说明该区域下没有学校
            _generateNullStat(viewDistrictDto);
            return viewDistrictDto;
        }
        //获取该区域下的活跃数据
        List<String> schoolIds = StreamHelper.getProps(distSchoolList, LocSchoolSimpleItem::getId);
        Map<String, Integer> schoolId2ActiveCountMap = new HashMap<>();
        int ActiveCount = _getDistActiveCount(trgPromotions, schoolIds, schoolId2ActiveCountMap);
        viewDistrictDto.setActiveCount(ActiveCount);

        //设置该区域下的老师人数和学生人数
        _setStuCntAndTeaCntOfViewDistrictDto(schoolAndTeacherCntAndStuCnts, schoolId2ActiveCountMap, schoolIds, viewDistrictDto);

        return viewDistrictDto;
    }


    private static void _setStuCntAndTeaCntOfViewDistrictDto(List<SchoolAndTeacherCntAndStuCnt> schoolAndTeacherCntAndStuCnts, Map<String, Integer> schoolId2ActiveCountMap, List<String> schoolIds,
                                                             ViewDistrictDto viewDistrictDto) {
        if (null == schoolAndTeacherCntAndStuCnts || schoolAndTeacherCntAndStuCnts.isEmpty()) {
            viewDistrictDto.setStudentCount(0);
            viewDistrictDto.setTeacherCount(0);
            viewDistrictDto.setChildViewDistricts(Collections.emptyList());
            return;
        }
        int distStudentCount = 0;
        int distTeacherCount = 0;
        List<ViewDistrictDto> chilViewDistricts = new ArrayList<>();
        Map<String, SchoolAndTeacherCntAndStuCnt> schoolId2CntMap = StreamHelper.convertListToMap(schoolAndTeacherCntAndStuCnts, SchoolAndTeacherCntAndStuCnt::getSchoolId);
        //这里的schoolAndTeacherCntAndStuCnts已经是group by school的
        for (String schoolId : schoolIds) {
            SchoolAndTeacherCntAndStuCnt schoolAndTeacherCntAndStuCnt = schoolId2CntMap.get(schoolId);
            if (null == schoolAndTeacherCntAndStuCnt) {
                continue;
            }
            distStudentCount += schoolAndTeacherCntAndStuCnt.getStudentCount();
            distTeacherCount += schoolAndTeacherCntAndStuCnt.getTeacherCount();
            ViewDistrictDto viewDistrictDto1 = new ViewDistrictDto();
            viewDistrictDto1.setId(schoolAndTeacherCntAndStuCnt.getSchoolId());
            viewDistrictDto1.setName(schoolAndTeacherCntAndStuCnt.getSchoolName());
            viewDistrictDto1.setStudentCount(schoolAndTeacherCntAndStuCnt.getStudentCount());
            viewDistrictDto1.setTeacherCount(schoolAndTeacherCntAndStuCnt.getTeacherCount());
            viewDistrictDto1.setAreaType(CONST.AREA_TYPE_SCHOOL);
            if (null == schoolId2ActiveCountMap.get(schoolAndTeacherCntAndStuCnt.getSchoolId())) {
                viewDistrictDto1.setActiveCount(0);
            } else {
                viewDistrictDto1.setActiveCount(schoolId2ActiveCountMap.get(schoolAndTeacherCntAndStuCnt.getSchoolId()));
            }

            chilViewDistricts.add(viewDistrictDto1);
        }
        viewDistrictDto.setStudentCount(distStudentCount);
        viewDistrictDto.setTeacherCount(distTeacherCount);
        viewDistrictDto.setChildViewDistricts(chilViewDistricts);
    }


    public static int _getDistActiveCount(List<TrgPromotion> trgPromotions, List<String> schoolIds, Map<String, Integer> schoolId2ActiveCountMap) {
        if (null == trgPromotions || trgPromotions.isEmpty()) {
            return 0;
        }
        Map<String, List<TrgPromotion>> schoolId2TrgPromotionsMap = StreamHelper.convertListToMapList(trgPromotions, TrgPromotion::getSchoolId);
        int retTotalCount = 0;
        int schoolActiveCount = 0;
        for (String schoolId : schoolIds) {
            List<TrgPromotion> trgPromotionList = schoolId2TrgPromotionsMap.get(schoolId);
            if (null == trgPromotionList || trgPromotionList.isEmpty()) {
                schoolId2ActiveCountMap.put(schoolId, 0);
                continue;
            }
            schoolActiveCount = _getSchoolActiveCount(trgPromotionList);
            retTotalCount += schoolActiveCount;
            schoolId2ActiveCountMap.put(schoolId, schoolActiveCount);
        }
        return retTotalCount;
    }


    private static int _getSchoolActiveCount(List<TrgPromotion> trgPromotions) {
        int retCount = 0;
        String goodId = null;
        for (TrgPromotion trgPromotion : trgPromotions) {
            goodId = trgPromotion.getGoodId();
            if (_isSelfGroup(trgPromotion.getClassId()) && !isTrSupperUser) {
                continue;
            }
            if (null != goodId && (goodId.contains(CONST.GOODS_CATEGORY_TRAINING_CAMP_A)
                    || goodId.contains(CONST.GOODS_CATEGORY_TRAINING_CAMP_A_DISCOUNT))) {
                retCount++;
            }
        }
        return retCount;
    }

    private static boolean _isSelfGroup(String classId) {
        if (null == classId || classId.isEmpty()) {
            return false;
        }
        //91600000
        if (classId.length() == 8 && classId.startsWith("9")) {
            return true;
        }
        return false;
    }

    private static int _getQuantityOfPlan(List<TrgPromotion> trgPromotions, String goodType) {
        int retCount = 0;
        String goodId = null;
        for (TrgPromotion trgPromotion : trgPromotions) {
            if (_isSelfGroup(trgPromotion.getClassId()) && !isTrSupperUser) {
                continue;
            }
            goodId = trgPromotion.getGoodId();
            if (null != goodId && (goodId.contains(goodType))) {
                retCount++;
            }
        }
        return retCount;
    }


    public static List<ViewSchoolDto> generateViewSchoolDto(List<GroupsOfSchool> groupsOfSchools, List<TrgPromotion> trgPromotions, List<Account> accounts) {
        if (null == groupsOfSchools || groupsOfSchools.isEmpty()) {
            return null;
        }
        Map<String, Account> teacherId2AccountMap = StreamHelper.convertListToMap(accounts, Account::getId);
        Map<String, List<TrgPromotion>> schoolId2TrgPromotionsMap = StreamHelper.convertListToMapList(trgPromotions, TrgPromotion::getSchoolId);
        Map<String, List<TrgPromotion>> groupId2TrgPromotionsMap = StreamHelper.convertListToMapList(trgPromotions, TrgPromotion::getClassId);
        List<ViewSchoolDto> viewShoolDtos = new ArrayList<>();
        for (GroupsOfSchool groupsOfSchool : groupsOfSchools) {
            ViewSchoolDto viewSchoolDto = new ViewSchoolDto();
            viewSchoolDto.setId(groupsOfSchool.getSchoolId());
            viewSchoolDto.setName(groupsOfSchool.getSchoolName());
            viewSchoolDto.setAreaType(CONST.AREA_TYPE_SCHOOL);
            List<SimpleGroup> simpleGroups = groupsOfSchool.getGroups();
//            if (null == schoolId2TrgPromotionsMap || null == schoolId2TrgPromotionsMap.get(groupsOfSchool.getSchoolId())) {
//                viewSchoolDto.setActiveCount(0);
//            } else {
//                viewSchoolDto.setActiveCount(_getSchoolActiveCount(schoolId2TrgPromotionsMap.get(groupsOfSchool.getSchoolId())));
//            }
            if (null == simpleGroups || simpleGroups.isEmpty()) {
                viewSchoolDto.setActiveCount(0);
                viewSchoolDto.setStudentCount(0);
                viewSchoolDto.setTeacherCount(0);
                viewSchoolDto.setGrades(Collections.emptyList());
                continue;
            }
            //封装年级
            List<ViewGradeDto> grades = _convertSimpleGroupsToViewGreadeDtos(simpleGroups, teacherId2AccountMap, groupId2TrgPromotionsMap);
            int activeCount = 0, studentCount = 0, teacherCount = 0;
            for (ViewGradeDto viewGradeDto : grades) {
                activeCount += viewGradeDto.getQuantityOfPlanA() + viewGradeDto.getQuantityOfPlanB();
                studentCount += viewGradeDto.getStudentCount();
                teacherCount += viewGradeDto.getTeachers().size();
            }
            viewSchoolDto.setActiveCount(activeCount);
            viewSchoolDto.setStudentCount(studentCount);
            viewSchoolDto.setTeacherCount(teacherCount);
            viewSchoolDto.setGrades(grades);
            viewShoolDtos.add(viewSchoolDto);

        }
        return viewShoolDtos;
    }

    private static List<ViewGradeDto> _convertSimpleGroupsToViewGreadeDtos(List<SimpleGroup> simpleGroups, Map<String, Account> teacherId2AccountMap, Map<String, List<TrgPromotion>> groupId2TrgPromotionsMap) {
        List<ViewGradeDto> grades = new ArrayList<>();
        Map<Integer, List<SimpleGroup>> period2SimpleGroupsMap = StreamHelper.convertListToMapList(simpleGroups, SimpleGroup::getGradeNum);
        for (Map.Entry<Integer, List<SimpleGroup>> gradeGroupsMap : period2SimpleGroupsMap.entrySet()) {
            Integer gradeNum = gradeGroupsMap.getKey();
            if (gradeNum > 6) {
                continue;
            }
            ViewGradeDto viewGradeDto = new ViewGradeDto();
            viewGradeDto.setId(gradeNum + "");
            List<SimpleGroup> gradeGroups = gradeGroupsMap.getValue();
            if (null == gradeGroups || gradeGroups.isEmpty()) {
                viewGradeDto.setQuantityOfPlanA(0);
                viewGradeDto.setQuantityOfPlanB(0);
                viewGradeDto.setStudentCount(0);
                viewGradeDto.setTeachers(Collections.emptyList());
                continue;
            }
            SimpleGroup oneGroup = gradeGroups.get(0);
            viewGradeDto.setName(oneGroup.getPeriodName());
            //封装教师
            List<ViewTeacherDto> teachers = _convertSimpleGroupsToViewTeacherDtos(gradeGroups, teacherId2AccountMap, groupId2TrgPromotionsMap);
            viewGradeDto.setTeachers(teachers);
            int planACount = 0, planBCount = 0, studentCount = 0;
            for (ViewTeacherDto viewTeacherDto : teachers) {
                planACount += viewTeacherDto.getQuantityOfPlanA();
                planBCount += viewTeacherDto.getQuantityOfPlanB();
                studentCount += viewTeacherDto.getStudentCount();
            }
            viewGradeDto.setQuantityOfPlanA(planACount);
            viewGradeDto.setQuantityOfPlanB(planBCount);
            viewGradeDto.setStudentCount(studentCount);
            grades.add(viewGradeDto);
        }
        return grades;
    }

    private static List<ViewTeacherDto> _convertSimpleGroupsToViewTeacherDtos(List<SimpleGroup> gradeGroups, Map<String, Account> teacherId2AccountMap, Map<String, List<TrgPromotion>> groupId2TrgPromotionsMap) {
        List<ViewTeacherDto> teachers = new ArrayList<>();
        Map<String, List<SimpleGroup>> teacherId2SimpleGroupsMap = StreamHelper.convertListToMapList(gradeGroups, SimpleGroup::getTeacherId);
        for (Map.Entry<String, List<SimpleGroup>> teacherIdGroupsMap : teacherId2SimpleGroupsMap.entrySet()) {
            String teacherId = teacherIdGroupsMap.getKey();
            ViewTeacherDto viewTeacherDto = new ViewTeacherDto();
            viewTeacherDto.setId(teacherId);
            if (null == teacherId2AccountMap || null == teacherId2AccountMap.get(teacherId)) {
                viewTeacherDto.setName(CONST.SELF_STUDY_TEACHER_NAME);
            } else {
                viewTeacherDto.setName(teacherId2AccountMap.get(teacherId).getRealname());
            }
            List<SimpleGroup> teacherGroups = teacherIdGroupsMap.getValue();
            if (null == teacherGroups || teacherGroups.isEmpty()) {
                viewTeacherDto.setQuantityOfPlanA(0);
                viewTeacherDto.setQuantityOfPlanB(0);
                viewTeacherDto.setStudentCount(0);
                viewTeacherDto.setClasses(Collections.emptyList());
                continue;
            }
            //封装班级
            List<ViewClassDto> viewClassDtos = _convertSimpleGroupsToViewClassDtos(teacherGroups, groupId2TrgPromotionsMap);
            viewTeacherDto.setClasses(viewClassDtos);
            int planACount = 0, planBCount = 0, studentCount = 0;
            for (ViewClassDto viewClassDto : viewClassDtos) {
                planACount += viewClassDto.getQuantityOfPlanA();
                planBCount += viewClassDto.getQuantityOfPlanB();
                studentCount += viewClassDto.getStudentCount();
            }
            viewTeacherDto.setQuantityOfPlanA(planACount);
            viewTeacherDto.setQuantityOfPlanB(planBCount);
            viewTeacherDto.setStudentCount(studentCount);
            teachers.add(viewTeacherDto);
        }
        return teachers;
    }

    private static List<ViewClassDto> _convertSimpleGroupsToViewClassDtos(List<SimpleGroup> teacherGroups, Map<String, List<TrgPromotion>> groupId2TrgPromotionsMap) {
        List<ViewClassDto> viewClassDtos = new ArrayList<>();
        for (SimpleGroup simpleGroup : teacherGroups) {
            ViewClassDto viewClassDto = new ViewClassDto();
            viewClassDto.setId(simpleGroup.getId());
            viewClassDto.setName(simpleGroup.getName());
            viewClassDto.setStudentCount(simpleGroup.getQuantityOfStu());
            if (null == groupId2TrgPromotionsMap || null == groupId2TrgPromotionsMap.get(simpleGroup.getId())) {
                viewClassDto.setQuantityOfPlanA(0);
                viewClassDto.setQuantityOfPlanB(0);
            } else {
                int planACount = _getQuantityOfPlan(groupId2TrgPromotionsMap.get(simpleGroup.getId()), CONST.GOODS_CATEGORY_TRAINING_CAMP_A);
                int planADiscountCount = _getQuantityOfPlan(groupId2TrgPromotionsMap.get(simpleGroup.getId()), CONST.GOODS_CATEGORY_TRAINING_CAMP_A_DISCOUNT);
                viewClassDto.setQuantityOfPlanA(planACount + planADiscountCount);
                viewClassDto.setQuantityOfPlanB(0);//目前暂时没有b计划
            }
            viewClassDtos.add(viewClassDto);
        }
        return viewClassDtos;
    }

    public static LoginSuccessInfo genereateLoginSuccessInfo(String os, String userId, String username, List<GroupItemObj> classes) {

        LoginSuccessInfo loginSuccessInfo = new LoginSuccessInfo();
        loginSuccessInfo.setId(UUID.randomUUID().toString());
        loginSuccessInfo.setUserId(userId);
        loginSuccessInfo.setUsername(username);
        loginSuccessInfo.setDevice(os);
        if (null != classes && !classes.isEmpty()) {
            GroupItemObj findGroup = null;
            for (GroupItemObj groupItemObj : classes) {
                if (groupItemObj.getType() != GmServantsConst.GROUP_TYPE_SELF_LEARN) {
                    findGroup = groupItemObj;
                    break;
                }
            }
            if (null == findGroup) {
                for (GroupItemObj groupItemObj : classes) {
                    if (groupItemObj.getType() == GmServantsConst.GROUP_TYPE_SELF_LEARN) {
                        findGroup = groupItemObj;
                        break;
                    }
                }
            }
            if (null != findGroup) {
                BusinessUtils.updateLoginSuccessInfoOfGroupItemObj(findGroup, loginSuccessInfo);
            }


        }
        return loginSuccessInfo;
    }

    private static void updateLoginSuccessInfoOfGroupItemObj(GroupItemObj groupItemObj, LoginSuccessInfo loginSuccessInfo) {
        loginSuccessInfo.setProvinceId(groupItemObj.getProvinceId());
        loginSuccessInfo.setProvinceName(groupItemObj.getProvinceName());
        loginSuccessInfo.setCityId(groupItemObj.getCityId());
        loginSuccessInfo.setDistrictId(groupItemObj.getDistrictId());
        loginSuccessInfo.setSchoolId(groupItemObj.getSchoolId());
        loginSuccessInfo.setClassId(groupItemObj.getId());
    }


    public static AreaViewDto generateAreaViewDto(String provinceId, String cityId, List<ViewDistrictDto> viewDistrictDtos) {
        AreaViewDto areaViewDto = new AreaViewDto();
        areaViewDto.setProvinceId(provinceId);
        areaViewDto.setCityId(cityId);
        areaViewDto.setChildViewDistricts(viewDistrictDtos);
        return areaViewDto;
    }

    public static TeacherDto convertAccountToTeacherDto(Account account) {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(account.getId());
        teacherDto.setLoginName(account.getUsername());
        teacherDto.setName(account.getRealname());
        teacherDto.setTelephone(account.getPhone());
        teacherDto.setGender(account.getGender() == 1);
        teacherDto.setEmail(account.getEmail());
        return teacherDto;
    }

    public static List<TeacherMessageDTO> convertTeacherMessageDTOsFromTeacherIdsAndGroupsOfTeachersAndStudents(List<Account> accounts, List<GroupsOfTeacher> groupsOfTeachers, List<Account> students) {
        List<TeacherMessageDTO> teacherMessageDTOs = new ArrayList<>();
        Map<String, Account> id2CountMap = new HashMap<>();
        if (null != students && !students.isEmpty()) {
            id2CountMap = StreamHelper.convertListToMap(students, Account::getId);
        }
        Map<String, GroupsOfTeacher> teacherId2GroupsOfTeacherMap = StreamHelper.convertListToMap(groupsOfTeachers, GroupsOfTeacher::getTeacherId);
        for (Account account : accounts) {
            TeacherMessageDTO dto = new TeacherMessageDTO();
            dto.setTeacherName(account.getRealname());
            dto.setTeacherLoginName(account.getUsername());
            GroupsOfTeacher groupsOfTeacher = teacherId2GroupsOfTeacherMap.get(account.getId());
            if (null != groupsOfTeacher && null != groupsOfTeacher.getGroupOfStudents()) {
                List<GroupOfStudent> groupOfStudents = groupsOfTeacher.getGroupOfStudents();
                List<TeacherMessageDTO.ClazzDTO> clazzDTOList = new ArrayList<>();
                for (GroupOfStudent groupOfStudent : groupOfStudents) {
                    TeacherMessageDTO.ClazzDTO clazzDto = new TeacherMessageDTO.ClazzDTO();
                    clazzDto.setSchoolName(groupOfStudent.getSchoolName());
                    clazzDto.setCityName(groupOfStudent.getCityName());
                    clazzDto.setClassId(groupOfStudent.getClassId());
                    clazzDto.setClassName(groupOfStudent.getClassName());
                    clazzDto.setDistrictName(groupOfStudent.getDistrictName());
                    clazzDto.setGrade(groupOfStudent.getGrade() + "");
                    clazzDto.setProvinceName(groupOfStudent.getProvinceName());
                    if (null != groupOfStudent.getStudentIds()) {
                        List<TeacherMessageDTO.StudentDTO> studentDtos = new ArrayList<>();
                        List<String> studentIds = groupOfStudent.getStudentIds();
                        for (String studentId : studentIds) {
                            Account student = id2CountMap.get(studentId);
                            if (null != student) {
                                TeacherMessageDTO.StudentDTO studentDTO = new TeacherMessageDTO.StudentDTO();
                                studentDTO.setStudentLoginName(student.getUsername());
                                studentDTO.setStudentName(student.getRealname());
                                studentDtos.add(studentDTO);
                            }
                        }
                        clazzDto.setStudentDTOList(studentDtos);
                    }

                    clazzDTOList.add(clazzDto);
                }
                dto.setClazzDTOList(clazzDTOList);
            }

            teacherMessageDTOs.add(dto);
        }
        return teacherMessageDTOs;
    }

    public static List<ParentMessageDTO> convertParentMessageDTOsFromStudentExtras(List<Account> accounts, List<StudentExtra> studentExtras) {
        List<ParentMessageDTO> parentMessageDTOs = new ArrayList<>();
        Map<String, Account> stuId2AccountMap = StreamHelper.convertListToMap(accounts, Account::getId);
        Map<String, List<StudentExtra>> parentId2StudentExtrasMap = StreamHelper.convertListToMapList(studentExtras, StudentExtra::getGuardianId);
        for (Map.Entry<String, List<StudentExtra>> map : parentId2StudentExtrasMap.entrySet()) {
            String parentId = map.getKey();
            List<StudentExtra> students = map.getValue();
            Account parent = stuId2AccountMap.get(parentId);
            if (null == parent) {
                continue;
            }
            ParentMessageDTO parentDto = new ParentMessageDTO();
            parentDto.setParentName(parent.getRealname());
            parentDto.setParentLoginName(parent.getUsername());
            List<ParentMessageDTO.StudentDTO> studentDtos = new ArrayList<>();
            for (StudentExtra studentExtra : students) {
                ParentMessageDTO.StudentDTO studentDto = new ParentMessageDTO.StudentDTO();
                studentDto.setStudentName(studentExtra.getRealname());
                studentDto.setStudentLoginName(studentExtra.getUsername());
                studentDtos.add(studentDto);
            }
            parentDto.setStudentDTOList(studentDtos);
            parentMessageDTOs.add(parentDto);
        }
        return parentMessageDTOs;
    }

    public static List<StudentMessageDTO> convertStudentMessageDTOs(List<StudentGuardianInfo> studentGuardianInfos, Map<String, List<Group>> stuId2GroupsMap, List<Account> teachers) {
        if (null == studentGuardianInfos || studentGuardianInfos.isEmpty()) {
            return null;
        }
        if (null == stuId2GroupsMap) {
            stuId2GroupsMap = new HashMap<>();
        }
        Map<String, Account> teacherId2AccountMap = StreamHelper.convertListToMap(teachers, Account::getId);
        if (null == teacherId2AccountMap) {
            teacherId2AccountMap = new HashMap<>();
        }
        List<StudentMessageDTO> studentMessageDTOs = new ArrayList<>();
        for (StudentGuardianInfo studentGuardianInfo : studentGuardianInfos) {
            StudentMessageDTO studentMessageDto = new StudentMessageDTO();
            studentMessageDto.setName(studentGuardianInfo.getRealname());
            studentMessageDto.setLoginName(studentGuardianInfo.getUsername());
            List<GuardianInfo> guardianInfos = studentGuardianInfo.getGuardianInfos();
            if (null == guardianInfos || guardianInfos.isEmpty()) {
                continue;
            }
            List<StudentMessageDTO.ParentDTO> parentDTOs = new ArrayList<>();
            for (GuardianInfo guardianInfo : guardianInfos) {
                StudentMessageDTO.ParentDTO parentDTO = new StudentMessageDTO.ParentDTO();
                parentDTO.setParentName(guardianInfo.getRealName());
                parentDTO.setParentLoginName(guardianInfo.getLoginName());
                parentDTOs.add(parentDTO);
            }
            studentMessageDto.setParentDTOList(parentDTOs);
            List<Group> groups = stuId2GroupsMap.get(studentGuardianInfo.getId());
            if (null == groups || groups.isEmpty()) {
                continue;
            }
            List<StudentMessageDTO.SchoolDTO> schoolDTOs = new ArrayList<>();
            for (Group group : groups) {
                StudentMessageDTO.SchoolDTO schoolDTO = new StudentMessageDTO.SchoolDTO();
                schoolDTO.setClazz(group.getCls() + "");
                schoolDTO.setClazzName(group.getName());
                schoolDTO.setGrade(group.getGrade());
                schoolDTO.setSchoolName(group.getSchool().getName());
                if (null != group.getCreatorId() && null != teacherId2AccountMap.get(group.getCreatorId())) {
                    StudentMessageDTO.TeacherDTO teacherDTO = new StudentMessageDTO.TeacherDTO();
                    Account teacher = teacherId2AccountMap.get(group.getCreatorId());
                    teacherDTO.setTeacherName(teacher.getRealname());
                    teacherDTO.setTeacherLoginName(teacher.getUsername());
                    schoolDTO.setTeacherDTO(teacherDTO);
                }
                schoolDTOs.add(schoolDTO);
            }
            studentMessageDto.setSchoolDTOList(schoolDTOs);
            studentMessageDTOs.add(studentMessageDto);
        }
        return studentMessageDTOs;
    }

    public static StatisticsRegistOrLoginDto getOnlineWholeCountryStatDto(List<AreaItem> areaItems, List<GroupsOfSchool> groupsOfSchools,
                                                                          List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        if (null == areaItems || areaItems.isEmpty() || null == groupsOfSchools || groupsOfSchools.isEmpty()) {
            return null;
        }
        StatisticsRegistOrLoginDto statisticsRegistOrLoginDto = new StatisticsRegistOrLoginDto();
        statisticsRegistOrLoginDto.setId("0");  //  最外层为全国id设置未0
        statisticsRegistOrLoginDto.setName("全国");   //最外层设置为全国
        List<StatisticsRegistOrLoginDto.Province> provinces = getAllProvinceInCountryStatDtos(areaItems, groupsOfSchools, groupStuCntList, teacherId2AccountMap);
        if (null == provinces || provinces.isEmpty()) {
            statisticsRegistOrLoginDto.setStudentNumber(0);
            statisticsRegistOrLoginDto.setProvinceList(Collections.emptyList());
            return statisticsRegistOrLoginDto;
        }
        int number = 0;
        for (StatisticsRegistOrLoginDto.Province province : provinces) {
            number += province.getStudentNumber();
        }
        statisticsRegistOrLoginDto.setStudentNumber(number);
        statisticsRegistOrLoginDto.setProvinceList(provinces);
        return statisticsRegistOrLoginDto;
    }

    public static List<StatisticsRegistOrLoginDto.Province> getAllProvinceInCountryStatDtos(List<AreaItem> areaItems, List<GroupsOfSchool> groupsOfSchools, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.Province> provinces = new ArrayList<>();
        for (AreaItem areaItem : areaItems) {
            StatisticsRegistOrLoginDto.Province province = new StatisticsRegistOrLoginDto.Province();
            province.setProvinceId(areaItem.getId());
            province.setProvinceName(areaItem.getName());
            List<AreaItem> childAreaItems = areaItem.getChildAreaItems();
            if (null == childAreaItems || childAreaItems.isEmpty()) {
                province.setStudentNumber(0);
                province.setCityList(Collections.emptyList());
                provinces.add(province);
                continue;
            }
            List<StatisticsRegistOrLoginDto.City> cities = getAllCitiesInProvinceStatDtos(childAreaItems, groupsOfSchools, groupStuCntList, teacherId2AccountMap);
            int number = 0;
            for (StatisticsRegistOrLoginDto.City city : cities) {
                number += city.getStudentNumber();
            }
            province.setStudentNumber(number);
            province.setCityList(cities);
            provinces.add(province);
        }
        return provinces;
    }

    private static List<StatisticsRegistOrLoginDto.City> getAllCitiesInProvinceStatDtos(List<AreaItem> areaItems, List<GroupsOfSchool> groupsOfSchools, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.City> cities = new ArrayList<>();
        for (AreaItem areaItem : areaItems) {
            StatisticsRegistOrLoginDto.City city = new StatisticsRegistOrLoginDto.City();
            city.setCityId(areaItem.getId());
            city.setCityName(areaItem.getName());
            List<AreaItem> childAreaItems = areaItem.getChildAreaItems();
            if (null == childAreaItems || childAreaItems.isEmpty()) {
                city.setStudentNumber(0);
                city.setDistrictList(Collections.emptyList());
                cities.add(city);
                continue;
            }
            List<StatisticsRegistOrLoginDto.District> dists = getAllDistsInCityStatDtos(childAreaItems, groupsOfSchools, groupStuCntList, teacherId2AccountMap);
            int number = 0;
            for (StatisticsRegistOrLoginDto.District dist : dists) {
                number += dist.getStudentNumber();
            }
            city.setStudentNumber(number);
            city.setDistrictList(dists);
            cities.add(city);
        }
        return cities;
    }

    private static List<StatisticsRegistOrLoginDto.District> getAllDistsInCityStatDtos(List<AreaItem> areaItems, List<GroupsOfSchool> groupsOfSchools, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.District> districts = new ArrayList<>();
        for (AreaItem areaItem : areaItems) {
            StatisticsRegistOrLoginDto.District district = new StatisticsRegistOrLoginDto.District();
            district.setDistrictId(areaItem.getId());
            district.setDistrictName(areaItem.getName());
            List<AreaItem> childAreaItems = areaItem.getChildAreaItems();
            if (null == childAreaItems || childAreaItems.isEmpty()) {
                district.setStudentNumber(0);
                district.setSchoolList(Collections.emptyList());
                districts.add(district);
                continue;
            }
            List<StatisticsRegistOrLoginDto.School> schools = getAllSchoolsInDistStatDtos(childAreaItems, groupsOfSchools, groupStuCntList, teacherId2AccountMap);
            int number = 0;
            for (StatisticsRegistOrLoginDto.School school : schools) {
                number += school.getStudentNumber();
            }
            district.setStudentNumber(number);
            district.setSchoolList(schools);
            districts.add(district);
        }
        return districts;
    }

    private static List<StatisticsRegistOrLoginDto.School> getAllSchoolsInDistStatDtos(List<AreaItem> areaItems, List<GroupsOfSchool> groupsOfSchools, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.School> schools = new ArrayList<>();
        for (AreaItem areaItem : areaItems) {
            StatisticsRegistOrLoginDto.School school = new StatisticsRegistOrLoginDto.School();
            String schoolId = areaItem.getId();
            school.setSchoolId(schoolId);
            school.setSchoolName(areaItem.getName());
            /*List<AreaItem> childAreaItems = areaItem.getChildAreaItems();
            if (null == childAreaItems || childAreaItems.isEmpty() || null == groupsOfSchools || groupsOfSchools.isEmpty()) {
                school.setStudentNumber(0);
                school.setGradeList(Collections.emptyList());
                schools.add(school);
                continue;
            }*/
            Map<String, GroupsOfSchool> school2GroupsOfSchoolMap = StreamHelper.convertListToMap(groupsOfSchools, GroupsOfSchool::getSchoolId);
            GroupsOfSchool groupsOfSchool = school2GroupsOfSchoolMap.get(schoolId);
            if (null == groupsOfSchool || null == groupsOfSchool.getGroups() || groupsOfSchool.getGroups().isEmpty()) {
                school.setStudentNumber(0);
                school.setGradeList(Collections.emptyList());
                schools.add(school);
                continue;
            }
            List<StatisticsRegistOrLoginDto.Grade> grades = getAllGradesInSchoolStatDtos(groupsOfSchool.getGroups(), groupStuCntList, teacherId2AccountMap);
            int number = 0;
            for (StatisticsRegistOrLoginDto.Grade grade : grades) {
                number += grade.getStudentNumber();
            }
            school.setStudentNumber(number);
            school.setGradeList(grades);
            schools.add(school);
        }
        return schools;
    }

    private static List<StatisticsRegistOrLoginDto.Grade> getAllGradesInSchoolStatDtos(List<SimpleGroup> groups, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.Grade> grades = new ArrayList<>();
        Map<Integer, List<SimpleGroup>> grade2TeachersMap = StreamHelper.convertListToMapList(groups, SimpleGroup::getGradeNum);
        for (Map.Entry<Integer, List<SimpleGroup>> map : grade2TeachersMap.entrySet()) {
            Integer gradeNum = map.getKey();
//            if (gradeNum > 6) {
//                continue;
//            }
            StatisticsRegistOrLoginDto.Grade grade = new StatisticsRegistOrLoginDto.Grade();
            grade.setGradeId(gradeNum);
            if (null == map.getValue()) {
                grade.setStudentNumber(0);
                grade.setTeacherList(Collections.emptyList());
                grades.add(grade);
                continue;
            }
            grade.setGradeName(map.getValue().get(0).getPeriodName());
            List<StatisticsRegistOrLoginDto.Teacher> teachers = getAllTeachersInGradeDtos(map.getValue(), groupStuCntList, teacherId2AccountMap);
            int number = 0;
            for (StatisticsRegistOrLoginDto.Teacher teacher : teachers) {
                number += teacher.getStudentNumber();
            }
            grade.setStudentNumber(number);
            grade.setTeacherList(teachers);
            grades.add(grade);
        }
        return grades;
    }

    private static List<StatisticsRegistOrLoginDto.Teacher> getAllTeachersInGradeDtos(List<SimpleGroup> simpleGroups, List<SimpleGroup> groupStuCntList, Map<String, Account> teacherId2AccountMap) {
        List<StatisticsRegistOrLoginDto.Teacher> teachers = new ArrayList<>();
        if (null == simpleGroups || simpleGroups.isEmpty()) {
            return teachers;
        }


        Map<String, List<SimpleGroup>> teacher2GroupsMap = new HashMap<>();
        for (SimpleGroup simpleGroup : simpleGroups) {
            String teacherId = simpleGroup.getTeacherId();
            if (null == teacherId || teacherId.isEmpty()) {
                teacherId = CONST.SELF_STUDY_TEACHER_ID;
            }
            List<SimpleGroup> simpleGroupsForTeacher = teacher2GroupsMap.get(teacherId);
            if (null == simpleGroupsForTeacher) {
                simpleGroupsForTeacher = new ArrayList<>();
                simpleGroupsForTeacher.add(simpleGroup);
            } else {
                simpleGroupsForTeacher.add(simpleGroup);
            }
            teacher2GroupsMap.put(teacherId, simpleGroupsForTeacher);
        }
        for (Map.Entry<String, List<SimpleGroup>> map : teacher2GroupsMap.entrySet()) {
            StatisticsRegistOrLoginDto.Teacher teacher = new StatisticsRegistOrLoginDto.Teacher();
            String teacherId = map.getKey();
            teacher.setTeacherId(teacherId);
            if (null != teacherId2AccountMap.get(teacherId) && !CONST.SELF_STUDY_TEACHER_ID.equals(teacherId)) {
                teacher.setTeacherName(teacherId2AccountMap.get(teacherId).getRealname());
                teacher.setTeacherLoginName(teacherId2AccountMap.get(teacherId).getUsername());
            }
            if (CONST.SELF_STUDY_TEACHER_ID.equals(teacherId)) {
                teacher.setTeacherName(CONST.SELF_STUDY_TEACHER_NAME);
                teacher.setTeacherLoginName(CONST.SELF_STUDY_TEACHER_LOGIN_NAME);
            }
            if (null == map.getValue()) {
                teacher.setStudentNumber(0);
                teacher.setClazzList(Collections.emptyList());
                teachers.add(teacher);
                continue;
            }
            List<StatisticsRegistOrLoginDto.Clazz> groups = getAllGroupInTeacherDtos(map.getValue(), groupStuCntList);
            int number = 0;
            for (StatisticsRegistOrLoginDto.Clazz clazz : groups) {
                number += clazz.getStudentNumber();
            }
            teacher.setStudentNumber(number);
            teacher.setClazzList(groups);
            teachers.add(teacher);
        }
        return teachers;
    }

    private static List<StatisticsRegistOrLoginDto.Clazz> getAllGroupInTeacherDtos(List<SimpleGroup> simpleGroups, List<SimpleGroup> groupStuCntList) {
        List<StatisticsRegistOrLoginDto.Clazz> groups = new ArrayList<>();
        Map<String, SimpleGroup> groupId2SimpleGroupMap = new HashMap<>();
        if (null != groupStuCntList && !groupStuCntList.isEmpty()) {
            groupId2SimpleGroupMap = StreamHelper.convertListToMap(groupStuCntList, SimpleGroup::getId);
        }
        for (SimpleGroup simpleGroup : simpleGroups) {
            StatisticsRegistOrLoginDto.Clazz group = new StatisticsRegistOrLoginDto.Clazz();
            String groupId = simpleGroup.getId();
            group.setClazzId(groupId);
            group.setClazzName(simpleGroup.getName());
            SimpleGroup simpleGroup1 = groupId2SimpleGroupMap.get(groupId);

            if (null == simpleGroup1) {
                group.setStudentNumber(0);
            } else {
                group.setStudentNumber(simpleGroup1.getQuantityOfStu());
            }
            groups.add(group);

        }
        return groups;
    }


    public static List<RefundDesc> convertRefundItemsAndAccountsToRefundDescs(List<RefundItem> refundItems, List<Account> accounts) {
        if (null == refundItems || refundItems.isEmpty()) {
            return null;
        }
        Map<String, Account> userId2AccountMap = new HashMap<>();
        if (null != accounts && !accounts.isEmpty()) {
            userId2AccountMap = StreamHelper.convertListToMap(accounts, Account::getId);
        }
        List<RefundDesc> refundDescs = new ArrayList<>();
        for (RefundItem refundItem : refundItems) {
            RefundDesc refundDesc = new RefundDesc();
            String userId = refundItem.getUserId();
            refundDesc.setUserId(userId);
            Account account = userId2AccountMap.get(userId);
            if (null != account) {
                refundDesc.setUserAccount(account.getUsername());
                refundDesc.setUserName(account.getRealname());
            }
            refundDesc.setOrderId(refundItem.getOrderId());
            refundDesc.setRefund(refundItem.getRefund());
            refundDesc.setCreateTime(refundItem.getCreateTime());
            refundDescs.add(refundDesc);
        }
        return refundDescs;
    }

    public static List<TeacherCreditsRecord> convertCreditsHistoryList2TeacherCreditsRecordList(List<CreditsHistory> list) {
        List<TeacherCreditsRecord> result = new ArrayList<>();
        if (list != null) {
            for (CreditsHistory creditsHistory : list) {
                TeacherCreditsRecord teacherCreditsRecord = new TeacherCreditsRecord();
                teacherCreditsRecord.setType(creditsHistory.getType());
                teacherCreditsRecord.setSubType(creditsHistory.getSubType());
                teacherCreditsRecord.setCredits(creditsHistory.getCredits());
                teacherCreditsRecord.setDetail(creditsHistory.getDetail());
                teacherCreditsRecord.setDateTime(new Date(creditsHistory.getCreateTime()));
                result.add(teacherCreditsRecord);
            }
        }
        return result;
    }

    public static List<TeacherTask> convertTeacherTaskStateList2TeacherTaskList(List<TeacherTaskState> list) {
        List<TeacherTask> result = new ArrayList<>();
        if (list != null) {
            for (TeacherTaskState teacherTaskState : list) {
                TeacherTask teacherTask = new TeacherTask();
                teacherTask.setTaskType(teacherTaskState.getTaskType());
                teacherTask.setDeadline(new Date(teacherTaskState.getDeadline()));
                teacherTask.setTaskName(teacherTaskState.getTaskName());
                teacherTask.setTaskDetail(teacherTaskState.getTaskDetail());
                teacherTask.setCanDo(teacherTaskState.getCanDo());
                result.add(teacherTask);
            }
        }
        return result;
    }

    public static TeacherInviteDetails convertTeacherInviteStudentTaskDetail2TeacherInviteDetails(TeacherInviteStudentTaskDetail detail, Map<String, String> groupId2Name) {
        TeacherInviteDetails teacherInviteDetails = new TeacherInviteDetails();
        teacherInviteDetails.setAwardCredits(detail.getCredits());
        teacherInviteDetails.setStudentCount(detail.getStudentCount());
        teacherInviteDetails.setAwardId(detail.getAwardId());
        if (detail.getAwardId() == null || detail.getAwardId().isEmpty()) {
            teacherInviteDetails.setCanAward(false);
        } else {
            teacherInviteDetails.setCanAward(!detail.getAward());
        }
        teacherInviteDetails.setEnableTime(new Date(detail.getEnableTime()));
        if (detail.getGroup2StudentCount() != null) {
            List<TeacherInviteDetailsItem> list = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : detail.getGroup2StudentCount().entrySet()) {
                TeacherInviteDetailsItem teacherInviteDetailsItem = new TeacherInviteDetailsItem();
                teacherInviteDetailsItem.setClassName(groupId2Name.get(entry.getKey()));
                teacherInviteDetailsItem.setClassId(entry.getKey());
                teacherInviteDetailsItem.setCount(entry.getValue());
                list.add(teacherInviteDetailsItem);
            }
            teacherInviteDetails.setClassInfo(list);
        }
        return teacherInviteDetails;
    }

    public static TeacherRecommendDetails convertTeacherRecommendTaskDetail2TeacherRecommendDetails(TeacherRecommendTaskDetail detail, Map<String, Account> id2Account) {
        List<TeacherRecommendDetailsItem> list = new ArrayList<>();
        if (detail.getRecommendedItem() != null) {
            for (TeacherRecommendTaskDetailItem teacherRecommendTaskDetailItem : detail.getRecommendedItem()) {
                TeacherRecommendDetailsItem teacherTask = new TeacherRecommendDetailsItem();
                Account account = id2Account.get(teacherRecommendTaskDetailItem.getRecommendedId());
                if (account != null) {
                    teacherTask.setName(account.getRealname());
                    teacherTask.setPhone(account.getPhone());
                    teacherTask.setJoinStudentCount(teacherRecommendTaskDetailItem.getMaxGroupStudentCount());
                    teacherTask.setTargetStudentCount(teacherRecommendTaskDetailItem.getTargetStudentCount());
                    teacherTask.setAwardId(teacherRecommendTaskDetailItem.getAwardId());
                    if (teacherRecommendTaskDetailItem.getAwardId() == null || teacherRecommendTaskDetailItem.getAwardId().isEmpty()) {
                        teacherTask.setCanAward(false);
                    } else {
                        teacherTask.setCanAward(!teacherRecommendTaskDetailItem.getAward());
                    }
                    list.add(teacherTask);
                }
            }
        }

        TeacherRecommendDetails result = new TeacherRecommendDetails();
        result.setStudentCount(detail.getStudentCount());
        result.setMyCredits(detail.getCredits());
        result.setRecommendedCredits(detail.getRecommendedCredits());
        result.setDetails(list);
        return result;
    }

    public static List<TeacherTaskAward> convertTeacherTaskAwardList2TeacherTaskAwardList(List<TaskAward> list) {
        List<TeacherTaskAward> result = new ArrayList<>();
        if (list != null) {
            for (TaskAward taskAward : list) {
                TeacherTaskAward teacherTaskAward = new TeacherTaskAward();
                teacherTaskAward.setId(taskAward.getId());
                teacherTaskAward.setDetail(taskAward.getDetail());
                teacherTaskAward.setCredits(taskAward.getCredits());
                teacherTaskAward.setCreateTime(new Date(taskAward.getCreateTime()));
                result.add(teacherTaskAward);
            }
        }
        return result;
    }


    public static TeacherTeachDetail convertTeacherTeachTaskDetail2TeacherTeachDetail(TeacherTeachTaskDetail detail, Map<String, String> studentId2Name) {
        TeacherTeachDetail result = new TeacherTeachDetail();
        result.setCredits(detail.getCredits());
        result.setEnableTime(new Date(detail.getTermStartTime()));
        result.setAward(convertFinishGroup2TeacherTeachDetailGroup(detail.getStudentAwardGroup(), studentId2Name));
        result.setUnAward(convertFinishGroup2TeacherTeachDetailGroup(detail.getStudentUnAwardGroup(), studentId2Name));
        result.setUnFinish(convertFinishGroup2TeacherTeachDetailGroup(detail.getStudentUnFinishGroup(), studentId2Name));
        return result;
    }

    private static List<TeacherTeachDetailGroup> convertFinishGroup2TeacherTeachDetailGroup(List<TeacherTeachStudentFinishGroup> groupList, Map<String, String> studentId2Name) {
        List<TeacherTeachDetailGroup> result = new ArrayList<>();
        for (TeacherTeachStudentFinishGroup teacherTeachStudentFinishGroup : groupList) {
            TeacherTeachDetailGroup item = new TeacherTeachDetailGroup();
            item.setAward(teacherTeachStudentFinishGroup.getHasAward());
            item.setAwardId(teacherTeachStudentFinishGroup.getAwardId());

            List<TeacherTeacherDetailGroupItem> list = new ArrayList<>();
            for (TeacherTeachStudentFinishItem finishItem : teacherTeachStudentFinishGroup.getStudentFinishList()) {
                TeacherTeacherDetailGroupItem groupItem = new TeacherTeacherDetailGroupItem();
                groupItem.setStudentName(studentId2Name.get(finishItem.getStudentId()));
                groupItem.setFinishCount(finishItem.getFinishCount());
                list.add(groupItem);
            }
            item.setStudentList(list);
            result.add(item);
        }
        return result;
    }

    public static TeacherPaperDetails convertTeacherPaperTaskDetail2TeacherPaperDetails(TeacherPaperTaskDetail detail) {
        TeacherPaperDetails teacherPaperDetails = new TeacherPaperDetails();
        teacherPaperDetails.setAwardId(detail.getAwardId());
        teacherPaperDetails.setCredits(detail.getCredits());
        if (detail.getAwardId() == null || detail.getAwardId().isEmpty()) {
            teacherPaperDetails.setCanAward(false);
        } else {
            teacherPaperDetails.setCanAward(!detail.getAward());
        }
        teacherPaperDetails.setAssignCount(detail.getAssignCount());
        teacherPaperDetails.setFinishCount(detail.getFinishCount());
        teacherPaperDetails.setTargetCount(detail.getTargetCount());
        return teacherPaperDetails;
    }

    public static TeacherGameDetails convertTeacherGameTaskDetail2TeacherGameDetails(TeacherGameTaskDetail detail) {
        TeacherGameDetails teacherGameDetails = new TeacherGameDetails();
        teacherGameDetails.setAwardId(detail.getAwardId());
        teacherGameDetails.setCredits(detail.getCredits());
        if (detail.getAwardId() == null || detail.getAwardId().isEmpty()) {
            teacherGameDetails.setCanAward(false);
        } else {
            teacherGameDetails.setCanAward(!detail.getAward());
        }
        teacherGameDetails.setAssignCount(detail.getAssignCount());
        teacherGameDetails.setFinishCount(detail.getFinishCount());
        teacherGameDetails.setTargetCount(detail.getTargetCount());
        return teacherGameDetails;
    }

    public static int getTermByGrade(int grade) {
        int term = grade * 2;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int month = calendar.get(Calendar.MONTH);

        return term;
    }
}
