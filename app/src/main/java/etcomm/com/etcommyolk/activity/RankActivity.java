package etcomm.com.etcommyolk.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.DensityUtil;

/**
 * 健步排名
 */
public class RankActivity extends BaseActivity {
    protected static final int GetUserRankUrl = 0;
    protected static final int GetStructureRankUrl = 1;
    ImageView leftimage;
    ImageView title_right_iv;
    WebView webview;

    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        initView();
    }

    private void initView() {
        EtcommApplication.addActivity(this);
        leftimage = (ImageView) findViewById(R.id.leftimage);
        title_right_iv = (ImageView) findViewById(R.id.title_right_iv);
        webview = (WebView) findViewById(R.id.webview);


        leftimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        WebSettings webSettings = webview.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                neterrorloadLocalPage(webview);
                Toast.makeText(mContext, "网络异常，请求数据失败,请检查网络，稍后重试！", Toast.LENGTH_SHORT).show();
            }
        });
        title_right_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Token", prefs.getAccessToken());
                intent.setClass(RankActivity.this, PointsColleketDetailActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * tab 指示器，默认是已关注为0
     */
    protected int curTab = 0;
    String userurl;
    String structureurl;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GetUserRankUrl:
                    if (userurl == null) {
                        Toast.makeText(mContext, "用户排名地址获取失败", Toast.LENGTH_SHORT).show();
                    } else {
                        webview.loadUrl(userurl);
                    }
                    break;
                case GetStructureRankUrl:
                    if (userurl == null) {
                        Toast.makeText(mContext, "组织机构排名地址获取失败", Toast.LENGTH_SHORT).show();
                    } else {
                        webview.loadUrl(structureurl);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    protected TranslateAnimation animation;


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

    private void geturlFromNet(final int usrstructure) {
        RequestParams params = new RequestParams();
        params.put("access_token", token);
        String httpurl;
        if (usrstructure == 0) {
            httpurl = EtcommApplication.toMyRank();
        } else {
            httpurl = EtcommApplication.toStructureRank();
        }
        cancelmDialog();
        showProgress(0, true);
        client.toRank(this, httpurl, params, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                String content = commen.content;
                if (usrstructure == 0) {
                    userurl = content;
                    mHandler.sendEmptyMessage(GetUserRankUrl);
                } else {
                    structureurl = content;
                    mHandler.sendEmptyMessage(GetStructureRankUrl);
                }

            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });

    }

    public class DcareJavaScriptInterface {
        private Context mContext;
        public DcareJavaScriptInterface(Context mContext) {
            this.mContext = mContext;
        }
        public void onpicclick() {
//			webview.
//			WebView.
            Log.i(tag, "onpicclick");
            if(webview!=null){
                webview.reload();
            }else{

            }
            Log.i(tag, "onpicclick");
        }
    }


    public void neterrorloadLocalPage(WebView webview) {

        webview.loadUrl("file:///android_asset/html/tips.html");//, html, mimeType, encoding, "");
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // TODO Auto-generated method stub
                Log.i(tag, message);
                result.confirm();
                return true;
            }
        });
        webview.addJavascriptInterface(new DcareJavaScriptInterface(mContext), "dcare");
    }

}
