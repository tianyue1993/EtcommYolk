package etcomm.com.etcommyolk.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import etcomm.com.etcommyolk.R;

/**
 * 体检报告
 *
 * @author iexpressbox
 */
public class ExamineReportActivity extends BaseActivity {

    @Bind(R.id.iv_rocket)
    ImageView iv_rocket;
    @Bind(R.id.webview)
    WebView webview;

    TranslateAnimation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examin_report);
        ButterKnife.bind(this);
        setTitleTextView("体检报告", null);
        WebSettings webSettings = webview.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webview.loadUrl("http://m.ikang.com/#/index/ikang");
        animation = new TranslateAnimation(0, 0, 500, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(600);//设置动画持续时间
        animation.setRepeatCount(60);//重复次数
        animation.setRepeatMode(Animation.REVERSE);//反向执行
        animation.setStartOffset(0);//设置启动时间
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);//DOM Storage
        webview.setWebViewClient(new MyWebview());


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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(tag);
    }


}
