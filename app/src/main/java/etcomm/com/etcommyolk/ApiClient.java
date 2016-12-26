package etcomm.com.etcommyolk;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import etcomm.com.etcommyolk.handler.JsonResponseHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class ApiClient {
    public static final String BASE_URL = "http://113.59.227.10:86/index.php?r="; // 测试
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);


    private AsyncHttpClient asyncHttpClient;
    private SyncHttpClient syncHttpClient;
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
        syncHttpClient = new SyncHttpClient();
        syncHttpClient.setURLEncodingEnabled(false);
        syncHttpClient.setTimeout(10000);
        syncHttpClient.setMaxRetriesAndTimeout(0, 5000);
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
    public void lostPwdverify(Context context,String url, RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, url + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken() , entity, handler);
    }

    /**
     *  找回密码/注册 综合调用 设置密码
     */
    public void newPwdverify(Context context,String url, RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, url + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 完善注册信息
     */
    public void toRegisterUpdateInfo(Context context,RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, EtcommApplication.toRegisterUpdateInfo() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 修改用户基本信息
     */
    public void toUserEdit(Context context,RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, EtcommApplication.toUserEdit() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 修改密码
     */
    public void toChangePassword(Context context,RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, EtcommApplication.toChangePassword() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }

    /**
     * 上传用户头像
     */
    public void toUploadUserAvator(Context context,RequestParams entity, JsonResponseHandler handler){
        asyncHttpClient.post(context, EtcommApplication.toUserEdit() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
    }
    /**
     * 意见反馈
     */
    public void toFeedBack(Context context,RequestParams entity, JsonResponseHandler handler){
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
     * 创建小组
     */
    public void TopicCreate(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(EtcommApplication.TOPIC_CREATE() + "?access_token=" + GlobalSetting.getInstance(context).getAccessToken(), entity, handler);
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

    public void cancelRequest() {
        asyncHttpClient.cancelAllRequests(true);
    }

}
