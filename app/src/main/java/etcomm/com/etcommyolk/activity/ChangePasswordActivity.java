package etcomm.com.etcommyolk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.Commen;
import etcomm.com.etcommyolk.exception.BaseException;
import etcomm.com.etcommyolk.handler.CommenHandler;
import etcomm.com.etcommyolk.utils.InterfaceUtils;

/**
 * zuoh
 */
public class ChangePasswordActivity extends BaseActivity {
    //旧密码
    @Bind(R.id.old_pwd)
    EditText oldPwd;
    //新密码
    @Bind(R.id.new_pwd)
    EditText newPwd;
    //再次确认
    @Bind(R.id.renew_pwd)
    EditText renewPwd;
    //眼睛
    @Bind(R.id.forget_show_pwd_check)
    CheckBox forgetShowPwdCheck;
    //修改
    @Bind(R.id.btn_next)
    Button btnNext;
    //数据库获取的UserID
    String user_id;
    //个推CID
    private String client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        EtcommApplication.addActivity(this);
        setTitleTextView("修改密码", null);
        client_id = PushManager.getInstance().getClientid(getApplicationContext());

        forgetShowPwdCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // true 为隐藏密码 false 为显示密码
                if (isChecked) {
                    //如果选中，隐藏密码
                    oldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    renewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则显示密码
                    oldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    renewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }



    @OnClick({R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:

                //修改密码
                String oldPass = oldPwd.getText().toString();
                String newPass = newPwd.getText().toString();
                String renewPass = renewPwd.getText().toString();
                if (oldPass.length() < 6 || newPass.length() < 6 || newPass.length() < 6) {
                    showToast(R.string.password_length_error);
                } else if (!newPass.trim().equalsIgnoreCase(renewPass.trim())) {
                    showToast("请检查密码是否一致");
                } else if (oldPass.trim().length() > 12 || newPass.trim().length() > 12 || renewPass.trim().length() > 12) {
                    showToast(R.string.password_length_error);
                } else {
                    RequestParams params = new RequestParams();
                    params.put("access_token", prefs.getAccessToken());
                    if (TextUtils.isEmpty(user_id)) {
                        user_id = prefs.getUserId();
                    }
                    params.put("user_id", user_id);
                    params.put("device_id", InterfaceUtils.readDeviceId(this));
                    params.put("old_password", oldPwd.getText().toString());
                    params.put("password", newPwd.getText().toString());
                    params.put("repeat_password", renewPwd.getText().toString());
                    params.put("client_id", client_id);
                    cancelmDialog();
                    showProgress(0, true);
                    client.toChangePassword(this, params, new CommenHandler() {

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
                            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(BaseException exception) {
                            super.onFailure(exception);
                            cancelmDialog();
                        }
                    });

                }

                break;
        }
    }
}
