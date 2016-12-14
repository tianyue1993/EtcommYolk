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
        asyncHttpClient.setTimeout(20000);
        asyncHttpClient.setMaxRetriesAndTimeout(0, 5000);
        asyncHttpClient.addHeader("Accept", "application/json; version=public");
        syncHttpClient = new SyncHttpClient();
        syncHttpClient.setURLEncodingEnabled(false);
        syncHttpClient.setTimeout(20000);
        syncHttpClient.setMaxRetriesAndTimeout(0, 5000);
    }

    public void login(String url, TextHttpResponseHandler handler) {
        asyncHttpClient.get(url, handler);
    }

    /**
     * 登录接口
     */
    public void invokeLogin(Context context, RequestParams entity, JsonResponseHandler handler) {
        asyncHttpClient.post(context, EtcommApplication.LOGIN(),
                entity, handler);

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

    public void cancelRequest() {
        asyncHttpClient.cancelAllRequests(true);
    }

}
