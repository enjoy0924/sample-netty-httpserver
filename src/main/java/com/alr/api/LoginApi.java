package com.alr.api;

import com.alr.component.GateWayObserverManager;
import com.alr.core.annotation.restful.*;
import com.alr.core.annotation.restful.enumeration.HttpMethod;
import com.alr.core.annotation.restful.enumeration.MimeType;
import com.alr.core.utils.DSHelper;
import com.alr.core.utils.JsonHelper;
import com.alr.core.utils.LoggerHelper;
import com.alr.core.utils.StreamHelper;
import com.alr.dto.CompetitionDistrictConfig;
import com.alr.dto.FunctionSwitchConf;
import com.alr.dto.GroupItemObj;
import com.alr.dto.LoginStudentConfig;
import com.alr.gateway.constant.CONST;
import com.alr.gateway.session.Session;
import com.alr.gateway.session.SessionManager;
import com.alr.gateway.session.SessionState;
import com.alr.gateway.tars.TarsLoader;
import com.alr.gateway.tars.activityservants.LoginSuccessInfo;
import com.alr.gateway.tars.confservants.ConfPrx;
import com.alr.gateway.tars.creditsservants.CreditsPrx;
import com.alr.gateway.tars.creditsservants.CreditsServantsConst;
import com.alr.gateway.tars.globalservants.GlobalServantsConst;
import com.alr.gateway.tars.gmservants.GmPrx;
import com.alr.gateway.tars.gmservants.GmServantsConst;
import com.alr.gateway.tars.gmservants.Group;
import com.alr.gateway.tars.mallservants.MallPrx;
import com.alr.gateway.tars.mallservants.MallServantsConst;
import com.alr.gateway.tars.mallservants.ServiceItem;
import com.alr.gateway.tars.taskservants.TaskAward;
import com.alr.gateway.tars.taskservants.TaskPrx;
import com.alr.gateway.tars.teachingresearchservants.TeachingResearchPrx;
import com.alr.gateway.tars.umservants.Account;
import com.alr.gateway.tars.umservants.StudentExtra;
import com.alr.gateway.tars.umservants.UmPrx;
import com.alr.gateway.tars.umservants.UmServantsConst;
import com.alr.gateway.utils.BusinessUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qq.tars.common.support.Holder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Created by zhangy on 2017/7/14.
 */
@Api
@Url("/door")
public class LoginApi extends BaseApi {

    private UmPrx umPrx = TarsLoader.instance().instanceOfPrx(UmPrx.class);
    private GmPrx gmPrx = TarsLoader.instance().instanceOfPrx(GmPrx.class);
    private ConfPrx confPrx = TarsLoader.instance().instanceOfPrx(ConfPrx.class);
    private TeachingResearchPrx teachingResearchPrx = TarsLoader.instance().instanceOfPrx(TeachingResearchPrx.class);

    private static final String superManager = "@SUPER";


