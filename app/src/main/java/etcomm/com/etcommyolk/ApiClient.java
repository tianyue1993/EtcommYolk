package etcomm.com.etcommyolk;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;

import etcomm.com.etcommyolk.handler.JsonResponseHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class ApiClient {
    public static final String BASE_URL = "http://113.59.227.10:86/index.php?r="; // 测试
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);


    private AsyncHttpClient asyncHttpClient;
    private GlobalSetting pres;

    private volatile static ApiClient instance;

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }


    private ApiClient() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setURLEncodingEnabled(false);
        asyncHttpClient.setTimeout(10000);
        asyncHttpClient.setMaxRetriesAndTimeout(0, 5000);
        asyncHttpClient.addHeader("Accept", "application/json; version=public");
        try {
            asyncHttpClient.setSSLSocketFactory(new MySSLSocketFactory(KeyStore.getInstance("BKS")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    public void login(String url, TextHttpResponseHandler handler) {
        asyncHttpClient.get(url, handler);
    }

    /**
     * 登录接口
     */
    public void invokeLogin(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.LOGIN() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(),
                entity, handler);
    }

    /**
     * 找回密码/注册页面 综合接口
     */
    public void lostPwdverify(Context context, String url, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, url + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
<<<<<<< HEAD
     * 找回密码/注册 综合调用 设置密码
=======
     * 部门排名
     */
    public void toRank(Context context,String url, RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.get(context, url + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     *  找回密码/注册 综合调用 设置密码
>>>>>>> a2378aaccfe5afe1495f56b0d7efff1c4fbc3b94
     */
    public void newPwdverify(Context context, String url, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, url + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 完善注册信息
     */
    public void toRegisterUpdateInfo(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.toRegisterUpdateInfo() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 修改用户基本信息
     */
    public void toUserEdit(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.toUserEdit() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 修改密码
     */
    public void toChangePassword(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.toChangePassword() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 上传用户头像
     */
    public void toUploadUserAvator(Context context,RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, EtcommApplication.toUploadUserAvator() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 意见反馈
     */
    public void toFeedBack(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.toFeedBack() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 企业邀请码效验
     */
    public void toSerialNumber(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.getSerialNumber() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 健康资讯-获取已收藏列表
     */
    public void toFavorite(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toFavorite() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 趋势页面链接接口
     */
    public void toTrendsUrl(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toTrendsUrl() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 每日签到
     */
    public void toSignIn(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toSignIn() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 每日排名
     */
    public void toShowRank(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toShowRank() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 天气预报
     */
    public void toWeather(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toWeather() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 获取前七天的记步数据
     */
    public void toPedometerWeek(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toPedometerWeek() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 设置消息推送
     */
    public void toSetPush(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toSetPush() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 退出
     */
    public void toExit(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toExit() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 我的活动
     */
    public void toMyActivity(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toMyActivity() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取默认头像
     */
    public void getDefaultAvator(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.getDefaultAvator() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);

    }

    /**
     * 检测版本更新
     */
    public void toUploadversion(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toUploadversion() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 解绑设备
     */
    public void toBindOff(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toBindOff() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 获取用户基本信息[我的模块使用]
     */
    public void toUserProfile(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toUserProfile() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 积分兑换接口
     */
    public void invokePointExch(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.POINT_EXCHANGE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 兑换接口
     */
    public void invokeExch(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.EXCHANGE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 我的积分记录页面
     */
    public void GetMyPointsDetail(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.MYPOINTS_DETAIL() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取积分规则URL链接
     */
    public void GetPointRuleUrl(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.SCORE_RULE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取我的兑换列表
     */
    public void GetMyExchange(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.MY_EXCHANGE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取发现，首页列表
     */
    public void GetFindHome(Context context, RequestParams entity, JsonResponseHandler handler) {
        String str = EtcommApplication.HOME() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken();
        asyncHttpClient.get(EtcommApplication.HOME() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取发现，活动列表
     */
    public void GetFindList(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.ACTIVITY_LIST() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 资讯阅读，活动列表
     */
    public void GetNewsList(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.NEWS_LIST() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 资讯阅读，资讯搜索
     */
    public void GetNewsSearch(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.NEWS_SEARCH() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 公司福利列表
     */
    public void GetWelfareList(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.WELFARE_LIST() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 小组搜索页面所有小组显示列表
     */
    public void GetGroupList(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.ALL_GROUP_LIST() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 小组关注
     */
    public void Attention(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.ATTENTION_GROUP() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 小组取消关注
     */
    public void UnAttention(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.UN_ATTENTION_GROUP() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 检测是否可以创建小组
     */
    public void CheckCreate(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.CHECK_CREATE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 消息推送
     */
    public void toNews(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toNews() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 删除某一条推送消息
     */
    public void toNewsDelete(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.toNewsDelete() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 绑定设备
     */
    public void toBindOn(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.toBindOn() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 创建小组
     */
    public void TopicCreate(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.TOPIC_CREATE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 按天同步数据
     */
    public void toDateSync(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.toDateSync() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 小组详情
     */
    public void GetDiscussion(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.DISCUSSION() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 小组修改
     */
    public void topicUpdate(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.TOPIC_UPDATE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 发布文字
     */
    public void createDiscussion(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.CREATE_DISCUSSION() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 上传图片
     */
    public void createDiscussionPic(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.CREATE_DISCUSSION_PIC() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 删除话题
     */
    public void discussionDelete(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.DISCUSSION_DELETE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 取消赞
     */
    public void discussionUnlike(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.DISCUSSION_UNLIKE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 赞
     */
    public void discussionLike(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.DISCUSSION_LIKE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 删除评论
     */
    public void commentDelete(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.COMMENT_DELETE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    /**
     * 获取评论列表
     */
    public void discussionComment(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.DISCUSSION_COMMENT() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取评论列表
     */
    public void commentCreate(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.COMMENT_CREATE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 获取评论列表
     */
    public void topicUser(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.TOPIC_USE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 话题删除
     */
    public void topicDelete(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.TOPIC_DELETE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 优质小组
     */
    public void goodGroup(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.GOOD_GROUP() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 举报
     */
    public void Report(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.REPORT() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 分享到小组
     */
    public void ShareToGroup(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.get(EtcommApplication.SHARE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }


    public void cancelRequest() {
        asyncHttpClient.cancelAllRequests(true);
    }

}
