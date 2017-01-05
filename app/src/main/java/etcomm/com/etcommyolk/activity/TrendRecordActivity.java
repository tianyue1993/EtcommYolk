package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;

public class TrendRecordActivity extends BaseActivity {
    protected static final int GetTrendUrl = 0;
    String token = "";
    @Bind(R.id.webview)
    WebView webview;
    private String userurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_record);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("记录趋势", null);
        WebSettings webSettings = webview.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(tag, "url=" + url);
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(tag, "onReceivedError errorCode:" + errorCode + " description:" + description + " failingUrl : " + failingUrl);
                Toast.makeText(mContext, "网络异常，请求数据失败,请检查网络，稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GetTrendUrl:
                    if (userurl == null) {
                        // geturlFromNet(0);//0user排名 1 部门排名
                        Toast.makeText(mContext, "地址获取失败", Toast.LENGTH_SHORT).show();
                    } else {
                        webview.loadUrl(userurl);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        Log.i(tag, "onAttachedToWindow");
        if (userurl == null) {
            geturlFromNet(0);// 0user排名 1 部门排名
        } else {
            webview.loadUrl(userurl);
        }
    }

    private void geturlFromNet(int i) {
        RequestParams params = new RequestParams();
        params.put("access_token", prefs.getAccessToken());
        client.toTrendsUrl(this, params, new CommenHandler() {

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                userurl = commen.content;
                mHandler.sendEmptyMessage(GetTrendUrl);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

}