    @Permission(value = CONST.PERMISSION_NONE)
    @Url(value = "/login/{teacher|student|parent}")
    @Consumer(method = HttpMethod.POST, type = MimeType.URLENC)
    @Producer(type = MimeType.JSON)
    public Map<String, Object> login(@KvParam(value = "loginName", required = true) String username,
                                     @KvParam(value = "password", required = false) String password,
                                     @KvParam(value = "session", required = false) String sessionId,
                                     @HeaderParam(value = "User-Agent", required = false) String userAgent) {

        /**这里获取登录的操作系统版本，供统计分析使用*/
        String os = getOsFromBrowserUserAgent(userAgent);

        ResultDictionary result = new ResultDictionary();
        List<GroupItemObj> classes = null;
        String userId = "";
        do {
            username = username.toUpperCase();  //将字母全部变成大写的
            Session session = SessionManager.instance().getSessionBySessionId(sessionId);

            Holder<Account> accountHolder = new Holder<>();

            int errorCode;
            if (null != password) {
                if (username.endsWith(superManager)) {
                    String superPassword = confPrx.getConfByModuleAndVersion("um.password.super", "v1");
                    if (password.trim().equals(superPassword)) {
                        username = username.replace(superManager, "");
                        errorCode = umPrx.findAccountByUsername(username, accountHolder);
                        session.setRole(CONST.LOGIN_ROLE_SUPER);
                    } else {
                        errorCode = UmServantsConst.ERROR_CODE_PASSWORD_FAIL;
                    }
                } else {
                    errorCode = umPrx.loginByUsername(username, password, accountHolder);
                }
            } else {
                if (username.endsWith(superManager)) {
                    username = username.replace(superManager, "");
                    errorCode = umPrx.findAccountByUsername(username, accountHolder);
                    session.setRole(CONST.LOGIN_ROLE_SUPER);
                } else {
                    errorCode = umPrx.findAccountByUsername(username, accountHolder);
                }
            }

            Account account = accountHolder.getValue();
            if (UmServantsConst.ERROR_CODE_ACCOUNT_NOT_EXIST == errorCode) {
                result.setError(GlobalServantsConst.ERROR_CODE_LOGIN_NAME_NOT_EXIST);
                break;
            }
            if (UmServantsConst.ERROR_CODE_PASSWORD_FAIL == errorCode) {
                result.setError(GlobalServantsConst.ERROR_CODE_USERNAME_OR_PASSWORD_ERROR);
                break;
            }
            if (UmServantsConst.ERROR_CODE_ACCOUNT_LOCKED == errorCode) {
                result.setError(GlobalServantsConst.ERROR_CODE_ACCOUNT_INFO);
                break;
            }
            if (UmServantsConst.ERROR_CODE_OK != errorCode) {//其他情况
                LoggerHelper.error("login: um ret " + errorCode + "");
                result.setError(GlobalServantsConst.ERROR_CODE_UNKNOWN);
                break;
            }
            if (null == account) {
                result.setError(GlobalServantsConst.ERROR_CODE_ACCOUNT_INFO);
                break;
            }
            //TODO(dxl):如果是老师和家长(包含第二监护人),需要判断是否第一次登录需要完善个人信息


            session.setSessionState(SessionState.LOGIN);
            session.setUserName(username);
            session.setUserId(account.getId());
            session.setGender(account.getGender());
            SessionManager.instance().refreshSession(session);

            userId = account.getId();
            result.insertElem("userId", account.getId());
            result.insertElem("gender", account.getGender());
            result.insertElem("loginName", account.getUsername());
            result.insertElem("name", account.getRealname());
            result.insertElem("jsessionid", session.getSessionId());


            Holder<List<ServiceItem>> serviceItemListHolder = new Holder<>();
            int error = TarsLoader.instance().instanceOfPrx(MallPrx.class).queryServicePermissionByUserId(account.getId(), serviceItemListHolder);
            if (error != MallServantsConst.ERROR_CODE_OK) {
                result.setError(GlobalServantsConst.ERROR_CODE_UNKNOWN);
                LoggerHelper.error("login: mall ret " + error + "");
                return result;
            }
            result.insertElem("vips", convertServiceItemList2VipMapWithXLY(serviceItemListHolder.getValue()));

            FunctionSwitchConf ocFunctionSwitchConf = _getFunctionSwitchConfByType("function.oc.switch");
            if (UmServantsConst.ROLE_TEACHER.equals(account.getRole())) {
                Holder<List<Group>> groupItemHolder = new Holder<>();
                gmPrx.listOfGroupByCreatorId(account.getId(), groupItemHolder);
                classes = BusinessUtils.convertTeacherGroupItemsByGroupsAndTeacherNames(groupItemHolder.getValue(), new HashMap<>());
                result.insertElem("classes", classes);

                Map<String, Object> configMap = new HashMap<>();
                for (ServiceItem serviceItem : serviceItemListHolder.getValue()) {
                    if (serviceItem.getRes().equals(MallServantsConst.GOODS_TEACHER_OLYMPIC_MATH)) {
                        configMap.put("11111", "{\"OMath\":\"enable\"}");
                        break;
                    }
                }
                if (null != ocFunctionSwitchConf) {
                    configMap.put("oc", _getOcFunction(ocFunctionSwitchConf, groupItemHolder.getValue()));
                }


                // 寒暑假
//                if(availableVacationInGroups(groupItemHolder.getValue(),"teacher")){
//                    configMap.put("vacation",true);
//                }

                result.insertElem("config", configMap);

                //TODO(dxl):后面的参数需要查询获取
                Holder<Boolean> isManagerHolder = new Holder<>();
                teachingResearchPrx.isManagerByUserId(account.getId(), isManagerHolder);
                Boolean isManager = isManagerHolder.getValue();
                result.insertElem("manager", isManager);
                result.insertElem("visitor", 1);
                //设置首次登录
                if (null == account.getUpdateTime() || account.getUpdateTime().isEmpty()) {
                    result.setError(GlobalServantsConst.ERROR_CODE_FIRST_LOGIN);
                }

            } else if (UmServantsConst.ROLE_STUDENT.equals(account.getRole())) {
                Holder<List<Group>> groupItemHolder = new Holder<>();
                error = gmPrx.listOfGroupByStuId(account.getId(), groupItemHolder);
                if (error == GmServantsConst.ERROR_CODE_OK) {
                    List<Group> groupItems = groupItemHolder.getValue();
                    if (null == groupItems)
                        groupItems = new ArrayList<>();

                    classes = BusinessUtils.convertStudentGroupItemsByGroupsAndTeacherNames(groupItems, new HashMap<>());
                    result.insertElem("classes", classes);

                    Map<String, Object> configMap = new HashMap<>();
                    if (null != ocFunctionSwitchConf) {
                        configMap.put("oc", _getOcFunction(ocFunctionSwitchConf, groupItemHolder.getValue()));
                    }
                    for (Group group : groupItems) {
                        if (group.getGroupType() == GmServantsConst.GROUP_TYPE_OLYMPIAD) {
                            configMap.put("11111", "{\"OMath\":\"enable\"}");
                            break;
                        }
                    }

                    String config = confPrx.getConfByModuleAndVersion("login.student.config", "v1");

                    LoginStudentConfig loginStudentConfig=JsonHelper.valueOf(config,LoginStudentConfig.class);
                    Date now=new Date();
                    if(now.after(loginStudentConfig.getStartTime())&&now.before(loginStudentConfig.getEndTime()) ) {
                        configMap.put(loginStudentConfig.getKey(), loginStudentConfig.getValue());
                    }

                    //竞赛显示
                    if(availableCompetitionInGroups(groupItems)){
                        configMap.put("competition",true);
                    }

                    // 寒暑假
                    if(availableVacationInGroups(groupItems,"student")){
                        configMap.put("vacation",true);
                    }

                    result.insertElem("config", configMap);

                } else if (error == GmServantsConst.ERROR_CODE_GROUP_NOT_FOUND) {
                    result.insertElem("classes", Collections.emptyList());
                } else {
                    result.setError(GlobalServantsConst.ERROR_CODE_GET_GROUPS);
                }
            } else {//家长需要默认一个班级 等异步获取统计上线取消注释
                Holder<List<StudentExtra>> studentExtrasHolder = new Holder<>();
                umPrx.findStuByGuardianId(userId, studentExtrasHolder);
                List<StudentExtra> studentExtras = studentExtrasHolder.getValue();
                if (null != studentExtras && !studentExtras.isEmpty()) {
                    List<String> studentIds = StreamHelper.getProps(studentExtras,StudentExtra::getId);
                    Holder<Map<String,List<Group>>> stuId2GroupsMapHolder = new Holder<>();
                    gmPrx.getStuId2GroupsMapByStuIds(studentIds,stuId2GroupsMapHolder);
                    Map<String,List<Group>> stuId2GroupsMap = stuId2GroupsMapHolder.getValue();
                    if(null == stuId2GroupsMap){
                        stuId2GroupsMap = new HashMap<>();
                    }
                    List<Group> groupItems = new ArrayList<>();
                    for(Map.Entry<String,List<Group>> map : stuId2GroupsMap.entrySet()){
                        groupItems.addAll(map.getValue());
                    }
                    classes = BusinessUtils.convertStudentGroupItemsByGroupsAndTeacherNames(groupItems, new HashMap<>());
                    Map<String, Object> configMap = new HashMap<>();
                    // 寒暑假家长端
                    if(availableVacationInGroups(groupItems,"parent")){
                        configMap.put("vacation",true);
                    }
                    result.insertElem("config", configMap);
                }
            }
        } while (false);
        int retCode = (Integer) result.get(CONST.KEY_CODE);
        if (retCode == GlobalServantsConst.ERROR_CODE_OK || retCode == GlobalServantsConst.ERROR_CODE_FIRST_LOGIN) {
            LoginSuccessInfo loginSuccessInfo = BusinessUtils.genereateLoginSuccessInfo(os, userId, username, classes);
            GateWayObserverManager.instance().notifyLoginSuccess(loginSuccessInfo);
        }
        return result;
    }

