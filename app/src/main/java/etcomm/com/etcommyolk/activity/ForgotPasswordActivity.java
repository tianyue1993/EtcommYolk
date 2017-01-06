package etcomm.com.etcommyolk.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.LostPwdGetCodeHandler;
import etcomm.com.etcommyolk.utils.InterfaceUtils;
import etcomm.com.etcommyolk.utils.StringUtils;

public class ForgotPasswordActivity extends BaseActivity implements TextWatcher {
    //跳转返回判断
    private static final int TO_REQUEST_CODE = 3;
    //显示隐藏Icon
    @Bind(R.id.forget_show_pwd_check)
    CheckBox forgetShowPwdCheck;
    //显示隐藏文本
    @Bind(R.id.forget_show_pwd_text)
    TextView forgetShowPwdText;
    //显示面
    @Bind(R.id.forget_show_pwd)
    LinearLayout forgetShowPwd;
    //完成
    @Bind(R.id.forget_commit)
    Button forgetCommit;
    //新密码
    @Bind(R.id.forget_new_pwd)
    EditText forgetNewPwd;
    //确认密码
    @Bind(R.id.forget_confirm_pwd)
    EditText forgetConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        initView();
    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("找回密码", null);
        initListener();
    }
    //注册监听
    private void initListener(){
        forgetNewPwd.addTextChangedListener(this);
        forgetConfirmPwd.addTextChangedListener(this);
    }

    //确认提交
    private void toCommitPwd() {
        if (forgetNewPwd.getText().toString().trim().length() < 6 || forgetConfirmPwd.getText().toString().trim().length() < 6) {
            //判断长度
            showToast(R.string.password_length_error);
            return;
        } else if (!forgetNewPwd.getText().toString().trim().trim().equalsIgnoreCase(forgetConfirmPwd.getText().toString().trim().trim())) {
            //判断前后一致
            showToast(R.string.password_equals_error);
            return;
        } else if (forgetNewPwd.getText().toString().trim().trim().length() > 12 || forgetConfirmPwd.getText().toString().trim().trim().length() > 12) {
            showToast(R.string.password_length_error);
            return;
        }
        RequestParams object = new RequestParams();
        String url;
        if (getIntent().getStringArrayExtra("type").equals("phone")) {
            //手机找回
            url = EtcommApplication.alterPhonePwd();
            object.put("mobile", getIntent().getStringArrayExtra("input"));
        } else {
            //邮箱找回
            url = EtcommApplication.alterMailPwd();
            object.put("email", getIntent().getStringArrayExtra("input"));
        }
        object.put("device_id", InterfaceUtils.readDeviceId(this));
        object.put("password", forgetNewPwd.getText().toString().trim());
        object.put("repeat_password", forgetConfirmPwd.getText().toString().trim());
        cancelmDialog();
        showProgress(0, true);
        client.newPwdverify(this, url, object, new LostPwdGetCodeHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                showToast(commen.message);
                setResult(TO_REQUEST_CODE);
                finish();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });


    }

    @OnClick({R.id.forget_show_pwd, R.id.forget_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_show_pwd:
                // true 为隐藏密码 false 为显示密码
                if (forgetShowPwdCheck.isChecked()) {
                    //如果选中，隐藏密码
                    forgetShowPwdCheck.setChecked(false);
                    forgetShowPwdText.setText("   显示密码");
                    forgetNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    forgetConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则显示密码
                    forgetShowPwdCheck.setChecked(true);
                    forgetShowPwdText.setText("   隐藏密码");
                    forgetNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    forgetConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.forget_commit:

                toCommitPwd();

                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //修改Button背景
        if (StringUtils.isEmpty(forgetNewPwd.getText().toString().trim()) || StringUtils.isEmpty(forgetConfirmPwd.getText().toString().trim())) {
            forgetCommit.setBackgroundResource(R.mipmap.all_fil_button);
        } else {
            forgetCommit.setBackgroundResource(R.mipmap.all_ok_button);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
