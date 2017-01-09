package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import etcomm.com.etcommyolk.entity.Login;
import etcomm.com.etcommyolk.entity.StructureContent;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.ActivationCodeHandler;
import etcomm.com.etcommyolk.handler.EfficacyCodeHandler;
import etcomm.com.etcommyolk.handler.RegisterHandler;
import etcomm.com.etcommyolk.utils.InterfaceUtils;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.zxing.activity.CaptureActivity;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    //邀请码
    @Bind(R.id.reg_invitation_code)
    EditText regInvitationCode;
    //二维码
    @Bind(R.id.qr_code)
    ImageView qrCode;
    //邮箱/手机号
    @Bind(R.id.registeredPhone)
    TextInputLayout registeredPhone;
    //获取验证码
    @Bind(R.id.reg_get_code)
    TextView regGetCode;
    //输入验证码
    @Bind(R.id.verificationCode)
    TextInputLayout verificationCode;
    //输入密码
    @Bind(R.id.registerPassword)
    TextInputLayout registerPassword;
    //注册
    @Bind(R.id.reg_commit)
    Button regCommit;
    //注册输入方式
    @Bind(R.id.reg_input_type)
    TextView regInputType;
    @Bind(R.id.login_showh_pwd)
    CheckBox loginShowhPwd;
    /**
     * 获取验证码方式 true 为手机验证码 false 为邮箱验证码
     */
    private boolean getCodeType = true;
    /**
     * 组织结构ID (部门ID)
     */
    private String structure_id;

    /**
     * 效验邀请码是否正确
     */
    private Boolean structure_code = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    //杂项
    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("注册", null);
        getRightTextView().setVisibility(View.VISIBLE);
        getRightTextView().setText("邮箱注册");
    }

    //监听注册
    private void initListener() {
        regInvitationCode.addTextChangedListener(this);
        verificationCode.getEditText().addTextChangedListener(this);
        registerPassword.getEditText().addTextChangedListener(this);
        registeredPhone.getEditText().addTextChangedListener(this);
        getRightTextView().setOnClickListener(this);
    }


    //注册方式
    private void toSetInputType() {
        if (getRightTextView().getText().equals("邮箱注册")) {
            //切换到邮箱
            getRightTextView().setText("手机注册");
            regInputType.setText("邮箱号：");
            getCodeType = false;
            if (!registeredPhone.getEditText().getText().toString().isEmpty()) {
                registeredPhone.getEditText().setText("");
            }
            if (!verificationCode.getEditText().getText().toString().isEmpty()) {
                verificationCode.getEditText().setText("");
            }
        } else {
            //切换到手机
            getRightTextView().setText("邮箱注册");
            regInputType.setText("手机号：");
            getCodeType = true;
            if (!registeredPhone.getEditText().getText().toString().isEmpty()) {
                registeredPhone.getEditText().setText("");
            }
            if (!verificationCode.getEditText().getText().toString().isEmpty()) {
                verificationCode.getEditText().setText("");
            }
        }
    }

    //注册
    private void toCommitReg() {
        //综合输入
        if (regInputType.getText().toString().contains("邮箱")) {
            if (!registeredPhone.getEditText().getText().toString().contains("@") || StringUtils.isEmpty(registeredPhone.getEditText().getText().toString())) {
                registeredPhone.setError("请输入有效的邮箱号");
                registeredPhone.setErrorEnabled(true);
                return;
            } else {
                registeredPhone.setError(null);
                registeredPhone.setErrorEnabled(false);
            }
        }
        if (regInputType.getText().toString().contains("手机")) {
            if (registeredPhone.getEditText().getText().toString().trim().length() != 11 || StringUtils.isEmpty(registeredPhone.getEditText().getText().toString())) {
                registeredPhone.setError("请输入11位有效的手机号");
                registeredPhone.setErrorEnabled(true);
                return;
            } else {
                registeredPhone.setError(null);
                registeredPhone.setErrorEnabled(false);
            }
        }
        //验证码
        if (verificationCode.getEditText().getText().toString().trim().length() != 4) {
            verificationCode.setError("请输入4位有效验证码");
            verificationCode.setErrorEnabled(true);
            return;
        } else {
            verificationCode.setError(null);
            verificationCode.setErrorEnabled(false);
        }
        if (StringUtils.isEmpty(regInvitationCode.getText().toString().trim())) {
            showToast("请输入邀请码");
            return;
        }

        if (structure_code){
            RequestParams object = new RequestParams();
            object.put("receiver", registeredPhone.getEditText().getText().toString().trim());
            object.put("verify_code", verificationCode.getEditText().getText().toString().trim());
            //true 为手机验证码 false 为邮箱验证码
            String url;
            if (getCodeType) {
                object.put("type", "mobile_sign_up");
                url = EtcommApplication.verifyPhoneCode();
            } else {
                object.put("type", "email_sign_up");
                url = EtcommApplication.verifyMailCode();
            }
            cancelmDialog();
            showProgress(0, true);
            client.lostPwdverify(this, url, object, new EfficacyCodeHandler() {

                @Override
                public void onCancel() {
                    super.onCancel();
                    cancelmDialog();
                }

                @Override
                public void onSuccess(Commen commen) {
                    super.onSuccess(commen);
                    cancelmDialog();
                    if (commen.code != 0) {
                        showToast(commen.message);
                    } else {
                        toPerfectRegister();
                    }
                }

                @Override
                public void onFailure(BaseException exception) {
                    super.onFailure(exception);
                    cancelmDialog();
                }
            });
        }else {
            showToast("请输入邀请码");
        }
    }


    /**
     * 显示隐藏密码
     */
    private void toShowHidePwd() {
        if (loginShowhPwd.isChecked()) {
            //如果选中，显示密码
            registerPassword.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //否则隐藏密码
            registerPassword.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    //调用注册监听
    private void toPerfectRegister() {
        RequestParams object = new RequestParams();
        String url;
        //true 为手机 false 为邮箱
        if (getCodeType) {
            object.put("mobile", registeredPhone.getEditText().getText().toString().trim());
            url = EtcommApplication.toPhoneRegister();
        } else {
            object.put("email", registeredPhone.getEditText().getText().toString().trim());
            url = EtcommApplication.toMailRegister();
        }
        object.put("password", registerPassword.getEditText().getText().toString().trim());
        object.put("repeat_password", registerPassword.getEditText().getText().toString().trim());
        object.put("structure_id", structure_id);
        object.put("sn", regInvitationCode.getText().toString().trim());
        object.put("device_id", InterfaceUtils.readDeviceId(this));
        object.put("client_id", InterfaceUtils.getClientId(this));
        cancelmDialog();
        showProgress(0, true);
        client.lostPwdverify(this, url, object, new RegisterHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Login login) {
                super.onSuccess(login);
                cancelmDialog();
                prefs.saveLoginUserName(registeredPhone.getEditText().getText().toString().trim());
                prefs.setUserId(login.content.user_id);
                prefs.setDepartmentId(login.content.department_id);
                prefs.setCustomerId(login.content.customer_id);
                prefs.setSerialNumberId(login.content.serial_number_id);
                prefs.setNickName(login.content.nick_name);
                prefs.setRealName(login.content.real_name);
                prefs.setAccessToken(login.content.access_token);
                prefs.setBirthday(login.content.birthday);
                prefs.setBirthYear(login.content.birth_year);
                prefs.setHeight(login.content.height);
                prefs.setGender(login.content.gender);
                prefs.setWeight(login.content.weight);
                prefs.setAvatar(login.content.avatar);
                prefs.setMobile(login.content.mobile);
                prefs.setJobNumber(login.content.job_number);
                prefs.setScore(login.content.score);
                prefs.setTotalScore(login.content.total_score);
                prefs.setTotalscore(login.content.total_score);
                prefs.setPedometerTarget(login.content.pedometer_target);
                prefs.setPedometerDistance(login.content.pedometer_distance);
                prefs.setPedometerTime(login.content.pedometer_time);
                prefs.setPedometerConsume(login.content.pedometer_consume);
                prefs.setIsLeader(login.content.is_leader);
                prefs.setCreatedAt(login.content.created_at);
                prefs.setIsSign(login.content.is_sign);
                prefs.setCustomerImage(login.content.customer_image);
                prefs.setInfoStatus(login.content.info_status);
                //用户信息完整性
                if (login.content.info_status.equals("1")) {
                    prefs.saveInfoState(true);
                } else {
                    prefs.saveInfoState(false);
                }
                prefs.setIsLike(login.content.is_like);
                prefs.setIsComment(login.content.is_comment);
                prefs.setIsComment(login.content.islevel);
                startActivity(new Intent(RegisterActivity.this, ChoosePictureActivity.class));
                finish();
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }

    //获取验证码
    private void toGetCode() {
        //本地综合验证
        if (regInputType.getText().toString().contains("邮箱")) {
            if (!registeredPhone.getEditText().getText().toString().contains("@")) {
                registeredPhone.setError("请输入有效的邮箱号");
                registeredPhone.setErrorEnabled(true);
                return;
            } else {
                registeredPhone.setError(null);
                registeredPhone.setErrorEnabled(false);
            }
        }
        if (regInputType.getText().toString().contains("手机")) {
            if (registeredPhone.getEditText().getText().toString().trim().length() != 11) {
                registeredPhone.setError("请输入11位有效的手机号");
                registeredPhone.setErrorEnabled(true);
                return;
            } else {
                registeredPhone.setError(null);
                registeredPhone.setErrorEnabled(false);
            }
        }
        RequestParams object = new RequestParams();
        object.put("receiver", registeredPhone.getEditText().getText().toString().trim());
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
        client.lostPwdverify(this, url, object, new EfficacyCodeHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(Commen commen) {
                super.onSuccess(commen);
                cancelmDialog();
                if (commen.code != 0) {
                    showToast(commen.message);
                } else {
                    timer.start();
                }
            }

            @Override
            public void onFailure(BaseException exception) {
                super.onFailure(exception);
                cancelmDialog();
            }
        });
    }


    /**
     * 企业邀请码效验
     */
    private void getSubStructure() {
        RequestParams object = new RequestParams();
        object.put("serial_number", regInvitationCode.getText().toString().trim());
        object.put("parent_id", 0);
        cancelmDialog();
        client.toSerialNumber(this, object, new ActivationCodeHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
                cancelmDialog();
            }

            @Override
            public void onSuccess(StructureContent commen) {
                super.onSuccess(commen);
                cancelmDialog();
                if (commen.code == 0) {
                    //获得部门ID
                    structure_id = commen.content.structure.get(0).getStructure_id();
                    structure_code = true;
                } else {
                    showToast(commen.message);
                    structure_code = false;
                }
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
            regGetCode.setText(millisUntilFinished / 1000 + "秒后重发");
            regGetCode.setClickable(false);
            regGetCode.setTextColor(Color.parseColor("#A8A8A8"));
        }

        @Override
        public void onFinish() {
            regGetCode.setClickable(true);
            regGetCode.setText(R.string.reget_check_code);
            regGetCode.setTextColor(Color.parseColor("#F7B23f"));
        }
    };


    @OnClick({R.id.qr_code, R.id.reg_get_code, R.id.reg_commit, R.id.login_showh_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qr_code:
                //二维码
                startActivityForResult(new Intent(RegisterActivity.this, CaptureActivity.class), 5);
                break;
            case R.id.reg_get_code:
                //获取验证码
                toGetCode();
                break;
            case R.id.reg_commit:
                //注册
                toCommitReg();
                break;
            case R.id.text_base_right:
                //注册方式
                toSetInputType();
                break;
            case R.id.login_showh_pwd:
                //注册方式
                toShowHidePwd();
                break;



        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //扫描后邀请码赋值
        if (requestCode == 5) {
            regInvitationCode.setText(data.getStringExtra("code"));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //二维码图片变色
        if (!StringUtils.isEmpty(regInvitationCode.getText().toString().trim())) {
            qrCode.setBackgroundResource(R.mipmap.reg_send_sel);
        } else {
            qrCode.setBackgroundResource(R.mipmap.reg_send_mor);
        }
        //Button背景颜色
        if (StringUtils.isEmpty(regInvitationCode.getText().toString().trim()) || StringUtils.isEmpty(verificationCode.getEditText().getText().toString().trim())
                || StringUtils.isEmpty(registerPassword.getEditText().getText().toString().trim()) || StringUtils.isEmpty(registeredPhone.getEditText().getText().toString().trim())) {
            regCommit.setBackgroundResource(R.mipmap.all_fil_button);
        } else {
            regCommit.setBackgroundResource(R.mipmap.all_ok_button);
        }
        //监听满6位进行说明邀请码输入完毕进行网络请求
        if (regInvitationCode.getText().toString().trim().length() >= 6) {
            getSubStructure();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
