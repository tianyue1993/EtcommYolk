package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;

/**
 * Created by ${tianyue} on 2016/12/16.
 * 福利、活动、资讯都显示在同一个容器，根据type处理业务
 */
public class TopicWebviewlActivity extends BaseActivity {
    @Bind(R.id.iv_rocket)
    ImageView iv_rocket;
    @Bind(R.id.webview)
    WebView webview;

    TranslateAnimation animation;
    String type = "";
    String url = "";
    boolean open = false;//是否有分享功能

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        final Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            url = intent.getStringExtra("url");
            if (type != "") {
                if (type.equals("activity")) {
                    //活动
                    setTitleTextView("活动详情", null);
                    open = true;
                } else if (type.equals("health")) {
                    //健康资讯
                    setTitleTextView("资讯中心", null);
                    open = true;
                }

            }

        }
        if (open) {
            setRightImage(R.mipmap.ic_title_more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ShareAction(TopicWebviewlActivity.this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).addButton("sharetogroup", "sharetogroup", "logo", "logo").
                            setShareboardclickCallback(new ShareBoardlistener() {
                                @Override
                                public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                                    if (snsPlatform.mShowWord.equals("sharetogroup")) {
                                        // 分享
                                        intent.setClass(mContext, SharetoGroupActivity.class);
                                        startActivity(intent);
                                    } else {
                                        if (intent != null) {
                                            UMImage image = new UMImage(mContext, intent.getStringExtra("image"));
                                            new ShareAction(TopicWebviewlActivity.this).withMedia(image).
                                                    withTitle(intent.getStringExtra("topic_name")).
                                                    withText(intent.getStringExtra("discuse")).
                                                    withTargetUrl(intent.getStringExtra("url")).
                                                    setPlatform(share_media).
                                                    setCallback(umShareListener).share();
                                        }
                                    }
                                }
                            }).open();
                }
            });
        }

        animation = new TranslateAnimation(0, 0, 500, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(600);//设置动画持续时间
        animation.setRepeatCount(60);//重复次数
        animation.setRepeatMode(Animation.REVERSE);//反向执行
        animation.setStartOffset(0);//设置启动时间


        WebSettings webSettings = webview.getSettings();
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false

        webview.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞

        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true

        webview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false

        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式

        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题

        webview.getSettings().setAppCacheEnabled(true);//是否使用缓存

        webview.getSettings().setDomStorageEnabled(true);//DOM Storage

        webview.setWebViewClient(new MyWebview());
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        if (url != "") {
            webview.loadUrl(url);
        }
    }

    public class MyWebview extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            iv_rocket.setVisibility(View.GONE);
            iv_rocket.clearAnimation();
            iv_rocket.invalidate();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            iv_rocket.setVisibility(View.VISIBLE);
            iv_rocket.startAnimation(animation);

        }

    }

// TODO: 2016/12/20分享功能

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(TopicWebviewlActivity.this, platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TopicWebviewlActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(TopicWebviewlActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}