package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import etcomm.com.etcommyolk.R;

public class BaseActivity extends Activity {
    //标题
    private Button title;
    //左边按钮
    private Button leftTextView;
    //右边按钮
    private Button rightTextView;
    //外部布局
    private RelativeLayout allRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

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
    public void setRightTextView(String string, View.OnClickListener onClickListener){
        if (rightTextView != null){
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
    public void setLeftTextView(String string, View.OnClickListener onClickListener){
        if (leftTextView != null){
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
    public void setTitleTextView(String string, View.OnClickListener onClickListener){
        if (title != null){
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
