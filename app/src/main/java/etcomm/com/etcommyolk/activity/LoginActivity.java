package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.ApiClient;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        prefs = GlobalSetting.getInstance(this);
        client = ApiClient.getInstance();
    }


    private void showProgress(int resId, boolean cancel) {
        mProgress = new ProgressDialog(this);
        if (resId <= 0) {
            mProgress.setMessage(R.string.loading_data, cancel);
        } else {
            mProgress.setMessage(resId, cancel);
        }
        mProgress.show();
    }

    private void cancelmDialog() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
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
                    Toast.makeText(LoginActivity.this, login.message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(BaseException exception) {
                    super.onFailure(exception);
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
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

                break;
            case R.id.login:
                toLogin();
                break;
            case R.id.login_register:

                break;
        }
    }


}
