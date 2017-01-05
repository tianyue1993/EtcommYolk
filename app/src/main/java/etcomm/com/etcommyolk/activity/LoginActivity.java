package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Login;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.LoginHandler;
import etcomm.com.etcommyolk.utils.GlobalSetting;
import etcomm.com.etcommyolk.utils.InterfaceUtils;
import etcomm.com.etcommyolk.utils.StringUtils;
import etcomm.com.etcommyolk.widget.ProgressDialog;

public class LoginActivity extends Activity {
    //账号
    @Bind(R.id.login_name)
    EditText loginName;
    //密码
    @Bind(R.id.login_pwd)
    EditText loginPwd;
    //显示隐藏密码
    @Bind(R.id.login_showh_pwd)
    CheckBox loginShowhPwd;
    //忘记密码
    @Bind(R.id.forget_pwd)
    TextView forgetPwd;
    //登录
    @Bind(R.id.login)
    Button login;
    //注册
    @Bind(R.id.login_register)
    TextView loginRegister;
    //登录头像
    @Bind(R.id.login_pic)
    SimpleDraweeView loginPic;
    //对话框
    private ProgressDialog mProgress;
    //存储
    private GlobalSetting prefs;
    //网络
    private ApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        EtcommApplication.addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        prefs = GlobalSetting.getInstance(this);
        client = ApiClient.getInstance();
        if (!prefs.getLoginUserAvatar().equals("")) {
            loginPic.setImageURI(prefs.getLoginUserAvatar());
        }
        if (!prefs.getLoginUserName().equals("")) {
            loginName.setText(prefs.getLoginUserName());
        }

    }



    /**
     * 登录
     */
    private void toLogin() {
        if (!StringUtils.isEmpty(loginName.getText().toString().trim()) && !StringUtils.isEmpty(loginPwd.getText().toString().trim())) {
            RequestParams object = new RequestParams();
            object.put("username", loginName.getText().toString().trim());
            object.put("password", loginPwd.getText().toString().trim());
            object.put("client_id", InterfaceUtils.getClientId(this));
            object.put("device_id", InterfaceUtils.readDeviceId(this));
            client.invokeLogin(this, object, new LoginHandler() {

                @Override
                public void onSuccess(Login login) {
                    super.onSuccess(login);
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
                    prefs.setIsLike(login.content.is_like);
                    prefs.setIsComment(login.content.is_comment);
                    prefs.setIsComment(login.content.islevel);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(BaseException exception) {
                    super.onFailure(exception);
                }
            });
        }
    }

    /**
     * 显示隐藏密码
     */
    private void toShowHidePwd() {
        if (loginShowhPwd.isChecked()) {
            //如果选中，显示密码
            loginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //否则隐藏密码
            loginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    @OnClick({R.id.login_showh_pwd, R.id.forget_pwd, R.id.login, R.id.login_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_showh_pwd:
                toShowHidePwd();
                break;
            case R.id.forget_pwd:
                startActivity(new Intent(this, LostPwdActivity.class));
                break;
            case R.id.login:
                toLogin();
                break;
            case R.id.login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }


}
