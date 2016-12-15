package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;

public class PointsColleketDetailActivity extends BaseActivity {
    String token = "";
    @Bind(R.id.webview)
    WebView webview;
    protected static final int GetUrl = 0;
    private String userurl;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GetUrl:
                    if (userurl == null) {
                        Toast.makeText(mContext, "用户排名地址获取失败", Toast.LENGTH_SHORT).show();
                    } else {
                        webview.loadUrl(userurl);
                    }
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_colleket_detail);
        ButterKnife.bind(this);
        setTitleTextView("积分规则", null);
        token = getIntent().getStringExtra("Token");
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(tag, "onAttachedToWindow");
        if (userurl == null) {
            geturlFromNet();//0user排名  1 部门排名
        } else {
            webview.loadUrl(userurl);
        }
    }

    /**
     * 获取积分规则的URL地址
     */
    private void geturlFromNet() {
        RequestParams params = new RequestParams();
        params.put("access_token", token);
        Log.d(tag, "getList: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        cancelmDialog();
        showProgress(0, true);
        client.GetPointRuleUrl(mContext, params, new CommenHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen login) {
                super.onSuccess(login);
                cancelmDialog();
                userurl = login.content;
                mHandler.sendEmptyMessage(GetUrl);
            }


            @Override
            public void onFailure(BaseException exception) {
                cancelmDialog();
                super.onFailure(exception);
                Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
