package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;

public class TopicReportPopActivity extends Activity implements View.OnClickListener {
    public String tag = "TopicReportPopActivity";
    Intent intent;
    public Context mContext;
    @Bind(R.id.btn_report)
    Button btn_report;
    @Bind(R.id.btn_cancel)
    Button btn_cancel;
    @Bind(R.id.pop_layout)
    LinearLayout popLayout;

    @Override
    public void onResume() {
        super.onResume();
        intent = getIntent();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(tag);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(tag);
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activ
    // ity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @OnClick({R.id.btn_report, R.id.btn_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_report: // 举报
                intent.setClass(mContext, TopicDisscussReportActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_report_pop);
        ButterKnife.bind(this);
        mContext = this;
    }
}
