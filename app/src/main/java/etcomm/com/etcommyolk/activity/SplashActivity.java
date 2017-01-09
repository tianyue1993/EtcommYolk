package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.igexin.sdk.PushManager;
import com.iwown.android_iwown_ble.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URL;
import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.service.StepDataUploadService;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.StringUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private static final String tag = "SplashActivity";
    // 引导图片资源
    private static final int[] pics = {R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3};
    private ArrayList<View> views = new ArrayList<View>();
    private SharedPreferences sp;
    private ImageView flyman;
    private SimpleDraweeView flymanbackground;
    private AlphaAnimation animation;

    // 在开启一个服务之前应该判断该服务知否已经在运行

    private void initData() {
        // TODO Auto-generated method stub
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            // 防止图片不能填满屏幕
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            // 加载图片资源
            iv.setImageResource(pics[i]);
            if (i == pics.length - 1) {
                iv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            sp.edit().putBoolean("isShowedGuide", true).commit();
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
            }
            views.add(iv);
        }
    }

    private void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PushManager.getInstance().initialize(this.getApplicationContext());
        // 注册广播
        startService(new Intent(this, StepDataUploadService.class));
        sp = getPreferences(MODE_PRIVATE);
        boolean isShowedGuide = sp.getBoolean("isShowedGuide", false);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        RelativeLayout loading = (RelativeLayout) findViewById(R.id.loading);
        flyman = (ImageView) findViewById(R.id.flyman);
        flymanbackground = (SimpleDraweeView) findViewById(R.id.flymanbackground);
        //判断是否第一次打开 进入ViewPager
        if (isShowedGuide) {
            loading.setVisibility(View.VISIBLE);
            viewpager.setVisibility(View.GONE);
            // 替换企业
            if (prefs.getCustomerImage() != null && prefs.getCustomerImage() != "") {
                flymanbackground.setImageURI("");
            } else {
                flymanbackground.setBackgroundResource(R.mipmap.loading_chaoren);
            }

            animation = new AlphaAnimation(1.0f, 1.0f);
            animation.setDuration(1500);
            flymanbackground.setAnimation(animation);
            animation.startNow();
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!StringUtils.isEmpty(prefs.getAccessToken())) {
                        if (prefs.getInfoState()) {
                            // 信息完整
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, ChoosePictureActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    SplashActivity.this.finish();
                }
            });
        } else {
            //第一次打开
            viewpager.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            initData();
            viewpager.setAdapter(new PagerAdapter() {
                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return (arg0 == arg1);
                }

                @Override
                public int getCount() {
                    if (views != null) {
                        return views.size();
                    } else
                        return 0;
                }

                /**
                 * 初始化position位置的界面
                 */
                @Override
                public Object instantiateItem(View container, int position) {
                    ((ViewPager) container).addView(views.get(position), 0);
                    return views.get(position);
                }

                /**
                 * 销毁position位置的界面
                 */
                @Override
                public void destroyItem(View container, int position, Object object) {
                    ((ViewPager) container).removeView(views.get(position));
                }
            });
        }
        Log.i(tag, "Uid: " + android.os.Process.myUid() + " Tid: " + android.os.Process.myTid() + " Pid: " + android.os.Process.myPid());
    }


}
