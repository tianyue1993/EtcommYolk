package etcomm.com.etcommyolk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;

import me.chunyu.pedometerservice.PedometerCounterService;

/**
 * Created by zuohr on 2016/12/8.
 * 进行gradle数据处理及相关参数引用
 */
public class EtcommApplication extends Application {
    public static Context mCon;

    public static EtcommApplication etcommApplication;
    /**
     * 接口跟Url
     */
    public static String BASE_URL;

    //在application文件中配置三方平台的appkey
    {
        PlatformConfig.setWeixin("wxfc0c9c862c377d06", "10e2560413f9b37dc5f01749ea061259");
    }

    /**
     * 是否是第一次默认设置
     * 针对MainActivity默认设置Home为显示页面
     * <p/>
     * 每一次从其他页面返回MainActivity可见页面时，都会先初始化一下Home的初始值
     * 无故增多网络请求次数，因此添加限定
     */
    public static boolean isFirstSetDefault = true;
    /**
     * 是否为强制更新
     */
    public static boolean MUSTUPDATE = false;

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
        mCon = getApplicationContext();
        Fresco.initialize(this);
        UMShareAPI.get(this);
        PedometerCounterService.initAppService(getApplicationContext());
        etcommApplication = this;
        initDoMain();
        initImageLoader();

    }


    private void initImageLoader() {

        DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultDisplayImageOptions).threadPriority(Thread.NORM_PRIORITY).discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.FIFO).memoryCache(new WeakMemoryCache()).writeDebugLogs().build();

        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PedometerCounterService.releaseAppService();
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
     *
     * @return
     */
    public static String getPhoneCode() {
        return BASE_URL + "mobile-captcha/send";
    }

    /**
     * 验证手机验证码
     *
     * @return
     */
    public static String verifyPhoneCode() {
        return BASE_URL + "mobile-captcha/validate";
    }

    /**
     * 获取邮箱验证码
     *
     * @return
     */
    public static String getMailCode() {
        return BASE_URL + "email-captcha/send";
    }

    /**
     * 验证邮箱验证码
     *
     * @return
     */
    public static String verifyMailCode() {
        return BASE_URL + "email-captcha/validate";
    }

    /**
     * 邮箱找回密码
     *
     * @return
     */
    public static String alterMailPwd() {
        return BASE_URL + "email-forgot-password";
    }

    /**
     * 手机找回密码
     *
     * @return
     */
    public static String alterPhonePwd() {
        return BASE_URL + "mobile-forgot-password";
    }

    /**
     * 效验邀请码
     */
    public static String getSerialNumber() {
        return BASE_URL + "serial-number";
    }

    /**
     * 邮箱注册
     */
    public static String toMailRegister() {
        return BASE_URL + "email-sign-up";
    }

    /**
     * 手机注册
     */
    public static String toPhoneRegister() {
        return BASE_URL + "mobile-sign-up";
    }

    /**
     * 获取默认头像
     */
    public static String getDefaultAvator() {
        return BASE_URL + "avatar";
    }

    /**
     * 完善注册信息
     */
    public static String toRegisterUpdateInfo() {
        return BASE_URL + "user/update";
    }

    /**
     * 修改用户基本信息
     */
    public static String toUserEdit() {
        return BASE_URL + "user/edit";
    }

    /**
     * 退出登录
     */
    public static String toExit() {
        return BASE_URL + "sign-in/sign-out";
    }

    /**
     * 上传头像
     */
    public static String toUploadUserAvator() {
        return BASE_URL + "user/avatar";
    }

    /**
     * 检测版本更新
     */
    public static String toUploadversion() {
        return BASE_URL + "version";
    }

    /**
     * 修改密码
     */
    public static String toChangePassword() {
        return BASE_URL + "user/change-password";
    }

    /**
     * 设置消息推送
     */
    public static String toSetPush() {
        return BASE_URL + "user/set-push";
    }

    /**
     * 意见反馈
     */
    public static String toFeedBack() {
        return BASE_URL + "feedback";
    }

    /**
     * 健康资讯-获取已收藏列表
     */
    public static String toFavorite() {
        return BASE_URL + "user/favorite";
    }

    /**
     * 我的活动
     */
    public static String toMyActivity() {
        return BASE_URL + "user/my-activity";
    }

    /**
     * 每日签到
     */
    public static String toSignIn() {
        return BASE_URL + "user/sign-in";
    }

    /**
     * 每日排名
     */
    public static String toShowRank() {
        return BASE_URL + "user/rank";
    }

    /**
     * 天气
     */
    public static String toWeather() {
        return BASE_URL + "weather";
    }

    /**
     * 获取前七天的计步数据
     */
    public static String toPedometerWeek() {
        return BASE_URL + "user/pedometer-two-months";
    }

    /**
     * 解绑设备
     */
    public static String toBindOff() {
        return BASE_URL + "device-bind/bind-off";
    }

    /**
     * 绑定设备
     */
    public static String toBindOn() {
        return BASE_URL + "device-bind/bind-on";
    }

    /**
     * 按天同步设备
     */
    public static String toDateSync() {
        return BASE_URL + "pedometer/date-sync";
    }

    /**
     * 个人排行榜 H5
     */
    public static String toMyRank() {
        return BASE_URL + "user/my-rank";
    }

    /**
     * 部门排行榜
     */
    public static String toStructureRank() {
        return BASE_URL + "structure/structure-rank";
    }

    /**
     * 上传个人记步数据
     */
    public static String toTrendsUrl() {
        return BASE_URL + "user/trends-url";
    }

    /**
     * 获取用户基本信息[我的模块使用]
     */
    public static String toUserProfile() {
        return BASE_URL + "user/profile";
    }

    /**
     * 消息推送
     */
    public static String toNews() {
        return BASE_URL + "news";
    }

    /**
     * 删除某一条推送消息
     */
    public static String toNewsDelete() {
        return BASE_URL + "news/news-delete";
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
        return BASE_URL + "topic/update";
    }

    public static String CREATE_DISCUSSION() {
        return BASE_URL + "discussion/create-discussion";// 讨论-发布(文字)
    }


    public static String CREATE_DISCUSSION_PIC() {
        return BASE_URL + "discussion/create-image-base";// 讨论-发布(图片-base64)
    }

    public static String DISCUSSION_DELETE() {
        return BASE_URL + "discussion/delete";// 删除讨论
    }


    public static String DISCUSSION_UNLIKE() {
        return BASE_URL + "discussion/un-like"; // 取消赞
    }

    public static String DISCUSSION_LIKE() {
        return BASE_URL + "discussion/like"; // 取消赞
    }

    public static String COMMENT_DELETE() {
        return BASE_URL + "discussion-comment/delete"; // 评论-删除
    }

    public static String DISCUSSION_COMMENT() {
        return BASE_URL + "discussion-comment";// 评论-列表
    }

    public static String COMMENT_CREATE() {
        return BASE_URL + "discussion-comment/create";// 添加或者回复某人评论
    }

    public static String TOPIC_USE() {
        return BASE_URL + "topic/topic-user";// 获取成员

    }

    public static String TOPIC_DELETE() {
        return BASE_URL + "topic/delete"; // 话题删除

    }

    public static String GOOD_GROUP() {
        return BASE_URL + "topic/quality"; //优质小组

    }

    public static String REPORT() {
        return BASE_URL + "common/report"; //举报

    }

    public static String SHARE() {
        return BASE_URL + "common/share"; //分享

    }


}