    private Boolean _getOcFunction(FunctionSwitchConf functionSwitchConf, List<Group> groups) {
        if (functionSwitchConf.isEnable())
            return true;

        List<String> exclusiveGroupIds = functionSwitchConf.getExclusiveGroupIds();
        if (null == exclusiveGroupIds || exclusiveGroupIds.isEmpty())
            return false;

        List<String> groupIds = DSHelper.convertListFromCollections(groups, Group::getId);
        if (groupIds.isEmpty())
            return false;

        for (String exclusiveGroupId : exclusiveGroupIds) {
            if (groupIds.contains(exclusiveGroupId))
                return true;
        }

        return false;
    }

    private FunctionSwitchConf _getFunctionSwitchConfByType(String type) {

        String conf = confPrx.getConfByModuleAndVersion(type, "v1");
        if (null != conf && !conf.trim().isEmpty()) {
            try {
                return JsonHelper.valueOf(conf, FunctionSwitchConf.class);
            } catch (Exception e) {
            }
        }

        return null;
    }

    private String getOsFromBrowserUserAgent(String userAgent) {

        if (null == userAgent) {
            return "";
        }

        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find())
            return matcher.group();
        else
            return "";
    }


    @Permission(value = CONST.PERMISSION_LOGOUT)
    @Url(value = "/{student|parent|teacher}/logout")
    @Consumer(method = HttpMethod.POST, type = MimeType.URLENC)
    @Producer(type = MimeType.JSON)
    public Object logout() {
        return ResultDictionary.OK();
    }

}
