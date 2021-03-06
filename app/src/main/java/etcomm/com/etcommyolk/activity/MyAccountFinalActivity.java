package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.handler.LostPwdGetCodeHandler;
import etcomm.com.etcommyolk.utils.StringUtils;

public class MyAccountFinalActivity extends BaseActivity implements TextWatcher {
    @Bind(R.id.final_type)
    TextView finalType;
    @Bind(R.id.final_phone)
    EditText finalPhone;
    @Bind(R.id.final_code)
    EditText finalCode;
    @Bind(R.id.get_code)
    TextView getCode;
    @Bind(R.id.final_commit)
    Button finalCommit;
    @Bind(R.id.bound_explain)
    TextView boundExplain;
    @Bind(R.id.clear_input)
    ImageView clearInput;
    /**
     * 获取验证码方式 true 为手机验证码 false 为邮箱验证码
     */
    private boolean getCodeType = true;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_final);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    //杂项
    private void initView() {
        intent = getIntent();
        EtcommApplication.addActivity(this);
        setTitleTextView(intent.getStringExtra("type"), null);
        toSetInputType();
        finalCommit.setClickable(false);
    }

    //添加监听
    private void initListener() {
        finalPhone.addTextChangedListener(this);
        finalCode.addTextChangedListener(this);
    }


    //找回方式
    private void toSetInputType() {
        if (intent.getStringExtra("type").contains("邮箱")) {
            //切换到邮箱
            finalType.setText("邮　箱");
            boundExplain.setText("绑定邮箱后，下次登录可使用邮箱登录");
            getCodeType = false;
            if (finalPhone.getText().toString().isEmpty()) {
                finalPhone.setHint("请输入邮箱");
            } else {
                finalPhone.setText("");
            }
            if (!finalCode.getText().toString().isEmpty()) {
                finalCode.setText("");
            }
        } else {
            //切换到手机
            finalType.setText("手机号");
            getCodeType = true;
            boundExplain.setText("绑定手机号后，下次登录可使用手机号登录");
            if (finalPhone.getText().toString().isEmpty()) {
                finalPhone.setHint("请输入手机号");
            } else {
                finalPhone.setText("");
            }
            if (!finalCode.getText().toString().isEmpty()) {
                finalCode.setText("");
            }
        }
    }

    //下一步
    private void toNext() {
        if (!StringUtils.isEmpty(finalCode.getText().toString().trim())) {
            toVerifyCode();
        } else {
            showToast("请输入验证码！");
        }
    }

    //获取验证码
    private void toGetCode() {
        if (!StringUtils.isEmpty(finalPhone.getText().toString().trim())) {
            toNSURLRequest();
        } else {
            if (getCodeType) {
                showToast("请输入手机号！");
            } else {
                showToast("请输入邮箱！");
            }
        }
    }

    //获取短信 邮箱验证码
    private void toNSURLRequest() {
        RequestParams object = new RequestParams();
        object.put("receiver", finalPhone.getText().toString().trim());
        //true 为手机验证码 false 为邮箱验证码
        String url;
        if (getCodeType) {
            object.put("type", "mobile_sign_up");
            url = EtcommApplication.getPhoneCode();
        } else {
            object.put("type", "email_sign_up");
            url = EtcommApplication.getMailCode();
        }
        cancelmDialog();
        showProgress(0, true);
        client.lostPwdverify(this, url, object, new LostPwdGetCodeHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                timer.start();
                showToast(commen.message);
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    //验证短信 邮箱验证码
    private void toVerifyCode() {
        RequestParams object = new RequestParams();
        object.put("receiver", finalPhone.getText().toString().trim());
        object.put("verify_code", finalCode.getText().toString().trim());
        object.put("type", "forgot_password");
        //true 为手机验证码 false 为邮箱验证码
        String url;
        if (getCodeType) {
            url = EtcommApplication.verifyPhoneCode();
        } else {
            url = EtcommApplication.verifyMailCode();
        }

        client.lostPwdverify(this, url, object, new LostPwdGetCodeHandler() {
            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                if (getCodeType) {
                    editUserInfo("mobile", finalPhone.getText().toString().trim());
                } else {
                    editUserInfo("email", finalPhone.getText().toString().trim());
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
            }
        });
    }

    private void editUserInfo(String field, final String value) {
        RequestParams params = new RequestParams();
        params.put("field", field);
        params.put("value", value);
        params.put("access_token", prefs.getAccessToken());
        Log.i(tag, "params: " + params.toString());
        cancelmDialog();
        showProgress(0, true);
        client.toUserEdit(this, params, new CommenHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                if (getCodeType) {
                    prefs.setMobile(finalPhone.getText().toString().trim());
                    showToast("更换手机号成功");
                } else {
                    prefs.setEmail(finalPhone.getText().toString().trim());
                    showToast("更换邮箱成功");
                }
                prefs.saveLoginUserName(finalPhone.getText().toString().trim());
                intent.putExtra("boo", false);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }


    // 注册 刷新验证码 倒计时
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            getCode.setText(millisUntilFinished / 1000 + "秒后重发");
            getCode.setClickable(false);
            getCode.setTextColor(Color.parseColor("#A8A8A8"));
        }

        @Override
        public void onFinish() {
            getCode.setClickable(true);
            getCode.setText(R.string.reget_check_code);
            getCode.setTextColor(Color.parseColor("#F7B23f"));
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!StringUtils.isEmpty(finalPhone.getText().toString().trim())) {
            clearInput.setVisibility(View.VISIBLE);
        } else {
            clearInput.setVisibility(View.GONE);
        }

        if (StringUtils.isEmpty(finalPhone.getText().toString().trim()) || StringUtils.isEmpty(finalCode.getText().toString().trim())) {
            finalCommit.setBackgroundResource(R.mipmap.all_fil_button);
            finalCommit.setClickable(false);
        } else {
            finalCommit.setBackgroundResource(R.mipmap.all_ok_button);
            finalCommit.setClickable(true);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @OnClick({R.id.get_code, R.id.final_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_code:
                //获取验证
                toGetCode();
                break;
            case R.id.final_commit:
                //下一步
                toNext();
                break;
        }
    }

    @OnClick(R.id.clear_input)
    public void onClick() {
        finalPhone.setText("");
    }
}
