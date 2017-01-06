package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import etcomm.com.etcommyolk.utils.StringUtils;

public class LostPwdActivity extends BaseActivity implements TextWatcher, View.OnClickListener {
    //跳转返回判断
    private static final int TO_REQUEST_CODE = 3;
    //输入手机号/邮箱
    @Bind(R.id.lost_phone)
    EditText lostPhone;
    //输入验证码
    @Bind(R.id.lost_code)
    EditText lostCode;
    //获取验证码
    @Bind(R.id.get_code)
    TextView getCode;
    //下一步
    @Bind(R.id.lost_commit)
    Button lostCommit;
    //找回方式
    @Bind(R.id.lost_type)
    TextView lostType;
    /**
     * 获取验证码方式 true 为手机验证码 false 为邮箱验证码
     */
    private boolean getCodeType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_pwd);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("找回密码", null);
        getRightTextView().setText("邮箱找回");
        getRightTextView().setVisibility(View.VISIBLE);
    }

    //添加监听
    private void initListener() {
        getRightTextView().setOnClickListener(this);
        lostPhone.addTextChangedListener(this);
        lostCode.addTextChangedListener(this);
    }

    @OnClick({R.id.get_code, R.id.lost_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_code:
                //获取验证
                toGetCode();
                break;
            case R.id.lost_commit:
                //下一步
                toNext();
                break;
            case R.id.text_base_right:
                //切换找回方式
                toSetInputType();
                break;

        }
    }
    //找回方式
    private void toSetInputType() {
        if (getRightTextView().getText().equals("邮箱找回")) {
            //切换到邮箱
            getRightTextView().setText("手机找回");
            lostType.setText("邮　箱：");
            getCodeType = false;
            if (lostPhone.getText().toString().isEmpty()) {
                lostPhone.setHint("请输入邮箱");
            } else {
                lostPhone.setText("");
            }
            if (!lostCode.getText().toString().isEmpty()) {
                lostCode.setText("");
            }
        } else {
            //切换到手机
            getRightTextView().setText("邮箱找回");
            lostType.setText("手机号：");
            getCodeType = true;
            if (lostPhone.getText().toString().isEmpty()) {
                lostPhone.setHint("请输入手机号");
            } else {
                lostPhone.setText("");
            }
            if (!lostCode.getText().toString().isEmpty()) {
                lostCode.setText("");
            }
            getRightTextView().setText("邮箱找回");
        }
    }

    //下一步
    private void toNext() {
        if (!StringUtils.isEmpty(lostCode.getText().toString().trim())) {
            toVerifyCode();
        } else {
            if (getCodeType) {
                showToast("请输入手机号！");
            } else {
                showToast("请输入邮箱！");
            }
        }
    }

    //获取验证码
    private void toGetCode() {
        if (!StringUtils.isEmpty(lostPhone.getText().toString().trim())) {
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
        object.put("receiver", lostPhone.getText().toString().trim());
        object.put("type", "forgot_password");
        //true 为手机验证码 false 为邮箱验证码
        String url;
        if (getCodeType) {
            url = EtcommApplication.getPhoneCode();
        } else {
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
        object.put("receiver", lostPhone.getText().toString().trim());
        object.put("verify_code", lostCode.getText().toString().trim());
        object.put("type", "forgot_password");
        //true 为手机验证码 false 为邮箱验证码
        String url;
        if (getCodeType) {
            url = EtcommApplication.verifyPhoneCode();
        } else {
            url = EtcommApplication.verifyMailCode();
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
                showToast(commen.message);
                Intent intent = new Intent(LostPwdActivity.this, ForgotPasswordActivity.class);
                if (getCodeType) {
                    intent.putExtra("type", "phone");
                    intent.putExtra("input", lostPhone.getText().toString().trim());
                    startActivityForResult(intent, TO_REQUEST_CODE);
                } else {
                    intent.putExtra("type", "mail");
                    intent.putExtra("input", lostPhone.getText().toString().trim());
                    startActivityForResult(intent, TO_REQUEST_CODE);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //回到登录页面
        if (requestCode == TO_REQUEST_CODE) {
            finish();
        }
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
        if (StringUtils.isEmpty(lostPhone.getText().toString().trim()) || StringUtils.isEmpty(lostCode.getText().toString().trim())) {
            lostCommit.setBackgroundResource(R.mipmap.all_fil_button);
        } else {
            lostCommit.setBackgroundResource(R.mipmap.all_ok_button);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
