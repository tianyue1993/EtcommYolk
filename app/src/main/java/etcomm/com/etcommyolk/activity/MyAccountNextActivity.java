package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;

/**
 * 我的账号
 */
public class MyAccountNextActivity extends BaseActivity {
    //修改类型
    @Bind(R.id.get_show_type)
    TextView getShowType;
    //修改内容
    @Bind(R.id.get_show_content)
    TextView getShowContent;
    //确认修改
    @Bind(R.id.get_show_change)
    RelativeLayout getShowChange;
    //进入修改
    @Bind(R.id.type_show_f)
    TextView typeShowF;
    private Intent intent;
    //绑定方式
    private String boundType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_next);
        ButterKnife.bind(this);
        initView();
    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        intent = getIntent();
        setTitleTextView(intent.getStringExtra("type"), null);
        if (intent.getStringExtra("type").contains("手机")) {
            getShowType.setText("已绑定手机号");
            getShowContent.setText(prefs.getMobile());
            typeShowF.setText("更换手机号");
            boundType = "绑定手机号";
        } else {
            getShowType.setText("已绑定邮箱");
            getShowContent.setText(prefs.getEmail());
            typeShowF.setText("更换邮箱");
            boundType = "绑定邮箱";
        }
    }

    @OnClick(R.id.get_show_change)
    public void onClick() {
        intent.putExtra("type", boundType);
        intent.setClass(MyAccountNextActivity.this, MyAccountFinalActivity.class);
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //异常捕捉报错 捕捉到时不做关闭 正常时关闭本页面
        try {
            if (data.getExtras().getBoolean("boo", false) || requestCode == 5) {
                finish();
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent = null;
    }
}
