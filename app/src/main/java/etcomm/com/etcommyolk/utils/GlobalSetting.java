package etcomm.com.etcommyolk.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class GlobalSetting {


    private static Context mContext;
    private static SharedPreferences mPrefs;
    private static GlobalSetting mSpInstance;

    private static final String PREFERENCE_NAME = "etcommyolk";
    private static final String PREFERENCE_NAME_LOGIN = "etcommyolk_login";
    private static final String PREFERENCE_USER_ID = "user_id";
    private static final String PREFERENCE_SCORE = "preference_score";
    public static final String ACCESS_TOKEN = "access_token"; //保存token
    public static final String TOTALSCORE = "totalscore";

    private static final String USER_ID = "id";//用户ID
    private static final String DEPARTMENT_ID = "department_id"; //组织结构ID
    private static final String CUSTOMER_ID = "customer_id"; //客户ID
    private static final String SERIAL_NUMBER_ID = "serial_number_id"; //激活码ID
    private static final String NICK_NAME = "nick_name"; //昵称
    private static final String REAL_NAME = "real_name"; //真实姓名
    private static final String BIRTHDAY = "birthday"; //生日
    private static final String BIRTH_YEAR = "birth_year"; //出生年份
    private static final String HEIGHT = "height";//身高CM
    private static final String GENDER = "gender"; // 男1，女2
    private static final String WEIGHT = "weight"; //体重KG
    private static final String AVATAR = "avatar"; //头像
    private static final String MOBILE = "mobile"; //手机
    private static final String EMAIL = "email"; //邮件
    private static final String JOB_NUMBER = "job_number"; //工号
    private static final String SCORE = "score";// 积分
    private static final String TOTAL_SCORE = "total_score";//累计积分
    private static final String PEDOMETER_TARGET = "pedometer_target";//运动目标
    private static final String PEDOMETER_DISTANCE = "pedometer_distance"; //运动积累距离
    private static final String PEDOMETER_TIME = "pedometer_time"; //运动积累时间
    private static final String PEDOMETER_CONSUME = "pedometer_consume"; //运动积累消耗
    private static final String IS_LEADER = "is_leader";//是否领导
    private static final String CREATED_AT = "created_at"; //注册时间
    private static final String IS_SING = "is_sign"; //签到状态 1:已签到 0：未签到
    private static final String CUSTOMER_IMAGE = "customer_image"; //app启动页
    private static final String INFO_STATUS = "info_status";//用户信息是否完整 1：完整 0：不完整
    private static final String IS_LIKE = "is_like";  //消息推送赞开关 1：开启 0：关闭
    private static final String IS_COMMENT = "is_comment"; //消息推送评论/回复开关 1：开启 0:关闭
    private static final String ISLEVEL = "islevel"; //1 :属于分级权限 0：没有分级权限
    private static final String USER_AGE = "user_age"; //1 :属于分级权限 0：没有分级权限
    private static final String FIRSTSETUSERINFO = "isfirstsetuserinfo";// 是否第一次设置用户信息
    private static final String USER_NAME = "user_name";// 是否第一次设置用户信息
    private static final String USER_PWD = "user_pwd";// 是否第一次设置用户信息
    private static final String USER_AVATAR = "user_avatar";// 是否第一次设置用户信息
    private static final String ISPUSHMSG = "IsPushMsg";
    private static final String ISPUSHMSGLIKE = "IsPushMsg_Like";
    private static final String ISPUSHMSGCOMMENT = "IsPushMsg_Comment";


    private GlobalSetting(Context context) {
        mContext = context;
    }

    // 单例
    public static synchronized GlobalSetting getInstance(Context context) {
        if (mSpInstance == null) {
            mSpInstance = new GlobalSetting(context);
        }
        return mSpInstance;
    }


    // 初始化sp对象
    public SharedPreferences getSharedPreferences() {
        if (mPrefs == null) {
            mPrefs = mContext.getSharedPreferences(PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mPrefs;
    }
    public SharedPreferences getSharedPreferencesLogin() {
        if (mPrefs == null) {
            mPrefs = mContext.getSharedPreferences(PREFERENCE_NAME_LOGIN,
                    Context.MODE_PRIVATE);
        }
        return mPrefs;
    }




    public void clear(){
        getSharedPreferences().edit().clear();
        getSharedPreferences().edit().commit();
    }
    public void clear_Login(){
        getSharedPreferencesLogin().edit().clear();
        getSharedPreferencesLogin().edit().clear();
    }

    //下几项用户用户登录时使用
    //用户名
    public void saveLoginUserNmae(String value){
        SharedPreferences.Editor editor = getSharedPreferencesLogin().edit();
        editor.putString(USER_NAME, value);
        editor.commit();
    }

    public String getLoginUserName(){
        return getSharedPreferencesLogin().getString(USER_NAME, "");
    }
    //密码
    public void saveLoginUserPwd(String value){
        SharedPreferences.Editor editor = getSharedPreferencesLogin().edit();
        editor.putString(USER_PWD, value);
        editor.commit();
    }

    public String getLoginUserPwd(){
        return getSharedPreferencesLogin().getString(USER_PWD, "");
    }

    //用户头像
    public void saveLoginUserAvatar(String value){
        SharedPreferences.Editor editor = getSharedPreferencesLogin().edit();
        editor.putString(USER_AVATAR, value);
        editor.commit();
    }

    public String getLoginUserAvatar(){
        return getSharedPreferencesLogin().getString(USER_AVATAR, "");
    }













    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();

    }
    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // 保存uid
    public void saveUid(String uid) {
        saveString(PREFERENCE_USER_ID, uid);
    }

    // 获取uid
    public String getUid() {
        return getSharedPreferences().getString(PREFERENCE_USER_ID, "");
    }

    /**
     * 设置积分
     *
     * @return
     */
    public void setScore(String score) {
        saveString(PREFERENCE_SCORE, score);
    }

    /**
     * 获取积分
     *
     * @return
     */
    public String getScore() {
        return getSharedPreferences().getString(PREFERENCE_SCORE, "0");
    }

    /**
     * 存储ACCESS_TOKEN
     *
     * @return
     */
    public void setAccessToken(String name) {
        saveString(ACCESS_TOKEN, name);
    }

    public String getAccessToken() {
        return getSharedPreferences().getString(ACCESS_TOKEN, "");
    }

    /**
     * 设总积分
     *
     * @return
     */
    public void setTotalScore(String score) {
        saveString(TOTALSCORE, score);
    }

    /**
     * 获取总积分
     *
     * @return
     */
    public String getTotalScore() {
        return getSharedPreferences().getString(TOTALSCORE, "");
    }

    /**
     * 用户ID
     * @param id
     */
    public void setUserId(String id){saveString(USER_ID, id);}
    public String getUserId(){return getSharedPreferences().getString(USER_ID , "");}

    /**
     * 组织结构ID
     */
    public void setDepartmentId(String string){
        saveString(DEPARTMENT_ID, string);
    }
    public String getDepartmentId(){
        return getSharedPreferences().getString(DEPARTMENT_ID, "");
    }

    /**
     * 客户ID
     * @param string
     */
    public void setCustomerId(String string){
        saveString(CUSTOMER_ID, "");
    }
    public String getCustomerId(){
        return getSharedPreferences().getString(CUSTOMER_ID, "");
    }

    /**
     * 激活码
     */
    public void setSerialNumberId(String string){
        saveString(SERIAL_NUMBER_ID, string);
    }
    public String getSerialNumberId(){
        return getSharedPreferences().getString(SERIAL_NUMBER_ID, "");
    }

    /**
     * 昵称
     */
    public void setNickName(String string){
        saveString(NICK_NAME, string);
    }
    public String getNickName(){
        return getSharedPreferences().getString(NICK_NAME, "");
    }

    /**
     * 真实姓名
     */
    public void setRealName(String string){
        saveString(REAL_NAME, string);
    }
    public String getRealName(){
        return getSharedPreferences().getString(REAL_NAME, "");
    }

    /**
     * 生日
     */
    public void setBirthday(String string){
        saveString(BIRTHDAY, string);
    }
    public String getBirthday(){
        return getSharedPreferences().getString(BIRTHDAY, "");
    }

    /**
     * 出生年份
     */
    public void setBirthYear(String string){
        saveString(BIRTH_YEAR, string);
    }
    public String getBirthYear(){
        return getSharedPreferences().getString(BIRTH_YEAR, "");
    }

    /**
     * 身高 CM
     */
    public void setHeight(String string){
        saveString(HEIGHT, string);
    }
    public String getHeight(){
        return getSharedPreferences().getString(HEIGHT, "");
    }

    /**
     * 性别
     */
    public void setGender(String string){
        saveString(GENDER, string);
    }
    public String getGender(){
        return  getSharedPreferences().getString(GENDER, "1");
    }

    /**
     * 体重KG
     */
    public void setWeight(String string){
        saveString(WEIGHT, string);
    }
    public String getWeight(){
        return getSharedPreferences().getString(WEIGHT, "");
    }

    /**
     * 头像 url
     */
    public void setAvatar(String string){
        saveString(AVATAR, string);
    }
    public String getAvatar(){
        return getSharedPreferences().getString(AVATAR, "");
    }

    /**
     * 手机
     */
    public void setMobile(String string){
        saveString(MOBILE, string);
    }
    public String getMobile(){
        return getSharedPreferences().getString(MOBILE, "");
    }

    /**
     * 邮件
     */
    public void setEmail(String string){
        saveString(EMAIL, string);
    }
    public String getEmail(){
        return getSharedPreferences().getString(EMAIL, "");
    }

    /**
     *工号
     */
    public void setJobNumber(String string){
        saveString(JOB_NUMBER, string);
    }
    public String getJobNumber(){
        return getSharedPreferences().getString(JOB_NUMBER, "");
    }

    /**
     * 积分
     */
    public void setLoginScore(String string){
        saveString(SCORE, string);
    }
    public String getLoginScore(){
        return getSharedPreferences().getString(SCORE, "");
    }

    /**
     * 累计积分
     */
    public void setTotalscore(String string){
        saveString(TOTAL_SCORE, string);
    }
    public String getTotalscore(){
        return getSharedPreferences().getString(TOTAL_SCORE, "");
    }

    /**
     * 运动目标
     */
    public void setPedometerTarget(String string){
        saveString(PEDOMETER_TARGET, string);
    }
    public String getPedometerTarget(){
        return getSharedPreferences().getString(PEDOMETER_TARGET, "");
    }

    /**
     * 运动累计距离
     */
    public void setPedometerDistance(String str){
        saveString(PEDOMETER_DISTANCE, str);
    }
    public String getPedometerDistance(){
        return getSharedPreferences().getString(PEDOMETER_DISTANCE, "");
    }

    /**
     * 运动累计时间
     */
    public void setPedometerTime(String str){
        saveString(PEDOMETER_TIME, str);
    }
    public String getPedometerTime(){
        return getSharedPreferences().getString(PEDOMETER_TIME, "");
    }

    /**
     * 运动累计消耗
     */
    public void setPedometerConsume(String str){
        saveString(PEDOMETER_CONSUME, str);
    }
    public String getPedometerConsume(){
        return  getSharedPreferences().getString(PEDOMETER_CONSUME, "");
    }

    /**
     * 是否为领导
     */
    public void setIsLeader(String str){
        saveString(IS_LEADER, str);
    }
    public String getIsLeader(){
        return getSharedPreferences().getString(IS_LEADER, "");
    }

    /**
     * 注册时间
     */
    public void setCreatedAt(String str){
        saveString(CREATED_AT, str);
    }
    public String getCreatedAt(){
        return getSharedPreferences().getString(CREATED_AT, "");
    }

    /**
     * 签到状态
     */
    public void setIsSign(String string){
        saveString(IS_SING, string);
    }
    public String getIsSign(){
        return getSharedPreferences().getString(IS_SING, "");
    }

    /**
     * APP启动页
     */
    public void setCustomerImage(String str){
        saveString(CUSTOMER_IMAGE, str);
    }
    public String getCustomerImage(){
        return getSharedPreferences().getString(CUSTOMER_IMAGE, "");
    }

    /**
     * 用户信息是否完整 1完整 0 不完整
     */
    public void setInfoStatus(String str){
        saveString(INFO_STATUS, str);
    }
    public String getInfoStatus(){
        return getSharedPreferences().getString(INFO_STATUS, "");
    }

    /**
     * 消息推送赞开关 1开启 0 关闭
     */
    public void setIsLike(String string){
        saveString(IS_LIKE, string);
    }
    public String getIsLike(){
        return getSharedPreferences().getString(IS_LIKE, "");
    }

    /**
     * 消息推送评论/回复开关 1开启 0关闭
     */
    public void setIsComment(String string){
        saveString(IS_COMMENT, string);
    }
    public String getIsComment(){
        return  getSharedPreferences().getString(IS_COMMENT, "");
    }

    /**
     * 是否属于分级权限 1有 0无
     */
    public void setIslevel(String string){
        saveString(ISLEVEL, string);
    }
    public String getIslevel(){
        return  getSharedPreferences().getString(ISLEVEL, "");
    }
    /**
     * 用户年龄
     */
    public void setFirstsetuserinfo(Boolean boo){
        saveBoolean(FIRSTSETUSERINFO, boo);
    }
    public boolean getFirstsetuserinfo(){
        return  getSharedPreferences().getBoolean(FIRSTSETUSERINFO, false);
    }
    /**
     * 用户年龄
     */
    public void setIsPushMsg(Boolean boo){
        saveBoolean(ISPUSHMSG, boo);
    }
    public boolean getIsPushMsg(){
        return  getSharedPreferences().getBoolean(ISPUSHMSG, true);
    }
    /**
     * 用户年龄
     */
    public void setIsPushMsg_Like(Boolean boo){
        saveBoolean(ISPUSHMSGLIKE, boo);
    }
    public boolean getIsPushMsg_Like(){
        return  getSharedPreferences().getBoolean(ISPUSHMSGLIKE, true);
    }
    /**
     * 用户年龄
     */
    public void setIsPushMsg_Comment(Boolean boo){
        saveBoolean(ISPUSHMSGCOMMENT, boo);
    }
    public boolean getIsPushMsg_Comment(){
        return  getSharedPreferences().getBoolean(ISPUSHMSGCOMMENT, true);
    }


}
