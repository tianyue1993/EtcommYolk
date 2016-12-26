package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("我的账号", null);



    }
}
