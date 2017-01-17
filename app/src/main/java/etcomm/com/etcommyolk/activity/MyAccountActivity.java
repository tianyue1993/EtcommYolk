package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;

public class MyAccountActivity extends BaseActivity {
    //手机号
    @Bind(R.id.acc_phone)
    TextView accPhone;
    //绑定手机号
    @Bind(R.id.acc_phone_button)
    Button accPhoneButton;
    //邮箱
    @Bind(R.id.acc_mail)
    TextView accMail;
    //绑定邮箱
    @Bind(R.id.acc_mail_button)
    Button accMailButton;
    //修改手机号
    @Bind(R.id.change_phone)
    RelativeLayout changePhone;
    //修改邮箱
    @Bind(R.id.change_email)
    RelativeLayout changeEmail;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        EtcommApplication.addActivity(this);
        setTitleTextView("我的账号", null);

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    //重复更新数据
    private void initView() {
        if (prefs.getMobile().isEmpty()) {
            accPhoneButton.setVisibility(View.VISIBLE);
            accPhone.setVisibility(View.GONE);
            changePhone.setClickable(false);
        } else {
            accPhoneButton.setVisibility(View.GONE);
            accPhone.setVisibility(View.VISIBLE);
            accPhone.setText(prefs.getMobile());
            changePhone.setClickable(true);
        }
        if (prefs.getEmail().isEmpty()) {
            accMailButton.setVisibility(View.VISIBLE);
            accMail.setVisibility(View.GONE);
            changeEmail.setClickable(false);
        } else {
            accMailButton.setVisibility(View.GONE);
            accMail.setVisibility(View.VISIBLE);
            accMail.setText(prefs.getEmail());
            changeEmail.setClickable(true);
        }
    }




    @OnClick({R.id.acc_phone_button, R.id.change_phone, R.id.acc_mail_button, R.id.change_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acc_phone_button:
                //绑定手机
                intent.putExtra("type", "绑定手机号");
                intent.setClass(MyAccountActivity.this, MyAccountFinalActivity.class);
                startActivity(intent);
                break;
            case R.id.acc_mail_button:
                //绑定邮箱
                intent.putExtra("type", "绑定邮箱");
                intent.setClass(MyAccountActivity.this, MyAccountFinalActivity.class);
                startActivity(intent);
                break;
            case R.id.change_phone:
                //修改手机号
                intent.putExtra("type", "更换手机号");
                intent.setClass(MyAccountActivity.this, MyAccountNextActivity.class);
                startActivity(intent);
                break;
            case R.id.change_email:
                //修改邮箱
                intent.putExtra("type", "更换邮箱");
                intent.setClass(MyAccountActivity.this, MyAccountNextActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent = null;
    }

}
