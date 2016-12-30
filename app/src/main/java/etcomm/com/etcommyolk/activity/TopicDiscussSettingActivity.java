package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.Preferences;

public class TopicDiscussSettingActivity extends Activity {

    @Bind(R.id.btn_attention)
    Button btn_attention;
    @Bind(R.id.btn_report)
    Button btn_report;
    @Bind(R.id.btn_share)
    Button btn_share;
    @Bind(R.id.btn_cancel)
    Button btn_cancel;
    @Bind(R.id.pop_layout)
    LinearLayout popLayout;
    String topic_id;
    Context mContext;
    @Bind(R.id.btn_going)
    LinearLayout btnGoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_discuss_setting);
        ButterKnife.bind(this);
        mContext = this;
        initDatas();

    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    protected void initDatas() {
        Intent intent = getIntent();
        topic_id = intent.getStringExtra("topic_id");
        String id = intent.getStringExtra("id");
        //首否是活动小组
        if (intent.getStringExtra("is_activity").equals("1")) {
            btnGoing.setVisibility(View.VISIBLE);
        } else {
            btnGoing.setVisibility(View.GONE);
        }
        if (id.equals(GlobalSetting.getInstance(mContext).getUserId())) {
            btn_attention.setText("删除");
        } else {
            if (intent.getStringExtra("isAttentioned").equals("1")) {
                btn_attention.setText("取消关注");
            } else {
                btn_attention.setText("关注");
            }

        }


        btn_report.setText("举报");

    }

    @OnClick({R.id.btn_report, R.id.btn_share, R.id.btn_attention, R.id.btn_cancel, R.id.btn_going})
    public void onClick(View v) {
        Intent data = new Intent();
        switch (v.getId()) {
            case R.id.btn_attention: // 取关
                data.putExtra(Preferences.TOPICSET, "1");
                setResult(RESULT_OK, data);
                break;
            case R.id.btn_report: // 举报小组
                Intent intent = new Intent(mContext, TopicDisscussReportActivity.class);
                intent.putExtra("topic_id", topic_id);
                intent.putExtra("type", "topic");
                startActivity(intent);
                break;
            case R.id.btn_share:
                Intent shareintent = new Intent(mContext, SharetoGroupActivity.class);
                shareintent.putExtra("topic_id", topic_id);
                shareintent.putExtra("type", "topic");
                shareintent.putExtra("discuse", getIntent().getStringExtra("discuse"));
                shareintent.putExtra("image", getIntent().getStringExtra("image"));
                shareintent.putExtra("topic_name", getIntent().getStringExtra("topic_name"));
                startActivity(shareintent);
                break;
            case R.id.btn_cancel:
                break;
            case R.id.btn_going:
                Intent intent1 = getIntent();
                intent1.setClass(mContext, SportDetailActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
        finish();
    }
}
