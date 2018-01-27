package com.alr.gateway.constant;

/**
 * Created by Dengxl on 2017/7/14.
 */
public class CONST {

    public static final int STUDY_PERIOD_FOR_FIRST_TERM      = 1;  //上学期
    public static final int STUDY_PERIOD_FOR_LATTER_TERM     = 3;  //下半期
    public static final int STUDY_PERIOD_FOR_SUMMER_VACATION = 4;  //暑假
    public static final int STUDY_PERIOD_FOR_WINTER_VACATION = 2;  //寒假

    /**获取课时知识点掌握明细时需要*/
    public static final String DIAGNOSE_DISPLAY_IMG_URI_PREFIX_MASTERFUL = "diagnose/signal_three.png";
    public static final String DIAGNOSE_DISPLAY_IMG_URI_PREFIX_UNFIRM = "diagnose/signal_two.png";
    public static final String DIAGNOSE_DISPLAY_IMG_URI_PREFIX_UNVERSED = "diagnose/signal_one.png";
    public static final String DIAGNOSE_DISPLAY_IMG_URI_PREFIX_NONE_SAMPLE = "diagnose/signal_none.png";
    public static final String DIAGNOSE_DISPLAY_IMG_URI_PREFIX_DEFAULT = "diagnose/signal_default.png";


    //权限用到的常量
    public static final String MULTI_PERMISSION_PEPARATOR_ADD  = "&";//多个权限共同作用时，且
    public static final String MULTI_PERMISSION_PEPARATOR_OR   = "|";//多个权限共同作用时，或
    public static final String PERMISSION_VALIDATE_METHOD_NAME = "validatePermission"; //所有权限类都实现了这个方法

    public static final String COMMON_SEPERATOR_COMMA = ",";//常用分隔符逗号
    public static final String COMMON_SEPERATOR_SEMICOLON = ";";//常用分隔符分号
    public static final String COMMON_SEPERATOR_SHORTBAR = "-";//常用分隔符短横线

    public static final String PATTERN_BRACES_INNER_WITHOUT_BRACES  ="(?<=\\{)(.+?)(?=\\})";//匹配大括号里面的内容（不包含大括号）

    public static final String PATTERN_BRACES_INNER_WITH_BRACES  ="\\{.+?\\}";//匹配大括号里面的内容（包含大括号）

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";//日期格式化字符串
    public static final String DATE_TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";//时间格式化字符串

    public static final String PAPER_LIST_ASSIGNEE_DISPLAY_ALL  = "all";
    public static final String PAPER_LIST_ASSIGNEE_DISPLAY_PART = "part";

    public static final String IMG_FEED_BACK_PATH = "/software/apache/img";
    public static final String IMG_URL = "http://{ip}:90/img";

    //KEY
    public static final String KEY_CODE    = "code";
    public static final String KEY_MESSAGE = "msg";
    public static final String KEY_RESULT  = "result";
    public static final String KEY_SESSION = "session";

    /**家长与子女关系*/
    public static final String KEY_USER_ID  = "userId";
    public static final String KEY_PATH_URI = "uri";


    //短信验证
    public static final String KEY_SMS_VALIDATE_CODE = "telVC";
    public static final String KEY_IMG_VALIDATE_CODE = "imgVC";

    public static final Integer PROXY_QBU_FOR_BIG_DATA = 0x1001;
    public static final Integer PROXY_QBU_FOR_MYSQL    = 0x1002;


    public static final int XINGE_ENV_IOS_DEV      = 1;
    public static final int XINGE_ENV_IOS_PROD     = 2;
    public static final int XINGE_ENV_ANDROID_DEV  = 3;
    public static final int XINGE_ENV_ANDROID_PROD = 4;


    //年级数
    public static int  MAX_GRADE_NUM = 6;
    //班级数
    public static int  MAX_CLASS_NUM = 15;
    //当前时间超过指定日期时进入高年级
    public static int  MONTH_HIGHER_GRADE = 8;

    public static final String PERMISSION_NONE      = "alr:none";
    public static final String PERMISSION_LOGOUT    = "alr::logout";
    public static final String PERMISSION_AUTHZ     = "alr::authz";



    //出题的数目
    public static final int GENERATE_QUESTION_AMOUNT = 30;

    //班级人数是否超过10个
    public static final int GROUP_STUDENT_NUM = 10;

    public static final int LOGIN_ROLE_NORMAL = 1;//用户登陆身份=普通用户
    public static final int LOGIN_ROLE_SUPER  = 2;//用户登陆身份=超级管理员

    public static final String GOODS_CATEGORY_TRAINING_CAMP_A  = "XN-XLY-001";//A计划
    public static final String GOODS_CATEGORY_TRAINING_CAMP_A_DISCOUNT  = "XN-XLY-002";//A计划减50

    public static final String CONFIG_STATIC_TR_SUPER="teachResearch.default.super";//静态配置里面的教研圈模块超级权限

    //地区类型
    public static final int AREA_TYPE_PROVINCE = 1;
    public static final int AREA_TYPE_CITY = 2;
    public static final int AREA_TYPE_DISTRICT = 3;
    public static final int AREA_TYPE_SCHOOL = 4;

    public static final Integer TRAIN_PET_DEFAULT_TOP = 5;  //霸主排行默认取前5名

    public static final Integer TRAIN_PET_TOP_TYPE_YESTERDAY = 1;  //昨天霸主排行
    public static final Integer TRAIN_PET_TOP_TYPE_BEFORE_YESTERDAY = 2;  //前天霸主排行


    public static final String MQ_TOPIC_GATE_WAY = "GATEWAY";
    public static final String MQ_TAG_GATE_WAY_LOGIN_SUCCESS = "GateWayLoginSuccess";

    public static final String URL_ROLE_TYPE_TEACHER = "teacher";
    public static final String URL_ROLE_TYPE_PARENT = "parent";
    public static final String URL_ROLE_TYPE_STUDENT = "student";

    //统计时候，自学班没有教师就默认一下
    public static final String SELF_STUDY_TEACHER_ID = "90000000";
    public static final String SELF_STUDY_TEACHER_NAME = "自学";
    public static final String SELF_STUDY_TEACHER_LOGIN_NAME = "无";

    //跨单元组卷时，获取零时组卷方式
    public static final int TEMP_PAPER_GET_Q_DEFUALT = 1;  //默认获取试题方式
    public static final int TEMP_PAPER_GET_Q_DIFF_UP = 2;  //获取试题方式（单元下根据难度高一级替换试题）
    public static final int TEMP_PAPER_GET_Q_DIFF_DOWN = 3;  //获取试题方式（单元下根据难度低一级替换试题）
    public static final int TEMP_PAPER_GET_Q_GROUP_SIMILAR = 4;  //获取试题方式（同组替换试题）

    public static final int VIEW_AREA_TYPE_PROVINCE             = 1; //省
    public static final int VIEW_AREA_TYPE_CITY                 = 2; //市
    public static final int VIEW_AREA_TYPE_DISTRICT             = 3; //区
    public static final int VIEW_AREA_TYPE_SCHOOL               = 4; //学校
    public static final int VIEW_AREA_TYPE_GRADE                = 5; //年级
    public static final int VIEW_AREA_TYPE_TEACHER              = 6; //教师
    public static final int VIEW_AREA_TYPE_GROUP                = 7; //班级


    public static final String SESSION_TYPE_RECOMMEND = "Recommend";

}
