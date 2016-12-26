package etcomm.com.etcommyolk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
 * Created by zuohr on 2016/12/8.
 * 进行gradle数据处理及相关参数引用
 */
public class EtcommApplication extends Application {

    public static EtcommApplication etcommApplication;
    /**
     * 接口跟Url
     */
    public static String BASE_URL;


    /**
     * 是否是第一次默认设置
     * 针对MainActivity默认设置Home为显示页面
     * <p/>
     * 每一次从其他页面返回MainActivity可见页面时，都会先初始化一下Home的初始值
     * 无故增多网络请求次数，因此添加限定
     */
    public static boolean isFirstSetDefault = true;
    /**
     * 添加的打开过的Activity
     */
    public static ArrayList<Activity> activities = new ArrayList<Activity>();

    /**
     * 添加Activity  一键退出调用这个方法
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * Activity 的一键退出
     */
    public static void finishActivity() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).finish();
        }
        /**
         * 所有activity关闭后，结束进程
         */
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化域名信息
     */
    private void initDoMain() {
        //获取本应用程序信息
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //读取信息
        if (null != applicationInfo) {
            this.BASE_URL = applicationInfo.metaData.getString("serverDoMain");
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        etcommApplication = this;
        initDoMain();
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public static Context getContext() {
        return etcommApplication;
    }

    /**
     * @return 服务器域名
     */
    public static String getServerDoMain() {
        return BASE_URL;
    }

    /**
     * 获取手机验证码
     * @return
     */
    public static String getPhoneCode(){
        return BASE_URL + "mobile-captcha/send";
    }

    /**
     * 验证手机验证码
     * @return
     */
    public static String verifyPhoneCode(){
        return BASE_URL + "mobile-captcha/validate";
    }

    /**
     * 获取邮箱验证码
     * @return
     */
    public static String getMailCode(){
        return BASE_URL + "email-captcha/send";
    }

    /**
     * 验证邮箱验证码
     * @return
     */
    public static String verifyMailCode(){
        return BASE_URL + "email-captcha/validate";
    }

    /**
     * 邮箱找回密码
     * @return
     */
    public static String alterMailPwd(){
        return BASE_URL + "email-forgot-password";
    }

    /**
     * 手机找回密码
     * @return
     */
    public static String alterPhonePwd(){
        return BASE_URL + "mobile-forgot-password";
    }

    /**
     * 效验邀请码
     */
    public static String getSerialNumber(){ return BASE_URL + "serial-number"; }

    /**
     * 邮箱注册
     */
    public static String toMailRegister(){ return BASE_URL + "email-sign-up"; }

    /**
     * 手机注册
     */
    public static String toPhoneRegister(){ return BASE_URL + "mobile-sign-up"; }

    /**
     * 获取默认头像
     */
    public static String getDefaultAvator(){ return BASE_URL + "avatar"; }

    /**
     * 完善注册信息
     */
    public static String toRegisterUpdateInfo(){return  BASE_URL + "user/update"; }
    /**
     * 修改用户基本信息
     */
    public static String toUserEdit(){return  BASE_URL + "user/edit"; }
    /**
     * 退出登录
     */
    public static String toExit(){return  BASE_URL + "sign-in/sign-out"; }
    /**
     * 上传头像
     */
    public static String toUploadUserAvator(){return  BASE_URL + "user/avatar"; }
    /**
     * 检测版本更新
     */
    public static String toUploadversion(){return  BASE_URL + "version"; }

    /**
     * 修改密码
     */
    public static String toChangePassword(){return  BASE_URL + "user/change-password"; }
    /**
     * 设置消息推送
     */
    public static String toSetPush(){return  BASE_URL + "user/set-push"; }
    /**
     * 意见反馈
     */
    public static String toFeedBack(){return  BASE_URL + "feedback"; }
    /**
     * 健康资讯-获取已收藏列表
     */
    public static String toFavorite(){return  BASE_URL + "user/favorite"; }



    public static String LOGIN() {
        return BASE_URL + "sign-in";
    }

    public static String POINT_EXCHANGE() {
        return BASE_URL + "gift";
    }

    public static String EXCHANGE() {
        return BASE_URL + "gift/gift-exchange";// 兑换礼品
    }

    public static String MYPOINTS_DETAIL() {
        return BASE_URL + "user/score";//我的积分记录页面
    }

    public static String SCORE_RULE() {
        return BASE_URL + "user/score-rules";//我的积分规则
    }

    public static String MY_EXCHANGE() {
        return BASE_URL + "gift/exchange-list";
    }

    public static String HOME() {
        return BASE_URL + "home";
    }

    public static String ACTIVITY_LIST() {
        return BASE_URL + "activity";
    }

    public static String NEWS_LIST() {
        return BASE_URL + "health-news";
    }

    public static String NEWS_SEARCH() {
        return BASE_URL + "health-news/search";
    }

    public static String WELFARE_LIST() {
        return BASE_URL + "welfare";
    }

    public static String ALL_GROUP_LIST() {
        return BASE_URL + "topic";
    }

    public static String ATTENTION_GROUP() {
        return BASE_URL + "topic/follow";
    }

    public static String UN_ATTENTION_GROUP() {
        return BASE_URL + "topic/un-follow";
    }

    public static String CHECK_CREATE() {
        return BASE_URL + "topic/check-create";
    }

    public static String TOPIC_CREATE() {
        return BASE_URL + "topic/create";
    }

}
