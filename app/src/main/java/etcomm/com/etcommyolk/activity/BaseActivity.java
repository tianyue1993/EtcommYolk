package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.utils.GlobalSetting;

public class BaseActivity extends Activity {

    //标题
    private Button title;
    //左边按钮
    private Button leftTextView;
    //右边按钮
    private Button rightTextView;
    //外部布局
    private RelativeLayout allRelativeLayout;
    String tag = getClass().getSimpleName();
    Context mContext;
    protected GlobalSetting prefs;
    protected ApiClient client;

    public static final int MSG_CLOSE_PROGRESS = 1;
    public static final int MSG_SHOW_TOAST = 2;
    public Handler baseHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_PROGRESS:
                    // cancelProgress(); //加载进度条
                    break;
                case MSG_SHOW_TOAST:
                    int resid = msg.arg1;
                    if (resid > 0) {
                        showToast(resid);
                    } else if (msg.obj != null) {
                        String mes = (String) msg.obj;
                        if (!TextUtils.isEmpty(mes)) {
                            showToast(mes);
                        }
                    }
                    break;
            }
        }

        ;
    };
    Toast toast = null;

    protected void showToast(String message) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(mContext, (!TextUtils.isEmpty(message)) ? message
                : this.getString(R.string.network_error), Toast.LENGTH_SHORT);
        toast.show();
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }
        }, 2000);
    }

    protected void showToast(int resid) {
        if (toast != null) {
            return;
        }
        toast = Toast.makeText(mContext, resid, Toast.LENGTH_SHORT);
        toast.show();
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        prefs = etcomm.com.etcommyolk.utils.GlobalSetting.getInstance(mContext);
        client = ApiClient.getInstance();
        title = (Button) findViewById(R.id.base_title);
        leftTextView = (Button) findViewById(R.id.base_left);
        rightTextView = (Button) findViewById(R.id.base_right);
        allRelativeLayout = (RelativeLayout) findViewById(R.id.all_base_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


    /**
     * 设置子布局
     */
    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.base_layout);
        if (null != allRelativeLayout)
            allRelativeLayout.addView(view, lp);
    }

    /**
     * 设置右边按钮
     */
    public void setRightTextView(String string, View.OnClickListener onClickListener) {
        if (rightTextView != null) {
            rightTextView.setVisibility(View.VISIBLE);
            if (string != null) {
                rightTextView.setText(string);
            }
            if (onClickListener != null) {
                rightTextView.setOnClickListener(onClickListener);
            }
        }
    }

    /**
     * 设置左边按钮
     */
    public void setLeftTextView(String string, View.OnClickListener onClickListener) {
        if (leftTextView != null) {
            leftTextView.setVisibility(View.VISIBLE);
            if (string != null) {
                leftTextView.setText(string);
            }
            if (onClickListener != null) {
                leftTextView.setOnClickListener(onClickListener);
            }
        }
    }

    /**
     * 设置标题
     */
    public void setTitleTextView(String string, View.OnClickListener onClickListener) {
        if (title != null) {
            title.setVisibility(View.VISIBLE);
            if (string != null) {
                title.setText(string);
            }
            if (onClickListener != null) {
                title.setOnClickListener(onClickListener);
            }
        }
    }

}
