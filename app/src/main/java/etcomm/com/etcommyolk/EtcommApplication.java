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

    public static String DISCUSSION() {
        return BASE_URL + "discussion";
    }

    public static String TOPIC_UPDATE() {
        return BASE_URL + " topic/update";
    }
   

}
