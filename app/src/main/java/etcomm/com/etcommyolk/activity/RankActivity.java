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
    View leftindicator;
    View rightindicator;
    TextView around_tab_attationed;
    TextView around_tab_notattationed;
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
        leftindicator = findViewById(R.id.leftindicator);
        rightindicator = findViewById(R.id.rightindicator);
        around_tab_attationed = (TextView) findViewById(R.id.around_tab_attationed);
        around_tab_notattationed = (TextView) findViewById(R.id.around_tab_notattationed);
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
        around_tab_attationed.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                around_tab_notattationed.setBackground(null);
                around_tab_notattationed.setTextColor(Color.parseColor("#aba8a8"));
                around_tab_attationed.setTextColor(Color.parseColor("#ffffff"));
                if (curTab == 0) {

                } else {
                    if (animation != null) {
                        animation.cancel();
                        animation = null;
                    }
                    animation = new TranslateAnimation(0, -DensityUtil.dip2px(mContext, 80), 0, 0);
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            leftindicator.setVisibility(View.VISIBLE);
                            rightindicator.setVisibility(View.INVISIBLE);
                            around_tab_attationed.setBackground(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            around_tab_attationed.setBackgroundResource(R.mipmap.arout_tab_choosed_bg);
                            leftindicator.setVisibility(View.INVISIBLE);
                            rightindicator.setVisibility(View.INVISIBLE);
                        }
                    });
                    rightindicator.startAnimation(animation);
                }
                curTab = 0;
                if (userurl == null) {
                    geturlFromNet(0);
                } else {
                    mHandler.sendEmptyMessage(GetUserRankUrl);
                }
            }
        });
        around_tab_notattationed.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                around_tab_attationed.setBackground(null);
                around_tab_notattationed.setTextColor(Color.parseColor("#ffffff"));
                around_tab_attationed.setTextColor(Color.parseColor("#aba8a8"));// /aba8a8
                // //656363
                if (curTab == 0) {
                    if (animation != null) {
                        animation.cancel();
                        animation = null;
                    }
                    animation = new TranslateAnimation(0, DensityUtil.dip2px(mContext, 80), 0, 0);
                    ;
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            rightindicator.setVisibility(View.VISIBLE);
                            leftindicator.setVisibility(View.INVISIBLE);
                            around_tab_notattationed.setBackground(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            around_tab_notattationed.setBackgroundResource(R.mipmap.arout_tab_choosed_bg);
                            leftindicator.setVisibility(View.INVISIBLE);
                            rightindicator.setVisibility(View.INVISIBLE);
                        }
                    });
                    leftindicator.startAnimation(animation);
                } else {

                }
                curTab = 1;
                if (structureurl == null) {
                    geturlFromNet(1);
                } else {
                    mHandler.sendEmptyMessage(GetStructureRankUrl);
                }
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
